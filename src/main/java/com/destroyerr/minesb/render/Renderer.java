package com.destroyerr.minesb.render;

import com.destroyerr.minesb.esp.ModuleESP;
import com.destroyerr.minesb.module.Module;
import com.destroyerr.minesb.module.ModuleManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;

public final class Renderer {

    private Renderer() {}

    public static void renderAll(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        MatrixStack ms = ctx.matrixStack();
        if (ms == null) return;

        ms.push();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        // 1.21.4: shader programı RenderPipelines üzerinden gelir, manuel setShader gerekmez

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bb = tess.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f mat = ms.peek().getPositionMatrix();

        for (Module m : ModuleManager.MODULES) {
            if (m instanceof ModuleESP esp && m.isEnabled()) {
                esp.render(ctx, bb, mat);
            }
        }

        // 1.21.4: tess.draw() yok → BufferRenderer.drawWithGlobalProgram
        BufferRenderer.drawWithGlobalProgram(bb.end());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        ms.pop();
    }

    public static void box(BufferBuilder bb, Matrix4f m, Box b, float r, float g, float bl, float a) {
        float x1 = (float) b.minX, y1 = (float) b.minY, z1 = (float) b.minZ;
        float x2 = (float) b.maxX, y2 = (float) b.maxY, z2 = (float) b.maxZ;
        line(bb, m, x1, y1, z1, x2, y1, z1, r, g, bl, a);
        line(bb, m, x2, y1, z1, x2, y1, z2, r, g, bl, a);
        line(bb, m, x2, y1, z2, x1, y1, z2, r, g, bl, a);
        line(bb, m, x1, y1, z2, x1, y1, z1, r, g, bl, a);
        line(bb, m, x1, y2, z1, x2, y2, z1, r, g, bl, a);
        line(bb, m, x2, y2, z1, x2, y2, z2, r, g, bl, a);
        line(bb, m, x2, y2, z2, x1, y2, z2, r, g, bl, a);
        line(bb, m, x1, y2, z2, x1, y2, z1, r, g, bl, a);
        line(bb, m, x1, y1, z1, x1, y2, z1, r, g, bl, a);
        line(bb, m, x2, y1, z1, x2, y2, z1, r, g, bl, a);
        line(bb, m, x2, y1, z2, x2, y2, z2, r, g, bl, a);
        line(bb, m, x1, y1, z2, x1, y2, z2, r, g, bl, a);
    }

    public static void line(BufferBuilder bb, Matrix4f m,
                            float x1, float y1, float z1, float x2, float y2, float z2,
                            float r, float g, float b, float a) {
        bb.vertex(m, x1, y1, z1).color(r, g, b, a);
        bb.vertex(m, x2, y2, z2).color(r, g, b, a);
    }
}
