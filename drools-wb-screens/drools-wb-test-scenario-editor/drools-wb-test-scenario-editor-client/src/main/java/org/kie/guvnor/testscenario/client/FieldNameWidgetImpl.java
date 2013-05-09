package org.kie.guvnor.testscenario.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.common.SmallLabel;

public class FieldNameWidgetImpl implements IsWidget {

    private SmallLabel view = new SmallLabel();

    public FieldNameWidgetImpl(String fieldName) {
        view.setText(fieldName + ":");
        view.addClickHandler(createClickHandler());
    }

    private ClickHandler createClickHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                //TODO: -Rikkola-
            }
        };
    }

    @Override
    public Widget asWidget() {
        return view;
    }
}
