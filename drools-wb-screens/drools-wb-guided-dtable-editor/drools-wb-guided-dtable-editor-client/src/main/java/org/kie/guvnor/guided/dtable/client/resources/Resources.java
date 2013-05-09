package org.kie.guvnor.guided.dtable.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.kie.workbench.widgets.decoratedgrid.client.resources.TableImageResources;
import org.kie.guvnor.commons.ui.client.resources.CollapseExpand;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.guided.dtable.client.resources.css.CssResources;
import org.kie.guvnor.guided.dtable.client.resources.images.ImageResources;

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
