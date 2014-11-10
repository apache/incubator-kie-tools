package org.uberfire.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class PropertyEditorItemWidget extends Composite {

    @UiField
    FlowPanel item;


    public PropertyEditorItemWidget() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    public void add(Widget widget){
        item.add( widget );

    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemWidget> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}