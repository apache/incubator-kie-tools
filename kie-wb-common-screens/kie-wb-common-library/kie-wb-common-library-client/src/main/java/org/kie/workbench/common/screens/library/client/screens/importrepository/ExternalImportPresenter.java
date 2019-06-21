package org.kie.workbench.common.screens.library.client.screens.importrepository;

import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ProjectImportService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.ImportProjectsPreferences;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.example.ExampleProjectWidget;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.library.client.screens.importrepository.Source.Kind.EXTERNAL;

@Source(EXTERNAL)
public class ExternalImportPresenter extends ImportPresenter {

    private final TranslationService ts;
    private final Caller<ProjectImportService> importService;

    @Inject
    public ExternalImportPresenter(ImportPresenter.View view,
                                   LibraryPlaces libraryPlaces,
                                   Caller<ProjectImportService> importService,
                                   ManagedInstance<ExampleProjectWidget> tileWidgets,
                                   WorkspaceProjectContext projectContext,
                                   Event<NotificationEvent> notificationEvent,
                                   Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent,
                                   Elemental2DomUtil elemental2DomUtil,
                                   TranslationService ts,
                                   ImportProjectsPreferences importProjectsPreferences,
                                   Caller<LibraryService> libraryService) {
        super(view,
              libraryPlaces,
              tileWidgets,
              projectContext,
              notificationEvent,
              projectContextChangeEvent,
              elemental2DomUtil,
              importProjectsPreferences,
              libraryService,
              ts.getTranslation(LibraryConstants.ImportProjects));
        this.importService = importService;
        this.ts = ts;
    }

    @Override
    protected void loadProjects(PlaceRequest placeRequest,
                                RemoteCallback<Set<ImportProject>> callback) {
        // Projects are loaded by CDI event that calls setupEvents. Nothing to do here.
    }

    @Override
    protected void importProjects(List<ImportProject> projects,
                                  RemoteCallback<WorkspaceProjectContextChangeEvent> callback,
                                  ErrorCallback<Message> errorCallback) {
        final OrganizationalUnit activeOU = activeOrganizationalUnit();
        this.importService.call((WorkspaceProjectContextChangeEvent event) -> callback.callback(event),
                                errorCallback).importProjects(activeOU,
                                                              projects);
    }
}
