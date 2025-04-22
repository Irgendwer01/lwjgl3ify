package me.eigenraven.lwjgl3ify.rfb.transformers.mod;

import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

public class ZenUtilsTransformer implements RfbClassTransformer {

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "zenutils-transformer";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        return className.equals("youyihj.zenutils.Reference");
    }

    // ZenUtils checks in a variable if it's cleanroom by checking the java version, causing a crash, thus we remove
    // final and set the variable later to false
    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context,
        @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        ClassNode node = classNode.getNode();
        if (node != null) {
            for (FieldNode field : node.fields) {
                if (field.name.equals("IS_CLEANROOM")) {
                    field.access &= ~Opcodes.ACC_FINAL; // Remove Final
                    break;
                }
            }
        }
    }
}
