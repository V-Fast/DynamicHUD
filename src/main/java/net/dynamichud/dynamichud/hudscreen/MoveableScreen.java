package net.dynamichud.dynamichud.hudscreen;

import net.dynamichud.dynamichud.Util.ColorPicker;
import net.dynamichud.dynamichud.Util.ContextMenu;
import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.SliderWidget;
import net.dynamichud.dynamichud.Widget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.dynamichud.dynamichud.helpers.TextureHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MoveableScreen extends AbstractMoveableScreen{
    /**
     * Constructs a AbstractMoveableScreen object.
     *
     * @param title
     * @param dynamicutil The DynamicUtil instance used by this screen
     */
    public MoveableScreen(Text title, DynamicUtil dynamicutil) {
        super(title, dynamicutil);
    }

    @Override
    protected boolean handleRightClickOnWidget(Widget widget) {
            selectedWidget = widget;
            sliderWigdet = widget;
            int x = selectedWidget.getX();
            int y = selectedWidget.getY();
            // Show context menu
            menu(widget,x,y);
            return true;
    }

    @Override
    protected void menu(Widget widget, int x, int y) {
        if (widget instanceof ArmorWidget armorWidget)
        {
            Slider=null;
            contextMenu=null;
            contextMenu = new ContextMenu(mc, x, y + armorWidget.getWidgetBox().getHeight()+100, widget);

            contextMenu.setPadding(2);
            contextMenu.setCornerRadius(45);
            contextMenu.addEnumCycleOption("Position", TextureHelper.Position.values(), () -> armorWidget.currentTextPosition[0], newPosition -> {
                System.out.println("Position: " + newPosition);
                armorWidget.currentTextPosition[0] = newPosition;
            });
        }
        if (widget instanceof TextWidget textWidget) {
            contextMenu = new ContextMenu(mc, x, y + textWidget.getWidgetBox().getHeight()+3, widget);
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
                colorPicker = new ColorPicker(mc, mc.getWindow().getScaledWidth() / 2, (mc.getWindow().getScaledHeight() / 2) - 50, textWidget.getColor(), textWidget::setColor);
            });
            Slider = new SliderWidget(mc, x, y + 20, 105, 20, "Rainbow Speed", textWidget.getRainbowSpeed(), 5f, 25.0f, selectedWidget);
        }
    }
    /**
     * Returns whether this screen should pause the game when it is displayed.
     *
     * @return False to indicate that the game should not be paused
     */
    @Override
    public boolean shouldPause() {
        return false;
    }

    /**
     * Called when this screen is resized.
     *
     * @param client The Minecraft client instance
     * @param width  The new width of this screen in pixels
     * @param height The new height of this screen in pixels
     */
    @Override
    public void resize(MinecraftClient client, int width, int height) {
        return;
    }
}
