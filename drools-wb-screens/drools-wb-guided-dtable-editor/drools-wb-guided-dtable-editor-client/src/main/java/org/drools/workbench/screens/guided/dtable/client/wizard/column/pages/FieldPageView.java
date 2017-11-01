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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.jboss.errai.common.client.dom.Div;
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

    private FieldPage<?> page;

    @DataField("fieldList")
    private ListBox fieldList;

    @DataField("fieldTextBox")
    private TextBox fieldTextBox;

    @DataField("fieldListDescription")
    private Div fieldListDescription;

    @DataField("predicateFieldDescription")
    private Div predicateFieldDescription;

    @DataField("patternWarning")
    private Div patternWarning;

    @Inject
    @DataField("fieldWarning")
    private Div fieldWarning;

    private TranslationService translationService;

    @Inject
    public FieldPageView(final ListBox fieldList,
                         final TextBox fieldTextBox,
                         final Div patternWarning,
                         final TranslationService translationService,
                         final Div fieldListDescription,
                         final Div predicateFieldDescription) {
        this.fieldList = fieldList;
        this.fieldTextBox = fieldTextBox;
        this.patternWarning = patternWarning;
        this.translationService = translationService;
        this.fieldListDescription = fieldListDescription;
        this.predicateFieldDescription = predicateFieldDescription;
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
        patternWarning.setHidden(!isVisible);
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
        fieldWarning.setHidden(false);
    }

    @Override
    public void hideSelectFieldWarning() {
        fieldWarning.setHidden(true);
    }

    @Override
    public void enableListFieldView() {
        toggleViewElements(false);
    }

    @Override
    public void enablePredicateFieldView() {
        toggleViewElements(true);
    }

    private void toggleViewElements(final boolean showPredicateElements) {

        fieldTextBox.setVisible(showPredicateElements);
        predicateFieldDescription.setHidden(!showPredicateElements);

        fieldList.setVisible(!showPredicateElements);
        fieldListDescription.setHidden(showPredicateElements);
    }
}
