/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.CellUtilities;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.screens.guided.rule.client.editor.BindingTextBox;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

/**
 * This is a configuration editor for a column in a the guided decision table.
 */
public class ConditionPopup {

    protected ConditionPopupView view;

    private final AsyncPackageDataModelOracle oracle;
    private final GuidedDecisionTableView.Presenter presenter;
    private final DTCellValueWidgetFactory factory;
    private final Validator validator;
    private final BRLRuleModel rm;
    private final CellUtilities cellUtilities;
    private final ColumnUtilities columnUtilities;

    //TODO {manstis} Popups need to MVP'ed
    private final GuidedDecisionTable52 model;

    private Pattern52 editingPattern;
    private ConditionCol52 editingCol;
    private final ConditionColumnCommand refreshGrid;
    private final ConditionCol52 originalCol;
    private final boolean isNew;
    private final boolean isReadOnly;

    public ConditionPopup(final GuidedDecisionTable52 model,
                          final AsyncPackageDataModelOracle oracle,
                          final GuidedDecisionTableView.Presenter presenter,
                          final ConditionColumnCommand refreshGrid,
                          final ConditionCol52 col,
                          final boolean isNew,
                          final boolean isReadOnly) {
        this(model,
             oracle,
             presenter,
             refreshGrid,
             new Pattern52(),
             col,
             isNew,
             isReadOnly);
    }

    public ConditionPopup(final GuidedDecisionTable52 model,
                          final AsyncPackageDataModelOracle oracle,
                          final GuidedDecisionTableView.Presenter presenter,
                          final ConditionColumnCommand refreshGrid,
                          final Pattern52 pattern,
                          final ConditionCol52 column,
                          final boolean isNew,
                          final boolean isReadOnly) {

        this.rm = new BRLRuleModel(model);
        this.editingPattern = pattern != null ? pattern.clonePattern() : null;
        this.editingCol = cloneConditionColumn(column);
        this.model = model;
        this.oracle = oracle;
        this.presenter = presenter;
        this.refreshGrid = refreshGrid;
        this.originalCol = column;
        this.isNew = isNew;
        this.isReadOnly = isReadOnly;
        this.validator = new Validator(model.getConditions());
        this.cellUtilities = new CellUtilities();
        this.columnUtilities = new ColumnUtilities(model,
                                                   oracle);

        //Set-up a factory for value editors
        factory = DTCellValueWidgetFactory.getInstance(model,
                                                       oracle,
                                                       isReadOnly,
                                                       allowEmptyValues());

        view = new ConditionPopupView(this);
        view.initializeView();
    }

    public void show() {
        view.show();
    }

    public void applyChanges() {
        if (null == editingCol.getHeader() || "".equals(editingCol.getHeader())) {
            view.warnAboutMissingColumnHeaderDescription();
            return;
        }
        if (editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE) {

            //Field mandatory for Literals and Formulae
            if (null == editingCol.getFactField() || "".equals(editingCol.getFactField())) {
                view.warnAboutMissingFactField();
                return;
            }

            //Operator optional for Literals and Formulae
            if (editingCol.getOperator() == null) {
                view.warnAboutMissingOperator();
                return;
            }
        } else {

            //Clear operator for predicates, but leave field intact for interpolation of $param values
            editingCol.setOperator(null);
        }

        //Check for unique binding
        String factBinding = editingPattern.getBoundName();
        String fieldBinding = editingCol.getBinding();
        if (factBinding != null && fieldBinding != null) {
            if (editingCol.isBound() && (!isBindingUnique(fieldBinding) || factBinding.compareTo(fieldBinding) == 0)) {
                view.warnAboutAlreadyUsedBinding();
                return;
            }
        }

        //Check column header is unique
        if (isNew) {
            if (!unique(editingCol.getHeader())) {
                view.warnAboutAlreadyUsedColumnHeaderName();
                return;
            }
        } else {
            if (!originalCol.getHeader().equals(editingCol.getHeader())) {
                if (!unique(editingCol.getHeader())) {
                    view.warnAboutAlreadyUsedColumnHeaderName();
                    return;
                }
            }
        }

        //Clear binding if column is not a literal
        if (editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL) {
            editingCol.setBinding(null);
        }

        // Pass new\modified column back for handling
        refreshGrid.execute(editingPattern,
                            editingCol);
        view.hide();
    }

