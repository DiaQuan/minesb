package com.destroyerr.minesb.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public final class FullBright {

    public static boolean enabled = false;
    private static double oldGamma = 0.5;

    private FullBright() {}

    public static void toggle(MinecraftClient mc) {
        enabled = !enabled;
        if (mc.options == null) return;
        if (enabled) {
            oldGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(100.0);
        } else {
            mc.options.getGamma().setValue(oldGamma);
        }
        if (mc.player != null)
            mc.player.sendMessage(Text.literal("FullBright: " + (enabled ? "§aAÇIK" : "§cKAPALI")), true);
    }
}
