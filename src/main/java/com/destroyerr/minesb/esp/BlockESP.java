package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockESP extends ModuleESP {

    public int minY = -60, maxY = -50;
    public int radius = 6;
    public final Set<Block> selected = ConcurrentHashMap.newKeySet();
    private final Map<ChunkPos, Set<BlockPos>> found = new ConcurrentHashMap<>();
    private long lastScan = 0;

    public BlockESP() {
        selected.add(Blocks.DIAMOND_ORE);
        selected.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        selected.add(Blocks.ANCIENT_DEBRIS);
        selected.add(Blocks.ENDER_CHEST);
        selected.add(Blocks.NETHERITE_BLOCK);
        selected.add(Blocks.DIAMOND_BLOCK);
    }

    @Override public String getName() { return "BlockESP"; }
    @Override public int count() { return found.values().stream().mapToInt(Set::size).sum(); }
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
                Set<BlockPos> hits = new HashSet<>();
                BlockPos.Mutable mp = new BlockPos.Mutable();
                for (int x = 0; x < 16; x++)
                    for (int z = 0; z < 16; z++)
                        for (int y = minY; y <= maxY; y++) {
                            mp.set(cp.getStartX() + x, y, cp.getStartZ() + z);
                            if (selected.contains(chunk.getBlockState(mp).getBlock()))
                                hits.add(mp.toImmutable());
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
        for (Set<BlockPos> set : found.values())
            for (BlockPos p : set) {
                Box box = new Box(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z,
                        p.getX() + 1 - cam.x, p.getY() + 1 - cam.y, p.getZ() + 1 - cam.z);
                Renderer.box(bb, mat, box, 0.0f, 1.0f, 1.0f, 0.9f);
            }
    }
}
