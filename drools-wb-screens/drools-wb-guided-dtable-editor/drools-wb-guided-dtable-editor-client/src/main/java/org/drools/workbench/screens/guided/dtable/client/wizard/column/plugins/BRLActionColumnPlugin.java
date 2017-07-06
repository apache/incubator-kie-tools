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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.shared.HandlerRegistration;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.rule.IAction;
import org.drools.workbench.models.datamodel.rule.InterpolationVariable;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.visitors.RuleModelVisitor;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DTColumnConfig52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryBRLActionColumn;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasAdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasRuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.RuleModellerPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.AdditionalInfoPageInitializer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModellerConfiguration;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

@Dependent
public class BRLActionColumnPlugin extends BaseDecisionTableColumnPlugin implements HasRuleModellerPage,
                                                                                    HasAdditionalInfoPage,
                                                                                    TemplateVariablesChangedEvent.Handler {

    private RuleModellerPage ruleModellerPage;

    private AdditionalInfoPage<BRLActionColumnPlugin> additionalInfoPage;

    private BRLActionColumn editingCol;

    private Boolean ruleModellerPageCompleted = Boolean.FALSE;

    private HandlerRegistration registration;

    private RuleModel ruleModel = null;

    @Inject
    public BRLActionColumnPlugin(final RuleModellerPage ruleModellerPage,
                                 final AdditionalInfoPage<BRLActionColumnPlugin> additionalInfoPage,
                                 final Event<WizardPageStatusChangeEvent> changeEvent,
                                 final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.ruleModellerPage = ruleModellerPage;
        this.additionalInfoPage = additionalInfoPage;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.BRLActionColumnPlugin_AddActionBRL);
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(ruleModellerPage);
            add(additionalInfoPage());
        }};
    }

    @Override
    public void init(final NewGuidedDecisionTableColumnWizard wizard) {
        super.init(wizard);

        setupEditingCol();
        setupRuleModellerEvents();
    }

    void setupRuleModellerEvents() {
        registration = getPresenter().getEventBus().addHandler(TemplateVariablesChangedEvent.TYPE,
                                                               this);
    }

    void setupEditingCol() {
        editingCol = newBRLActionColumn();

        if (!isNewColumn()) {
            editingCol().setHeader(originalCol().getHeader());
            editingCol().setDefinition(originalCol().getDefinition());
            editingCol().setChildColumns(originalCol().getChildColumns());
            editingCol().setHideColumn(originalCol().isHideColumn());

            fireChangeEvent(additionalInfoPage);
        }
    }

    void teardownRuleModellerEvents() {
        registration.removeHandler();
    }

    @Override
    public void onClose() {
        super.onClose();

        teardownRuleModellerEvents();
    }

    @Override
    public Boolean generateColumn() {
        getDefinedVariables(getRuleModel());
        editingCol().setDefinition(Arrays.asList(getRuleModel().rhs));

        if (isNewColumn()) {
            presenter.appendColumn(editingCol());
        } else {
            presenter.updateColumn(originalCol(),
                                   editingCol());
        }

        return true;
    }

    BRLActionColumn originalCol() {
        return (BRLActionColumn) getOriginalColumnConfig52();
    }

    boolean getDefinedVariables(RuleModel ruleModel) {
        Map<InterpolationVariable, Integer> ivs = new HashMap<InterpolationVariable, Integer>();
        RuleModelVisitor rmv = new RuleModelVisitor(ivs);
        rmv.visit(ruleModel);

        //Update column and UI
        editingCol.setChildColumns(convertInterpolationVariables(ivs));

        return ivs.size() > 0;
    }

    private List<BRLActionVariableColumn> convertInterpolationVariables(Map<InterpolationVariable, Integer> ivs) {

        //If there are no variables add a boolean column to specify whether the fragment should apply
        if (ivs.isEmpty()) {
            BRLActionVariableColumn variable = new BRLActionVariableColumn("",
                                                                           DataType.TYPE_BOOLEAN);
            variable.setHeader(editingCol.getHeader());
            variable.setHideColumn(editingCol.isHideColumn());
            List<BRLActionVariableColumn> variables = new ArrayList<BRLActionVariableColumn>();
            variables.add(variable);
            return variables;
        }

        //Convert to columns for use in the Decision Table
        BRLActionVariableColumn[] variables = new BRLActionVariableColumn[ivs.size()];
        for (Map.Entry<InterpolationVariable, Integer> me : ivs.entrySet()) {
            InterpolationVariable iv = me.getKey();
            int index = me.getValue();
            BRLActionVariableColumn variable = new BRLActionVariableColumn(iv.getVarName(),
                                                                           iv.getDataType(),
                                                                           iv.getFactType(),
                                                                           iv.getFactField());
            variable.setHeader(editingCol.getHeader());
            variable.setHideColumn(editingCol.isHideColumn());
            variables[index] = variable;
        }

        //Convert the array into a mutable list (Arrays.toList provides an immutable list)
        List<BRLActionVariableColumn> variableList = new ArrayList<BRLActionVariableColumn>();
        for (BRLActionVariableColumn variable : variables) {
            variableList.add(variable);
        }
        return variableList;
    }

    @Override
    public BRLActionColumn editingCol() {
        return editingCol;
    }

    @Override
    public String getHeader() {
        return editingCol.getHeader();
    }

    @Override
    public void setHeader(final String header) {
        editingCol.setHeader(header);

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

    @Override
    public RuleModel getRuleModel() {
        ruleModel = Optional.ofNullable(ruleModel).orElse(newRuleModel());

        return ruleModel;
    }

    private RuleModel newRuleModel() {
        final BRLRuleModel ruleModel = new BRLRuleModel(presenter.getModel());
        final List<IAction> definition = editingCol.getDefinition();

        ruleModel.rhs = definition.toArray(new IAction[definition.size()]);

        return ruleModel;
    }

    @Override
    public RuleModellerConfiguration getRuleModellerConfiguration() {
        return new RuleModellerConfiguration(true,
                                             false,
                                             true,
                                             true);
    }

    @Override
    public String getRuleModellerDescription() {
        return translate(GuidedDecisionTableErraiConstants.RuleModellerPage_InsertAnActionBRLFragment);
    }

    @Override
    public void setRuleModellerPageAsCompleted() {
        if (!isRuleModellerPageCompleted()) {
            setRuleModellerPageCompleted();

            fireChangeEvent(ruleModellerPage);
        }
    }

    void setRuleModellerPageCompleted() {
        this.ruleModellerPageCompleted = Boolean.TRUE;
    }

    @Override
    public Boolean isRuleModellerPageCompleted() {
        return ruleModellerPageCompleted;
    }

    private AdditionalInfoPage additionalInfoPage() {
        return AdditionalInfoPageInitializer.init(additionalInfoPage,
                                                  this);
    }

    BRLActionColumn newBRLActionColumn() {
        switch (tableFormat()) {
            case EXTENDED_ENTRY:
                return new BRLActionColumn();
            case LIMITED_ENTRY:
                return new LimitedEntryBRLActionColumn();
            default:
                throw new UnsupportedOperationException("Unsupported table format: " + tableFormat());
        }
    }

    @Override
    public GuidedDecisionTable52.TableFormat tableFormat() {
        return presenter.getModel().getTableFormat();
    }

    @Override
    public void onTemplateVariablesChanged(TemplateVariablesChangedEvent event) {
        if (event.getSource() == getRuleModel()) {
            getDefinedVariables(event.getModel());
        }
    }

    @Override
    public Type getType() {
        return Type.ADVANCED;
    }
}
