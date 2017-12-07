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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import elemental2.dom.HTMLDivElement;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.getCurrentIndexFromList;

@Dependent
@Templated
public class FieldPageView implements IsElement,
                                      FieldPage.View {

    @DataField("fieldList")
    private final ListBox fieldList;

    @DataField("fieldTextBox")
    private final TextBox fieldTextBox;

    @DataField("fieldListDescription")
    private final HTMLDivElement fieldListDescription;

    @DataField("predicateFieldDescription")
    private final HTMLDivElement predicateFieldDescription;

    @DataField("patternWarning")
    private final HTMLDivElement patternWarning;

    @DataField("fieldWarning")
    private final HTMLDivElement fieldWarning;

    @DataField("bindingContainer")
    private final HTMLDivElement bindingContainer;

    @DataField("bindingFieldContainer")
    private final HTMLDivElement bindingFieldContainer;

    @DataField("fieldBindingWarning")
    private final HTMLDivElement fieldBindingWarning;

    @DataField("predicateBindingInfo")
    private final HTMLDivElement predicateBindingInfo;

    private final DecisionTablePopoverUtils popoverUtils;

    private final Elemental2DomUtil elemental2DomUtil;

    private final TranslationService translationService;

    private FieldPage<?> page;

    @Inject
    public FieldPageView(final ListBox fieldList,
                         final TextBox fieldTextBox,
                         final HTMLDivElement fieldWarning,
                         final HTMLDivElement patternWarning,
                         final HTMLDivElement fieldListDescription,
                         final HTMLDivElement predicateFieldDescription,
                         final HTMLDivElement bindingContainer,
                         final HTMLDivElement bindingFieldContainer,
                         final HTMLDivElement fieldBindingWarning,
                         final HTMLDivElement predicateBindingInfo,
                         final TranslationService translationService,
                         final Elemental2DomUtil elemental2DomUtil,
                         final DecisionTablePopoverUtils popoverUtils) {
        this.fieldList = fieldList;
        this.fieldTextBox = fieldTextBox;
        this.patternWarning = patternWarning;
        this.fieldWarning = fieldWarning;
        this.fieldListDescription = fieldListDescription;
        this.predicateFieldDescription = predicateFieldDescription;
        this.bindingContainer = bindingContainer;
        this.bindingFieldContainer = bindingFieldContainer;
        this.fieldBindingWarning = fieldBindingWarning;
        this.predicateBindingInfo = predicateBindingInfo;
        this.translationService = translationService;
        this.elemental2DomUtil = elemental2DomUtil;
        this.popoverUtils = popoverUtils;
    }

    @PostConstruct
    public void initPopovers() {
        final String bindingDescription = translate(GuidedDecisionTableErraiConstants.FieldPage_BindingDescription);

        popoverUtils.setupAndRegisterPopover(bindingFieldContainer, bindingDescription);
    }

    @Override
    public void init(final FieldPage page) {
        this.page = page;
    }

    @EventHandler("fieldList")
    public void onFieldListSelected(final ChangeEvent event) {
        page.setEditingCol(fieldList.getSelectedValue());
    }

    @EventHandler("fieldTextBox")
    public void onFieldTextBoxChange(final KeyUpEvent event) {
        page.setEditingCol(fieldTextBox.getText());
    }

    @Override
    public void patternWarningToggle(final boolean isVisible) {
        patternWarning.hidden = !isVisible;

        fieldTextBox.setEnabled(!isVisible);
        fieldList.setEnabled(!isVisible);
    }

    @Override
    public void setupEmptyFieldList() {

        final String selectField = translate(GuidedDecisionTableErraiConstants.FieldPageView_SelectField);
        final String item = "-- " + selectField + " --";
        final String blankValue = "";

        fieldList.clear();
        fieldList.addItem(item, blankValue);
    }

    @Override
    public void selectField(final String factField) {
        fieldList.setSelectedIndex(getCurrentIndexFromList(factField, fieldList));
    }

    @Override
    public void setField(final String factField) {
        fieldTextBox.setText(factField);
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }

    @Override
    public void addItem(final String itemName,
                        final String itemKey) {
        fieldList.addItem(itemName,
                          itemKey);
    }

    @Override
    public void showSelectFieldWarning() {
        fieldWarning.hidden = false;
    }

    @Override
    public void hideSelectFieldWarning() {
        fieldWarning.hidden = true;
    }

    @Override
    public void enableListFieldView() {
        toggleViewElements(false);
    }

    @Override
    public void enablePredicateFieldView() {
        toggleViewElements(true);
    }

    @Override
    public void setupBinding(final IsWidget widget) {
        elemental2DomUtil.removeAllElementChildren(bindingFieldContainer);
        elemental2DomUtil.appendWidgetToElement(bindingFieldContainer, widget.asWidget());
    }

    @Override
    public void showFieldBindingWarning() {
        fieldBindingWarning.hidden = false;
    }

    @Override
    public void hideFieldBindingWarning() {
        fieldBindingWarning.hidden = true;
    }

    @Override
    public void showPredicateBindingInfo() {
        predicateBindingInfo.hidden = false;
    }

    @Override
    public void hidePredicateBindingInfo() {
        predicateBindingInfo.hidden = true;
    }

    @Override
    public void bindingToggle(final boolean isVisible) {
        bindingContainer.hidden = !isVisible;
    }

    private void toggleViewElements(final boolean showPredicateElements) {

        fieldTextBox.setVisible(showPredicateElements);
        predicateFieldDescription.hidden = !showPredicateElements;

        fieldList.setVisible(!showPredicateElements);
        fieldListDescription.hidden = showPredicateElements;
    }
}
