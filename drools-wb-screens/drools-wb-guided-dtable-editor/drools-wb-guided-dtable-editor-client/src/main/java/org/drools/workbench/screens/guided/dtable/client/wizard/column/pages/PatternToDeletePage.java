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
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ActionRetractFactPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

public class PatternToDeletePage extends BaseDecisionTableColumnPage<ActionRetractFactPlugin> {

    private View view;

    @Inject
    public PatternToDeletePage(final View view,
                               final TranslationService translationService) {
        super(translationService);

        this.view = view;
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.PatternToDeletePage_Pattern);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        callback.callback(plugin().isPatternToDeletePageCompleted());
    }

    @Override
    public void prepareView() {
        view.init(this);

        markAsViewed();
        setupPatternList();
    }

    private void setupPatternList() {
        final List<String> patterns = loadPatterns();
        final boolean hasPatterns = !patterns.isEmpty();

        if (hasPatterns) {
            view.setupPatternList(patterns);
        } else {
            view.setupEmptyPatternList();
        }

        view.selectTheCurrentPattern(binding());
    }

    List<String> loadPatterns() {
        return getLHSBoundFacts()
                .stream()
                .filter(boundName -> !nil(boundName))
                .collect(Collectors.toList());
    }

    String binding() {
        return plugin().getEditingColStringValue();
    }

    List<String> getLHSBoundFacts() {
        final BRLRuleModel brlRuleModel = new BRLRuleModel(presenter.getModel());

        return brlRuleModel.getLHSBoundFacts();
    }

    void setTheSelectedPattern() {
        plugin().setEditingColStringValue(view.selectedPattern());
    }

    void markAsViewed() {
        plugin().setPatternToDeletePageAsCompleted();
    }

    public interface View extends UberElement<PatternToDeletePage> {

        String selectedPattern();

        void setupPatternList(final List<String> patterns);

        void setupEmptyPatternList();

        void selectTheCurrentPattern(final String currentValue);
    }
}
