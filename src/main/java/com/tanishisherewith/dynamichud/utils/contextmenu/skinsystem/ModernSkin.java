package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.helpers.animationhelper.AnimationProperty;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.MathAnimations;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.SquishAnimator;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.ValueAnimation;
import com.tanishisherewith.dynamichud.utils.Util;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutEngine;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import com.tanishisherewith.dynamichud.utils.handlers.ScrollHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tanishisherewith.dynamichud.helpers.ColorHelper.DARK_GREEN;
import static com.tanishisherewith.dynamichud.helpers.ColorHelper.DARK_RED;

public class ModernSkin extends Skin implements GroupableSkin {
    static Color DARK_GRAY = new Color(20, 20, 20, 200);
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

    int searchBoxWidth = 0;
    int searchBoxHeight = 14;
    int searchBoxX = 0;
    int searchBoxY = 0;

    private final Map<OptionGroup, GroupAnimData> groupAnimations = new HashMap<>();

    private ModernSearchBox searchBox;
    private String searchQuery = "";

    public ModernSkin(float radius, Component defaultToolTipHeader, Component defaultToolTipText) {
        this.radius = radius;
        this.TOOLTIP_TEXT = defaultToolTipText;
        this.TOOLTIP_HEAD = defaultToolTipHeader;
        this.defaultToolTipText = defaultToolTipText;
        this.defaultToolTipHeader = defaultToolTipHeader;

        addRenderer(BooleanOption.class, ModernBooleanRenderer::new);
        addRenderer(DoubleOption.class, ModernDoubleRenderer::new);
        addRenderer(CycleOption.class, ModernCycleRenderer::new);
        addRenderer(SubMenuOption.class, ModernSubMenuRenderer::new);
        addRenderer(RunnableOption.class, ModernRunnableRenderer::new);
        addRenderer(ColorOption.class, ModernColorOptionRenderer::new);

        this.scrollHandler = new ScrollHandler();

        setCreateNewScreen(true);
    }

    @Override
    public List<Option<?>> getOptions(ContextMenu<?> menu) {
        if(searchQuery != null && !searchQuery.isEmpty()) {
            return Util.getSearchResults(searchQuery,-1, contextMenu.getOptions());
        }
        return super.getOptions(menu);
    }

    public ModernSkin(float radius) {
        this(radius, Component.literal("Example Tip"), Component.literal("Hover over a setting to see its tool-tip (if present) here!"));
    }

    public ModernSkin() {
        this(4);
    }

    @Override
    public LayoutEngine.Offset getGroupIndent() {
        return new LayoutEngine.Offset(4, 2);
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
        if (option instanceof CycleOption) return 14;
        if (option instanceof SubMenuOption) return 14;
        if (option instanceof RunnableOption) return mc.font.lineHeight + 6;
        if (option instanceof ColorOption colorOption) {
            return colorOption.getHeight() > 0 ? colorOption.getHeight() : 20;
        }
        if (option instanceof OptionGroup group) {
            return group.isExpanded() ? group.getHeight() : 16;
        }
        return option.getHeight() > 0 ? option.getHeight() : mc.font.lineHeight;
    }

    private void renderSearchBox(GuiGraphics graphics, int mouseX, int mouseY){
        if (searchBox == null) {
            searchBox = new ModernSearchBox(searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight);
            searchBox.setResponder(query -> {
                searchQuery = query;
                // Reset scroll so results show from top
                scrollHandler.setScrollOffset(0);
            });
        }
        searchBox.setTotalBounds(searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight);

        searchBox.render(graphics, mouseX, mouseY, mc.getDeltaTracker().getGameTimeDeltaTicks());
    }

    private int computeGroupFullHeight(OptionGroup group, int groupX, int groupY, int targetWidth) {
        if (!group.isExpanded()) return 16;

        int yOffset = groupY + 16 + getGroupIndent().top(); // header height + indent
        int nestedIndent = getGroupIndent().left();
        int subWidth = targetWidth - nestedIndent - 8;

        for (Option<?> option : group.getGroupOptions()) {
            if (!option.shouldRender()) continue;
            option.setHeight(calcOptionHeight(option));
            yOffset = contextMenu.getLayoutEngine().layoutOption(option, groupX + nestedIndent, yOffset, subWidth);
        }
        return yOffset - groupY; // total height
    }

