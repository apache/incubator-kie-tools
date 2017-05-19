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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;
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

    @DataField("fieldsList")
    private ListBox fieldsList;

    @DataField("patternWarning")
    private Div patternWarning;

    @Inject
    @DataField("fieldWarning")
    private Div fieldWarning;

    @DataField("info")
    private Div info;

    private TranslationService translationService;

    @Inject
    public FieldPageView(final ListBox fieldsList,
                         final Div patternWarning,
                         final Div info,
                         final TranslationService translationService) {
        this.fieldsList = fieldsList;
        this.patternWarning = patternWarning;
        this.info = info;
        this.translationService = translationService;
    }

    @Override
    public void init(final FieldPage page) {
        this.page = page;
    }

    @EventHandler("fieldsList")
    public void onFieldSelected(final ChangeEvent event) {
        page.setEditingCol(fieldsList.getSelectedValue());
    }

    @Override
    public void showPredicateWarning() {
        info.setHidden(false);
        patternWarning.setHidden(true);
        fieldsList.setEnabled(false);
    }

    @Override
    public void showPatternWarningWhenItIsNotDefined(final boolean hasPattern) {
        info.setHidden(true);
        patternWarning.setHidden(hasPattern);
        fieldsList.setEnabled(hasPattern);
    }

    @Override
    public void setupFieldList() {
        final String selectField = translate(GuidedDecisionTableErraiConstants.FieldPageView_SelectField);

        fieldsList.clear();
        fieldsList.addItem("-- " + selectField + " --",
                           "");
    }

    @Override
    public void selectField(final String factField) {
        fieldsList.setSelectedIndex(getCurrentIndexFromList(factField,
                                                            fieldsList));
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }

    @Override
    public void addItem(final String itemName,
                        final String itemKey) {
        fieldsList.addItem(itemName,
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
}
