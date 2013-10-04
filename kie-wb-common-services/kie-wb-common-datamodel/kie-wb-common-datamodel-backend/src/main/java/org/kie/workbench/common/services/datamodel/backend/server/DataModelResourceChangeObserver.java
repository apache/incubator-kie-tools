package org.kie.workbench.common.services.datamodel.backend.server;

import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Server side component that observes for the different resource add/delete/update events related to
 * a given project and that causes the ProjectDataModelOracle to be invalidated. Typically .java, .class and pom.xml
 * files. When such a resource is modified an InvalidateDMOProjectCacheEvent event is fired.
 */
@ApplicationScoped
public class DataModelResourceChangeObserver {

    private static final Logger logger = LoggerFactory.getLogger(DataModelResourceChangeObserver.class);

    @Inject
    private ProjectService projectService;

    @Inject
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOProjectCacheEvent;

    public void processResourceAdd( @Observes final ResourceAddedEvent resourceAddedEvent ) {
        processResourceChange(resourceAddedEvent.getSessionInfo(), resourceAddedEvent.getPath(), ChangeType.ADD);
    }

    public void processResourceDelete( @Observes final ResourceDeletedEvent resourceDeletedEvent ) {
        processResourceChange(resourceDeletedEvent.getSessionInfo(), resourceDeletedEvent.getPath(), ChangeType.DELETE);
    }

    public void processResourceUpdate( @Observes final ResourceUpdatedEvent resourceUpdatedEvent ) {
        processResourceChange(resourceUpdatedEvent.getSessionInfo(), resourceUpdatedEvent.getPath(), ChangeType.UPDATE);
    }

    public void processBatchChanges( @Observes final ResourceBatchChangesEvent resourceBatchChangesEvent ) {

        final Set<ResourceChange> batchChanges = resourceBatchChangesEvent.getBatch();
        final Map<Project, Boolean> notifiedProjects = new HashMap<Project, Boolean>();

        if (batchChanges == null) {
            //un expected case
            logger.warn("No batchChanges was present for the given resourceBatchChangesEvent: " + resourceBatchChangesEvent);
        }

        //all the changes mus be processed, we don't have warranties that all the changes belongs to the same project.
        for ( ResourceChange change : batchChanges ) {
            processResourceChange(change.getSessionInfo(), change.getPath(), change.getType(), notifiedProjects);
        }
    }

    private void processResourceChange(final SessionInfo sessionInfo, final Path path, final ChangeType changeType) {
        processResourceChange(sessionInfo, path, changeType, new HashMap<Project, Boolean>());
    }

    private void processResourceChange(final SessionInfo sessionInfo, final Path path, final ChangeType changeType, final Map<Project, Boolean> notifiedProjects) {

        final Project project = projectService.resolveProject( path );

        if (logger.isDebugEnabled())
            logger.debug("Processing resource change for sessionInfo: " + sessionInfo + ", project: " + project +
                ", path: " + path + ", changeType: " + changeType);

        if (project != null && !notifiedProjects.containsKey(project) && isObservableResource(path)) {
            invalidateDMOProjectCacheEvent.fire(new InvalidateDMOProjectCacheEvent(sessionInfo, project, path));
            notifiedProjects.put(project, Boolean.TRUE);
        }
    }

    private boolean isObservableResource(Path path) {
        return path != null && (path.getFileName().endsWith(".java") || path.getFileName().endsWith(".class") || path.getFileName().equals("pom.xml"));
    }
}
