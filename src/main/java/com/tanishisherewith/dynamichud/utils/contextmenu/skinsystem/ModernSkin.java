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

public class ModernSkin extends Skin implements GroupableSkin {
    static Color DARK_GRAY = new Color(20, 20, 20, 229);
    static Color DARKER_GRAY = new Color(10, 10, 10, 243);
    static Color DARKER_GRAY_2 = new Color(12, 12, 12, 246);

    private final Color themeColor;
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


    public ModernSkin(Color themeColor, float radius, Component defaultToolTipHeader, Component defaultToolTipText) {
        this.themeColor = themeColor;
        this.radius = radius;
        TOOLTIP_TEXT = defaultToolTipText;
        TOOLTIP_HEAD = defaultToolTipHeader;
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

    public ModernSkin(Color themeColor, float radius) {
        this(themeColor, radius, Component.literal("Example Tip"), Component.literal("Hover over a setting to see its tool-tip (if present) here!"));
    }

    public ModernSkin(Color themeColor) {
        this(themeColor, 4);
    }

    public ModernSkin() {
        this(Color.CYAN.darker().darker());
    }

    @Override
    public LayoutContext.Offset getGroupIndent() {
        return new LayoutContext.Offset(2, 2);
    }

    public void enableSkinScissor(GuiGraphics graphics) {
        DrawHelper.enableScissor(contextMenuX + (int) (width * 0.2f) + 10, contextMenuY + 19, (int) (width * 0.8f - 14), height - 23, SCALE_FACTOR, graphics);
    }

    @Override
    public void renderGroup(GuiGraphics graphics, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY) {
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
            for (Option<?> option : group.getGroupOptions()) {
                if (!option.shouldRender()) continue;

                option.render(graphics, groupX + getGroupIndent().left(), yOffset, mouseX, mouseY);
                yOffset += option.getHeight() + 1;
            }

            group.setHeight(yOffset - groupY);
        } else {
            group.setHeight(20);
        }
    }

    private void drawScrollbar(GuiGraphics graphics) {
        if (getMaxScrollOffset() > 0) {
            int scrollbarX = contextMenuX + width + 5; // Position at the right of the panel
            int scrollbarY = contextMenuY + 19; // Position below the header
            int handleHeight = (int) ((float) (height - 23) * ((height - 23) / (float) contextMenu.getHeight()));
            int handleY = scrollbarY + (int) ((float) ((height - 23) - handleHeight) * ((float) scrollHandler.getScrollOffset() / getMaxScrollOffset()));

            DrawHelper.drawRoundedRectangle(graphics, scrollbarX, scrollbarY, 2, height - 23, 1, DARKER_GRAY.getRGB());
            DrawHelper.drawRoundedRectangle(graphics, scrollbarX, handleY, 2, handleHeight, 1, Color.LIGHT_GRAY.getRGB());
        }
    }

    @Override
    public void renderContextMenu(GuiGraphics graphics, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        //This is equivalent to "Auto" GUI scale in minecraft options
        SCALE_FACTOR = mc.getWindow().calculateScale(0, mc.isEnforceUnicode());

        mouseX = (int) (mc.mouseHandler.xpos() / SCALE_FACTOR);
        mouseY = (int) (mc.mouseHandler.ypos() / SCALE_FACTOR);

        // Apply custom scaling to counteract Minecraft's default scaling
        DrawHelper.customScaledProjection(SCALE_FACTOR);

        updateContextDimensions();
        contextMenu.set(contextMenuX, contextMenuY, 0);

        //Background
        DrawHelper.drawRoundedRectangle(graphics,
                contextMenuX, contextMenuY, width, height, radius, DARKER_GRAY.getRGB());

        drawBackButton(graphics, mouseX, mouseY);

        //OptionStartX = Tool-Tip width + padding
        int optionStartX = contextMenu.x + (int) (width * 0.2f) + 10;

        //Background behind the options
        DrawHelper.drawRoundedRectangle(graphics,
                optionStartX, contextMenuY + 19, width * 0.8f - 14, height - 23, radius, DARK_GRAY.getRGB());

        enableSkinScissor(graphics);
        int yOffset = contextMenu.y + 19 + 3 - scrollHandler.getScrollOffset();
        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (option.isMouseOver(mouseX, mouseY)) {
                setTooltipText(option.name, option.description);
            }

            if (option instanceof OptionGroup group) {
                this.renderGroup(graphics, group, optionStartX + 2, yOffset, mouseX, mouseY);
            } else {
                option.render(graphics, optionStartX + 2, yOffset, mouseX, mouseY);
            }

            yOffset += option.getHeight() + 1;
        }
        DrawHelper.disableScissor(graphics);

