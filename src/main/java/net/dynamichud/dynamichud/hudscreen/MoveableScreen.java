package net.dynamichud.dynamichud.hudscreen;

import net.dynamichud.dynamichud.Util.ColorPicker.ColorGradientPicker;
import net.dynamichud.dynamichud.Util.ContextMenu.ContextMenu;
import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.ItemWidget.ItemWidget;
import net.dynamichud.dynamichud.Widget.SliderWidget.SliderWidgetBuilder;
import net.dynamichud.dynamichud.Widget.TextWidget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.helpers.ColorHelper;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class MoveableScreen extends AbstractMoveableScreen {
    /**
     * Constructs a AbstractMoveableScreen object.
     *
     * @param title
     * @param dynamicutil The DynamicUtil instance used by this screen
     */
    public MoveableScreen(Text title, DynamicUtil dynamicutil) {
        super(title, dynamicutil);
        setGridSize(1);
        setShouldPause(false);
        setShouldBeAffectedByResize(false);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        MinecraftClient.getInstance().textRenderer.drawWithShadow(matrices, "Editor Screen", MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f - textRenderer.getWidth("Editor Screen") / 2f, 5, ColorHelper.ColorToInt(Color.WHITE), false);
    }

    @Override
    protected boolean handleRightClickOnWidget(Widget widget) {
        selectedWidget = widget;
        sliderWigdet = widget;
        int x = selectedWidget.getX();
        int y = selectedWidget.getY();
        // Show context menu
        menu(widget, x, y);
        return true;
    }

    @Override
    protected void menu(Widget widget, int x, int y) {
        contextMenu = new ContextMenu(mc, x, y + widget.getHeight() + 5, selectedWidget);
        if (widget instanceof ArmorWidget armorWidget) {
            Slider = null;
            contextMenu.setHeightfromwidget(15);
            contextMenu.setPadding(5);
            contextMenu.addEnumCycleOption("Position", TextureHelper.Position.values(), () -> armorWidget.currentTextPosition[0], newPosition -> {
                armorWidget.currentTextPosition[0] = newPosition;
            });
        }
        if (widget instanceof ItemWidget) {
            contextMenu = null;
            Slider = null;
            colorPicker = null;
            return;
        }
        if (widget instanceof TextWidget textWidget) {
            contextMenu.setHeightfromwidget(2);
            contextMenu.setPadding(5);
            contextMenu.addOption("Shadow", () -> {
                textWidget.setShadow(!textWidget.hasShadow());
            });
            contextMenu.addOption("Rainbow", () -> {
                textWidget.setRainbow(!textWidget.hasRainbow());
            });
            contextMenu.addOption("Vertical Rainbow", () -> {
                textWidget.setVerticalRainbow(!textWidget.hasVerticalRainbow());
            });
            if (!textWidget.getDataText().trim().isEmpty()) {
                contextMenu.addOption("TextColor", () -> {
                    textWidget.toggleTextColorOption();

                    colorPicker = null;

                    if (textWidget.isTextcolorOptionEnabled())
                        colorPicker = new ColorGradientPicker(mc, x + 110, y + widget.getHeight() + 5, textWidget.getTextcolor(), textWidget::setTextColor, 50, 100, selectedWidget);
                });
            }
            if (!textWidget.getDataText().trim().isEmpty()) {
                contextMenu.addOption("DataColor", () -> {
                    textWidget.toggleDataColorOption();

                    colorPicker = null;

                    if (textWidget.isDatacolorOptionEnabled())
                        colorPicker = new ColorGradientPicker(mc, x + 110, y + widget.getHeight() + 5, textWidget.getDatacolor(), textWidget::setDataColor, 50, 100, selectedWidget);
                });
            }
            Slider = new SliderWidgetBuilder(client)
                    .setX(x)
                    .setY(y)
                    .setWidth(105)
                    .setHeight(15)
                    .setLabel("Rainbow Speed")
                    .setValue(textWidget.getRainbowSpeed())
                    .setMinValue(5f)
                    .setMaxValue(25.0f)
                    .setSelectedWidget(selectedWidget)
                    .build();
        }
    }
}


