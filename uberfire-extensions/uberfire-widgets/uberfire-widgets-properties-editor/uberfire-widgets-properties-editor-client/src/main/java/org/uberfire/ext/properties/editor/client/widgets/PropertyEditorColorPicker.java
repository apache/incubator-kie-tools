package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.InputGroupAddon;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.properties.editor.model.validators.ColorValidator;
import org.uberfire.ext.widgets.common.client.colorpicker.ColorPickerDialog;
import org.uberfire.ext.widgets.common.client.colorpicker.dialog.DialogClosedEvent;
import org.uberfire.ext.widgets.common.client.colorpicker.dialog.DialogClosedHandler;

public class PropertyEditorColorPicker extends AbstractPropertyEditorWidget {

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorColorPicker> {}
    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    @UiField
    InputGroupAddon icon;

    @UiField
    TextBox colorTextBox;

    public PropertyEditorColorPicker() {
        initWidget( uiBinder.createAndBindUi( this ) );

        icon.addDomHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                openColorPickerDialog();
            }
        }, ClickEvent.getType());
    }

    public void setValue(String value) {
        if (ColorValidator.isValid(value)) {
            colorTextBox.setValue(value);
        }
    }

    public String getValue() {
        return colorTextBox.getValue();
    }

    public void addChangeHandler(ValueChangeHandler<String> changeHandler ) {
        colorTextBox.addValueChangeHandler(changeHandler);
    }

    protected void openColorPickerDialog() {
        final ColorPickerDialog dlg = new ColorPickerDialog();
        dlg.getElement().getStyle().setZIndex(9999);
        String color = getValue();
        if (ColorValidator.isValid(color)) dlg.setColor(color);
        dlg.addDialogClosedHandler(new DialogClosedHandler() {
            public void dialogClosed(DialogClosedEvent event) {
                if (!event.isCanceled()) {
                    colorTextBox.setValue(dlg.getColor().toUpperCase(), true);
                }
            }
        });
        dlg.showRelativeTo(icon);
    }

}