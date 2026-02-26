package com.tanishisherewith.dynamichud.internal;

import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.Skin;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;

import java.awt.*;
import java.io.File;
import java.util.List;

public class WarningScreen extends Screen {
    private final List<ModError> modErrors;

    public WarningScreen(List<ModError> modErrors) {
        super(Component.literal("DynamicHUD Warning"));
        this.modErrors = modErrors;
    }

    @Override
    protected void init() {
        Button confirmButton = Button.builder(Component.literal("I Understand"), button -> Minecraft.getInstance().setScreen(null))
                .bounds(this.width / 2 - 100, this.height - 40, 200, 20)
                .createNarration((e) -> Component.literal("I understand"))
                .build();

        Button logs_folder = Button.builder(Component.literal("Open logs"), button -> {
                    File logsFolder = new File(Minecraft.getInstance().gameDirectory, "logs");
                    Util.getPlatform().openFile(logsFolder);
                })
                .bounds(this.width / 2 - 100, this.height - 70, 200, 20)
                .createNarration((e) -> Component.literal("Open logs"))
                .build();

        // Add "I Understand" button
        this.addRenderableWidget(confirmButton);
        this.addRenderableWidget(logs_folder);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        graphics.drawCenteredString(this.font, "Mods with bad implementation of DynamicHUD found!", this.width / 2, 35, Color.ORANGE.getRGB());

        int y = 60;
        for (ModError error : modErrors) {
            Component modName = Component.literal("> \"" + error.modName() + "\"").withStyle(ChatFormatting.RED);
            graphics.drawString(this.font, modName, this.width / 2 - 100, y, -1, false);
            List<FormattedCharSequence> errorMessage =
                    this.font.split(Component.literal("Error: " + error.errorMessage()), this.width / 2);

            if (Skin.isMouseOver(mouseX,mouseY,(double) this.width / 2 - 102, y - 1, this.font.width(modName) + 4,this.font.lineHeight + 2)) {
                graphics.setTooltipForNextFrame(errorMessage, mouseX, mouseY);
            }
            y += 11; // Space between mod errors
        }

        y += 5;
        graphics.drawCenteredString(this.font, Component.literal("Please report this problem to the respective mod owners."), this.width / 2, y, -1);
        graphics.drawCenteredString(this.font, Component.literal("Widgets of these mods won't work.").withStyle(ChatFormatting.YELLOW), this.width / 2, y + 10, -1);
        graphics.drawCenteredString(this.font, Component.literal("Check latest.log for more details").withStyle(ChatFormatting.ITALIC), this.width / 2, y + 30, -1);
    }
}