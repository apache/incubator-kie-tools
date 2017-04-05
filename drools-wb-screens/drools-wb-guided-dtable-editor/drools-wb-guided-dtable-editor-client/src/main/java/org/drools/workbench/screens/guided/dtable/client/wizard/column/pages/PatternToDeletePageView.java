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

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.ListBox;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.getCurrentIndexFromList;

@Dependent
@Templated
public class PatternToDeletePageView implements IsElement,
                                                PatternToDeletePage.View {

    @DataField("patternList")
    private ListBox patternList;

    private TranslationService translationService;

    private PatternToDeletePage page;

    @Inject
    public PatternToDeletePageView(final ListBox patternList,
                                   final TranslationService translationService) {
        this.patternList = patternList;
        this.translationService = translationService;
    }

    @Override
    public void init(final PatternToDeletePage page) {
        this.page = page;
    }

    @EventHandler("patternList")
    public void onPatternSelected(ChangeEvent event) {
        page.setTheSelectedPattern();
    }

    @Override
    public String selectedPattern() {
        return patternList.getSelectedValue();
    }

    @Override
    public void setupPatternList(final List<String> patterns) {
        patternList.clear();
        patternList.setEnabled(true);

        patternList.addItem(translate(GuidedDecisionTableErraiConstants.PatternToDeletePageView_Choose));

        patterns.forEach(patternList::addItem);
    }

    @Override
    public void setupEmptyPatternList() {
        patternList.clear();
        patternList.setEnabled(false);

        patternList.addItem(translate(GuidedDecisionTableErraiConstants.PatternToDeletePageView_None));
    }

    @Override
    public void selectTheCurrentPattern(final String currentValue) {
        final int currentValueIndex = getCurrentIndexFromList(currentValue,
                                                              patternList);

        patternList.setSelectedIndex(currentValueIndex);
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }
}
