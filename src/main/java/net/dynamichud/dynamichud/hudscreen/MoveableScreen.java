package net.dynamichud.dynamichud.hudscreen;

import net.dynamichud.dynamichud.Util.ColorPicker;
import net.dynamichud.dynamichud.Util.ContextMenu;
import net.dynamichud.dynamichud.Util.ContextMenuBuilder;
import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.*;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

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
        contextMenu = new ContextMenu(mc,x,y+widget.getHeight()+5,selectedWidget);
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
                // Toggle shadow
                textWidget.setShadow(!textWidget.hasShadow());
            });
            contextMenu.addOption("Rainbow", () -> {
                // Toggle rainbow
                textWidget.setRainbow(!textWidget.hasRainbow());
            });
            contextMenu.addOption("Vertical Rainbow", () -> {
                // Toggle vertical rainbow
                textWidget.setVerticalRainbow(!textWidget.hasVerticalRainbow());
            });
            contextMenu.addOption("Color", () -> {
                // Show color picker
                // Set the color of the text
                textWidget.toggleColorOption();
                colorPicker=null;
                if (textWidget.isColorOptionEnabled()) colorPicker = new ColorPicker(mc, mc.getWindow().getScaledWidth() / 2, (mc.getWindow().getScaledHeight() / 2) - 50, textWidget.getColor(), textWidget::setColor);
                else colorPicker=null;
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


