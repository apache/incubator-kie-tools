/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.workbench.models.datamodel.rule.IPattern;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.util.InterpolationVariableCollector;
import org.drools.workbench.models.datamodel.rule.visitors.RuleModelVisitor;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLConditionColumn;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.RuleModelCloneVisitor;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoUpdatePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasAdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasRuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.AdditionalInfoPageInitializer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerConfiguration;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.rule.client.editor.plugin.RuleModellerActionPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat.LIMITED_ENTRY;

@Dependent
public class BRLConditionColumnPlugin extends BaseDecisionTableColumnPlugin implements HasRuleModellerPage,
                                                                                       HasAdditionalInfoPage,
                                                                                       TemplateVariablesChangedEvent.Handler {

    private RuleModellerPage ruleModellerPage;

    private AdditionalInfoPage additionalInfoPage;

    private BRLConditionColumn editingCol;

    private Boolean ruleModellerPageCompleted = Boolean.FALSE;

    private HandlerRegistration registration;

    private RuleModel ruleModel = null;

    @Inject
    public BRLConditionColumnPlugin(final RuleModellerPage ruleModellerPage,
                                    final AdditionalInfoPage additionalInfoPage,
                                    final Event<WizardPageStatusChangeEvent> changeEvent,
                                    final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.ruleModellerPage = ruleModellerPage;
        this.additionalInfoPage = additionalInfoPage;
    }

    @Override
    public void init(final NewGuidedDecisionTableColumnWizard wizard) {
        super.init(wizard);

        setupEditingCol();
        setupRuleModellerEvents();
    }

    @Override
    public void onClose() {
        super.onClose();

        teardownRuleModellerEvents();
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.BRLConditionColumnPlugin_AddConditionBRL);
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(ruleModellerPage);
            add(additionalInfoPage());
        }};
    }

    @Override
    public Boolean generateColumn() {
        getDefinedVariables(getRuleModel());

        editingCol().setDefinition(Arrays.asList(getRuleModel().lhs));

        if (isNewColumn()) {
            presenter.appendColumn(editingCol());
        } else {
            try {
                presenter.updateColumn(getOriginalColumn(),
                                       editingCol());
            } catch (VetoUpdatePatternInUseException veto) {
                wizard.showPatternInUseError();
                return false;
            } catch (VetoException veto) {
                wizard.showGenericVetoError();
                return false;
            }
        }

        return true;
    }

    ConditionCol52 getOriginalColumn() {
        return (ConditionCol52) getOriginalColumnConfig52();
    }

    @Override
    public Type getType() {
        return Type.ADVANCED;
    }

    boolean getDefinedVariables(RuleModel ruleModel) {
        Map<InterpolationVariable, Integer> ivs = new HashMap<>();
        RuleModelVisitor rmv = new RuleModelVisitor(ivs);
        rmv.visit(ruleModel);

        Map<InterpolationVariable, Integer> result = new InterpolationVariableCollector(ivs,
                                                                                        DataType.TYPE_STRING).getMap();

        //Update column and UI
        editingCol.setChildColumns(convertInterpolationVariables(result));

        return ivs.size() > 0;
    }

    private List<BRLConditionVariableColumn> convertInterpolationVariables(Map<InterpolationVariable, Integer> ivs) {

        //If there are no variables add a boolean column to specify whether the fragment should apply
        if (ivs.isEmpty()) {
            BRLConditionVariableColumn variable = new BRLConditionVariableColumn("",
                                                                                 DataType.TYPE_BOOLEAN);
            variable.setHeader(editingCol.getHeader());
            variable.setHideColumn(editingCol.isHideColumn());
            List<BRLConditionVariableColumn> variables = new ArrayList<BRLConditionVariableColumn>();
            variables.add(variable);
            return variables;
        }

        //Convert to columns for use in the Decision Table
        BRLConditionVariableColumn[] variables = new BRLConditionVariableColumn[ivs.size()];
        for (Map.Entry<InterpolationVariable, Integer> me : ivs.entrySet()) {
            InterpolationVariable iv = me.getKey();
            int index = me.getValue();
            BRLConditionVariableColumn variable = new BRLConditionVariableColumn(iv.getVarName(),
                                                                                 iv.getDataType(),
                                                                                 iv.getFactType(),
                                                                                 iv.getFactField(),
                                                                                 iv.getOperator());
            variable.setHeader(editingCol.getHeader());
            variable.setHideColumn(editingCol.isHideColumn());
            variables[index] = variable;
        }

        //Convert the array into a mutable list (Arrays.toList provides an immutable list)
        List<BRLConditionVariableColumn> variableList = new ArrayList<BRLConditionVariableColumn>();
        for (BRLConditionVariableColumn variable : variables) {
            variableList.add(variable);
        }
        return variableList;
    }

    @Override
    public BRLConditionColumn editingCol() {
        return editingCol;
    }

    @Override
    public String getHeader() {
        return editingCol().getHeader();
    }

    @Override
    public void setHeader(String header) {
        editingCol().setHeader(header);

        fireChangeEvent(additionalInfoPage);
    }

    @Override
    public Set<String> getAlreadyUsedColumnHeaders() {
        final List<CompositeColumn<? extends BaseColumn>> conditions = getModel().getConditions();
        final Set<String> columnNames = new HashSet<>();

        for (final CompositeColumn<?> condition : conditions) {
            final List<String> headers = condition
                    .getChildColumns()
                    .stream()
                    .map(BaseColumn::getHeader)
                    .collect(Collectors.toList());

            columnNames.addAll(headers);
        }

        return columnNames;
    }

    private GuidedDecisionTable52 getModel() {
        return getPresenter().getModel();
    }

    @Override
    public boolean isHideColumn() {
        return editingCol().isHideColumn();
    }

    @Override
    public void setHideColumn(boolean hideColumn) {
        editingCol().setHideColumn(hideColumn);
    }

    @Override
    public void setInsertLogical(Boolean value) {
        // empty
    }

    @Override
    public void setUpdate(Boolean value) {
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

    @Override
    public RuleModel getRuleModel() {
        ruleModel = Optional.ofNullable(ruleModel).orElse(newRuleModel());

        return ruleModel;
    }

    @Override
    public Collection<RuleModellerActionPlugin> getRuleModellerActionPlugins() {
        return Collections.emptyList();
    }

    private RuleModel newRuleModel() {
        final BRLRuleModel ruleModel = new BRLRuleModel(getModel());
        final List<IPattern> definition = editingCol.getDefinition();

        ruleModel.lhs = definition.toArray(new IPattern[definition.size()]);

        return ruleModel;
    }

    @Override
    public RuleModellerConfiguration getRuleModellerConfiguration() {
        return new RuleModellerConfiguration(false,
                                             true,
                                             true,
                                             true);
    }

    @Override
    public String getRuleModellerDescription() {
        return translate(GuidedDecisionTableErraiConstants.RuleModellerPage_InsertAConditionBRLFragment);
    }

    @Override
    public void setRuleModellerPageAsCompleted() {
        if (!isRuleModellerPageCompleted()) {
            setRuleModellerPageCompleted();

            fireChangeEvent(ruleModellerPage);
        }
    }

    void setupEditingCol() {
        if (isNewColumn()) {
            editingCol = newBRLCondition();
        } else {
            editingCol = clone(getOriginalColumnConfig52());
        }
    }

    BRLConditionColumn clone(final DTColumnConfig52 column) {
        final BRLConditionColumn brlConditionColumn = (BRLConditionColumn) column;
        final BRLConditionColumn clone;

        if (tableFormat() == LIMITED_ENTRY) {
            clone = new LimitedEntryBRLConditionColumn();
        } else {
            clone = new BRLConditionColumn();
            clone.setChildColumns(cloneVariables(brlConditionColumn.getChildColumns()));
        }

        clone.setHeader(column.getHeader());
        clone.setHideColumn(column.isHideColumn());
        clone.setDefinition(cloneDefinition(brlConditionColumn.getDefinition()));
        clone.setOperator(brlConditionColumn.getOperator());

        return clone;
    }

    List<BRLConditionVariableColumn> cloneVariables(List<BRLConditionVariableColumn> variables) {
        return variables
                .stream()
                .map(this::cloneVariable)
                .collect(Collectors.toList());
    }

    BRLConditionVariableColumn cloneVariable(BRLConditionVariableColumn variable) {
        final BRLConditionVariableColumn clone = new BRLConditionVariableColumn(variable.getVarName(),
                                                                                variable.getFieldType(),
                                                                                variable.getFactType(),
                                                                                variable.getFactField());

        clone.setHeader(variable.getHeader());
        clone.setHideColumn(variable.isHideColumn());
        clone.setWidth(variable.getWidth());
        clone.setOperator(variable.getOperator());

        return clone;
    }

    private List<IPattern> cloneDefinition(final List<IPattern> definition) {
        final RuleModelCloneVisitor visitor = new RuleModelCloneVisitor();
        final RuleModel rm = new RuleModel();

        definition.forEach(rm::addLhsItem);

        final List<IPattern> clone = new ArrayList<>();

        Collections.addAll(clone,
                           visitor.visitRuleModel(rm).lhs);
        return clone;
    }

    void setupRuleModellerEvents() {
        registration = presenter.getEventBus().addHandler(TemplateVariablesChangedEvent.TYPE,
                                                          this);
    }

    void teardownRuleModellerEvents() {
        registration.removeHandler();
    }

    void setRuleModellerPageCompleted() {
        this.ruleModellerPageCompleted = Boolean.TRUE;
    }

    @Override
    public Boolean isRuleModellerPageCompleted() {
        return ruleModellerPageCompleted;
    }

    private BRLConditionColumn newBRLCondition() {
        switch (tableFormat()) {
            case EXTENDED_ENTRY:
                return new BRLConditionColumn();
            case LIMITED_ENTRY:
                return new LimitedEntryBRLConditionColumn();
            default:
                throw new UnsupportedOperationException("Unsupported table format: " + tableFormat());
        }
    }

    @Override
    public TableFormat tableFormat() {
        return getModel().getTableFormat();
    }

    private AdditionalInfoPage additionalInfoPage() {
        return AdditionalInfoPageInitializer.init(additionalInfoPage,
                                                  this);
    }

    @Override
    public void onTemplateVariablesChanged(TemplateVariablesChangedEvent event) {
        if (event.getSource() == getRuleModel()) {
            getDefinedVariables(event.getModel());
        }
    }
}
