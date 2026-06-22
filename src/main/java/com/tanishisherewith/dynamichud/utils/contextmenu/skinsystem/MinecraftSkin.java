package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.helpers.animationhelper.AnimationProperty;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.ValueAnimation;
import com.tanishisherewith.dynamichud.utils.Util;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutEngine;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import com.tanishisherewith.dynamichud.utils.handlers.ScrollHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

import static com.tanishisherewith.dynamichud.helpers.ColorHelper.DARK_GREEN;
import static com.tanishisherewith.dynamichud.helpers.ColorHelper.DARK_RED;

/**
 * This is one of the Skins provided by DynamicHUD featuring the minecraft-like style rendering.
 * It runs on a separate screen and provides more complex features like scrolling and larger dimension.
 * It tries to imitate the minecraft look and provides various form of panel shades {@link PanelColor}
 */
public class MinecraftSkin extends Skin implements GroupableSkin {
    public static final WidgetSprites TEXTURES = new WidgetSprites(
            Identifier.withDefaultNamespace("widget/button"),
            Identifier.withDefaultNamespace("widget/button_disabled"),
            Identifier.withDefaultNamespace("widget/button_highlighted")
    );
    public static final Identifier DEFAULT_BACKGROUND_PANEL = Identifier.withDefaultNamespace("textures/gui/demo_background.png");
    public static final Identifier SCROLLER_TEXTURE = Identifier.withDefaultNamespace("widget/scroller");
    public static final Identifier SCROLL_BAR_BACKGROUND = Identifier.withDefaultNamespace("widget/scroller_background");
    public static final Identifier GROUP_BACKGROUND = Identifier.fromNamespaceAndPath(DynamicHUD.MOD_ID, "textures/minecraftskin/group_panel.png");

    private final Identifier BACKGROUND_PANEL;

    public static final int DEFAULT_SCROLLBAR_WIDTH = 8;
    public static final int DEFAULT_PANEL_WIDTH = 248;
    public static final int DEFAULT_PANEL_HEIGHT = 165;
    private int panelWidth;
    private int panelHeight;
    private int groupPanelWidth = 60; // Width for the group panel

    private int imageX, imageY;
    private final ScrollHandler scrollHandler;

    private List<OptionGroup> optionGroups;
    private OptionGroup selectedGroup;

    private PanelColor panelColor;
    private final ScrollHandler groupScrollHandler;
    private final IntSupplier groupPanelX = () -> imageX - groupPanelWidth - 15;

    private final EditBox searchBox;
    private String searchQuery = "";
    private int searchBoxWidth = 0;
    private int searchBoxHeight = 14;
    private int searchBoxX = 0;
    private int searchBoxY = 0;

    public MinecraftSkin(PanelColor color) {
        super();
        this.panelColor = color;
        addRenderer(BooleanOption.class, MinecraftBooleanRenderer::new);
        addRenderer(DoubleOption.class, MinecraftDoubleRenderer::new);
        addRenderer(CycleOption.class, MinecraftCycleRenderer::new);
        addRenderer(SubMenuOption.class, MinecraftSubMenuRenderer::new);
        addRenderer(RunnableOption.class, MinecraftRunnableRenderer::new);
        addRenderer(ColorOption.class, MinecraftColorOptionRenderer::new);

        // if in case of different texture support.
        this.panelHeight = DEFAULT_PANEL_HEIGHT;
        this.panelWidth = DEFAULT_PANEL_WIDTH;
        this.BACKGROUND_PANEL = DEFAULT_BACKGROUND_PANEL;
        this.scrollHandler = new ScrollHandler();
        this.groupScrollHandler = new ScrollHandler();

        setCreateNewScreen(true);

        searchBox = new EditBox(mc.font, searchBoxX, searchBoxY, searchBoxWidth, searchBoxHeight, Component.empty());
        searchBox.setResponder(query -> {
            searchQuery = query;
            scrollHandler.setScrollOffset(0);
        });
        searchBox.setHint(Component.literal("Search..."));
    }

