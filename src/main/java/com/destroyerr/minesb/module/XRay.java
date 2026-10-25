package com.destroyerr.minesb.module;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

public final class XRay {

    public static boolean enabled = false;
    public static final Set<Block> VISIBLE = new HashSet<>();

    static {
        VISIBLE.add(Blocks.DIAMOND_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        VISIBLE.add(Blocks.IRON_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_IRON_ORE);
        VISIBLE.add(Blocks.GOLD_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_GOLD_ORE);
        VISIBLE.add(Blocks.EMERALD_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_EMERALD_ORE);
        VISIBLE.add(Blocks.ANCIENT_DEBRIS);
        VISIBLE.add(Blocks.NETHER_GOLD_ORE);
        VISIBLE.add(Blocks.NETHER_QUARTZ_ORE);
        VISIBLE.add(Blocks.LAPIS_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_LAPIS_ORE);
        VISIBLE.add(Blocks.REDSTONE_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        VISIBLE.add(Blocks.COAL_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_COAL_ORE);
        VISIBLE.add(Blocks.COPPER_ORE);
        VISIBLE.add(Blocks.DEEPSLATE_COPPER_ORE);
        VISIBLE.add(Blocks.ENDER_CHEST);
        VISIBLE.add(Blocks.CHEST);
        VISIBLE.add(Blocks.TRAPPED_CHEST);
        VISIBLE.add(Blocks.BARREL);
        VISIBLE.add(Blocks.SPAWNER);
        VISIBLE.add(Blocks.LAVA);
        VISIBLE.add(Blocks.WATER);
    }

    private XRay() {}

    public static void toggle(MinecraftClient mc) {
        enabled = !enabled;
        if (mc.worldRenderer != null) mc.worldRenderer.reload();
        if (mc.player != null)
            mc.player.sendMessage(Text.literal("XRay: " + (enabled ? "§aAÇIK" : "§cKAPALI")), true);
    }
}
