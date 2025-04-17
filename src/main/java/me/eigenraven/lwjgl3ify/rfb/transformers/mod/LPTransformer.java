package me.eigenraven.lwjgl3ify.rfb.transformers.mod;

import java.util.jar.Manifest;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

public class LPTransformer implements RfbClassTransformer {

    @Override
    public @NotNull String id() {
        return "lp-transformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        return className.equals("logisticspipes.asm.td.ClassRenderDuctItemsHandler");
    }

    // LP crashes the game specifically if a method from TD does not have a specific checksum,
    // since this is different, we set the boolean to false so it won't do that
    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        ClassNode node = classNode.getNode();
        if (node != null) {
            for (MethodNode methodNode : node.methods) {
                if (methodNode.name.equals("handleRenderDuctItemsClass")) {
                    for (AbstractInsnNode abstractInsnNode : methodNode.instructions) {
                        if (abstractInsnNode instanceof InsnNode insnNode && insnNode.getOpcode() == Opcodes.ICONST_1) {
                            methodNode.instructions.set(insnNode, new InsnNode(Opcodes.ICONST_0));
                            break;
                        }
                    }
                }
            }
        }
    }
}
