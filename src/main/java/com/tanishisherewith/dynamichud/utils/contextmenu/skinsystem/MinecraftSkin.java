package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutContext;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import com.tanishisherewith.dynamichud.utils.handlers.ScrollHandler;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * This is one of the Skins provided by DynamicHUD featuring the minecraft-like style rendering.
 * It runs on a separate screen and provides more complex features like scrolling and larger dimension.
 * It tries to imitate the minecraft look and provides various form of panel shades {@link PanelColor}
 */
public class MinecraftSkin extends Skin implements GroupableSkin {
    public static final ButtonTextures TEXTURES = new ButtonTextures(
            Identifier.ofVanilla("widget/button"),
            Identifier.ofVanilla("widget/button_disabled"),
            Identifier.ofVanilla("widget/button_highlighted")
    );
    private static final int DEFAULT_SCROLLBAR_WIDTH = 8;
    private static final int DEFAULT_PANEL_WIDTH = 248;
    private static final int DEFAULT_PANEL_HEIGHT = 165;
    private static final Identifier DEFAULT_BACKGROUND_PANEL = Identifier.ofVanilla("textures/gui/demo_background.png");
    private static final Identifier SCROLLER_TEXTURE = Identifier.ofVanilla("widget/scroller");
    private static final Identifier SCROLL_BAR_BACKGROUND = Identifier.ofVanilla("widget/scroller_background");
    private static final Identifier GROUP_BACKGROUND = Identifier.of(DynamicHUD.MOD_ID, "textures/minecraftskin/group_panel.png");

    private final Identifier BACKGROUND_PANEL;
    private final int panelWidth;
    private final int panelHeight;
    private final PanelColor panelColor;

    private int imageX, imageY;
    private final ScrollHandler scrollHandler;

    private List<OptionGroup> optionGroups;
    private OptionGroup selectedGroup;
    private final ScrollHandler groupScrollHandler;
    private final int groupPanelWidth = 60; // Width for the group panel
    private final IntSupplier groupPanelX = () -> imageX - groupPanelWidth - 15;

    public MinecraftSkin(PanelColor color) {
        super();
        this.panelColor = color;
        addRenderer(BooleanOption.class, MinecraftBooleanRenderer::new);
        addRenderer(DoubleOption.class, MinecraftDoubleRenderer::new);
        addRenderer(EnumOption.class, MinecraftEnumRenderer::new);
        addRenderer(ListOption.class, MinecraftListRenderer::new);
        addRenderer(SubMenuOption.class, MinecraftSubMenuRenderer::new);
        addRenderer(RunnableOption.class, MinecraftRunnableRenderer::new);
        addRenderer(ColorOption.class, MinecraftColorOptionRenderer::new);

        this.panelHeight = DEFAULT_PANEL_HEIGHT;
        this.panelWidth = DEFAULT_PANEL_WIDTH;
        this.BACKGROUND_PANEL = DEFAULT_BACKGROUND_PANEL;
        this.scrollHandler = new ScrollHandler();
        this.groupScrollHandler = new ScrollHandler();

        setCreateNewScreen(true);
    }

    private void enableContextMenuScissor() {
        DrawHelper.enableScissor(0, imageY + 3, mc.getWindow().getScaledWidth(), panelHeight - 8);
    }

