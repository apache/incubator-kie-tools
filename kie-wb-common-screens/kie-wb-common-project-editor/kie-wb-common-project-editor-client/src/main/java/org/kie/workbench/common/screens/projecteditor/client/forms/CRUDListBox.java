package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;

import javax.inject.Inject;

public class CRUDListBox
        implements HasRemoveItemHandlers,
        HasAddItemHandlers,
        IsWidget,
        CRUDListBoxView.Presenter {

    private CRUDListBoxView view;
    private TextBoxFormPopup newItemPopup;

    public CRUDListBox() {
    }

    @Inject
    public CRUDListBox(final CRUDListBoxView view,
                       TextBoxFormPopup newItemPopup) {
        this.view = view;
        this.newItemPopup = newItemPopup;
        view.setPresenter(this);
    }

    @Override
    public void onAdd() {
        newItemPopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                view.addItemAndFireEvent(name);
            }
        });
    }

    @Override
    public void onDelete() {
        if (view.getSelectedItem() != null) {
            view.removeItem(view.getSelectedItem());
        }
    }

    @Override
    public HandlerRegistration addRemoveItemHandler(RemoveItemHandler handler) {
        return view.addRemoveItemHandler(handler);
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.fireEvent(event);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public void addPackageName(String name) {
        view.addItem(name);
    }

    @Override
    public HandlerRegistration addAddItemHandler(AddItemHandler handler) {
        return view.addAddItemHandler(handler);
    }
}
