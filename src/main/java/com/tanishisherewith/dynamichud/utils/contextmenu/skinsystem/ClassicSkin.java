package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.Util;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

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
        addRenderer(CycleOption.class, ClassicCycleRenderer::new);
        addRenderer(SubMenuOption.class, ClassicSubMenuRenderer::new);
        addRenderer(RunnableOption.class, ClassicRunnableRenderer::new);
        addRenderer(DoubleOption.class, ClassicDoubleRenderer::new);
        addRenderer(KeybindOption.class, ClassicKeybindRenderer::new);

        setCreateNewScreen(false);
    }

    @Override
    public void renderContextMenu(GuiGraphicsExtractor graphics, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        this.contextMenu = contextMenu;
        ContextMenuProperties properties = contextMenu.getProperties();

        drawBackground(graphics, contextMenu, properties);

        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (properties.hoverEffect() && contextMenu.isMouseOver(mouseX, mouseY, option.getX() - 3, option.getY() - 1, contextMenu.getWidth() - 2, option.getHeight())) {
                drawBackground(graphics, contextMenu, properties, option.getY() - 1, contextMenu.getWidth(), option.getHeight() + 1, properties.getHoverColor().getRGB(), false);
            }
        }

        if (contextMenu.getRootNode() != null) {
            contextMenu.getRootNode().render(graphics, mouseX, mouseY);
        }

        if (properties.shouldDrawBorder()) {
            drawBorder(graphics, contextMenu, properties);
        }
    }

    private void drawBackground(GuiGraphicsExtractor graphics, ContextMenu<?> contextMenu, ContextMenuProperties properties) {
        drawBackground(graphics, contextMenu, properties, contextMenu.y, contextMenu.getWidth(), contextMenu.getHeight(), properties.getBackgroundColor().getRGB(), properties.shadow());
    }

    private void drawBackground(GuiGraphicsExtractor graphics, ContextMenu<?> contextMenu, ContextMenuProperties properties, int yOffset, int width, int height, int color, boolean shadow) {
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

    private void drawBorder(GuiGraphicsExtractor graphics, ContextMenu<?> contextMenu, ContextMenuProperties properties) {
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
        public void render(GuiGraphicsExtractor graphics, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.get() ? Color.GREEN.getRGB() : Color.RED.getRGB();
            Component displayName = Util.getTruncatedName(option.name, option.getWidth());
            graphics.text(mc.font, displayName, x, y, color, false);
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, option.getWidth());
        }
    }

    public static class ClassicColorOptionRenderer implements SkinRenderer<ColorOption> {
        @Override
        public void render(GuiGraphicsExtractor graphics, ColorOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.isVisible ? Color.GREEN.getRGB() : Color.RED.getRGB();
            Component text = Util.getTruncatedName(option.name, option.getWidth() - 4);
            graphics.text(mc.font, text, x, y, color, false);

            int shadowOpacity = Math.min(option.value.getAlpha(), 90);
            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    x + option.getWidth() - 8,
                    y - 1,
                    8,
                    8,
                    3,
                    option.value.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            option.getColorGradient().render(graphics, x + option.getParentMenu().getWidth() + 7, y - 10, mouseX, mouseY);
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, option.getWidth() - 4);
        }
    }

    public static class ClassicCycleRenderer<E> implements SkinRenderer<CycleOption<E>> {
        @Override
        public void render(GuiGraphicsExtractor graphics, CycleOption<E> option, int x, int y, int mouseX, int mouseY) {
            String valStr = option.get().toString();
            int valueWidth = mc.font.width(": " + valStr);
            Component displayName = Util.getTruncatedName(option.name, option.getWidth() - valueWidth - 2);
            graphics.text(mc.font, displayName.copy().append(": "), x, y, Color.WHITE.getRGB(), false);
            graphics.text(mc.font, valStr, x + mc.font.width(displayName.getString() + ": ") + 1, y, Color.CYAN.getRGB(), false);
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, option.getWidth() - valueWidth - 2);
        }
    }

    public static class ClassicSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(GuiGraphicsExtractor graphics, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.value ? Color.GREEN.getRGB() : Color.RED.getRGB();
            Component displayName = Util.getTruncatedName(option.name, option.getParentMenu().getWidth() - 14);
            graphics.text(mc.font, displayName, x, y, color, false);
            graphics.text(mc.font, option.getSubMenu().isVisible() ? "-" : "+", x + Math.max(option.getParentMenu().getWidth() - 12, mc.font.width(displayName) + 2), y, color, false);

            option.getSubMenu().render(graphics, x + option.getParentMenu().getWidth(), y - 1, mouseX, mouseY);
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, option.getParentMenu().getWidth() - 14);
        }
    }

    public static class ClassicRunnableRenderer implements SkinRenderer<RunnableOption> {
        @Override
        public void render(GuiGraphicsExtractor graphics, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            int color = option.value ? ColorHelper.DARK_GREEN.getRGB() : ColorHelper.DARK_RED.getRGB();
            Component displayName = Util.getTruncatedName(Component.literal("Run: ").append(option.name), option.getWidth());
            graphics.text(mc.font, displayName, x, y, color, false);
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, option.getWidth());
        }
    }

    public static class ClassicDoubleRenderer implements SkinRenderer<DoubleOption> {
        @Override
        public void render(GuiGraphicsExtractor graphics, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            Font font = mc.font;
            int decimalPlaces = String.valueOf(option.step).split("\\.")[1].length();

            String decimalValue = String.format("%." + decimalPlaces + "f", option.value);
            float scale = 0.7f;
            //get the truncated text with the scaled width
            int maxTextWidth = (int)(option.getWidth()*(1/scale)) - (int)(mc.font.width(": " + decimalValue) * scale) - 2;
            Component labelText = Util.getTruncatedName(option.name, maxTextWidth).append(": " + decimalValue);
            Util.drawScaledText(graphics, labelText, x, y + 1, scale, 0xFFFFFFFF);

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
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, maxTextWidth);
        }
    }

    public static class ClassicKeybindRenderer implements SkinRenderer<KeybindOption> {
        @Override
        public void render(GuiGraphicsExtractor graphics, KeybindOption option, int x, int y, int mouseX, int mouseY) {
            String valueText = option.isListening() ? "???" : KeybindOption.getKeyName(option.get());
            int valueColor = option.isListening() ? Color.YELLOW.getRGB() : Color.CYAN.getRGB();
            int valueWidth = mc.font.width(": " + valueText);
            Component displayName = Util.getTruncatedName(option.name, option.getWidth() - valueWidth - 2);
            graphics.text(mc.font, displayName.copy().append(": "), x, y, Color.WHITE.getRGB(), false);
            graphics.text(mc.font, valueText, x + mc.font.width(displayName.getString() + ": ") + 1, y, valueColor, false);
            option.getProperties().getSkin().renderTooltipIfHovered(graphics, option, x, y, mouseX, mouseY, option.getWidth() - valueWidth - 2);
        }

        @Override
        public boolean mouseClicked(KeybindOption option, double mouseX, double mouseY, int button) {
            if (option.isMouseOver(mouseX, mouseY)) {
                option.setListening(true);
                return true;
            }
            return false;
        }
    }
}