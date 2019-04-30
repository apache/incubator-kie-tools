/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.util.Arrays;
import java.util.HashSet;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.JavaScriptObject;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.editor.type.JSClientResourceType;
import org.uberfire.client.editor.type.JSNativeClientResourceType;
import org.uberfire.client.plugin.JSNativePlugin;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.jsbridge.client.cdi.SingletonBeanDefinition;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Others;

import static org.jboss.errai.ioc.client.QualifierUtil.DEFAULT_QUALIFIERS;

@ApplicationScoped
public class ResourceTypeDefinitionJSExporter implements UberfireJSExporter {

    public static void registerResourceTypeDefinition(final Object _obj) {
        final JavaScriptObject obj = (JavaScriptObject) _obj;

        if (JSNativePlugin.hasStringProperty(obj,
                                             "id")) {
            final SyncBeanManager beanManager = IOC.getBeanManager();
            final JSNativeClientResourceType newNativeClientResourceType = beanManager.lookupBean(JSNativeClientResourceType.class).getInstance();
            final Category category = beanManager.lookupBean(Others.class).getInstance();
            newNativeClientResourceType.build(obj);
            JSClientResourceType jsClientResourceType = new JSClientResourceType(newNativeClientResourceType,
                                                                                 category);
            beanManager.registerBean(new SingletonBeanDefinition<ClientResourceType, JSClientResourceType>(jsClientResourceType,
                                                                   ClientResourceType.class,
                                                                   new HashSet<>(Arrays.asList(DEFAULT_QUALIFIERS)),
                                                                   jsClientResourceType.getId(),
                                                                   true,
                                                                   JSClientResourceType.class));
        }
    }

    @Override
    public void export() {
        publish();
    }

    private native void publish() /*-{
        $wnd.$registerResourceType = @org.uberfire.client.exporter.ResourceTypeDefinitionJSExporter::registerResourceTypeDefinition(Ljava/lang/Object;);
    }-*/;
}
