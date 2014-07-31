package org.uberfire.client.exporter;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.perspective.JSNativePerspective;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.perspective.JSWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
public class PerspectiveJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerPerspective = @org.uberfire.client.exporter.PerspectiveJSExporter::registerPerspective(Ljava/lang/Object;);
    }-*/;

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

}
