/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.screen.JSNativeScreen;
import org.uberfire.client.screen.JSWorkbenchScreenActivity;

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

            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) Activity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) WorkbenchScreenActivity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );
            ( (SyncBeanManagerImpl) beanManager ).addBean( (Class) JSWorkbenchScreenActivity.class, JSWorkbenchScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, newNativePlugin.getId(), true, null );

            activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( newNativePlugin.getId() ).iterator().next() );
        }
    }

}
