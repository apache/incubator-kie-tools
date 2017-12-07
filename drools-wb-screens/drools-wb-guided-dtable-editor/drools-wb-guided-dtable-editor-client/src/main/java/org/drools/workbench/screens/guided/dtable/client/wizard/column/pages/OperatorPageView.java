/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class OperatorPageView implements IsElement,
                                         OperatorPage.View {

    private OperatorPage page;

    @DataField("warning")
    private HTMLDivElement warning;

    @Inject
    @DataField("operatorWarning")
    private HTMLDivElement operatorWarning;

    @DataField("info")
    private HTMLDivElement info;

    @DataField("operatorsContainer")
    private HTMLDivElement operatorsContainer;

    @DataField("currentField")
    private HTMLDivElement currentField;

    @DataField("currentFieldContainer")
    private HTMLDivElement currentFieldContainer;

    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    public OperatorPageView(final HTMLDivElement warning,
                            final HTMLDivElement info,
                            final HTMLDivElement operatorsContainer,
                            final HTMLDivElement currentField,
                            final HTMLDivElement currentFieldContainer,
                            final Elemental2DomUtil elemental2DomUtil) {
        this.warning = warning;
        this.info = info;
        this.operatorsContainer = operatorsContainer;
        this.currentField = currentField;
        this.currentFieldContainer = currentFieldContainer;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    @Override
    public void init(final OperatorPage page) {
        this.page = page;
    }

    @Override
    public void showFactFieldWarningWhenItIsNotDefined(final boolean hasOperator) {
        info.hidden = true;
        warning.hidden = hasOperator;
    }

    @Override
    public void showPredicateWarning() {
        info.hidden = false;
        warning.hidden = true;
    }

    @Override
    public void setupOperator(final IsWidget dropdown) {
        elemental2DomUtil.removeAllElementChildren(operatorsContainer);
        elemental2DomUtil.appendWidgetToElement(operatorsContainer, dropdown.asWidget());
    }

    @Override
    public void showOperatorWarning() {
        operatorWarning.hidden = false;
    }

    @Override
    public void hideOperatorWarning() {
        operatorWarning.hidden = true;
    }

    @Override
    public void setCurrentField(final String currentField) {
        this.currentField.textContent = currentField;
    }

    @Override
    public void currentFieldToggle(final boolean isVisible) {
        this.currentFieldContainer.hidden = !isVisible;
    }
}
