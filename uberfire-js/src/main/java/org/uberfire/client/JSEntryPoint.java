package org.uberfire.client;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.backend.plugin.RuntimePluginsService;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.ApplicationReadyEvent;

import static com.google.gwt.core.client.ScriptInjector.*;
import static org.jboss.errai.ioc.client.QualifierUtil.*;

@EntryPoint
public class JSEntryPoint {

    @Inject
    private Caller<RuntimePluginsService> runtimePluginsService;

    @Inject
    private Event<ApplicationReadyEvent> appReady;

    @PostConstruct
    public void init() {
        publish();
    }

    @AfterInitialization
    public void setup() {
        runtimePluginsService.call( new RemoteCallback<Collection<String>>() {
            @Override
            public void callback( Collection<String> response ) {
                for ( final String s : response ) {
                    ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                }
                runtimePluginsService.call( new RemoteCallback<Collection<String>>() {
                    @Override
                    public void callback( Collection<String> response ) {
                        try {
                            for ( final String s : response ) {
                                ScriptInjector.fromString( s ).setWindow( TOP_WINDOW ).inject();
                            }
                        } finally {
                            appReady.fire( new ApplicationReadyEvent() );
                        }
                    }

                } ).listPluginsContent();
            }
        } ).listFramworksContent();
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

    public static void goTo( final String place ) {
        final SyncBeanManager beanManager = IOC.getBeanManager();
        final PlaceManager placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();
        placeManager.goTo( new DefaultPlaceRequest( place ) );
    }

    // Alias registerPlugin with a global JS function.
    private native void publish() /*-{
        $wnd.$registerPlugin = @org.uberfire.client.JSEntryPoint::registerPlugin(Ljava/lang/Object;);
        $wnd.$registerPerspective = @org.uberfire.client.JSEntryPoint::registerPerspective(Ljava/lang/Object;);
        $wnd.$goToPlace = @org.uberfire.client.JSEntryPoint::goTo(Ljava/lang/String;);
    }-*/;
}
