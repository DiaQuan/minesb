package com.destroyerr.minesb;

import com.destroyerr.minesb.esp.*;
import com.destroyerr.minesb.module.*;
import net.minecraft.block.Block;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class MineSBMenu extends Screen {

    private TextFieldWidget addBlockField, minYField, maxYField, radiusField;
    private final List<ButtonWidget> blockButtons = new ArrayList<>();

    public MineSBMenu() { super(Text.literal("MineSB Ultra — Ayarlar")); }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int top = 26;
        int w = 260, h = 20, gap = 22;

        for (Module m : ModuleManager.MODULES) {
            this.addDrawableChild(ButtonWidget.builder(label(m), b -> { m.toggle(); b.setMessage(label(m)); })
                    .dimensions(cx - w / 2, top, w, h).build());
            top += gap;
        }
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((SpeedMine.enabled ? "§aSpeedMine AÇIK" : "§cSpeedMine KAPALI")),
                b -> { SpeedMine.toggle(); b.setMessage(Text.literal((SpeedMine.enabled ? "§aSpeedMine AÇIK" : "§cSpeedMine KAPALI"))); }
        ).dimensions(cx - w / 2, top, w, h).build());
        top += gap;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((XRay.enabled ? "§aXRay AÇIK" : "§cXRay KAPALI")),
                b -> { XRay.toggle(client); b.setMessage(Text.literal((XRay.enabled ? "§aXRay AÇIK" : "§cXRay KAPALI"))); }
        ).dimensions(cx - w / 2, top, w, h).build());
        top += gap;
        this.addDrawableChild(ButtonWidget.builder(
                Text.literal((FullBright.enabled ? "§aFullBright AÇIK" : "§cFullBright KAPALI")),
                b -> { FullBright.toggle(client); b.setMessage(Text.literal((FullBright.enabled ? "§aFullBright AÇIK" : "§cFullBright KAPALI"))); }
        ).dimensions(cx - w / 2, top, w, h).build());

        top += gap;
        addBlockField = new TextFieldWidget(this.textRenderer, cx - w / 2, top, w - 80, h, Text.literal("blok id"));
        addBlockField.setPlaceholder(Text.literal("minecraft:diamond_ore"));
        this.addDrawableChild(addBlockField);
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Ekle"), b -> addBlock())
                .dimensions(cx + w / 2 - 76, top, 76, h).build());

        top += gap;
        minYField = new TextFieldWidget(this.textRenderer, cx - w / 2, top, 60, h, Text.literal("minY"));
        minYField.setText(String.valueOf(getBlockESP().minY));
        this.addDrawableChild(minYField);
        maxYField = new TextFieldWidget(this.textRenderer, cx - w / 2 + 64, top, 60, h, Text.literal("maxY"));
        maxYField.setText(String.valueOf(getBlockESP().maxY));
        this.addDrawableChild(maxYField);
        radiusField = new TextFieldWidget(this.textRenderer, cx - w / 2 + 128, top, 40, h, Text.literal("r"));
        radiusField.setText(String.valueOf(getBlockESP().radius));
        this.addDrawableChild(radiusField);
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Uygula"), b -> applyRange())
                .dimensions(cx + w / 2 - 76, top, 76, h).build());

        top += gap;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("§eY = -55 tarama (varsayılan)"), b -> {
            getBlockESP().minY = -60; getBlockESP().maxY = -50;
            getTunnelESP().minY = -55; getTunnelESP().maxY = 60;
            minYField.setText("-60"); maxYField.setText("-50");
        }).dimensions(cx - w / 2, top, w, h).build());

        top += gap;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Seçili blokları temizle"), b -> {
            getBlockESP().selected.clear(); rebuildBlockButtons();
        }).dimensions(cx - w / 2, top, w, h).build());

        rebuildBlockButtons();
    }

    private Text label(Module m) {
        return Text.literal((m.isEnabled() ? "§a" : "§c") + m.getName() + (m.isEnabled() ? " AÇIK" : " KAPALI"));
    }
    private BlockESP getBlockESP() {
        return (BlockESP) ModuleManager.MODULES.stream().filter(m -> m instanceof BlockESP).findFirst().orElseThrow();
    }
    private TunnelESP getTunnelESP() {
        return (TunnelESP) ModuleManager.MODULES.stream().filter(m -> m instanceof TunnelESP).findFirst().orElseThrow();
    }

    private void rebuildBlockButtons() {
        blockButtons.forEach(this::remove);
        blockButtons.clear();
        int cx = this.width / 2;
        int startY = this.height - 100;
        int bw = 110, bh = 18, pad = 4, perRow = 4;
        List<Block> list = new ArrayList<>(getBlockESP().selected);
        list.sort(Comparator.comparing(b -> Registries.BLOCK.getId(b).toString()));
        for (int i = 0; i < list.size(); i++) {
            Block b = list.get(i);
            int x = cx - (perRow * (bw + pad)) / 2 + (i % perRow) * (bw + pad);
            int y = startY + (i / perRow) * (bh + pad);
            String name = Registries.BLOCK.getId(b).getPath();
            ButtonWidget btn = ButtonWidget.builder(
                    Text.literal("§c✖ §f" + name),
                    bb -> { getBlockESP().selected.remove(b); rebuildBlockButtons(); }
            ).dimensions(x, y, bw, bh).build();
            blockButtons.add(btn);
            this.addDrawableChild(btn);
        }
    }

    private void addBlock() {
        String s = addBlockField.getText().trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return;
        if (!s.contains(":")) s = "minecraft:" + s;
        Identifier id = Identifier.tryParse(s);
        if (id == null || !Registries.BLOCK.containsId(id)) {
            if (client != null && client.player != null)
                client.player.sendMessage(Text.literal("Geçersiz blok: " + s).formatted(Formatting.RED), true);
            return;
        }
        getBlockESP().selected.add(Registries.BLOCK.get(id));
        addBlockField.setText("");
        rebuildBlockButtons();
    }

    private void applyRange() {
        try {
            int a = Integer.parseInt(minYField.getText().trim());
            int b = Integer.parseInt(maxYField.getText().trim());
            int r = Integer.parseInt(radiusField.getText().trim());
            if (a > b) { int t = a; a = b; b = t; }
            BlockESP be = getBlockESP();
            be.minY = a; be.maxY = b; be.radius = r;
            TunnelESP te = getTunnelESP();
            te.minY = a; te.maxY = Math.max(b, 60); te.radius = r;
            if (client != null && client.player != null)
                client.player.sendMessage(Text.literal("Ayarlandı: Y=" + a + ".." + b + " R=" + r)
                        .formatted(Formatting.GREEN), true);
        } catch (NumberFormatException e) {
            if (client != null && client.player != null)
                client.player.sendMessage(Text.literal("Sayı geçersiz").formatted(Formatting.RED), true);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("§7Seçili blok sayısı: " + getBlockESP().selected.size()),
                this.width / 2 - 130, this.height - 110, 0xFFFFFF);
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
