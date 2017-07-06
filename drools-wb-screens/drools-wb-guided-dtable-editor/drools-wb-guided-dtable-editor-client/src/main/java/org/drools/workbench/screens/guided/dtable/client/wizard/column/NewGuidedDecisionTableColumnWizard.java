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

package org.drools.workbench.screens.guided.dtable.client.wizard.column;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.SummaryPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.BaseDecisionTableColumnPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.AbstractWizard;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardView;
import org.uberfire.mvp.Command;

/**
 * Wizard for creating a Guided Decision Table
 */
@Dependent
public class NewGuidedDecisionTableColumnWizard extends AbstractWizard {

    private List<WizardPage> pages = new ArrayList<>();

    private Supplier<Boolean> finishCommand;

    private WizardView view;

    private SummaryPage summaryPage;

    private TranslationService translationService;

    private GuidedDecisionTableView.Presenter presenter;

    private Command onCloseCallback = () -> {
    };

    private String title;

    @Inject
    public NewGuidedDecisionTableColumnWizard(final WizardView view,
                                              final SummaryPage summaryPage,
                                              final TranslationService translationService) {
        this.view = view;
        this.summaryPage = summaryPage;
        this.translationService = translationService;
    }

    @Override
    public String getTitle() {
        final String defaultTitle = translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_AddNewColumn);

        return Optional.ofNullable(title).orElse(defaultTitle);
    }

    void setupTitle(final DecisionTableColumnPlugin plugin) {
        final String addNewColumn = translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_AddNewColumn);
        final String editColumn = translate(GuidedDecisionTableErraiConstants.NewGuidedDecisionTableColumnWizard_EditColumn);

        setTitle(plugin.isNewColumn() ? addNewColumn : editColumn);
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public List<WizardPage> getPages() {
        return this.pages;
    }

    public void setPages(final List<WizardPage> pages) {
        this.pages = pages;
    }

    @Override
    public Widget getPageWidget(final int pageNumber) {
        final WizardPage wizardPage = this.pages.get(pageNumber);
        final Widget widget = wizardPage.asWidget();

        wizardPage.prepareView();

        return widget;
    }

    @Override
    public int getPreferredHeight() {
        return 600;
    }

    @Override
    public int getPreferredWidth() {
        return 900;
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        //Assume complete
        callback.callback(true);

        for (final WizardPage page : this.pages) {
            page.isComplete(result -> {
                if (Boolean.FALSE.equals(result)) {
                    callback.callback(false);
                }
            });
        }
    }

    @Override
    public void start() {
        pages = new ArrayList<WizardPage>() {{
            add(summaryPage);
            addAll(pages);
        }};

        for (WizardPage page : pages) {
            ((BaseDecisionTableColumnPage) page).init(this);
        }

        parentStart();
    }

    public void start(final DecisionTableColumnPlugin plugin) {
        plugin.init(this);

        loadPlugin(plugin);

        parentStart();
    }

    void parentStart() {
        super.start();
    }

    private void loadPlugin(final DecisionTableColumnPlugin plugin) {
        setupTitle(plugin);
        loadPages(plugin);
        initPages(plugin);
    }

    void initPages(final DecisionTableColumnPlugin plugin) {
        for (final WizardPage page : pages) {
            final BaseDecisionTableColumnPage tableColumnPage = (BaseDecisionTableColumnPage) page;

            tableColumnPage.init(this);
            tableColumnPage.setPlugin(plugin);
        }
    }

    void loadPages(final DecisionTableColumnPlugin plugin) {
        getPages().clear();

        if (plugin.isNewColumn()) {
            getPages().add(summaryPage);
        }

        getPages().addAll(plugin.getPages());
    }

    @Override
    public void complete() {
        if (finishCommand.get()) {
            super.complete();
        }
    }

    @Override
    public void close() {
        onCloseCallback.execute();

        super.close();
    }

    private WizardView getView() {
        return view;
    }

    public void goTo(final int index) {
        getView().selectPage(index);
    }

    public void init(final GuidedDecisionTableView.Presenter presenter) {
        this.presenter = presenter;
    }

    public void setFinishCommand(final Supplier<Boolean> finishCommand) {
        this.finishCommand = finishCommand;
    }

    public void setOnCloseCallback(final Command onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }

    public GuidedDecisionTableView.Presenter getPresenter() {
        return presenter;
    }

    private String translate(final String key,
                             final Object... args) {
        return translationService.format(key,
                                         args);
    }
}
