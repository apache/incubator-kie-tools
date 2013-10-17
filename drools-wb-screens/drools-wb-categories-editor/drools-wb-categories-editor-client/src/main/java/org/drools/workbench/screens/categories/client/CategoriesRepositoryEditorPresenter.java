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
import org.guvnor.common.services.shared.metadata.model.CategoriesModelContent;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

/**
 *
 */
@Dependent
@WorkbenchScreen(identifier = "CategoryManager")
public class CategoriesRepositoryEditorPresenter
        extends CategoriesEditorBasePresenter {

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private CategoryDefinitionResourceType type;

    @Inject
    protected ProjectContext context;

    private CategoriesModelContent categoriesModelContent;

    @OnStartup
    public void onStartup() {

        makeMenuBar();

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        categoryService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).getContentByRoot(context.getActiveRepository().getRoot());
    }

    private RemoteCallback<CategoriesModelContent> getModelSuccessCallback() {
        return new RemoteCallback<CategoriesModelContent>() {

            @Override
            public void callback(final CategoriesModelContent content) {
                CategoriesRepositoryEditorPresenter.this.categoriesModelContent = content;

                view.setContent(content.getCategories());
                view.hideBusyIndicator();
            }
        };
    }

    public void onSave() {
        new SaveOperationService().save(categoriesModelContent.getPath(),
                new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String commitMessage) {
                        view.showBusyIndicator(CommonConstants.INSTANCE.Saving());
                        categoryService.call(getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(view)).save(categoriesModelContent.getPath(),
                                view.getContent());
                    }
                });
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if (isDirty()) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.CategoriesEditor();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
