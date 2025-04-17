package me.eigenraven.lwjgl3ify.mixins.late.sfr;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import tk.zeitheron.solarflux.api.SolarScriptEngine;

@Mixin(value = SolarScriptEngine.class, remap = false)
public class MixinSolarScriptEngine {

    @ModifyVariable(method = "newEngine", at = @At("STORE"))
    private static ScriptEngine inject(ScriptEngine se) {
        return new ScriptEngineManager().getEngineByName("nashorn"); //
    }
}
