package org.drools.guvnor.client.workbench.menu;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.drools.guvnor.client.mvp.IPlaceRequest;

import java.util.Set;

public class SelectPlacePopup extends PopupPanel implements HasSelectionHandlers<IPlaceRequest> {

    private final VerticalPanel layout = new VerticalPanel();

    public SelectPlacePopup(Set<IPlaceRequest> places) {
        add(layout);

        for (final IPlaceRequest placeRequest : places) {
            Button button = new Button(placeRequest.getNameToken());
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    SelectionEvent.fire(SelectPlacePopup.this,
                            placeRequest);

                    SelectPlacePopup.this.hide();


                }
            });
            layout.add(button);
        }
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<IPlaceRequest> handler) {
        return addHandler(handler,
                SelectionEvent.getType());
    }
}
