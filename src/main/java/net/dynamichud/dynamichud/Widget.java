package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public abstract class Widget {
    protected final MinecraftClient client;
    protected boolean enabled = true;
    protected float xPercent;
    protected float yPercent;
    public Widget(MinecraftClient client) {
        this.client = client;
    }

    public abstract void render(MatrixStack matrices);

    public boolean isEnabled() {
        return enabled;
    }

    public int getX() {
        return (int) (client.getWindow().getScaledWidth() * xPercent);
    }

    public int getY() {
        return (int) (client.getWindow().getScaledHeight() * yPercent);
    }

    public void setX(int x) {
        this.xPercent = (float) x / client.getWindow().getScaledWidth();
    }

    public void setY(int y) {
        this.yPercent = (float) y / client.getWindow().getScaledHeight();
    }
    public int getHeight() {return client.textRenderer.fontHeight;}

}
