package org.uberfire.client.exporter;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.splash.JSNativeSplashScreen;
import org.uberfire.client.splash.JSSplashScreenActivity;

import com.google.gwt.core.client.JavaScriptObject;

@ApplicationScoped
public class SplashScreenJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }
    private native void publish() /*-{
        $wnd.$registerSplashScreen = @org.uberfire.client.exporter.SplashScreenJSExporter::registerSplashScreen(Ljava/lang/Object;);
    }-*/;


    public static void registerSplashScreen( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if ( JSNativeSplashScreen.hasStringProperty( obj, "id" ) && JSNativeSplashScreen.hasTemplate( obj ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativeSplashScreen newNativePlugin = beanManager.lookupBean( JSNativeSplashScreen.class ).getInstance();
            newNativePlugin.build( obj );

            final JSSplashScreenActivity activity = new JSSplashScreenActivity( newNativePlugin );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) SplashScreenActivity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSSplashScreenActivity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );

            activityBeansCache.addNewSplashScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

}
