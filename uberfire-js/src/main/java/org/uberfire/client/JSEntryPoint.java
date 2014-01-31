package org.uberfire.client;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.framework.BusState;
import org.jboss.errai.bus.client.framework.ClientMessageBusImpl;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.workbench.events.ApplicationReadyEvent;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import static com.google.gwt.core.client.ScriptInjector.*;
import static org.jboss.errai.ioc.client.QualifierUtil.*;

@EntryPoint
public class JSEntryPoint {

    @Inject
    private RuntimePluginsServiceProxy runtimePluginsService;

    @Inject
    private Event<ApplicationReadyEvent> appReady;

    @Inject
    private ClientMessageBus bus;

    @PostConstruct
    public void init() {
        publish();
    }

    @AfterInitialization
    public void setup() {
        runtimePluginsService.listFramworksContent( new ParameterizedCommand<Collection<String>>() {
            @Override
            public void execute( final Collection<String> response ) {
                for ( final String s : response ) {
                    ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                }
                runtimePluginsService.listPluginsContent( new ParameterizedCommand<Collection<String>>() {
                    @Override
                    public void execute( final Collection<String> response ) {
                        try {
                            for ( final String s : response ) {
                                ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                            }
                        } finally {
                            appReady.fire( new ApplicationReadyEvent() );
                        }

                    }
                } );
            }
        } );
    }

    public static void registerPlugin( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if ( JSNativePlugin.hasStringProperty( obj, "id" ) && JSNativePlugin.hasTemplate( obj ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativePlugin newNativePlugin = beanManager.lookupBean( JSNativePlugin.class ).getInstance();
            newNativePlugin.build( obj );

            final JSWorkbenchScreenActivity activity = new JSWorkbenchScreenActivity( newNativePlugin, beanManager.lookupBean( PlaceManager.class ).getInstance() );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) WorkbenchScreenActivity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSWorkbenchScreenActivity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );

            activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

    public static void registerPerspective( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if ( JSNativePlugin.hasStringProperty( obj, "id" ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativePerspective newNativePerspective = beanManager.lookupBean( JSNativePerspective.class ).getInstance();
            newNativePerspective.build( obj );

            final JSWorkbenchPerspectiveActivity activity = new JSWorkbenchPerspectiveActivity( newNativePerspective );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) PerspectiveActivity.class, JSWorkbenchPerspectiveActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePerspective.getId(), true );

            activityBeansCache.addNewPerspectiveActivity( beanManager.lookupBeans( newNativePerspective.getId() ).iterator().next() );
        }
    }

    public static void registerSplashScreen( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if ( JSNativeSplashScreen.hasStringProperty( obj, "id" ) && JSNativeSplashScreen.hasTemplate( obj ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativeSplashScreen newNativePlugin = beanManager.lookupBean( JSNativeSplashScreen.class ).getInstance();
            newNativePlugin.build( obj );

            final JSSplashScreenActivity activity = new JSSplashScreenActivity( newNativePlugin );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) SplashScreenActivity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSSplashScreenActivity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );

            activityBeansCache.addNewSplashScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

    public static void goTo( final String place ) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final PlaceManager placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();
        placeManager.goTo( new DefaultPlaceRequest( place ) );
    }

    // Alias registerPlugin with a global JS function.
    private native void publish() /*-{
        $wnd.$registerPlugin = @org.uberfire.client.JSEntryPoint::registerPlugin(Ljava/lang/Object;);
        $wnd.$registerSplashScreen = @org.uberfire.client.JSEntryPoint::registerSplashScreen(Ljava/lang/Object;);
        $wnd.$registerPerspective = @org.uberfire.client.JSEntryPoint::registerPerspective(Ljava/lang/Object;);
        $wnd.$goToPlace = @org.uberfire.client.JSEntryPoint::goTo(Ljava/lang/String;);
    }-*/;
}
