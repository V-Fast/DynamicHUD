package com.tanishisherewith.dynamichud.integration;

import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import com.tanishisherewith.dynamichud.widgets.ItemWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;

/**
 * The default implementation for included widgets.
 */
public class DefaultIntegrationImpl implements DynamicHudIntegration {
    @Override
    public DynamicHudConfigurator configure(DynamicHudConfigurator configurator) {
        configurator.markAsUtility = true;
        return configurator;
    }

    @Override
    public void init() {}

    @Override
    public void registerCustomWidgets() {
        WidgetManager.registerCustomWidgets(
                TextWidget.DATA,
                ItemWidget.DATA,
                GraphWidget.DATA
        );
    }
}
