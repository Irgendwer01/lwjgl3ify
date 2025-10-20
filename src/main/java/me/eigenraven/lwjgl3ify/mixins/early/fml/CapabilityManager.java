package me.eigenraven.lwjgl3ify.mixins.early.fml;

import java.util.IdentityHashMap;

import net.minecraftforge.common.capabilities.Capability;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.eigenraven.lwjgl3ify.api.ICapabilityManagerAccessor;

@Mixin(value = net.minecraftforge.common.capabilities.CapabilityManager.class, remap = false)
public class CapabilityManager implements ICapabilityManagerAccessor {

    @Shadow
    private IdentityHashMap<String, Capability<?>> providers;

    public IdentityHashMap getProviders() {
        return this.providers;
    }
}
