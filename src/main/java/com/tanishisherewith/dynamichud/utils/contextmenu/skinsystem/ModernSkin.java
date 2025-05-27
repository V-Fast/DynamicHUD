package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

//TODO: Complete this
public class ModernSkin extends Skin implements GroupableSkin {
    private final Color themeColor;
    private final float radius;
    private final Text defaultToolTipHeader;
    private final Text defaultToolTipText;
    Color DARK_GRAY = new Color(20, 20, 20, 229);
    Color DARKER_GRAY_2 = new Color(12, 12, 12, 246);
    Color DARKER_GRAY = new Color(10, 10, 10, 243);
    private int contextMenuX = 0, contextMenuY = 0;
    private int width = 0, height = 0;
    private float scaledWidth = 0, scaledHeight = 0;
    private Text TOOLTIP_TEXT;
    private Text TOOLTIP_HEAD;
    private static int SCALE_FACTOR = 4;
    private final ScrollHandler scrollHandler;


    public ModernSkin(Color themeColor, float radius, Text defaultToolTipHeader, Text defaultToolTipText) {
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
        this(themeColor, radius, Text.of("Example Tip"), Text.of("Hover over a setting to see its tool-tip (if present) here!"));
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

    public void enableSkinScissor() {
        DrawHelper.enableScissor(contextMenuX + (int) (width * 0.2f) + 10, contextMenuY + 19, (int) (width * 0.8f - 14), height - 23, SCALE_FACTOR);
    }

    @Override
    public void renderGroup(DrawContext drawContext, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY) {
        mouseX = (int) (mc.mouse.getX() / SCALE_FACTOR);
        mouseY = (int) (mc.mouse.getY() / SCALE_FACTOR);

        if (group.isExpanded() && group.getHeight() > 20) {
            DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                    groupX + 1, groupY + 14, width - groupX - 8 + contextMenuX, group.getHeight() - 16, radius, DARKER_GRAY_2.getRGB());
        }

        String groupText = group.name + " " + (group.isExpanded() ? "-" : "+");

        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                groupX + 1, groupY + 1, true, true, !group.isExpanded(), !group.isExpanded(), mc.textRenderer.getWidth(groupText) + 6, 16, radius, DARKER_GRAY_2.getRGB());

        drawContext.drawText(mc.textRenderer, groupText, groupX + 4, groupY + 4, -1, true);

