/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.perspective;

import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@ApplicationScoped
@WorkbenchPerspective(identifier = LibraryPlaces.LIBRARY_PERSPECTIVE)
public class LibraryPerspective {

    private LibraryPlaces libraryPlaces;

    private Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent;

    private Caller<VFSService> vfsServices;

    private Event<NotificationEvent> notificationEvent;

    private TranslationService ts;

    private PerspectiveDefinition perspectiveDefinition;

    private boolean refresh = true;

    private String projectPath;

    public LibraryPerspective() {
    }

    @Inject
    public LibraryPerspective(final LibraryPlaces libraryPlaces,
                              final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent,
                              final Caller<VFSService> vfsServices,
                              final Event<NotificationEvent> notificationEvent,
                              final TranslationService ts) {
        this.libraryPlaces = libraryPlaces;
        this.projectContextChangeEvent = projectContextChangeEvent;
        this.vfsServices = vfsServices;
        this.notificationEvent = notificationEvent;
        this.ts = ts;
    }

    @Perspective
    public PerspectiveDefinition buildPerspective() {
        if (perspectiveDefinition == null) {
            perspectiveDefinition = new PerspectiveDefinitionImpl(MultiListWorkbenchPanelPresenter.class.getName());
            perspectiveDefinition.setName("Library Perspective");
        }

        return perspectiveDefinition;
    }

    @OnStartup
    public void onStartup(final PlaceRequest placeRequest) {
        this.projectPath = (getWindowParameterMap().containsKey("path") ? getWindowParameterMap().get("path").get(0) : "").trim();
        this.refresh = Boolean.parseBoolean(placeRequest.getParameter("refresh", "true"));
        this.libraryPlaces.init(this);
    }

    public void perspectiveChangeEvent(@Observes PerspectiveChange event) {
        if (event.getIdentifier().equals(LibraryPlaces.LIBRARY_PERSPECTIVE)) {
            if (refresh) {
                if (projectPath != null && !projectPath.isEmpty()) {
                    libraryPlaces.refresh(() -> {
                        if (getRootPanel() != null) {
                            vfsServices.call((RemoteCallback<Path>) path -> {
                                libraryPlaces.goToProject(path);
                            }, (o, throwable) -> {
                                notificationEvent.fire(new NotificationEvent(ts.format(LibraryConstants.InvalidProjectPath),
                                                                             NotificationEvent.NotificationType.ERROR));
                                return false;
                            }).get(projectPath);
                        }
                    });
                } else {
                    libraryPlaces.refresh(() -> {
                        if (getRootPanel() != null) {
                            libraryPlaces.goToLibrary();
                        }
                    });
                }
            } else {
                libraryPlaces.refresh(() -> {
                });
            }
        }
    }

    @OnClose
    public void onClose() {
        projectContextChangeEvent.fire(new WorkspaceProjectContextChangeEvent());
    }

    Map<String, List<String>> getWindowParameterMap() {
        return Window.Location.getParameterMap();
    }

    public PanelDefinition getRootPanel() {
        return buildPerspective().getRoot();
    }
}