    private void createGroups() {
        OptionGroup generalGroup = new OptionGroup(Text.of("General"));
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

    @Override
    public void renderContextMenu(DrawContext drawContext, ContextMenu<?> contextMenu, int mouseX, int mouseY) {
        this.contextMenu = contextMenu;

        initOptionGroups();

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        contextMenu.set(centerX, centerY, 0);

        // Calculate the top-left corner of the image
        imageX = (screenWidth - panelWidth + 25) / 2;
        imageY = (screenHeight - panelHeight) / 2;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        panelColor.applyColor();

        drawContext.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_PANEL, imageX, imageY, 0, 0, panelWidth, panelHeight, 256, 254);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        drawSingularButton(drawContext, "X", mouseX, mouseY, imageX + 3, imageY + 3, 14, 14);

        //Up and down arrows near the group panel
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        int size = (int) (groupPanelWidth * 0.5f);
        drawSingularButton(drawContext, "^", mouseX, mouseY, groupPanelX.getAsInt() + groupPanelWidth / 2 - size / 2, imageY - 14, size, 14, groupScrollHandler.isOffsetWithinBounds(-10));
        drawSingularButton(drawContext, "v", mouseX, mouseY, groupPanelX.getAsInt() + groupPanelWidth / 2 - size / 2, imageY + panelHeight - 2, size, 14, groupScrollHandler.isOffsetWithinBounds(10));
        drawContext.draw();
        // drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(true, isMouseOver(mouseX, mouseY, imageX + 3, imageY + 3, 14, 14)), imageX + 3, imageY + 3, 14, 14);
        // drawContext.drawText(mc.textRenderer, "X", imageX + 10 - mc.textRenderer.getWidth("X") / 2, imageY + 6, -1, true);

        this.enableContextMenuScissor();

        contextMenu.setWidth(panelWidth - 4);
        contextMenu.y = imageY;

        renderOptionGroups(drawContext, mouseX, mouseY);
        renderSelectedGroupOptions(drawContext, mouseX, mouseY);

        contextMenu.setHeight(getContentHeight() + 15);

        drawScrollbar(drawContext);

        scrollHandler.updateScrollOffset(getMaxScrollOffset());

        // Disable scissor after rendering
        DrawHelper.disableScissor();
    }

