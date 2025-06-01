package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces;

import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutContext;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import net.minecraft.client.gui.DrawContext;

public interface GroupableSkin {
    LayoutContext.Offset getGroupIndent();

    void renderGroup(DrawContext drawContext, OptionGroup group, int groupX, int groupY, int mouseX, int mouseY);
}

