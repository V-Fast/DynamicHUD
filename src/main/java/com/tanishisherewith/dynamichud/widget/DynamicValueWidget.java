package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.Util;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Supplier;

/**
 * DynamicValueWidget is an abstract extension of Widget that automatically handles DynamicValueRegistry to retrieve supplier data.
 * @see GraphWidget
 * @see TextWidget
 */
public abstract class DynamicValueWidget extends Widget {
    protected String registryKey;
    protected String registryID;
    protected Supplier<?> valueSupplier;

    public DynamicValueWidget(WidgetData<?> data, String modID, String registryID, String registryKey) {
        this(data, modID, Anchor.CENTER, registryID, registryKey);
    }

    public DynamicValueWidget(WidgetData<?> data, String modId, Anchor anchor, String registryID, String registryKey) {
        super(data, modId, anchor);
        boolean emptyCheck = Util.warnIfTrue(registryID == null || registryID.isEmpty(), "Empty registry ID, using global registry. Widget: {}", this.toString());
        this.registryID = emptyCheck ? DynamicValueRegistry.GLOBAL_ID : registryID;
        this.registryKey = registryKey;

        initializeValueSupplier();
    }

    protected void initializeValueSupplier() {
        this.valueSupplier = DynamicValueRegistry.getValue(registryID, registryKey);
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putString("RegistryID", registryID);
        tag.putString("RegistryKey", registryKey);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        registryID = tag.getString("RegistryID").orElse(DynamicValueRegistry.GLOBAL_ID);
        registryKey = tag.getString("RegistryKey").orElse("null");

        initializeValueSupplier();

        if (valueSupplier == null) throw new IllegalStateException("Value supplier remains null");
    }

    /**
     * Subclasses should implement this to get value from the supplier.
     */
    public abstract Object getValue();

    public abstract static class DynamicValueWidgetBuilder<T extends DynamicValueWidgetBuilder<T, W>, W extends DynamicValueWidget> extends WidgetBuilder<T, W> {
        protected String registryKey = "";
        protected String registryID = null;

        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }

        public T registryKey(String registryKey) {
            this.registryKey = registryKey;
            return self();
        }

        public T registryID(String registryID) {
            this.registryID = registryID;
            return self();
        }
    }
}