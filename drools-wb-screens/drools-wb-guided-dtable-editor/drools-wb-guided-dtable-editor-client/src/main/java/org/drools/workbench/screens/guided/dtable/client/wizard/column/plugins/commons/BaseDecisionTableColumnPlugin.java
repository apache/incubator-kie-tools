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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons;

import java.util.Optional;
import javax.enterprise.event.Event;

import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

/**
 * Base Plugin for the column wizard (Guided Decision Table Editor).
 * It has a 'DecisionTableColumnPlugin'  base implementation.
 */
public abstract class BaseDecisionTableColumnPlugin implements DecisionTableColumnPlugin {

    protected NewGuidedDecisionTableColumnWizard wizard;

    protected GuidedDecisionTableView.Presenter presenter;

    private Event<WizardPageStatusChangeEvent> changeEvent;

    private TranslationService translationService;

    private DTColumnConfig52 originalColumnConfig52;

    private Pattern52 originalPattern52;

    protected BaseDecisionTableColumnPlugin(final Event<WizardPageStatusChangeEvent> changeEvent,
                                            final TranslationService translationService) {
        this.changeEvent = changeEvent;
        this.translationService = translationService;
    }

    @Override
    public void init(final NewGuidedDecisionTableColumnWizard wizard) {
        this.wizard = wizard;
        this.presenter = wizard.getPresenter();

        setupCommands();
    }

    @Override
    public String getIdentifier() {
        return getClass().getSimpleName();
    }

    @Override
    public Pattern52 getOriginalPattern52() {
        return originalPattern52;
    }

    @Override
    public void setOriginalPattern52(final Pattern52 originalPattern52) {
        this.originalPattern52 = originalPattern52;
    }

    @Override
    public DTColumnConfig52 getOriginalColumnConfig52() {
        return originalColumnConfig52;
    }

    @Override
    public void setOriginalColumnConfig52(final DTColumnConfig52 originalColumnConfig52) {
        this.originalColumnConfig52 = originalColumnConfig52;
    }

    public void onClose() {
        // do nothing
    }

    public void fireChangeEvent(final WizardPage wizardPage) {
        changeEvent.fire(new WizardPageStatusChangeEvent(wizardPage));
    }

    protected String translate(final String key,
                               final Object... args) {
        return translationService.format(key,
                                         args);
    }

    private void setupFinishCommand() {
        wizard.setFinishCommand(this::generateColumn);
    }

    private void setupCommands() {
        setupFinishCommand();
        setupCloseCommand();
    }

    private void setupCloseCommand() {
        wizard.setOnCloseCallback(this::onClose);
    }

    public GuidedDecisionTableView.Presenter getPresenter() {
        return presenter;
    }

    public Boolean isNewColumn() {
        return !Optional.ofNullable(originalColumnConfig52).isPresent();
    }
}
