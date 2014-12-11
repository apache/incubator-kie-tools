package org.uberfire.wbtest.gwttest;

import static org.uberfire.wbtest.testutil.TestingPredicates.*;

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.main.DefaultScreenActivity;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.wbtest.client.perspective.MultiPanelPerspective;

public class ScreenLifecycleTest extends AbstractUberFireGwtTest {

    public void testScreenActivityOnlyCreatedOneTimeOnPerspectiveLoad() throws Exception {
        final PlaceManager placeManager = IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();

        DefaultScreenActivity.instanceCount = 0;

        pollWhile( DEFAULT_SCREEN_NOT_LOADED )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( 1, DefaultScreenActivity.instanceCount );
                placeManager.goTo( new DefaultPlaceRequest( MultiPanelPerspective.class.getName() ) );
            }
        })
        .thenPollWhile( NESTING_SCREEN_NOT_LOADED )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( 1, NestingScreen.instanceCount );
            }
        } );

    }
}
