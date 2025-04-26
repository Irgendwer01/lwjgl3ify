package me.eigenraven.lwjgl3ify.rfb.transformers.mod;

import java.util.Iterator;
import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvel2.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

public class AllASMTransformer implements RfbClassTransformer {

    private final String LOLI_TRANSFORMER = "zone.rong.loliasm.core.LoliTransformer";
    private final String NORMAL_TRANSFORMER = "mirror.normalasm.core.NormalTransformer";

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "allasm-transformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        return className.equals(LOLI_TRANSFORMER) || className.equals(NORMAL_TRANSFORMER);
    }

    // The PackageStringCanonicalization option doesn't work under newer Java, so we just disable it regardless of the
    // config
    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        ClassNode node = classNode.getNode();
        InsnList insnList = new InsnList();
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/util/Collections", "emptyMap", "()Ljava/util/Map"));
        if (node != null) {
            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.equals("<init>")) {
                    Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode insnNode = iterator.next();
                        if (insnNode instanceof FieldInsnNode fieldInsnNode && fieldInsnNode.name.equals("instance")) {
                            if (iterator.next() instanceof FieldInsnNode fieldInsnNode1
                                && fieldInsnNode1.name.equals("packageStringCanonicalization")) {
                                methodNode.instructions.remove(fieldInsnNode);
                                methodNode.instructions.set(fieldInsnNode1, new InsnNode(Opcodes.ICONST_0));
                            }
                        }
                    }
                }
            }
        }
    }
}
