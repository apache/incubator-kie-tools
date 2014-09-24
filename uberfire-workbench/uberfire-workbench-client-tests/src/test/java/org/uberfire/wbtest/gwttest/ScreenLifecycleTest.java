package org.uberfire.wbtest.gwttest;

import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.main.DefaultScreenActivity;
import org.uberfire.wbtest.client.panels.docking.NestingScreen;
import org.uberfire.wbtest.client.perspective.MultiPanelPerspective;

import com.google.common.base.Predicate;

public class ScreenLifecycleTest extends AbstractUberFireGwtTest {

    @Override
    public String getModuleName() {
        return "org.uberfire.wbtest.UberFireClientGwtTest";
    }

    public void testScreenActivityOnlyCreatedOneTimeOnPerspectiveLoad() throws Exception {
        final PlaceManager placeManager = IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();

        DefaultScreenActivity.instanceCount = 0;

        pollWhile( new Predicate<Void>() {
            @Override
            public boolean apply( Void input ) {
                return DefaultScreenActivity.instanceCount == 0;
            }
        } )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( 1, DefaultScreenActivity.instanceCount );
                placeManager.goTo( new DefaultPlaceRequest( MultiPanelPerspective.class.getName() ) );
            }
        })
        .thenPollWhile( new Predicate<Void>() {
            @Override
            public boolean apply( Void input ) {
                return NestingScreen.instanceCount == 0;
            }
        } )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( 1, NestingScreen.instanceCount );
            }
        } );

    }
}
