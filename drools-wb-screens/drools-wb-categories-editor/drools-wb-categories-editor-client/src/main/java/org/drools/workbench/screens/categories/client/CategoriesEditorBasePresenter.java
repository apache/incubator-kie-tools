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

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.screens.categories.client.type.CategoryDefinitionResourceType;
import org.guvnor.common.services.shared.metadata.CategoriesService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.CategoriesModelContent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.callbacks.MetadataSuccessCallback;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.common.MultiPageEditor;
import org.uberfire.client.common.Page;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

public abstract class CategoriesEditorBasePresenter {

    @Inject
    protected CategoriesEditorView view;

    @Inject
    protected Caller<CategoriesService> categoryService;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private FileMenuBuilder menuBuilder;

    @Inject
    private CategoryDefinitionResourceType type;

    @Inject
    protected MultiPageEditor multiPage;

    @Inject
    protected MetadataWidget metadataWidget;

    @Inject
    private Caller<MetadataService> metadataService;

    protected Path path;

    protected Menus menus;

    protected boolean isReadOnly = false;



    protected void makeRestoreMenuBar() {
        menus = menuBuilder.addRestoreVersion( path ).build();
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
                new CommandWithCommitMessage() {
                    @Override
                    public void execute(final String commitMessage) {
                        view.showBusyIndicator(CommonConstants.INSTANCE.Saving());
                        categoryService.call(getSaveSuccessCallback(),
                                new HasBusyIndicatorDefaultErrorCallback(view)).save(
                                path,
                                view.getContent(),
                                metadataWidget.getContent(),
                                commitMessage
                        );
                    }
                });
    }

    protected void addMetadataPage() {
        multiPage.addPage(new Page(metadataWidget,
                CommonConstants.INSTANCE.MetadataTabTitle()) {
            @Override
            public void onFocus() {
                metadataWidget.showBusyIndicator(CommonConstants.INSTANCE.Loading());
                metadataService.call(new MetadataSuccessCallback(metadataWidget,
                        isReadOnly),
                        new HasBusyIndicatorDefaultErrorCallback(metadataWidget)).getMetadata(path);
            }

            @Override
            public void onLostFocus() {
                //Nothing to do
            }
        });
    }

    protected RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                view.setNotDirty();
                view.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
            }
        };
    }

}
