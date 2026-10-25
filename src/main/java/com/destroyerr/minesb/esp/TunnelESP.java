package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.render.Renderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
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

public class TunnelESP extends ModuleESP {

    public int minY = -55, maxY = 60;
    public int radius = 5;
    public int minRunLength = 3;
    private final Map<ChunkPos, List<BlockPos>> tunnels = new ConcurrentHashMap<>();
    private long lastScan = 0;

    @Override public String getName() { return "TunnelESP"; }
    @Override public int count() { return tunnels.values().stream().mapToInt(List::size).sum(); }
    @Override protected void clear() { tunnels.clear(); }

    @Override
    public void tick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) return;
        if (System.currentTimeMillis() - lastScan < 800) return;
        lastScan = System.currentTimeMillis();

        ChunkPos center = mc.player.getChunkPos();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);
                WorldChunk chunk = mc.world.getChunk(cp.x, cp.z);
                if (chunk == null) continue;
                List<BlockPos> hits = new ArrayList<>();
                BlockPos.Mutable mp = new BlockPos.Mutable();
                for (int x = 0; x < 16; x++)
                    for (int z = 0; z < 16; z++) {
                        int run = 0;
                        for (int y = minY; y <= maxY; y++) {
                            mp.set(cp.getStartX() + x, y, cp.getStartZ() + z);
                            boolean air = chunk.getBlockState(mp).isAir();
                            if (air && solidAbove(chunk, mp) && solidSides(chunk, mp)) run++;
                            else {
                                if (run >= minRunLength)
                                    hits.add(new BlockPos(cp.getStartX() + x, y - run / 2, cp.getStartZ() + z));
                                run = 0;
                            }
                        }
                        if (run >= minRunLength)
                            hits.add(new BlockPos(cp.getStartX() + x, maxY - run / 2, cp.getStartZ() + z));
                    }
                if (hits.isEmpty()) tunnels.remove(cp);
                else tunnels.put(cp, hits);
            }
        }
    }

    private boolean solidAbove(WorldChunk c, BlockPos p) {
        var b = c.getBlockState(p.up()).getBlock();
        return !c.getBlockState(p.up()).isAir() && b != Blocks.WATER && b != Blocks.LAVA;
    }
    private boolean solidSides(WorldChunk c, BlockPos p) {
        int s = 0;
        if (!c.getBlockState(p.north()).isAir()) s++;
        if (!c.getBlockState(p.south()).isAir()) s++;
        if (!c.getBlockState(p.east()).isAir()) s++;
        if (!c.getBlockState(p.west()).isAir()) s++;
        return s >= 2;
    }

    @Override
    public void render(WorldRenderContext ctx, BufferBuilder bb, Matrix4f mat) {
        if (tunnels.isEmpty()) return;
        Vec3d cam = ctx.camera().getPos();
        for (List<BlockPos> list : tunnels.values())
            for (BlockPos p : list) {
                Box box = new Box(p.getX() - cam.x, p.getY() - cam.y, p.getZ() - cam.z,
                        p.getX() + 1 - cam.x, p.getY() + 1 - cam.y, p.getZ() + 1 - cam.z);
                Renderer.box(bb, mat, box, 1.0f, 0.55f, 0.0f, 0.9f);
            }
    }
}
