package net.dynamichud.dynamichud.hudscreen;

import net.dynamichud.dynamichud.Util.ColorPicker;
import net.dynamichud.dynamichud.Util.ContextMenu;
import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget.ArmorWidget;
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
                System.out.println("Position: " + newPosition);
                armorWidget.currentTextPosition[0] = newPosition;
            });
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
            contextMenu.addOption("Color", () -> {
                textWidget.toggleColorOption();
                if (textWidget.isColorOptionEnabled())
                    colorPicker = new ColorPicker(mc, mc.getWindow().getScaledWidth() / 2, (mc.getWindow().getScaledHeight() / 2) - 50, textWidget.getColor(), textWidget::setColor);
                else colorPicker = null;
            });
            Slider = new SliderWidgetBuilder(client)
                    .setX(x)
                    .setY(y)
                    .setWidth(105)
                    .setHeight(10)
                    .setLabel("Rainbow Speed")
                    .setValue(textWidget.getRainbowSpeed())
                    .setMinValue(5f)
                    .setMaxValue(25.0f)
                    .setSelectedWidget(selectedWidget)
                    .build();
        }
    }
}


