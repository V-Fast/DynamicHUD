package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.awt.*;
/**
 * This is one of the Skins provided by DynamicHUD featuring the classic rendering,
 * which should be used when you have a low amount of settings and want quicker way of changing the settings.
 */
public class ClassicSkin extends Skin {

    public ClassicSkin() {
        super();
        addRenderer(BooleanOption.class, ClassicBooleanRenderer::new);
        addRenderer(ColorOption.class, ClassicColorOptionRenderer::new);
        addRenderer(EnumOption.class, ClassicEnumRenderer::new);
        addRenderer(ListOption.class, ClassicListRenderer::new);
        addRenderer(SubMenuOption.class, ClassicSubMenuRenderer::new);
        addRenderer(RunnableOption.class, ClassicRunnableRenderer::new);
        addRenderer(DoubleOption.class, ClassicDoubleRenderer::new);

        setCreateNewScreen(false);
    }

    @Override
    public void renderContextMenu(GuiGraphics graphics, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        this.contextMenu = contextMenu;
        ContextMenuProperties properties = contextMenu.getProperties();

        if (contextMenu.getLayoutEngine() != null) {
            contextMenu.getLayoutEngine().applyLayout(contextMenu);
        }

        drawBackground(graphics, contextMenu, properties);

        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (properties.hoverEffect() && contextMenu.isMouseOver(mouseX, mouseY, option.getX() - 3, option.getY() - 1, contextMenu.getWidth() - 2, option.getHeight())) {
                drawBackground(graphics, contextMenu, properties, option.getY() - 1, contextMenu.getWidth(), option.getHeight() + 1, properties.getHoverColor().getRGB(), false);
            }

            option.render(graphics, option.getX(), option.getY(), mouseX, mouseY);
        }

