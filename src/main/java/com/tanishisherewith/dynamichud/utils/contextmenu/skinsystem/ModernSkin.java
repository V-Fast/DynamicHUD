package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutContext;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

//TODO: Complete this
public class ModernSkin extends Skin implements GroupableSkin {
    private final Color themeColor;
    private final float radius;
    private final String defaultToolTipHeader;
    private final String defaultToolTipText;
    Color DARK_GRAY = new Color(20, 20, 20, 229);
    Color DARKER_GRAY_2 = new Color(12,12,12,246);
    Color DARKER_GRAY = new Color(10, 10, 10, 243);
    private int contextMenuX = 0, contextMenuY = 0;
    private int width = 0, height = 0;
    private float scaledWidth = 0, scaledHeight = 0;
    private String TOOLTIP_TEXT;
    private String TOOLTIP_HEAD;
    private static int SCALE_FACTOR = 4;

    public ModernSkin(Color themeColor, float radius, String defaultToolTipHeader, String defaultToolTipText) {
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

        setCreateNewScreen(true);
    }

    public ModernSkin(Color themeColor, float radius) {
        this(themeColor, radius, "Example Tip", "Hover over a setting to see its tool-tip (if present) here!");
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

    @Override
    public void renderGroup(DrawContext drawContext, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY) {
        mouseX = (int) (mc.mouse.getX()/SCALE_FACTOR);
        mouseY = (int) (mc.mouse.getY()/SCALE_FACTOR);

        if(group.isExpanded()) {
            DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                    groupX + 1, groupY + 14, width - groupX - 8 + contextMenuX, group.getHeight(), radius, DARKER_GRAY_2.getRGB());
        }

        String groupText = group.name + " " + (group.isExpanded() ? "-" : "+");

        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                groupX + 1, groupY + 1,true,true, !group.isExpanded(),!group.isExpanded(), mc.textRenderer.getWidth(groupText) + 6, 16, radius, DARKER_GRAY_2.getRGB());

        drawContext.drawText(mc.textRenderer, groupText, groupX + 4, groupY + 4, -1, true);

