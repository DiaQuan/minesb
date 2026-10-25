package com.destroyerr.minesb.module;

import com.destroyerr.minesb.esp.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public final class ModuleManager {

    public static final List<Module> MODULES = List.of(
            new BlockESP(),
            new TunnelESP(),
            new PlayerESP(),
            new StorageESP(),
            new MobESP(),
            new ItemESP(),
            new ProjectileESP()
    );

    private ModuleManager() {}

    public static void init() {
        MODULES.forEach(Module::init);
    }

    public static void tick(MinecraftClient mc) {
        for (Module m : MODULES) if (m.isEnabled()) m.tick(mc);
        SpeedMine.tick(mc);
    }

    public static void renderHud(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        int y = 6, x = 6;
        ctx.drawTextWithShadow(mc.textRenderer,
                Text.literal("§b§l[ destroyerr1558 MineSB Ultra ]"), x, y, 0xFFFFFF);
        y += 12;
        for (Module m : MODULES) {
            if (m.isEnabled()) {
                ctx.drawTextWithShadow(mc.textRenderer,
                        Text.literal("§a● §f" + m.getName() + " §7(" + m.count() + ")"),
                        x, y, 0xFFFFFF);
                y += 10;
            }
        }
        if (SpeedMine.enabled) {
            ctx.drawTextWithShadow(mc.textRenderer,
                    Text.literal("§a● §fSpeedMine §7x" + SpeedMine.multiplier), x, y, 0xFFFFFF);
        }
    }
}
