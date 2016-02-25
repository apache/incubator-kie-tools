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

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.perspective.JSNativePerspective;
import org.uberfire.client.perspective.JSWorkbenchPerspectiveActivity;
import org.uberfire.client.plugin.JSNativePlugin;

import com.google.gwt.core.client.JavaScriptObject;

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

            beanManager.registerBean( new SingletonBeanDef<PerspectiveActivity, JSWorkbenchPerspectiveActivity>( activity,
                                                                  PerspectiveActivity.class,
                                                                  new HashSet<Annotation>( Arrays.asList( DEFAULT_QUALIFIERS ) ),
                                                                  newNativePerspective.getId(),
                                                                  true,
                                                                  JSWorkbenchPerspectiveActivity.class ) );

            activityBeansCache.addNewPerspectiveActivity( beanManager.lookupBeans( newNativePerspective.getId() ).iterator().next() );
        }
    }

}
