package me.eigenraven.lwjgl3ify.mixins.late.lp;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import logisticspipes.proxy.cc.CCProxy;

// LP tries to access a Field in Thread which does not exist anymore, thus we return null in getDeclaredField and do
// nothing when calling setAccessible as well for no errors in log, see MainProxy$getEffectiveSide for where it was
// used.
@Mixin(value = CCProxy.class, remap = false)
public class MixinCCProxy {

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/Class;getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;"))
    private Field getDeclaredField(Class instance, String name) throws NoSuchFieldException {
        return null;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Field;setAccessible(Z)V"))
    private void setAccessible(Field instance, boolean flag) throws NoSuchFieldException {}

}