        contextMenu.setWidth(width);
        contextMenu.setHeight(yOffset - (contextMenu.y + 19 + 3 - scrollHandler.getScrollOffset()) + 4);

        scrollHandler.updateScrollOffset(getMaxScrollOffset());

        drawScrollbar(graphics);

        renderToolTipText(graphics, mouseX, mouseY);

        //Reset our scaling so minecraft runs normally\
        DrawHelper.scaledProjection();
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
        int color = isHoveringOver ? themeColor.darker().getRGB() : themeColor.getRGB();

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

        // Draw background
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

        //Draw the head Component
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

        // Draw Component
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
        TOOLTIP_TEXT = tooltip_text;
        TOOLTIP_HEAD = head_text;
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
            for (Option<?> option : getOptions(contextMenu)) {
                if (!option.shouldRender()) continue;

                if (option instanceof OptionGroup group) {
                    if (isMouseOver(mouseX, mouseY, optionStartX + 2, yOffset,
                            mc.font.width(group.name + " " + (group.isExpanded() ? "-" : "+")) + 6,
                            16)) {
                        group.setExpanded(!group.isExpanded());
                        break;
                    }
                }

                yOffset += option.getHeight() + 1;
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
        return themeColor;
    }

    public class ModernBooleanRenderer implements SkinRenderer<BooleanOption> {
        private long animationStartTime;

        @Override
        public void render(GuiGraphics graphics, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int backgroundWidth = (int) (width * 0.8f - 14);

            option.setHeight(14);
            option.setPosition(x, y);
            option.setWidth(backgroundWidth);

            // Calculate the current progress of the animation
            int toggleBgX = x + backgroundWidth - 30;
            // Background
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

            // Draw toggle circle
            float startX = active ? toggleBgX + 4 : toggleBgX + 10;
            float endX = active ? toggleBgX + 10 : toggleBgX + 4;
            EasingType easingType = active ? EasingType.EASE_IN_CUBIC : EasingType.EASE_OUT_QUAD;
            float toggleX = MathAnimations.lerp(startX, endX, animationStartTime, 200f, easingType);

            DrawHelper.drawFilledCircle(graphics, toggleX, y + 2 + 3.3f, 2.8f, Color.WHITE.getRGB());

            // Draw option name
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

            int backgroundWidth = (int) (width * 0.8f - 14);
            int toggleBgX = option.getX() + backgroundWidth - 30;

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

            int backgroundWidth = (int) (width * 0.8f - 14);

            // Draw option name
            graphics.drawString(
                    mc.font,
                    option.name,
                    x + 2,
                    y + 5,
                    -1,
                    false
            );

            option.setWidth(20);
            option.setPosition(x, y);


            int width = 20;
            int shadowOpacity = Math.min(option.value.getAlpha(), 45);

            //The shape behind the preview
            Color behindColor = isMouseOver(mouseX, mouseY, x + backgroundWidth - width - 17, y + 1, width + 2, 14) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    x + backgroundWidth - width - 17,
                    y + 1,
                    width + 2,
                    14,
                    2,
                    behindColor.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            //The letter above the shape behind the preview
            graphics.drawString(
                    mc.font,
                    option.getColorGradient().shouldDisplay() ? "^" : "v",
                    x + backgroundWidth - 21,
                    y + 4,
                    -1,
                    false
            );

            //Preview
            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics,
                    x + backgroundWidth - width - 15,
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
            option.setWidth(width);

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                DrawHelper.disableScissor(graphics); //Disable scissor so the color picker preview works
            }

