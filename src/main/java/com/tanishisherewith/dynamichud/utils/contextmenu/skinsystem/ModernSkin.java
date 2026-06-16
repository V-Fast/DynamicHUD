package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.MathAnimations;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutContext;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import com.tanishisherewith.dynamichud.utils.handlers.ScrollHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static com.tanishisherewith.dynamichud.helpers.ColorHelper.DARK_GREEN;
import static com.tanishisherewith.dynamichud.helpers.ColorHelper.DARK_RED;

public class ModernSkin extends Skin implements GroupableSkin {
    static Color DARK_GRAY = new Color(20, 20, 20, 229);
    static Color DARKER_GRAY = new Color(10, 10, 10, 243);
    static Color DARKER_GRAY_2 = new Color(12, 12, 12, 246);

    private final float radius;
    private final Component defaultToolTipHeader;
    private final Component defaultToolTipText;
    private int contextMenuX = 0, contextMenuY = 0;
    private int width = 0, height = 0;
    private float scaledWidth = 0, scaledHeight = 0;
    private Component TOOLTIP_TEXT;
    private Component TOOLTIP_HEAD;
    private static int SCALE_FACTOR = 4;
    private final ScrollHandler scrollHandler;

    public ModernSkin(float radius, Component defaultToolTipHeader, Component defaultToolTipText) {
        this.radius = radius;
        this.TOOLTIP_TEXT = defaultToolTipText;
        this.TOOLTIP_HEAD = defaultToolTipHeader;
        this.defaultToolTipText = defaultToolTipText;
        this.defaultToolTipHeader = defaultToolTipHeader;

        addRenderer(BooleanOption.class, ModernBooleanRenderer::new);
        addRenderer(DoubleOption.class, ModernDoubleRenderer::new);
        addRenderer(EnumOption.class, ModernEnumRenderer::new);
        addRenderer(ListOption.class, ModernListRenderer::new);
        addRenderer(SubMenuOption.class, ModernSubMenuRenderer::new);
        addRenderer(RunnableOption.class, ModernRunnableRenderer::new);
        addRenderer(ColorOption.class, ModernColorOptionRenderer::new);

        this.scrollHandler = new ScrollHandler();

        setCreateNewScreen(true);
    }

    public ModernSkin(float radius) {
        this(radius, Component.literal("Example Tip"), Component.literal("Hover over a setting to see its tool-tip (if present) here!"));
    }

    public ModernSkin() {
        this(4);
    }

    @Override
    public LayoutContext.Offset getGroupIndent() {
        return new LayoutContext.Offset(4, 4);
    }

    public void enableSkinScissor(GuiGraphics graphics) {
        DrawHelper.enableScissor(contextMenuX + (int) (width * 0.2f) + 10, contextMenuY + 19, (int) (width * 0.8f - 14), height - 23, SCALE_FACTOR, graphics);
    }

    @Override
    public void renderGroup(GuiGraphics graphics, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY) {
        int targetWidth = (int) (width * 0.8f - 18);
        renderGroup(graphics, group, groupX, groupY, targetWidth, mouseX, mouseY);
    }
    private int calcOptionHeight(Option<?> option) {
        if (option instanceof BooleanOption || option instanceof DoubleOption) return 14;
        if (option instanceof EnumOption || option instanceof ListOption) return mc.font.lineHeight + 2;
        if (option instanceof SubMenuOption) return 16;
        if (option instanceof RunnableOption) return mc.font.lineHeight + 6;
        if (option instanceof ColorOption colorOption) {
            return colorOption.getHeight() > 0 ? colorOption.getHeight() : 20;
        }
        if (option instanceof OptionGroup group) {
            return group.isExpanded() ? group.getHeight() : 20;
        }
        return option.getHeight() > 0 ? option.getHeight() : mc.font.lineHeight;
    }

