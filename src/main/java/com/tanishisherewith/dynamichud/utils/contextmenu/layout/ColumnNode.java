package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

/**
 * A parent node that lays out its child nodes vertically in a single column.
 * Spaces children by a configurable item spacing value.
 */
public class ColumnNode extends ParentNode {
    private int spacing;

    public ColumnNode(int spacing) {
        this.spacing = spacing;
    }

    public ColumnNode() {
        this(2);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        int currentY = y;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.setPosition(x, currentY);
                currentY += child.getHeight() + spacing;
            }
        }
    }

    @Override
    public void layout(int maxWidth) {
        int totalHeight = 0;
        int maxW = 0;
        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.layout(maxWidth);
                totalHeight += child.getHeight() + spacing;
                maxW = Math.max(maxW, child.getWidth());
            }
        }
        if (totalHeight > 0) {
            totalHeight -= spacing;
        }
        setSize(maxW, totalHeight);
    }

    @Override
    public String toString() {
        return "ColumnNode = " + this.children;
    }
}
