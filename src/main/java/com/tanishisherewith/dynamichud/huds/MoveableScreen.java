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
        contextMenu.clear();
        contextMenu.add(new ContextMenu(mc, x, y + widget.getHeight() + 5, selectedWidget, this));
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
        Slider.clear();
        colorPicker = null;
        contextMenu.clear();
    }

    protected void ArmorWidgetMenu(ArmorWidget armorWidget, int x, int y) {
        Slider.clear();
        contextMenu.get(0).setHeightFromWidget(14);
        contextMenu.get(0).setPadding(5);
        contextMenu.get(0).addEnumCycleOption("", TextureHelper.Position.values(), () -> armorWidget.currentTextPosition[0], newPosition -> {
            armorWidget.currentTextPosition[0] = newPosition;
        });
    }

    protected void TextWidgetMenu(TextWidget textWidget, int x, int y) {
        contextMenu.get(0).setHeightFromWidget(2);
        contextMenu.get(0).setPadding(5);

        contextMenu.get(0).addOption("Shadow", () -> {
            textWidget.setShadow(!textWidget.hasShadow());
        });
        contextMenu.get(0).addOption("Rainbow", () -> {
            textWidget.setRainbow(!textWidget.hasRainbow());
        });
        contextMenu.get(0).addOption("Vertical Rainbow", () -> {
            textWidget.setVerticalRainbow(!textWidget.hasVerticalRainbow());
        });
        if (!textWidget.getText().isEmpty()) {
            contextMenu.get(0).addOption("TextColor", () -> {
                textWidget.toggleTextColorOption();

                if (textWidget.isTextcolorOptionEnabled())
                    colorPicker = new ColorGradientPicker(mc, widgetX + 110, widgetY + textWidget.getHeight() + 5, textWidget.getTextcolor(), textWidget::setTextColor, 50, 100, selectedWidget);
                else
                    colorPicker = null;
            });
        }
        if (!textWidget.getDataText().trim().isEmpty()) {
            contextMenu.get(0).addOption("DataColor", () -> {
                textWidget.toggleDataColorOption();

                if (textWidget.isDatacolorOptionEnabled())
                    colorPicker = new ColorGradientPicker(mc, widgetX + 110, widgetY + textWidget.getHeight() + 5, textWidget.getDatacolor(), textWidget::setDataColor, 50, 100, selectedWidget);
                else
                    colorPicker = null;
            });
        }
       /*  contextMenu.get(0).addOption("SubMenu: ",()->
        {
            if (contextMenu.size()>1) contextMenu.remove(1);
            contextMenu.add(new ContextMenu(mc, contextMenu.get(0).getX() + 40, contextMenu.get(0).getOptionY(6), selectedWidget,this));
            contextMenu.get(1).addOption("Option: 1",()->System.out.println("Pressed 1 "));
            contextMenu.get(1).addOption("Option: 2",()->System.out.println("Pressed 2 "));
            contextMenu.get(1).addOption("Option: 3",()->System.out.println("Pressed 3 "));
        });
        contextMenu.get(0).addDataTextOption(("Enter data"), data -> {
            System.out.println("Entered data: " + data);
        },widgetX,widgetY);
        contextMenu.get(0).addDoubleTextOption(("Enter double"), data -> {
            System.out.println("Entered data: " + data);
        },widgetX,widgetY);*/

        Slider.add(new SliderWidgetBuilder(client)
                .setX(x)
                .setY(y)
                .setWidth(105)
                .setHeight(contextMenu.get(0).getHeight() + 13)
                .setLabel("Rainbow Speed")
                .setValue(textWidget.getRainbowSpeed())
                .setMinValue(5f)
                .setMaxValue(25.0f)
                .setSelectedWidget(selectedWidget)
                .build());
    }
}


