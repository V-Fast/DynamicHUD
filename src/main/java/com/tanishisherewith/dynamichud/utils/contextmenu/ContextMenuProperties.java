package com.tanishisherewith.dynamichud.utils.contextmenu;

import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.ClassicSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.Skin;

import java.awt.*;

/**
 * Note: Not all of these properties are used in all skins or all places.
 */
public class ContextMenuProperties {
    protected Color backgroundColor = new Color(107, 112, 126, 124);
    protected Color borderColor = Color.BLACK;
    protected float borderWidth = 1f;
    protected int padding = 5;  // The amount of padding around the rectangle
    protected int heightOffset = 4; // Height offset from the widget
    protected boolean drawBorder = true;
    protected boolean shadow = true;
    protected boolean roundedCorners = true;
    protected int cornerRadius = 4;
    protected boolean hoverEffect = true;
    protected Color hoverColor = new Color(42, 42, 42, 150);
    protected boolean enableAnimations = true;
    protected Skin skin = new ClassicSkin();

    protected ContextMenuProperties() {
    }

    public static Builder<?> builder() {
        return new Builder<>(new ContextMenuProperties());
    }

    public static ContextMenuProperties createGenericSimplified() {
        return new ContextMenuProperties();
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

    /**
     * @return Cloned object of every property except skin
     */
    public ContextMenuProperties clone() {
        return cloneToBuilder().build();
    }

    public Builder<?> cloneToBuilder() {
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
                .enableAnimations(enableAnimations);
    }

    /**
     * @return Cloned object with same skin as the current ContextMenuProperties
     */
    public ContextMenuProperties cloneWithSkin() {
        return this.cloneToBuilder()
                .skin(skin)
                .build();
    }

    /**
     * @return Cloned object with a clone of the skin as the current ContextMenuProperties
     */
    public ContextMenuProperties cloneSkin() {
        return this.cloneToBuilder()
                .skin(skin.clone())
                .build();
    }

    public static class Builder<T extends ContextMenuProperties> {
        protected final T properties;

        protected Builder(T properties) {
            this.properties = properties;
        }

        public Builder<T> backgroundColor(Color backgroundColor) {
            properties.backgroundColor = backgroundColor;
            return this;
        }

        public Builder<T> borderColor(Color borderColor) {
            properties.borderColor = borderColor;
            return this;
        }

        public Builder<T> skin(Skin skin) {
            properties.skin = skin;
            return this;
        }

        public Builder<T> borderWidth(float borderWidth) {
            properties.borderWidth = borderWidth;
            return this;
        }

        public Builder<T> padding(int padding) {
            properties.padding = padding;
            return this;
        }

        public Builder<T> heightOffset(int heightOffset) {
            properties.heightOffset = heightOffset;
            return this;
        }

        public Builder<T> drawBorder(boolean drawBorder) {
            properties.drawBorder = drawBorder;
            return this;
        }

        public Builder<T> shadow(boolean shadow) {
            properties.shadow = shadow;
            return this;
        }

        public Builder<T> roundedCorners(boolean roundedCorners) {
            properties.roundedCorners = roundedCorners;
            return this;
        }

        public Builder<T> cornerRadius(int cornerRadius) {
            properties.cornerRadius = cornerRadius;
            return this;
        }

        public Builder<T> hoverEffect(boolean hoverEffect) {
            properties.hoverEffect = hoverEffect;
            return this;
        }

        public Builder<T> hoverColor(Color hoverColor) {
            properties.hoverColor = hoverColor;
            return this;
        }

        public Builder<T> enableAnimations(boolean enableAnimations) {
            properties.enableAnimations = enableAnimations;
            return this;
        }

        public T build() {
            return properties;
        }
    }
}