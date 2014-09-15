package org.uberfire.wbtest.client.main;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@Named( "org.uberfire.wbtest.client.main.DefaultScreenActivity" )
public class DefaultScreenActivity extends AbstractTestScreenActivity {

    public static final String DEBUG_ID = "DefaultScreenActivity";

    private final VerticalPanel perspectives = new VerticalPanel();

    @Inject private SyncBeanManager bm;
    @Inject private PlaceManager placeManager;

    @Inject
    public DefaultScreenActivity( PlaceManager pm ) {
        super( pm );
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        perspectives.ensureDebugId( DEBUG_ID );
        perspectives.add( new Label( "Welcome to the default perspective!" ) );
        perspectives.add( new Label( "Some other perspectives you might enjoy:" ) );

        for ( final IOCBeanDef<PerspectiveActivity> perspectiveBean : bm.lookupBeans( PerspectiveActivity.class ) ) {
            Button b = new Button( perspectiveBean.getBeanClass().getName() );
            b.addClickHandler( new ClickHandler() {
                @Override
                public void onClick( ClickEvent event ) {
                    placeManager.goTo( new DefaultPlaceRequest( perspectiveBean.getBeanClass().getName() ) );
                }
            } );
            perspectives.add( b );
        }

        perspectives.add( new Label( "Have a nice day" ) );
    }

    @Override
    public IsWidget getWidget() {
        return perspectives;
    }

}
