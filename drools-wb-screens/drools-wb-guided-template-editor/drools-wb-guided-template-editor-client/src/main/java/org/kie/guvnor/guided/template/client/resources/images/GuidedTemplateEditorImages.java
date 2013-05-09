package org.kie.guvnor.guided.template.client.resources.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Images for Guided Rule Editor
 */
public interface GuidedTemplateEditorImages extends
                                            ClientBundle {

    @ClientBundle.Source("guidedRuleTemplateIcon.gif")
    ImageResource guidedRuleTemplateIcon();

    @ClientBundle.Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @ClientBundle.Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @ClientBundle.Source("icon-merge.png")
    ImageResource toggleMergeIcon();

}
