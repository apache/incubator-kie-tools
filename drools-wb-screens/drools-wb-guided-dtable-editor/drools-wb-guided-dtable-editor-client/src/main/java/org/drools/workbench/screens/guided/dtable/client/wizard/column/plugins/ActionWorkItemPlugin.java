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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasAdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasWorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.WorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.AdditionalInfoPageInitializer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class ActionWorkItemPlugin extends BaseDecisionTableColumnPlugin implements HasWorkItemPage,
                                                                                   HasAdditionalInfoPage {

    private AdditionalInfoPage<ActionWorkItemPlugin> additionalInfoPage;

    private WorkItemPage workItemPage;

    private ActionWorkItemCol52 editingCol;

    @Inject
    public ActionWorkItemPlugin(final AdditionalInfoPage<ActionWorkItemPlugin> additionalInfoPage,
                                final WorkItemPage workItemPage,
                                final Event<WizardPageStatusChangeEvent> changeEvent,
                                final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.additionalInfoPage = additionalInfoPage;
        this.workItemPage = workItemPage;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.ActionWorkItemPlugin_ExecuteWorkItem);
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(workItemPage());
            add(additionalInfoPage());
        }};
    }

    @Override
    public void init(final NewGuidedDecisionTableColumnWizard wizard) {
        super.init(wizard);

        setupDefaultValues();
    }

    void setupDefaultValues() {
        if (isNewColumn()) {
            editingCol = newActionWorkItemCol52();
            return;
        }

        editingCol = clone(originalCol());

        fireChangeEvent(workItemPage);
        fireChangeEvent(additionalInfoPage);
    }

    ActionWorkItemCol52 newActionWorkItemCol52() {
        return new ActionWorkItemCol52();
    }

    ActionWorkItemCol52 clone(final ActionWorkItemCol52 column) {
        final ActionWorkItemCol52 clone = new ActionWorkItemCol52();

        clone.setHeader(column.getHeader());
        clone.setWorkItemDefinition(column.getWorkItemDefinition());
        clone.setHideColumn(column.isHideColumn());

        return clone;
    }

    @Override
    public String getWorkItem() {
        final boolean hasWorkItemDefinition = getWorkItemDefinition() != null;

        if (hasWorkItemDefinition) {
            return getWorkItemDefinition().getName();
        } else {
            return "";
        }
    }

    @Override
    public void setWorkItem(final String workItem) {
        if (!nil(workItem)) {
            editingCol.setWorkItemDefinition(findWorkItemDefinition(workItem));
        } else {
            editingCol.setWorkItemDefinition(null);
        }

        fireChangeEvent(workItemPage);
    }

    @Override
    public ActionWorkItemCol52 editingCol() {
        return editingCol;
    }

    @Override
    public String getHeader() {
        return editingCol().getHeader();
    }

    @Override
    public void setHeader(final String header) {
        editingCol().setHeader(header);

        fireChangeEvent(additionalInfoPage);
    }

    @Override
    public Set<String> getAlreadyUsedColumnHeaders() {
        return presenter
                .getModel()
                .getActionCols()
                .stream()
                .map(DTColumnConfig52::getHeader)
                .collect(Collectors.toSet());
    }

    @Override
    public Boolean isWorkItemSet() {
        return editingCol.getWorkItemDefinition() != null;
    }

    @Override
    public PortableWorkDefinition getWorkItemDefinition() {
        return editingCol().getWorkItemDefinition();
    }

    @Override
    public void forEachWorkItem(final BiConsumer<String, String> biConsumer) {
        getPresenter()
                .getWorkItemDefinitions()
                .forEach(workDefinition -> biConsumer.accept(workDefinition.getDisplayName(),
                                                             workDefinition.getName()));
    }

    @Override
    public void setWorkItemPageAsCompleted() {
        // empty
    }

    @Override
    public void setInsertLogical(final Boolean value) {
        // empty
    }

    @Override
    public void setUpdate(final Boolean value) {
        // empty
    }

    @Override
    public boolean showUpdateEngineWithChanges() {
        return false;
    }

    @Override
    public boolean showLogicallyInsert() {
        return false;
    }

    @Override
    public boolean isLogicallyInsert() {
        return false;
    }

    @Override
    public boolean isUpdateEngine() {
        return false;
    }

    PortableWorkDefinition findWorkItemDefinition(final String workItem) {
        final List<PortableWorkDefinition> workItemDefinitions = new ArrayList<>(presenter.getWorkItemDefinitions());

        return workItemDefinitions
                .stream()
                .filter(a -> a.getName().equals(workItem))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public Boolean generateColumn() {

        if (isNewColumn()) {
            presenter.appendColumn(editingCol());
        } else {
            presenter.updateColumn(originalCol(),
                                   editingCol());
        }

        return true;
    }

    private ActionWorkItemCol52 originalCol() {
        return (ActionWorkItemCol52) getOriginalColumnConfig52();
    }

    WizardPage workItemPage() {
        workItemPage.enableParameters();

        return workItemPage;
    }

    AdditionalInfoPage additionalInfoPage() {
        return AdditionalInfoPageInitializer.init(additionalInfoPage,
                                                  this);
    }

    @Override
    public Type getType() {
        return Type.ADVANCED;
    }
}
