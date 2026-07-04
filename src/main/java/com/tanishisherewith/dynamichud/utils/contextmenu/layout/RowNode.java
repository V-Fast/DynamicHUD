package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

/**
 * A parent node that lays out its child nodes horizontally in a single row.
 * Divides the available width evenly among all visible children.
 */
public class RowNode extends ParentNode {
    private int spacing;

    public RowNode(int spacing) {
        this.spacing = spacing;
    }

    public RowNode() {
        this(2);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        int currentX = x;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.setPosition(currentX, y);
                currentX += child.getWidth() + spacing;
            }
        }
    }

    @Override
    public void layout(int maxWidth) {
        int visibleCount = 0;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                visibleCount++;
            }
        }
        if (visibleCount == 0) {
            setSize(0, 0);
            return;
        }
        int totalSpacing = spacing * (visibleCount - 1);
        int childWidth = (maxWidth - totalSpacing) / visibleCount;
        int maxHeight = 0;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.layout(childWidth);
                maxHeight = Math.max(maxHeight, child.getHeight());
            }
        }
        setSize(maxWidth, maxHeight);
    }
}
