/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

/**
 * Constraint editor for the VerifyField of the expect part
 */
public class VerifyFieldConstraintEditor extends Composite {

    private String factType;
    private VerifyField field;
    private final Panel panel;
    private Scenario scenario;
    private AsyncPackageDataModelOracle oracle;
    private Callback<String> callback;
    private ExecutionTrace executionTrace;

    public VerifyFieldConstraintEditor(final String factType,
                                       final Callback<String> callback,
                                       final VerifyField field,
                                       final AsyncPackageDataModelOracle oracle,
                                       final Scenario scenario,
                                       final ExecutionTrace executionTrace) {
        this.field = field;
        this.oracle = oracle;
        this.factType = factType;
        this.callback = callback;
        this.scenario = scenario;
        this.executionTrace = executionTrace;
        panel = new SimplePanel();
        refreshEditor();
        initWidget(panel);
    }

    private void refreshEditor() {
        String flType = oracle.getFieldType(factType,
                                            field.getFieldName());
        panel.clear();

        if (Objects.equals(flType, DataType.TYPE_BOOLEAN)) {
            String[] c = new String[]{"true", "false"};
            panel.add(new EnumDropDown(field.getExpected(),
                                       new DropDownValueChanged() {
                                           public void valueChanged(String newText,
                                                                    String newValue) {
                                               callback.callback(newValue);
                                           }
                                       },
                                       DropDownData.create(c),
                                       oracle.getResourcePath()));
        } else if (Objects.equals(flType, DataType.TYPE_DATE)) {
            FieldDatePicker fieldDatePicker = new FieldDatePicker(new FieldDatePickerViewImpl());
            fieldDatePicker.setValue(field.getExpected());

            fieldDatePicker.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    field.setExpected(event.getValue());
                }
            });

            panel.add(fieldDatePicker);
        } else {
            Map<String, String> currentValueMap = new HashMap<String, String>();
            // TODO fill currentValueMap with values of other VerifyFields (if any)
            DropDownData dropDownData = oracle.getEnums(factType,
                                                        field.getFieldName(),
                                                        currentValueMap);
            if (Objects.nonNull(dropDownData)) {
                //GUVNOR-1324: Java enums are of type TYPE_COMPARABLE whereas Guvnor enums are not.
                //The distinction here controls whether the EXPECTED value is handled as a true
                //Java enum or a literal with a selection list (i.e. Guvnor enum)
                if (Objects.equals(flType, DataType.TYPE_COMPARABLE)) {
                    field.setNature(FieldData.TYPE_ENUM);
                } else {
                    field.setNature(FieldData.TYPE_LITERAL);
                }

                panel.add(new EnumDropDown(field.getExpected(),
                                           new DropDownValueChanged() {
                                               public void valueChanged(String newText,
                                                                        String newValue) {
                                                   callback.callback(newValue);
                                               }
                                           },
                                           dropDownData,
                                           oracle.getResourcePath()));
            } else {
                if (field.getNature() == FieldData.TYPE_UNDEFINED) {
                    if (Objects.nonNull(field.getExpected()) && field.getExpected().indexOf('=') == 0) {
                        field.setNature(FieldData.TYPE_VARIABLE);
                    } else if (isThereABoundVariableToSet()) {
                        Image clickme = CommonAltedImages.INSTANCE.Edit();
                        clickme.addClickHandler(new ClickHandler() {

                            public void onClick(ClickEvent event) {
                                showTypeChoice(field);
                            }
                        });
                        panel.add(clickme);
                    } else {
                        field.setNature(FieldData.TYPE_LITERAL);
                    }
                }

                if (field.getNature() == FieldData.TYPE_VARIABLE) {
                    panel.add(variableEditor());
                } else if (field.getNature() == FieldData.TYPE_LITERAL) {
                    panel.add(textBoxEditor(flType).asWidget());
                }
            }
        }
    }

    IsWidget textBoxEditor(final String flType) {
        if (Objects.equals(flType, DataType.TYPE_COLLECTION)) {
            return editableCollectionTextBox(callback,
                                             flType,
                                             field.getFieldName(),
                                             field.getExpected());
        } else {
            return editableTextBox(callback,
                                   flType,
                                   field.getFieldName(),
                                   field.getExpected());
        }
    }

    private Widget variableEditor() {
        List<String> vars = this.scenario.getFactNamesInScope(this.executionTrace,
                                                              true);

        final ListBox box = new ListBox();

        if (this.field.getExpected() == null) {
            box.addItem(CommonConstants.INSTANCE.Choose());
        }
        int j = 0;
        for (int i = 0; i < vars.size(); i++) {
            String var = vars.get(i);
            FactData f = scenario.getFactTypes().get(var);
            String fieldType = oracle.getFieldType(this.factType,
                                                   field.getFieldName());
            if (f.getType().equals(fieldType)) {
                if (box.getItemCount() == 0) {
                    box.addItem("...");
                    j++;
                }
                box.addItem("=" + var);
                if (this.field.getExpected() != null && this.field.getExpected().equals("=" + var)) {
                    box.setSelectedIndex(j);
                }
                j++;
            }
        }

        box.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                field.setExpected(box.getItemText(box.getSelectedIndex()));
            }
        });

        return box;
    }

    private static EditableTextBox editableTextBox(final Callback<String> changed,
                                                   final String dataType,
                                                   final String fieldName,
                                                   final String initialValue) {
        return new EditableTextBox(changed,
                                   TextBoxFactory.getTextBox(dataType),
                                   fieldName,
                                   initialValue);
    }

    private static EditableCollectionBox editableCollectionTextBox(final Callback<String> changed,
                                                                   final String dataType,
                                                                   final String fieldName,
                                                                   final String initialValue) {
        return new EditableCollectionBox(changed,
                                         TextBoxFactory.getTextBox(dataType),
                                         fieldName,
                                         initialValue);
    }

    private void showTypeChoice(final VerifyField con) {
        final FormStylePopup form = new FormStylePopup(TestScenarioAltedImages.INSTANCE.Wizard(),
                                                       TestScenarioConstants.INSTANCE.FieldValue());

        Button lit = new Button(TestScenarioConstants.INSTANCE.LiteralValue());
        lit.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                con.setNature(FieldData.TYPE_LITERAL);
                doTypeChosen(form);
            }
        });
        form.addAttribute(TestScenarioConstants.INSTANCE.LiteralValue() + ":",
                          widgets(lit,
                                  new InfoPopup(TestScenarioConstants.INSTANCE.LiteralValue(),
                                                TestScenarioConstants.INSTANCE.LiteralValTip())));

        form.addRow(new HTML("<hr/>"));
        form.addRow(new SmallLabel(TestScenarioConstants.INSTANCE.AdvancedOptions()));

        // If we are here, then there must be a bound variable compatible with me
        Button variable = new Button(TestScenarioConstants.INSTANCE.BoundVariable());
        variable.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                con.setNature(FieldData.TYPE_VARIABLE);
                doTypeChosen(form);
            }
        });
        form.addAttribute(TestScenarioConstants.INSTANCE.AVariable(),
                          widgets(variable,
                                  new InfoPopup(TestScenarioConstants.INSTANCE.ABoundVariable(),
                                                TestScenarioConstants.INSTANCE.BoundVariableTip())));

        form.show();
    }

    private boolean isThereABoundVariableToSet() {
        boolean retour = false;
        List<String> vars = this.scenario.getFactNamesInScope(executionTrace,
                                                              true);
        if (vars.size() > 0) {
            for (int i = 0; i < vars.size(); i++) {
                String var = vars.get(i);
                FactData f = scenario.getFactTypes().get(var);
                String fieldType = oracle.getFieldType(this.factType,
                                                       field.getFieldName());
                if (f.getType().equals(fieldType)) {
                    retour = true;
                    break;
                }
            }
        }
        return retour;
    }

    private void doTypeChosen(final FormStylePopup form) {
        refreshEditor();
        form.hide();
    }

    private Panel widgets(final IsWidget left,
                          final IsWidget right) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add(left);
        panel.add(right);
        panel.setWidth("100%");
        return panel;
    }
}
