package me.eigenraven.lwjgl3ify.mixins.late.athenaeum;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.codetaylor.mc.athenaeum.util.Injector;

import me.eigenraven.lwjgl3ify.UnsafeHacks;

@Mixin(value = Injector.class, remap = false)
public class MixinInjector {

    /**
     * @author Irgendwer01
     * @reason Old one seems like it didn't work, so we directly call Unsafe
     */
    @Overwrite
    public void inject(Class<?> apiClass, String fieldName, Object value) {
        try {
            UnsafeHacks.setField(
                Class.forName(apiClass.getName())
                    .getDeclaredField(fieldName),
                null,
                value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
