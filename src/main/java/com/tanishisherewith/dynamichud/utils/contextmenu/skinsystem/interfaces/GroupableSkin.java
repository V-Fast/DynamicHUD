package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces;

import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutEngine;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface GroupableSkin {
    LayoutEngine.Offset getGroupIndent();

    void renderGroup(GuiGraphicsExtractor graphics, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY);
}

