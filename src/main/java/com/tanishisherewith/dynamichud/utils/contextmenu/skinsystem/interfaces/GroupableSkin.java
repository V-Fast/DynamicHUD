package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces;

import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutEngine;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import net.minecraft.client.gui.GuiGraphics;

public interface GroupableSkin {
    LayoutEngine.Offset getGroupIndent();

    void renderGroup(GuiGraphics graphics, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY);
}

