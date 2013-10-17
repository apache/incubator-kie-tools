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
import org.guvnor.common.services.shared.metadata.model.Categories;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
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

    protected Menus menus;

    protected void makeMenuBar() {
        menus = menuBuilder.addSave(new Command() {
            @Override
            public void execute() {
                onSave();
            }
        }).build();
    }

    public abstract void onSave();

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
