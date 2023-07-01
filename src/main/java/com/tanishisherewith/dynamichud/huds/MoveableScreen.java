package com.tanishisherewith.dynamichud.huds;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.TextureHelper;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.util.colorpicker.ColorGradientPicker;
import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.armor.ArmorWidget;
import com.tanishisherewith.dynamichud.widget.item.ItemWidget;
import com.tanishisherewith.dynamichud.widget.slider.SliderWidgetBuilder;
import com.tanishisherewith.dynamichud.widget.text.TextWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
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
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);
        drawContext.drawTextWithShadow(textRenderer, "Editors Screen", (int) (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f - textRenderer.getWidth("Editor Screen") / 2f), 5, ColorHelper.ColorToInt(Color.WHITE));
    }

    @Override
    protected boolean handleRightClickOnWidget(Widget widget) {
        selectedWidget = widget;
        sliderWigdet = widget;
        // Show context menu
        menu(widget, widgetX, widgetY);
        return true;
    }

    @Override
    protected void menu(Widget widget, int x, int y) {
        contextMenu = new ContextMenu(mc, x, y + widget.getHeight() + 5, selectedWidget);
        if (widget instanceof ArmorWidget armorWidget) {
            ArmorWidgetMenu(armorWidget, x, y);
        }
        if (widget instanceof ItemWidget itemWidget) {
            ItemWidgetMenu(itemWidget, x, y);
        }
        if (widget instanceof TextWidget textWidget) {
            TextWidgetMenu(textWidget, x, y);
        }
    }

    protected void ItemWidgetMenu(ItemWidget itemWidget, int x, int y) {
        Slider = null;
        colorPicker = null;
        contextMenu = null;
    }

    protected void ArmorWidgetMenu(ArmorWidget armorWidget, int x, int y) {
        Slider = null;
        contextMenu.setHeightFromWidget(14);
        contextMenu.setPadding(5);
        contextMenu.addEnumCycleOption("", TextureHelper.Position.values(), () -> armorWidget.currentTextPosition[0], newPosition -> {
            armorWidget.currentTextPosition[0] = newPosition;
        });
    }

    protected void TextWidgetMenu(TextWidget textWidget, int x, int y) {
        contextMenu.setHeightFromWidget(2);
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
        if (!textWidget.getText().isEmpty()) {
            contextMenu.addOption("TextColor", () -> {
                textWidget.toggleTextColorOption();

                if (textWidget.isTextcolorOptionEnabled())
                    colorPicker = new ColorGradientPicker(mc, widgetX + 110, widgetY + textWidget.getHeight() + 5, textWidget.getTextcolor(), textWidget::setTextColor, 50, 100, selectedWidget);
                else
                    colorPicker = null;
            });
        }
        if (!textWidget.getDataText().trim().isEmpty()) {
            contextMenu.addOption("DataColor", () -> {
                textWidget.toggleDataColorOption();

                if (textWidget.isDatacolorOptionEnabled())
                    colorPicker = new ColorGradientPicker(mc, widgetX + 110, widgetY + textWidget.getHeight() + 5, textWidget.getDatacolor(), textWidget::setDataColor, 50, 100, selectedWidget);
                else
                    colorPicker=null;
            });
        }
        contextMenu.addDataTextOption(("Enter data"), data -> {
            System.out.println("Entered data: " + data);
        });
        contextMenu.addDoubleTextOption(("Enter data"), data -> {
            System.out.println("Entered data: " + data);
        });

        Slider = new SliderWidgetBuilder(client)
                .setX(x)
                .setY(y)
                .setWidth(105)
                .setHeight(contextMenu.getHeight()+13)
                .setLabel("Rainbow Speed")
                .setValue(textWidget.getRainbowSpeed())
                .setMinValue(5f)
                .setMaxValue(25.0f)
                .setSelectedWidget(selectedWidget)
                .build();
    }
}


