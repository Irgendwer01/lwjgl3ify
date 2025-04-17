package me.eigenraven.lwjgl3ify.mixins.late.sfr;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import me.eigenraven.lwjgl3ify.UnsafeHacks;
import tk.zeitheron.solarflux.SolarFlux;

@Mixin(SolarFlux.FinalFieldHelper.class)
public class MixinSolarFlux {

    /**
     * @author Irgendwer01
     * @reason Fix SFR crash on world load
     */
    @Overwrite(remap = false)
    public static boolean setFinalField(Field field, Object instance, Object thing)
        throws ReflectiveOperationException {
        UnsafeHacks.setField(field, instance, thing);
        return true;
    }
}
