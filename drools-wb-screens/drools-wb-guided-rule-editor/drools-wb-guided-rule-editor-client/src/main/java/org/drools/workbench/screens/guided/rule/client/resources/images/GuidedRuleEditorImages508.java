/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.resources.images;

import com.google.gwt.user.client.ui.Image;
import org.kie.workbench.common.widgets.client.resources.CommonImages;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;

public class GuidedRuleEditorImages508 {

    public static final GuidedRuleEditorImages508 INSTANCE = new GuidedRuleEditorImages508();

    private GuidedRuleEditorImages508() {
    }

    public Image Wizard() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.images().newWiz() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.Wizard() );
        return image;
    }

    public Image DeleteItemSmall() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.itemImages().deleteItemSmall() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.DeleteItem() );
        return image;
    }

    public Image NewItem() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.itemImages().newItem() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.NewItem() );
        return image;
    }

    public Image WarningSmall() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.images().warning() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.Warning() );
        return image;
    }

    public Image Error() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.images().error() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.Error() );
        return image;
    }

    public Image EditDisabled() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.images().editDisabled() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.EditDisabled() );
        return image;
    }

    public Image AddFieldToFact() {
        Image image = new Image( GuidedRuleEditorResources.INSTANCE.images().addFieldToFact() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.AddAFieldToThisExpectation() );
        return image;
    }

    public Image Edit() {
        Image image = new Image( CommonImages.INSTANCE.edit() );
        image.setAltText( GuidedRuleEditorResources.CONSTANTS.Edit() );
        return image;
    }

}
