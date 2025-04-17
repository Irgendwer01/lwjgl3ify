package me.eigenraven.lwjgl3ify.mixins.late.plustic;

import java.util.concurrent.Callable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Loader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import landmaster.plustic.api.Toggle;

@Mixin(value = Toggle.class, remap = false)
public class MixinToggle {

    @Redirect(
        method = "<clinit>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraftforge/common/capabilities/CapabilityManager;register(Ljava/lang/Class;Lnet/minecraftforge/common/capabilities/Capability$IStorage;Ljava/util/concurrent/Callable;)V"))
    private static void register(CapabilityManager instance, Class tClass, Capability.IStorage type, Callable storage) {
        if (!Loader.isModLoaded("conarm")) return;
        instance.register(tClass, type, storage);
    }
}
