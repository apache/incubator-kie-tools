package org.uberfire.client.exporter;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.screen.JSNativeScreen;
import org.uberfire.client.screen.JSWorkbenchScreenActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
public class PluginJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerPlugin = @org.uberfire.client.exporter.PluginJSExporter::registerPlugin(Ljava/lang/Object;);
    }-*/;

    public static void registerPlugin( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if ( JSNativePlugin.hasStringProperty( obj, "id" ) && JSNativePlugin.hasTemplate( obj ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativeScreen newNativePlugin = beanManager.lookupBean( JSNativeScreen.class ).getInstance();
            newNativePlugin.build( obj );

            final JSWorkbenchScreenActivity activity = new JSWorkbenchScreenActivity( newNativePlugin, beanManager.lookupBean( PlaceManager.class ).getInstance() );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) WorkbenchScreenActivity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSWorkbenchScreenActivity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true );

            activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

}
