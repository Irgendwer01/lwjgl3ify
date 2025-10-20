package me.eigenraven.lwjgl3ify.rfb.transformers.mod;

import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

public class LadyLibTransformer implements RfbClassTransformer {

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "ladylib-transformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        return className.equals("ladylib.registration.internal.AutoRegistrar");
    }

    // Ladylib checks if fields are final that use their annotation, but we unfinalize all ObjectHolder, so we just return true
    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        boolean visitedIsFinal = false;
        ClassNode node = classNode.getNode();
        if (node != null) {
            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.equals("scanClassForFields")) {
                    for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                        if (visitedIsFinal && abstractInsnNode instanceof InsnNode insnNode
                            && insnNode.getOpcode() == Opcodes.ICONST_0) {
                            methodNode.instructions.set(insnNode, new InsnNode(Opcodes.ICONST_1));
                        }
                        if (abstractInsnNode instanceof MethodInsnNode methodInsnNode
                            && methodInsnNode.name.equals("isFinal")) {
                            visitedIsFinal = true;
                        }
                    }
                }
            }
        }
    }
}
