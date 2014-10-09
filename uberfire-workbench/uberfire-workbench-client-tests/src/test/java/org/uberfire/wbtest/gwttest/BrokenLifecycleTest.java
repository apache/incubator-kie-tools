package org.uberfire.wbtest.gwttest;

import static com.google.common.base.Predicates.*;
import static org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase.*;
import static org.uberfire.debug.Debug.*;
import static org.uberfire.wbtest.gwttest.TestingPredicates.*;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.IOC;
import org.uberfire.client.mvp.ActivityLifecycleError;
import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.breakable.BreakableMenuScreen;
import org.uberfire.wbtest.client.breakable.BreakablePerspective;
import org.uberfire.wbtest.client.breakable.BreakableScreen;
import org.uberfire.wbtest.client.main.DefaultPerspectiveActivity;
import org.uberfire.wbtest.client.main.DefaultScreenActivity;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

import com.google.common.base.Predicate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * Tests for bulletproofing and notification for activities that throw exceptions from their lifecycle methods.
 */
public class BrokenLifecycleTest extends AbstractUberFireGwtTest {

    private PlaceManager placeManager;
    private ActivityManager activityManager;
    private PanelManager panelManager;
    private PerspectiveManager perspectiveManager;

    private final LifecycleErrorLogger lifecycleErrorLog = new LifecycleErrorLogger();

