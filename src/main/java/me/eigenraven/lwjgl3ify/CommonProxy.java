package me.eigenraven.lwjgl3ify;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import zone.rong.mixinbooter.ILateMixinLoader;

public class CommonProxy implements ILateMixinLoader {

    public void registerF3Handler() {}

    @SuppressWarnings("unchecked")
    public void runCompatHooks() {
        // Fix crash with OpenComputers/COFHCore due to a class extending OpenALException
        try {
            Class<?> cofhATTransformer = Class.forName("cofh.asm.CoFHAccessTransformer");
            Field superClassesField = cofhATTransformer.getDeclaredField("superClasses");
            superClassesField.setAccessible(true);
            HashMap<String, String> superClasses = (HashMap<String, String>) superClassesField.get(null);
            superClasses.put("org/lwjgl/openal/OpenALException", "java/lang/RuntimeException");
            superClasses.put("org/lwjglx/openal/OpenALException", "java/lang/RuntimeException");
            Lwjgl3ify.LOG.info("Installed CoFH AT compatibility");
        } catch (ReflectiveOperationException e) {
            // ignore - cofh not present
        }
    }

    @Override
    public List<String> getMixinConfigs() {
        ArrayList<String> mixinConfigs = new ArrayList<>();
        mixinConfigs.add("mixins.lwjgl3ify.late.cmm.json");
        mixinConfigs.add("mixins.lwjgl3ify.late.sfr.json");
        mixinConfigs.add("mixins.lwjgl3ify.late.plustic.json");
        mixinConfigs.add("mixins.lwjgl3ify.late.lp.json");
        mixinConfigs.add("mixins.lwjgl3ify.late.athenaeum.json");
        return mixinConfigs;
    }
}
