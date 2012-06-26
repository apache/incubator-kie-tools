package org.drools.guvnor.client.workbench.menu;

import java.util.Set;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.PlaceRequest;

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

    public SelectPlacePopup(Set<AbstractStaticScreenActivity> activities) {
        add( layout );

        for ( final AbstractStaticScreenActivity activity : activities ) {
            Button button = new Button( activity.getTitle() );
            button.addClickHandler( new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    SelectionEvent.fire( SelectPlacePopup.this,
                                         new PlaceRequest( activity.getTitle() ) );

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
