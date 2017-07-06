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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasAdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasFieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasPatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasWorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.WorkItemPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemInsertWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemSetWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ActionWorkItemWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.AdditionalInfoPageInitializer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class ActionWorkItemSetFieldPlugin extends BaseDecisionTableColumnPlugin implements HasFieldPage,
                                                                                           HasPatternPage,
                                                                                           HasWorkItemPage,
                                                                                           HasAdditionalInfoPage {

    private Map<String, WorkItemParameter> workItems = new HashMap<>();

    private String selectedWorkItemKey;

    private ActionWorkItemWrapper editingWrapper;

    private PatternPage patternPage;

    private FieldPage fieldPage;

    private AdditionalInfoPage additionalInfoPage;

    private WorkItemPage workItemPage;

    private PatternWrapper patternWrapper;

    private Boolean workItemPageCompleted = Boolean.FALSE;

    @Inject
    public ActionWorkItemSetFieldPlugin(final PatternPage patternPage,
                                        final FieldPage fieldPage,
                                        final AdditionalInfoPage additionalInfoPage,
                                        final WorkItemPage workItemPage,
                                        final Event<WizardPageStatusChangeEvent> changeEvent,
                                        final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.patternPage = patternPage;
        this.fieldPage = fieldPage;
        this.additionalInfoPage = additionalInfoPage;
        this.workItemPage = workItemPage;
    }

    @Override
    public void init(final NewGuidedDecisionTableColumnWizard wizard) {
        super.init(wizard);

        setupValues();
    }

    void setupValues() {
        if (!isNewColumn()) {
            editingWrapper = newActionWorkItemWrapper(getOriginalColumnConfig52());
            patternWrapper = newPatternWrapper(editingWrapper());

            setupWorkItems(editingWrapper());

            fireChangeEvent(patternPage);
            fireChangeEvent(fieldPage);
            fireChangeEvent(additionalInfoPage);
        }
    }

    PatternWrapper newPatternWrapper(final ActionWorkItemWrapper actionWrapper) {
        final String boundName = actionWrapper.getBoundName();
        final String factType = actionWrapper.getFactType();
        final PatternWrapper defaultWrapper = new PatternWrapper(factType,
                                                                 boundName,
                                                                 false);

        return getPatterns()
                .stream()
                .filter(wrapper -> wrapper.getBoundName().equals(boundName))
                .findFirst()
                .orElse(defaultWrapper);
    }

    ActionWorkItemWrapper newActionWorkItemWrapper(final DTColumnConfig52 column) {
        if (column instanceof ActionWorkItemInsertFactCol52) {
            return new ActionWorkItemInsertWrapper(this,
                                                   (ActionWorkItemInsertFactCol52) column);
        }

        if (column instanceof ActionWorkItemSetFieldCol52) {
            return new ActionWorkItemSetWrapper(this,
                                                (ActionWorkItemSetFieldCol52) column);
        }

        throw new UnsupportedOperationException("Unsupported column type: " + column.getClass().getSimpleName());
    }

    @Override
    public String getWorkItem() {
        return selectedWorkItemKey;
    }

    @Override
    public void setWorkItem(final String workItemKey) {
        setWorkItem("",
                    "",
                    "");

        getWorkItems()
                .forEach((key, workItemParameter) -> {
                    if (trim(key).equals(trim(workItemKey))) {
                        final PortableWorkDefinition workDefinition = workItemParameter.getWorkDefinition();
                        final PortableParameterDefinition parameterDefinition = workItemParameter.getWorkParameterDefinition();

                        selectedWorkItemKey = trim(key);

                        setWorkItem(workDefinition.getName(),
                                    parameterDefinition.getName(),
                                    parameterDefinition.getClassName());

                        fireChangeEvent(workItemPage);
                    }
                });
    }

    private void setWorkItem(final String workItemName,
                             final String workItemResultParameterName,
                             final String parameterClassName) {
        editingWrapper().setWorkItemName(workItemName);
        editingWrapper().setWorkItemResultParameterName(workItemResultParameterName);
        editingWrapper().setParameterClassName(parameterClassName);
    }

    void setupWorkItems() {
        actionWorkItems()
                .forEach(actionCol52 -> {
                    final PortableWorkDefinition workItemDefinition = ((ActionWorkItemCol52) actionCol52).getWorkItemDefinition();

                    workItemDefinition
                            .getResults()
                            .stream()
                            .filter(this::acceptParameterType)
                            .forEach(parameterDefinition -> {
                                final String key = trim((workItemDefinition.getName() + "" + parameterDefinition.getName()));

                                workItems.put(key,
                                              new WorkItemParameter(workItemDefinition,
                                                                    parameterDefinition));
                            });
                });
    }

    private String trim(final String workItemKey) {
        return workItemKey.replaceAll("\\s",
                                      "");
    }

    private void setupWorkItems(final ActionWorkItemWrapper wrapper) {
        final String workItemKey = wrapper.getWorkItemName() + wrapper.getWorkItemResultParameterName();

        setupWorkItems();
        setWorkItem(workItemKey);
        setWorkItemPageAsCompleted();
    }

    private boolean acceptParameterType(final PortableParameterDefinition ppd) {
        final AsyncPackageDataModelOracle oracle = presenter.getDataModelOracle();

        if (nil(editingWrapper().getFactField())) {
            return false;
        }
        if (nil(ppd.getClassName())) {
            return false;
        }

        final String fieldClassName = oracle.getFieldClassName(patternWrapper().getFactType(),
                                                               editingWrapper().getFactField());
        return fieldClassName.equals(ppd.getClassName());
    }

    Map<String, WorkItemParameter> getWorkItems() {
        return workItems;
    }

    @Override
    public ActionCol52 editingCol() {
        return editingWrapper().getActionCol52();
    }

    @Override
    public Boolean isWorkItemSet() {
        return isWorkItemPageCompleted();
    }

    @Override
    public PortableWorkDefinition getWorkItemDefinition() {
        return null;
    }

    @Override
    public void forEachWorkItem(BiConsumer<String, String> biConsumer) {
        setupWorkItems();

        workItems.forEach((key, workItemParameter) -> {
            final PortableWorkDefinition workItemDefinition = workItemParameter.getWorkDefinition();
            final PortableParameterDefinition parameterDefinition = workItemParameter.getWorkParameterDefinition();
            final String name = workItemDefinition.getName() + " - " + parameterDefinition.getName();

            biConsumer.accept(name,
                              key);
        });
    }

    @Override
    public void setWorkItemPageAsCompleted() {
        if (!isWorkItemPageCompleted()) {
            setWorkItemPageCompleted();

            fireChangeEvent(workItemPage);
        }
    }

    Boolean isWorkItemPageCompleted() {
        return workItemPageCompleted;
    }

    void setWorkItemPageCompleted() {
        this.workItemPageCompleted = Boolean.TRUE;
    }

    private List<ActionCol52> actionWorkItems() {
        return model()
                .getActionCols()
                .stream()
                .filter(actionCol52 -> actionCol52 instanceof ActionWorkItemCol52)
                .collect(Collectors.toList());
    }

    private GuidedDecisionTable52 model() {
        return presenter.getModel();
    }

    ActionWorkItemWrapper editingWrapper() {
        return Optional.ofNullable(editingWrapper).orElse(getEmptyColumn());
    }

    private ActionWorkItemWrapper getEmptyColumn() {
        return ActionWorkItemWrapper.EMPTY_COLUMN;
    }

    @Override
    public String getHeader() {
        return editingWrapper().getHeader();
    }

    @Override
    public void setHeader(final String header) {
        editingWrapper().setHeader(header);

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
    public void setInsertLogical(final Boolean isInsertLogical) {
        editingWrapper().setInsertLogical(isInsertLogical);
    }

    @Override
    public void setUpdate(final Boolean isUpdate) {
        editingWrapper().setUpdate(isUpdate);
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.ActionWorkItemSetFieldPlugin_SetValue);
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(initializedPatternPage());
            add(fieldPage);
            add(workItemPage);
            add(initializedAdditionalInfoPage());
        }};
    }

    @Override
    public Boolean generateColumn() {
        final ActionCol52 actionCol52 = editingWrapper().getActionCol52();

        if (isNewColumn()) {
            presenter.appendColumn(actionCol52);
        } else {
            presenter.updateColumn(originalCol(),
                                   actionCol52);
        }

        return true;
    }

    private ActionCol52 originalCol() {
        return (ActionCol52) getOriginalColumnConfig52();
    }

    @Override
    public PatternWrapper patternWrapper() {
        return Optional.ofNullable(patternWrapper).orElse(new PatternWrapper());
    }

    @Override
    public void setEditingPattern(final PatternWrapper patternWrapper) {
        this.patternWrapper = patternWrapper;

        editingWrapper().setFactField(null);
        editingWrapper().setFactType(null);
        editingWrapper().setBoundName(null);
        editingWrapper().setType(null);

        fireChangeEvent(patternPage);
        fireChangeEvent(fieldPage);
        fireChangeEvent(additionalInfoPage);
    }

    @Override
    public String getEntryPointName() {
        return "";
    }

    @Override
    public void setEntryPointName(final String entryPointName) {
        // empty
    }

    @Override
    public List<PatternWrapper> getPatterns() {
        final Set<PatternWrapper> patterns = new HashSet<>();

        if (isNewColumn() || !isNewFactPattern()) {
            for (Pattern52 pattern52 : presenter.getModel().getPatterns()) {
                patterns.add(new PatternWrapper(pattern52));
            }
        }

        if (isNewColumn() || isNewFactPattern()) {
            for (Object o : presenter.getModel().getActionCols()) {
                ActionCol52 col = (ActionCol52) o;
                if (col instanceof ActionWorkItemInsertFactCol52) {
                    patterns.add(new PatternWrapper((ActionWorkItemInsertFactCol52) col));
                }
            }
        }

        return new ArrayList<>(patterns);
    }

    @Override
    public int constraintValue() {
        return BaseSingleFieldConstraint.TYPE_UNDEFINED;
    }

    @Override
    public FieldAccessorsAndMutators getAccessor() {
        return FieldAccessorsAndMutators.ACCESSOR;
    }

    @Override
    public boolean filterEnumFields() {
        return false;
    }

    @Override
    public String getFactField() {
        return editingWrapper().getFactField();
    }

    @Override
    public void setFactField(final String selectedValue) {
        if (isNewColumn()) {
            editingWrapper = newActionWrapper();
        }

        final String factType = patternWrapper().getFactType();

        editingWrapper().setFactField(selectedValue);
        editingWrapper().setFactType(factType);
        editingWrapper().setBoundName(patternWrapper().getBoundName());
        editingWrapper().setType(oracle().getFieldType(editingWrapper().getFactType(),
                                                       editingWrapper().getFactField()));

        fireChangeEvent(fieldPage);
    }

    private ActionWorkItemWrapper newActionWrapper() {
        if (isNewFactPattern()) {
            return new ActionWorkItemInsertWrapper(this);
        } else {
            return new ActionWorkItemSetWrapper(this);
        }
    }

    private AsyncPackageDataModelOracle oracle() {
        return presenter.getDataModelOracle();
    }

    @Override
    public boolean showUpdateEngineWithChanges() {
        return editingWrapper() instanceof ActionWorkItemSetWrapper;
    }

    @Override
    public boolean showLogicallyInsert() {
        return editingWrapper() instanceof ActionWorkItemInsertWrapper;
    }

    @Override
    public boolean isLogicallyInsert() {
        return editingWrapper.isInsertLogical();
    }

    @Override
    public boolean isUpdateEngine() {
        return editingWrapper.isUpdateEngine();
    }

    boolean isNewFactPattern() {
        return !presenter
                .getModel()
                .getPatterns()
                .stream()
                .anyMatch(p -> p.getBoundName().equals(patternWrapper().getBoundName()));
    }

    PatternPage initializedPatternPage() {
        patternPage.disableEntryPoint();
        patternPage.disableNegatedPatterns();

        if (getOriginalColumnConfig52() instanceof ActionWorkItemSetFieldCol52) {
            patternPage.disablePatternCreation();
        }

        return patternPage;
    }

    AdditionalInfoPage initializedAdditionalInfoPage() {
        return AdditionalInfoPageInitializer.init(additionalInfoPage,
                                                  this);
    }

    @Override
    public Type getType() {
        return Type.ADVANCED;
    }

    static class WorkItemParameter {

        private PortableWorkDefinition workDefinition;
        private PortableParameterDefinition workParameterDefinition;

        WorkItemParameter(final PortableWorkDefinition workDefinition,
                          final PortableParameterDefinition workParameterDefinition) {
            this.workDefinition = workDefinition;
            this.workParameterDefinition = workParameterDefinition;
        }

        PortableWorkDefinition getWorkDefinition() {
            return workDefinition;
        }

        PortableParameterDefinition getWorkParameterDefinition() {
            return workParameterDefinition;
        }
    }
}
