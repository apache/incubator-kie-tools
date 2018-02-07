/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.widget;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.drools.workbench.models.datamodel.rule.DSLSentence;
import org.drools.workbench.models.datamodel.rule.DSLVariableValue;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.kie.soup.project.datamodel.oracle.DropDownData;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;

public class DSLDropDown extends Composite
        implements
        DSLSentenceWidget.DSLVariableEditor {

    private RuleModeller ruleModeller;
    private AsyncPackageDataModelOracle oracle;
    private EnumDropDown resultWidget = null;
    private String factType;
    private String factField;
    private DSLSentence dslSentence;
    private DSLVariableValue selectedValue;
    private Callback<DSLDropDown> updateEnumDropDownsCallback;

    public DSLDropDown(final RuleModeller ruleModeller,
                       final String variableDef,
                       final DSLSentence dslSentence,
                       final DSLVariableValue value,
                       final boolean multipleSelect,
                       final Callback<DSLDropDown> updateEnumDropDownsCallback) {

        this.ruleModeller = ruleModeller;
        this.oracle = ruleModeller.getDataModelOracle();
        this.dslSentence = dslSentence;
        this.updateEnumDropDownsCallback = updateEnumDropDownsCallback;

        //Parse Fact Type and Field for retrieving DropDown data from Suggestion Completion Engine
        //Format for the drop-down definition within a DSLSentence is <varName>:<type>:<Fact.field>
        int lastIndex = variableDef.lastIndexOf(":");
        String factAndField = variableDef.substring(lastIndex + 1,
                                                    variableDef.length());
        int dotIndex = factAndField.indexOf(".");
        factType = factAndField.substring(0,
                                          dotIndex);
        factField = factAndField.substring(dotIndex + 1,
                                           factAndField.length());
        selectedValue = value;

        //ChangeHandler for drop-down; not called when initialising the drop-down
        DropDownValueChanged handler = new DropDownValueChanged() {

            public void valueChanged(String newText,
                                     String newValue) {
                if (selectedValue.getValue().equals(newValue)) {
                    return;
                }

                selectedValue = new DSLVariableValue(newValue);

                //When the value changes we need to reset the content of *ALL* DSLSentenceWidget drop-downs.
                //An improvement would be to determine the chain of dependent drop-downs and only update
                //children of the one whose value changes. However in reality DSLSentences only contain
                //a couple of drop-downs so it's quicker to simply update them all.
                updateEnumDropDownsCallback.callback(DSLDropDown.this);
            }
        };

        DropDownData dropDownData = getDropDownData();
        resultWidget = new EnumDropDown(value.getValue(),
                                        handler,
                                        dropDownData,
                                        multipleSelect,
                                        ruleModeller.getPath());

        //Wrap widget within a HorizontalPanel to add a space before and after the Widget
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new HTML("&nbsp;"));
        hp.add(resultWidget);
        hp.add(new HTML("&nbsp;"));

        initWidget(hp);
    }

    public DSLVariableValue getSelectedValue() {
        return selectedValue;
    }

    public void refreshDropDownData() {
        resultWidget.setDropDownData(selectedValue.getValue(),
                                     getDropDownData());
    }

    DropDownData getDropDownData() {
        DropDownData dropDownData = oracle.getEnums(factType,
                                                    factField,
                                                    dslSentence.getEnumFieldValueMap());
        return dropDownData;
    }
}