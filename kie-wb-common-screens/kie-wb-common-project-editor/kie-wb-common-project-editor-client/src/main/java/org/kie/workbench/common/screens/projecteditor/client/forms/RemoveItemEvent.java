package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.GwtEvent;

public class RemoveItemEvent
        extends GwtEvent<RemoveItemHandler> {


    private static Type<RemoveItemHandler> TYPE;
    private String itemName;

    public static Type<RemoveItemHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<RemoveItemHandler>();
        }
        return TYPE;
    }

    public RemoveItemEvent(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public static <T> void fire(HasRemoveItemHandlers source, String itemName) {
        if (TYPE != null) {
            RemoveItemEvent event = new RemoveItemEvent(itemName);
            source.fireEvent(event);
        }
    }

    @Override
    public Type<RemoveItemHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RemoveItemHandler handler) {
        handler.onRemoveItem(this);
    }
}