        if(group.isExpanded()) {
            int yOffset = groupY + 16 + getGroupIndent().top;
            for (Option<?> option : group.getGroupOptions()) {
                if (!option.shouldRender()) continue;
                option.render(drawContext, groupX + getGroupIndent().left, yOffset, mouseX, mouseY);
                yOffset += option.getHeight() + 1;
            }

            group.setHeight(yOffset - groupY - 16);
        } else{
            group.setHeight(16);
        }
    }

    @Override
    public void renderContextMenu(DrawContext drawContext, ContextMenu contextMenu, int mouseX, int mouseY) {
        //This is equivalent to "Auto" GUI scale in minecraft options
        SCALE_FACTOR = mc.getWindow().calculateScaleFactor(0, mc.forcesUnicodeFont());

        mouseX = (int) (mc.mouse.getX()/SCALE_FACTOR);
        mouseY = (int) (mc.mouse.getY()/SCALE_FACTOR);

        // Apply custom scaling to counteract Minecraft's default scaling
        DrawHelper.customScaledProjection(SCALE_FACTOR);

        updateContextDimensions();
        contextMenu.set(contextMenuX, contextMenuY, 0);

        //Background
        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                contextMenuX, contextMenuY, width, height, radius, DARKER_GRAY.getRGB());

        drawBackButton(drawContext, mouseX, mouseY);

        //Tool-Tip width + padding
        int optionStartX = contextMenu.x + (int) (width * 0.2f) + 10;

        //Background behind the options
        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),
                optionStartX, contextMenuY + 19, width * 0.8f - 14, height - 23, radius, DARK_GRAY.getRGB());

        int yOffset = contextMenu.y + 19 + 3;
        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (option.isMouseOver(mouseX, mouseY)) {
                setTooltipText(option.name, option.description);
            }

            if (option instanceof OptionGroup group) {
                renderGroup(drawContext, group, optionStartX + 2, yOffset, mouseX, mouseY);
            } else {
                option.render(drawContext, optionStartX + 2, yOffset, mouseX, mouseY);
            }

            yOffset += option.getHeight() + 1;
        }

        contextMenu.setWidth(width);
        contextMenu.setHeight(height);

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

        if (!TOOLTIP_TEXT.isEmpty()) {
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

        if (TOOLTIP_TEXT.isEmpty() || TOOLTIP_HEAD.isEmpty()) {
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

        List<OrderedText> wrappedText = mc.textRenderer.wrapLines(StringVisitable.plain(TOOLTIP_TEXT), toolTipWidth);

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

    public void setTooltipText(String head_text, String tooltip_text) {
        TOOLTIP_TEXT = tooltip_text;
        TOOLTIP_HEAD = head_text;
    }

    @Override
    public boolean mouseClicked(ContextMenu menu, double mouseX, double mouseY, int button) {
        mouseX = mc.mouse.getX()/SCALE_FACTOR;
        mouseY = mc.mouse.getY()/SCALE_FACTOR;

        if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int optionStartX = contextMenuX + (int) (width * 0.2f) + 10;
            int yOffset = contextMenuY + 22;
            for (Option<?> option : getOptions(contextMenu)) {
                if (!option.shouldRender()) continue;

                if (option instanceof OptionGroup group) {
                    if (isMouseOver(mouseX, mouseY, optionStartX + 2, yOffset,
                            mc.textRenderer.getWidth(group.name + " " + (group.isExpanded() ? "-" : "+")) + 6,
                            16)) {
                        group.setExpanded(!group.isExpanded());
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

    public Color getThemeColor() {
        return themeColor;
    }

    public class ModernBooleanRenderer implements SkinRenderer<BooleanOption> {
        private float animationProgress = 0f;
        private boolean animating = false;

        @Override
        public void render(DrawContext drawContext, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            int backgroundWidth = (int) (width * 0.8f - 14);

            option.setHeight(14);
            option.set(x,y);
            option.setWidth(backgroundWidth);

            MatrixStack matrices = drawContext.getMatrices();

            // Animate the toggle
            if (animating) {
                animationProgress += 0.1f; // Adjust speed as needed
                if (animationProgress >= 1f) {
                    animationProgress = 0f;
                    animating = false;
                }
            }

            int toggleBgX = x + backgroundWidth - 30;
            // Background
            boolean active = option.get();
            Color backgroundColor = active ? getThemeColor() : DARKER_GRAY;

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    matrices.peek().getPositionMatrix(),
                    toggleBgX, y + 2, 14, 7,
                    3,
                    backgroundColor.getRGB(),
                    125, 1, 1
            );

            // Draw toggle circle
            float toggleX = active ? toggleBgX + 10 : toggleBgX + 4;
            if (animating) {
                toggleX = MathHelper.lerp(animationProgress, active ? toggleBgX + 4 : toggleBgX + 10, toggleX);
            }

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
            mouseX = mc.mouse.getX()/SCALE_FACTOR;
            mouseY = mc.mouse.getY()/SCALE_FACTOR;

            int backgroundWidth = (int) (width * 0.8f - 14);
            int toggleBgX = option.getX() + backgroundWidth - 30;

            if(isMouseOver(mouseX,mouseY,toggleBgX,option.getY(),14,option.getHeight())){
                option.set(!option.get());
                animating = true;
                return true;
            }
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }

    public class ModernColorOptionRenderer implements SkinRenderer<ColorOption> {
        private static final float ANIMATION_SPEED = 0.1f;
        private float scale = 0f;
        private boolean display = false;

        public void update(ColorOption option){
            if(option.getColorGradient().isDisplay() && display){
                scale += ANIMATION_SPEED;
            }
            if(!display){
                scale -= ANIMATION_SPEED;
            }
            scale = MathHelper.clamp(scale,0,1.0f);
            if(scale <= 0){
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

            option.set(x, y);

            int width = 20;
            int shadowOpacity = Math.min(option.value.getAlpha(), 45);

            //The shape behind the preview
            DrawHelper.drawRoundedRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                    x + backgroundWidth - width - 17,
                    y + 1,
                    width + 2,
                    14,
                    2,
                    getThemeColor().getRGB(),
                    shadowOpacity,
                    1,
                    1);

            //The letter above the shape behind the preview
            drawContext.drawText(
                    mc.textRenderer,
                    option.getColorGradient().isDisplay() ? "^" : "v",
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

            int targetHeight = option.getColorGradient().isDisplay() ? (int) (option.getColorGradient().getBoxSize() + option.getColorGradient().getGradientBox().getSize() * scale) : 25;
            option.setHeight(targetHeight);
            option.setWidth(width);

            DrawHelper.scaleAndPosition(drawContext.getMatrices(),x + backgroundWidth/2.0f,y, scale);

            option.getColorGradient().render(drawContext, x + backgroundWidth/2 - 50, y + 6);

            DrawHelper.stopScaling(drawContext.getMatrices());
        }

        @Override
        public boolean mouseClicked(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX()/SCALE_FACTOR;
            mouseY = mc.mouse.getY()/SCALE_FACTOR;
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX() + (int) (width * 0.8f - 14) - 37, option.getY(), 30, 23)) {
                option.isVisible = !option.isVisible;
                if (option.isVisible) {
                    option.getColorGradient().display();
                    display = true;
                } else {
                    display = false;
                }
                return true;
            }
            option.getColorGradient().mouseClicked(mouseX,mouseY,button);
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(ColorOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            mouseX = mc.mouse.getX()/SCALE_FACTOR;
            mouseY = mc.mouse.getY()/SCALE_FACTOR;
            option.getColorGradient().mouseDragged(mouseX,mouseY,button);
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(ColorOption option, double mouseX, double mouseY, int button) {
            mouseX = mc.mouse.getX()/SCALE_FACTOR;
            mouseY = mc.mouse.getY()/SCALE_FACTOR;
            option.getColorGradient().mouseReleased(mouseX,mouseY,button);

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

            option.set(x,y);
            option.setWidth(sliderBackgroundWidth);
            option.setHeight(14);

            // Smoothly interpolate to the new value
            displayValue = MathHelper.lerp(ANIMATION_SPEED, displayValue, option.get());

            // Background
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight,1, DARKER_GRAY.getRGB()
            );

            // Active fill
            int activeFillWidth = (int) ((displayValue - option.minValue) / (option.maxValue - option.minValue) * option.getWidth());
            Color activeColor = getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    drawContext.getMatrices().peek().getPositionMatrix(),
                    sliderX, y, activeFillWidth, sliderBackgroundHeight,1, activeColor.getRGB()
            );

            // Draw slider handle
            float sliderHandleX = sliderX + activeFillWidth - 5;
            DrawHelper.drawFilledCircle(drawContext.getMatrices().peek().getPositionMatrix(), sliderHandleX + 5, y + 1, 2, Color.WHITE.getRGB());

            // Draw value text
            String text = String.format("%.2f", displayValue);
            DrawHelper.scaleAndPosition(drawContext.getMatrices(),sliderX + 120 - mc.textRenderer.getWidth(text),y + 7,0.6f);
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
            mouseX = mc.mouse.getX()/SCALE_FACTOR;
            mouseY = mc.mouse.getY()/SCALE_FACTOR;
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX() + (int) (width * 0.8f - 14) - 125, option.getY() - 1, option.getWidth() + 2, option.getHeight() + 1)) {
                option.setDragging(true);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(DoubleOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (option.isDragging()) {
                mouseX = mc.mouse.getX()/SCALE_FACTOR;
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
        private int maxWidth = 50;

        private void calculateMaxWidth(EnumOption<E> option) {
            for (E listValues : option.getValues()) {
                int width = mc.textRenderer.getWidth(listValues.toString()) + 5;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }

        @Override
        public void render(DrawContext drawContext, EnumOption<E> option, int x, int y, int mouseX, int mouseY) {
            calculateMaxWidth(option);
            option.setHeight(25);
            option.setWidth(maxWidth);

            drawContext.drawText(mc.textRenderer, option.name + ": ", x + 15, y + 25 / 2 - 5, -1, true);

            option.set(x + width - maxWidth - 25, y);

            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = option.get().toString();
            drawContext.drawText(mc.textRenderer, text, option.getX() + maxWidth / 2 - mc.textRenderer.getWidth(text) / 2, y + 5, Color.CYAN.getRGB(), true);
        }
    }

    public class ModernListRenderer<E> implements SkinRenderer<ListOption<E>> {
        private int maxWidth = 50;

        private void calculateMaxWidth(ListOption<E> option) {
            for (E listValues : option.getValues()) {
                int width = mc.textRenderer.getWidth(listValues.toString()) + 5;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }

        @Override
        public void render(DrawContext drawContext, ListOption<E> option, int x, int y, int mouseX, int mouseY) {
            calculateMaxWidth(option);
            option.setHeight(25);
            option.setWidth(maxWidth);

            drawContext.drawText(mc.textRenderer, option.name + ": ", x + 15, y + 25 / 2 - 5, -1, true);

            option.set(x + width - maxWidth - 25, y);

            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = option.get().toString();
            drawContext.drawText(mc.textRenderer, text, option.getX() + maxWidth / 2 - mc.textRenderer.getWidth(text) / 2, y + 5, Color.CYAN.getRGB(), true);
        }
    }

    public class ModernSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(DrawContext drawContext, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(20);
            option.setWidth(30);

            drawContext.drawText(mc.textRenderer, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.set(x + width - 75, y);

            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = "Open";
            drawContext.drawText(mc.textRenderer, text, option.getX() + option.getWidth() / 2 - mc.textRenderer.getWidth(text) / 2, y + 5, Color.YELLOW.getRGB(), true);

            option.getSubMenu().render(drawContext, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
        }
    }

    public class ModernRunnableRenderer implements SkinRenderer<RunnableOption> {
        Color DARK_RED = new Color(116, 0, 0);
        Color DARK_GREEN = new Color(24, 132, 0, 226);

        @Override
        public void render(DrawContext drawContext, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(25);
            option.setWidth(26);

            drawContext.drawText(mc.textRenderer, option.name.replaceFirst("Run: ", "") + ": ", x + 15, y + 25 / 2 - 5, -1, true);

            option.set(x + width - 75, y);

            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawContext.drawText(mc.textRenderer, "Run", option.getX() + option.getWidth() / 2 - mc.textRenderer.getWidth("Run") / 2, y + 5, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);
        }
    }
}