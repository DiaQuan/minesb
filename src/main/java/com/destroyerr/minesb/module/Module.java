package com.destroyerr.minesb.module;

import net.minecraft.client.MinecraftClient;

public abstract class Module {
    protected boolean enabled = false;

    public abstract String getName();
    public void init() {}
    public void tick(MinecraftClient mc) {}
    public int count() { return 0; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean v) { enabled = v; if (!v) clear(); }
    public void toggle() { setEnabled(!enabled); }
    protected void clear() {}
}
