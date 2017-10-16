/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.screens.explorer.client.utils.IdHelper;
import org.kie.workbench.common.screens.explorer.client.widgets.BaseViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderItem;
import org.kie.workbench.common.screens.explorer.model.FolderListing;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class Explorer
        extends Composite {

    private final FlowPanel container = new FlowPanel();
    private boolean isAlreadyInitialized = false;
    private BaseViewPresenter presenter = null;
    private Navigator activeNavigator = null;
    private Map<NavType, Navigator> navigators = new HashMap<>();

    public Explorer() {
        initWidget(container);
        IdHelper.setId(container,
                       "pex_nav_");
    }

    public void init(final NavigatorOptions options,
                     final NavType navType,
                     final BaseViewPresenter presenter) {
        this.presenter = presenter;
        setNavType(navType,
                   options);
    }

    public void setNavType(final NavType navType,
                           final NavigatorOptions options) {
        checkNotNull("navType",
                     navType);
        if (activeNavigator != null) {
            if (navType.equals(NavType.TREE) && activeNavigator instanceof TreeNavigator) {
                activeNavigator.loadContent(presenter.getActiveContent());
                return;
            } else if (navType.equals(NavType.BREADCRUMB) && activeNavigator instanceof BreadcrumbNavigator) {
                activeNavigator.loadContent(presenter.getActiveContent());
                return;
            }
            container.remove(activeNavigator);
        }

        if (!navigators.containsKey(navType)) {
            if (navType.equals(NavType.TREE)) {
                activeNavigator = IOC.getBeanManager()
                        .lookupBean(TreeNavigator.class)
                        .getInstance();
            } else {
                activeNavigator = IOC.getBeanManager()
                        .lookupBean(BreadcrumbNavigator.class)
                        .getInstance();
            }
            activeNavigator.setPresenter(presenter);
            activeNavigator.setOptions(options);
            navigators.put(navType,
                           activeNavigator);
        } else {
            activeNavigator = navigators.get(navType);
        }

        container.add(activeNavigator);

        activeNavigator.loadContent(presenter.getActiveContent());
    }

    public void clear() {
        for (final Navigator navigator : navigators.values()) {
            navigator.clear();
        }
    }

    public void setupHeader(final Project activeProject) {

        presenter.onProjectSelected(activeProject);

        if (!isAlreadyInitialized) {
            container.clear();

            addDivToAlignComponents();

            container.add(activeNavigator);

            isAlreadyInitialized = true;
        }
    }

    private void addDivToAlignComponents() {
        FlowPanel divClear = new FlowPanel();
        divClear.getElement()
                .getStyle()
                .setClear(Style.Clear.BOTH);
        container.add(divClear);
    }

    public void hideHeaderNavigator() {
    }

    public void showHeaderNavigator() {
    }

    public void loadContent(final FolderListing content) {
        if (content != null) {
            activeNavigator.loadContent(content);
        }
    }

    public void loadContent(final FolderListing content,
                            final Map<FolderItem, List<FolderItem>> siblings) {
        if (content != null) {
            activeNavigator.loadContent(content,
                                        siblings);
        }
    }

    public enum NavType {
        TREE,
        BREADCRUMB
    }
}