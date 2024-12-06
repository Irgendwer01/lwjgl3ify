package me.eigenraven.lwjgl3ify.rfb.transformers;

import static org.objectweb.asm.Opcodes.*;

import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

public class ForgePatchTransformer implements RfbClassTransformer {

    final Logger logger = LogManager.getLogger("Lwjgl3ifyForgePatches");

    @Override
    public @NotNull String id() {
        return "forge-patch";
    }

    public static final String CLASS_PATCH_MANAGER = "net.minecraftforge.fml.common.patcher.ClassPatchManager";
    public static final String TRACING_PRINT_STREAM = "net.minecraftforge.fml.common.TracingPrintStream";
    public static final String FML_SECURITY_MANAGER = "net.minecraftforge.fml.relauncher.FMLSecurityManager";

    public static final String[] PATCHED_CLASSES = new String[] { CLASS_PATCH_MANAGER, TRACING_PRINT_STREAM,
        FML_SECURITY_MANAGER };

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader,
        @NotNull RfbClassTransformer.Context context, @Nullable Manifest manifest, @NotNull String className,
        @NotNull ClassNodeHandle classNode) {
        for (final String toPatch : PATCHED_CLASSES) {
            if (toPatch.equals(className)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull RfbClassTransformer.Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        if (!classNode.isPresent()) {
            return;
        }
        switch (className) {
            case CLASS_PATCH_MANAGER -> tfClassPatchManager(classNode);
            case TRACING_PRINT_STREAM -> tfTracingPrintStream(classNode);
            case FML_SECURITY_MANAGER -> tfFmlSecurityManager(classNode);
        }
    }

    private void tfTracingPrintStream(@NotNull ClassNodeHandle handle) {
        // Add a close() override that does not close the underlying stream
        // Pack200 tries to close this stream when loading patches.
        final ClassNode node = handle.getNode();
        if (node == null || node.methods == null) {
            logger.error("Tracing print stream missing class data");
            return;
        }
        if (node.methods.stream()
            .anyMatch(m -> "close".equals(m.name) && "()V".equals(m.desc))) {
            logger.warn("{} already has a close method", TRACING_PRINT_STREAM);
            // Someone already added a close method
            return;
        }
        final MethodVisitor mv = node.visitMethod(ACC_PUBLIC, "close", "()V", null, null);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 1);
        mv.visitEnd();
    }

    private static Integer tryIconst(AbstractInsnNode i) {
        return switch (i.getOpcode()) {
            case ICONST_0 -> 0;
            case ICONST_1 -> 1;
            case ICONST_2 -> 2;
            case ICONST_3 -> 3;
            case ICONST_4 -> 4;
            case ICONST_5 -> 5;
            case ICONST_M1 -> -1;
            case LDC -> {
                final Object cst = ((LdcInsnNode) i).cst;
                if (cst instanceof Integer) {
                    yield (Integer) cst;
                } else {
                    yield null;
                }
            }
            default -> null;
        };
    }

    private static AbstractInsnNode makeIconst(int i) {
        return switch (i) {
            case 0 -> new InsnNode(ICONST_0);
            case 1 -> new InsnNode(ICONST_1);
            case 2 -> new InsnNode(ICONST_2);
            case 3 -> new InsnNode(ICONST_3);
            case 4 -> new InsnNode(ICONST_4);
            case 5 -> new InsnNode(ICONST_5);
            case -1 -> new InsnNode(ICONST_M1);
            default -> new LdcInsnNode(i);
        };
    }

    private void tfFmlSecurityManager(@NotNull ClassNodeHandle handle) {
        // Fix an ArrayIndexOutOfBounds exception due to off-by-1 array length checks
        final ClassNode node = handle.getNode();
        if (node == null || node.methods == null) {
            logger.error("FML security manager missing class data");
            return;
        }
        final MethodNode checkPermission = node.methods.stream()
            .filter(m -> "checkPermission".equals(m.name) && "(Ljava/security/Permission;)V".equals(m.desc))
            .findFirst()
            .orElse(null);
        if (checkPermission == null || checkPermission.instructions == null
            || checkPermission.instructions.size() < 5) {
            logger.error("FML security manager missing the checkPermission method");
            return;
        }
        final InsnList chkInsns = checkPermission.instructions;
        // Search for a iconst_A; if_icmple; _; iconst_A+1 pattern and fix it
        final int insns = chkInsns.size();
        int matches = 0;
        for (int i = 0; i < insns - 4; i++) {
            final AbstractInsnNode i_iconstA = chkInsns.get(i);
            final AbstractInsnNode i_icmple = chkInsns.get(i + 1);
            final AbstractInsnNode i_iconstAp1 = chkInsns.get(i + 3);
            if (i_icmple.getOpcode() != IF_ICMPLE) {
                continue;
            }
            final Integer A = tryIconst(i_iconstA);
            final Integer Ap1 = tryIconst(i_iconstAp1);
            if (A == null || Ap1 == null || Ap1 != A + 1) {
                continue;
            }
            matches++;
            chkInsns.set(i_iconstA, makeIconst(A + 1));
        }

        if (matches < 2) {
            logger.warn("Only found {}<2 instances of AIOOB fixes in FMLSecurityManager", matches);
        }
    }

    private void tfClassPatchManager(@NotNull ClassNodeHandle handle) {
        // Fix an infinite loop if an EOFException happens
        final ClassNode node = handle.getNode();
        if (node == null || node.methods == null) {
            logger.error("Class patch manager missing class data");
            return;
        }
        for (final MethodNode mn : node.methods) {
            if (!"setup".equals(mn.name)) {
                continue;
            }
            if (mn.instructions == null || mn.instructions.size() == 0) {
                logger.error("ClassPatchManager#setup(Side) missing code");
                return;
            }
            for (final AbstractInsnNode insn : mn.instructions) {
                if (insn.getOpcode() != INVOKEVIRTUAL) {
                    continue;
                }
                if (!(insn instanceof MethodInsnNode minsn)) {
                    continue;
                }
                if (!"java/util/jar/JarInputStream".equals(minsn.owner)) {
                    continue;
                }
                if (!"getNextJarEntry".equals(minsn.name)) {
                    continue;
                }
                // redirect
                minsn.setOpcode(INVOKESTATIC);
                minsn.owner = "me/eigenraven/lwjgl3ify/redirects/JarInputStream";
                minsn.name = "getNextJarEntrySafe";
                minsn.desc = "(Ljava/util/jar/JarInputStream;)Ljava/util/jar/JarEntry;";
            }
        }
    }
}
