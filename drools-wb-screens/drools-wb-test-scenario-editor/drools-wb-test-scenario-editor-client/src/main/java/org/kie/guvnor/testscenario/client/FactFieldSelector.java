package org.kie.guvnor.testscenario.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class FactFieldSelector
        extends Composite
        implements HasSelectionHandlers<String> {

    @UiField
    ListBox fieldsListBox;

    @UiField
    Button ok;

    interface FactFieldSelectorUiBinder
            extends
            UiBinder<Widget, FactFieldSelector> {
    }

    private static FactFieldSelectorUiBinder uiBinder = GWT.create(FactFieldSelectorUiBinder.class);

    public FactFieldSelector() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void addField(String field) {
        fieldsListBox.addItem(field);
    }

    @UiHandler("ok")
    public void handleClick(ClickEvent event) {
        SelectionEvent.fire(this, fieldsListBox.getItemText(fieldsListBox.getSelectedIndex()));
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<String> selectionHandler) {
        return addHandler(selectionHandler, SelectionEvent.getType());
    }
}