    private static final Predicate<Void> BREAKABLE_MENU_NOT_VISIBLE = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            return DOM.getElementById( shortName( BreakableMenuScreen.class ) ) == null;
        }
    };

    private static final Predicate<Void> BREAKABLE_SCREEN_NOT_VISIBLE = new Predicate<Void>() {
        @Override
        public boolean apply( Void input ) {
            Element element = DOM.getElementById( shortName( BreakableScreen.class ) );
            return element == null;
        }
    };

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        placeManager = IOC.getBeanManager().lookupBean( PlaceManager.class ).getInstance();
        activityManager = IOC.getBeanManager().lookupBean( ActivityManager.class ).getInstance();
        panelManager = IOC.getBeanManager().lookupBean( PanelManager.class ).getInstance();
        perspectiveManager = IOC.getBeanManager().lookupBean( PerspectiveManager.class ).getInstance();
        CDI.subscribeLocal( ActivityLifecycleError.class.getName(), lifecycleErrorLog );
    }

    /**
     * Tests that we remain on the current perspective when the requested one can't be started.
     */
    public void testBrokenPerspectiveStartup() throws Exception {
        final PlaceRequest brokenPerspectivePlace = DefaultPlaceRequest.parse( BreakablePerspective.class.getName() + "?broken=" + STARTUP );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenPerspectivePlace );
            }
        } )
        .thenDelay( 500 )
        .thenPollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultScreenActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenPerspectivePlace ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( BreakableMenuScreen.class.getName() ) );
                assertTrue( lifecycleErrorLog.contains( BreakablePerspective.class, STARTUP ) );
            }
        } );
    }

    /**
     * Tests that we remain on the current perspective when the requested one can't be opened.
     */
    public void testBrokenPerspectiveOpen() throws Exception {
        final PlaceRequest brokenPerspectivePlace = DefaultPlaceRequest.parse( BreakablePerspective.class.getName() + "?broken=" + OPEN );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenPerspectivePlace );
            }
        } )
        .thenDelay( 500 )
        .thenPollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( DefaultPerspectiveActivity.class.getName(),
                              perspectiveManager.getCurrentPerspective().getPlace().getIdentifier() );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultScreenActivity.class.getName() ) );
                assertNull( placeManager.getActivity( brokenPerspectivePlace ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenPerspectivePlace ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( BreakableMenuScreen.class.getName() ) );
                assertTrue( lifecycleErrorLog.contains( BreakablePerspective.class, OPEN ) );
            }
        } );
    }

    /**
     * Tests that switching away from a perspective with a broken onClose works as if the onClose was functional.
     * (And that an error is fired).
     */
    public void testBrokenPerspectiveClose() throws Exception {
        final PlaceRequest brokenPerspectivePlace = DefaultPlaceRequest.parse( BreakablePerspective.class.getName() + "?broken=" + CLOSE );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenPerspectivePlace );
            }
        } )
        .thenPollWhile( BREAKABLE_MENU_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( DefaultPerspectiveActivity.class.getName() );
            }
        } )
        .thenPollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenPerspectivePlace ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( BreakableMenuScreen.class.getName() ) );
                assertTrue( lifecycleErrorLog.contains( BreakablePerspective.class, CLOSE ) );
            }
        } );
    }

    /**
     * Tests that switching away from a perspective with a broken onShutdown works as if the onShutdown was functional.
     * (And that an error is fired).
     */
    public void testBrokenPerspectiveShutdown() throws Exception {
        final PlaceRequest brokenPerspectivePlace = DefaultPlaceRequest.parse( BreakablePerspective.class.getName() + "?broken=" + SHUTDOWN );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenPerspectivePlace );
            }
        } )
        .thenPollWhile( BREAKABLE_MENU_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( DefaultPerspectiveActivity.class.getName() );
            }
        } )
        .thenPollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenPerspectivePlace ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( BreakableMenuScreen.class.getName() ) );
                assertTrue( lifecycleErrorLog.contains( BreakablePerspective.class, SHUTDOWN ) );
            }
        } );
    }

    /**
     * Tests that launching a screen with broken startup doesn't corrupt the *Manager state.
     */
    public void testBrokenScreenStartup() throws Exception {
        final PlaceRequest brokenScreenPlace = DefaultPlaceRequest.parse( BreakableScreen.class.getName() + "?broken=" + STARTUP );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenScreenPlace );
            }
        } )
        .thenDelay( 500 )
        .thenPollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultScreenActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenScreenPlace ) );
                assertPanelDoesNotContain( panelManager.getRoot(), brokenScreenPlace );
                assertTrue( lifecycleErrorLog.contains( BreakableScreen.class, STARTUP ) );
            }
        } );
    }

    /**
     * Tests that launching a screen with broken open doesn't corrupt the *Manager state.
     */
    public void testBrokenScreenOpen() throws Exception {
        final PlaceRequest brokenScreenPlace = DefaultPlaceRequest.parse( BreakableScreen.class.getName() + "?broken=" + OPEN );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenScreenPlace );
            }
        } )
        .thenDelay( 500 )
        .thenPollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultScreenActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenScreenPlace ) );
                assertPanelDoesNotContain( panelManager.getRoot(), brokenScreenPlace );
                assertTrue( lifecycleErrorLog.contains( BreakableScreen.class, OPEN ) );
            }
        } );
    }

    /**
     * Tests that closing a screen with broken close doesn't corrupt the *Manager state.
     */
    public void testBrokenScreenClose() throws Exception {
        final PlaceRequest brokenScreenPlace = DefaultPlaceRequest.parse( BreakableScreen.class.getName() + "?broken=" + CLOSE );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenScreenPlace );
            }
        } )
        .thenPollWhile( BREAKABLE_SCREEN_NOT_VISIBLE )
        .thenDelay( 500 )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.closePlace( brokenScreenPlace );
            }
        } )
        .thenPollWhile( not( BREAKABLE_SCREEN_NOT_VISIBLE ) )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultScreenActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenScreenPlace ) );
                assertPanelDoesNotContain( panelManager.getRoot(), brokenScreenPlace );
                assertTrue( lifecycleErrorLog.contains( BreakableScreen.class, CLOSE ) );
            }
        } );
    }

    /**
     * Tests that closing a screen with broken shutdown doesn't corrupt the *Manager state.
     */
    public void testBrokenScreenShutdown() throws Exception {
        final PlaceRequest brokenScreenPlace = DefaultPlaceRequest.parse( BreakableScreen.class.getName() + "?broken=" + SHUTDOWN );
        pollWhile( DEFAULT_SCREEN_NOT_VISIBLE )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.goTo( brokenScreenPlace );
            }
        } )
        .thenPollWhile( BREAKABLE_SCREEN_NOT_VISIBLE )
        .thenDelay( 500 )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                placeManager.closePlace( brokenScreenPlace );
            }
        } )
        .thenPollWhile( not( BREAKABLE_SCREEN_NOT_VISIBLE ) )
        .thenDo( new Runnable() {
            @Override
            public void run() {
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultPerspectiveActivity.class.getName() ) );
                assertEquals( PlaceStatus.OPEN, placeManager.getStatus( DefaultScreenActivity.class.getName() ) );
                assertEquals( PlaceStatus.CLOSE, placeManager.getStatus( brokenScreenPlace ) );
                assertPanelDoesNotContain( panelManager.getRoot(), brokenScreenPlace );
                assertTrue( lifecycleErrorLog.contains( BreakableScreen.class, SHUTDOWN ) );
            }
        } );
    }

    public void assertPanelDoesNotContain( PanelDefinition panelDef,
                                           PlaceRequest place ) {
        for ( PartDefinition part : panelDef.getParts() ) {
            if ( part.getPlace().equals( place ) ) {
                fail( "Found a part for " + place + " in a panel it should not have been in" );
            }
        }
    }

    public static class LifecycleErrorLogger extends AbstractCDIEventCallback<ActivityLifecycleError> {
        List<ActivityLifecycleError> errors = new ArrayList<ActivityLifecycleError>();

        @Override
        protected void fireEvent( ActivityLifecycleError event ) {
            errors.add( event );
        }

        public boolean contains( Class<?> activityType, LifecyclePhase failedCallback ) {
            for ( ActivityLifecycleError error : errors ) {
                if ( error.getFailedActivity().getClass() == activityType && error.getFailedCall() == failedCallback ) {
                    return true;
                }
            }
            return false;
        }
    }

}
