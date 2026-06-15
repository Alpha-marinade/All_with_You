package com.magafin.allwithyou.client;

import com.magafin.allwithyou.common.item.BackpackItem;
import com.magafin.allwithyou.common.item.BackpackTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import com.magafin.allwithyou.common.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ClientBackpackTooltip implements ClientTooltipComponent {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_background");
    private static final ResourceLocation SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_slot");
    private static final ResourceLocation BLOCKED_SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_blocked_slot");
    private static final ResourceLocation SELECTED_SLOT_SPRITE = ResourceLocation.fromNamespaceAndPath("all_with_you", "tooltip/backpack_selected_slot");

    private final BackpackTooltip tooltip;
    private final List<ItemStack> items = new ArrayList<>();
    private final int totalWeight;
    private final int selectedIndex;
    private final int color;

    public ClientBackpackTooltip(BackpackTooltip tooltip) {
        this.tooltip = tooltip;
        tooltip.contents().items().forEach(this.items::add);

        this.totalWeight = BackpackItem.getContentsWeight(this.items);
        this.selectedIndex = tooltip.selectedIndex();
        this.color = tooltip.color();
    }

    @Override
    public int getHeight() {
        return this.gridHeight() + 4;
    }

    @Override
    public int getWidth(Font font) {
        return this.gridWidth();
    }

    private int gridWidth() {
        return this.columns() * 18 + 2;
    }

    private int gridHeight() {
        return this.rows() * 20 + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int cols = this.columns();
        int rows = this.rows();

        float r = ((this.color >> 16) & 0xFF) / 255.0F;
        float g = ((this.color >> 8) & 0xFF) / 255.0F;
        float b = (this.color & 0xFF) / 255.0F;

        graphics.setColor(r, g, b, 1.0F);
        graphics.blitSprite(BACKGROUND_SPRITE, x, y, this.gridWidth(), this.gridHeight());
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        int itemIndex = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int slotX = x + col * 18 + 1;
                int slotY = y + row * 20 + 1;
                this.renderSlot(slotX, slotY, itemIndex++, this.items, graphics, font, r, g, b);
            }
        }
    }

    private void renderSlot(int slotX, int slotY, int index, List<ItemStack> items, GuiGraphics graphics, Font font, float r, float g, float b) {
        graphics.setColor(r, g, b, 1.0F);

        if (index >= items.size()) {
            if (this.totalWeight >= Config.BACKPACK_CAPACITY.get()) {
                graphics.blitSprite(BLOCKED_SLOT_SPRITE, slotX, slotY, 18, 20);
            } else {
                graphics.blitSprite(SLOT_SPRITE, slotX, slotY, 18, 20);
            }
        } else {
            graphics.blitSprite(SLOT_SPRITE, slotX, slotY, 18, 20);

            if (index == this.selectedIndex) {
                graphics.blitSprite(SELECTED_SLOT_SPRITE, slotX, slotY, 18, 20);
            }
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);

        if (index < items.size()) {
            ItemStack stack = items.get(index);
            graphics.renderItem(stack, slotX + 1, slotY + 1);
            graphics.renderItemDecorations(font, stack, slotX + 1, slotY + 1);
        }
    }

    private int columns() {
        return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0)));
    }

    private int rows() {
        return (int)Math.ceil(((double)this.items.size() + 1.0) / (double)this.columns());
    }
}