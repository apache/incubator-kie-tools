/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client;

import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.NewDataObjectPopup;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.events.PathChangeEvent;
import org.uberfire.client.workbench.widgets.menu.MenuFactory;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.Menus;
import org.uberfire.client.workbench.widgets.toolbar.IconType;
import org.uberfire.client.workbench.widgets.toolbar.ToolBar;
import org.uberfire.client.workbench.widgets.toolbar.ToolBarItem;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBar;
import org.uberfire.client.workbench.widgets.toolbar.impl.DefaultToolBarItem;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.uberfire.client.workbench.widgets.menu.MenuFactory.newSimpleItem;

//@Dependent
@WorkbenchScreen(identifier = "dataModelerScreen")
public class DataModelerScreenPresenter {

    public interface DataModelerScreenView
            extends
            UberView<DataModelerScreenPresenter> {

        void setContext(DataModelerContext context);

        void setBaseTypes(List<PropertyTypeTO> baseTypes);

        boolean confirmClose();

    }

    @Inject
    private DataModelerScreenView view;

    @Inject
    private NewDataObjectPopup newDataObjectPopup;

    @Inject
    private Caller<DataModelerService> modelerService;

    private Menus menus;

    private ToolBar toolBar;

    @Inject
    Event<DataModelerEvent> dataModelerEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private WorkbenchContext workbenchContext;

    private Path currentProject;
    
    private DataModelTO dataModel;

    private DataModelerContext context;

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.modelEditor_screen_name() + " [" + (currentProject != null ? currentProject.getFileName() : "") + "]";
    }

    @WorkbenchPartView
    public UberView<DataModelerScreenPresenter> getView() {
        return view;
    }

    @OnStart
    public void onStart() {
        makeMenuBar();
        makeToolBar();
        processPathChange(workbenchContext.getActivePath());
    }

    @IsDirty
    public boolean isDirty() {
        return getContext() != null ? getContext().isDirty() : false;
    }

    @OnMayClose
    public boolean onMayClose() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @OnSave
    public void onSave() {

        BusyPopup.showMessage(Constants.INSTANCE.modelEditor_saving());
        modelerService.call(new RemoteCallback<Object>() {
                @Override
                public void callback(Object response) {
                    BusyPopup.close();
                    restoreModelStatus();
                    getContext().setDirty(false);
                    notification.fire(new NotificationEvent(Constants.INSTANCE.modelEditor_notification_dataModel_saved()));
                }
            },
            new DataModelerErrorCallback(Constants.INSTANCE.modelEditor_saving_error())
        ).saveModel(getDataModel(), currentProject);
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
    }

    private void loadProjectDataModel(final Path path) {

        BusyPopup.showMessage(Constants.INSTANCE.modelEditor_loading());

        modelerService.call(
                new RemoteCallback<List<PropertyTypeTO>>() {
                    @Override
                    public void callback(List<PropertyTypeTO> baseTypes) {
                        view.setBaseTypes(baseTypes);
                    }
                },
                new DataModelerErrorCallback(Constants.INSTANCE.modelEditor_propertyType_loading_error())
        ).getBasePropertyTypes();

        modelerService.call(
                new RemoteCallback<Map<String, AnnotationDefinitionTO>>() {
                    @Override
                    public void callback(final Map<String, AnnotationDefinitionTO> defs) {

                        modelerService.call(
                                new RemoteCallback<DataModelTO>() {

                                    @Override
                                    public void callback(DataModelTO dataModel) {
                                        BusyPopup.close();
                                        setDataModel(dataModel, defs);
                                        notification.fire(new NotificationEvent(Constants.INSTANCE.modelEditor_notification_dataModel_loaded(path.toString())));
                                    }

                                },
                                new DataModelerErrorCallback(Constants.INSTANCE.modelEditor_loading_error())
                        ).loadModel(path);

                    }
                },
                new DataModelerErrorCallback(Constants.INSTANCE.modelEditor_annotationDef_loading_error())
        ).getAnnotationDefinitions();



        currentProject = path;
    }

    public DataModelTO getDataModel() {
        return dataModel;
    }

    public DataModelerContext getContext() {
        return context;
    }

    private void setDataModel(DataModelTO dataModel, Map<String, AnnotationDefinitionTO> annotationDefinitions) {
        this.dataModel = dataModel;

        // Set data model helper before anything else
        if (dataModel != null) {
            context = new DataModelerContext(dataModel, annotationDefinitions);
            view.setContext(context);
            if (dataModel.getDataObjects().size() > 0) {
                dataModelerEvent.fire(new DataObjectSelectedEvent(DataModelerEvent.DATA_MODEL_BROWSER, getDataModel(), dataModel.getDataObjects().get(0)));
            }
        }
    }

    private void onNewDataObject() {
        newDataObjectPopup.setContext(getContext());
        newDataObjectPopup.show();
    }

    private void onPathChange(@Observes final PathChangeEvent event) {
        processPathChange(event.getPath());
    }

    private void processPathChange(Path newPath) {

        if (newPath != null) {

            modelerService.call(
                    new RemoteCallback<Path>() {
                        @Override
                        public void callback(Path projectPath) {

                            if (projectPath != null) {
                                if (!projectPath.equals(currentProject)) {
                                    loadProjectDataModel(projectPath);
                                }
                            } else {
                                //TODO
                                //check what will be the default policy when the user click in a place
                                //that doesn't belong to a project.
                            }
                        }
                    },
                    new DataModelerErrorCallback(Constants.INSTANCE.modelEditor_projectPath_calc_error())
            ).resolveProject(newPath);

        } else {
            //TODO check if this is possible. By definition we will always have a path.
        }
    }

    private void restoreModelStatus() {
        //when the model is saved without errors
        //clean the deleted dataobjects status, mark all dataobjects as persisted, etc.
        getDataModel().setPersistedStatus();
    }

    private void makeMenuBar() {
        menus = MenuFactory
                    .newTopLevelMenu(Constants.INSTANCE.modelEditor_menu_main())
                    .withItems( getItems() )
                    .endMenu().build();
    }

    private void makeToolBar() {
        toolBar = new DefaultToolBar( "dataModelerToolbar" );

        org.uberfire.client.mvp.Command saveCommand = new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {
                onSave();
            }
        };

        org.uberfire.client.mvp.Command newDataObjectCommand = new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {
                onNewDataObject();
            }
        };

        ToolBarItem item = new DefaultToolBarItem( IconType.SAVE, Constants.INSTANCE.modelEditor_menu_save(), saveCommand);
        toolBar.addItem(item);

        item = new DefaultToolBarItem( IconType.FILE, Constants.INSTANCE.modelEditor_menu_new_dataObject(), newDataObjectCommand);
        toolBar.addItem(item);

    }

    private List<MenuItem> getItems() {

        final List<MenuItem> menuItems = new ArrayList<MenuItem>();

        org.uberfire.client.mvp.Command newDataObjectCommand = new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {
                onNewDataObject();
            }
        };

        org.uberfire.client.mvp.Command saveCommand = new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {
                onSave();
            }
        };

        if ( newDataObjectCommand != null ) {
            menuItems.add(newSimpleItem(Constants.INSTANCE.modelEditor_menu_new_dataObject())
                    .respondsWith(newDataObjectCommand)
                    .endMenu().build().getItems().get(0));
        }

        if ( saveCommand != null ) {
            menuItems.add(newSimpleItem(Constants.INSTANCE.modelEditor_menu_save())
                    .respondsWith(saveCommand)
                    .endMenu().build().getItems().get(0));
        }

        return menuItems;
    }
}