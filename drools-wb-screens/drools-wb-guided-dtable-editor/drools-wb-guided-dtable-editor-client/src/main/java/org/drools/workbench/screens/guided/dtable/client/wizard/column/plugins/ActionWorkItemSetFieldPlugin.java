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

import com.google.gwt.user.client.Window;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
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
    public String getWorkItem() {
        return selectedWorkItemKey;
    }

    @Override
    public void setWorkItem(final String workItemKey) {
        final WorkItemParameter workItemParameter = getWorkItems().get(workItemKey);
        final PortableWorkDefinition workDefinition = workItemParameter.getWorkDefinition();
        final PortableParameterDefinition parameterDefinition = workItemParameter.getWorkParameterDefinition();

        selectedWorkItemKey = workItemKey;

        editingWrapper().setWorkItemName(workDefinition.getName());
        editingWrapper().setWorkItemResultParameterName(parameterDefinition.getName());
        editingWrapper().setParameterClassName(parameterDefinition.getClassName());

        fireChangeEvent(workItemPage);
    }

    Map<String, WorkItemParameter> getWorkItems() {
        return workItems;
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
        workItems.clear();

        actionWorkItems().forEach(actionCol52 -> {
            final PortableWorkDefinition workItemDefinition = ((ActionWorkItemCol52) actionCol52).getWorkItemDefinition();

            for (final PortableParameterDefinition parameterDefinition : workItemDefinition.getResults()) {
                if (acceptParameterType(parameterDefinition)) {
                    final String name = workItemDefinition.getName() + " - " + parameterDefinition.getName();
                    final String key = workItemDefinition.getDisplayName() + "" + parameterDefinition.getName();

                    biConsumer.accept(name,
                                      key);

                    workItems.put(key,
                                  new WorkItemParameter(workItemDefinition,
                                                        parameterDefinition));
                }
            }
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
        if (!isFactPatternValid()) {
            Window.alert(translate(GuidedDecisionTableErraiConstants.ActionWorkItemSetFieldPlugin_YouMustEnterAColumnFact));
            return false;
        }

        if (nil(editingWrapper().getFactField())) {
            Window.alert(translate(GuidedDecisionTableErraiConstants.ActionWorkItemSetFieldPlugin_YouMustEnterAColumnField));
            return false;
        }
        if (nil(editingWrapper().getHeader())) {
            Window.alert(translate(GuidedDecisionTableErraiConstants.ActionWorkItemSetFieldPlugin_YouMustEnterAColumnHeaderValueDescription));
            return false;
        }

        if (!unique(editingWrapper().getHeader())) {
            Window.alert(translate(GuidedDecisionTableErraiConstants.ActionWorkItemSetFieldPlugin_ThatColumnNameIsAlreadyInUsePleasePickAnother));
            return false;
        }

        // Pass new\modified column back for handling
        presenter.appendColumn(editingWrapper().getActionCol52());

        return true;
    }

    private boolean isFactPatternValid() {
        if (isNewFactPattern()) {
            return !nil(editingWrapper().getFactType());
        } else {
            return !nil(editingWrapper().getBoundName());
        }
    }

    boolean unique(String header) {
        for (ActionCol52 o : model().getActionCols()) {
            if (o.getHeader().equals(header)) {
                return false;
            }
        }
        return true;
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

        for (ActionCol52 actionCol52 : model().getActionCols()) {
            if (actionCol52 instanceof ActionInsertFactCol52) {
                patterns.add(new PatternWrapper((ActionInsertFactCol52) actionCol52));
            }
        }

        for (Pattern52 pattern52 : model().getPatterns()) {
            patterns.add(new PatternWrapper(pattern52));
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
        if (isNewFactPattern()) {
            editingWrapper = new ActionWorkItemInsertWrapper(this);
        } else {
            editingWrapper = new ActionWorkItemSetWrapper(this);
        }

        final String factType = patternWrapper().getFactType();

        editingWrapper().setFactField(selectedValue);
        editingWrapper().setFactType(factType);
        editingWrapper().setBoundName(patternWrapper().getBoundName());
        editingWrapper().setType(oracle().getFieldType(editingWrapper().getFactType(),
                                                       editingWrapper().getFactField()));

        fireChangeEvent(fieldPage);
    }

    private AsyncPackageDataModelOracle oracle() {
        return presenter.getDataModelOracle();
    }

    @Override
    public boolean showUpdateEngineWithChanges() {
        return !isNewFactPattern();
    }

    @Override
    public boolean showLogicallyInsert() {
        return isNewFactPattern();
    }

    boolean isNewFactPattern() {
        final BRLRuleModel validator = new BRLRuleModel(model());

        return !validator.isVariableNameUsed(patternWrapper().getBoundName());
    }

    PatternPage initializedPatternPage() {
        patternPage.disableEntryPoint();
        patternPage.disableNegatedPatterns();

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
