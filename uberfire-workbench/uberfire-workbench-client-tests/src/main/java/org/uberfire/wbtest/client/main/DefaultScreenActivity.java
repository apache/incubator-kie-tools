package org.uberfire.wbtest.client.main;

import static org.uberfire.debug.Debug.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestScreenActivity;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

@Dependent
@Named( "org.uberfire.wbtest.client.main.DefaultScreenActivity" )
public class DefaultScreenActivity extends AbstractTestScreenActivity {

    public static final String DEBUG_ID = "DefaultScreenActivity";

    /**
     * Gets incremented every time a new instance of this class is created. Tests that want to assert on how many
     * instances have been created by a specific operation are free to reset this to 0.
     */
    public static int instanceCount;

    private final VerticalPanel perspectives = new VerticalPanel();

    @Inject private SyncBeanManager bm;
    @Inject private PlaceManager placeManager;

    @Inject
    public DefaultScreenActivity( PlaceManager pm ) {
        super( pm );
        instanceCount++;
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        perspectives.ensureDebugId( DEBUG_ID );
        perspectives.add( new Label( "Welcome to the default perspective!" ) );

        perspectives.add( new Label( "Some other perspectives you might enjoy:" ) );
        perspectives.add( makeLinksInColumns( bm.lookupBeans( PerspectiveActivity.class ) ) );

        perspectives.add( new Label( "All screens of this app:" ) );
        perspectives.add( makeLinksInColumns( bm.lookupBeans( WorkbenchScreenActivity.class ) ) );

        perspectives.add( new Label( "Have a nice day" ) );
    }

    private <A> Panel makeLinksInColumns( Collection<IOCBeanDef<A>> activityBeans ) {
        List<IOCBeanDef<A>> sortedBeans = new ArrayList<IOCBeanDef<A>>( activityBeans );
        Collections.sort( sortedBeans, new Comparator<IOCBeanDef<A>>() {
            @Override
            public int compare( IOCBeanDef<A> o1,
                                IOCBeanDef<A> o2 ) {
                return shortName( o1.getBeanClass() ).compareTo( shortName( o2.getBeanClass() ) );
            }
        } );

        final int colCount = 3;
        final int rowCount = (int) Math.ceil( (double) sortedBeans.size() / (double) colCount );

        HorizontalPanel columns = new HorizontalPanel();
        columns.setSpacing( 20 );

        for ( int c = 0; c < colCount; c++ ) {
            VerticalPanel col = new VerticalPanel();
            columns.add( col );
            for ( int r = 0; r < rowCount; r++ ) {
                int index = r + (rowCount * c);
                if ( index >= sortedBeans.size() ) break;
                final IOCBeanDef<A> activityBean = sortedBeans.get( index );
                Anchor a = new Anchor( shortName( activityBean.getBeanClass() ) );
                a.addClickHandler( new ClickHandler() {
                    @Override
                    public void onClick( ClickEvent event ) {
                        placeManager.goTo( new DefaultPlaceRequest( activityBean.getBeanClass().getName() ) );
                    }
                } );
                col.add( a );
            }
        }

        return columns;
    }

    @Override
    public IsWidget getWidget() {
        return perspectives;
    }

}