    @Override
    public List<Option<?>> getOptions(ContextMenu<?> menu) {
        if(searchQuery != null && !searchQuery.isEmpty()) {
            return Util.getSearchResults(searchQuery,-1, contextMenu.getOptions());
        }
        return super.getOptions(menu);
    }

    private void enableContextMenuScissor(GuiGraphics graphics) {
        DrawHelper.enableScissor(0, imageY + 3, mc.getWindow().getGuiScaledWidth(), panelHeight - 8,graphics);
    }

    private void createGroups() {
        OptionGroup generalGroup = new OptionGroup(Component.literal("General"));
        for (Option<?> option : getOptions(contextMenu)) {
            if (option instanceof OptionGroup og) {
                optionGroups.add(og);
                og.setExpanded(false);
            } else {
                generalGroup.addOption(option);
            }
        }
        optionGroups.addFirst(generalGroup);
    }

    private void initOptionGroups() {
        if (this.optionGroups == null) {
            this.optionGroups = new ArrayList<>();
            createGroups();
            selectedGroup = optionGroups.getFirst(); // Default to the first group
            selectedGroup.setExpanded(true);
            scrollHandler.updateScrollPosition(0);
        }
    }

    private int getOptionHeight(Option<?> option) {
        if (option instanceof SubMenuOption) return 20;
        if (option instanceof ColorOption colorOption) {
            int baseHeight = 25;
            if (colorOption.getColorGradient().shouldDisplay()) {
                int colorGradientHeight = colorOption.getColorGradient().getBoxSize() + 10 + colorOption.getColorGradient().getGradientSlider().getHeight();
                return baseHeight + colorGradientHeight;
            }
            return baseHeight;
        }
        return 25;
    }

    @Override
    public void renderContextMenu(GuiGraphics graphics, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        this.contextMenu = contextMenu;

        initOptionGroups();

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        contextMenu.set(centerX, centerY, 0);

        // Calculate the top-left corner of the image
        imageX = (screenWidth - panelWidth + 25) / 2;
        imageY = (screenHeight - panelHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND_PANEL, imageX, imageY, 0, 0, panelWidth, panelHeight, 256, 254,panelColor.getColor());

        drawSingularButton(graphics, "X", mouseX, mouseY, imageX + 3, imageY + 3, 14, 14);

        //Up and down arrows near the group panel

        int size = (int) (groupPanelWidth * 0.5f);
        drawSingularButton(graphics, "^", mouseX, mouseY, groupPanelX.getAsInt() + groupPanelWidth / 2 - size / 2, imageY - 14, size, 14, groupScrollHandler.isOffsetWithinBounds(-10));
        drawSingularButton(graphics, "v", mouseX, mouseY, groupPanelX.getAsInt() + groupPanelWidth / 2 - size / 2, imageY + panelHeight - 2, size, 14, groupScrollHandler.isOffsetWithinBounds(10));
        // graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(true, isMouseOver(mouseX, mouseY, imageX + 3, imageY + 3, 14, 14)), imageX + 3, imageY + 3, 14, 14);
        // graphics.drawString(mc.font, "X", imageX + 10 - mc.font.width("X") / 2, imageY + 6, -1, true);

        searchBoxWidth = panelWidth/2;
        searchBoxHeight = mc.font.lineHeight + 5;
        searchBoxX = imageX + (panelWidth - searchBoxWidth) / 2;
        searchBoxY = imageY - 18;

        renderSearchBox(graphics, mouseX, mouseY);

        this.enableContextMenuScissor(graphics);

        contextMenu.setWidth(panelWidth - 4);
        contextMenu.y = imageY;

        renderOptionGroups(graphics, mouseX, mouseY);
        renderSelectedGroupOptions(graphics, mouseX, mouseY);

        contextMenu.setHeight(getContentHeight() + 15);

        drawScrollbar(graphics);

        scrollHandler.updateScrollOffset(getMaxScrollOffset());

        // Disable scissor after rendering
        DrawHelper.disableScissor(graphics);
    }

