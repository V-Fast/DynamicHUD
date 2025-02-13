package com.tanishisherewith.dynamichud.internal;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashReportSection;

import java.awt.*;
import java.io.File;
import java.util.List;

public class WarningScreen extends Screen {
    private final List<ModError> modErrors;

    public WarningScreen(List<ModError> modErrors) {
        super(Text.of("DynamicHUD Warning"));
        this.modErrors = modErrors;
    }

    @Override
    protected void init() {
        ButtonWidget confirmButton = ButtonWidget.builder(Text.of("I Understand"), button -> MinecraftClient.getInstance().setScreen(null))
                                                 .dimensions(this.width / 2 - 100, this.height - 40, 200, 20)
                                                 .narrationSupplier((e)-> Text.literal("I understand"))
                                                 .build();

        ButtonWidget logs_folder = ButtonWidget.builder(Text.of("Open logs"), button -> {
                        File logsFolder = new File(MinecraftClient.getInstance().runDirectory, "logs");
                        Util.getOperatingSystem().open(logsFolder);
                })
                .dimensions(this.width / 2 - 100, this.height - 70, 200, 20)
                .narrationSupplier((e)-> Text.literal("Open logs"))
                .build();

        // Add "I Understand" button
        this.addDrawableChild(confirmButton);
        this.addDrawableChild(logs_folder);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

       context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
       context.drawCenteredTextWithShadow(this.textRenderer, "Mods with invalid implementation of DynamicHUD found!", this.width / 2, 35, Color.ORANGE.getRGB());

        int y = 60;
        for (ModError error : modErrors) {
            Text modName = Text.literal( "> \"" + error.modName() + "\"").formatted(Formatting.RED);
            context.drawText(this.textRenderer, modName, this.width / 2 - 100, y, -1, false);
            List<OrderedText> errorMessage = this.textRenderer.wrapLines(Text.literal( "Error: " + error.errorMessage()), this.width/2);

            if(mouseX >= this.width/2 - 100 && mouseX <= this.width/2 - 100 + this.textRenderer.getWidth(modName) && mouseY >= y && mouseY <= y + this.textRenderer.fontHeight){
                context.drawOrderedTooltip(textRenderer,errorMessage,mouseX,mouseY);
            }
            y += 11; // Space between mod errors
        }

        y += 5;
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of("Please report this problem to the respective mod owners."), this.width / 2, y, -1);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Widgets of these mods won't work.").formatted(Formatting.YELLOW), this.width / 2, y + 10,-1);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Check latest.log for more details").formatted(Formatting.ITALIC), this.width / 2, y + 30,-1);
    }
}