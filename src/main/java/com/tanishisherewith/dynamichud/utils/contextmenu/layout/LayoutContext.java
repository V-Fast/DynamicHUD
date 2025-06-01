package com.tanishisherewith.dynamichud.utils.contextmenu.layout;

import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;

//This was supposed to be used for something bigger but that got scraped.
public class LayoutContext {
    private final Offset indent;
    private final Option<?> parentOption;

    public LayoutContext() {
        this(Offset.zero(), null);
    }

    public LayoutContext(Offset margin, Option<?> parentOption) {
        this.indent = margin;
        this.parentOption = parentOption;
    }

    public Offset getIndent() {
        return indent;
    }

    public Option<?> getParentOption() {
        return parentOption;
    }

    // Builder-style methods for creating new contexts
    public LayoutContext withIndent(Offset indent) {
        return new LayoutContext(indent, this.parentOption);
    }

    public LayoutContext withParent(Option<?> parent) {
        return new LayoutContext(this.indent, parent);
    }

    public LayoutContext addIndent(Offset additionalIndent) {
        return new LayoutContext(
                this.indent.add(additionalIndent),
                this.parentOption
        );
    }

    // Utility methods for calculating positions
    public int getEffectiveX(int baseX) {
        return baseX + indent.left;
    }

    public int getEffectiveY(int baseY) {
        return baseY + indent.top;
    }

    public int getEffectiveWidth(int baseWidth) {
        return baseWidth + indent.left;
    }

    public int getEffectiveHeight(int baseHeight) {
        return baseHeight + indent.top;
    }

    public static class Offset {
        public final int left;
        public final int top;

        public Offset(int all) {
            this(all, all);
        }

        public Offset(int horizontal, int vertical) {
            this.left = horizontal;
            this.top = vertical;
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
