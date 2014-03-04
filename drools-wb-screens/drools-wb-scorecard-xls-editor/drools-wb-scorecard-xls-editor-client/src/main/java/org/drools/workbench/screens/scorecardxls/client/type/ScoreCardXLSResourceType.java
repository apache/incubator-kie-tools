package org.drools.workbench.screens.scorecardxls.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scorecardxls.client.resources.ScoreCardXLSEditorResources;
import org.drools.workbench.screens.scorecardxls.client.resources.i18n.ScoreCardXLSEditorConstants;
import org.drools.workbench.screens.scorecardxls.type.ScoreCardXLSResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class ScoreCardXLSResourceType
        extends ScoreCardXLSResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( ScoreCardXLSEditorResources.INSTANCE.images().typeXLSScoreCard() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = ScoreCardXLSEditorConstants.INSTANCE.scoreCardXLSResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