    private void renderSearchBox(GuiGraphics graphics, int mouseX, int mouseY) {
        searchBox.setX(searchBoxX);
        searchBox.setY(searchBoxY);
        searchBox.setWidth(searchBoxWidth);
        searchBox.setHeight(searchBoxHeight);

        searchBox.render(graphics, mouseX, mouseY, mc.getDeltaTracker().getGameTimeDeltaTicks());

        String icon = "\uD83D\uDD0D";
        int iconX = searchBoxX - 2 - mc.font.width(icon);
        int iconY = searchBoxY + (searchBoxHeight - mc.font.lineHeight) / 2 + 1;
        int iconColor = searchBox.isFocused()? Color.WHITE.getRGB() : 0x80FFFFFF;
        //DrawHelper.drawRectangle(graphics,iconX - 2, iconY - 1, mc.font.width(icon) + 3, mc.font.lineHeight + 2,Color.BLACK.getRGB());
        //DrawHelper.drawOutlineBox(graphics,iconX - 2, iconY - 1, mc.font.width(icon) + 3,mc.font.lineHeight + 2,1f,Color.GRAY.getRGB());
        graphics.drawString(mc.font, icon, iconX, iconY, iconColor, false);
    }

    public void drawSingularButton(GuiGraphics graphics, String Component, int mouseX, int mouseY, int x, int y, int width, int height, boolean enabled) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(enabled, isMouseOver(mouseX, mouseY, x, y, width, height)), x, y, width, height);
        graphics.drawString(mc.font, Component, x + width / 2 - mc.font.width(Component) / 2, y + mc.font.lineHeight / 2 - 1, Color.WHITE.getRGB(), true);
    }

    public void drawSingularButton(GuiGraphics graphics, String Component, int mouseX, int mouseY, int x, int y, int width, int height) {
        this.drawSingularButton(graphics, Component, mouseX, mouseY, x, y, width, height, true);
    }

    private void renderOptionGroups(GuiGraphics graphics, int mouseX, int mouseY) {
        int groupX = groupPanelX.getAsInt();
        int groupY = imageY;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GROUP_BACKGROUND, groupX - 10, groupY + 2, 0,0,groupPanelWidth + 20, panelHeight - 2,  groupPanelWidth + 20, panelHeight - 3);

        int yOffset = groupY + 12 - groupScrollHandler.getScrollOffset();
        for (OptionGroup group : optionGroups) {
            if (yOffset >= groupY + 12 && yOffset <= groupY + panelHeight - 15) {
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(!group.isExpanded(), isMouseOver(mouseX, mouseY, groupX, yOffset, groupPanelWidth, 20)), groupX, yOffset, groupPanelWidth, 20);

                DrawHelper.drawScrollableText(graphics, mc.font, group.getName(), groupX + groupPanelWidth / 2, groupX + 2, yOffset, groupX + groupPanelWidth - 2, yOffset + 20, -1);

                //Scrollable Component uses scissor, so we need to enable the context menu scissor again
                this.enableContextMenuScissor(graphics);

                yOffset += 20; // Space for the header
            }
            yOffset += 10; // Space for the group
        }

        groupScrollHandler.updateScrollOffset(yOffset - groupY - 12 + groupScrollHandler.getScrollOffset() - panelHeight);
    }

    private int renderSelectedGroupOptions(GuiGraphics graphics, int mouseX, int mouseY) {
        int yOffset = imageY + 12 - scrollHandler.getScrollOffset();
        int targetWidth = panelWidth - 25;

        List<Option<?>> optionsToRender;
        if (searchQuery != null && !searchQuery.isEmpty()) {
            optionsToRender = getOptions(contextMenu);
        } else {
            optionsToRender = selectedGroup.getGroupOptions();
        }

        for (Option<?> option : optionsToRender) {
            if (!option.shouldRender()) continue;

            option.setHeight(getOptionHeight(option));
            yOffset = contextMenu.getLayoutEngine().layoutOption(option, imageX + 4, yOffset, targetWidth);

            if (option.getY() >= imageY - option.getHeight() && option.getY() <= imageY + option.getHeight() + panelHeight) {
                option.render(graphics, option.getX(), option.getY(), mouseX, mouseY);
            }
        }
        return yOffset;
    }

    private void drawScrollbar(GuiGraphics graphics) {
        if (getMaxScrollOffset() > 0) {
            int scrollbarX = imageX + panelWidth + 10;
            int scrollbarY = imageY;
            double ratio = (double) panelHeight / getContentHeight();
            double handleHeight = panelHeight * ratio;
            int handleY = (int) (scrollbarY + (panelHeight - handleHeight) * ((double) scrollHandler.getScrollOffset() / getMaxScrollOffset()));

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL_BAR_BACKGROUND, scrollbarX, scrollbarY, DEFAULT_SCROLLBAR_WIDTH, panelHeight);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLER_TEXTURE, scrollbarX, handleY, DEFAULT_SCROLLBAR_WIDTH, (int) handleHeight);
        }
    }

    private int getMaxScrollOffset() {
        return getContentHeight() - panelHeight + 10;
    }

    private int getContentHeight() {
        return selectedGroup.getHeightOfOptions() + 10;
    }

    @Override
    public void mouseScrolled(ContextMenu<?> menu, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isMouseOver(mouseX, mouseY, imageX, imageY, panelWidth, panelHeight)) {
            scrollHandler.mouseScrolled(verticalAmount);
        }
        if (isMouseOver(mouseX, mouseY, groupPanelX.getAsInt() - 10, imageY, groupPanelWidth + 10, panelHeight)) {
            groupScrollHandler.mouseScrolled(verticalAmount);
        }
    }

    @Override
    public boolean mouseClicked(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0));
        if (searchBox.mouseClicked(event, false)) {
            searchBox.setFocused(true);
            return true;
        } else{
            searchBox.setFocused(false);
        }


        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isMouseOver(mouseX, mouseY, imageX + 3, imageY + 3, 14, 14)) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F));

                contextMenu.close();
                scrollHandler.stopDragging();
                return true;
            }
            int size = (int) (groupPanelWidth * 0.5f);
            //Up and down button
            if (groupScrollHandler.isOffsetWithinBounds(-10) && isMouseOver(mouseX, mouseY, groupPanelX.getAsInt() + (double) groupPanelWidth / 2 - size / 2, imageY - 14, size, 14)) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F));
                groupScrollHandler.addOffset(-10);
            }
            if (groupScrollHandler.isOffsetWithinBounds(10) && isMouseOver(mouseX, mouseY, groupPanelX.getAsInt() + (double) groupPanelWidth / 2 - size / 2, imageY + panelHeight - 2, size, 14)) {
                mc.getSoundManager().play(SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F));
                groupScrollHandler.addOffset(10);
            }

            if (isMouseOver(mouseX, mouseY, imageX + panelWidth + 10, imageY, 10, panelHeight)) {
                scrollHandler.startDragging(mouseY);
                return true;
            }
            int groupX = groupPanelX.getAsInt();
            int groupY = imageY;

            int yOffset = groupY + 10 - groupScrollHandler.getScrollOffset();
            for (OptionGroup group : optionGroups) {
                if (yOffset >= groupY && yOffset <= groupY + panelHeight) {
                    // Handle click to select the group
                    if (isMouseOver(mouseX, mouseY, groupX + 10, yOffset, groupPanelWidth - 20, 20)) {
                        selectedGroup.setExpanded(false);
                        selectedGroup = group;
                        selectedGroup.setExpanded(true);
                    }
                    yOffset += 20;
                }
                yOffset += 10;
            }
        }

        return super.mouseClicked(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            scrollHandler.stopDragging();
            groupScrollHandler.stopDragging();
        }
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0));
        searchBox.mouseReleased(event);
        return super.mouseReleased(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(ContextMenu<?> menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        MouseButtonEvent event = new MouseButtonEvent(mouseX, mouseY, new MouseButtonInfo(button, 0));
        searchBox.mouseDragged(event, deltaX, deltaY);

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isMouseOver(mouseX, mouseY, imageX + panelWidth + 5, imageY - 5, DEFAULT_SCROLLBAR_WIDTH + 5, panelHeight + 10)) {
                scrollHandler.updateScrollPosition(mouseY);
            }
            return true;
        }
        return super.mouseDragged(menu, mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void keyPressed(ContextMenu<?> menu, int key, int scanCode, int modifiers) {
        searchBox.keyPressed(new KeyEvent(key, scanCode, modifiers));
        super.keyPressed(menu, key, scanCode, modifiers);
    }

    @Override
    public void charTyped(ContextMenu<?> menu, char c, int modifiers) {
        searchBox.charTyped(new CharacterEvent(c, modifiers));
        super.charTyped(menu, c, modifiers);
    }

    @Override
    public LayoutEngine.Offset getGroupIndent() {
        return LayoutEngine.Offset.zero();
    }

    public void setPanelColor(PanelColor panelColor) {
        this.panelColor = panelColor;
    }

    public PanelColor getPanelColor() {
        return panelColor;
    }

    public int getPanelHeight() {
        return panelHeight;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public int getImageX() {
        return imageX;
    }

    public int getImageY() {
        return imageY;
    }

    /**
     * Group rendering handled already
     */
    @Override
    public void renderGroup(GuiGraphics graphics, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY) {
    }

    public enum PanelColor {
        COFFEE_BROWN(0.6f, 0.3f, 0.1f, 1.0f),
        CREAMY(1.0f, 0.9f, 0.8f, 1.0f),
        DARK_PANEL(0.2f, 0.2f, 0.2f, 1.0f),
        FOREST_GREEN(0.0f, 0.6f, 0.2f, 1.0f),
        GOLDEN_YELLOW(1.0f, 0.8f, 0.0f, 1.0f),
        LAVENDER(0.8f, 0.6f, 1.0f, 1.0f),
        LIGHT_BLUE(0.6f, 0.8f, 1.0f, 1.0f),
        LIME_GREEN(0.7f, 1.0f, 0.3f, 1.0f),
        MIDNIGHT_PURPLE(0.3f, 0.0f, 0.5f, 1.0f),
        OCEAN_BLUE(0.0f, 0.5f, 1.0f, 1.0f),
        ROSE_PINK(1.0f, 0.4f, 0.6f, 1.0f),
        SKY_BLUE(0.5f, 0.8f, 1.0f, 1.0f),
        SOFT_GREEN(0.6f, 1.0f, 0.6f, 1.0f),
        SUNSET_ORANGE(1.0f, 0.5f, 0.0f, 1.0f),
        WARM_YELLOW(1.0f, 1.0f, 0.6f, 1.0f),
        CUSTOM(0.0f, 0.0f, 0.0f, 0.0f); /// PlaceHolder for custom colors

        private float red;
        private float green;
        private float blue;
        private float alpha;

        PanelColor(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        public static PanelColor custom(float red, float green, float blue, float alpha) {
            PanelColor custom = CUSTOM;
            custom.red = red;
            custom.green = green;
            custom.blue = blue;
            custom.alpha = alpha;
            return custom;
        }

        public int getColor() {
            return new Color(red,green,blue,alpha).getRGB();
        }
    }

    public class MinecraftBooleanRenderer implements SkinRenderer<BooleanOption> {
        @Override
        public void render(GuiGraphics graphics, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            graphics.drawString(mc.font, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 75, y);

            int width = 50;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, width, 20);

            Component Component = option.getBooleanType().getText(option.value);
            int color = option.value ? Color.GREEN.getRGB() : Color.RED.getRGB();
            graphics.drawString(mc.font, Component, (int) (option.getX() + (width / 2.0f) - (mc.font.width(Component) / 2.0f)), y + 5, color, true);


            //Widths don't matter in this skin
            option.setWidth(width);
        }
    }

    public class MinecraftColorOptionRenderer implements SkinRenderer<ColorOption> {
        private ValueAnimation scaleAnimation;
        private float scale = 0.0f;

        @Override
        public void init(ColorOption option) {
            this.scaleAnimation = new ValueAnimation(new AnimationProperty<>() {
                @Override
                public Float get() {
                    return scale;
                }

                @Override
                public void set(Float value) {
                    scale = value;
                }
            }, 0.0f, 1.0f);
            scaleAnimation.easing(EasingType.EASE_OUT_BACK);
            scaleAnimation.duration(200);
            this.scaleAnimation.onComplete(() -> {
                if (scale <= 0.0f) {
                    option.getColorGradient().close();
                }
            });
        }

        @Override
        public void render(GuiGraphics graphics, ColorOption option, int x, int y, int mouseX, int mouseY) {
            if (scaleAnimation != null) {
                scaleAnimation.update();
            }

            graphics.drawString(mc.font, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 45, y);

            int width = 20;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(!option.isVisible, option.isMouseOver(mouseX, mouseY)), option.getX(), y, width, 20);

            int shadowOpacity = Math.min(option.value.getAlpha(), 45);
            DrawHelper.drawRectangleWithShadowBadWay(graphics,
                    option.getX() + 4,
                    y + 4,
                    width - 8,
                    20 - 8,
                    option.value.getRGB(),
                    shadowOpacity,
                    1,
                    1);

            option.setWidth(width);

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                DrawHelper.disableScissor(graphics);
            }

            int colorGradientWidth = option.getColorGradient().getBoxSize() + option.getColorGradient().getAlphaSlider().getWidth() + option.getColorGradient().getColorPickerButton().getWidth();
            int pickerX = x + panelWidth / 2 - colorGradientWidth / 2;
            int pickerY = y + 12;

            if (scale > 0.0f) {
                DrawHelper.scaleAndPosition(graphics.pose(), pickerX + colorGradientWidth / 2.0f, pickerY, scale);
                option.getColorGradient().render(graphics, pickerX, pickerY, mouseX, mouseY);
                DrawHelper.stopScaling(graphics.pose());
            }

            int baseHeight = 25;
            if (option.getColorGradient().shouldDisplay() || (scaleAnimation != null && !scaleAnimation.isFinished())) {
                int colorGradientHeight = option.getColorGradient().getBoxSize() + 10 + option.getColorGradient().getGradientSlider().getHeight();
                option.setHeight(baseHeight + (int) (colorGradientHeight * scale));
            } else {
                option.setHeight(baseHeight);
            }

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                DrawHelper.enableScissor(imageX, imageY + 2, panelWidth, panelHeight - 4, graphics);
            }
        }

        @Override
        public boolean mouseClicked(ColorOption option, double mouseX, double mouseY, int button) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && isMouseOver(mouseX, mouseY, option.getX(), option.getY(), 20, 20)) {
                boolean isOpening = !option.getColorGradient().shouldDisplay();
                scaleAnimation.startValue(scale);
                if (isOpening) {
                    option.getColorGradient().display();
                    scaleAnimation.endValue(1.0f);
                } else {
                    scaleAnimation.endValue(0.0f);
                }
                scaleAnimation.start();
                return true;
            }

            if (option.getColorGradient().shouldDisplay()) {
                option.getColorGradient().mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }

        @Override
        public boolean mouseDragged(ColorOption option, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (option.getColorGradient().shouldDisplay()) {
                option.getColorGradient().mouseDragged(mouseX, mouseY, button);
            }
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseReleased(ColorOption option, double mouseX, double mouseY, int button) {
            if (option.getColorGradient().shouldDisplay()) {
                option.getColorGradient().mouseReleased(mouseX, mouseY, button);
            }
            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }
    }

    public class MinecraftDoubleRenderer implements SkinRenderer<DoubleOption> {
        private static final Identifier TEXTURE = Identifier.withDefaultNamespace("widget/slider");
        private static final Identifier HIGHLIGHTED_TEXTURE = Identifier.withDefaultNamespace("widget/slider_highlighted");
        private static final Identifier HANDLE_TEXTURE = Identifier.withDefaultNamespace("widget/slider_handle");
        private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = Identifier.withDefaultNamespace("widget/slider_handle_highlighted");

        @Override
        public void render(GuiGraphics graphics, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            graphics.drawString(mc.font, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setWidth(panelWidth - 150);
            option.setPosition(x + panelWidth - 122, y);

            double sliderX = option.getX() + ((option.value - option.minValue) / (option.maxValue - option.minValue)) * (option.getWidth() - 8);
            boolean isMouseOverHandle = isMouseOver(mouseX, mouseY, sliderX, y, 10, 20);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, option.isMouseOver(mouseX, mouseY) ? HIGHLIGHTED_TEXTURE : TEXTURE, option.getX(), y, option.getWidth(), 20);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, isMouseOverHandle ? HANDLE_HIGHLIGHTED_TEXTURE : HANDLE_TEXTURE, (int) Math.round(sliderX), y, 8, 20);

            int decimalPlaces = String.valueOf(option.step).split("\\.")[1].length();

            // Format option.value to the determined number of decimal places
            String formattedValue = String.format("%." + decimalPlaces + "f", option.value);
            graphics.drawCenteredString(mc.font, formattedValue, option.getX() + option.getWidth() / 2, y + Math.round((float) mc.font.lineHeight /2), -1);
        }
    }

    public class MinecraftCycleRenderer<E> implements SkinRenderer<CycleOption<E>> {
        private int maxWidth = 50;

        private void calculateMaxWidth(CycleOption<E> option) {
            for (E listValues : option.getValues()) {
                int width = mc.font.width(listValues.toString()) + 5;
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
        }

        @Override
        public void render(GuiGraphics graphics, CycleOption<E> option, int x, int y, int mouseX, int mouseY) {
            calculateMaxWidth(option);
            option.setWidth(maxWidth);

            graphics.drawString(mc.font, option.name.copy().append(": "), x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - maxWidth - 25, y);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, maxWidth, 20);
            String Component = option.get().toString();
            graphics.drawString(mc.font, Component, option.getX() + maxWidth / 2 - mc.font.width(Component) / 2, y + 5, Color.CYAN.getRGB(), true);
        }
    }

    public class MinecraftSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(GuiGraphics graphics, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            option.setWidth(30);

            graphics.drawString(mc.font, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 55, y);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, option.getWidth(), 20);
            String Component = "Open";
            graphics.drawString(mc.font, Component, option.getX() + option.getWidth() / 2 - mc.font.width(Component) / 2, y + 5, Color.YELLOW.getRGB(), true);

            option.getSubMenu().render(graphics, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
        }
    }

    public class MinecraftRunnableRenderer implements SkinRenderer<RunnableOption> {

        @Override
        public void render(GuiGraphics graphics, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            option.setWidth(26);

            graphics.drawString(mc.font, option.name.copy().append(": "), x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 51, y);

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TEXTURES.get(!option.value, option.isMouseOver(mouseX, mouseY)), option.getX(), y, option.getWidth(), 20);
            graphics.drawString(mc.font, "Run", option.getX() + option.getWidth() / 2 - mc.font.width("Run") / 2, y + 5, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);
        }
    }

    @Override
    public Skin clone() {
        return new MinecraftSkin(panelColor);
    }
}