    private boolean allowEmptyValues() {
        return this.model.getTableFormat() == TableFormat.EXTENDED_ENTRY;
    }

    private ConditionCol52 cloneConditionColumn(ConditionCol52 col) {
        ConditionCol52 clone = null;
        if (col instanceof LimitedEntryConditionCol52) {
            clone = new LimitedEntryConditionCol52();
            DTCellValue52 dcv = cloneDTCellValue(((LimitedEntryCol) col).getValue());
            ((LimitedEntryCol) clone).setValue(dcv);
        } else {
            clone = new ConditionCol52();
        }
        clone.setConstraintValueType(col.getConstraintValueType());
        clone.setFactField(col.getFactField());
        clone.setFieldType(col.getFieldType());
        clone.setHeader(col.getHeader());
        clone.setOperator(col.getOperator());
        clone.setValueList(col.getValueList());
        clone.setDefaultValue(cloneDTCellValue(col.getDefaultValue()));
        clone.setHideColumn(col.isHideColumn());
        clone.setParameters(col.getParameters());
        clone.setWidth(col.getWidth());
        clone.setBinding(col.getBinding());
        return clone;
    }

    private DTCellValue52 cloneDTCellValue(DTCellValue52 dcv) {
        if (dcv == null) {
            return null;
        }
        DTCellValue52 clone = new DTCellValue52(dcv);
        return clone;
    }

    public void makeLimitedValueWidget() {

        if (model.getTableFormat() == TableFormat.LIMITED_ENTRY) {
            view.addLimitedEntryValue();
            if (!(editingCol instanceof LimitedEntryConditionCol52)) {
                return;
            }
            LimitedEntryConditionCol52 lec = (LimitedEntryConditionCol52) editingCol;
            boolean doesOperatorNeedValue = validator.doesOperatorNeedValue(editingCol);
            if (!doesOperatorNeedValue) {
                view.setLimitedEntryVisibility(false);
                lec.setValue(null);
                return;
            }
            view.setLimitedEntryVisibility(true);
            if (lec.getValue() == null) {
                lec.setValue(factory.makeNewValue(editingPattern,
                                                  editingCol));
            }
            view.setLimitedEntryWidget(factory.getWidget(editingPattern,
                                                         editingCol,
                                                         lec.getValue()));
        }
    }

    public void makeDefaultValueWidget() {
        //Default value
        if (model.getTableFormat() == TableFormat.EXTENDED_ENTRY) {
            view.addDefaultValueIfNoPresent();
            if (model.getTableFormat() == TableFormat.LIMITED_ENTRY) {
                return;
            }
            if (nil(editingCol.getFactField())) {
                view.setDefaultValueVisibility(false);
                return;
            }

            //Don't show Default Value if operator does not require a value
            if (!validator.doesOperatorNeedValue(editingCol)) {
                view.setDefaultValueVisibility(false);
                return;
            }

            view.setDefaultValueVisibility(true);
            if (editingCol.getDefaultValue() == null) {
                editingCol.setDefaultValue(factory.makeNewValue(editingPattern,
                                                                editingCol));
            }

            //Ensure the Default Value has been updated to represent the column's
            //data-type. Legacy Default Values are all String-based and need to be
            //coerced to the correct type
            final DTCellValue52 defaultValue = editingCol.getDefaultValue();
            final DataType.DataTypes dataType = columnUtilities.getDataType(editingPattern,
                                                                            editingCol);
            cellUtilities.convertDTCellValueType(dataType,
                                                 defaultValue);

            //Correct comma-separated Default Value if operator does not support it
            if (!validator.doesOperatorAcceptCommaSeparatedValues(editingCol)) {
                cellUtilities.removeCommaSeparatedValue(defaultValue);
            }

            view.setDefaultValueWidget(factory.getWidget(editingPattern,
                                                         editingCol,
                                                         defaultValue));
        }
    }

    public void applyConsTypeChange(int newConstraintValueType) {
        editingCol.setConstraintValueType(newConstraintValueType);
        initialiseViewForConstraintValueType();
    }

    public void initialiseViewForConstraintValueType() {
        view.enableBinding(editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_LITERAL && !isReadOnly);
        doFieldLabel();
        doValueList();
        doOperatorLabel();
        doImageButtons();
        makeDefaultValueWidget();
    }

