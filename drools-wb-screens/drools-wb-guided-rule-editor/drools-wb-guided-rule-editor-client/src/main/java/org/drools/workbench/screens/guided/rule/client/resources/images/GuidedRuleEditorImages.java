package org.drools.workbench.screens.guided.rule.client.resources.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Images for Guided Rule Editor
 */
public interface GuidedRuleEditorImages extends
                                        ClientBundle {

    @ClientBundle.Source("function_assets.gif")
    ImageResource functionAssets();

    @ClientBundle.Source("error.gif")
    ImageResource error();

    @ClientBundle.Source("config.png")
    ImageResource config();

    @ClientBundle.Source("edit.gif")
    ImageResource edit();

    @ClientBundle.Source("editDisabled.gif")
    ImageResource editDisabled();

    @ClientBundle.Source("add_field_to_fact.gif")
    ImageResource addFieldToFact();

    @ClientBundle.Source("add_connective.gif")
    ImageResource addConnective();

    @ClientBundle.Source("warning.gif")
    ImageResource warning();

    @ClientBundle.Source("new_wiz.gif")
    ImageResource newWiz();

    @ClientBundle.Source("field.gif")
    ImageResource field();

    @ClientBundle.Source("fact.gif")
    ImageResource fact();

    @ClientBundle.Source("BPM_FileIcons_guidedrule.png")
    ImageResource typeGuidedRule();

    @ClientBundle.Source("clock.png")
    ImageResource clock();

    @ClientBundle.Source("delete_item_small.gif")
    ImageResource deleteItemSmall();

    @ClientBundle.Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @ClientBundle.Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @ClientBundle.Source("icon-merge.png")
    ImageResource toggleMergeIcon();

    @ClientBundle.Source("information.gif")
    ImageResource information();

}
