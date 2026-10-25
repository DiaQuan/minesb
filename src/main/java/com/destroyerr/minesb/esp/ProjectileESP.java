package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class ProjectileESP extends ModuleESP {
    public double maxDistance = 128.0;

    @Override public String getName() { return "ProjectileESP"; }
    @Override public int count() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return 0;
        int c = 0;
        for (Entity e : mc.world.getEntities())
            if (e instanceof ProjectileEntity && e.distanceTo(mc.player) <= maxDistance) c++;
        return c;
    }

    @Override
    public void render(WorldRenderContext ctx, BufferBuilder bb, Matrix4f mat) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) return;
        Vec3d cam = ctx.camera().getPos();
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof ProjectileEntity) || e.distanceTo(mc.player) > maxDistance) continue;
            Box box = e.getBoundingBox().offset(-cam.x, -cam.y, -cam.z).expand(0.05);
            Renderer.box(bb, mat, box, 1.0f, 0.1f, 0.1f, 0.9f);
        }
    }
}