        if (group.isExpanded()) {
            int yOffset = groupY + 16 + getGroupIndent().top;
            for (Option<?> option : group.getGroupOptions()) {
                if (!option.shouldRender()) continue;

                option.render(drawContext, groupX + getGroupIndent().left, yOffset, mouseX, mouseY);
                yOffset += option.getHeight() + 1;
            }

            group.setHeight(yOffset - groupY);
        } else {
            group.setHeight(20);
        }
    }

    private void drawScrollbar(DrawContext drawContext) {
        if (getMaxScrollOffset() > 0) {
            int scrollbarX = contextMenuX + width + 5; // Position at the right of the panel
            int scrollbarY = contextMenuY + 19; // Position below the header
            int handleHeight = (int) ((float) (height - 23) * ((height - 23) / (float) contextMenu.getHeight()));
            int handleY = scrollbarY + (int) ((float) ((height - 23) - handleHeight) * ((float) scrollHandler.getScrollOffset() / getMaxScrollOffset()));

            DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(), scrollbarX, scrollbarY, 2, height - 23, 1, DARKER_GRAY.getRGB());
            DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(), scrollbarX, handleY, 2, handleHeight, 1, Color.LIGHT_GRAY.getRGB());
        }
    }

    @Override
    public void renderContextMenu(DrawContext drawContext, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        //This is equivalent to "Auto" GUI scale in minecraft options
        SCALE_FACTOR = mc.getWindow().calculateScaleFactor(0, mc.forcesUnicodeFont());

        mouseX = (int) (mc.mouse.getX() / SCALE_FACTOR);
        mouseY = (int) (mc.mouse.getY() / SCALE_FACTOR);

        // Apply custom scaling to counteract Minecraft's default scaling
        DrawHelper.customScaledProjection(SCALE_FACTOR);

        updateContextDimensions();
        contextMenu.set(contextMenuX, contextMenuY, 0);

        //Background
        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                contextMenuX, contextMenuY, width, height, radius, DARKER_GRAY.getRGB());

        drawBackButton(drawContext, mouseX, mouseY);

        //OptionStartX = Tool-Tip width + padding
        int optionStartX = contextMenu.x + (int) (width * 0.2f) + 10;

        //Background behind the options
        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                optionStartX, contextMenuY + 19, width * 0.8f - 14, height - 23, radius, DARK_GRAY.getRGB());

        enableSkinScissor();
        int yOffset = contextMenu.y + 19 + 3 - scrollHandler.getScrollOffset();
        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (option.isMouseOver(mouseX, mouseY)) {
                setTooltipText(option.name, option.description);
            }

            if (option instanceof OptionGroup group) {
                this.renderGroup(drawContext, group, optionStartX + 2, yOffset, mouseX, mouseY);
            } else {
                option.render(drawContext, optionStartX + 2, yOffset, mouseX, mouseY);
            }

            yOffset += option.getHeight() + 1;
        }
        RenderSystem.disableScissor();

        contextMenu.setWidth(width);
        contextMenu.setHeight(yOffset - (contextMenu.y + 19 + 3 - scrollHandler.getScrollOffset()) + 4);

        scrollHandler.updateScrollOffset(getMaxScrollOffset());

        drawScrollbar(drawContext);

        renderToolTipText(drawContext, mouseX, mouseY);

        //Reset our scaling so minecraft runs normally
        DrawHelper.scaledProjection();
    }

    private void updateContextDimensions() {
        scaledWidth = (float) mc.getWindow().getFramebufferWidth() / SCALE_FACTOR;
        scaledHeight = (float) mc.getWindow().getFramebufferHeight() / SCALE_FACTOR;
        contextMenuX = (int) (scaledWidth * 0.1f);
        contextMenuY = (int) (scaledHeight * 0.1f);
        width = (int) (scaledWidth * 0.8f);
        height = (int) (scaledHeight * 0.8f);
    }

    public void drawBackButton(DrawContext drawContext, int mouseX, int mouseY) {
        String backText = "< Back";
        int textWidth = mc.textRenderer.getWidth(backText);

        boolean isHoveringOver = isMouseOver(mouseX, mouseY, contextMenuX + 2, contextMenuY + 2, textWidth + 8, 14);
        int color = isHoveringOver ? themeColor.darker().getRGB() : themeColor.getRGB();

        DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                contextMenuX + 2, contextMenuY + 2, textWidth + 8, 14, radius, color, 125, 2, 2);

        drawContext.drawText(mc.textRenderer, backText, contextMenuX + 6, contextMenuY + 5, -1, true);
    }

    public void renderToolTipText(DrawContext drawContext, int mouseX, int mouseY) {
        int tooltipY = contextMenuY + 19;
        int toolTipWidth = (int) (width * 0.2f) + 4;
        int toolTipHeight = (int) (height * 0.16f);

        if (!TOOLTIP_TEXT.getString().isEmpty()) {
            toolTipHeight = Math.max(toolTipHeight, mc.textRenderer.getWrappedLinesHeight(TOOLTIP_TEXT, toolTipWidth)) + 18;
            toolTipHeight = Math.min(height - 23, toolTipHeight);
        }

        float textScale = 0.8f;

        // Draw background
        DrawHelper.drawRoundedRectangle(
                drawContext.getMatrices().peek().getPositionMatrix(),
                contextMenuX + 2,
                tooltipY,
                toolTipWidth,
                toolTipHeight,
                radius,
                DARK_GRAY.getRGB()
        );
        DrawHelper.drawHorizontalLine(
                drawContext.getMatrices().peek().getPositionMatrix(),
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

        //Draw the head text
        drawContext.drawText(
                mc.textRenderer,
                TOOLTIP_HEAD,
                contextMenuX + 4,
                tooltipY + 4,
                -1,
                true
        );

        List<OrderedText> wrappedText = mc.textRenderer.wrapLines(StringVisitable.styled(TOOLTIP_TEXT.getString(), TOOLTIP_TEXT.getStyle()), toolTipWidth);

        DrawHelper.scaleAndPosition(drawContext.getMatrices(), contextMenuX + 4, tooltipY + 19, textScale);

        // Draw text
        int textY = tooltipY + 19;
        for (OrderedText line : wrappedText) {
            drawContext.drawText(
                    mc.textRenderer,
                    line,
                    contextMenuX + 2 + 2,
                    textY,
                    -1,
                    false
            );
            textY += mc.textRenderer.fontHeight;
        }

        DrawHelper.stopScaling(drawContext.getMatrices());

        setTooltipText(defaultToolTipHeader, defaultToolTipText);
    }

    public void setTooltipText(Text head_text, Text tooltip_text) {
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
        mouseX = mc.mouse.getX() / SCALE_FACTOR;
        mouseY = mc.mouse.getY() / SCALE_FACTOR;

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
                            mc.textRenderer.getWidth(group.name + " " + (group.isExpanded() ? "-" : "+")) + 6,
                            16)) {
                        group.setExpanded(!group.isExpanded());
                        break;
                    }
                }

                yOffset += option.getHeight() + 1;
            }
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + 2, contextMenuY + 2, mc.textRenderer.getWidth("< Back") + 8, 14)) {
            mc.getSoundManager().play(PositionedSoundInstance.master(
                    SoundEvents.UI_BUTTON_CLICK, 1.0F));
            contextMenu.close();
        }
        return super.mouseClicked(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(ContextMenu<?> menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        mouseX = mc.mouse.getX() / SCALE_FACTOR;
        mouseY = mc.mouse.getY() / SCALE_FACTOR;

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
        public void render(DrawContext drawContext, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int backgroundWidth = (int) (width * 0.8f - 14);

            option.setHeight(14);
            option.setPosition(x, y);
            option.setWidth(backgroundWidth);

            MatrixStack matrices = drawContext.getMatrices();

            // Calculate the current progress of the animation
            int toggleBgX = x + backgroundWidth - 30;
            // Background
            boolean active = option.get();
            Color backgroundColor = active ? getThemeColor() : DARKER_GRAY;
            Color hoveredColor = isMouseOver(mouseX, mouseY, toggleBgX, y + 2, 14, 7) ? backgroundColor.darker() : backgroundColor;

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    matrices.peek().getPositionMatrix(),
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

            DrawHelper.drawFilledCircle(matrices.peek().getPositionMatrix(), toggleX, y + 2 + 3.3f, 2.8f, Color.WHITE.getRGB());

            // Draw option name
            drawContext.drawText(
                    mc.textRenderer,
                    option.name,
                    x + 2,
                    y + 4,
                    -1,
                    false
            );
        }

        @Override
        public boolean mouseClicked(BooleanOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;

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
            scale = MathHelper.clamp(scale, 0, 1.0f);
            if (scale <= 0) {
                option.getColorGradient().close();
            }
        }

        @Override
        public void render(DrawContext drawContext, ColorOption option, int x, int y, int mouseX, int mouseY) {
            update(option);

            int backgroundWidth = (int) (width * 0.8f - 14);

            // Draw option name
            drawContext.drawText(
                    mc.textRenderer,
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
            DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
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
            drawContext.drawText(
                    mc.textRenderer,
                    option.getColorGradient().shouldDisplay() ? "^" : "v",
                    x + backgroundWidth - 21,
                    y + 4,
                    -1,
                    false
            );

            //Preview
            DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
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
                RenderSystem.disableScissor(); //Disable scissor so the color picker preview works
            }

            DrawHelper.scaleAndPosition(drawContext.getMatrices(), x + backgroundWidth / 2.0f, y, scale);
            option.getColorGradient().render(drawContext, x + backgroundWidth / 2 - 50, y + 6, mouseX, mouseY);
            DrawHelper.stopScaling(drawContext.getMatrices());

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                enableSkinScissor(); // re-enable the scissor
            }
        }

        @Override
        public boolean mouseClicked(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
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
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            option.getColorGradient().mouseDragged(mouseX, mouseY, button);
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            option.getColorGradient().mouseReleased(mouseX, mouseY, button);

            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }
    }

    public class ModernDoubleRenderer implements SkinRenderer<DoubleOption> {
        private double displayValue;
        private static final float ANIMATION_SPEED = 0.1f;

        @Override
        public void render(DrawContext drawContext, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            // Draw option name
            drawContext.drawText(
                    mc.textRenderer,
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
            displayValue = MathHelper.lerp(ANIMATION_SPEED, displayValue, option.get());

            // Background
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight, 1, DARKER_GRAY.getRGB()
            );

            // Active fill
            int activeFillWidth = (int) ((displayValue - option.minValue) / (option.maxValue - option.minValue) * option.getWidth());
            Color fillColor = isMouseOver(mouseX, mouseY, sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    sliderX, y, activeFillWidth, sliderBackgroundHeight, 1, fillColor.getRGB()
            );

            // Draw slider handle
            float sliderHandleX = sliderX + activeFillWidth - 5;
            DrawHelper.drawFilledCircle(drawContext.getMatrices().peek().getPositionMatrix(), sliderHandleX + 5, y + 1, 2, Color.WHITE.getRGB());

            // Draw value text
            String text = String.format("%.2f", displayValue);
            DrawHelper.scaleAndPosition(drawContext.getMatrices(), sliderX + 120 - mc.textRenderer.getWidth(text), y + 7, 0.6f);
            drawContext.drawText(
                    mc.textRenderer,
                    text,
                    sliderX + sliderBackgroundWidth + 10 - mc.textRenderer.getWidth(text),
                    y + 2,
                    -1,
                    true
            );
            drawContext.getMatrices().pop();
        }

        @Override
        public boolean mouseClicked(DoubleOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX() + (int) (width * 0.8f - 14) - 125, option.getY() - 1, option.getWidth() + 2, option.getHeight() + 1)) {
                option.setDragging(true);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(DoubleOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (option.isDragging()) {
                mouseX = mc.mouse.getX() / SCALE_FACTOR;
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
        public void render(DrawContext drawContext, EnumOption<E> option, int x, int y, int mouseX, int mouseY) {
            // Set dimensions for the main label and dropdown area
            option.setHeight(mc.textRenderer.fontHeight + 2);

            // Draw main option name and selected option
            String mainLabel = option.name + ": ";
            String selectedOption = option.get().toString();
            drawContext.drawText(mc.textRenderer, mainLabel, x + 4, y + 2, -1, false);
            Color fillColor = isMouseOver(mouseX, mouseY, x + 4 + mc.textRenderer.getWidth(mainLabel), y, mc.textRenderer.getWidth(selectedOption) + 5, mc.textRenderer.fontHeight + 2) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    x + 4 + mc.textRenderer.getWidth(mainLabel), y, mc.textRenderer.getWidth(selectedOption) + 5, mc.textRenderer.fontHeight + 2, 2,
                    fillColor.getRGB()
            );
            // "<" and ">" buttons
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.textRenderer.getWidth("<") + 5, mc.textRenderer.fontHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.textRenderer.getWidth("<") + 6, y, mc.textRenderer.getWidth(">") + 5, mc.textRenderer.fontHeight);
            // Shadow
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX + 1, y + 3,
                    (mc.textRenderer.getWidth("<") * 2) + 10, mc.textRenderer.fontHeight, 2,
                    ColorHelper.changeAlpha(Color.BLACK, 128).getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX, y + 2,
                    true, false, true, false,
                    mc.textRenderer.getWidth("<") + 5, mc.textRenderer.fontHeight, 2,
                    hoveredOverLeft ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX + mc.textRenderer.getWidth("<") + 6, y + 2,
                    false, true, false, true,
                    mc.textRenderer.getWidth(">") + 5, mc.textRenderer.fontHeight, 2,
                    hoveredOverRight ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawVerticalLine(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX + mc.textRenderer.getWidth("<") + 5,
                    y + 2,
                    mc.textRenderer.fontHeight,
                    0.7f,
                    Color.WHITE.getRGB()
            );
            drawContext.drawText(mc.textRenderer, "<", leftX + mc.textRenderer.getWidth("<") / 2 + 1, y + 3, -1, false);
            drawContext.drawText(mc.textRenderer, ">", leftX + mc.textRenderer.getWidth("<") + 7 + mc.textRenderer.getWidth(">") / 2, y + 3, -1, false);

            drawContext.drawText(mc.textRenderer, selectedOption, x + 6 + mc.textRenderer.getWidth(mainLabel), y + 2, Color.LIGHT_GRAY.getRGB(), false);
        }

        @Override
        public boolean mouseClicked(EnumOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().length == 0) return false;

            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            String mainLabel = option.name + ": ";
            String selectedOption = option.get().toString();

            // Check if the main label is clicked to cycle
            // "<" and ">" buttons
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.textRenderer.getWidth("<") + 5, mc.textRenderer.fontHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.textRenderer.getWidth("<") + 6, y, mc.textRenderer.getWidth(">") + 5, mc.textRenderer.fontHeight);
            boolean hoveredOverMainLabel = isMouseOver(mouseX, mouseY, x + 4 + mc.textRenderer.getWidth(mainLabel), y, mc.textRenderer.getWidth(selectedOption) + 5, mc.textRenderer.fontHeight + 2);

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
        public void render(DrawContext drawContext, ListOption<E> option, int x, int y, int mouseX, int mouseY) {

            // Set dimensions for the main label and dropdown area
            option.setHeight(mc.textRenderer.fontHeight + 2);

            // Draw main option name and selected option
            String mainLabel = option.name + ": ";
            String selectedOption = option.get().toString();
            drawContext.drawText(mc.textRenderer, mainLabel, x + 4, y + 2, -1, false);
            Color fillColor = isMouseOver(mouseX, mouseY, x + 4 + mc.textRenderer.getWidth(mainLabel), y, mc.textRenderer.getWidth(selectedOption) + 5, mc.textRenderer.fontHeight + 2) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    x + 4 + mc.textRenderer.getWidth(mainLabel), y, mc.textRenderer.getWidth(selectedOption) + 5, mc.textRenderer.fontHeight + 2, 2,
                    fillColor.getRGB()
            );

            // "<" and ">" buttons
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.textRenderer.getWidth("<") + 5, mc.textRenderer.fontHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.textRenderer.getWidth("<") + 6, y, mc.textRenderer.getWidth(">") + 5, mc.textRenderer.fontHeight);
            // Shadow
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX + 1, y + 3,
                    (mc.textRenderer.getWidth("<") * 2) + 10, mc.textRenderer.fontHeight, 2,
                    ColorHelper.changeAlpha(Color.BLACK, 128).getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX, y + 2,
                    true, false, true, false,
                    mc.textRenderer.getWidth("<") + 5, mc.textRenderer.fontHeight, 2,
                    hoveredOverLeft ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX + mc.textRenderer.getWidth("<") + 6, y + 2,
                    false, true, false, true,
                    mc.textRenderer.getWidth(">") + 5, mc.textRenderer.fontHeight, 2,
                    hoveredOverRight ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            DrawHelper.drawVerticalLine(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    leftX + mc.textRenderer.getWidth("<") + 5,
                    y + 2,
                    mc.textRenderer.fontHeight,
                    0.7f,
                    Color.WHITE.getRGB()
            );
            drawContext.drawText(mc.textRenderer, "<", leftX + mc.textRenderer.getWidth("<") / 2 + 1, y + 3, -1, false);
            drawContext.drawText(mc.textRenderer, ">", leftX + mc.textRenderer.getWidth("<") + 7 + mc.textRenderer.getWidth(">") / 2, y + 3, -1, false);

            drawContext.drawText(mc.textRenderer, selectedOption, x + 6 + mc.textRenderer.getWidth(mainLabel), y + 2, Color.LIGHT_GRAY.getRGB(), false);
        }

        @Override
        public boolean mouseClicked(ListOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().isEmpty()) return false;

            mouseX /= SCALE_FACTOR;
            mouseY /= SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            String mainLabel = option.name + ": ";
            String selectedOption = option.get().toString();

            // Calculate positions
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int leftX = x + contextMenuWidth - 30;

            // Check hover states
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, mc.textRenderer.getWidth("<") + 5, mc.textRenderer.fontHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + mc.textRenderer.getWidth("<") + 6, y, mc.textRenderer.getWidth(">") + 5, mc.textRenderer.fontHeight);
            boolean hoveredOverMainLabel = isMouseOver(mouseX, mouseY, x + 4 + mc.textRenderer.getWidth(mainLabel), y, mc.textRenderer.getWidth(selectedOption) + 5, mc.textRenderer.fontHeight + 2);

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
        public void render(DrawContext drawContext, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            mouseX = (int) (mc.mouse.getX() / SCALE_FACTOR);
            mouseY = (int) (mc.mouse.getY() / SCALE_FACTOR);

            String text = "Open";
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int xPos = x + 4 + contextMenuWidth - 40;

            option.setPosition(xPos - 1, y);
            option.setWidth(mc.textRenderer.getWidth(text) + 5);
            option.setHeight(16);

            drawContext.drawText(mc.textRenderer, option.name, x + 4, y + 4, -1, false);

            drawContext.drawText(mc.textRenderer, text, xPos + 2, y + 4, Color.WHITE.getRGB(), true);

            Color fillColor = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, mc.textRenderer.getWidth(text) + 5, mc.textRenderer.fontHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    xPos - 1, y + 1,
                    mc.textRenderer.getWidth(text) + 5, mc.textRenderer.fontHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );
            DrawHelper.drawOutlineRoundedBox(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    xPos - 1, y + 1,
                    mc.textRenderer.getWidth(text) + 5, mc.textRenderer.fontHeight + 4,
                    2,
                    0.7f,
                    Color.WHITE.getRGB()
            );

            option.getSubMenu().render(drawContext, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
        }

        @Override
        public void mouseScrolled(SubMenuOption option, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            SkinRenderer.super.mouseScrolled(option, mouseX, mouseY, horizontalAmount, verticalAmount);
        }

        @Override
        public boolean mouseDragged(SubMenuOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(SubMenuOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }

        @Override
        public boolean mouseClicked(SubMenuOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }

    public class ModernRunnableRenderer implements SkinRenderer<RunnableOption> {
        Color DARK_RED = new Color(116, 0, 0);
        Color DARK_GREEN = new Color(24, 132, 0, 226);

        @Override
        public void render(DrawContext drawContext, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            String text = "Run ▶";
            int contextMenuWidth = (int) (width * 0.8f - 14);
            int xPos = x + 4 + contextMenuWidth - 45;

            option.setPosition(xPos - 1, y);
            option.setWidth(mc.textRenderer.getWidth(text) + 5);
            option.setHeight(mc.textRenderer.fontHeight + 6);

            drawContext.drawText(mc.textRenderer, option.name, x + 4, y + 4, -1, false);

            drawContext.drawText(mc.textRenderer, text, xPos + 2, y + 4, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);

            Color fillColor = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, mc.textRenderer.getWidth(text) + 5, mc.textRenderer.fontHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    xPos - 1, y + 1,
                    mc.textRenderer.getWidth(text) + 5, mc.textRenderer.fontHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );
        }

        @Override
        public boolean mouseClicked(RunnableOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX() / SCALE_FACTOR;
            mouseY = mc.mouse.getY() / SCALE_FACTOR;
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }
}