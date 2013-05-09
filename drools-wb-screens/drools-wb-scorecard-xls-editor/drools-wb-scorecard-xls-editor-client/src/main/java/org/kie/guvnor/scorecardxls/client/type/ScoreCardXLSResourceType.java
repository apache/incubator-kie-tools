package org.kie.guvnor.scorecardxls.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.IsWidget;

import org.kie.guvnor.scorecardxls.type.ScoreCardXLSResourceTypeDefinition;
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
