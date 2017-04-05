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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.modals;

import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasPatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class NewPatternPresenter {

    private final View view;

    private TranslationService translationService;

    private PatternPage<? extends HasPatternPage> patternPage;

    @Inject
    public NewPatternPresenter(final View view,
                               final TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show() {
        view.clear();

        if (!patternPage.isNegatedPatternEnabled()) {
            view.disableNegatedPattern();
        }

        view.show();
    }

    public void hide() {
        view.hide();
    }

    public void init(final PatternPage patternPage) {
        this.patternPage = patternPage;
    }

    public List<String> getFactTypes() {
        return Arrays.asList(oracle().getFactTypes());
    }

    void cancel() {
        view.hide();
    }

    void addPattern() {
        if (!isValid()) {
            return;
        }

        setEditingPattern();
        updatePatternPageView();
        hide();
    }

    boolean isBindingUnique(String binding) {
        final BRLRuleModel brlRuleModel = new BRLRuleModel(model());

        return !brlRuleModel.isVariableNameUsed(binding);
    }

    PatternWrapper pattern52() {
        return new PatternWrapper(factType(),
                                  factName(),
                                  isNegatePatternMatch());
    }

    private void updatePatternPageView() {
        patternPage.prepareView();
    }

    private boolean isValid() {
        if (!isNegatePatternMatch()) {
            if (factName().equals("")) {
                view.showError(translate(GuidedDecisionTableErraiConstants.NewPatternPresenter_PleaseEnterANameForFact));
                return false;
            } else if (factName().equals(factType())) {
                view.showError(translate(GuidedDecisionTableErraiConstants.NewPatternPresenter_PleaseEnterANameThatIsNotTheSameAsTheFactType));
                return false;
            } else if (!isBindingUnique(factName())) {
                view.showError(translate(GuidedDecisionTableErraiConstants.NewPatternPresenter_PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern));
                return false;
            }
        }

        return true;
    }

    private void setEditingPattern() {
        patternPage.setEditingPattern(pattern52());
    }

    private String factName() {
        return isNegatePatternMatch() ? "" : view.getBindingText();
    }

    private String factType() {
        return view.getSelectedFactType();
    }

    private boolean isNegatePatternMatch() {
        return view.isNegatePatternMatch();
    }

    private AsyncPackageDataModelOracle oracle() {
        return patternPage.presenter().getDataModelOracle();
    }

    private GuidedDecisionTable52 model() {
        return patternPage.presenter().getModel();
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }

    public interface View extends UberElement<NewPatternPresenter> {

        void show();

        void hide();

        void clear();

        boolean isNegatePatternMatch();

        String getSelectedFactType();

        String getBindingText();

        void showError(String errorMessage);

        void disableNegatedPattern();
    }
}
