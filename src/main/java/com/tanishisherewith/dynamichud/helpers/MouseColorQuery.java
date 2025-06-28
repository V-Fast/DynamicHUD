package com.tanishisherewith.dynamichud.helpers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.Window;
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

    public static void processIfPending() {
        if (pendingRequest == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer framebuffer = client.getFramebuffer();
        Window window = client.getWindow();

        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();
        int framebufferWidth = framebuffer.textureWidth;
        int framebufferHeight = framebuffer.textureHeight;

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