        if (properties.shouldDrawBorder()) {
            drawBorder(graphics, contextMenu, properties);
        }
    }

    private void drawBackground(GuiGraphics graphics, ContextMenu<?> contextMenu, ContextMenuProperties properties) {
        drawBackground(graphics, contextMenu, properties, contextMenu.y, contextMenu.getWidth(), contextMenu.getHeight(), properties.getBackgroundColor().getRGB(), properties.shadow());
    }

    private void drawBackground(GuiGraphics graphics, ContextMenu<?> contextMenu, ContextMenuProperties properties, int yOffset, int width, int height, int color, boolean shadow) {
        if (properties.roundedCorners()) {
            if (shadow) {
                DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics, contextMenu.x, yOffset, width, height, properties.getCornerRadius(), color, 150, 1, 1);
            } else {
                DrawHelper.drawRoundedRectangle(graphics, contextMenu.x, yOffset, width, height, properties.getCornerRadius(), color);
            }
        } else {
            if (shadow) {
                DrawHelper.drawRectangleWithShadowBadWay(graphics, contextMenu.x, yOffset, width, height, color, 150, 1, 1);
            } else {
                DrawHelper.drawRectangle(graphics, contextMenu.x, yOffset, width, height, color);
            }
        }
    }

    private void drawBorder(GuiGraphics graphics, ContextMenu<?> contextMenu, ContextMenuProperties properties) {
        if (properties.roundedCorners()) {
            DrawHelper.drawOutlineRoundedBox(graphics, contextMenu.x, contextMenu.y, contextMenu.getWidth(), contextMenu.getHeight(), properties.getCornerRadius(), properties.getBorderWidth(), properties.getBorderColor().getRGB());
        } else {
            DrawHelper.drawOutlineBox(graphics, contextMenu.x, contextMenu.y, contextMenu.getWidth(), contextMenu.getHeight(), properties.getBorderWidth(), properties.getBorderColor().getRGB());
        }
    }

    @Override
    public Skin clone() {
        return new ClassicSkin();
    }


    public static class ClassicBooleanRenderer implements SkinRenderer<BooleanOption> {
        @Override
        public void render(GuiGraphics graphics, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.get() ? Color.GREEN.getRGB() : Color.RED.getRGB();
            graphics.drawString(mc.font, option.name, x, y, color, false);
        }
    }

    public static class ClassicColorOptionRenderer implements SkinRenderer<ColorOption> {
        @Override
        public void render(GuiGraphics graphics, ColorOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.isVisible ? Color.GREEN.getRGB() : Color.RED.getRGB();
            graphics.drawString(mc.font, option.name, x, y, color, false);

            int shadowOpacity = Math.min(option.value.getAlpha(), 90);
            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    x + option.getWidth() - 10,
                    y - 1,
                    8,
                    8,
                    3,
                    option.value.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            option.getColorGradient().render(graphics, x + option.getParentMenu().getWidth() + 7, y - 10, mouseX, mouseY);
        }
    }

    public static class ClassicEnumRenderer<E extends Enum<E>> implements SkinRenderer<EnumOption<E>> {
        @Override
        public void render(GuiGraphics graphics, EnumOption<E> option, int x, int y, int mouseX, int mouseY) {
            graphics.drawString(mc.font, option.name.copy().append(": "), x, y, Color.WHITE.getRGB(), false);
            graphics.drawString(mc.font, option.get().name(), x + mc.font.width(option.name + ": ") + 1, y, Color.CYAN.getRGB(), false);
        }
    }

    public static class ClassicListRenderer<E> implements SkinRenderer<ListOption<E>> {
        @Override
        public void render(GuiGraphics graphics, ListOption<E> option, int x, int y, int mouseX, int mouseY) {
            graphics.drawString(mc.font, option.name.copy().append(": "), x, y + 1, Color.WHITE.getRGB(), false);
            graphics.drawString(mc.font, option.get().toString(), x + mc.font.width(option.name + ": ") + 1, y + 1, Color.CYAN.getRGB(), false);
        }
    }

    public static class ClassicSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(GuiGraphics graphics, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.value ? Color.GREEN.getRGB() : Color.RED.getRGB();
            graphics.drawString(mc.font, option.name, x, y, color, false);
            graphics.drawString(mc.font, option.getSubMenu().isVisible() ? "-" : "+", x + Math.max(option.getParentMenu().getWidth() - 12, mc.font.width(option.name) + 2), y, color, false);

            option.getSubMenu().render(graphics, x + option.getParentMenu().getWidth(), y - 1, mouseX, mouseY);
        }
    }

    public static class ClassicRunnableRenderer implements SkinRenderer<RunnableOption> {
        @Override
        public void render(GuiGraphics graphics, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.value ? ColorHelper.DARK_GREEN.getRGB() : ColorHelper.DARK_RED.getRGB();
            graphics.drawString(mc.font, Component.literal("Run: ").append(option.name), x, y, color, false);
        }
    }

    public static class ClassicDoubleRenderer implements SkinRenderer<DoubleOption> {
        @Override
        public void render(GuiGraphics graphics, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            Font font = mc.font;
            DrawHelper.scaleAndPosition(graphics.pose(), x, y, 0.7f);
            Component labelText = option.name.copy().append(": " + String.format("%.1f", option.value));
            graphics.drawString(font, labelText, x, y + 1, 0xFFFFFFFF, true);
            DrawHelper.stopScaling(graphics.pose());

            float handleWidth = 3;
            float handleHeight = 8;
            double handleX = x + (option.value - option.minValue) / (option.maxValue - option.minValue) * (option.getWidth() - handleWidth);
            double handleY = y + font.lineHeight + 1 + ((2 - handleHeight) / 2);

            option.drawSlider(graphics, x, y + font.lineHeight + 1, option.getWidth(), handleX);

            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    (float) handleX,
                    (float) handleY,
                    handleWidth,
                    handleHeight,
                    3,
                    0xFFFFFFFF,
                    90,
                    0.6f,
                    0.6f);
        }
    }
}