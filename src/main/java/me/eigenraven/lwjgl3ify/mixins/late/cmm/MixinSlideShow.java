package me.eigenraven.lwjgl3ify.mixins.late.cmm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import lumien.custommainmenu.configuration.elements.Slideshow;
import lumien.custommainmenu.lib.textures.ITexture;

@Mixin(value = Slideshow.class, remap = false)
public class MixinSlideShow {

    @Shadow
    public ITexture[] ressources;

    /**
     * @author Irgendwe01
     * @reason Fix CustomMainMenu crashing, casting list.toArray() to smth else will cause a crash in newer Java
     *         versions
     */
    @Overwrite
    public void shuffle() {
        List<ITexture> list = Arrays.asList(ressources);
        Collections.shuffle(list);
        ressources = list.toArray(new ITexture[0]);
    }
}
