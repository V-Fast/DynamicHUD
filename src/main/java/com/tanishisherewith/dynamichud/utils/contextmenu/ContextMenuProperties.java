package com.tanishisherewith.dynamichud.utils.contextmenu;

import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.ClassicSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.Skin;

import java.awt.*;

public class ContextMenuProperties {
    private Color backgroundColor = new Color(107, 112, 126, 124);
    private Color borderColor = Color.BLACK;
    private float borderWidth = 1f;
    private int padding = 5;  // The amount of padding around the rectangle
    private int heightOffset = 4; // Height offset from the widget
    private boolean drawBorder = true;
    private boolean shadow = false;
    private boolean roundedCorners = true;
    private int cornerRadius = 3;
    private boolean hoverEffect = true;
    private Color hoverColor = new Color(42, 42, 42, 150);
    private boolean enableAnimations = true;
    private Skin skin = new ClassicSkin();

    private ContextMenuProperties() {
    }

    public static Builder builder() {
        return new ContextMenuProperties().new Builder();
    }

    public static ContextMenuProperties createGenericSimplified() {
        return new ContextMenuProperties().new Builder().build();
    }

    // Getters for all properties
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public int getPadding() {
        return padding;
    }

    public int getHeightOffset() {
        return heightOffset;
    }

    public void setHeightOffset(int heightOffset) {
        this.heightOffset = heightOffset;
    }

    public boolean shouldDrawBorder() {
        return drawBorder;
    }

    public boolean shadow() {
        return shadow;
    }

    public boolean roundedCorners() {
        return roundedCorners;
    }

    public int getCornerRadius() {
        return cornerRadius;
    }

    public boolean hoverEffect() {
        return hoverEffect;
    }

    public Color getHoverColor() {
        return hoverColor;
    }

    public boolean enableAnimations() {
        return enableAnimations;
    }

    public Skin getSkin() {
        return skin;
    }

    public ContextMenuProperties copy() {
        return ContextMenuProperties.builder()
                .backgroundColor(backgroundColor)
                .borderColor(borderColor)
                .borderWidth(borderWidth)
                .padding(padding)
                .heightOffset(heightOffset)
                .drawBorder(drawBorder)
                .shadow(shadow)
                .roundedCorners(roundedCorners)
                .cornerRadius(cornerRadius)
                .hoverEffect(hoverEffect)
                .hoverColor(hoverColor)
                .skin(skin)
                .enableAnimations(enableAnimations)
                .build();
    }

    public class Builder {
        private Builder() {
        }

        public Builder backgroundColor(Color backgroundColor) {
            ContextMenuProperties.this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder borderColor(Color borderColor) {
            ContextMenuProperties.this.borderColor = borderColor;
            return this;
        }

        public Builder skin(Skin skin) {
            ContextMenuProperties.this.skin = skin;
            return this;
        }

        public Builder borderWidth(float borderWidth) {
            ContextMenuProperties.this.borderWidth = borderWidth;
            return this;
        }

        public Builder padding(int padding) {
            ContextMenuProperties.this.padding = padding;
            return this;
        }

        public Builder heightOffset(int heightOffset) {
            ContextMenuProperties.this.heightOffset = heightOffset;
            return this;
        }

        public Builder drawBorder(boolean drawBorder) {
            ContextMenuProperties.this.drawBorder = drawBorder;
            return this;
        }

        public Builder shadow(boolean shadow) {
            ContextMenuProperties.this.shadow = shadow;
            return this;
        }

        public Builder roundedCorners(boolean roundedCorners) {
            ContextMenuProperties.this.roundedCorners = roundedCorners;
            return this;
        }

        public Builder cornerRadius(int cornerRadius) {
            ContextMenuProperties.this.cornerRadius = cornerRadius;
            return this;
        }

        public Builder hoverEffect(boolean hoverEffect) {
            ContextMenuProperties.this.hoverEffect = hoverEffect;
            return this;
        }

        public Builder hoverColor(Color hoverColor) {
            ContextMenuProperties.this.hoverColor = hoverColor;
            return this;
        }

        public Builder enableAnimations(boolean enableAnimations) {
            ContextMenuProperties.this.enableAnimations = enableAnimations;
            return this;
        }

        public ContextMenuProperties build() {
            return ContextMenuProperties.this;
        }
    }
}