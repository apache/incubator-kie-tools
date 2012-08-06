package org.uberfire.client.workbench.menu;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.client.mvp.AbstractScreenActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.shared.mvp.PlaceRequest;

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

                final Set<AbstractScreenActivity> activities = new HashSet<AbstractScreenActivity>();

                Collection<IOCBeanDef<AbstractScreenActivity>> activityBeans = iocManager.lookupBeans( AbstractScreenActivity.class );

                for ( IOCBeanDef activityBean : activityBeans ) {
                    final AbstractScreenActivity instance = (AbstractScreenActivity) activityBean.getInstance();
                    activities.add( instance );
                }

                SelectPlacePopup popup = new SelectPlacePopup( activities );
                popup.addSelectionHandler( new SelectionHandler<PlaceRequest>() {
                    @Override
                    public void onSelection(SelectionEvent<PlaceRequest> event) {
                        placeManager.goTo( event.getSelectedItem().getPlace() );
                    }
                } );

                popup.show();
            }
        } );

        initWidget( add );
    }
}