    public void renderGroup(GuiGraphics graphics, OptionGroup group, int groupX, int groupY, int targetWidth, int mouseX, int mouseY) {
        mouseX = (int) (mc.mouseHandler.xpos() / SCALE_FACTOR);
        mouseY = (int) (mc.mouseHandler.ypos() / SCALE_FACTOR);

        if (group.isExpanded() && group.getHeight() > 20) {
            DrawHelper.drawRoundedRectangle(graphics,
                    groupX + 1, groupY + 14, width - groupX - 8 + contextMenuX, group.getHeight() - 16, radius, DARKER_GRAY_2.getRGB());
        }

        Component groupText = group.name.copy().append(" " + (group.isExpanded() ? "-" : "+"));

        DrawHelper.drawRoundedRectangle(graphics,
                groupX + 1, groupY + 1, true, true, !group.isExpanded(), !group.isExpanded(), mc.font.width(groupText) + 6, 16, radius, DARKER_GRAY_2.getRGB());

        graphics.drawString(mc.font, groupText, groupX + 4, groupY + 4, -1, true);

        if (group.isExpanded()) {
            int yOffset = groupY + 16 + getGroupIndent().top();
            int nestedIndent = getGroupIndent().left();
            int subWidth = targetWidth - nestedIndent - 8;

            for (Option<?> option : group.getGroupOptions()) {
                if (!option.shouldRender()) continue;

                // Position child option with layout engine
                option.setHeight(calcOptionHeight(option));
                yOffset = contextMenu.getLayoutEngine().layoutOption(option, groupX + nestedIndent, yOffset, subWidth);
                option.render(graphics, option.getX(), option.getY(), mouseX, mouseY);
            }

            group.setHeight(yOffset - groupY);
        } else {
            group.setHeight(20);
        }
    }

    private void drawScrollbar(GuiGraphics graphics) {
        if (getMaxScrollOffset() > 0) {
            int scrollbarX = contextMenuX + width + 5;
            int scrollbarY = contextMenuY + 19;
            int handleHeight = (int) ((float) (height - 23) * ((height - 23) / (float) contextMenu.getHeight()));
            int handleY = scrollbarY + (int) ((float) ((height - 23) - handleHeight) * ((float) scrollHandler.getScrollOffset() / getMaxScrollOffset()));

            DrawHelper.drawRoundedRectangle(graphics, scrollbarX, scrollbarY, 2, height - 23, 1, DARKER_GRAY.getRGB());
            DrawHelper.drawRoundedRectangle(graphics, scrollbarX, handleY, 2, handleHeight, 1, Color.LIGHT_GRAY.getRGB());
        }
    }

    @Override
    public void renderContextMenu(GuiGraphics graphics, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        SCALE_FACTOR = mc.getWindow().calculateScale(0, mc.isEnforceUnicode());
        this.contextMenu = contextMenu;

        mouseX = (int) (mc.mouseHandler.xpos() / SCALE_FACTOR);
        mouseY = (int) (mc.mouseHandler.ypos() / SCALE_FACTOR);

        DrawHelper.scaledProjection(SCALE_FACTOR, graphics);

        updateContextDimensions();
        contextMenu.set(contextMenuX, contextMenuY, 0);

        // Background
        DrawHelper.drawRoundedRectangle(graphics,
                contextMenuX, contextMenuY, width, height, radius, DARKER_GRAY.getRGB());

        drawBackButton(graphics, mouseX, mouseY);

        int optionStartX = contextMenu.x + (int) (width * 0.2f) + 10;
        int targetWidth = (int) (width * 0.8f - 18);

        // Background behind the options scroll area
        DrawHelper.drawRoundedRectangle(graphics,
                optionStartX, contextMenuY + 19, width * 0.8f - 14, height - 23, radius, DARK_GRAY.getRGB());

        enableSkinScissor(graphics);

        int yOffset = contextMenu.y + 19 + 3 - scrollHandler.getScrollOffset();

        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (option.isMouseOver(mouseX, mouseY)) {
                setTooltipText(option.name, option.description);
            }

            option.setHeight(calcOptionHeight(option));
            int nextY = contextMenu.getLayoutEngine().layoutOption(option, optionStartX + 2, yOffset, targetWidth);

            if (option instanceof OptionGroup group) {
                this.renderGroup(graphics, group, optionStartX + 2, yOffset, targetWidth, mouseX, mouseY);
                yOffset += group.getHeight() + contextMenu.getLayoutEngine().getItemSpacing();
            } else {
                option.render(graphics, option.getX(), option.getY(), mouseX, mouseY);
                yOffset = nextY;
            }
        }

