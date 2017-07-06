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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasPatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.modals.HasList;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.modals.NewPatternPresenter;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberElement;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class PatternPage<T extends HasPatternPage & DecisionTableColumnPlugin> extends BaseDecisionTableColumnPage<T> {

    private View view;

    private NewPatternPresenter newPatternPresenter;

    private boolean negatedPatternEnabled = true;

    private boolean patternCreationEnabled = true;

    @Inject
    public PatternPage(final View view,
                       final NewPatternPresenter newPatternPresenter,
                       final TranslationService translationService) {
        super(translationService);

        this.view = view;
        this.newPatternPresenter = newPatternPresenter;
    }

    @Override
    public void initialise() {
        super.initialise();

        newPatternPresenter.init(this);
    }

    @Override
    protected UberElement<?> getView() {
        return view;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.PatternPage_Pattern);
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        boolean isPatternSet = isPatternSet();
        if(!isPatternSet) {
            view.showPatternWarning();
        } else {
            view.hidePatternWarning();
        }
        callback.callback(isPatternSet);
    }

    private boolean isPatternSet() {
        return !nil(currentPattern().getFactType());
    }

    @Override
    public void prepareView() {
        view.init(this);

        setupPattern();
        setupEntryPoint();
    }

    private void setupEntryPoint() {
        view.setupEntryPointName(getEntryPointName());
    }

    void setupPattern() {
        view.clearPatternList();

        setUpPatternList();

        if (!isPatternCreationEnabled()) {
            view.disablePatternCreation();
        }

        view.selectPattern(currentPatternValue());
        view.hidePatternListWhenItIsEmpty();
    }

    private void setUpPatternList() {
        forEachPattern((name, key) -> view.addItem(name,
                                                   key));
    }

    public GuidedDecisionTableView.Presenter presenter() {
        return presenter;
    }

    public void setEditingPattern(final PatternWrapper wrapper) {
        wrapper.setEntryPointName(view.getEntryPointName());

        plugin().setEditingPattern(wrapper);
    }

    public void disableEntryPoint() {
        view.disableEntryPoint();
    }

    public boolean isNegatedPatternEnabled() {
        return negatedPatternEnabled;
    }

    public void disableNegatedPatterns() {
        negatedPatternEnabled = false;
    }

    public boolean isPatternCreationEnabled() {
        return patternCreationEnabled;
    }

    public void disablePatternCreation() {
        patternCreationEnabled = false;
    }

    void setSelectedEditingPattern() {
        final String selectedValue = view.getSelectedValue();
        final PatternWrapper patternWrapper = findPatternWrapperByKey(selectedValue);

        setEditingPattern(patternWrapper);
    }

    void forEachPattern(final BiConsumer<String, String> biConsumer) {
        final Set<String> addedBounds = new HashSet<>();

        getPatterns()
                .forEach(pattern -> {
                    if (!addedBounds.contains(pattern.key())) {
                        biConsumer.accept(pattern.name(),
                                          pattern.key());

                        addedBounds.add(pattern.key());
                    }
                });
    }

    void showNewPatternModal() {
        newPatternPresenter.show();
    }

    String currentPatternName() {
        return currentPattern().name();
    }

    String currentPatternValue() {
        return currentPattern().key();
    }

    List<PatternWrapper> getPatterns() {
        final List<PatternWrapper> patterns = plugin().getPatterns();

        if (isPatternSet()) {
            patterns.add(currentPattern());
        }

        if (isNegatedPatternEnabled()) {
            return patterns;
        }

        return patterns
                .stream()
                .filter(pattern -> !pattern.isNegated())
                .collect(Collectors.toList());
    }

    private PatternWrapper currentPattern() {
        return Optional.ofNullable(plugin().patternWrapper()).orElse(new PatternWrapper());
    }

    String getEntryPointName() {
        return plugin().getEntryPointName();
    }

    void setEntryPoint() {
        plugin().setEntryPointName(view.getEntryPointName());
    }

    PatternWrapper findPatternWrapperByKey(final String key) {
        return getPatterns()
                .stream()
                .filter(p -> p.key().equals(key))
                .findAny()
                .orElse(PatternWrapper.DEFAULT);
    }

    public interface View extends HasList,
                                  UberElement<PatternPage> {

        void setupEntryPointName(final String entryPointName);

        String getSelectedValue();

        String getEntryPointName();

        void disableEntryPoint();

        void clearPatternList();

        void hidePatternListWhenItIsEmpty();

        void selectPattern(String currentPatternValue);

        void showPatternWarning();

        void hidePatternWarning();

        void disablePatternCreation();
    }
}
