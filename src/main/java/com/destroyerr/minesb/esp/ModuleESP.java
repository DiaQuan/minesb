package com.destroyerr.minesb.esp;

import com.destroyerr.minesb.module.Module;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;

public abstract class ModuleESP extends Module {
    public abstract void render(WorldRenderContext ctx, BufferBuilder bb, Matrix4f mat);
}
