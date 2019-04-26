/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.dashboard;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

import org.dashbuilder.client.resources.i18n.AppConstants;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.PerspectiveCoordinator;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.displayer.client.widgets.DisplayerEditorPopup;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.security.ResourceType;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.ToolBar;

/**
 * @deprecated Since version 0.7, dashboards are created from the Content Manager perspective. This class is
 * still needed in order to deal with old dashboards created from existing installations.
 */
public class DashboardPerspectiveActivity implements PerspectiveActivity {

    private SyncBeanManager beanManager;
    private DashboardManager dashboardManager;
    private PerspectiveManager perspectiveManager;
    private PlaceManager placeManager;
    private DisplayerSettingsJSONMarshaller jsonMarshaller = DisplayerSettingsJSONMarshaller.get();
    private PerspectiveCoordinator perspectiveCoordinator;

    private PlaceRequest place;
    private String id;
    private boolean persistent;

    public DashboardPerspectiveActivity() {
    }

    public DashboardPerspectiveActivity(String id,
                                        DashboardManager dashboardManager,
                                        SyncBeanManager beanManager,
                                        PerspectiveManager perspectiveManager,
                                        PlaceManager placeManager,
                                        PerspectiveCoordinator perspectiveCoordinator) {

        this.id = id;
        this.beanManager = beanManager;
        this.persistent = true;
        this.dashboardManager = dashboardManager;
        this.perspectiveManager = perspectiveManager;
        this.placeManager = placeManager;
        this.perspectiveCoordinator = perspectiveCoordinator;
    }

    public String getDisplayName() {
        return id.substring(10);
    }

    @Override
    public PlaceRequest getPlace() {
        return place;
    }

    @Override
    public void onStartup(final PlaceRequest place) {
        this.place = place;
    }

    @Override
    public void onOpen() {
    }

    @Override
    public void onClose() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition perspective = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
        perspective.setName(id);
        return perspective;
    }

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public ResourceType getResourceType() {
        return ActivityResourceType.PERSPECTIVE;
    }

    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public boolean isTransient() {
        return !persistent;
    }

    @Override
    public void getMenus(final Consumer<Menus> menusConsumer) {
        menusConsumer.accept(MenuFactory
                                     .newTopLevelMenu(AppConstants.INSTANCE.dashboard_new_displayer())
                                     .respondsWith(getNewDisplayerCommand())
                                     .endMenu()
                                     .newTopLevelMenu(AppConstants.INSTANCE.dashboard_delete_dashboard())
                                     .respondsWith(getShowDeletePopupCommand())
                                     .endMenu().build());
    }

    @Override
    public ToolBar getToolBar() {
        return null;
    }

    // Internal stuff

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    protected YesNoCancelPopup deleteDashboardPopup;

    private Command getShowDeletePopupCommand() {
        return new Command() {
            public void execute() {
                deleteDashboardPopup = YesNoCancelPopup.newYesNoCancelPopup(
                        AppConstants.INSTANCE.dashboard_delete_popup_title(),
                        AppConstants.INSTANCE.dashboard_delete_popup_content(),
                        getDoDeleteCommand(),
                        getCancelDeleteCommand(),
                        null);
                deleteDashboardPopup.show();
            }
        };
    }

    private Command getCancelDeleteCommand() {
        return new Command() {
            public void execute() {
                deleteDashboardPopup.hide();
            }
        };
    }

    private Command getDoDeleteCommand() {
        return new Command() {
            public void execute() {
                perspectiveManager.removePerspectiveState(id, new Command() {
                    public void execute() {
                        dashboardManager.removeDashboard(id);
                        placeManager.goTo(getDefaultPerspectiveActivity().getIdentifier());
                    }
                });
            }
        };
    }

    private Command getNewDisplayerCommand() {
        return new Command() {
            public void execute() {
                /* Displayer settings == null => Create a brand new displayer */
                perspectiveCoordinator.editOn();
                DisplayerEditorPopup displayerEditor = beanManager.lookupBean(DisplayerEditorPopup.class).newInstance();
                displayerEditor.init(null);
                displayerEditor.setOnSaveCommand(getSaveDisplayerCommand(displayerEditor));
                displayerEditor.setOnCloseCommand(getCloseDisplayerCommand(displayerEditor));
            }
        };
    }

    protected Command getSaveDisplayerCommand(final DisplayerEditorPopup editor) {
        return new Command() {
            public void execute() {
                perspectiveCoordinator.editOff();
                beanManager.destroyBean(editor);

                placeManager.goTo(createPlaceRequest(editor.getDisplayerSettings()));
                perspectiveManager.savePerspectiveState(new Command() {
                    public void execute() {
                    }
                });
            }
        };
    }

    protected Command getCloseDisplayerCommand(final DisplayerEditorPopup editor) {
        return new Command() {
            public void execute() {
                perspectiveCoordinator.editOff();
                beanManager.destroyBean(editor);
            }
        };
    }

    private PlaceRequest createPlaceRequest(DisplayerSettings displayerSettings) {
        String json = jsonMarshaller.toJsonString(displayerSettings);
        Map<String,String> params = new HashMap<>();
        params.put("json", json);
        params.put("edit", "true");
        params.put("clone", "true");
        return new DefaultPlaceRequest("DisplayerScreen", params);
    }

    private PerspectiveActivity getDefaultPerspectiveActivity() {
        PerspectiveActivity first = null;
        SyncBeanManagerImpl beanManager = (SyncBeanManagerImpl) IOC.getBeanManager();
        Collection<SyncBeanDef<PerspectiveActivity>> perspectives = beanManager.lookupBeans(PerspectiveActivity.class);
        Iterator<SyncBeanDef<PerspectiveActivity>> perspectivesIterator = perspectives.iterator();
        while (perspectivesIterator.hasNext() ) {

            SyncBeanDef<PerspectiveActivity> perspective = perspectivesIterator.next();
            PerspectiveActivity instance = perspective.getInstance();

            if (instance.isDefault()) {
                return instance;
            }
            if (first == null) {
                first = instance;
            }
        }
        return first;
    }
}
