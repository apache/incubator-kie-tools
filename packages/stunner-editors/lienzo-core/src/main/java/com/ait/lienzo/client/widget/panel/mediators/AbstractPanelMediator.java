package com.ait.lienzo.client.widget.panel.mediators;

import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;

public abstract class AbstractPanelMediator<T> {

    private final Supplier<LienzoBoundsPanel> panelSupplier;

    public AbstractPanelMediator(final Supplier<LienzoBoundsPanel> panelSupplier) {
        this.panelSupplier = panelSupplier;
    }

    public T enable() {
        if (isEnabled()) {
            return cast();
        }
        getLayer().setListening(false);
        onEnable();
        return cast();
    }

    protected abstract void onEnable();

    public T disable() {
        if (!isEnabled()) {
            return cast();
        }
        getLayer().setListening(true);
        onDisable();
        return cast();
    }

    protected abstract void onDisable();

    public abstract boolean isEnabled();

    public void removeHandler() {
        disable();
        onRemoveHandler();
    }

    protected void onRemoveHandler() {
    }

    @SuppressWarnings("unchecked")
    private T cast() {
        return (T) this;
    }

    protected LienzoBoundsPanel getPanel() {
        return panelSupplier.get();
    }

    protected Layer getLayer() {
        return getPanel().getLayer();
    }
}
