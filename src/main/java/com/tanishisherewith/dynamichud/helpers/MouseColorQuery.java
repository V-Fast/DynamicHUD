package com.tanishisherewith.dynamichud.helpers;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tanishisherewith.dynamichud.DynamicHUD;
import net.minecraft.client.Minecraft;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * Color query of the screen should be done when rendering is completed (i.e. at the end of the render tick),
 * so this is a helper class to make it easier to query color data
 */
public class MouseColorQuery {
    private static SampleRequest pendingRequest = null;

    public static void request(double mouseX, double mouseY, Consumer<int[]> callback) {
        if (pendingRequest == null) {
            pendingRequest = new SampleRequest(mouseX, mouseY, callback);
        }
    }
    public static void request(Consumer<int[]> callback) {
        request(DynamicHUD.MC.mouseHandler.xpos(), DynamicHUD.MC.mouseHandler.ypos(), callback);
    }

    public static void processIfPending() {
        if (pendingRequest == null) return;

        RenderTarget framebuffer = DynamicHUD.MC.getMainRenderTarget();
        Window window = DynamicHUD.MC.getWindow();

        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int framebufferWidth = framebuffer.width;
        int framebufferHeight = framebuffer.height;

        double scaleX = (double) framebufferWidth / windowWidth;
        double scaleY = (double) framebufferHeight / windowHeight;

        int x = (int) (pendingRequest.mouseX * scaleX);
        int y = (int) ((windowHeight - pendingRequest.mouseY) * scaleY);

        if (x < 0 || x >= framebufferWidth || y < 0 || y >= framebufferHeight) {
            pendingRequest.callback.accept(null);
            pendingRequest = null;
            return;
        }

        // Make sure rendering is complete
        RenderSystem.assertOnRenderThread();

        // buffer to store the pixel data
        ByteBuffer buffer = BufferUtils.createByteBuffer(4);

        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        int red = buffer.get(0) & 0xFF;
        int green = buffer.get(1) & 0xFF;
        int blue = buffer.get(2) & 0xFF;
        int alpha = buffer.get(3) & 0xFF;

        pendingRequest.callback.accept(new int[]{red, green, blue, alpha});
        pendingRequest = null;
    }

    private record SampleRequest(double mouseX, double mouseY, Consumer<int[]> callback) {}
}
