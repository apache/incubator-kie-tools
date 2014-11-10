package org.uberfire.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorItemsWidget extends Composite {

    @UiField
    FlowPanel items;

    public PropertyEditorItemsWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void add( Composite item ) {
        items.add( item );
    }

    public void setError(){
        items.addStyleName( "error" );
    }

    public void clearError() {
        items.removeStyleName( "error" );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemsWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}