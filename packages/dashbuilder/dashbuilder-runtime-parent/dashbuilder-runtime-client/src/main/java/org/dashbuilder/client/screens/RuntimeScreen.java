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

package org.dashbuilder.client.screens;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.dashbuilder.client.place.Place;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.shared.model.RuntimeModel;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

/**
 * The Main application screen that contains dashboards from a RuntimeModel. 
 *
 */
@ApplicationScoped
public class RuntimeScreen implements Place {

    public static final String ID = "RuntimeScreen";

    public static final String INDEX_PAGE_NAME = "index";

    public interface View extends UberElemental<RuntimeScreen> {

        void loadNavTree(NavTree navTree, boolean keepNavigation);

        void setContent(HTMLElement element);

    }

    @Inject
    View view;

    @Inject
    PerspectivePluginManager pluginManager;

    private RuntimeModel currentRuntimeModel;

    boolean newNavigation;

    @PostConstruct
    void setup() {
        view.init(this);
    }

    public void loadDashboards(RuntimeModel runtimeModel) {
        newNavigation = this.currentRuntimeModel != null &&
                        !this.currentRuntimeModel.getNavTree().equals(runtimeModel.getNavTree());
        this.currentRuntimeModel = runtimeModel;
        loadNavTree();
    }

    public void goToIndex(List<LayoutTemplate> templates) {
        var indexOp = templates.stream()
                .filter(lt -> INDEX_PAGE_NAME.equals(lt.getName()))
                .findFirst();
        if (indexOp.isPresent()) {
            loadTemplate(indexOp.get());
        } else if (templates.size() == 1) {
            loadTemplate(templates.get(0));
        } else {
            loadNavTree();
        }
    }

    private void loadTemplate(LayoutTemplate template) {
        pluginManager.buildPerspectiveWidget(template.getName(), view::setContent);
    }

    public void clearCurrentSelection() {
        currentRuntimeModel = null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    private void loadNavTree() {
        if (this.currentRuntimeModel != null) {
            view.loadNavTree(this.currentRuntimeModel.getNavTree(), !newNavigation);
        }
    }
}
