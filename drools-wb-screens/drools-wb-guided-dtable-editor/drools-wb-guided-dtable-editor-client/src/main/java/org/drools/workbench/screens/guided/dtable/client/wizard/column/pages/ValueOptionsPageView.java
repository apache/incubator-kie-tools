/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils;
import org.gwtbootstrap3.client.ui.TextBox;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.addWidgetToContainer;

@Dependent
@Templated
public class ValueOptionsPageView implements IsElement,
                                             ValueOptionsPage.View {

    @DataField("valueListGroupContainer")
    private Div valueListGroupContainer;

    @DataField("cepWindowOperatorsGroupContainer")
    private Div cepWindowOperatorsGroupContainer;

    @DataField("defaultValueGroupContainer")
    private Div defaultValueGroupContainer;

    @DataField("limitedValueGroupContainer")
    private Div limitedValueGroupContainer;

    @DataField("bindingGroupContainer")
    private Div bindingGroupContainer;

    @DataField("valueList")
    private TextBox valueList;

    @DataField("cepWindowOperatorsContainer")
    private Div cepWindowOperatorsContainer;

    @DataField("defaultValueContainer")
    private Div defaultValueContainer;

    @DataField("limitedValueContainer")
    private Div limitedValueContainer;

    @DataField("bindingContainer")
    private Div bindingContainer;

    private ValueOptionsPage<?> page;

    private TranslationService translationService;

    @Inject
    public ValueOptionsPageView(final Div valueListGroupContainer,
                                final Div cepWindowOperatorsGroupContainer,
                                final Div defaultValueGroupContainer,
                                final Div limitedValueGroupContainer,
                                final Div bindingGroupContainer,
                                final TextBox valueList,
                                final Div cepWindowOperatorsContainer,
                                final Div defaultValueContainer,
                                final Div limitedValueContainer,
                                final Div bindingContainer,
                                final TranslationService translationService) {
        this.valueListGroupContainer = valueListGroupContainer;
        this.cepWindowOperatorsGroupContainer = cepWindowOperatorsGroupContainer;
        this.defaultValueGroupContainer = defaultValueGroupContainer;
        this.limitedValueGroupContainer = limitedValueGroupContainer;
        this.bindingGroupContainer = bindingGroupContainer;
        this.valueList = valueList;
        this.cepWindowOperatorsContainer = cepWindowOperatorsContainer;
        this.defaultValueContainer = defaultValueContainer;
        this.limitedValueContainer = limitedValueContainer;
        this.bindingContainer = bindingContainer;
        this.translationService = translationService;
    }

    @PostConstruct
    public void initPopovers() {
        DecisionTableColumnViewUtils.setupPopover(cepWindowOperatorsContainer,
                                                  translate(GuidedDecisionTableErraiConstants.ValueOptionsPage_CEPWindowDescription));
        DecisionTableColumnViewUtils.setupPopover(defaultValueContainer,
                                                  translate(GuidedDecisionTableErraiConstants.ValueOptionsPage_DefaultValueDescription));
        DecisionTableColumnViewUtils.setupPopover(bindingContainer,
                                                  translate(GuidedDecisionTableErraiConstants.ValueOptionsPage_BindingDescription));
    }

    @Override
    public void init(final ValueOptionsPage page) {
        this.page = page;
    }

    @EventHandler("valueList")
    public void onSelectValueList(final KeyUpEvent event) {
        page.setValueList(valueList.getText());
    }

    @Override
    public void setValueListText(final String valueListText) {
        valueList.setText(valueListText);
    }

    @Override
    public void enableValueList() {
        valueList.getElement().removeAttribute("disabled");
    }

    @Override
    public void disableValueList() {
        valueList.getElement().setAttribute("disabled",
                                            "disabled");
    }

    @Override
    public void hideValueList() {
        valueListGroupContainer.setHidden(true);
    }

    @Override
    public void setupDefaultValue(final IsWidget widget) {
        addWidgetToContainer(widget,
                             defaultValueContainer);
    }

    @Override
    public void hideDefaultValue() {
        defaultValueGroupContainer.setHidden(true);
    }

    @Override
    public void setupLimitedValue(final IsWidget widget) {
        addWidgetToContainer(widget,
                             limitedValueContainer);
    }

    @Override
    public void hideLimitedValue() {
        limitedValueGroupContainer.setHidden(true);
    }

    @Override
    public void setupBinding(final IsWidget widget) {
        addWidgetToContainer(widget,
                             bindingContainer);
    }

    @Override
    public void hideBinding() {
        bindingGroupContainer.setHidden(true);
    }

    @Override
    public void setupCepOperators(final IsWidget widget) {
        addWidgetToContainer(widget,
                             cepWindowOperatorsContainer);
    }

    @Override
    public void hideCepOperators() {
        cepWindowOperatorsGroupContainer.setHidden(true);
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }
}
