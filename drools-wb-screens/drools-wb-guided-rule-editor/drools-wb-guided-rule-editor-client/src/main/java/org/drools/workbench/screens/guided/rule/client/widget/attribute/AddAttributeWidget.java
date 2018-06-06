/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;

public class AddAttributeWidget implements IsWidget {

    private final Image add = GuidedRuleEditorImages508.INSTANCE.NewItem();

    private RuleModeller ruleModeller;

    public AddAttributeWidget() {
        add.setTitle(GuidedRuleEditorResources.CONSTANTS.AddAnOptionToTheRuleToModifyItsBehaviorWhenEvaluatedOrExecuted());
    }

    public void init(final RuleModeller ruleModeller) {
        this.ruleModeller = ruleModeller;
        ((Image) asWidget()).addClickHandler(event -> showAttributeSelectorPopup());
    }

    @Override
    public Widget asWidget() {
        return add;
    }

    public void showAttributeSelectorPopup() {
        final GuidedRuleAttributeSelectorPopup popup = GWT.create(GuidedRuleAttributeSelectorPopup.class);
        popup.init(ruleModeller.getModel(),
                   ruleModeller.lockLHS(),
                   ruleModeller.lockRHS(),
                   () -> ruleModeller.refreshWidget());
        popup.show();
    }
}
