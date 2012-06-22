package org.drools.guvnor.client.workbench.menu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.IPlaceRequest;
import org.drools.guvnor.client.mvp.IPlaceRequestFactory;
import org.drools.guvnor.client.mvp.PlaceManager;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;

@ApplicationScoped
public class GuvnorMenu extends Composite {

    @Inject
    private IOCBeanManager iocManager;

    @Inject
    private PlaceManager   placeManager;

    public GuvnorMenu() {

        Button add = new Button( "Add (mr. Rikkola)" );

        add.addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                final Set<IPlaceRequestFactory> factories = new HashSet<IPlaceRequestFactory>();

                Collection<IOCBeanDef> beans = iocManager.lookupBeans( IPlaceRequestFactory.class );

                for ( IOCBeanDef bean : beans ) {
                    final IPlaceRequestFactory instance = (IPlaceRequestFactory) bean.getInstance();
                    factories.add( instance );
                }

                SelectPlacePopup popup = new SelectPlacePopup( factories );
                popup.addSelectionHandler( new SelectionHandler<IPlaceRequest>() {
                    @Override
                    public void onSelection(SelectionEvent<IPlaceRequest> event) {
                        placeManager.goTo( event.getSelectedItem().getPlace() );
                    }
                } );

                popup.show();
            }
        } );

        initWidget( add );
    }
}
