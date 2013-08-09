package org.kie.workbench.common.screens.projecteditor.client.forms;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopup;

public class CRUDListBox
        implements HasRemoveItemHandlers,
        IsWidget,
        CRUDListBoxView.Presenter {

    private CRUDListBoxView view;
    private TextBoxFormPopup newItemPopup;

    public CRUDListBox() {
    }

    public CRUDListBox(final CRUDListBoxView view,
                       TextBoxFormPopup newItemPopup) {
        this.view = view;
        this.newItemPopup = newItemPopup;
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
}
