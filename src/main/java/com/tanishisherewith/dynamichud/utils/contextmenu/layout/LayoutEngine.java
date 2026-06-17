package com.tanishisherewith.dynamichud.utils.contextmenu.layout;
import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.Skin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2d;

import java.util.List;

public class LayoutEngine {
    private int horizontalPadding = 8;
    private int verticalPadding = 4;
    private int itemSpacing = 2;
    private int minWidth = 80;
    private LayoutStrategy activeStrategy;

    public LayoutEngine() {}

    public LayoutEngine(int horizontalPadding, int verticalPadding, int itemSpacing, int minWidth) {
         this(horizontalPadding,verticalPadding,itemSpacing,minWidth,new VerticalFlowStrategy());
    }

    public LayoutEngine(int horizontalPadding, int verticalPadding, int itemSpacing, int minWidth, LayoutStrategy layoutStrategy) {
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
        this.itemSpacing = itemSpacing;
        this.minWidth = minWidth;
        this.activeStrategy = layoutStrategy;
    }

    @FunctionalInterface
    public interface LayoutStrategy {
        /**
         * Computes dimensions and positions options within the context menu.
         */
        void layout(ContextMenu<?> menu, LayoutEngine engine);
    }

    /**
     * Executes the currently active layout strategy to position and size options dynamically.
     */
    public void applyLayout(ContextMenu<?> menu) {
        if (activeStrategy != null) {
            activeStrategy.layout(menu, this);
        }
    }

    /**
     * Helper function for immediate layouts
     * Automatically updates the option's dimensions and returns the updated Y position for the next element.
     *
     * @param option      option to arrange.
     * @param x           start X coordinate.
     * @param y           start Y coordinate.
     * @param targetWidth horizontal width for the option.
     * @return next Y coordinate.
     */
    public int layoutOption(Option<?> option, int x, int y, int targetWidth) {
        if (option == null || !option.shouldRender()) return y;

        int itemHeight = option.getHeight() > 0 ? option.getHeight() : DynamicHUD.MC.font.lineHeight;

        option.setPosition(x, y);
        option.setWidth(targetWidth);
        option.setHeight(itemHeight);

        return y + itemHeight + itemSpacing;
    }


    public LayoutStrategy getActiveStrategy() {
        return activeStrategy;
    }

    public void setActiveStrategy(LayoutStrategy activeStrategy) {
        this.activeStrategy = activeStrategy;
    }

    public int getHorizontalPadding() { return horizontalPadding; }
    public void setHorizontalPadding(int horizontalPadding) { this.horizontalPadding = horizontalPadding; }

    public int getVerticalPadding() { return verticalPadding; }
    public void setVerticalPadding(int verticalPadding) { this.verticalPadding = verticalPadding; }

    public int getItemSpacing() { return itemSpacing; }
    public void setItemSpacing(int itemSpacing) { this.itemSpacing = itemSpacing; }

    public int getMinWidth() { return minWidth; }
    public void setMinWidth(int minWidth) { this.minWidth = minWidth; }

    /**
     * Default layout strategy that arranges items in a single vertical column.
     * Automatically scales the menu width to match the longest option.
     */
    public static class VerticalFlowStrategy implements LayoutStrategy {
        @Override
        public void layout(ContextMenu<?> menu, LayoutEngine engine) {
            if (menu == null) return;

            Font font = Minecraft.getInstance().font;
            List<Option<?>> visibleOptions = menu.getProperties().getSkin().getOptions(menu);
            int paddingValue = menu.getProperties().getPadding();

            // width calc
            int maxInnerWidth = engine.getMinWidth();
            for (Option<?> option : visibleOptions) {
                if (!option.shouldRender()) continue;
                int preferredWidth = option.getWidth() > 0 ? option.getWidth() : font.width(option.getName());
                maxInnerWidth = Math.max(maxInnerWidth, preferredWidth);
            }

            int totalMenuWidth = maxInnerWidth + (engine.getHorizontalPadding() * 2) + paddingValue;

            // height calc
            int currentY = menu.getY() + engine.getVerticalPadding() + 3;
            int currentX = menu.getX() + engine.getHorizontalPadding();
            int targetWidth = totalMenuWidth - (engine.getHorizontalPadding() * 2) - paddingValue;

            for (Option<?> option : visibleOptions) {
                if (!option.shouldRender()) continue;
                currentY = engine.layoutOption(option, currentX, currentY, targetWidth);
            }

            menu.setWidth(totalMenuWidth);
            menu.setHeight(currentY - menu.getY());
        }
    }

    public record Offset(int left, int top) {
        public Offset(int all) {
            this(all, all);
        }

        public static Offset zero() {
            return new Offset(0);
        }

        public Offset add(Offset other) {
            return new Offset(
                    this.left + other.left,
                    this.top + other.top
            );
        }
    }
}