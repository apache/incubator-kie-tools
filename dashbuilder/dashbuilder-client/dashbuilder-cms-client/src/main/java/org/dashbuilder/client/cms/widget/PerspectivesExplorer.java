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
package org.dashbuilder.client.cms.widget;

import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.client.navigation.event.PerspectivePluginsChangedEvent;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerI18n;
import org.dashbuilder.client.navigation.plugin.PerspectivePluginManager;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.plugin.client.security.PluginController;
import org.uberfire.ext.plugin.client.widget.popup.NewPluginPopUp;
import org.uberfire.ext.plugin.model.Plugin;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;

@Dependent
public class PerspectivesExplorer implements IsElement {

    public interface View extends UberElement<PerspectivesExplorer> {

        void clear();

        void setExpanded(boolean expanded);

        void setMaximized(boolean maximized);

        void setTitle(String text);

        void setNewMenuVisible(boolean visible);

        void setNewMenuName(String text);

        void setPerspectiveName(String text);

        void addPerspective(String name, Command onClicked);

        void showEmpty(String message);
    }

    View view;
    PerspectivePluginManager perspectivePluginManager;
    PlaceManager placeManager;
    PluginController pluginController;
    NewPluginPopUp newPluginPopUp;
    ContentManagerI18n i18n;
    Command onExpandCommand;
    boolean expanded;

    @Inject
    public PerspectivesExplorer(View view,
                                PerspectivePluginManager perspectivePluginManager,
                                PluginController pluginController,
                                NewPluginPopUp newPluginPopUp,
                                PlaceManager placeManager,
                                ContentManagerI18n i18n) {
        this.view = view;
        this.perspectivePluginManager = perspectivePluginManager;
        this.pluginController = pluginController;
        this.newPluginPopUp = newPluginPopUp;
        this.placeManager = placeManager;
        this.i18n = i18n;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void setOnExpandCommand(Command onExpandCommand) {
        this.onExpandCommand = onExpandCommand;
    }

    public void show() {
        view.clear();
        view.setTitle(i18n.capitalizeFirst(i18n.getPerspectivesResourceName()));
        view.setNewMenuName(i18n.getContentExplorerNew());
        view.setNewMenuVisible(pluginController.canCreatePerspectives());
        view.setPerspectiveName(i18n.capitalizeFirst(i18n.getPerspectiveResourceName()));

        perspectivePluginManager.getPerspectivePlugins(perspectivePlugins -> {

            List<Plugin> filteredPlugins = perspectivePlugins.stream()
                    .filter(pluginController::canRead)
                    .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                    .collect(Collectors.toList());


            if (filteredPlugins.isEmpty()) {
                view.showEmpty(i18n.getNoPerspectives());
            } else {
                filteredPlugins.forEach(p -> view.addPerspective(p.getName(), () -> onPerspectiveClick(p)));
            }
        });

        this.expand();
    }

    public void onPerspectiveClick(Plugin plugin) {
        PlaceRequest placeRequest = new PathPlaceRequest(plugin.getPath()).addParameter("name", plugin.getName());
        placeManager.goTo(placeRequest);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void expand() {
        if (!expanded) {
            expanded = true;
            view.setExpanded(true);
        }
    }

    public void collapse() {
        if (expanded) {
            expanded = false;
            view.setExpanded(false);
        }
    }

    public void expandOrCollapse() {
        if (expanded) {
            collapse();
        } else {
            expand();
        }
        if (onExpandCommand != null) {
            onExpandCommand.execute();
        }
    }

    public void setMaximized(boolean maximized) {
        view.setMaximized(maximized);
    }

    public void createNewPerspective() {
        newPluginPopUp.show(PluginType.PERSPECTIVE_LAYOUT);
    }

    public void onPerspectivePluginsChanged(@Observes final PerspectivePluginsChangedEvent event) {
        show();
    }
}