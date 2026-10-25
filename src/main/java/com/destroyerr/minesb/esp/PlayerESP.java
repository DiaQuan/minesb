package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class PlayerESP extends ModuleESP {
    public double maxDistance = 256.0;

    @Override public String getName() { return "PlayerESP"; }
    @Override public int count() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return 0;
        return (int) mc.world.getPlayers().stream()
                .filter(p -> p != mc.player && p.distanceTo(mc.player) <= maxDistance).count();
    }

    @Override
    public void render(WorldRenderContext ctx, BufferBuilder bb, Matrix4f mat) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        Vec3d cam = ctx.camera().getPos();
        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;
            if (p instanceof AbstractClientPlayerEntity acp && acp.isInvisible()) continue;
            if (p.distanceTo(mc.player) > maxDistance) continue;
            Box box = p.getBoundingBox().offset(-cam.x, -cam.y, -cam.z).expand(0.05);
            Renderer.box(bb, mat, box, 0.0f, 1.0f, 0.0f, 0.95f);
        }
    }
}