    public void drawSingularButton(DrawContext drawContext, String text, int mouseX, int mouseY, int x, int y, int width, int height, boolean enabled) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(enabled, isMouseOver(mouseX, mouseY, x, y, width, height)), x, y, width, height);
        drawContext.drawText(mc.textRenderer, text, x + width / 2 - mc.textRenderer.getWidth(text) / 2, y + mc.textRenderer.fontHeight / 2 - 1, Color.WHITE.getRGB(), true);
    }

    public void drawSingularButton(DrawContext drawContext, String text, int mouseX, int mouseY, int x, int y, int width, int height) {
        this.drawSingularButton(drawContext, text, mouseX, mouseY, x, y, width, height, true);
    }

    private void renderOptionGroups(DrawContext drawContext, int mouseX, int mouseY) {
        int groupX = groupPanelX.getAsInt();
        int groupY = imageY;

        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawContext.drawTexture(RenderLayer::getGuiTextured, GROUP_BACKGROUND, groupX - 10, groupY + 2, 0,0,groupPanelWidth + 20, panelHeight,  80, 158);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        int yOffset = groupY + 12 - groupScrollHandler.getScrollOffset();
        for (OptionGroup group : optionGroups) {
            if (yOffset >= groupY + 12 && yOffset <= groupY + panelHeight - 15) {
                drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(!group.isExpanded(), isMouseOver(mouseX, mouseY, groupX, yOffset, groupPanelWidth, 20)), groupX, yOffset, groupPanelWidth, 20);

                DrawHelper.drawScrollableText(drawContext, mc.textRenderer, group.getName(), groupX + groupPanelWidth / 2, groupX + 2, yOffset, groupX + groupPanelWidth - 2, yOffset + 20, -1);

                //Scrollable text uses scissor, so we need to enable the context menu scissor again
                this.enableContextMenuScissor();

                yOffset += 20; // Space for the header
            }
            yOffset += 10; // Space for the group
        }

        groupScrollHandler.updateScrollOffset(yOffset - groupY - 12 + groupScrollHandler.getScrollOffset() - panelHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    private int renderSelectedGroupOptions(DrawContext drawContext, int mouseX, int mouseY) {
        int yOffset = imageY + 12 - scrollHandler.getScrollOffset();
        for (Option<?> option : selectedGroup.getGroupOptions()) {
            if (!option.shouldRender()) continue;

            if (yOffset >= imageY - option.getHeight() && yOffset <= imageY + option.getHeight() + panelHeight) {
                option.render(drawContext, imageX + 4, yOffset, mouseX, mouseY);
            }
            yOffset += option.getHeight() + 1;
        }
        return yOffset;
    }

    private void drawScrollbar(DrawContext drawContext) {
        if (getMaxScrollOffset() > 0) {
            int scrollbarX = imageX + panelWidth + 10;
            int scrollbarY = imageY;
            double ratio = (double) panelHeight / getContentHeight();
            double handleHeight = panelHeight * ratio;
            int handleY = (int) (scrollbarY + (panelHeight - handleHeight) * ((double) scrollHandler.getScrollOffset() / getMaxScrollOffset()));

            RenderSystem.enableBlend();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, SCROLL_BAR_BACKGROUND, scrollbarX, scrollbarY, DEFAULT_SCROLLBAR_WIDTH, panelHeight);
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, SCROLLER_TEXTURE, scrollbarX, handleY, DEFAULT_SCROLLBAR_WIDTH, (int) handleHeight);
            RenderSystem.disableBlend();
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isMouseOver(mouseX, mouseY, imageX + 3, imageY + 3, 14, 14)) {
                mc.getSoundManager().play(PositionedSoundInstance.master(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F));

                contextMenu.close();
                scrollHandler.stopDragging();
                return true;
            }
            int size = (int) (groupPanelWidth * 0.5f);
            //Up and down button
            if (groupScrollHandler.isOffsetWithinBounds(-10) && isMouseOver(mouseX, mouseY, groupPanelX.getAsInt() + (double) groupPanelWidth / 2 - size / 2, imageY - 14, size, 14)) {
                mc.getSoundManager().play(PositionedSoundInstance.master(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F));
                groupScrollHandler.addOffset(-10);
            }
            if (groupScrollHandler.isOffsetWithinBounds(10) && isMouseOver(mouseX, mouseY, groupPanelX.getAsInt() + (double) groupPanelWidth / 2 - size / 2, imageY + panelHeight - 2, size, 14)) {
                mc.getSoundManager().play(PositionedSoundInstance.master(
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
        return super.mouseReleased(menu, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(ContextMenu<?> menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isMouseOver(mouseX, mouseY, imageX + panelWidth + 5, imageY - 5, DEFAULT_SCROLLBAR_WIDTH + 5, panelHeight + 10)) {
                scrollHandler.updateScrollPosition(mouseY);
            }
            return true;
        }
        return super.mouseDragged(menu, mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public LayoutContext.Offset getGroupIndent() {
        return LayoutContext.Offset.zero();
    }

    /**
     * Group rendering handled already
     */
    @Override
    public void renderGroup(DrawContext drawContext, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY) {
    }

    public enum PanelColor {
        COFFEE_BROWN(0.6f, 0.3f, 0.1f, 0.9f),
        CREAMY(1.0f, 0.9f, 0.8f, 0.9f),
        DARK_PANEL(0.2f, 0.2f, 0.2f, 0.9f),
        FOREST_GREEN(0.0f, 0.6f, 0.2f, 0.9f),
        GOLDEN_YELLOW(1.0f, 0.8f, 0.0f, 0.9f),
        LAVENDER(0.8f, 0.6f, 1.0f, 0.9f),
        LIGHT_BLUE(0.6f, 0.8f, 1.0f, 0.9f),
        LIME_GREEN(0.7f, 1.0f, 0.3f, 0.9f),
        MIDNIGHT_PURPLE(0.3f, 0.0f, 0.5f, 0.9f),
        OCEAN_BLUE(0.0f, 0.5f, 1.0f, 0.9f),
        ROSE_PINK(1.0f, 0.4f, 0.6f, 0.9f),
        SKY_BLUE(0.5f, 0.8f, 1.0f, 0.9f),
        SOFT_GREEN(0.6f, 1.0f, 0.6f, 0.9f),
        SUNSET_ORANGE(1.0f, 0.5f, 0.0f, 0.9f),
        WARM_YELLOW(1.0f, 1.0f, 0.6f, 0.9f),
        CUSTOM(0.0f, 0.0f, 0.0f, 0.0f); // PlaceHolder for custom colors

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

        public void applyColor() {
            RenderSystem.setShaderColor(red, green, blue, alpha);
        }
    }

    public class MinecraftBooleanRenderer implements SkinRenderer<BooleanOption> {
        @Override
        public void render(DrawContext drawContext, BooleanOption option, int x, int y, int mouseX, int mouseY) {
            drawContext.drawText(mc.textRenderer, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 75, y);

            int width = 50;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, width, 20);
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Text text = option.getBooleanType().getText(option.value);
            int color = option.value ? Color.GREEN.getRGB() : Color.RED.getRGB();
            drawContext.drawText(mc.textRenderer, text, (int) (option.getX() + (width / 2.0f) - (mc.textRenderer.getWidth(text) / 2.0f)), y + 5, color, true);

            option.setHeight(25);

            //Widths don't matter in this skin
            option.setWidth(width);
        }

        @Override
        public boolean mouseClicked(BooleanOption option, double mouseX, double mouseY, int button) {
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }

    public class MinecraftColorOptionRenderer implements SkinRenderer<ColorOption> {
        @Override
        public void render(DrawContext drawContext, ColorOption option, int x, int y, int mouseX, int mouseY) {
            drawContext.drawText(mc.textRenderer, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 45, y);

            int width = 20;
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(!option.isVisible, option.isMouseOver(mouseX, mouseY)), option.getX(), y, width, 20);
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int shadowOpacity = Math.min(option.value.getAlpha(), 45);
            drawContext.draw();
            DrawHelper.drawRectangleWithShadowBadWay(drawContext.getMatrices().peek().getPositionMatrix(),
                    option.getX() + 4,
                    y + 4,
                    width - 8,
                    20 - 8,
                    option.value.getRGB(),
                    shadowOpacity,
                    1,
                    1);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);


            option.setHeight(25);
            option.setWidth(width);

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                DrawHelper.disableScissor(); // Disable scissor test for the colorpicker
            }
            //TODO: WHAT IS THISSSSS
            int colorGradientWidth = option.getColorGradient().getBoxSize() + option.getColorGradient().getAlphaSlider().getWidth() + option.getColorGradient().getColorPickerButton().getWidth();
            option.getColorGradient().render(drawContext, x + panelWidth / 2 - colorGradientWidth / 2, y + 12, mouseX, mouseY);

            if (option.getColorGradient().shouldDisplay()) {
                int colorGradientHeight = option.getColorGradient().getBoxSize() + 10 + option.getColorGradient().getGradientSlider().getHeight();
                option.setHeight(option.getHeight() + colorGradientHeight);
            }

            if (option.getColorGradient().getColorPickerButton().isPicking()) {
                DrawHelper.enableScissor(imageX, imageY + 2, panelWidth, panelHeight - 4);
            }
        }
    }

    public class MinecraftDoubleRenderer implements SkinRenderer<DoubleOption> {
        private static final Identifier TEXTURE = Identifier.ofVanilla("widget/slider");
        private static final Identifier HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_highlighted");
        private static final Identifier HANDLE_TEXTURE = Identifier.ofVanilla("widget/slider_handle");
        private static final Identifier HANDLE_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/slider_handle_highlighted");

        @Override
        public void init(DoubleOption option) {
            SkinRenderer.super.init(option);
        }

        @Override
        public void render(DrawContext drawContext, DoubleOption option, int x, int y, int mouseX, int mouseY) {
            drawContext.drawText(mc.textRenderer, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setWidth(panelWidth - 150);
            option.setHeight(25);
            option.setPosition(x + panelWidth - 122, y);

            double sliderX = option.getX() + ((option.value - option.minValue) / (option.maxValue - option.minValue)) * (option.getWidth() - 8);
            boolean isMouseOverHandle = isMouseOver(mouseX, mouseY, sliderX, y, 10, 20);

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, option.isMouseOver(mouseX, mouseY) ? HIGHLIGHTED_TEXTURE : TEXTURE, option.getX(), y, option.getWidth(), 20);
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, isMouseOverHandle ? HANDLE_HIGHLIGHTED_TEXTURE : HANDLE_TEXTURE, (int) Math.round(sliderX), y, 8, 20);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            // Determine the number of decimal places in option.step
            int decimalPlaces = String.valueOf(option.step).split("\\.")[1].length();

            // Format option.value to the determined number of decimal places
            String formattedValue = String.format("%." + decimalPlaces + "f", option.value);
            drawContext.drawText(mc.textRenderer, formattedValue, option.getX() + option.getWidth() / 2 - mc.textRenderer.getWidth(formattedValue) / 2, y + 5, 16777215, false);
        }

        @Override
        public boolean mouseClicked(DoubleOption option, double mouseX, double mouseY, int button) {
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }
    }

    public class MinecraftEnumRenderer<E extends Enum<E>> implements SkinRenderer<EnumOption<E>> {
        private int maxWidth = 50;

        private void calculateMaxWidth(EnumOption<E> option) {
            for (E enumConstant : option.getValues()) {
                int width = mc.textRenderer.getWidth(enumConstant.name()) + 5;
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

            option.setPosition(x + panelWidth - maxWidth - 25, y);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, maxWidth, 20);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = option.get().toString();
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            drawContext.drawText(mc.textRenderer, text, option.getX() + maxWidth / 2 - mc.textRenderer.getWidth(text) / 2, y + 5, Color.CYAN.getRGB(), true);
        }
    }

    public class MinecraftListRenderer<E> implements SkinRenderer<ListOption<E>> {
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

            option.setPosition(x + panelWidth - maxWidth - 25, y);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, maxWidth, 20);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = option.get().toString();
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
            drawContext.drawText(mc.textRenderer, text, option.getX() + maxWidth / 2 - mc.textRenderer.getWidth(text) / 2, y + 5, Color.CYAN.getRGB(), true);
        }
    }

    public class MinecraftSubMenuRenderer implements SkinRenderer<SubMenuOption> {
        @Override
        public void render(DrawContext drawContext, SubMenuOption option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(20);
            option.setWidth(30);

            drawContext.drawText(mc.textRenderer, option.name, x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 55, y);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(true, option.isMouseOver(mouseX, mouseY)), option.getX(), y, option.getWidth(), 20);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            String text = "Open";
            drawContext.drawText(mc.textRenderer, text, option.getX() + option.getWidth() / 2 - mc.textRenderer.getWidth(text) / 2, y + 5, Color.YELLOW.getRGB(), true);
            RenderSystem.disableBlend();

            option.getSubMenu().render(drawContext, x + option.getParentMenu().getWidth(), y, mouseX, mouseY);
        }
    }

    public class MinecraftRunnableRenderer implements SkinRenderer<RunnableOption> {
        Color DARK_RED = new Color(116, 0, 0);
        Color DARK_GREEN = new Color(24, 132, 0, 226);

        @Override
        public void render(DrawContext drawContext, RunnableOption option, int x, int y, int mouseX, int mouseY) {
            option.setHeight(25);
            option.setWidth(26);

            drawContext.drawText(mc.textRenderer, option.name + ": ", x + 15, y + 25 / 2 - 5, -1, true);

            option.setPosition(x + panelWidth - 51, y);

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            drawContext.drawGuiTexture(RenderLayer::getGuiTextured, TEXTURES.get(!option.value, option.isMouseOver(mouseX, mouseY)), option.getX(), y, option.getWidth(), 20);
            drawContext.drawText(mc.textRenderer, "Run", option.getX() + option.getWidth() / 2 - mc.textRenderer.getWidth("Run") / 2, y + 5, option.value ? DARK_GREEN.getRGB() : DARK_RED.getRGB(), true);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
            RenderSystem.disableDepthTest();
        }
    }
}