    // Adds a nice animation while opening and closing
    public void renderGroup(GuiGraphics graphics, OptionGroup group, int groupX, int groupY, int targetWidth, int mouseX, int mouseY) {
        GroupAnimData animData = groupAnimations.computeIfAbsent(group, g -> new GroupAnimData(16f));
        AnimationProperty<Float> heightProp = animData.property;
        if (group.isExpanded() && heightProp.get() <= 16) {
            int fullHeight = computeGroupFullHeight(group, groupX, groupY, targetWidth);
            heightProp.set((float) fullHeight);
        }

        if (animData.animation != null) {
            animData.animation.update();
            if(animData.animation.isFinished()){
                animData.animation = null;
            }
        }

        float animatedHeight = heightProp.get();
        int groupHeight = Math.round(animatedHeight);

        if (group.isExpanded() && groupHeight > 16) {
            DrawHelper.drawRoundedRectangle(graphics,
                    groupX + 1, groupY + 14, width - groupX - 8 + contextMenuX, groupHeight - 16 + getGroupIndent().top(), radius, DARKER_GRAY_2.getRGB());
        }

        Component groupText = group.name.copy().append(" " + (group.isExpanded() ? "-" : "+"));

        DrawHelper.drawRoundedRectangle(graphics,
                groupX + 1, groupY + 1, true, true, !group.isExpanded(), !group.isExpanded(), mc.font.width(groupText) + 6, 16, radius, DARKER_GRAY_2.getRGB());

        graphics.drawString(mc.font, groupText, groupX + 4, groupY + 5, -1, true);

        if (group.isExpanded() && groupHeight > 16) {
            int clipX = groupX + 1;
            int clipY = groupY + 16;
            int clipWidth = targetWidth + getGroupIndent().left() + 8;
            int clipHeight = groupHeight - 16;
            DrawHelper.enableScissor(clipX, clipY, clipWidth, clipHeight, SCALE_FACTOR, graphics);

            int yOffset = groupY + 16 + getGroupIndent().top();
            int nestedIndent = getGroupIndent().left();
            int subWidth = targetWidth - nestedIndent - 8;

            for (Option<?> option : group.getGroupOptions()) {
                if (!option.shouldRender()) continue;
                option.setHeight(calcOptionHeight(option));
                yOffset = contextMenu.getLayoutEngine().layoutOption(option, groupX + nestedIndent, yOffset, subWidth);
                option.render(graphics, option.getX(), option.getY(), mouseX, mouseY);
            }

            DrawHelper.disableScissor(graphics);
        }

        // actual height for layout
        group.setHeight(groupHeight);
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

        DrawHelper.scaledProjection(SCALE_FACTOR, graphics);

        updateContextDimensions();
        contextMenu.set(contextMenuX, contextMenuY, 0);

        // Background
        DrawHelper.drawRoundedRectangle(graphics,
                contextMenuX, contextMenuY, width, height, radius, DARKER_GRAY.getRGB());

        mouseX = (int) (mc.mouseHandler.xpos() / SCALE_FACTOR);
        mouseY = (int) (mc.mouseHandler.ypos() / SCALE_FACTOR);

        renderSearchBox(graphics,mouseX, mouseY);

        drawBackButton(graphics, mouseX, mouseY);

        int optionStartX = contextMenu.x + (int) (width * 0.2f) + 10;
        int targetWidth = (int) (width * 0.8f - 18);

        // Background behind the options scroll area
        DrawHelper.drawRoundedRectangle(graphics,
                optionStartX, contextMenuY + 19, width * 0.8f - 14, height - 23, radius, DARK_GRAY.getRGB());

        enableSkinScissor(graphics);

        int yPos = contextMenu.y + 22 - scrollHandler.getScrollOffset();

        for (Option<?> option : getOptions(contextMenu)) {
            if (!option.shouldRender()) continue;

            if (option.isMouseOver(mouseX, mouseY)) {
                setTooltipText(option.name, option.description);
            }

            option.setHeight(calcOptionHeight(option));
            int nextY = contextMenu.getLayoutEngine().layoutOption(option, optionStartX + 2, yPos, targetWidth);

            if (option instanceof OptionGroup group) {
                this.renderGroup(graphics, group, optionStartX + 2, yPos, mouseX, mouseY);
                yPos += group.getHeight() + contextMenu.getLayoutEngine().getItemSpacing();
            } else {
                option.render(graphics, option.getX(), option.getY(), mouseX, mouseY);
                yPos = nextY;
            }
        }

        DrawHelper.disableScissor(graphics);

        contextMenu.setWidth(width);
        contextMenu.setHeight(yPos - (contextMenu.y + 26 - scrollHandler.getScrollOffset()));

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
        searchBoxWidth = (int)(width * 0.35f);
        searchBoxHeight = mc.font.lineHeight + 5;
        searchBoxX = contextMenuX + searchBoxWidth;
        searchBoxY = contextMenuY + 2;
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

        DrawHelper.enableScissor(contextMenuX + 2, tooltipY,toolTipWidth,toolTipHeight,SCALE_FACTOR,graphics);

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

        DrawHelper.disableScissor(graphics);
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
    public void keyPressed(ContextMenu<?> menu, int key, int scanCode, int modifiers) {
        if (searchBox != null && searchBox.isFocused()) {
            searchBox.keyPressed(new KeyEvent(key,scanCode,modifiers));
            return;
        }
        super.keyPressed(menu, key, scanCode, modifiers);
    }

    @Override
    public void keyReleased(ContextMenu<?> menu, int key, int scanCode, int modifiers) {
        if (searchBox != null && searchBox.isFocused()) {
            searchBox.keyReleased(new KeyEvent(key,scanCode,modifiers));
            return;
        }
        super.keyReleased(menu, key, scanCode, modifiers);
    }

    @Override
    public void charTyped(ContextMenu<?> menu, char c, int modifiers) {
        if (searchBox != null && searchBox.isFocused()) {
            searchBox.charTyped(new CharacterEvent((int) c,modifiers));
            return;
        }
        super.charTyped(menu, c, modifiers);
    }

    @Override
    public boolean mouseReleased(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        scrollHandler.stopDragging();
        if (searchBox != null) {
            MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0));
            searchBox.mouseReleased(event);
        }
        return super.mouseReleased(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseClicked(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
        mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

        if (searchBox != null) {
            MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0));
            searchBox.mouseClicked(event,false);
        }


        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + width - 5, contextMenuY + 19, 7, height)) {
            scrollHandler.startDragging(mouseY);
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int optionStartX = contextMenuX + (int) (width * 0.2f) + 10;
            int yPos = contextMenu.y + 22 - scrollHandler.getScrollOffset();
            int spacing = contextMenu.getLayoutEngine().getItemSpacing();

            for (Option<?> option : getOptions(contextMenu)) {
                if (!option.shouldRender()) continue;
                int optHeight = calcOptionHeight(option);
                if (option instanceof OptionGroup group) {
                    Component groupText = group.name.copy().append(" " + (group.isExpanded() ? "-" : "+"));
                    if (isMouseOver(mouseX, mouseY, optionStartX + 2, yPos,
                            mc.font.width(groupText) + 6, 16)) {
                        boolean willBeExpanded = !group.isExpanded();
                        group.setExpanded(willBeExpanded);

                        GroupAnimData animData = groupAnimations.computeIfAbsent(group, g -> new GroupAnimData(16f));
                        AnimationProperty<Float> heightProp = animData.property;
                        float current = heightProp.get();
                        float target;
                        if (willBeExpanded) {
                            int targetWidthForGroup = (int) (width * 0.8f - 18);
                            int fullHeight = computeGroupFullHeight(group, optionStartX + 2, yPos, targetWidthForGroup);
                            target = Math.max(fullHeight, 16f);
                        } else {
                            target = 16f;
                        }

                        ValueAnimation anim = new ValueAnimation(heightProp, current, target, EasingType.EASE_OUT_QUAD);
                        anim.duration(200);
                        anim.start();
                        animData.animation = anim;
                        return true;
                    }
                    yPos += group.getHeight() + spacing;
                } else {
                    yPos += optHeight + spacing;
                }
            }
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + 1, contextMenuY + 1, mc.font.width("< Back") + 10, 16)) {
            mc.getSoundManager().play(SimpleSoundInstance.forUI(
                    SoundEvents.UI_BUTTON_CLICK, 1.0F));
            contextMenu.close();
            if (searchBox != null) {
                searchBox.setFocused(false);
                searchBox.setValue("");
            }
            searchQuery = "";
            groupAnimations.clear();
            return true;
        }
        return super.mouseClicked(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(ContextMenu<?> menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (searchBox != null) {
            MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0));
            searchBox.mouseDragged(event, deltaX, deltaY);
        }

        mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
        mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, contextMenuX + width - 5, contextMenuY + 19, 7, height)) {
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
                    x + 4,
                    y + 2,
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
        private final SquishAnimator animator = new SquishAnimator();


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
                    x + 4,
                    y + 5,
                    -1,
                    false
            );

            int width = 20;
            int shadowOpacity = Math.min(option.value.getAlpha(), 45);

            boolean isHovering = isMouseOver(mouseX, mouseY, x + option.getWidth() - width - 17, y + 1, width + 2, 14);
            boolean isDown = isHovering && GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            animator.update(isDown);

            Color behindColor = isHovering ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.scaleAndPosition(graphics.pose(), x + option.getWidth() - width - 17,
                    y + 1,width + 2, 14, animator.getScale());

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

            DrawHelper.stopScaling(graphics.pose());

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
                    x + 4,
                    y + 2,
                    -1,
                    false
            );
            int sliderX = x + option.getWidth() - sliderBackgroundWidth - 10;

            displayValue = Mth.lerp(ANIMATION_SPEED, displayValue, option.get());

            DrawHelper.drawRoundedRectangle(
                    graphics,
                    sliderX, y + 2, sliderBackgroundWidth, sliderBackgroundHeight, 1, DARKER_GRAY.getRGB()
            );

            int activeFillWidth = (int) ((displayValue - option.minValue) / (option.maxValue - option.minValue) * sliderBackgroundWidth);
            Color fillColor = isMouseOver(mouseX, mouseY, sliderX, y, sliderBackgroundWidth, sliderBackgroundHeight + 4) ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    sliderX, y + 2, activeFillWidth, sliderBackgroundHeight, 2, fillColor.getRGB()
            );

            float sliderHandleX = sliderX + activeFillWidth - 5;
            DrawHelper.drawFilledCircle(graphics, sliderHandleX + 5, y + 3, 2, Color.WHITE.getRGB());

            int decimalPlaces = String.valueOf(option.step).split("\\.")[1].length();

            // Format option.value to the determined number of decimal places
            String label = String.format("%." + decimalPlaces + "f", displayValue);
            DrawHelper.scaleAndPosition(graphics.pose(), sliderX + sliderBackgroundWidth - mc.font.width(label), y + 7, 0.6f);
            graphics.drawString(
                    mc.font,
                    label,
                    sliderX + sliderBackgroundWidth + 10 - mc.font.width(label),
                    y + 4,
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
            }
            return false;
        }
    }

    public class ModernCycleRenderer<E> implements SkinRenderer<CycleOption<E>> {
        private final SquishAnimator textAnim = new SquishAnimator();
        private final SquishAnimator leftAnim = new SquishAnimator(1.0f,0.9f);
        private final SquishAnimator rightAnim = new SquishAnimator(1.0f,0.9f);

        @Override
        public void render(GuiGraphics graphics, CycleOption<E> option, int x, int y, int mouseX, int mouseY) {
            y += 2;
            option.setHeight(mc.font.lineHeight + 2);

            Component mainLabel = option.name.copy().append(": ");
            graphics.drawString(mc.font, mainLabel, x + 4, y + 2, -1, false);

            String selectedOption = option.get().toString();

            int leftX = x + option.getWidth() - 30;

            int mainLabelWidth = mc.font.width(mainLabel);
            int selectedOptionWidth = mc.font.width(selectedOption);
            int leftWidth = mc.font.width("<");
            int rightWidth = mc.font.width(">");

            boolean hoveredOverText = isMouseOver(mouseX, mouseY, x + 4 + mainLabelWidth, y, selectedOptionWidth + 5, mc.font.lineHeight + 2);
            boolean hoveredOverLeft = isMouseOver(mouseX, mouseY, leftX, y, leftWidth + 5, mc.font.lineHeight);
            boolean hoveredOverRight = isMouseOver(mouseX, mouseY, leftX + leftWidth + 6, y, rightWidth + 5, mc.font.lineHeight);

            boolean isPressed = GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS || GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            boolean isClickingOnText = hoveredOverText && isPressed;
            boolean isClickingOnLeft = hoveredOverLeft && isPressed;
            boolean isClickingOnRight = hoveredOverRight && isPressed;

            textAnim.update(isClickingOnText);
            leftAnim.update(isClickingOnLeft);
            rightAnim.update(isClickingOnRight);

            DrawHelper.scaleAndPosition(graphics.pose(),x + 4 + mainLabelWidth, y, selectedOptionWidth + 5, mc.font.lineHeight + 2, textAnim.getScale());

            Color fillColor = hoveredOverText ? getThemeColor().darker().darker() : getThemeColor();
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    x + 4 + mainLabelWidth, y, selectedOptionWidth + 5, mc.font.lineHeight + 2, 2,
                    fillColor.getRGB()
            );
            graphics.drawString(mc.font, selectedOption, x + 6 + mainLabelWidth, y + 2, Color.WHITE.getRGB(), hoveredOverText);

            DrawHelper.stopScaling(graphics.pose());

            //Shadow
            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX + 1, y + 3,
                    (leftWidth * 2) + 10, mc.font.lineHeight, 2,
                    ColorHelper.changeAlpha(Color.BLACK, 128).getRGB()
            );

            DrawHelper.scaleAndPosition(graphics.pose(),leftX, y + 2, leftWidth + 5, mc.font.lineHeight, leftAnim.getScale());

            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX, y + 2,
                    true, false, true, false,
                    leftWidth + 5, mc.font.lineHeight, 2,
                    hoveredOverLeft ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            graphics.drawString(mc.font, "<", leftX + leftWidth / 2 + 1, y + 3, -1, false);

            DrawHelper.stopScaling(graphics.pose());

            DrawHelper.scaleAndPosition(graphics.pose(),leftX + leftWidth + 6, y + 2, rightWidth + 5, mc.font.lineHeight, rightAnim.getScale());

            DrawHelper.drawRoundedRectangle(
                    graphics,
                    leftX + leftWidth + 6, y + 2,
                    false, true, false, true,
                    rightWidth + 5, mc.font.lineHeight, 2,
                    hoveredOverRight ? getThemeColor().darker().darker().getRGB() : getThemeColor().getRGB()
            );
            graphics.drawString(mc.font, ">", leftX + leftWidth + 7 + rightWidth / 2, y + 3, -1, false);
            DrawHelper.stopScaling(graphics.pose());

            //todo: unsure whether to keep this or not? it removes the 3D illusion
            /*
            DrawHelper.drawVerticalLine(
                    graphics,
                    leftX + leftWidth + 5,
                    y + 2,
                    mc.font.lineHeight,
                    1f,
                    Color.WHITE.getRGB()
            );

             */
        }

        @Override
        public boolean mouseClicked(CycleOption<E> option, double mouseX, double mouseY, int button) {
            if (option.getValues().isEmpty()) return false;

            mouseX = mc.mouseHandler.xpos() / SCALE_FACTOR;
            mouseY = mc.mouseHandler.ypos() / SCALE_FACTOR;

            int x = option.getX();
            int y = option.getY();
            Component mainLabel = option.name.copy().append(": ");
            String selectedOption = option.get().toString();
            y += 2;

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
        private final SquishAnimator animator = new SquishAnimator();

        @Override
        public void render(GuiGraphics graphics, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            String textLabel = "Open";
            int xPos = x + option.getWidth() - 40;
            float width = mc.font.width(textLabel) + 5;

            boolean isHovering = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, width, mc.font.lineHeight + 4);
            boolean isDown = isHovering && GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            animator.update(isDown);

            graphics.drawString(mc.font, option.name, x + 4, y + 4, -1, false);

            Color fillColor = isHovering ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.scaleAndPosition(graphics.pose(),x,y,width, mc.font.lineHeight + 4, animator.getScale());
            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    xPos - 1, y,
                    mc.font.width(textLabel) + 5, mc.font.lineHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );
            DrawHelper.drawOutlineRoundedBox(
                    graphics,
                    xPos - 1, y,
                    mc.font.width(textLabel) + 5, mc.font.lineHeight + 4,
                    2,
                    0.7f,
                    Color.WHITE.getRGB()
            );
            graphics.drawString(mc.font, textLabel, xPos + 2, y + 3, Color.WHITE.getRGB(), true);

            DrawHelper.stopScaling(graphics.pose());

            option.getSubMenu().render(graphics, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
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
        private final SquishAnimator animator = new SquishAnimator();

        @Override
        public void render(GuiGraphics graphics, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            String labelText = "Run ▶";
            int xPos = x + option.getWidth() - 45;

            option.setHeight(mc.font.lineHeight + 6);

            boolean isHovering = isMouseOver(mouseX, mouseY, xPos + 2, y + 4, width, mc.font.lineHeight + 4);
            boolean isDown = isHovering && GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            animator.update(isDown);

            graphics.drawString(mc.font, option.name, x + 4, y + 4, -1, false);

            float width = mc.font.width(labelText) + 5;

            Color fillColor = isHovering ? getThemeColor().darker().darker() : getThemeColor();

            DrawHelper.scaleAndPosition(graphics.pose(),xPos - 1, y + 1,width, mc.font.lineHeight + 4, animator.getScale());

            DrawHelper.drawRoundedRectangleWithShadowBadWay(
                    graphics,
                    xPos - 1, y + 1,
                    width, mc.font.lineHeight + 4,
                    2,
                    fillColor.getRGB(),
                    180,
                    1,
                    1
            );

            graphics.drawString(mc.font, labelText, xPos + 2, y + 4, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);
            DrawHelper.stopScaling(graphics.pose());
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

        @Override
        public boolean mouseReleased(RunnableOption option, double mouseX, double mouseY, int button) {
            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }
    }

    @Override
    public Skin clone() {
        return new ModernSkin(radius, defaultToolTipHeader, defaultToolTipText);
    }


    private static class GroupAnimData {
        AnimationProperty<Float> property;
        ValueAnimation animation;

        GroupAnimData(float initial) {
            property = new AnimationProperty<Float>() {
                private float value = initial;
                @Override public Float get() { return value; }
                @Override public void set(Float v) { value = v; }
            };
        }
    }
    public class ModernSearchBox extends EditBox {
        private static final int CORNER_RADIUS = 6;
        private static final int ICON_PADDING = 6;
        private static final int CLEAR_BUTTON_SIZE = 16;
        private static final int TEXT_PADDING = 4;

        private final SquishAnimator clearAnimator = new SquishAnimator();
        private int totalX, totalY, totalWidth, totalHeight;

        public ModernSearchBox(int x, int y, int width, int height) {
            super(mc.font, x, y, width, height, Component.empty());
            this.setBordered(false);
            this.setCentered(false);
            this.setTextShadow(true);
            this.setEditable(true);
            this.setVisible(true);
            this.setMaxLength(50);
            this.active = true;
            this.setCanLoseFocus(true);
            this.setHint(Component.literal("Search..."));
            setTotalBounds(x, y, width, height);
        }

        public void setTotalBounds(int x, int y, int width, int height) {
            this.totalX = x;
            this.totalY = y;
            this.totalWidth = width;
            this.totalHeight = height;

            int iconWidth = mc.font.width("\uD83D\uDD0D");
            int textX = totalX + iconWidth + ICON_PADDING + TEXT_PADDING;
            int textWidth = totalWidth - (textX - totalX) - (CLEAR_BUTTON_SIZE + ICON_PADDING + TEXT_PADDING);
            if (textWidth < 10) textWidth = 10;

            this.setX(textX);
            this.setY(totalY);
            this.setWidth(textWidth);
            this.setHeight(totalHeight);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            if (!this.visible) return;

            int x = totalX;
            int y = totalY;
            int w = totalWidth;
            int h = totalHeight;

            // Background
            Color bgColor = isFocused() ? new Color(25, 25, 25, 240) : DARKER_GRAY;
            DrawHelper.drawRoundedRectangleWithShadowBadWay(graphics, x, y, w, h, CORNER_RADIUS, bgColor.getRGB(),60,1,2);

            if (isFocused()) {
                float pulse = MathAnimations.pulse2(2.5f, 0.3f, 1.0f); // 2.5 oscillations/sec
                int glowAlpha = (int) (pulse * 200 + 55); // alpha range 55–255
                int glowColor = ColorHelper.changeAlpha(getThemeColor(), glowAlpha).getRGB();
                DrawHelper.drawOutlineRoundedBox(graphics, x, y, w, h, CORNER_RADIUS, 1f, glowColor);
            } else {
                DrawHelper.drawOutlineRoundedBox(graphics, x, y, w, h, CORNER_RADIUS, 1f, DARK_GRAY.getRGB());
            }


            String icon = "\uD83D\uDD0D";
            int iconX = x + ICON_PADDING;
            int iconY = y + (h - mc.font.lineHeight) / 2 + 1;
            int iconColor = isFocused()? Color.WHITE.getRGB() : 0x80FFFFFF;
            graphics.drawString(mc.font, icon, iconX, iconY, iconColor, false);

            graphics.pose().pushMatrix();
            graphics.pose().translate(-1,3);
            super.renderWidget(graphics, mouseX, mouseY, delta);
            graphics.pose().popMatrix();

            String clearText = "✕";
            int clearWidth = mc.font.width(clearText);
            int clearX = x + w - CLEAR_BUTTON_SIZE;
            int clearY = y + (h - mc.font.lineHeight) / 2;
            boolean isClearHovered = Skin.isMouseOver(mouseX, mouseY, clearX - 2, clearY - 1, clearWidth + 4, mc.font.lineHeight + 2);
            clearAnimator.update(isClearHovered && (mouseX != -1 && mouseY != -1));

            if (!getValue().isEmpty()) {
                float scale = clearAnimator.getScale();
                int clearBgColor = isClearHovered ? Color.RED.darker().getRGB() : ColorHelper.changeAlpha(Color.WHITE, 30).getRGB();
                int bgW = clearWidth + 4;
                int bgH = mc.font.lineHeight;
                DrawHelper.scaleAndPosition(graphics.pose(), clearX - 0.25f, clearY + 0.5f, bgW, bgH, scale);
                DrawHelper.drawRoundedRectangle(graphics, clearX - 0.25f, clearY + 0.5f, bgW, bgH, 4, clearBgColor);
                graphics.drawString(mc.font, clearText, clearX + 2, clearY + 1,
                        isClearHovered ? 0xFFFFFFFF : 0xB0FFFFFF, false);
                DrawHelper.stopScaling(graphics.pose());
            }
            if (isHovered()) {
                graphics.requestCursor(CursorTypes.IBEAM);
            }
        }
        @Override
        public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean bl) {
            if (!this.isActive()) {
                return false;
            }
            if (!getValue().isEmpty()) {
                int clearX = totalX + totalWidth - CLEAR_BUTTON_SIZE - 2;
                int clearY = totalY + (totalHeight - mc.font.lineHeight) / 2 - 1;
                int clearW = mc.font.width("✕") + 4;
                int clearH = mc.font.lineHeight + 2;
                if (Skin.isMouseOver(event.x(), event.y(), clearX, clearY, clearW, clearH)) {
                    setValue("");
                    return true;
                }
            }

            if (this.isValidClickButton(event.buttonInfo()) && Skin.isMouseOver(event.x(), event.y(), totalX, totalY, totalWidth, totalHeight)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onClick(event, bl);
                setFocused(true);
                return true;
            } else {
                setFocused(false);
            }

            return false;
        }

        public int getTotalWidth() {
            return totalWidth;
        }

        private Color getThemeColor() {
            return ModernSkin.this.getThemeColor();
        }
    }
}