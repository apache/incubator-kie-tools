package org.kie.workbench.common.services.datamodel.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.model.IncrementalBuildResults;

/**
 * Test Observer for Build events
 */
@ApplicationScoped
public class BuildResultsObserver {

    private BuildResults buildResults;
    private IncrementalBuildResults incrementalBuildResults;

    public void onBuildResults( final @Observes BuildResults results ) {
        this.buildResults = results;
    }

    public void onIncrementalBuildResults( final @Observes IncrementalBuildResults results ) {
        this.incrementalBuildResults = results;
    }

    public BuildResults getBuildResults() {
        return buildResults;
    }

    public IncrementalBuildResults getIncrementalBuildResults() {
        return incrementalBuildResults;
    }
}
