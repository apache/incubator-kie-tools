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

package org.kie.workbench.common.screens.datasource.management.client.explorer.project;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.dropdown.CustomDropdown;
import org.kie.workbench.common.screens.explorer.client.widgets.navigator.NavigatorBreadcrumbs;

public class ModuleSelector extends Composite {

    private final FlowPanel container = new FlowPanel();
    private final CustomDropdown organizationUnits = new CustomDropdown();
    private final CustomDropdown repos = new CustomDropdown();
    private final CustomDropdown modules = new CustomDropdown();
    private NavigatorBreadcrumbs navigatorBreadcrumbs;

    private boolean isAlreadyInitialized = false;

    private List<ModuleSelectorHandler> handlers = new ArrayList<>();

    public ModuleSelector() {
        initWidget(container);
    }

    public void loadOptions(final Collection<OrganizationalUnit> organizationalUnits,
                            final OrganizationalUnit activeOrganizationalUnit,
                            final Collection<Repository> repositories,
                            final Repository activeRepository,
                            final Collection<Module> modules,
                            final Module activeModule) {

        this.organizationUnits.clear();
        if (organizationalUnits != null) {
            if (activeOrganizationalUnit != null) {
                this.organizationUnits.setText(activeOrganizationalUnit.getName());
            }
            for (final OrganizationalUnit ou : organizationalUnits) {
                this.organizationUnits.add(new AnchorListItem(ou.getName()) {{
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            onOrganizationalUnitSelected(ou);
                        }
                    });
                }});
            }
        }

        this.repos.clear();
        if (repositories != null) {
            if (activeRepository != null) {
                this.repos.setText(activeRepository.getAlias());
            } else {
                this.repos.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            }
            for (final Repository repository : repositories) {
                this.repos.add(new AnchorListItem(repository.getAlias()) {{
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            onRepositorySelected(repository);
                        }
                    });
                }});
            }
        }

        this.modules.clear();
        if (modules != null) {
            if (activeModule != null) {
                this.modules.setText(activeModule.getModuleName());
            } else {
                this.modules.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            }
            for (final Module module : modules) {
                this.modules.add(new AnchorListItem(module.getModuleName()) {{
                    addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {
                            onModuleSelected(module);
                        }
                    });
                }});
            }
        }

        if (organizationalUnits != null && organizationalUnits.isEmpty()) {
            this.organizationUnits.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            this.organizationUnits.add(new AnchorListItem(ProjectExplorerConstants.INSTANCE.nullEntry()));
            this.repos.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            this.repos.add(new AnchorListItem(ProjectExplorerConstants.INSTANCE.nullEntry()));
            this.modules.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            this.modules.add(new AnchorListItem(ProjectExplorerConstants.INSTANCE.nullEntry()));
        } else if (repositories != null && repositories.isEmpty()) {
            this.repos.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            this.repos.add(new AnchorListItem(ProjectExplorerConstants.INSTANCE.nullEntry()));
            this.modules.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            this.modules.add(new AnchorListItem(ProjectExplorerConstants.INSTANCE.nullEntry()));
        } else if (modules != null && modules.isEmpty()) {
            this.modules.setText(ProjectExplorerConstants.INSTANCE.nullEntry());
            this.modules.add(new AnchorListItem(ProjectExplorerConstants.INSTANCE.nullEntry()));
        }

        if (!isAlreadyInitialized) {
            container.clear();
            setupNavigatorBreadcrumbs();
            addDivToAlignComponents();
            isAlreadyInitialized = true;
        }
    }

    public void addModuleSelectorHandler(final ModuleSelectorHandler handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
        }
    }

    private void addDivToAlignComponents() {
        FlowPanel divClear = new FlowPanel();
        divClear.getElement().getStyle().setClear(Style.Clear.BOTH);
        container.add(divClear);
    }

    private void setupNavigatorBreadcrumbs() {
        this.navigatorBreadcrumbs = new NavigatorBreadcrumbs(NavigatorBreadcrumbs.Mode.HEADER) {{
            build(organizationUnits,
                  repos,
                  ModuleSelector.this.modules);
        }};

        FlowPanel navigatorBreadcrumbsContainer = new FlowPanel();
        navigatorBreadcrumbsContainer.getElement().getStyle().setFloat(Style.Float.LEFT);
        navigatorBreadcrumbsContainer.add(navigatorBreadcrumbs);
        container.add(navigatorBreadcrumbsContainer);
    }

    private void onOrganizationalUnitSelected(final OrganizationalUnit ou) {
        for (ModuleSelectorHandler handler : handlers) {
            handler.onOrganizationalUnitSelected(ou);
        }
    }

    private void onRepositorySelected(final Repository repository) {
        for (ModuleSelectorHandler handler : handlers) {
            handler.onRepositorySelected(repository);
        }
    }

    private void onModuleSelected(final Module module) {
        for (ModuleSelectorHandler handler : handlers) {
            handler.onModuleSelected(module);
        }
    }
}