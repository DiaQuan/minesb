package com.destroyerr.minesb.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class SpeedMine {

    public static boolean enabled = false;
    public static float multiplier = 5.0f;

    private SpeedMine() {}

    public static void toggle() {
        enabled = !enabled;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null)
            mc.player.sendMessage(
                    Text.literal("SpeedMine: " + (enabled ? "§aAÇIK" : "§cKAPALI") + " §7x" + multiplier), true);
    }

    public static void tick(MinecraftClient mc) {
        if (!enabled || mc.player == null || mc.world == null || mc.interactionManager == null) return;
        if (mc.crosshairTarget instanceof BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos();
            Direction dir = hit.getSide();
            mc.interactionManager.updateBlockBreakingProgress(pos, dir);
            mc.player.networkHandler.sendPacket(
                    new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, dir));
            mc.player.networkHandler.sendPacket(
                    new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, dir));
        }
    }
}
