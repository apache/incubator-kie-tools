package org.drools.workbench.screens.scorecardxls.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;

import org.drools.workbench.screens.scorecardxls.type.ScoreCardXLSResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class ScoreCardXLSResourceType
        extends ScoreCardXLSResourceTypeDefinition
        implements ClientResourceType {

    @Override
    public IsWidget getIcon() {
        return null;
    }

}
