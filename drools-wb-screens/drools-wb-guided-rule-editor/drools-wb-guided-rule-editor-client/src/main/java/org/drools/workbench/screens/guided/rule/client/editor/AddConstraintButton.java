/*
 * Copyright 2014 JBoss Inc
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
package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.uberfire.client.common.ImageButton;

public class AddConstraintButton
        extends Composite {

    private final ImageButton imageButton = getAddButton();

    private final Image error = GuidedRuleEditorImages508.INSTANCE.Error();

    public AddConstraintButton(ClickHandler clickHandler) {
        imageButton.addClickHandler(clickHandler);

        HorizontalPanel widgets = new HorizontalPanel();
        widgets.add(imageButton);
        error.setVisible(false);
        error.setTitle(GuidedRuleEditorResources.CONSTANTS.PleaseSetTheConstraintValue());
        error.setAltText(GuidedRuleEditorResources.CONSTANTS.PleaseSetTheConstraintValue());
        widgets.add(error);

        initWidget(widgets);
    }

    public void showError() {
        error.setVisible(true);
    }

    public void hideError() {
        error.setVisible(false);
    }

    private ImageButton getAddButton() {
        return new ImageButton(
                GuidedRuleEditorImages508.INSTANCE.Edit(),
                GuidedRuleEditorImages508.INSTANCE.EditDisabled(),
                GuidedRuleEditorResources.CONSTANTS.Edit()
        );
    }

    public void setEnabled(boolean enabled) {
        imageButton.setEnabled(enabled);
    }
}
