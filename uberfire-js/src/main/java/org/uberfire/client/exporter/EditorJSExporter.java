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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.editor.JSEditorActivity;
import org.uberfire.client.editor.JSNativeEditor;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.jsbridge.client.cdi.SingletonBeanDefinition;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@ApplicationScoped
public class EditorJSExporter implements UberfireJSExporter {

    public static void registerEditor(final Object _obj) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;
        if (JSNativeEditor.hasStringProperty(obj,
                                             "id")) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final ActivityBeansCache activityBeansCache = beanManager.lookupBean(ActivityBeansCache.class).getInstance();

            final JSNativeEditor newNativeEditor = beanManager.lookupBean(JSNativeEditor.class).getInstance();
            newNativeEditor.build(obj);

            PlaceManager placeManager = beanManager.lookupBean(PlaceManager.class).getInstance();

            JSEditorActivity activity = JSExporterUtils.findActivityIfExists(beanManager,
                                                                             newNativeEditor.getId(),
                                                                             JSEditorActivity.class);

            if (activity == null) {
                registerNewActivity(beanManager,
                                    activityBeansCache,
                                    newNativeEditor,
                                    placeManager);
            } else {
                updateExistentActivity(newNativeEditor,
                                       activity);
            }
        }
    }

    private static void updateExistentActivity(final JSNativeEditor newNativeEditor,
                                               final JSEditorActivity activity) {
        activity.setNativeEditor(newNativeEditor);
    }

    private static void registerNewActivity(final SyncBeanManager beanManager,
                                            final ActivityBeansCache activityBeansCache,
                                            final JSNativeEditor newNativeEditor,
                                            final PlaceManager placeManager) {
        final JSEditorActivity activity;
        activity = new JSEditorActivity(newNativeEditor,
                                        placeManager);

        final Set<Annotation> qualifiers = new HashSet<Annotation>(Arrays.asList(DEFAULT_QUALIFIERS));
        final SingletonBeanDefinition<JSEditorActivity, JSEditorActivity> beanDef = new SingletonBeanDefinition<>(
                activity,
                JSEditorActivity.class,
                qualifiers,
                newNativeEditor.getId(),
                true,
                WorkbenchEditorActivity.class,
                Activity.class);

        beanManager.registerBean(beanDef);
        beanManager.registerBeanTypeAlias(beanDef,
                                          WorkbenchEditorActivity.class);
        beanManager.registerBeanTypeAlias(beanDef,
                                          Activity.class);

        activityBeansCache.addNewEditorActivity(beanManager.lookupBeans(newNativeEditor.getId()).iterator().next(),
                                                newNativeEditor.getPriority(),
                                                newNativeEditor.getResourceType());
    }

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerEditor = @org.uberfire.client.exporter.EditorJSExporter::registerEditor(Ljava/lang/Object;);
    }-*/;

    public static class EditorResourceTypeNotFound extends RuntimeException {

    }
}