    public void doImageButtons() {
        int constraintType = editingCol.getConstraintValueType();
        boolean enableField = !(nil(editingPattern.getFactType()) || constraintType == BaseSingleFieldConstraint.TYPE_PREDICATE || isReadOnly);
        boolean enableOp = !(nil(editingCol.getFactField()) || constraintType == BaseSingleFieldConstraint.TYPE_PREDICATE || isReadOnly);
        view.enableEditField(enableField);
        view.enableEditOperator(enableOp);
    }

    private boolean isBindingUnique(String binding) {
        return !rm.isVariableNameUsed(binding);
    }

    public void doFieldLabel() {
        if (editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE) {
            if (this.editingCol.getFactField() == null || this.editingCol.getFactField().equals("")) {
                view.setFieldLabelText(GuidedDecisionTableConstants.INSTANCE.notNeededForPredicate());
            } else {
                view.setFieldLabelText(this.editingCol.getFactField());
            }
            view.setFieldLabelDisplayStyle(Style.Display.INLINE);
        } else if (nil(editingPattern.getFactType())) {
            view.setFieldLabelText(GuidedDecisionTableConstants.INSTANCE.pleaseSelectAPatternFirst());
            view.setFieldLabelDisplayStyle(Style.Display.NONE);
        } else if (nil(editingCol.getFactField())) {
            view.setFieldLabelText(GuidedDecisionTableConstants.INSTANCE.pleaseSelectAField());
            view.setFieldLabelDisplayStyle(Style.Display.NONE);
        } else {
            view.setFieldLabelText(this.editingCol.getFactField());
        }
    }

    public void doOperatorLabel() {
        if (editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE) {
            view.setOperatorLabelText(GuidedDecisionTableConstants.INSTANCE.notNeededForPredicate());
        } else if (nil(editingPattern.getFactType())) {
            view.setOperatorLabelText(GuidedDecisionTableConstants.INSTANCE.pleaseSelectAPatternFirst());
        } else if (nil(editingCol.getFactField())) {
            view.setOperatorLabelText(GuidedDecisionTableConstants.INSTANCE.pleaseChooseAFieldFirst());
        } else if (nil(editingCol.getOperator())) {
            view.setOperatorLabelText(GuidedDecisionTableConstants.INSTANCE.pleaseSelectAnOperator());
        } else {
            view.setOperatorLabelText(HumanReadable.getOperatorDisplayName(editingCol.getOperator()));
        }
    }

    public void doPatternLabel() {
        if (editingPattern.getFactType() != null) {
            StringBuilder patternLabel = new StringBuilder();
            String factType = editingPattern.getFactType();
            String boundName = editingPattern.getBoundName();
            if (factType != null && factType.length() > 0) {
                if (editingPattern.isNegated()) {
                    patternLabel.append(GuidedDecisionTableConstants.INSTANCE.negatedPattern()).append(" ").append(factType);
                } else {
                    patternLabel.append(factType).append(" [").append(boundName).append("]");
                }
            }
            view.setPatternLabelText(patternLabel.toString());
        }
        doFieldLabel();
        doOperatorLabel();
    }

    public void doValueList() {
        if (model.getTableFormat() == TableFormat.LIMITED_ENTRY) {
            return;
        }

        //Don't show a Value List if either the Fact\Field is empty
        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        boolean enableValueList = !(isReadOnly || ((factType == null || "".equals(factType)) || (factField == null || "".equals(factField))));

        //Don't show Value List if operator does not accept one
        if (enableValueList) {
            enableValueList = validator.doesOperatorAcceptValueList(editingCol);
        }

        //Don't show a Value List if the Fact\Field has an enumeration
        if (enableValueList) {
            enableValueList = !oracle.hasEnums(factType,
                                               factField);
        }
        view.enableValueListWidget(enableValueList);
        if (!enableValueList) {
            view.setValueListWidgetText("");
        } else {
            view.setValueListWidgetText(editingCol.getValueList());
        }
    }

    public void doCalculationType() {
        if (model.getTableFormat() == TableFormat.LIMITED_ENTRY) {
            return;
        }

        //Disable Formula and Predicate if the Fact\Field has enums
        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        final boolean hasEnums = oracle.hasEnums(factType,
                                                 factField);
        view.enableLiteral(hasEnums || !isReadOnly);
        view.enableFormula(!(hasEnums || isReadOnly));
        view.enablePredicate(!(hasEnums || isReadOnly));

        //If Fact\Field has enums the Value Type has to be a literal
        if (hasEnums) {
            this.editingCol.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
        }
    }

    ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();

