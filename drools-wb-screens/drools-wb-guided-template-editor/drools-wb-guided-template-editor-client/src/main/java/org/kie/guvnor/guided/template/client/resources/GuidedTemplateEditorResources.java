package org.kie.guvnor.guided.template.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.kie.workbench.widgets.decoratedgrid.client.resources.TableImageResources;
import org.kie.guvnor.commons.ui.client.resources.CollapseExpand;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.guided.template.client.resources.css.GuidedTemplateEditorCss;
import org.kie.guvnor.guided.template.client.resources.images.GuidedTemplateEditorImages;

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
