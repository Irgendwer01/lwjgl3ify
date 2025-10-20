package me.eigenraven.lwjgl3ify.mixins.late.gravisuite;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.eigenraven.lwjgl3ify.UnsafeHacks;

// If the Keyboard class isn't loaded, the Key Enum class can't be found by Reflection
@Mixin(targets = "com.chocohead.gravisuite.GraviKeys$GraviKey", remap = false)
public class MixinGraviKeys {

    @Redirect(
        method = "addKey",
        at = @At(value = "INVOKE", target = "setValue(Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Object;)V"))
    private void setValue(Object object, Field field, Object value) {
        try {
            Class.forName("ic2.core.util.Keyboard");
            Class.forName("ic2.core.util.Keyboard$Key");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        UnsafeHacks.setField(field, object, value);
    }
}
