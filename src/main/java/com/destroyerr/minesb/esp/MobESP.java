package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class MobESP extends ModuleESP {
    public double maxDistance = 128.0;
    public boolean hostileOnly = false;

    @Override public String getName() { return "MobESP"; }
    @Override public int count() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return 0;
        int c = 0;
        for (Entity e : mc.world.getEntities())
            if (e instanceof LivingEntity && !(e instanceof PlayerEntity) && e.distanceTo(mc.player) <= maxDistance)
                if (!hostileOnly || e instanceof HostileEntity) c++;
        return c;
    }

    @Override
    public void render(WorldRenderContext ctx, BufferBuilder bb, Matrix4f mat) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        Vec3d cam = ctx.camera().getPos();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof LivingEntity) || e instanceof PlayerEntity) continue;
            if (e.distanceTo(mc.player) > maxDistance) continue;
            if (hostileOnly && !(e instanceof HostileEntity)) continue;
            float r = e instanceof HostileEntity ? 1.0f : 0.3f;
            float g = e instanceof HostileEntity ? 0.2f : 0.9f;
            float b = e instanceof HostileEntity ? 0.2f : 0.9f;
            Box box = e.getBoundingBox().offset(-cam.x, -cam.y, -cam.z).expand(0.04);
            Renderer.box(bb, mat, box, r, g, b, 0.9f);
        }
    }
}
