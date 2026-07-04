package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import java.util.ArrayList;
import java.util.List;

/**
 * A parent node that lays out its child nodes in a grid with a fixed number of columns.
 * Automatically wraps rows and adjusts spacing between components.
 */
public class GridNode extends ParentNode {
    private int columns;
    private int spacing;
    private List<Integer> rowHeights = new ArrayList<>();

    public GridNode(int columns, int spacing) {
        this.columns = columns;
        this.spacing = spacing;
    }

    public GridNode(int columns) {
        this(columns, 2);
    }

    @Override
    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        int currentY = y;
        int visibleIndex = 0;
        int currentRowHeightIndex = 0;
        int childWidth = (width - spacing * (columns - 1)) / columns;

        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                int col = visibleIndex % columns;
                int childX = x + col * (childWidth + spacing);
                child.setPosition(childX, currentY);
                visibleIndex++;
                if (visibleIndex % columns == 0) {
                    if (currentRowHeightIndex < rowHeights.size()) {
                        currentY += rowHeights.get(currentRowHeightIndex) + spacing;
                        currentRowHeightIndex++;
                    }
                }
            }
        }
    }

    @Override
    public void layout(int maxWidth) {
        rowHeights.clear();
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
        int childWidth = (maxWidth - spacing * (columns - 1)) / columns;
        int currentRowHeight = 0;
        int currentCol = 0;
        int totalHeight = 0;

        for (LayoutNode child : children) {
            if (child.shouldRender()) {
                child.layout(childWidth);
                currentRowHeight = Math.max(currentRowHeight, child.getHeight());
                currentCol++;
                if (currentCol == columns) {
                    rowHeights.add(currentRowHeight);
                    totalHeight += currentRowHeight + spacing;
                    currentRowHeight = 0;
                    currentCol = 0;
                }
            }
        }
        if (currentCol > 0) {
            rowHeights.add(currentRowHeight);
            totalHeight += currentRowHeight + spacing;
        }
        if (totalHeight > 0) {
            totalHeight -= spacing;
        }
        setSize(maxWidth, totalHeight);
    }
}