        int selectedIndex = -1;
        final List<Pattern52> availablePatterns = model.getPatterns();
        final String editingPatternBinding = editingPattern == null ? null : editingPattern.getBoundName();

        for (int i = 0; i < availablePatterns.size(); i++) {
            final Pattern52 p = availablePatterns.get(i);
            if (p.getBoundName().equals(editingPatternBinding)) {
                selectedIndex = i;
            }
            if (!vars.contains(p.getBoundName())) {
                patterns.addItem((p.isNegated() ? GuidedDecisionTableConstants.INSTANCE.negatedPattern() + " " : "")
                                         + p.getFactType()
                                         + " [" + p.getBoundName() + "]",
                                 p.getFactType()
                                         + " " + p.getBoundName()
                                         + " " + p.isNegated());
                vars.add(p.getBoundName());
            }
        }

        if (selectedIndex >= 0) {
            selectListBoxItem(patterns,
                              selectedIndex);
        }

        return patterns;
    }

    void selectListBoxItem(final ListBox listBox,
                           final int index) {
        listBox.setSelectedIndex(index);
    }

    private boolean nil(String s) {
        return s == null || s.equals("");
    }

    public void showOperatorChange() {
        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        this.oracle.getOperatorCompletions(factType,
                                           factField,
                                           new Callback<String[]>() {
                                               @Override
                                               public void callback(final String[] ops) {
                                                   doShowOperatorChange(factType,
                                                                        factField,
                                                                        ops);
                                               }
                                           });
    }

    private void doShowOperatorChange(final String factType,
                                      final String factField,
                                      final String[] ops) {
        final FormStylePopup pop = new FormStylePopup(GuidedDecisionTableConstants.INSTANCE.SetTheOperator());

        //Operators "in" and "not in" are only allowed if the Calculation Type is a Literal
        final List<String> filteredOps = new ArrayList<String>();
        for (String op : ops) {
            filteredOps.add(op);
        }
        if (BaseSingleFieldConstraint.TYPE_LITERAL != this.editingCol.getConstraintValueType()) {
            filteredOps.remove("in");
            filteredOps.remove("not in");
        }

        final String[] displayOps = new String[filteredOps.size()];
        filteredOps.toArray(displayOps);

        final CEPOperatorsDropdown box = new CEPOperatorsDropdown(displayOps,
                                                                  editingCol);

        box.insertItem(GuidedDecisionTableConstants.INSTANCE.noOperator(),
                       "",
                       1);
        pop.addAttribute(GuidedDecisionTableConstants.INSTANCE.Operator(),
                         box);

        pop.add(new ModalFooterOKCancelButtons(new Command() {
            @Override
            public void execute() {
                editingCol.setOperator(box.getValue(box.getSelectedIndex()));
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doOperatorLabel();
                doValueList();
                pop.hide();
                view.enableFooter(true);
            }
        },
                                               new Command() {
                                                   @Override
                                                   public void execute() {
                                                       pop.hide();
                                                       view.enableFooter(true);
                                                   }
                                               }
        ));

        view.enableFooter(false);
        pop.show();
    }

    private boolean unique(String header) {
        for (CompositeColumn<?> cc : model.getConditions()) {
            for (int iChild = 0; iChild < cc.getChildColumns().size(); iChild++) {
                if (cc.getChildColumns().get(iChild).getHeader().equals(header)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void showChangePattern(ClickEvent w) {
        final ListBox pats = this.loadPatterns();
        if (pats.getItemCount() == 0) {
            showNewPatternDialog();
            return;
        }
        final FormStylePopup pop = new FormStylePopup(GuidedDecisionTableConstants.INSTANCE.FactType());

        pop.addAttribute(GuidedDecisionTableConstants.INSTANCE.ChooseExistingPatternToAddColumnTo(),
                         pats);

        pop.add(new ModalFooterChangePattern(getShowPatternOKCommand(pats,
                                                                     pop),
                                             () -> {
                                                 pop.hide();
                                                 showNewPatternDialog();
                                             },
                                             () -> {
                                                 pop.hide();
                                                 view.enableFooter(true);
                                             }
        ));

        view.enableFooter(false);
        pop.show();
    }

    Command getShowPatternOKCommand(final ListBox pats,
                                    final FormStylePopup pop) {
        return () -> {
            String[] val = pats.getValue(pats.getSelectedIndex()).split("\\s");
            final Optional<Pattern52> p = extractEditingPattern(val);

            //This should never be thrown; but let's fail fast to make debugging easier
            editingPattern = p.orElseThrow(IllegalStateException::new);

            //Clear Field and Operator when pattern changes
            editingCol.setFactField(null);
            editingCol.setOperator(null);

            //Set-up UI
            view.setEntryPointName(editingPattern.getEntryPointName());
            view.selectOperator(editingPattern.getWindow().getOperator());
            makeLimitedValueWidget();
            makeDefaultValueWidget();
            displayCEPOperators();
            doPatternLabel();
            doValueList();
            doCalculationType();
            doImageButtons();

            pop.hide();
            view.enableFooter(true);
        };
    }

    Optional<Pattern52> extractEditingPattern(final String[] metadata) {
        final String factType = metadata[0];
        final String factBinding = metadata[1];
        final boolean negated = Boolean.parseBoolean(metadata[2]);
        if (!negated) {
            return Optional.ofNullable(model.getConditionPattern(factBinding));
        }
        return model.getPatterns().stream().filter(Pattern52::isNegated).filter(p -> p.getFactType().equals(factType)).findFirst();
    }

    public void showFieldChange() {
        view.enableFooter(false);
        view.showFieldChangePopUp();
    }

    ListBox loadFields() {
        final ListBox box = new ListBox();
        this.oracle.getFieldCompletions(this.editingPattern.getFactType(),
                                        FieldAccessorsAndMutators.ACCESSOR,
                                        (ModelField[] fields) -> {
                                            switch (editingCol.getConstraintValueType()) {
                                                case BaseSingleFieldConstraint.TYPE_LITERAL:
                                                    //Literals can be on any field
                                                    int selectedIndex = -1;
                                                    for (int i = 0; i < fields.length; i++) {
                                                        final String fieldName = fields[i].getName();
                                                        if (fieldName.equals(editingCol.getFactField())) {
                                                            selectedIndex = i;
                                                        }
                                                        box.addItem(fields[i].getName());
                                                    }
                                                    if (selectedIndex >= 0) {
                                                        selectListBoxItem(box,
                                                                          selectedIndex);
                                                    }
                                                    break;

                                                case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                                                    //Formulae can only consume fields that do not have enumerations
                                                    for (int i = 0; i < fields.length; i++) {
                                                        final String fieldName = fields[i].getName();
                                                        if (!oracle.hasEnums(editingPattern.getFactType(),
                                                                             fieldName)) {
                                                            box.addItem(fieldName);
                                                        }
                                                    }
                                                    break;

                                                case BaseSingleFieldConstraint.TYPE_PREDICATE:
                                                    //Predicates don't need a field (this should never be reachable as the
                                                    //field selector is disabled when the Calculation Type is Predicate)
                                                    break;
                                            }
                                        });
        return box;
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup(GuidedDecisionTableConstants.INSTANCE.FactType());
        pop.setTitle(GuidedDecisionTableConstants.INSTANCE.CreateANewFactPattern());
        final ListBox types = new ListBox();
        for (int i = 0; i < oracle.getFactTypes().length; i++) {
            types.addItem(oracle.getFactTypes()[i]);
        }
        pop.addAttribute(GuidedDecisionTableConstants.INSTANCE.FactType(),
                         types);
        final TextBox binding = new BindingTextBox();
        binding.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                binding.setText(binding.getText().replace(" ",
                                                          ""));
            }
        });
        pop.addAttribute(new StringBuilder(GuidedDecisionTableConstants.INSTANCE.Binding()).append(GuidedDecisionTableConstants.COLON).toString(),
                         binding);

        //Patterns can be negated, i.e. "not Pattern(...)"
        final CheckBox chkNegated = new CheckBox();
        chkNegated.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                boolean isPatternNegated = chkNegated.getValue();
                binding.setEnabled(!isPatternNegated);
            }
        });
        pop.addAttribute(GuidedDecisionTableConstants.INSTANCE.negatePattern(),
                         chkNegated);

        pop.add(new ModalFooterOKCancelButtons(new Command() {
            @Override
            public void execute() {
                boolean isPatternNegated = chkNegated.getValue();
                String ft = types.getItemText(types.getSelectedIndex());
                String fn = isPatternNegated ? "" : binding.getText();
                if (!isPatternNegated) {
                    if (fn.equals("")) {
                        Window.alert(GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameForFact());
                        return;
                    } else if (fn.equals(ft)) {
                        Window.alert(GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameThatIsNotTheSameAsTheFactType());
                        return;
                    } else if (!isBindingUnique(fn)) {
                        Window.alert(GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern());
                        return;
                    }
                }

                //Create new pattern
                editingPattern = new Pattern52();
                editingPattern.setFactType(ft);
                editingPattern.setBoundName(fn);
                editingPattern.setNegated(isPatternNegated);

                //Clear Field and Operator when pattern changes
                editingCol.setFactField(null);
                editingCol.setOperator(null);

                //Set-up UI
                view.setEntryPointName(editingPattern.getEntryPointName());
                view.selectOperator(editingPattern.getWindow().getOperator());
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                displayCEPOperators();
                doPatternLabel();
                doValueList();
                doCalculationType();
                doOperatorLabel();
                doImageButtons();

                pop.hide();
                view.enableFooter(true);
            }
        },
                                               new Command() {
                                                   @Override
                                                   public void execute() {
                                                       pop.hide();
                                                       view.enableFooter(true);
                                                   }
                                               }
        ));

        view.enableFooter(false);
        pop.show();
    }

    public void displayCEPOperators() {
        oracle.isFactTypeAnEvent(editingPattern.getFactType(),
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(final Boolean result) {
                                         view.setCepWindowVisibility(Boolean.TRUE.equals(result));
                                     }
                                 });
    }

    public boolean isReadOnly() {
        return isReadOnly;
    }

    public TableFormat getTableFormat() {
        return model.getTableFormat();
    }

    public int getConstraintValueType() {
        return editingCol.getConstraintValueType();
    }

    public void setFactField(String factField) {
        editingCol.setFactField(factField);
    }

    public Pattern52 getEditingPattern() {
        return editingPattern;
    }

    public String getHeader() {
        return editingCol.getHeader();
    }

    public void setHeader(String header) {
        editingCol.setHeader(header);
    }

    public String getValueList() {
        return editingCol.getValueList();
    }

    public void setValueList(String valueList) {
        editingCol.setValueList(valueList);
    }

    public ConditionCol52 getEditingCol() {
        return editingCol;
    }

    public void assertDefaultValue() {
        final List<String> valueList = Arrays.asList(columnUtilities.getValueList(editingCol));
        if (valueList.size() > 0) {
            final String defaultValue = cellUtilities.asString(editingCol.getDefaultValue());
            if (!valueList.contains(defaultValue)) {
                editingCol.getDefaultValue().clearValues();
            }
        } else {
            //Ensure the Default Value has been updated to represent the column's data-type.
            final DTCellValue52 defaultValue = editingCol.getDefaultValue();
            final DataType.DataTypes dataType = columnUtilities.getDataType(editingPattern,
                                                                            editingCol);
            cellUtilities.convertDTCellValueType(dataType,
                                                 defaultValue);
        }
    }

    public String getBinding() {
        return editingCol.getBinding();
    }

    public void setBinding(String binding) {
        editingCol.setBinding(binding);
    }

    public void confirmFieldChangePopUp(FormStylePopup popUp,
                                        String newSelectedField) {
        boolean fieldChanged = true;
        if (editingCol.getFactField() != null) {
            fieldChanged = editingCol.getFactField().compareTo(newSelectedField) != 0;
        }

        editingCol.setFactField(newSelectedField);
        editingCol.setFieldType(oracle.getFieldType(editingPattern.getFactType(),
                                                    editingCol.getFactField()
                                )
        );

        //Clear Operator when field changes
        if (fieldChanged) {
            editingCol.setOperator(null);
            editingCol.setValueList(null);
        }

        //Setup UI
        doFieldLabel();
        doValueList();
        doCalculationType();
        makeLimitedValueWidget();
        makeDefaultValueWidget();
        doOperatorLabel();
        doImageButtons();

        popUp.hide();
        view.enableFooter(true);
    }

    public void cancelFieldChangePopUp(FormStylePopup popUp) {
        popUp.hide();
        view.enableFooter(true);
    }
}
