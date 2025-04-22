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

public class ChiselAndBitsTransformer implements RfbClassTransformer {

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "chiselandbits-transformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        return className.equals("mod.chiselsandbits.config.ModConfig");
    }

    // Chisels & Bits never calls setAccessible when trying to modify its fields, thus erroring when trying to set a
    // private field, so we make that specific field public.
    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        ClassNode node = classNode.getNode();
        if (node != null) {
            for (FieldNode field : node.fields) {
                if (field.name.equals("showUsage")) {
                    field.access &= ~Opcodes.ACC_PRIVATE;
                    field.access |= Opcodes.ACC_PUBLIC;
                    break;
                }
            }
        }
    }
}
