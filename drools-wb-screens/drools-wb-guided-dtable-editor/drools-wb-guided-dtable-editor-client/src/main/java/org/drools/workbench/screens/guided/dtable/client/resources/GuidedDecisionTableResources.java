package org.drools.workbench.screens.guided.dtable.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.dtable.client.resources.css.CssResources;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.TableImageResources;
import org.kie.workbench.common.widgets.client.resources.CollapseExpand;
import org.kie.workbench.common.widgets.client.resources.ItemImages;

/**
 * General Decision Table resources.
 */
public interface GuidedDecisionTableResources
        extends
        ClientBundle {

    GuidedDecisionTableResources INSTANCE = GWT.create( GuidedDecisionTableResources.class );

    TableImageResources tableImageResources();

    CollapseExpand collapseExpand();

    ItemImages itemImages();

    @Source("css/DecisionTable.css")
    CssResources css();

    GuidedDecisionTableImageResources images();

};
