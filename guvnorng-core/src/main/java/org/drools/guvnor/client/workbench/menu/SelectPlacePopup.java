package org.drools.guvnor.client.workbench.menu;

import java.util.Set;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.IPlaceRequestFactory;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectPlacePopup extends PopupPanel
    implements
    HasSelectionHandlers<IPlaceRequest> {

    private final VerticalPanel layout = new VerticalPanel();

    public SelectPlacePopup(Set<IPlaceRequestFactory> factories) {
        add( layout );

        for ( final IPlaceRequestFactory factory : factories ) {
            Button button = new Button( factory.getFactoryName() );
            button.addClickHandler( new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    SelectionEvent.fire( SelectPlacePopup.this,
                                         factory.makePlace() );

                    SelectPlacePopup.this.hide();

                }
            } );
            layout.add( button );
        }
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<IPlaceRequest> handler) {
        return addHandler( handler,
                           SelectionEvent.getType() );
    }
}
