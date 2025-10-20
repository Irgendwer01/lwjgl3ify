package me.eigenraven.lwjgl3ify.mixins.early.fml;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eigenraven.lwjgl3ify.api.ICapabilityManagerAccessor;

// TODO: Find out what causes this
// Fixes Capabilities not working on Server-side
@Mixin(value = net.minecraftforge.common.ForgeModContainer.class, remap = false)
public class ForgeModContainer {

    @Inject(method = "preInit", at = @At("TAIL"))
    private void preInit$tail(CallbackInfo ci) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == null) {
            CapabilityItemHandler.ITEM_HANDLER_CAPABILITY = getCapability(IItemHandler.class);
        }
        if (CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY == null) {
            CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY = getCapability(IFluidHandler.class);
        }
        if (CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY == null) {
            CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY = getCapability(IFluidHandlerItem.class);
        }
        if (CapabilityAnimation.ANIMATION_CAPABILITY == null) {
            CapabilityAnimation.ANIMATION_CAPABILITY = getCapability(IAnimationStateMachine.class);
        }
        if (CapabilityEnergy.ENERGY == null) {
            CapabilityEnergy.ENERGY = getCapability(IEnergyStorage.class);
        }
    }

    private <T> Capability<T> getCapability(Class<T> type) {
        return (Capability<T>) ((ICapabilityManagerAccessor) (Object) CapabilityManager.INSTANCE).getProviders()
            .get(
                type.getName()
                    .intern());
    }
}
