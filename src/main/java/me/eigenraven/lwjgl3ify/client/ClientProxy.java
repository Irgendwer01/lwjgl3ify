package me.eigenraven.lwjgl3ify.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjglx.input.Keyboard;
import org.lwjglx.opengl.Display;

import me.eigenraven.lwjgl3ify.CommonProxy;
import me.eigenraven.lwjgl3ify.api.InputEvents;
import me.eigenraven.lwjgl3ify.core.Config;

public class ClientProxy extends CommonProxy {

    static final String javaVersion;
    static final String lwjglVersion = "LWJGL: " + org.lwjgl.Version.getVersion();

    static {
        String javaVersionRaw = "Java: " + System.getProperty("java.version");
        if (javaVersionRaw.length() > 32) {
            javaVersionRaw = javaVersionRaw.substring(0, 29) + "...";
        }
        javaVersion = javaVersionRaw;
    }

    @Override
    public void runCompatHooks() {
        super.runCompatHooks();
        if (Config.DEBUG_REGISTER_OPENGL_LOGGER) {
            GLDebugLog.setupDebugMessageCallback();
        }
        // Populate keyboard-layout-dependent key lookup tables
        Keyboard.populateKeyLookupTables();
        registerKeybindHandler();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static final class McKeybindHandler implements InputEvents.KeyboardListener {

        @Override
        public void onKeyEvent(InputEvents.KeyEvent event) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) {
                return;
            }
            if (mc.currentScreen != null) {
                return;
            }
            if (event.lwjgl2KeyCode > Keyboard.KEY_NONE) {
                KeyBinding.setKeyBindState(event.lwjgl2KeyCode, event.action != InputEvents.KeyAction.RELEASED);
                if (event.action != InputEvents.KeyAction.RELEASED) {
                    KeyBinding.onTick(event.lwjgl2KeyCode);
                }
            }
        }
    }

    private void registerKeybindHandler() {
        InputEvents.addKeyboardListener(new McKeybindHandler());
    }

    @Override
    public void registerF3Handler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    @SuppressWarnings("unused") // event handler
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (Minecraft.getMinecraft().gameSettings.showDebugInfo
            && event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (Config.SHOW_LWJGL_VERSION) {
                event.getRight()
                    .add(
                        Math.min(
                            3,
                            event.getRight()
                                .size()),
                        lwjglVersion);
            }
            if (Config.SHOW_JAVA_VERSION) {
                event.getRight()
                    .add(
                        Math.min(
                            3,
                            event.getRight()
                                .size()),
                        javaVersion);
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused") // event handler
    public void onConfigChange(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (!event.getModID()
            .equals("lwjgl3ify")) {
            return;
        }
        Config.config.save();
        Config.reloadConfigObject();
        Display.lwjgl3ify$updateRawMouseMode(Config.INPUT_RAW_MOUSE);
    }
}
