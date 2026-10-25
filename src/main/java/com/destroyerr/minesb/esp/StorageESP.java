package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageESP extends ModuleESP {

    public int radius = 6;
    private final Map<ChunkPos, List<BlockEntity>> found = new ConcurrentHashMap<>();
    private long lastScan = 0;

    @Override public String getName() { return "StorageESP"; }
    @Override public int count() { return found.values().stream().mapToInt(List::size).sum(); }
    @Override protected void clear() { found.clear(); }

    @Override
    public void tick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;
        if (System.currentTimeMillis() - lastScan < 500) return;
        lastScan = System.currentTimeMillis();
        ChunkPos center = mc.player.getChunkPos();
        Set<ChunkPos> now = new HashSet<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);
                now.add(cp);
                WorldChunk chunk = mc.world.getChunk(cp.x, cp.z);
                if (chunk == null) continue;
                List<BlockEntity> hits = new ArrayList<>();
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity
                            || be instanceof FurnaceBlockEntity || be instanceof BlastFurnaceBlockEntity
                            || be instanceof SmokerBlockEntity || be instanceof HopperBlockEntity
                            || be instanceof DispenserBlockEntity || be instanceof DropperBlockEntity
                            || be instanceof ShulkerBoxBlockEntity || be instanceof EnderChestBlockEntity) {
                        hits.add(be);
                    }
                }
                if (hits.isEmpty()) found.remove(cp);
                else found.put(cp, hits);
            }
        }
        found.keySet().removeIf(cp -> !now.contains(cp));
    }

    @Override
    public void render(WorldRenderContext ctx, BufferBuilder bb, Matrix4f mat) {
        if (found.isEmpty()) return;
        Vec3d cam = ctx.camera().getPos();
        for (List<BlockEntity> list : found.values())
            for (BlockEntity be : list) {
                var p = be.getPos();
                Box box = new Box(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z,
                        p.getX() + 1 - cam.x, p.getY() + 1 - cam.y, p.getZ() + 1 - cam.z).expand(0.02);
                float r = 1.0f, g = 0.85f, b = 0.0f;
                if (be instanceof EnderChestBlockEntity) { r = 0.55f; g = 0.0f; b = 1.0f; }
                else if (be instanceof ShulkerBoxBlockEntity) { r = 0.9f; g = 0.2f; b = 0.9f; }
                Renderer.box(bb, mat, box, r, g, b, 0.9f);
            }
    }
}
