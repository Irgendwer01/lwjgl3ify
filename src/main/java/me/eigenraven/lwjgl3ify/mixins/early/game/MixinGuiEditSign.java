package me.eigenraven.lwjgl3ify.mixins.early.game;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.eigenraven.lwjgl3ify.api.InputEvents;
import me.eigenraven.lwjgl3ify.client.TextFieldHandler;

@Mixin(GuiEditSign.class)
public class MixinGuiEditSign implements InputEvents.KeyboardListener {

    @Shadow
    private TileEntitySign tileSign;

    @Shadow
    private int editLine;

    @Inject(method = "initGui", at = @At("HEAD"))
    private void lwjgl3ify$onOpen(CallbackInfo ci) {
        TextFieldHandler.beginTextInput();
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void lwjgl3ify$onClose(CallbackInfo ci) {
        TextFieldHandler.endTextInput(null);
    }

    @Redirect(
        method = "keyTyped",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ChatAllowedCharacters;isAllowedCharacter(C)Z"))
    private boolean lwjgl3ify$denyStandardTextInput(char character) {
        // Inject queued IME text input
        return false;
    }

    @Override
    public void onTextEvent(InputEvents.TextEvent event) {
        // if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.tileSign.signText[this.editLine].length() <
        // 15)
        // {
        // this.tileSign.signText[this.editLine] = this.tileSign.signText[this.editLine] + typedChar;
        // }
        final String toAppend = event.text;
        if (!event.text.chars()
            .allMatch(chr -> ChatAllowedCharacters.isAllowedCharacter((char) chr))) {
            return;
        }
        final String[] signText = tileSign.signText;
        final String existing = signText[editLine];
        final int charsToAdd = Math.min(15 - existing.length(), toAppend.length());
        if (charsToAdd > 0) {
            signText[editLine] = existing + toAppend.substring(0, charsToAdd);
        }
    }
}
