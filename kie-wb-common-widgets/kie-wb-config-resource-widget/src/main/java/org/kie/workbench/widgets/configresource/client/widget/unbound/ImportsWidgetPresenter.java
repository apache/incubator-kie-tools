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

package org.kie.workbench.widgets.configresource.client.widget.unbound;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.drools.workbench.models.commons.shared.imports.Import;
import org.drools.workbench.models.commons.shared.imports.Imports;
import org.kie.workbench.widgets.common.client.popups.text.FormPopup;
import org.kie.workbench.widgets.common.client.popups.text.PopupSetFieldCommand;
import org.drools.guvnor.models.commons.shared.imports.Import;
import org.drools.guvnor.models.commons.shared.imports.Imports;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.guvnor.commons.ui.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.guvnor.commons.ui.client.popups.text.FormPopup;
import org.kie.guvnor.commons.ui.client.popups.text.PopupSetFieldCommand;
import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.kie.guvnor.project.model.ProjectImports;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;

import javax.enterprise.event.Event;

import static org.kie.commons.validation.PortablePreconditions.checkNotNull;

public class ImportsWidgetPresenter
        implements ImportsWidgetView.Presenter,
        IsWidget {

    private final ImportsWidgetView view;
    private final FormPopup addImportPopup;
    private final Caller<ProjectService> projectService;
    private final Event<NotificationEvent> notification;

    private Imports resourceImports;
    private Path path;
    private ProjectImports projectImports;

    @Inject
    public ImportsWidgetPresenter(final ImportsWidgetView view,
                                  Caller<ProjectService> projectService,
                                  Event<NotificationEvent> notification,
                                  final FormPopup addImportPopup) {
        this.view = view;
        this.projectService = projectService;
        this.notification = notification;
        this.addImportPopup = addImportPopup;
        view.setPresenter(this);
    }

    public void init(Path path,
                     final boolean isReadOnly) {
        this.path = checkNotNull("resourceImports",
                path);

        projectService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load(path);


        view.setReadOnly(isReadOnly);

        for (Import item : resourceImports.getImports()) {
            view.addImport(item.getType());
        }
    }

    private RemoteCallback<ProjectImports> getModelSuccessCallback() {
        return new RemoteCallback<ProjectImports>() {

            @Override
            public void callback(final ProjectImports projectImports) {
                ImportsWidgetPresenter.this.projectImports = projectImports;

                view.hideBusyIndicator();
            }
        };
    }

    @Override
    public void onAddImport() {
        addImportPopup.show(new PopupSetFieldCommand() {
            @Override
            public void setName(String name) {
                final Import item = new Import(name);
                view.addImport(name);
                resourceImports.getImports().add(item);
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
            resourceImports.removeImport(item);
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
        return resourceImports != null;
    }

    public void save(String comment, Metadata projectImportsMetadata) {
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
