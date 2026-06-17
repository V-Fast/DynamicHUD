package com.tanishisherewith.dynamichud.utils.handlers;


public class ScrollHandler {
    protected int scrollOffset;
    protected double scrollVelocity;
    protected long lastScrollTime;
    protected boolean isDragging;
    protected int maxScrollOffset;
    protected double SCROLL_SPEED = 1;
    protected double lastMouseY;

    public ScrollHandler() {
        this.scrollOffset = 0;
        this.scrollVelocity = 0;
        this.lastScrollTime = 0;
        this.isDragging = false;
    }

    public void updateScrollOffset(int maxYOffset) {
        if (maxYOffset < 0) maxYOffset = 0;

        this.maxScrollOffset = maxYOffset;
        applyMomentum();
        scrollOffset = Math.clamp(scrollOffset, 0, maxScrollOffset);
    }

    public void mouseScrolled(double deltaY) {
        scrollVelocity -= deltaY * 10;
        lastScrollTime = System.currentTimeMillis();
    }

    public void startDragging(double mouseY) {
        isDragging = true;
        lastMouseY = mouseY;
    }

    public void stopDragging() {
        isDragging = false;
    }

    public void addOffset(int offset) {
        this.scrollOffset = Math.clamp(scrollOffset + offset, 0, maxScrollOffset);
    }

    public void updateScrollPosition(double mouseY) {
        if (isDragging) {
            // Calculate the difference in mouse Y position
            double deltaY = lastMouseY - mouseY;

            scrollOffset = Math.clamp(scrollOffset - (int) deltaY, 0, maxScrollOffset);

            lastMouseY = mouseY;
        }
    }

    private void applyMomentum() {
        long currentTime = System.currentTimeMillis();
        double timeDelta = (currentTime - lastScrollTime) / 1000.0;
        scrollOffset += (int) (scrollVelocity * timeDelta);
        scrollVelocity *= 0.9; // Decay factor
        scrollOffset = Math.clamp(scrollOffset, 0, maxScrollOffset);
    }

    public int getScrollOffset() {
        return Math.max(scrollOffset, 0);
    }

    public boolean isOffsetWithinBounds(int offset) {
        return scrollOffset + offset >= 0 && scrollOffset + offset <= maxScrollOffset;
    }

    public ScrollHandler setScrollSpeed(double scrollSpeed) {
        this.SCROLL_SPEED = scrollSpeed;
        return this;
    }
}