        DrawHelper.disableScissor(graphics);

        contextMenu.setWidth(width);
        contextMenu.setHeight(yOffset - (contextMenu.y + 19 + 3 - scrollHandler.getScrollOffset()) + 4);

        scrollHandler.updateScrollOffset(getMaxScrollOffset());

        drawScrollbar(graphics);

        renderToolTipText(graphics, mouseX, mouseY);

        DrawHelper.stopScaling(graphics.pose());
    }

    private void updateContextDimensions() {
        scaledWidth = (float) mc.getWindow().getWidth() / SCALE_FACTOR;
        scaledHeight = (float) mc.getWindow().getHeight() / SCALE_FACTOR;
        contextMenuX = (int) (scaledWidth * 0.1f);
        contextMenuY = (int) (scaledHeight * 0.1f);
        width = (int) (scaledWidth * 0.8f);
        height = (int) (scaledHeight * 0.8f);
    }

    public void drawBackButton(GuiGraphics graphics, int mouseX, int mouseY) {
        String backText = "< Back";
        int textWidth = mc.font.width(backText);

        boolean isHoveringOver = isMouseOver(mouseX, mouseY, contextMenuX + 2, contextMenuY + 2, textWidth + 8, 14);
        int color = isHoveringOver ? getThemeColor().darker().getRGB() : getThemeColor().getRGB();

        DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                contextMenuX + 2, contextMenuY + 2, textWidth + 8, 14, radius, color, 125, 2, 2);

        graphics.drawString(mc.font, backText, contextMenuX + 6, contextMenuY + 5, -1, true);
    }

    public void renderToolTipText(GuiGraphics graphics, int mouseX, int mouseY) {
        int tooltipY = contextMenuY + 19;
        int toolTipWidth = (int) (width * 0.2f) + 4;
        int toolTipHeight = (int) (height * 0.16f);

        if (!TOOLTIP_TEXT.getString().isEmpty()) {
            toolTipHeight = Math.max(toolTipHeight, mc.font.wordWrapHeight(TOOLTIP_TEXT, toolTipWidth)) + 18;
            toolTipHeight = Math.min(height - 23, toolTipHeight);
        }

        float textScale = 0.8f;

        DrawHelper.drawRoundedRectangle(
                graphics,
                contextMenuX + 2,
                tooltipY,
                toolTipWidth,
                toolTipHeight,
                radius,
                DARK_GRAY.getRGB()
        );
        DrawHelper.drawHorizontalLine(
                graphics,
                contextMenuX + 2,
                toolTipWidth,
                tooltipY + 16,
                0.7f,
                ColorHelper.changeAlpha(Color.WHITE, 175).getRGB()
        );

        if (TOOLTIP_TEXT.getString().isEmpty() || TOOLTIP_HEAD.getString().isEmpty()) {
            setTooltipText(defaultToolTipHeader, defaultToolTipText);
            return;
        }

        graphics.drawString(
                mc.font,
                TOOLTIP_HEAD,
                contextMenuX + 4,
                tooltipY + 4,
                -1,
                true
        );

        List<FormattedCharSequence> wrappedText = mc.font.split(TOOLTIP_TEXT, toolTipWidth);

        DrawHelper.scaleAndPosition(graphics.pose(), contextMenuX + 4, tooltipY + 19, textScale);

        int textY = tooltipY + 19;
        for (FormattedCharSequence line : wrappedText) {
            graphics.drawString(
                    mc.font,
                    line,
                    contextMenuX + 2 + 2,
                    textY,
                    -1,
                    false
            );
            textY += mc.font.lineHeight;
        }

        DrawHelper.stopScaling(graphics.pose());

        setTooltipText(defaultToolTipHeader, defaultToolTipText);
    }

    public void setTooltipText(Component head_text, Component tooltip_text) {
        this.TOOLTIP_TEXT = tooltip_text;
        this.TOOLTIP_HEAD = head_text;
    }

    private int getMaxScrollOffset() {
        return contextMenu.getHeight() - height + 23;
    }

    @Override
    public void mouseScrolled(ContextMenu<?> menu, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(menu, mouseX, mouseY, horizontalAmount, verticalAmount);
        scrollHandler.mouseScrolled(verticalAmount);
    }

    @Override
    public boolean mouseReleased(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        scrollHandler.stopDragging();
        return super.mouseReleased(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
        mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + width - 5, contextMenuY, 7, height)) {
            scrollHandler.startDragging(mouseY);
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int optionStartX = contextMenuX + (int) (width * 0.2f) + 10;
            int yOffset = contextMenu.y + 22 - scrollHandler.getScrollOffset();
            int spacing = contextMenu.getLayoutEngine().getItemSpacing();

            for (Option<?> option : getOptions(contextMenu)) {
                if (!option.shouldRender()) continue;

                int optHeight = calcOptionHeight(option);
                if (option instanceof OptionGroup group) {
                    Component groupText = group.name.copy().append(" " + (group.isExpanded() ? "-" : "+"));
                    if (isMouseOver(mouseX, mouseY, optionStartX + 2, yOffset,
                            mc.font.width(groupText) + 6,
                            16)) {
                        group.setExpanded(!group.isExpanded());
                        return true;
                    }
                    yOffset += group.getHeight() + spacing;
                } else {
                    yOffset += optHeight + spacing;
                }
            }
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + 2, contextMenuY + 2, mc.font.width("< Back") + 8, 14)) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.UI_BUTTON_CLICK, 1.0F));
            contextMenu.close();
        }
        return super.mouseClicked(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(ContextMenu<?> menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
        mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + width - 5, contextMenuY, 7, height)) {
            scrollHandler.updateScrollPosition(mouseY);
        }
        return super.mouseDragged(menu, mouseX, mouseY, button, deltaX, deltaY);
    }

    public Color getThemeColor() {
        return contextMenu.getProperties().getAccentColor();
    }

    public class ModernBooleanRenderer implements SkinRenderer<BooleanOption> {
        private long animationStartTime;

        @Override
        public void render(GuiGraphics graphics, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int toggleBgX = x + option.getWidth() - 30;
            boolean active = option.get();
            Color backgroundColor = active ? getThemeColor() : DARKER_GRAY;
            Color hoveredColor = isMouseOver(mouseX, mouseY, toggleBgX, y + 2, 14, 7) ? backgroundColor.darker() : backgroundColor;

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    toggleBgX, y + 2, 14, 7,
                    3,
                    hoveredColor.getRGB(),
                    125, 1, 1
            );

            float startX = active ? toggleBgX + 4 : toggleBgX + 10;
            float endX = active ? toggleBgX + 10 : toggleBgX + 4;
            EasingType easingType = active ? EasingType.EASE_IN_CUBIC : EasingType.EASE_OUT_QUAD;
            float toggleX = MathAnimations.lerp(startX, endX, animationStartTime, 200f, easingType);

            DrawHelper.drawFilledCircle(graphics, toggleX, y + 2 + 3.3f, 2.8f, Color.WHITE.getRGB());

            graphics.drawString(
                    mc.font,
                    option.name,
                    x + 2,
                    y + 4,
                    -1,
                    false
            );
        }

        @Override
        public boolean mouseClicked(BooleanOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

            int toggleBgX = option.getX() + option.getWidth() - 30;

            if (isMouseOver(mouseX, mouseY, toggleBgX, option.getY(), 14, option.getHeight())) {
                option.set(!option.get());
                animationStartTime = System.currentTimeMillis();
                return true;
            }
            return false;
        }
    }

    public class ModernColorOptionRenderer implements SkinRenderer<ColorOption> {
        private static final float ANIMATION_SPEED = 0.1f;
        private float scale = 0f;
        private boolean display = false;

        public void update(ColorOption option) {
            if (option.getColorGradient().shouldDisplay() && display) {
                scale += ANIMATION_SPEED;
            }
            if (!display) {
                scale -= ANIMATION_SPEED;
            }
            scale = Math.clamp(scale, 0, 1.0f);
            if (scale <= 0) {
                option.getColorGradient().close();
            }
        }

        @Override
        public void render(GuiGraphics graphics, ColorOption option, int x, int y, int mouseX, int mouseY) {
            update(option);

            graphics.drawString(
                    mc.font,
                    option.name,
                    x + 2,
                    y + 5,
                    -1,
                    false
            );

            int width = 20;
            int shadowOpacity = Math.min(option.value.getAlpha(), 45);

            Color behindColor = isMouseOver(mouseX, mouseY, x + option.getWidth() - width - 17, y + 1, width + 2, 14) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    x + option.getWidth() - width - 17,
                    y + 1,
                    width + 2,
                    14,
                    2,
                    behindColor.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            graphics.drawString(
                    mc.font,
                    option.getColorGradient().shouldDisplay() ? "^" : "v",
                    x + option.getWidth() - 21,
                    y + 4,
                    -1,
                    false
            );

            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    x + option.getWidth() - width - 15,
                    y + 2,
                    width - 8,
                    12,
                    1,
                    option.value.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            int targetHeight = (int) (option.getColorGradient().getBoxSize() + option.getColorGradient().getGradientBox().getSize() * scale);
            option.setHeight(option.getColorGradient().shouldDisplay() ? targetHeight : 20);

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                DrawHelper.disableScissor(graphics);
            }

            DrawHelper.scaleAndPosition(graphics.pose(), x + option.getWidth() / 2.0f, y, scale);
            option.getColorGradient().render(graphics, x + option.getWidth() / 2 - 50, y + 6, mouseX, mouseY);
            DrawHelper.stopScaling(graphics.pose());

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                enableSkinScissor(graphics);
            }
        }

        @Override
        public boolean mouseClicked(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX() + option.getWidth() - 37, option.getY(), 22, 16)) {
                option.isVisible = !option.isVisible;
                if (option.isVisible) {
                    option.getColorGradient().display();
                    display = true;
                } else {
                    display = false;
                }
                return true;
            }
            return option.getColorGradient().mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(ColorOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            return  option.getColorGradient().mouseDragged(mouseX, mouseY, button) && SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            return option.getColorGradient().mouseReleased(mouseX, mouseY, button) && SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }
    }

    public class ModernDoubleRenderer implements SkinRenderer<DoubleOption> {
        private double displayValue;
        private static final float ANIMATION_SPEED = 0.1f;
        int sliderBackgroundWidth = 120;
        int sliderBackgroundHeight = 2;

        @Override
        public void render(GuiGraphics graphics, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            graphics.drawString(
                    mc.font,
                    option.name,
                    x + 2,
                    y,
                    -1,
                    false
            );
            int sliderX = x + option.getWidth() - sliderBackgroundWidth - 10;

            displayValue = Mth.lerp(ANIMATION_SPEED, displayValue, option.get());

            DrawHelper.drawRoundedRectangle(
                    graphics,
                    sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight, 1, DARKER_GRAY.getRGB()
            );

            int activeFillWidth = (int) ((displayValue - option.minValue) / (option.maxValue - option.minValue) * sliderBackgroundWidth);
            Color fillColor = isMouseOver(mouseX, mouseY, sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    sliderX, y, activeFillWidth, sliderBackgroundHeight, 2, fillColor.getRGB()
            );

            float sliderHandleX = sliderX + activeFillWidth - 5;
            DrawHelper.drawFilledCircle(graphics, sliderHandleX + 5, y + 1, 2, Color.WHITE.getRGB());

            String label = String.format("%.2f", displayValue);
            DrawHelper.scaleAndPosition(graphics.pose(), sliderX + sliderBackgroundWidth - mc.font.width(label), y + 7, 0.6f);
            graphics.drawString(
                    mc.font,
                    label,
                    sliderX + sliderBackgroundWidth + 10 - mc.font.width(label),
                    y + 2,
                    -1,
                    true
            );
            graphics.pose().popMatrix();
        }

        @Override
        public boolean mouseClicked(DoubleOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            int sliderX = option.getX() + option.getWidth() - sliderBackgroundWidth - 10;
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, sliderX - 2, option.getY() - 1, sliderBackgroundWidth + 4, option.getHeight() + 1)) {
                option.setDragging(true);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(DoubleOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (option.isDragging()) {
                mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
                int sliderX = option.getX() + option.getWidth() - sliderBackgroundWidth - 10;
                option.step(mouseX, sliderX, sliderBackgroundWidth);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(DoubleOption option, double mouseX, double mouseY, int button) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                option.setDragging(false);
                return true;
            }
            return false;
        }
    }

    public class ModernEnumRenderer<E extends Enum<E>> implements SkinRenderer<EnumOption<E>> {
        @Override
        public void render(GuiGraphics graphics, EnumOption<E> option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(mc.font.lineHeight + 2);

            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();
            graphics.drawString(mc.font, mainLabel, x + 4, y + 2, -1, false);
            boolean isMouseOverText = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2);
            Color fillColor = isMouseOverText ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2, 2,
                    fillColor.getRGB()
            );

            int leftX = x + option.getWidth() - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);

            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX + 1, y + 3,
                    (mc.font.width("<") * 2) + 10, mc.font.lineHeight, 2,
                    ColorHelper.changeAlpha(Color.BLACK, 128).getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX, y + 2,
                    true, false, true, false,
                    mc.font.width("<") + 5, mc.font.lineHeight, 2,
                    hoveredOverLeft ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX + mc.font.width("<") + 6, y + 2,
                    false, true, false, true,
                    mc.font.width(">") + 5, mc.font.lineHeight, 2,
                    hoveredOverRight ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawVerticalLine(
                    graphics,
                    leftX + mc.font.width("<") + 5,
                    y + 2,
                    mc.font.lineHeight,
                    0.7f,
                    Color.WHITE.getRGB()
            );
            graphics.drawString(mc.font, "<", leftX + mc.font.width("<") / 2 + 1, y + 3, -1, false);
            graphics.drawString(mc.font, ">", leftX + mc.font.width("<") + 7 + mc.font.width(">") / 2, y + 3, -1, false);

            graphics.drawString(mc.font, selectedOption, x + 6 + mc.font.width(mainLabel), y + 2, Color.LIGHT_GRAY.getRGB(), isMouseOverText);
        }

        @Override
        public boolean mouseClicked(EnumOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().length == 0) return false;

            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();

            int leftX = x + option.getWidth() - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);
            boolean hoveredOverMainLabel = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2);

            if (hoveredOverLeft || hoveredOverRight || hoveredOverMainLabel) {
                E[] values = option.getValues();
                int index = Arrays.asList(values).indexOf(option.value);

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || hoveredOverLeft) {
                    E nextVal = values[(index + 1) % values.length];
                    option.set(nextVal);
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || hoveredOverRight) {
                    E nextVal = values[(index - 1 + values.length) % values.length];
                    option.set(nextVal);
                } else {
                    return false;
                }
                return true;
            }

            return false;
        }
    }

    public class ModernListRenderer<E> implements SkinRenderer<ListOption<E>> {
        @Override
        public void render(GuiGraphics graphics, ListOption<E> option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(mc.font.lineHeight + 2);

            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();
            graphics.drawString(mc.font, mainLabel, x + 4, y + 2, -1, false);
            boolean isMouseOverText = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2);
            Color fillColor = isMouseOverText ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2, 2,
                    fillColor.getRGB()
            );

            int leftX = x + option.getWidth() - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);

            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX + 1, y + 3,
                    (mc.font.width("<") * 2) + 10, mc.font.lineHeight, 2,
                    ColorHelper.changeAlpha(Color.BLACK, 128).getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX, y + 2,
                    true, false, true, false,
                    mc.font.width("<") + 5, mc.font.lineHeight, 2,
                    hoveredOverLeft ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX + mc.font.width("<") + 6, y + 2,
                    false, true, false, true,
                    mc.font.width(">") + 5, mc.font.lineHeight, 2,
                    hoveredOverRight ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawVerticalLine(
                    graphics,
                    leftX + mc.font.width("<") + 5,
                    y + 2,
                    mc.font.lineHeight,
                    0.7f,
                    Color.WHITE.getRGB()
            );
            graphics.drawString(mc.font, "<", leftX + mc.font.width("<") / 2 + 1, y + 3, -1, false);
            graphics.drawString(mc.font, ">", leftX + mc.font.width("<") + 7 + mc.font.width(">") / 2, y + 3, -1, false);

            graphics.drawString(mc.font, selectedOption, x + 6 + mc.font.width(mainLabel), y + 2, Color.LIGHT_GRAY.getRGB(), isMouseOverText);
        }

        @Override
        public boolean mouseClicked(ListOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().isEmpty()) return false;

            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();

            int leftX = x + option.getWidth() - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);
            boolean hoveredOverMainLabel = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2);

            if (hoveredOverLeft || hoveredOverRight || hoveredOverMainLabel) {
                List<E> values = option.getValues();
                int currentIndex = values.indexOf(option.value);
                int nextIndex;

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || hoveredOverLeft) {
                    nextIndex = (currentIndex + 1) % values.size();
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || hoveredOverRight) {
                    nextIndex = (currentIndex - 1 + values.size()) % values.size();
                } else {
                    return false;
                }

                option.set(values.get(nextIndex));
                return true;
            }

            return false;
        }
    }

    public class ModernSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(GuiGraphics graphics, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            String textLabel = "Open";
            int xPos = x + option.getWidth() - 40;

            option.setHeight(16);

            graphics.drawString(mc.font, option.name, x + 4, y + 4, -1, false);
            Color fillColor = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, mc.font.width(textLabel) + 5, mc.font.lineHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    xPos - 1, y + 1,
                    mc.font.width(textLabel) + 5, mc.font.lineHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );
            DrawHelper.drawOutlineRoundedBox(
                    graphics,
                    xPos - 1, y + 1,
                    mc.font.width(textLabel) + 5, mc.font.lineHeight + 4,
                    2,
                    0.7f,
                    Color.WHITE.getRGB()
            );

            graphics.drawString(mc.font, textLabel, xPos + 2, y + 4, Color.WHITE.getRGB(), true);

            option.getSubMenu().render(graphics, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
        }

        @Override
        public void mouseScrolled(SubMenuOption option, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            SkinRenderer.super.mouseScrolled(option, mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean mouseDragged(SubMenuOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(SubMenuOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            return isMouseOver(mouseX, mouseY, option.getX() + option.getWidth() - 40 + 2, option.getY() + 4, mc.font.width("Open") + 5, mc.font.lineHeight + 4);
        }

        @Override
        public boolean mouseClicked(SubMenuOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            if(isMouseOver(mouseX, mouseY, option.getX() + option.getWidth() - 40 + 2, option.getY() + 4, mc.font.width("Open") + 5, mc.font.lineHeight + 4)){
                option.toggle();
                return true;
            }
            return false;
        }
    }

    public class ModernRunnableRenderer implements SkinRenderer<RunnableOption> {
        @Override
        public void render(GuiGraphics graphics, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            String labelText = "Run ▶";
            int xPos = x + option.getWidth() - 45;

            option.setHeight(mc.font.lineHeight + 6);

            graphics.drawString(mc.font, option.name, x + 4, y + 4, -1, false);

            Color fillColor = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, mc.font.width(labelText) + 5, mc.font.lineHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    xPos - 1, y + 1,
                    mc.font.width(labelText) + 5, mc.font.lineHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );

            graphics.drawString(mc.font, labelText, xPos + 2, y + 4, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);
        }

        @Override
        public boolean mouseClicked(RunnableOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            if(isMouseOver(mouseX, mouseY, option.getX() + option.getWidth() - 45 + 2, option.getY() + 4, mc.font.width("Run ▶") + 5, mc.font.lineHeight + 4)){
                option.toggle();
                return true;
            }
            return false;
        }
    }

    @Override
    public Skin clone() {
        return new ModernSkin(radius, defaultToolTipHeader, defaultToolTipText);
    }
}