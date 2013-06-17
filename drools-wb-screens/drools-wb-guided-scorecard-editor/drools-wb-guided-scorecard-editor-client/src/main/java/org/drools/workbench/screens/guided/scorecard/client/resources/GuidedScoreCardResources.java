package org.drools.workbench.screens.guided.scorecard.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.scorecard.client.resources.images.GuidedScoreCardImageResources;
import org.kie.workbench.common.widgets.client.resources.ItemImages;

/**
 * Resources for the Guided Rule Editor
 */
public interface GuidedScoreCardResources extends ClientBundle {

    GuidedScoreCardResources INSTANCE = GWT.create( GuidedScoreCardResources.class );

    ItemImages itemImages();

    GuidedScoreCardImageResources images();

}
