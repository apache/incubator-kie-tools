/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.categories.client;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.categories.client.resources.i18n.Constants;
import org.drools.workbench.screens.categories.client.type.CategoryDefinitionResourceType;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.model.CategoriesModelContent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.ext.editor.commons.client.file.SaveOperationService;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.MultiPageEditor;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 *
 */
@Dependent
@WorkbenchScreen(identifier = "CategoryManager")
public class CategoriesRepositoryEditorPresenter {

    @Inject
    protected CategoriesEditorView view;

    @Inject
    protected Caller<CategoriesService> categoryService;

    @Inject
    private FileMenuBuilder menuBuilder;

    @Inject
    private CategoryDefinitionResourceType type;

    @Inject
    protected MultiPageEditor multiPage;

    protected Path path;

    protected Menus menus;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    protected ProjectContext context;

    @OnStartup
    public void onStartup() {

        makeMenuBar();

        view.showLoading();
        categoryService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).getContentByRoot(context.getActiveRepository().getRoot());
    }

    protected RemoteCallback<CategoriesModelContent> getModelSuccessCallback() {
        return new RemoteCallback<CategoriesModelContent>() {

            @Override
            public void callback(final CategoriesModelContent content) {
                multiPage.clear();
                multiPage.addWidget(view,
                        CommonConstants.INSTANCE.EditTabTitle());

                path = content.getPath();

                view.setContent(content.getCategories());
                view.hideBusyIndicator();

            }
        };
    }

    protected void makeMenuBar() {
        menus = menuBuilder.addSave(new Command() {
            @Override
            public void execute() {
                onSave();
            }
        }).build();
    }

    public void onSave() {
        new SaveOperationService().save(path,
                new ParameterizedCommand<String>() {
                    @Override
                    public void execute(final String commitMessage) {
                        view.showSaving();
                        categoryService.call(getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(view)).save(
                                path,
                                view.getContent(),
                                null,
                                commitMessage
                        );
                    }
                });
    }

    protected RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                view.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
            }
        };
    }

    @OnMayClose
    public boolean mayClose() {
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.CategoriesEditor();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return multiPage;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
