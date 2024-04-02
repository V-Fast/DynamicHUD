package com.tanishisherewith.dynamichud.util.contextmenu;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class TextWidgetButtonExt extends TextFieldWidget {
    public TextWidgetButtonExt(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

}
