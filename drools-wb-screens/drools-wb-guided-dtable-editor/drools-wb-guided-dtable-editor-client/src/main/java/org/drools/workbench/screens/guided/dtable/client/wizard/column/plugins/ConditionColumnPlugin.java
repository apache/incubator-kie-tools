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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.models.guided.dtable.shared.model.adaptors.FactPatternPattern52Adaptor;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoUpdatePatternInUseException;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.NewGuidedDecisionTableColumnWizard;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasAdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasFieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasPatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.commons.HasValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AdditionalInfoPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.CalculationTypePage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.FieldPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.OperatorPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.PatternPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.ValueOptionsPage;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.AdditionalInfoPageInitializer;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.BaseDecisionTableColumnPlugin;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.DefaultWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.LimitedWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.PatternWrapper;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.commons.ValueOptionsPageInitializer;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.FieldAccessorsAndMutators;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY;
import static org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat.LIMITED_ENTRY;
import static org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTableColumnViewUtils.nil;

@Dependent
public class ConditionColumnPlugin extends BaseDecisionTableColumnPlugin implements HasFieldPage,
                                                                                    HasPatternPage,
                                                                                    HasValueOptionsPage,
                                                                                    HasAdditionalInfoPage {

    private PatternPage<ConditionColumnPlugin> patternPage;

    private CalculationTypePage calculationTypePage;

    private FieldPage<ConditionColumnPlugin> fieldPage;

    private OperatorPage operatorPage;

    private AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage;

    private ValueOptionsPage<ConditionColumnPlugin> valueOptionsPage;

    private PatternWrapper patternWrapper;

    private ConditionCol52 editingCol;

    private int constraintValue;

    private Boolean valueOptionsPageCompleted;

    private Pattern52 editingPattern;

    @Inject
    public ConditionColumnPlugin(final PatternPage<ConditionColumnPlugin> patternPage,
                                 final CalculationTypePage calculationTypePage,
                                 final FieldPage<ConditionColumnPlugin> fieldPage,
                                 final OperatorPage operatorPage,
                                 final ValueOptionsPage<ConditionColumnPlugin> valueOptionsPage,
                                 final AdditionalInfoPage<ConditionColumnPlugin> additionalInfoPage,
                                 final Event<WizardPageStatusChangeEvent> changeEvent,
                                 final TranslationService translationService) {
        super(changeEvent,
              translationService);

        this.patternPage = patternPage;
        this.calculationTypePage = calculationTypePage;
        this.fieldPage = fieldPage;
        this.operatorPage = operatorPage;
        this.valueOptionsPage = valueOptionsPage;
        this.additionalInfoPage = additionalInfoPage;
    }

    @Override
    public String getTitle() {
        return translate(GuidedDecisionTableErraiConstants.ConditionColumnPlugin_AddConditionColumn);
    }

    @Override
    public void init(NewGuidedDecisionTableColumnWizard wizard) {
        super.init(wizard);

        setupDefaultValues();
        setupPatternWrapper();
    }

    void setupPatternWrapper() {
        patternWrapper = new PatternWrapper(getEditingPattern().getFactType(),
                                            getEditingPattern().getBoundName(),
                                            getEditingPattern().getEntryPointName(),
                                            getEditingPattern().isNegated());
    }

    @Override
    public List<WizardPage> getPages() {
        return new ArrayList<WizardPage>() {{
            add(patternPage);

            if (isExtendedEntryTable()) {
                add(calculationTypePage);
            }

            add(fieldPage);
            add(operatorPage);
            add(initializedValueOptionsPage());
            add(initializedAdditionalInfoPage());
        }};
    }

    @Override
    public Boolean generateColumn() {
        prepareValues();
        try {
            appendColumn();
        } catch (VetoUpdatePatternInUseException veto) {
            wizard.showPatternInUseError();
            return false;
        } catch (VetoException veto) {
            wizard.showGenericVetoError();
            return false;
        }

        return true;
    }

    @Override
    public Type getType() {
        return Type.BASIC;
    }

    void appendColumn() throws VetoException {
        if (isNewColumn()) {
            presenter.appendColumn(editingPattern(),
                                   editingCol());
        } else {
            presenter.updateColumn(getOriginalPattern52(),
                                   originalCondition(),
                                   editingPattern(),
                                   editingCol());
        }
    }

    @Override
    public Pattern52 editingPattern() {
        return wrappedPattern();
    }

    private Pattern52 wrappedPattern() {
        editingPattern.setFactType(patternWrapper().getFactType());
        editingPattern.setBoundName(patternWrapper().getBoundName());
        editingPattern.setNegated(patternWrapper().isNegated());
        editingPattern.setEntryPointName(patternWrapper().getEntryPointName());

        return editingPattern;
    }

    public Pattern52 getEditingPattern() {
        return editingPattern;
    }

    @Override
    public void setEditingPattern(final PatternWrapper patternWrapper) {
        setupDefaultValues();
        setPatternWrapper(patternWrapper);

        fireChangeEvent(patternPage);
        fireChangeEvent(calculationTypePage);
        fireChangeEvent(fieldPage);
        fireChangeEvent(operatorPage);
        fireChangeEvent(valueOptionsPage);
        fireChangeEvent(additionalInfoPage);
    }

    void setPatternWrapper(final PatternWrapper patternWrapper) {
        this.patternWrapper = patternWrapper;
        this.editingPattern = extractEditingPattern();
    }

    private Pattern52 extractEditingPattern() {
        final String factType = patternWrapper().getFactType();
        final String boundName = patternWrapper().getBoundName();
        final Optional<Pattern52> pattern;

        if (!patternWrapper().isNegated()) {
            pattern = Optional.ofNullable(model().getConditionPattern(boundName));
        } else {
            pattern = model()
                    .getPatterns()
                    .stream()
                    .filter(Pattern52::isNegated)
                    .filter(p -> p.getFactType().equals(factType))
                    .findFirst();
        }

        return pattern.orElse(emptyPattern());
    }

    void prepareValues() {
        if (constraintValue() == BaseSingleFieldConstraint.TYPE_PREDICATE) {
            editingCol().setOperator(operatorPlaceholder());
        }

        if (!isBindable()) {
            editingCol().setBinding(null);
        }
    }

    @Override
    public PatternWrapper patternWrapper() {
        return Optional.ofNullable(patternWrapper).orElse(new PatternWrapper());
    }

    @Override
    public String getEntryPointName() {
        return patternWrapper().getEntryPointName();
    }

    @Override
    public String getPatternPageDescription() {
        return translate(GuidedDecisionTableErraiConstants.PatternPageView_PatternPageDescriptionConditions);
    }

    @Override
    public void setEntryPointName(final String entryPointName) {
        patternWrapper().setEntryPointName(entryPointName);
    }

    @Override
    public Set<PatternWrapper> getPatterns() {
        final Set<PatternWrapper> patterns = new HashSet<>();
        final BRLRuleModel brlRuleModel = makeBRLRuleModel();
        final List<String> variables = brlRuleModel.getLHSPatternVariables();
        variables.forEach(var -> {
            final Pattern52 pattern = getPresenter().getModel().getConditionPattern(var);
            if (!(pattern instanceof FactPatternPattern52Adaptor)) {
                final String factType = brlRuleModel.getLHSBoundFact(var).getFactType();
                final boolean isNegated = brlRuleModel.getLHSBoundFact(var).isNegated();
                patterns.add(new PatternWrapper(factType,
                                                var,
                                                isNegated));
            }
        });

        return patterns;
    }

    @Override
    public ConditionCol52 editingCol() {
        if (nil(patternWrapper().getFactType())) {
            resetFieldAndOperator();
        }

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
        final List<CompositeColumn<? extends BaseColumn>> conditions = getPresenter().getModel().getConditions();
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
        // empty - this widget is not enabled
    }

    @Override
    public void setUpdate(Boolean value) {
        // empty - this widget is not enabled
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
    public String getFactField() {
        return editingCol().getFactField();
    }

    @Override
    public void setFactField(final String selectedValue) {
        final AsyncPackageDataModelOracle oracle = presenter.getDataModelOracle();

        editingCol().setFactField(selectedValue);
        editingCol().setFieldType(oracle.getFieldType(getFactType(),
                                                      getFactField()));

        fireChangeEvent(fieldPage);
        fireChangeEvent(operatorPage);
        fireChangeEvent(additionalInfoPage);
        fireChangeEvent(valueOptionsPage);
    }

    @Override
    public String getBinding() {
        return editingCol().getBinding();
    }

    @Override
    public void setBinding(final String binding) {
        editingCol().setBinding(binding);

        fireChangeEvent(valueOptionsPage);
    }

    @Override
    public boolean doesOperatorNeedValue() {
        return validator().doesOperatorNeedValue(editingCol());
    }

    @Override
    public boolean isBindable() {
        return tableFormat() == LIMITED_ENTRY || constraintValue() == BaseSingleFieldConstraint.TYPE_LITERAL;
    }

    @Override
    public int constraintValue() {
        final boolean factHasEnums = presenter.getDataModelOracle().hasEnums(getFactType(),
                                                                             getFactField());

        if (factHasEnums) {
            setConstraintValueFieldAndUpdateEditingCol(BaseSingleFieldConstraint.TYPE_LITERAL);
        }

        return constraintValue;
    }

    @Override
    public FieldAccessorsAndMutators getAccessor() {
        return FieldAccessorsAndMutators.ACCESSOR;
    }

    @Override
    public boolean filterEnumFields() {
        return constraintValue() == BaseSingleFieldConstraint.TYPE_RET_VALUE;
    }

    @Override
    public TableFormat tableFormat() {
        return model().getTableFormat();
    }

    @Override
    public String getValueList() {
        return editingCol().getValueList();
    }

    @Override
    public void setValueList(final String valueList) {
        editingCol().setValueList(valueList);

        assertDefaultValue();

        fireChangeEvent(valueOptionsPage);
    }

    @Override
    public boolean doesOperatorAcceptValueList() {
        return validator().doesOperatorAcceptValueList(editingCol());
    }

    @Override
    public IsWidget defaultValueWidget() {
        return new DefaultWidgetFactory<>(this).create();
    }

    @Override
    public IsWidget limitedValueWidget() {
        return new LimitedWidgetFactory<>(this).create();
    }

    private Validator validator() {
        return new Validator(model().getConditions());
    }

    public void setConstraintValue(final int constraintValue) {
        setConstraintValueFieldAndUpdateEditingCol(constraintValue);

        resetFieldAndOperator();

        fireChangeEvent(calculationTypePage);
        fireChangeEvent(fieldPage);
        fireChangeEvent(operatorPage);
    }

    @Override
    public void setValueOptionsPageAsCompleted() {
        if (!isValueOptionsPageCompleted()) {
            setValueOptionsPageCompleted();

            fireChangeEvent(valueOptionsPage);
        }
    }

    @Override
    public Boolean isValueOptionsPageCompleted() {
        return valueOptionsPageCompleted;
    }

    @Override
    public Boolean isFieldBindingValid() {
        if (!isBindable()) {
            return Boolean.TRUE;
        }

        final String binding = getBinding();
        if ((binding == null || binding.isEmpty())) {
            return Boolean.TRUE;
        }

        if (!isNewColumn()) {
            if (binding.equals(originalCondition().getBinding())) {
                return Boolean.TRUE;
            }
        }

        return !makeBRLRuleModel().isVariableNameUsed(binding);
    }

    BRLRuleModel makeBRLRuleModel() {

        final GuidedDecisionTable52 model = getPresenter().getModel();
        final BRLRuleModel brlRuleModel = new BRLRuleModel(model);
        final FactPattern pat = patternWrapper().makeFactPattern();

        brlRuleModel.addLhsItem(pat);

        return brlRuleModel;
    }

    public String getFactType() {
        return patternWrapper().getFactType();
    }

    public void setOperator(final String operator) {
        editingCol().setOperator(operator);

        fireChangeEvent(operatorPage);
        fireChangeEvent(additionalInfoPage);
        fireChangeEvent(valueOptionsPage);
    }

    void setValueOptionsPageCompleted() {
        valueOptionsPageCompleted = Boolean.TRUE;
    }

    void setupDefaultValues() {
        if (isNewColumn()) {
            setupDefaultValuesForANewColumn();
            resetConstraintValueAndValueOptionsPageStatus();
            resetFieldAndOperator();
        } else {
            setupDefaultValuesForAnExistingColumn();
            setValueOptionsPageCompleted();
        }
    }

    private void setupDefaultValuesForAnExistingColumn() {
        final Pattern52 clonedPattern52 = getOriginalPattern52().clonePattern();
        final ConditionCol52 clonedConditionCol52 = clone(originalCondition());
        final int constraintValueType = originalCondition().getConstraintValueType();

        editingPattern = clonedPattern52;
        editingCol = clonedConditionCol52;
        constraintValue = constraintValueType;
    }

    private void setupDefaultValuesForANewColumn() {
        final Pattern52 newPattern52 = emptyPattern();
        final ConditionCol52 newConditionCol52 = newConditionColumn();

        editingPattern = newPattern52;
        editingCol = newConditionCol52;
    }

    private void resetConstraintValueAndValueOptionsPageStatus() {
        constraintValue = BaseSingleFieldConstraint.TYPE_UNDEFINED;
        valueOptionsPageCompleted = Boolean.FALSE;
    }

    ConditionCol52 clone(final ConditionCol52 column) {
        final ConditionCol52 clone;

        if (tableFormat() == LIMITED_ENTRY) {
            clone = new LimitedEntryConditionCol52() {{
                setValue(cloneDTCellValue(((LimitedEntryCol) column).getValue()));
            }};
        } else {
            clone = new ConditionCol52();
        }

        clone.setConstraintValueType(column.getConstraintValueType());
        clone.setFactField(column.getFactField());
        clone.setFieldType(column.getFieldType());
        clone.setHeader(column.getHeader());
        clone.setOperator(column.getOperator());
        clone.setValueList(column.getValueList());
        clone.setDefaultValue(cloneDTCellValue(column.getDefaultValue()));
        clone.setHideColumn(column.isHideColumn());
        clone.setParameters(column.getParameters());
        clone.setWidth(column.getWidth());
        clone.setBinding(column.getBinding());

        return clone;
    }

    DTCellValue52 cloneDTCellValue(final DTCellValue52 dcv) {
        if (dcv == null) {
            return null;
        }

        return new DTCellValue52(dcv);
    }

    ConditionCol52 originalCondition() {
        return (ConditionCol52) getOriginalColumnConfig52();
    }

    void resetFieldAndOperator() {
        editingCol.setFactField("");
        editingCol.setFieldType("");
        editingCol.setOperator(operatorPlaceholder());
    }

    void setConstraintValueFieldAndUpdateEditingCol(int constraintValue) {
        this.constraintValue = constraintValue;

        editingCol().setConstraintValueType(constraintValue);
    }

    void assertDefaultValue() {
        final CellUtilities cellUtilities = new CellUtilities();
        final AsyncPackageDataModelOracle oracle = presenter.getDataModelOracle();
        final ColumnUtilities columnUtilities = new ColumnUtilities(model(),
                                                                    oracle);
        final List<String> valueList = Arrays.asList(columnUtilities.getValueList(editingCol));

        if (valueList.size() > 0) {
            final String defaultValue = cellUtilities.asString(editingCol().getDefaultValue());
            if (!valueList.contains(defaultValue)) {
                editingCol.getDefaultValue().clearValues();
            }
        } else {
            //Ensure the Default Value has been updated to represent the column's data-type.
            final DTCellValue52 defaultValue = editingCol().getDefaultValue();
            final DataType.DataTypes dataType = columnUtilities.getDataType(editingPattern(),
                                                                            editingCol());
            cellUtilities.convertDTCellValueType(dataType,
                                                 defaultValue);
        }
    }

    private AdditionalInfoPage initializedAdditionalInfoPage() {
        return AdditionalInfoPageInitializer.init(additionalInfoPage,
                                                  this);
    }

    private ValueOptionsPage<ConditionColumnPlugin> initializedValueOptionsPage() {
        return ValueOptionsPageInitializer.init(valueOptionsPage,
                                                this);
    }

    ConditionCol52 newConditionColumn() {
        switch (tableFormat()) {
            case EXTENDED_ENTRY:
                return new ConditionCol52();
            case LIMITED_ENTRY:
                return new LimitedEntryConditionCol52();
            default:
                throw new UnsupportedOperationException("Unsupported table format: " + tableFormat());
        }
    }

    private boolean isExtendedEntryTable() {
        return tableFormat() == EXTENDED_ENTRY;
    }

    GuidedDecisionTable52 model() {
        return presenter.getModel();
    }

    Pattern52 emptyPattern() {
        return new Pattern52().clonePattern();
    }

    public String operatorPlaceholder() {
        return GuidedRuleEditorResources.CONSTANTS.pleaseChoose();
    }
}
