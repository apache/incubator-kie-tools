package org.kie.workbench.common.screens.datamodeller.client.widgets.common.properties;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.properties.editor.client.widgets.AbstractPropertyEditorWidget;
import org.uberfire.ext.properties.editor.model.PropertyEditorFieldInfo;

public abstract class BasePopupPropertyEditorWidget extends AbstractPropertyEditorWidget {

    interface BasePopupPropertyEditorWidgetUiBinder extends UiBinder<Widget, BasePopupPropertyEditorWidget> {

    }

    private static BasePopupPropertyEditorWidgetUiBinder uiBinder = GWT.create( BasePopupPropertyEditorWidgetUiBinder.class );

    @UiField
    TextBox propertyTextBox;

    @UiField
    InputAddOn propertyAddOn;

    Icon editIcon = new Icon( IconType.EDIT );

    ValueChangeHandler<String> valueChangeHandler;

    PropertyEditorFieldInfo property;

    public BasePopupPropertyEditorWidget() {

        initWidget( uiBinder.createAndBindUi( this ) );

        propertyTextBox.setReadOnly( true );

        editIcon.addDomHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                openEditionPopup();
            }
        }, ClickEvent.getType() );

        propertyAddOn.addAppendWidget( editIcon );
    }

    public void setValue( String value ) {
        propertyTextBox.setText( value );
    }

    public String getValue() {
        return propertyTextBox.getText();
    }

    public PropertyEditorFieldInfo getProperty() {
        return property;
    }

    public void setProperty( PropertyEditorFieldInfo property ) {
        this.property = property;
    }

    public void addChangeHandler( ValueChangeHandler<String> changeHandler ) {
        this.valueChangeHandler = changeHandler;
    }

    protected void openEditionPopup() {

        final PropertyEditionPopup popup = createEditionPopup( property );

        popup.setStringValue( getValue() );
        popup.setOkCommand( new Command() {
            @Override
            public void execute() {
                valueChangeHandler.onValueChange( new StringValueChangeEvent( popup.getStringValue() ) );
                setValue( popup.getStringValue() );
            }
        } );
        popup.show();
    }

    protected abstract PropertyEditionPopup createEditionPopup( PropertyEditorFieldInfo property );

    public static class StringValueChangeEvent extends ValueChangeEvent<String> {

        public StringValueChangeEvent( String value ) {
            super( value );
        }
    }
}
