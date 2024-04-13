package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;


public class ModMenuIntegration implements ModMenuApi {
    public static Screen YACL_CONFIG_SCREEN = GlobalConfig.get().createYACLGUI();

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YACL_CONFIG_SCREEN;
    }
}
