package com.destroyerr.minesb;

import com.destroyerr.minesb.esp.*;
import com.destroyerr.minesb.module.*;
import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class MineSBClient implements ClientModInitializer {

    public static KeyBinding keyMenu, keyXray, keySpeedMine, keyFullBright, keyHud;
    public static boolean showHud = true;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        keyMenu       = bind("menu",       GLFW.GLFW_KEY_RIGHT_SHIFT);
        keyXray       = bind("xray",       GLFW.GLFW_KEY_X);
        keySpeedMine  = bind("speedmine",  GLFW.GLFW_KEY_V);
        keyFullBright = bind("fullbright", GLFW.GLFW_KEY_G);
        keyHud        = bind("hud",        GLFW.GLFW_KEY_H);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (keyMenu.wasPressed())       mc.setScreen(new MineSBMenu());
            while (keyXray.wasPressed())       XRay.toggle(mc);
            while (keySpeedMine.wasPressed())  SpeedMine.toggle();
            while (keyFullBright.wasPressed()) FullBright.toggle(mc);
            while (keyHud.wasPressed())        showHud = !showHud;
            ModuleManager.tick(mc);
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(Renderer::renderAll);
        HudRenderCallback.EVENT.register((ctx, tick) -> {
            if (showHud) ModuleManager.renderHud(ctx);
        });
    }

    private static KeyBinding bind(String id, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.minesb." + id, InputUtil.Type.KEYSYM, key, "category.minesb"));
    }

    public static void msg(MinecraftClient mc, String s, Formatting f) {
        if (mc.player != null) mc.player.sendMessage(Text.literal(s).formatted(f), true);
    }
}
