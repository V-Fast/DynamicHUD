package com.tanishisherewith.dynamichud.huds;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.TextureHelper;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.util.colorpicker.ColorGradientPicker;
import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.armor.ArmorWidget;
import com.tanishisherewith.dynamichud.widget.item.ItemWidget;
import com.tanishisherewith.dynamichud.widget.slider.SliderWidget;
import com.tanishisherewith.dynamichud.widget.slider.SliderWidgetBuilder;
import com.tanishisherewith.dynamichud.widget.text.TextWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.*;

public class MoveableScreen extends AbstractMoveableScreen {
    SliderWidget SliderWidget;
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
        assert client != null;
        /*SliderWidget = new SliderWidgetBuilder(client)
                .setX(client.getWindow().getScaledWidth() - 120)
                .setY(client.getWindow().getScaledHeight() - 20)
                .setWidth(105)
                .setHeight(10)
                .setLabel("Scale")
                .setValue(Widget.getScale())
                .getValue(Widget::setScale)
                .setMinValue(0.5f)
                .setMaxValue(4f)
                .setSelectedWidget(null)
                .build();*/
        drawContext.drawTextWithShadow(textRenderer, "Editors Screen", (int) (MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f - textRenderer.getWidth("Editor Screen") / 2f), 5, ColorHelper.ColorToInt(Color.WHITE));
       // SliderWidget.render(drawContext);
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
        contextMenus.clear();
        contextMenus.add(new ContextMenu(mc, x, y + widget.getHeight() + 5, selectedWidget, this));
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
        Sliders.clear();
        colorPicker = null;
        contextMenus.clear();
    }

    protected void ArmorWidgetMenu(ArmorWidget armorWidget, int x, int y) {
        Sliders.clear();
        contextMenus.get(0).setHeightFromWidget(14);
        contextMenus.get(0).setPadding(5);
        contextMenus.get(0).addEnumCycleOption("", TextureHelper.Position.values(), () -> armorWidget.currentTextPosition[0], newPosition -> {
            armorWidget.currentTextPosition[0] = newPosition;
        });
    }

    protected void TextWidgetMenu(TextWidget textWidget, int x, int y) {
        contextMenus.get(0).setHeightFromWidget(2);
        contextMenus.get(0).setPadding(5);

        contextMenus.get(0).addOption("Shadow", () -> {
            textWidget.setShadow(!textWidget.hasShadow());
        });
        contextMenus.get(0).addOption("Rainbow", () -> {
            textWidget.setRainbow(!textWidget.hasRainbow());
        });
        if (!textWidget.getText().isEmpty()) {
            contextMenus.get(0).addOption("TextColor", () -> {
                textWidget.toggleTextColorOption();

                if (textWidget.isTextcolorOptionEnabled())
                    colorPicker = new ColorGradientPicker(mc, widgetX +110, widgetY + textWidget.getHeight() + 5, textWidget.getTextcolor(), textWidget::setTextColor, 50, 100, selectedWidget);
                else
                    colorPicker = null;
            });
        }
        if (!textWidget.getDataText().trim().isEmpty()) {
            contextMenus.get(0).addOption("DataColor", () -> {
                textWidget.toggleDataColorOption();

                if (textWidget.isDatacolorOptionEnabled())
                    colorPicker = new ColorGradientPicker(mc, widgetX + 110, widgetY + textWidget.getHeight() + 5, textWidget.getDatacolor(), textWidget::setDataColor, 50, 100, selectedWidget);
                else
                    colorPicker = null;
            });
        }
       /*  contextMenus.get(0).addOption("SubMenu: ",()->
        {
            if (contextMenus.size()>1) contextMenus.remove(1);
            contextMenus.add(new ContextMenu(mc, contextMenus.get(0).getX() + 40, contextMenus.get(0).getOptionY(6), selectedWidget,this));
            contextMenus.get(1).addOption("Option: 1",()->System.out.println("Pressed 1 "));
            contextMenus.get(1).addOption("Option: 2",()->System.out.println("Pressed 2 "));
            contextMenus.get(1).addOption("Option: 3",()->System.out.println("Pressed 3 "));
        });
        contextMenus.get(0).addDataTextOption(("Enter data"), data -> {
            System.out.println("Entered data: " + data);
        },widgetX,widgetY);
        contextMenus.get(0).addDoubleTextOption(("Enter double"), data -> {
            System.out.println("Entered data: " + data);
        },widgetX,widgetY);*/

        SliderWidget sliderWidget =new SliderWidgetBuilder(client)
                .setX(x)
                .setY(y)
                .setWidth(105)
                .setHeight(contextMenus.get(0).getHeight() + 7)
                .setLabel("Rainbow Speed")
                .setValue(textWidget.getRainbowSpeed())
                .setMinValue(5f)
                .setMaxValue(25.0f)
                .getValue(TextWidget::setRainbowSpeed)
                .setSelectedWidget(selectedWidget)
                .build();
        Sliders.add(sliderWidget);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
           // if(SliderWidget.mouseDragged(mouseX,mouseY,button,deltaX,deltaY))
            //{
              //  return true;
            //}
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
      //  if(SliderWidget.mouseClicked(mouseX,mouseY,button))
     //   {
         //   SliderWidget.updatePosition();
      //      return true;
       // }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}


