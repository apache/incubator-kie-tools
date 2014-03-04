package org.drools.workbench.screens.guided.scorecard.client.type;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.scorecard.client.resources.GuidedScoreCardResources;
import org.drools.workbench.screens.guided.scorecard.client.resources.i18n.GuidedScoreCardConstants;
import org.drools.workbench.screens.guided.scorecard.type.GuidedScoreCardResourceTypeDefinition;
import org.uberfire.client.workbench.type.ClientResourceType;

@ApplicationScoped
public class GuidedScoreCardResourceType
        extends GuidedScoreCardResourceTypeDefinition
        implements ClientResourceType {

    private static final Image IMAGE = new Image( GuidedScoreCardResources.INSTANCE.images().typeGuidedScoreCard() );

    @Override
    public IsWidget getIcon() {
        return IMAGE;
    }

    @Override
    public String getDescription() {
        String desc = GuidedScoreCardConstants.INSTANCE.guidedScoreCardResourceTypeDescription();
        if ( desc == null || desc.isEmpty() ) return super.getDescription();
        return desc;
    }
}
