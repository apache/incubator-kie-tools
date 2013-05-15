package org.drools.workbench.screens.guided.template.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.drools.workbench.screens.guided.template.client.resources.css.GuidedTemplateEditorCss;
import org.drools.workbench.screens.guided.template.client.resources.images.GuidedTemplateEditorImages;
import org.kie.workbench.common.widgets.client.resources.CollapseExpand;
import org.kie.workbench.common.widgets.client.resources.ItemImages;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.TableImageResources;

/**
 * Resources for the Guided Template Editor
 */
public interface GuidedTemplateEditorResources extends ClientBundle {

    GuidedTemplateEditorResources INSTANCE = GWT.create( GuidedTemplateEditorResources.class );

    TableImageResources tableImageResources();

    CollapseExpand collapseExpand();

    ItemImages itemImages();

    @Source("css/GuidedTemplateEditor.css")
    GuidedTemplateEditorCss css();

    GuidedTemplateEditorImages images();

}
