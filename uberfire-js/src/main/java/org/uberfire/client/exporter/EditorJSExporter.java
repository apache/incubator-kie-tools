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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.editor.JSEditorActivity;
import org.uberfire.client.editor.JSNativeEditor;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.workbench.type.ClientResourceType;

import com.google.gwt.core.client.JavaScriptObject;

@ApplicationScoped
public class EditorJSExporter implements UberfireJSExporter {

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerEditor = @org.uberfire.client.exporter.EditorJSExporter::registerEditor(Ljava/lang/Object;);
    }-*/;

    public static void registerEditor( final Object _obj ) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;
        if ( JSNativeEditor.hasStringProperty( obj, "id" ) ) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();

            final JSNativeEditor newNativeEditor = beanManager.lookupBean( JSNativeEditor.class ).getInstance();
            newNativeEditor.build( obj );

            PlaceManager placeManager = beanManager.lookupBean( PlaceManager.class ).getInstance();
            final JSEditorActivity activity = new JSEditorActivity( newNativeEditor, placeManager );

            final Set<Annotation> qualifiers = new HashSet<Annotation>( Arrays.asList( DEFAULT_QUALIFIERS ) );
            final SingletonBeanDef<JSEditorActivity, JSEditorActivity> beanDef = new SingletonBeanDef<JSEditorActivity, JSEditorActivity>( activity,
                                                                                                JSEditorActivity.class,
                                                                                                qualifiers,
                                                                                                newNativeEditor.getId(),
                                                                                                true,
                                                                                                WorkbenchEditorActivity.class,
                                                                                                Activity.class );
            beanManager.registerBean( beanDef );
            beanManager.registerBeanTypeAlias( beanDef, WorkbenchEditorActivity.class );
            beanManager.registerBeanTypeAlias( beanDef, Activity.class );

            Class<? extends ClientResourceType> resourceTypeClass = getResourceTypeClass( beanManager, newNativeEditor );
            activityBeansCache.addNewEditorActivity( beanManager.lookupBeans( newNativeEditor.getId() ).iterator().next(), resourceTypeClass );

        }
    }

    private static Class<? extends ClientResourceType> getResourceTypeClass( SyncBeanManager beanManager,
                                                                             JSNativeEditor newNativeEditor ) {

        Collection<SyncBeanDef<ClientResourceType>> iocBeanDefs = beanManager.lookupBeans( ClientResourceType.class );
        for ( IOCBeanDef<ClientResourceType> iocBeanDef : iocBeanDefs ) {
            String beanClassName = iocBeanDef.getBeanClass().getName();
            if ( beanClassName.equalsIgnoreCase( newNativeEditor.getResourceType() ) ) {
                return (Class<? extends ClientResourceType>) iocBeanDef.getBeanClass();
            }
        }
        throw new EditorResourceTypeNotFound();
    }

    public static class EditorResourceTypeNotFound extends RuntimeException {

    }
}
