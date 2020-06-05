/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.editor.page;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ShowRuleNameOptionView
        implements ShowRuleNameOptionPresenter.View,
                   IsElement {

    private ShowRuleNameOptionPresenter presenter;

    @DataField("ruleNameCheckBox")
    protected InputElement ruleNameCheckBox = Document.get().createCheckInputElement();

    public ShowRuleNameOptionView() {
        // CDI
    }

    @Override
    public void setShowRuleName(final boolean show) {
        ruleNameCheckBox.setChecked(show);
    }

    @EventHandler("ruleNameCheckBox")
    public void onCheckBoxChecked(ChangeEvent event) {
        presenter.onRuleNameCheckboxChanged(ruleNameCheckBox.isChecked());
    }

    @Override
    public void init(ShowRuleNameOptionPresenter presenter) {
        this.presenter = presenter;
        ruleNameCheckBox.setTitle(GuidedDecisionTableConstants.INSTANCE.ShowRuleNameColumnTooltip());
    }
}
