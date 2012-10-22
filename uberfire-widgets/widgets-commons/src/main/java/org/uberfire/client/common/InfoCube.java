package org.uberfire.client.common;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class InfoCube
        extends Composite
        implements HasClickHandlers {

    interface PerspectiveButtonBinder extends UiBinder<Widget, InfoCube> {

    }

    private static PerspectiveButtonBinder uiBinder = GWT.create(PerspectiveButtonBinder.class);

    @UiField
    Label title;

    @UiField
    Label content;

    public InfoCube() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        this.title.setText(title);
    }

    public void setContent(String text) {
        content.setText(text);
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler,
                ClickEvent.getType());
    }
}
