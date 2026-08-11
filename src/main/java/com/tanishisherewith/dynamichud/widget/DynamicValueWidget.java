package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.Util;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

/**
 * DynamicValueWidget is an abstract extension of Widget that automatically handles DynamicValueRegistry to retrieve supplier data.
 * @see GraphWidget
 * @see TextWidget
 */
public abstract class DynamicValueWidget extends Widget {
    protected Identifier valueId;
    protected Supplier<?> valueSupplier;

    public DynamicValueWidget(WidgetData<?> data, String modID, Identifier valueId) {
        this(data, modID, Anchor._default(), valueId);
    }

    public DynamicValueWidget(WidgetData<?> data, String modId, Anchor anchor, Identifier valueId) {
        super(data, modId, anchor);
        Util.warnIfTrue(valueId == null,"Null value Identifier, using fallback. Widget: {}", this.toString());
        this.valueId = valueId == null ? Identifier.fromNamespaceAndPath("dynamichud", "null") : valueId;

        initializeValueSupplier();
    }

    protected void initializeValueSupplier() {
        this.valueSupplier = DynamicValueRegistry.get(valueId);
    }

    @Override
    public void writeToTag(CompoundTag tag) {
        super.writeToTag(tag);
        tag.putString("ValueId", valueId.toString());
    }

    @Override
    public void readFromTag(CompoundTag tag) {
        super.readFromTag(tag);

        String idString = tag.getString("ValueId").orElse("dynamichud:null");
        this.valueId = Identifier.tryParse(idString);
        Util.warnIfTrue(valueId == null,"Failed to parse Identifier '{}', using fallback. Widget: {}", idString, this.toString());
        if(this.valueId == null) {
            this.valueId = Identifier.fromNamespaceAndPath("dynamichud", "null");
        }
        initializeValueSupplier();

        if (valueSupplier == null) {
            throw new IllegalStateException("Value supplier cannot be null for " + valueId + ". Was it registered in DynamicValueRegistry?");
        }
    }


    /**
     * Subclasses should implement this to get value from the supplier.
     */
    public abstract Object getValue();

    public Identifier getValueId() {
        return valueId;
    }

    public abstract static class DynamicValueWidgetBuilder<T extends DynamicValueWidgetBuilder<T, W>, W extends DynamicValueWidget> extends WidgetBuilder<T, W> {
        protected Identifier valueId;

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public T valueId(Identifier valueId) {
            this.valueId = valueId;
            return self();
        }

        public T valueId(String namespace, String path) {
            this.valueId = Identifier.fromNamespaceAndPath(namespace, path);
            return self();
        }
    }
}