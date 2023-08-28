/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;

/**
 * A specific Runtime perspective plugin manager. This is used by Navigation Components to load a custom perspective.
 *
 */
@Alternative
@ApplicationScoped
public class RuntimePerspectivePluginManager implements PerspectivePluginManager {

    @Inject
    LayoutGenerator layoutGenerator;

    List<LayoutTemplate> templates = new ArrayList<>();

    @Override
    public String getRuntimePerspectiveId(NavItem navItem) {
        NavWorkbenchCtx navCtx = NavWorkbenchCtx.get(navItem);
        return navCtx.getResourceId();
    }

    @Override
    public void buildPerspectiveWidget(String perspectiveName,
                                       Consumer<HTMLElement> afterBuild) {
        templates.stream()
                .filter(lt -> lt.getName().equals(perspectiveName))
                .findFirst().ifPresent(lt -> {
                    var result = layoutGenerator.build(lt);
                    afterBuild.accept(result.getElement());
                });
    }

    public void setTemplates(List<LayoutTemplate> templates) {
        this.templates = templates;
    }

}
