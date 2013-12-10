package org.drools.workbench.screens.guided.template.client.resources.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Images for Guided Rule Editor
 */
public interface GuidedTemplateEditorImages extends
                                            ClientBundle {

    @ClientBundle.Source("BPM_FileIcons_guidedrule.png")
    ImageResource typeGuidedRuleTemplate();

    @ClientBundle.Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @ClientBundle.Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @ClientBundle.Source("icon-merge.png")
    ImageResource toggleMergeIcon();

}