            DrawHelper.scaleAndPosition(graphics.pose(), x + backgroundWidth / 2.0f, y, scale);
            option.getColorGradient().render(graphics, x + backgroundWidth / 2 - 50, y + 6, mouseX, mouseY);
            DrawHelper.stopScaling(graphics.pose());

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                enableSkinScissor(graphics); // re-enable the scissor
            }
        }

        @Override
        public boolean mouseClicked(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX() + (int) (width * 0.8f - 14) - 37, option.getY(), 22, 16)) {
                option.isVisible = !option.isVisible;
                if (option.isVisible) {
                    option.getColorGradient().display();
                    display = true;
                } else {
                    display = false;
                }
                return true;
            }
            option.getColorGradient().mouseClicked(mouseX, mouseY, button);
            return false;
        }

        @Override
        public boolean mouseDragged(ColorOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            option.getColorGradient().mouseDragged(mouseX, mouseY, button);
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            option.getColorGradient().mouseReleased(mouseX, mouseY, button);

            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }
    }

    public class ModernDoubleRenderer implements SkinRenderer<DoubleOption> {
        private double displayValue;
        private static final float ANIMATION_SPEED = 0.1f;

        @Override
        public void render(GuiGraphics graphics, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            // Draw option name
            graphics.drawString(
                    mc.font,
                    option.name,
                    x + 2,
                    y,
                    -1,
                    false
            );

            int backgroundWidth = (int) (width * 0.8f - 14);
            int sliderBackgroundWidth = 120;
            int sliderBackgroundHeight = 2;
            int sliderX = x + backgroundWidth - sliderBackgroundWidth - 10;

            option.setPosition(x, y);
            option.setWidth(sliderBackgroundWidth);
            option.setHeight(14);

            // Smoothly interpolate to the new value
            displayValue = Mth.lerp(ANIMATION_SPEED, displayValue, option.get());

            // Background
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight, 1, DARKER_GRAY.getRGB()
            );

            // Active fill
            int activeFillWidth = (int) ((displayValue - option.minValue) / (option.maxValue - option.minValue) * option.getWidth());
            Color fillColor = isMouseOver(mouseX, mouseY, sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    sliderX, y, activeFillWidth, sliderBackgroundHeight, 2, fillColor.getRGB()
            );

            // Draw slider handle
            float sliderHandleX = sliderX + activeFillWidth - 5;
            DrawHelper.drawFilledCircle(graphics, sliderHandleX + 5, y + 1, 2, Color.WHITE.getRGB());

            // Draw value Component
            String Component = String.format("%.2f", displayValue);
            DrawHelper.scaleAndPosition(graphics.pose(), sliderX + 120 - mc.font.width(Component), y + 7, 0.6f);
            graphics.drawString(
                    mc.font,
                    Component,
                    sliderX + sliderBackgroundWidth + 10 - mc.font.width(Component),
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
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX() + (int) (width * 0.8f - 14) - 125, option.getY() - 1, option.getWidth() + 2, option.getHeight() + 1)) {
                option.setDragging(true);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(DoubleOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (option.isDragging()) {
                mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
                int backgroundWidth = (int) (width * 0.8f - 14);
                int sliderBackgroundWidth = 120;
                int sliderX = option.getX() + backgroundWidth - sliderBackgroundWidth - 10;
                option.step(mouseX, sliderX);
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
            // Set dimensions for the main label and dropdown area
            option.setHeight(mc.font.lineHeight + 2);

            // Draw main option name and selected option
            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();
            graphics.drawString(mc.font, mainLabel, x + 4, y + 2, -1, false);
            Color fillColor = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2, 2,
                    fillColor.getRGB()
            );
            // "<" and ">" buttons
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);
            // Shadow
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

            graphics.drawString(mc.font, selectedOption, x + 6 + mc.font.width(mainLabel), y + 2, Color.LIGHT_GRAY.getRGB(), false);
        }

        @Override
        public boolean mouseClicked(EnumOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().length == 0) return false;

            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            String mainLabel = option.name + ": ";
            String selectedOption = option.get().toString();

            // Check if the main label is clicked to cycle
            // "<" and ">" buttons
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);
            boolean hoveredOverMainLabel = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2);

            if (hoveredOverLeft || hoveredOverRight || hoveredOverMainLabel) {
                E[] values = option.getValues();
                int index = Arrays.asList(values).indexOf(option.value);

                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || hoveredOverLeft) {
                    // Cycle forward
                    E nextVal = values[(index + 1) % values.length];
                    option.set(nextVal);
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || hoveredOverRight) {
                    // Cycle backward with wrap-around
                    E nextVal = values[(index - 1 + values.length) % values.length];
                    option.set(nextVal);
                }
                return true;
            }

            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }

    }

    public class ModernListRenderer<E> implements SkinRenderer<ListOption<E>> {

        @Override
        public void render(GuiGraphics graphics, ListOption<E> option, int x, int y, int mouseX, int mouseY) {

            // Set dimensions for the main label and dropdown area
            option.setHeight(mc.font.lineHeight + 2);

            // Draw main option name and selected option
            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();
            graphics.drawString(mc.font, mainLabel, x + 4, y + 2, -1, false);
            Color fillColor = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2, 2,
                    fillColor.getRGB()
            );

            // "<" and ">" buttons
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);
            // Shadow
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

            graphics.drawString(mc.font, selectedOption, x + 6 + mc.font.width(mainLabel), y + 2, Color.LIGHT_GRAY.getRGB(), false);
        }

        @Override
        public boolean mouseClicked(ListOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().isEmpty()) return false;

            mouseX /= SCALE_FACTOR;
            mouseY /= SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();

            // Calculate positions
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;

            // Check hover states
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.font.width("<") + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.font.width("<") + 6, y, mc.font.width(">") + 5, mc.font.lineHeight);
            boolean hoveredOverMainLabel = isMouseOver(mouseX, mouseY, x + 4 + mc.font.width(mainLabel), y, mc.font.width(selectedOption) + 5, mc.font.lineHeight + 2);

            // Check if any area is clicked
            if (hoveredOverLeft || hoveredOverRight || hoveredOverMainLabel) {
                List<E> values = option.getValues();
                int currentIndex = values.indexOf(option.value);
                int nextIndex;

                // Determine the next index based on the button clicked
                if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || hoveredOverLeft) {
                    nextIndex = (currentIndex + 1) % values.size(); // Cycle forward
                } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT || hoveredOverRight) {
                    nextIndex = (currentIndex - 1 + values.size()) % values.size(); // Cycle backward
                } else {
                    return false; // No valid click
                }

                option.set(values.get(nextIndex));
                return true;
            }

            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }

    public class ModernSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(GuiGraphics graphics, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            mouseX = (int) (mc.mouseHandler.xpos() / SCALE_FACTOR);
            mouseY = (int) (mc.mouseHandler.ypos() / SCALE_FACTOR);

            String Component = "Open";
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int xPos = x + 4 + contextMenuWidth - 40;

            option.setPosition(xPos - 1, y);
            option.setWidth(mc.font.width(Component) + 5);
            option.setHeight(16);

            graphics.drawString(mc.font, option.name, x + 4, y + 4, -1, false);

            graphics.drawString(mc.font, Component, xPos + 2, y + 4, Color.WHITE.getRGB(), true);

            Color fillColor = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, mc.font.width(Component) + 5, mc.font.lineHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    xPos - 1, y + 1,
                    mc.font.width(Component) + 5, mc.font.lineHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );
            DrawHelper.drawOutlineRoundedBox(
                    graphics,
                    xPos - 1, y + 1,
                    mc.font.width(Component) + 5, mc.font.lineHeight + 4,
                    2,
                    0.7f,
                    Color.WHITE.getRGB()
            );

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
            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }

        @Override
        public boolean mouseClicked(SubMenuOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }

    public class ModernRunnableRenderer implements SkinRenderer<RunnableOption> {
        Color DARK_RED = new Color(116, 0, 0);
        Color DARK_GREEN = new Color(24, 132, 0, 226);

        @Override
        public void render(GuiGraphics graphics, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            String Component = "Run ▶";
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int xPos = x + 4 + contextMenuWidth - 45;

            option.setPosition(xPos - 1, y);
            option.setWidth(mc.font.width(Component) + 5);
            option.setHeight(mc.font.lineHeight + 6);

            graphics.drawString(mc.font, option.name, x + 4, y + 4, -1, false);

            graphics.drawString(mc.font, Component, xPos + 2, y + 4, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);

            Color fillColor = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, mc.font.width(Component) + 5, mc.font.lineHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    xPos - 1, y + 1,
                    mc.font.width(Component) + 5, mc.font.lineHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );
        }

        @Override
        public boolean mouseClicked(RunnableOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }
    @Override
    public Skin clone() {
        return new ModernSkin(themeColor,radius,defaultToolTipHeader,defaultToolTipText);
    }
}