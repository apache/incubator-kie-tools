/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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
package org.dashbuilder.client.navigation.layout.editor;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.NavigationManager;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.client.navigation.resources.i18n.NavigationConstants;
import org.dashbuilder.client.navigation.widget.NavCarouselWidget;
import org.dashbuilder.client.navigation.widget.NavComponentConfigModal;

/**
 * A layout editor's navigation component that a carousel of runtime perspectives
 */
@Dependent
public class NavCarouselDragComponent extends AbstractNavDragComponent {

    @Inject
    public NavCarouselDragComponent(NavigationManager navigationManager,
                                    PerspectivePluginManager pluginManager,
                                    NavComponentConfigModal navComponentConfigModal,
                                    NavCarouselWidget navWidget) {
        super(navigationManager,
                pluginManager,
                navComponentConfigModal,
                navWidget);
    }

    @Override
    public String getDragComponentTitle() {
        return NavigationConstants.INSTANCE.navCarouselDragComponent();
    }

    @Override
    public String getDragComponentNavGroupHelp() {
        return NavigationConstants.INSTANCE.navCarouselDragComponentNavGroupHelp();
    }
}
