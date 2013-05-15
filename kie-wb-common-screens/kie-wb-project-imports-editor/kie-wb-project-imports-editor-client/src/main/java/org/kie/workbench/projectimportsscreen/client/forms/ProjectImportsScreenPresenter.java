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

package org.kie.workbench.projectimportsscreen.client.forms;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.widgets.common.client.menu.FileMenuBuilder;
import org.kie.workbench.widgets.common.client.resources.i18n.CommonConstants;
import org.kie.workbench.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.projectimportsscreen.client.resources.i18n.ProjectConfigScreenConstants;
import org.kie.workbench.projectimportsscreen.client.type.ProjectImportsResourceType;
import org.kie.guvnor.services.metadata.MetadataService;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.*;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;

import javax.enterprise.event.Event;
import javax.enterprise.inject.New;

@WorkbenchEditor(identifier = "projectConfigScreen", supportedTypes = {ProjectImportsResourceType.class})
public class ProjectImportsScreenPresenter
        implements ProjectImportsScreenView.Presenter {

    private ProjectImportsScreenView view;

    private Caller<MetadataService> metadataService;

    private Event<NotificationEvent> notification;

    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private Path path;
    private ImportsWidgetPresenter importsWidget;

    public ProjectImportsScreenPresenter() {
    }

    @Inject
    public ProjectImportsScreenPresenter(@New ProjectImportsScreenView view,
                                         @New FileMenuBuilder menuBuilder,
                                         @New ImportsWidgetPresenter importsWidget,
                                         Caller<MetadataService> metadataService,
                                         Event<NotificationEvent> notification) {
        this.view = view;
        this.menuBuilder = menuBuilder;
        this.importsWidget = importsWidget;
        this.metadataService = metadataService;
        this.notification = notification;
        view.setPresenter(this);
    }

    @OnStart
    public void init(final Path path) {
        this.path = path;

        makeMenuBar();

        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());

        loadContent();
    }

    private void loadContent() {
        importsWidget.init(path, true);
    }

    private void makeMenuBar() {
        menus = menuBuilder.addRestoreVersion(path).build();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectConfigScreenConstants.INSTANCE.ExternalImports();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onShowMetadata() {
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        metadataService.call(getMetadataSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).getMetadata(path);
    }


    private RemoteCallback<Metadata> getMetadataSuccessCallback() {
        return new RemoteCallback<Metadata>() {

            @Override
            public void callback(final Metadata metadata) {
                view.setMetadata(metadata);
                view.hideBusyIndicator();
            }
        };
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
