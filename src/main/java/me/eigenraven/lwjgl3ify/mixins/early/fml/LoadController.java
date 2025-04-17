package me.eigenraven.lwjgl3ify.mixins.early.fml;

import net.minecraftforge.fml.common.Loader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = net.minecraftforge.fml.common.LoadController.class, remap = false, priority = Integer.MAX_VALUE)
public class LoadController {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void noCleanroom(Loader loader, CallbackInfo ci) {
        try {
            Class.forName("youyihj.zenutils.Reference")
                .getDeclaredField("IS_CLEANROOM")
                .set(null, false); // See ZenUtilsTransformer
        } catch (Exception e) {}
    }
}
