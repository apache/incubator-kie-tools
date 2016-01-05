/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.exporter;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.splash.JSNativeSplashScreen;
import org.uberfire.client.splash.JSSplashScreenActivity;
import org.uberfire.client.workbench.widgets.splash.SplashView;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

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

            final SplashView splashView = beanManager.lookupBean( SplashView.class ).getInstance();

            final JSSplashScreenActivity activity = new JSSplashScreenActivity( newNativePlugin,
                                                                                splashView );

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) SplashScreenActivity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSSplashScreenActivity.class, JSSplashScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );

            activityBeansCache.addNewSplashScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

}
