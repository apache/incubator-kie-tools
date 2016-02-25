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

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.client.splash.JSNativeSplashScreen;
import org.uberfire.client.splash.JSSplashScreenActivity;
import org.uberfire.client.workbench.widgets.splash.SplashView;

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

            final SplashView splashView = beanManager.lookupBean( SplashView.class ).getInstance();

            final JSSplashScreenActivity activity = new JSSplashScreenActivity( newNativePlugin,
                                                                                splashView );
            final Set<Annotation> qualifiers = new HashSet<Annotation>( Arrays.asList( DEFAULT_QUALIFIERS ) );
            final SingletonBeanDef<JSSplashScreenActivity, JSSplashScreenActivity> beanDef =
                    new SingletonBeanDef<JSSplashScreenActivity, JSSplashScreenActivity>( activity,
                                                                                          JSSplashScreenActivity.class,
                                                                                          qualifiers,
                                                                                          newNativePlugin.getId(),
                                                                                          true,
                                                                                          SplashScreenActivity.class,
                                                                                          Activity.class );
            beanManager.registerBean( beanDef );
            beanManager.registerBeanTypeAlias( beanDef, SplashScreenActivity.class );
            beanManager.registerBeanTypeAlias( beanDef, Activity.class );

            activityBeansCache.addNewSplashScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

}
