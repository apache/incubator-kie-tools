package org.drools.workbench.screens.scorecardxls.service;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class ScoreCardXLSContent {

    private Overview overview;

    public void setOverview(Overview overview) {
        this.overview = PortablePreconditions.checkNotNull("overview", overview);
    }

    public Overview getOverview() {
        return overview;
    }
}
