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

package org.kie.workbench.common.widgets.configresource.client.widget.unbound;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.text.FormPopup;
import org.kie.workbench.common.widgets.client.popups.text.PopupSetFieldCommand;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.events.NotificationEvent;

public class ImportsWidgetPresenter
        implements ImportsWidgetView.Presenter,
        IsWidget {

    private ImportsWidgetView view;
    private FormPopup addImportPopup;
    private Caller<ProjectService> projectService;
    private Event<NotificationEvent> notification;

    private Path path;
    private ProjectImports projectImports;

    public ImportsWidgetPresenter() {
    }

    @Inject
    public ImportsWidgetPresenter(final ImportsWidgetView view,
                                  final Caller<ProjectService> projectService,
                                  final Event<NotificationEvent> notification,
                                  final FormPopup addImportPopup) {
        this.view = view;
        this.projectService = projectService;
        this.notification = notification;
        this.addImportPopup = addImportPopup;
        view.setPresenter(this);
    }

    public void setData(ProjectImports projectImports, boolean isReadOnly) {

        view.setReadOnly(isReadOnly);

        ImportsWidgetPresenter.this.projectImports = projectImports;
        for (Import item : projectImports.getImports().getImports()) {
            view.addImport(item.getType());
        }
        view.hideBusyIndicator();
    }

    @Override
    public void onAddImport() {
        addImportPopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                final Import item = new Import(name);
                view.addImport(name);
                projectImports.getImports().getImports().add(item);
            }
        });
    }

    @Override
    public void onRemoveImport() {
        String selected = view.getSelected();
        if (selected == null) {
            view.showPleaseSelectAnImport();
        } else {
            final Import item = new Import(selected);
            view.removeImport(selected);
            projectImports.getImports().removeImport(item);
        }
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public boolean isDirty() {
        return false; // TODO: -Rikkola-
    }

    public void setNotDirty() {
        // TODO: -Rikkola-
    }

    public boolean hasBeenInitialized() {
        return projectImports != null;
    }

    public void save(String comment,
                     Metadata projectImportsMetadata) {
        view.showBusyIndicator(CommonConstants.INSTANCE.Saving());
        projectService.call(getSaveSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).save(path,
                projectImports,
                projectImportsMetadata,
                comment);
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback(final Path path) {
                view.hideBusyIndicator();
                notification.fire(new NotificationEvent(CommonConstants.INSTANCE.ItemSavedSuccessfully()));
            }
        };
    }
}
