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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.patternfly.panel.Panel;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@ApplicationScoped
public class PanelLayoutDragComponent extends SinglePageNavigationDragComponent {

    public static final String PAGE_NAME_PARAMETER = "Page Name";
    PerspectivePluginManager perspectivePluginManager;

    @Inject
    public PanelLayoutDragComponent(SyncBeanManager beanManager,
                                    PerspectivePluginManager perspectivePluginManager) {
        super(beanManager, perspectivePluginManager);
    }

    @Override
    ComponentBuilder getComponentBuilder() {
        var panel = beanManager.lookupBean(Panel.class).newInstance();
        return componentBuilder(panel.getElement(), (name, page) -> {
            panel.setTitle(name);
            panel.setContent(page);
        });
    }

    @Override
    String getPageParameterName() {
        return PAGE_NAME_PARAMETER;
    }

}
