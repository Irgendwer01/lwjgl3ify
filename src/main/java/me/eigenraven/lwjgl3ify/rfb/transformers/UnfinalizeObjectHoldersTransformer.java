package me.eigenraven.lwjgl3ify.rfb.transformers;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.Manifest;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassHeaderMetadata;
import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;

import me.eigenraven.lwjgl3ify.WasFinalObjectHolder;
import me.eigenraven.lwjgl3ify.core.Lwjgl3ifyCoremod;

public class UnfinalizeObjectHoldersTransformer implements RfbClassTransformer {

    private static final Set<String> HARDCODED_MC_CLASSES = new HashSet<>();
    final byte[] QUICKSCAN_BYTES = "net/minecraftforge/fml/common/registry/GameRegistry"
        .getBytes(StandardCharsets.UTF_8);

    static {
        // forge hardcodes these
        HARDCODED_MC_CLASSES.addAll(
            Arrays.asList(
                new String[] { "net.minecraft.init.Blocks", "net.minecraft.init.Items", "net.minecraft.init.MobEffects",
                    "net.minecraft.init.Biomes", "net.minecraft.init.Enchantments", "net.minecraft.init.SoundEvents",
                    "net.minecraft.init.PotionTypes" }));
    }

    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "unfinalize-object-holders";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader,
        @NotNull RfbClassTransformer.Context context, @Nullable Manifest manifest, @NotNull String className,
        @NotNull ClassNodeHandle classNode) {
        if (!classNode.isPresent()) {
            return false;
        }
        if (HARDCODED_MC_CLASSES.contains(className)) {
            return true;
        }
        if (classNode.isOriginal()) {
            return ClassHeaderMetadata.hasSubstring(classNode.getOriginalBytes(), QUICKSCAN_BYTES);
        } else {
            return true;
        }
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull RfbClassTransformer.Context context,
        @Nullable Manifest manifest, @NotNull String name, @NotNull ClassNodeHandle classNodeHandle) {
        final ClassNode node = classNodeHandle.getNode();
        if (node == null) {
            return;
        }
        boolean transformClass = false;
        boolean workDone = false;
        if (HARDCODED_MC_CLASSES.contains(name)) {
            transformClass = true;
        }
        transformClass |= isHolder(node.visibleAnnotations);
        int fieldsModified = 0;
        for (FieldNode field : node.fields) {
            boolean transform = transformClass;
            if (!transform) {
                transform = isHolder(field.visibleAnnotations);
            }
            if (transform) {
                workDone = true;
                if ((field.access & Opcodes.ACC_FINAL) != 0) {
                    if (field.visibleAnnotations == null) {
                        field.visibleAnnotations = new ArrayList<>(1);
                        field.visibleAnnotations
                            .add(new AnnotationNode(Type.getDescriptor(WasFinalObjectHolder.class)));
                    }
                    field.access = field.access & (~Opcodes.ACC_FINAL);
                }
                fieldsModified++;
            }
        }
        if (workDone) {
            Lwjgl3ifyCoremod.LOGGER.debug("Unfinalized {} Holder fields in {}", fieldsModified, name);
        }
    }

    private static boolean isHolder(List<AnnotationNode> annotations) {
        if (annotations == null) {
            return false;
        }
        for (AnnotationNode annotationNode : annotations) {
            if (annotationNode.desc.contains("net/minecraftforge/fml/common/registry/GameRegistry$ObjectHolder")) {
                return true;
            }
            if (annotationNode.desc.contains("net/minecraftforge/fml/common/registry/GameRegistry$ItemStackHolder")) {
                return true;
            }
            if (annotationNode.desc.contains("net/minecraftforge/fml/common/registry/GameRegistry/ObjectHolder")) {
                return true;
            }
            if (annotationNode.desc.contains("net/minecraftforge/fml/common/registry/GameRegistry/ItemStackHolder")) {
                return true;
            }
        }
        return false;
    }
}
