package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

/**
 * This is one of the Skins provided by DynamicHUD featuring the classic rendering,
 * which should be used when you have a low amount of settings and want quicker way of changing the settings.
 */
public class ClassicSkin extends Skin {
    public ClassicSkin() {
        super();

        addRenderer(BooleanOption.class, ClassicBooleanRenderer::new);
        addRenderer(DoubleOption.class, ClassicDoubleRenderer::new);
        addRenderer(EnumOption.class, ClassicEnumRenderer::new);
        addRenderer(ListOption.class, ClassicListRenderer::new);
        addRenderer(SubMenuOption.class, ClassicSubMenuRenderer::new);
        addRenderer(RunnableOption.class, ClassicRunnableRenderer::new);
        addRenderer(ColorOption.class, ClassicColorOptionRenderer::new);

        setCreateNewScreen(false);
    }

    @Override
    public void renderContextMenu(DrawContext drawContext, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        this.contextMenu = contextMenu;

        MatrixStack matrices = drawContext.getMatrices();
        ContextMenuProperties properties = contextMenu.getProperties();

        // Draw the background
        drawBackground(matrices, contextMenu, properties);

        int yOffset = contextMenu.y + 3;
        int width = 10;
        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            // Adjust mouse coordinates based on the scale
            if (contextMenu.getProperties().hoverEffect() && contextMenu.isMouseOver(mouseX, mouseY, contextMenu.x + 1, yOffset - 1, contextMenu.getWidth() - 2, option.getHeight())) {
                drawBackground(matrices, contextMenu, properties, yOffset - 1, contextMenu.getWidth(), option.getHeight() + 1, contextMenu.getProperties().getHoverColor().getRGB(), false);
            }

            option.render(drawContext, contextMenu.x + 4, yOffset, mouseX, mouseY);
            width = Math.max(width, option.getWidth());
            yOffset += option.getHeight() + 1;
        }
        contextMenu.setWidth(width + properties.getPadding());
        contextMenu.setHeight(yOffset - contextMenu.y);

