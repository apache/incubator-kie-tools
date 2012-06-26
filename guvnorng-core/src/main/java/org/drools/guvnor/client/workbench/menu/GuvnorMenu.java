package org.drools.guvnor.client.workbench.menu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.mvp.AbstractStaticScreenActivity;
import org.drools.guvnor.client.mvp.IPlaceRequest;
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

    @SuppressWarnings("rawtypes")
    public GuvnorMenu() {

        Button add = new Button( "Static places" );

        add.addClickHandler( new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {

                final Set<AbstractStaticScreenActivity> activities = new HashSet<AbstractStaticScreenActivity>();

                Collection<IOCBeanDef> activityBeans = iocManager.lookupBeans( AbstractStaticScreenActivity.class );

                for ( IOCBeanDef activityBean : activityBeans ) {
                    final AbstractStaticScreenActivity instance = (AbstractStaticScreenActivity) activityBean.getInstance();
                    activities.add( instance );
                }

                SelectPlacePopup popup = new SelectPlacePopup( activities );
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
