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

package org.dashbuilder.client.navigation.widget;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.CSSProperties.HeightUnionType;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLDivElement;
import jsinterop.base.Js;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@ApplicationScoped
public class ScreenLayoutDragComponent extends SinglePageNavigationDragComponent {

    public static final String SCREEN_NAME_PARAMETER = "Screen Name";

    @Inject
    public ScreenLayoutDragComponent(SyncBeanManager beanManager,
                                     PerspectivePluginManager perspectivePluginManager) {
        super(beanManager, perspectivePluginManager);
    }

    @Override
    String getPageParameterName() {
        return SCREEN_NAME_PARAMETER;
    }

    @Override
    ComponentBuilder getComponentBuilder() {
        HTMLDivElement div = Js.cast(DomGlobal.document.createElement("div"));
        div.classList.add("uf-perspective-col");
        return componentBuilder(div, (name, page) -> {
            page.style.height = HeightUnionType.of("auto");
            div.appendChild(page);
        });
    }

}