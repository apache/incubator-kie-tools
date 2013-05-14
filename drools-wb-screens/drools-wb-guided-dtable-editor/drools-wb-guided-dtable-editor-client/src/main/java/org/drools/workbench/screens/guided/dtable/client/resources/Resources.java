package org.drools.workbench.screens.guided.dtable.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.dtable.client.resources.css.CssResources;
import org.drools.workbench.screens.guided.dtable.client.resources.images.ImageResources;
import org.kie.workbench.widgets.decoratedgrid.client.resources.TableImageResources;
import org.kie.workbench.widgets.common.client.resources.CollapseExpand;
import org.kie.workbench.widgets.common.client.resources.ItemImages;

/**
 * General Decision Table resources.
 */
public interface Resources
        extends
        ClientBundle {

    Resources INSTANCE = GWT.create( Resources.class );

    TableImageResources tableImageResources();

    CollapseExpand collapseExpand();

    ItemImages itemImages();

    @Source("css/DecisionTable.css")
    CssResources css();

    ImageResources images();

};
