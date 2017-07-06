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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasPatternPage;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.getCurrentIndexFromList;

@Dependent
@Templated
public class PatternPageView implements IsElement,
                                        PatternPage.View {

    private PatternPage<? extends HasPatternPage> page;

    @Inject
    @DataField("patternWarning")
    private Div patternWarning;

    @DataField("patternList")
    private ListBox patternList;

    @DataField("entryPointName")
    private TextBox entryPointName;

    @DataField("createANewFactPattern")
    private Button createANewFactPattern;

    @DataField("entryPointContainer")
    private Div entryPointContainer;

    private TranslationService translationService;

    @Inject
    public PatternPageView(final ListBox patternList,
                           final TextBox entryPointName,
                           final Button createANewFactPattern,
                           final Div entryPointContainer,
                           final TranslationService translationService) {
        this.patternList = patternList;
        this.entryPointName = entryPointName;
        this.createANewFactPattern = createANewFactPattern;
        this.entryPointContainer = entryPointContainer;
        this.translationService = translationService;
    }

    @Override
    public void init(final PatternPage page) {
        this.page = page;
    }

    @Override
    public void setupEntryPointName(final String entryPointName) {
        this.entryPointName.setText(entryPointName);
    }

    @EventHandler("createANewFactPattern")
    public void onCreateANewFactPattern(ClickEvent event) {
        page.showNewPatternModal();
    }

    @EventHandler("patternList")
    public void onEditingPatternSelected(ChangeEvent event) {
        page.setSelectedEditingPattern();
    }

    @EventHandler("entryPointName")
    public void onEntryPointChange(KeyUpEvent event) {
        page.setEntryPoint();
    }

    @Override
    public String getSelectedValue() {
        return patternList.getSelectedValue();
    }

    @Override
    public String getEntryPointName() {
        return entryPointName.getText();
    }

    @Override
    public void disableEntryPoint() {
        entryPointContainer.setHidden(true);
    }

    @Override
    public void clearPatternList() {
        final String selectPattern = translate(GuidedDecisionTableErraiConstants.PatternPageView_SelectPattern);

        patternList.clear();
        patternList.addItem("-- " + selectPattern + " --",
                            "");
    }

    @Override
    public void hidePatternListWhenItIsEmpty() {
        patternList.setVisible(patternList.getItemCount() > 1);
    }

    @Override
    public void selectPattern(final String currentPatternValue) {
        final int currentValueIndex = getCurrentIndexFromList(currentPatternValue,
                                                              patternList);

        patternList.setSelectedIndex(currentValueIndex);
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }

    @Override
    public void addItem(final String itemName,
                        final String itemKey) {
        patternList.addItem(itemName,
                            itemKey);
    }

    @Override
    public void showPatternWarning() {
        patternWarning.setHidden(false);
    }

    @Override
    public void hidePatternWarning() {
        patternWarning.setHidden(true);
    }

    @Override
    public void disablePatternCreation() {
        createANewFactPattern.setVisible(false);
        createANewFactPattern.setEnabled(false);
    }
}
