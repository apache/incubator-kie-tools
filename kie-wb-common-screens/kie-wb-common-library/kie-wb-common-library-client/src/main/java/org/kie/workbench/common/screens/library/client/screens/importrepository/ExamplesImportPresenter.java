package org.kie.workbench.common.screens.library.client.screens.importrepository;

import java.util.List;
import java.util.Set;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.examples.model.ExampleOrganizationalUnit;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.screens.examples.service.ExamplesService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.preferences.ImportProjectsPreferences;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.library.client.widgets.example.ExampleProjectWidget;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.library.client.screens.importrepository.Source.Kind.EXAMPLE;

@Source(EXAMPLE)
public class ExamplesImportPresenter extends ImportPresenter {

    private final Caller<ExamplesService> examplesService;

    @Inject
    public ExamplesImportPresenter(final ImportPresenter.View view,
                                   final LibraryPlaces libraryPlaces,
                                   final ManagedInstance<ExampleProjectWidget> tileWidgets,
                                   final Caller<ExamplesService> examplesService,
                                   final WorkspaceProjectContext projectContext,
                                   final Event<NotificationEvent> notificationEvent,
                                   final Event<WorkspaceProjectContextChangeEvent> projectContextChangeEvent,
                                   final Elemental2DomUtil elemental2DomUtil,
                                   final TranslationService ts,
                                   final ImportProjectsPreferences importProjectsPreferences,
                                   final Caller<LibraryService> libraryService) {

        super(view,
              libraryPlaces,
              tileWidgets,
              projectContext,
              notificationEvent,
              projectContextChangeEvent,
              elemental2DomUtil,
              importProjectsPreferences,
              libraryService,
              ts.getTranslation(LibraryConstants.TrySamples));
        this.examplesService = examplesService;
    }

    @Override
    protected void loadProjects(PlaceRequest placeRequest,
                                RemoteCallback<Set<ImportProject>> callback) {
        view.showBusyIndicator(view.getLoadingMessage());
        examplesService.call(callback,
                             loadingErrorCallback()).getExampleProjects();
    }

    @Override
    protected void importProjects(List<ImportProject> projects,
                                  RemoteCallback<WorkspaceProjectContextChangeEvent> callback,
                                  ErrorCallback<Message> errorCallback) {
        examplesService.call(callback,
                             errorCallback).setupExamples(new ExampleOrganizationalUnit(activeOrganizationalUnit().getName()),
                                                          projects);
    }
}
