package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;


public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> GlobalConfig.get().createYACLGUI();
    }
}
