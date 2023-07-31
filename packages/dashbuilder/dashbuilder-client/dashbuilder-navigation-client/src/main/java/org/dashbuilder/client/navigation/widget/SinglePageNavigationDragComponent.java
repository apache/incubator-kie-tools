/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navigation.widget;

import java.util.function.BiConsumer;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLElement;
import jsinterop.base.Js;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.patternfly.alert.Alert;
import org.dashbuilder.patternfly.alert.AlertType;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

public abstract class SinglePageNavigationDragComponent implements LayoutDragComponent {

    SyncBeanManager beanManager;

    PerspectivePluginManager perspectivePluginManager;

    SinglePageNavigationDragComponent(SyncBeanManager beanManager, PerspectivePluginManager perspectivePluginManager) {
        this.beanManager = beanManager;
        this.perspectivePluginManager = perspectivePluginManager;
    }

    @Override
    public IsWidget getShowWidget(RenderingContext ctx) {
        var perspectiveId = ctx.getComponent().getProperties().get(getPageParameterName());
        var builder = getComponentBuilder();
        var root = builder.root;
        var pageBuilder = builder.pageConsumer;
        if (perspectiveId == null) {
            return ElementWrapperWidget.getWidget(alert("Page " + perspectiveId + "  not Found"));
        } else {
            perspectivePluginManager.buildPerspectiveWidget(perspectiveId,
                    page -> pageBuilder.accept(perspectiveId, Js.cast(page.asWidget().getElement())),
                    issue -> root.appendChild(alert("Error with infinite recursion. Review the embedded page")));
        }

        return ElementWrapperWidget.getWidget(root);
    }

    abstract ComponentBuilder getComponentBuilder();

    abstract String getPageParameterName();

    final ComponentBuilder componentBuilder(HTMLElement root, BiConsumer<String, HTMLElement> pageConsumer) {
        return new ComponentBuilder(root, pageConsumer);
    }

    private HTMLElement alert(String message) {
        var alert = beanManager.lookupBean(Alert.class).newInstance();
        alert.setup(AlertType.WARNING, message);
        return alert.getElement();
    }

    static class ComponentBuilder {

        HTMLElement root;
        BiConsumer<String, HTMLElement> pageConsumer;

        private ComponentBuilder(HTMLElement root, BiConsumer<String, HTMLElement> pageConsumer) {
            this.root = root;
            this.pageConsumer = pageConsumer;
        }
    }
}
