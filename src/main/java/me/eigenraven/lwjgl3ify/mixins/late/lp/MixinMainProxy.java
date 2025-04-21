package me.eigenraven.lwjgl3ify.mixins.late.lp;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import logisticspipes.proxy.MainProxy;

@Mixin(value = MainProxy.class, remap = false)
public class MixinMainProxy {

    /**
     * @author Irgendwer01
     * @reason Use Forge internal methods for checking sides
     */
    @Overwrite
    private static Side getEffectiveSide(Thread thr) {
        return FMLCommonHandler.instance()
            .getEffectiveSide();
    }
}
