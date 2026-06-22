package com.tanishisherewith.dynamichud.utils.handlers;

import net.minecraft.util.Mth;

public class ScrollHandler {
    protected double scrollOffset;
    protected double targetScrollOffset;
    protected boolean isDragging;
    protected int maxScrollOffset;
    protected double SCROLL_SPEED = 1.0;
    protected int trackHeight = 150;
    protected double lastMouseY;

    public ScrollHandler() {
        this.isDragging = false;
    }

    public void setScrollOffset(int offset) {
        this.displayValuePosition(offset);
    }

    public void setScrollOffsetDirectly(double offset) {
        this.displayValue = offset;
    }

    public int getScrollOffset() {
        return Math.toIntExact(Math.round(scrollOffset));
    }

    private double displayValue;
    private static final float LERP_SPEED = 0.22f;

    public void updateScrollOffset(int maxScrollOffset) {
        if(maxScrollOffset <= 0) return;
        this.maxScrollOffset = maxScrollOffset;
        displayValue = Mth.lerp(LERP_SPEED, displayValue, targetScrollOffset);
        displayValue = Mth.clamp(displayValue, 0.0, maxScrollOffset);
        this.scrollOffset = Math.round(displayValue);
    }

    public void mouseScrolled(double deltaY) {
        double amount = -deltaY * 18.0 * SCROLL_SPEED;
        this.targetScrollOffset = Mth.clamp(targetScrollOffset + amount, 0.0, maxScrollOffset);
    }

    public void startDragging(double mouseY) {
        this.isDragging = true;
        this.lastMouseY = mouseY;
    }

    public void stopDragging() {
        this.isDragging = false;
    }

    public void addOffset(int offset) {
        this.targetScrollOffset = Mth.clamp(targetScrollOffset + offset, 0.0, maxScrollOffset);
    }

    public void updateScrollPosition(double mouseY) {
        if (isDragging && maxScrollOffset > 0) {
            double deltaY = mouseY - lastMouseY;
            double scrollRatio = (double) maxScrollOffset / Math.max(1, trackHeight);
            double offsetDelta = deltaY * scrollRatio;
            this.targetScrollOffset = Mth.clamp(targetScrollOffset + offsetDelta, 0.0, maxScrollOffset);
            lastMouseY = mouseY;
        }
    }

    private void displayValuePosition(double val) {
        this.targetScrollOffset = Mth.clamp(val, 0.0, maxScrollOffset);
        this.displayValue = this.targetScrollOffset;
        this.scrollOffset = this.targetScrollOffset;
    }

    public boolean isOffsetWithinBounds(int offset) {
        return targetScrollOffset + offset >= 0 && targetScrollOffset + offset <= maxScrollOffset;
    }

    public ScrollHandler setScrollSpeed(double scrollSpeed) {
        this.SCROLL_SPEED = scrollSpeed;
        return this;
    }

    public void setTrackHeight(int trackHeight) {
        this.trackHeight = trackHeight;
    }

    public int getTrackHeight() {
        return trackHeight;
    }

    public boolean isDragging() {
        return isDragging;
    }
}