        // Draw the border if needed
        if (properties.shouldDrawBorder()) {
            drawBorder(matrices, contextMenu, properties);
        }
    }

    private void drawBackground(MatrixStack matrices, ContextMenu<?> contextMenu, ContextMenuProperties properties) {
        drawBackground(matrices, contextMenu, properties, contextMenu.y, contextMenu.getWidth(), contextMenu.getHeight(), properties.getBackgroundColor().getRGB(), properties.shadow());
    }

    private void drawBackground(MatrixStack matrices, ContextMenu<?> contextMenu, ContextMenuProperties properties, int yOffset, int width, int height, int color, boolean shadow) {
        if (properties.roundedCorners()) {
            // Rounded
            if (shadow) {
                DrawHelper.drawRoundedRectangleWithShadowBadWay(matrices.peek().getPositionMatrix(),
                        contextMenu.x,
                        yOffset,
                        width,
                        height,
                        properties.getCornerRadius(),
                        color,
                        150,
                        1,
                        1
                );
            } else {
                DrawHelper.drawRoundedRectangle(matrices.peek().getPositionMatrix(),
                        contextMenu.x,
                        yOffset,
                        width,
                        height,
                        properties.getCornerRadius(),
                        color
                );
            }
        } else {
            // Normal
            if (shadow) {
                DrawHelper.drawRectangleWithShadowBadWay(matrices.peek().getPositionMatrix(),
                        contextMenu.x,
                        yOffset,
                        width,
                        height,
                        color,
                        150,
                        1,
                        1
                );
            } else {
                DrawHelper.drawRectangle(matrices.peek().getPositionMatrix(),
                        contextMenu.x,
                        yOffset,
                        width,
                        height,
                        color
                );
            }
        }
    }

    private void drawBorder(MatrixStack matrices, ContextMenu<?> contextMenu, ContextMenuProperties properties) {
        if (properties.roundedCorners()) {
            DrawHelper.drawOutlineRoundedBox(matrices.peek().getPositionMatrix(),
                    contextMenu.x,
                    contextMenu.y,
                    contextMenu.getWidth(),
                    contextMenu.getHeight(),
                    properties.getCornerRadius(),
                    properties.getBorderWidth(),
                    properties.getBorderColor().getRGB()
            );
        } else {
            DrawHelper.drawOutlineBox(matrices.peek().getPositionMatrix(),
                    contextMenu.x,
                    contextMenu.y,
                    contextMenu.getWidth(),
                    contextMenu.getHeight(),
                    properties.getBorderWidth(),
                    properties.getBorderColor().getRGB()
            );
        }
    }

    public static class ClassicBooleanRenderer implements SkinRenderer<BooleanOption> {
        @Override
        public void render(DrawContext drawContext, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.value ? Color.GREEN.getRGB() : Color.RED.getRGB();
            drawContext.drawText(mc.textRenderer, option.name, x, y, color, false);
            option.setHeight(mc.textRenderer.fontHeight);
            option.setWidth(mc.textRenderer.getWidth(option.name) + 1);
        }
    }

    public static class ClassicColorOptionRenderer implements SkinRenderer<ColorOption> {
        @Override
        public void render(DrawContext drawContext, ColorOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.isVisible ? Color.GREEN.getRGB() : Color.RED.getRGB();
            drawContext.drawText(mc.textRenderer, option.name, x, y, color, false);
            option.setHeight(mc.textRenderer.fontHeight);
            option.setWidth(mc.textRenderer.getWidth(option.name) + 10);

            int shadowOpacity = Math.min(option.value.getAlpha(), 90);
            DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                    x + option.getWidth() - 10 + 1,
                    y - 1,
                    8,
                    8,
                    2,
                    option.value.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            option.getColorGradient().render(drawContext, x + option.getParentMenu().getWidth() + 7, y - 10,mouseX,mouseY);
        }
    }

    public static class ClassicEnumRenderer<E extends Enum<E>> implements SkinRenderer<EnumOption<E>> {
        @Override
        public void render(DrawContext drawContext, EnumOption<E> option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(mc.textRenderer.fontHeight + 1);
            option.setWidth(mc.textRenderer.getWidth(option.name + ": " + option.value.name()) + 1);

            drawContext.drawText(mc.textRenderer, option.name.copy().append( ": "), x, y, Color.WHITE.getRGB(), false);
            drawContext.drawText(mc.textRenderer, option.value.name(), x + mc.textRenderer.getWidth(option.name + ": ") + 1, y, Color.CYAN.getRGB(), false);
        }
    }

    public static class ClassicSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(DrawContext drawContext, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.value ? Color.GREEN.getRGB() : Color.RED.getRGB();
            drawContext.drawText(mc.textRenderer, option.name, x, y, color, false);
            option.setHeight(mc.textRenderer.fontHeight);
            option.setWidth(mc.textRenderer.getWidth(option.name) + 1);

            option.getSubMenu().render(drawContext, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
        }
    }

    public static class ClassicRunnableRenderer implements SkinRenderer<RunnableOption> {
        Color DARK_RED = new Color(116, 0, 0);
        Color DARK_GREEN = new Color(24, 132, 0, 226);

        @Override
        public void render(DrawContext drawContext, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(mc.textRenderer.fontHeight);
            option.setWidth(mc.textRenderer.getWidth("Run: " + option.name));
            int color = option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB();
            drawContext.drawText(mc.textRenderer, Text.literal("Run: ").append(option.name), x, y, color, false);
        }
    }

    public class ClassicDoubleRenderer implements SkinRenderer<DoubleOption> {
        @Override
        public void render(DrawContext drawContext, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            option.setWidth(Math.max(35, contextMenu != null ? contextMenu.getWidth() - option.getProperties().getPadding() - 2 : 0));
            option.setHeight(16);

            // Draw the label
            TextRenderer textRenderer = mc.textRenderer;
            DrawHelper.scaleAndPosition(drawContext.getMatrices(), x, y, 0.7f);
            Text labelText = option.name.copy().append(": " + String.format("%.1f", option.value));
            int labelWidth = textRenderer.getWidth(labelText);

            option.setWidth(Math.max(option.getWidth(), labelWidth));

            drawContext.drawTextWithShadow(textRenderer, labelText, x, y + 1, 0xFFFFFFFF);
            DrawHelper.stopScaling(drawContext.getMatrices());

            float handleWidth = 3;
            float handleHeight = 8;
            double handleX = x + (option.value - option.minValue) / (option.maxValue - option.minValue) * (option.getWidth() - handleWidth);
            double handleY = y + textRenderer.fontHeight + 1 + ((2 - handleHeight) / 2);

            // Draw the slider
            option.drawSlider(drawContext, x, y + textRenderer.fontHeight + 1, option.getWidth(), handleX);

            // Draw the handle

            DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                    (float) handleX,
                    (float) handleY,
                    handleWidth,
                    handleHeight,
                    1,
                    0xFFFFFFFF,
                    90,
                    0.6f,
                    0.6f);
        }
    }

    public static class ClassicListRenderer<E> implements SkinRenderer<ListOption<E>> {
        @Override
        public void render(DrawContext drawContext, ListOption<E> option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(mc.textRenderer.fontHeight + 1);
            option.setWidth(mc.textRenderer.getWidth(option.name + ": " + option.value.toString()) + 1);

            drawContext.drawText(mc.textRenderer, option.name.copy().append( ": "), x, y + 1, Color.WHITE.getRGB(), false);
            drawContext.drawText(mc.textRenderer, option.value.toString(), x + mc.textRenderer.getWidth(option.name + ": ") + 1, y + 1, Color.CYAN.getRGB(), false);
        }
    }
}