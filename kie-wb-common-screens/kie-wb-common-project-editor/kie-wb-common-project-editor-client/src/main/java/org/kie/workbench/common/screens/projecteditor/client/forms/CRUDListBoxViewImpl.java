package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CRUDListBoxViewImpl
        extends Composite
        implements CRUDListBoxView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, CRUDListBoxViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    ListBox listBox;


    public CRUDListBoxViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getSelectedItem() {
        return listBox.getValue(listBox.getSelectedIndex());
    }

    @Override
    public void removeItem(String itemName) {
        for (int i = 0; i < listBox.getItemCount(); i++) {
            if (listBox.getValue(i).equals(itemName)) {
                listBox.removeItem(i);
            }
        }
        RemoveItemEvent.fire(this, itemName);
    }

    @Override
    public void addItemAndFireEvent(String name) {
        AddItemEvent.fire(this, name);
        addItem(name);
    }

    @Override
    public void addItem(String name) {
        listBox.addItem(name);
    }

    @Override
    public HandlerRegistration addRemoveItemHandler(RemoveItemHandler handler) {
        return addHandler(handler, RemoveItemEvent.getType());
    }

    @Override
    public HandlerRegistration addAddItemHandler(AddItemHandler handler) {
        return addHandler(handler, AddItemEvent.getType());
    }

    @UiHandler("addButton")
    public void onAdd(ClickEvent clickEvent) {
        presenter.onAdd();
    }

    @UiHandler("deleteButton")
    public void onDelete(ClickEvent clickEvent) {
        presenter.onDelete();
    }
}
