package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.GwtEvent;

public class AddItemEvent
        extends GwtEvent<AddItemHandler> {


    private static Type<AddItemHandler> TYPE;
    private String itemName;

    public static Type<AddItemHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AddItemHandler>();
        }
        return TYPE;
    }

    public AddItemEvent(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public static <T> void fire(HasAddItemHandlers source, String itemName) {
        if (TYPE != null) {
            AddItemEvent event = new AddItemEvent(itemName);
            source.fireEvent(event);
        }
    }

    @Override
    public Type<AddItemHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AddItemHandler handler) {
        handler.onAddItem(this);
    }
}
