package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.user.client.ui.IsWidget;

public interface CRUDListBoxView
        extends HasRemoveItemHandlers,
        HasAddItemHandlers,
        IsWidget {

    public interface Presenter {

        void onAdd();

        void onDelete();
    }

    void addItem(String name);

    String getSelectedItem();

    void removeItem(String itemName);

    void addItemAndFireEvent(String name);
}
