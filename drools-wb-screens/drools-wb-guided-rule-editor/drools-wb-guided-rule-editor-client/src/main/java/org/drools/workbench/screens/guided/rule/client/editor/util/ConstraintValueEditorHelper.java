/*
 * Copyright 2014 JBoss Inc
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

package org.drools.workbench.screens.guided.rule.client.editor.util;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.HasOperator;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.CEPOracle;
import org.uberfire.client.callbacks.Callback;

public class ConstraintValueEditorHelper {

    private final RuleModel model;
    private final AsyncPackageDataModelOracle oracle;
    private final String factType;
    private final String fieldName;
    private final BaseSingleFieldConstraint constraint;
    private final String fieldType;
    private final DropDownData dropDownData;

    public ConstraintValueEditorHelper(
            RuleModel model,
            AsyncPackageDataModelOracle oracle,
            String factType,
            String fieldName,
            BaseSingleFieldConstraint constraint,
            String fieldType,
            DropDownData dropDownData) {
        this.model = model;
        this.oracle = oracle;
        this.factType = factType;
        this.fieldName = fieldName;
        this.constraint = constraint;
        this.fieldType = fieldType;
        this.dropDownData = dropDownData;
    }

    public void isBoundVariableApplicable(String boundVariable, Callback<Boolean> callback) {

        if (isBoundVariableApplicableByField(boundVariable, callback)) {
            isBoundVariableApplicableByFactType(boundVariable, callback);
        }

    }

    public void isApplicableBindingsInScope(final String binding,
            final Callback<Boolean> callback) {

        //LHS FactPattern
        FactPattern fp = model.getLHSBoundFact(binding);
        if (fp != null) {
            isLHSFactTypeEquivalent(binding,
                    callback);
        }

        //LHS FieldConstraint
        isBoundVariableApplicableByField(binding,callback);
    }

    private boolean isBoundVariableApplicableByField(String boundVariable, Callback<Boolean> callback) {
        SingleFieldConstraint lhsBoundField = this.model.getLHSBoundField(boundVariable);

        if (lhsBoundField != null) {
            String boundClassName = this.oracle.getFieldClassName(lhsBoundField.getFactType(), lhsBoundField.getFieldName());

            if (getFieldTypeClazz().equals(boundClassName)) {
                callback.callback(true);
                return true;
            }

            this.oracle.getSuperType(boundClassName,new Callback<String>() {
                @Override public void callback(String result) {

                }
            });
        }


            isLHSFieldTypeEquivalent(boundVariable,
                    callback);

        return false;
    }

    private void isBoundVariableApplicableByFactType(final String boundVariable,
            final Callback<Boolean> callback) {
        FactPattern lhsBoundFact = model.getLHSBoundFact(boundVariable);

        if (lhsBoundFact != null) {
            String boundFactType = lhsBoundFact.getFactType();

            //For collection, present the list of possible bound variable
            String factCollectionType = oracle.getParametricFieldType(this.factType,
                    this.fieldName);
            if (boundFactType != null && factCollectionType != null && boundFactType.equals(factCollectionType)) {
                callback.callback(true);
                return;
            }

        }
    }

    private void isBoundVariableApplicableByFieldType(String boundFieldType,
            final Callback<Boolean> callback) {

        //'this' can be compared to bound events if using a CEP operator
        if (this.fieldName.equals(DataType.TYPE_THIS)) {
            oracle.isFactTypeAnEvent(boundFieldType,
                    new Callback<Boolean>() {
                        @Override
                        public void callback(final Boolean result) {
                            if (Boolean.TRUE.equals(result)) {
                                if (constraint instanceof HasOperator) {
                                    HasOperator hop = (HasOperator) constraint;
                                    if (CEPOracle.isCEPOperator(hop.getOperator())) {
                                        callback.callback(true);
                                        return;
                                    }
                                }
                            }
                        }
                    });
        }

        //'this' can be compared to bound Dates if using a CEP operator
        if (this.fieldName.equals(DataType.TYPE_THIS) && boundFieldType.equals(DataType.TYPE_DATE)) {
            if (this.constraint instanceof HasOperator) {
                HasOperator hop = (HasOperator) this.constraint;
                if (CEPOracle.isCEPOperator(hop.getOperator())) {
                    callback.callback(true);
                    return;
                }
            }
        }

        //Dates can be compared to bound events if using a CEP operator
        if (this.fieldType.equals(DataType.TYPE_DATE)) {
            oracle.isFactTypeAnEvent(boundFieldType,
                    new Callback<Boolean>() {
                        @Override
                        public void callback(final Boolean result) {
                            if (Boolean.TRUE.equals(result)) {
                                if (constraint instanceof HasOperator) {
                                    HasOperator hop = (HasOperator) constraint;
                                    if (CEPOracle.isCEPOperator(hop.getOperator())) {
                                        callback.callback(true);
                                        return;
                                    }
                                }
                            }
                        }
                    });
        }

        //For collection, present the list of possible bound variable
        String factCollectionType = oracle.getParametricFieldType(this.factType,
                this.fieldName);
        if (factCollectionType != null && factCollectionType.equals(boundFieldType)) {
            callback.callback(true);
            return;
        }

        callback.callback(false);
    }

    private void isLHSFieldTypeEquivalent(final String boundVariable,
            final Callback<Boolean> callback) {

        String boundFieldType = this.model.getLHSBindingType(boundVariable);

        //If the fieldTypes are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if (boundFieldType.equals(DataType.TYPE_COMPARABLE)) {
            if (!this.fieldType.equals(DataType.TYPE_COMPARABLE)) {
                callback.callback(false);
                return ;
            }
            FieldConstraint fc = this.model.getLHSBoundField(boundVariable);
            if (fc instanceof SingleFieldConstraint) {
                String fieldName = ((SingleFieldConstraint) fc).getFieldName();
                String parentFactTypeForBinding = this.model.getLHSParentFactPatternForBinding(boundVariable).getFactType();
                String[] dd = this.oracle.getEnumValues(parentFactTypeForBinding,
                        fieldName);
                callback.callback(isEnumEquivalent(dd));
                return;
            }
            callback.callback(false);
            return;
        }

        isBoundVariableApplicableByFieldType(boundVariable,
                callback);
    }

    private boolean isEnumEquivalent(String[] values) {
        if (values == null && this.dropDownData.getFixedList() != null) {
            return false;
        }
        if (values != null && this.dropDownData.getFixedList() == null) {
            return false;
        }
        if (values.length != this.dropDownData.getFixedList().length) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (!values[i].equals(this.dropDownData.getFixedList()[i])) {
                return false;
            }
        }
        return true;
    }

    private void isLHSFactTypeEquivalent(final String boundVariable,
            final Callback<Boolean> callback) {
        String boundFactType = model.getLHSBoundFact(boundVariable).getFactType();

        if (getFieldTypeClazz().equals(boundFactType)) {
            callback.callback(true);
            return;
        }

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if (boundFactType.equals(DataType.TYPE_COMPARABLE)) {
            if (!this.fieldType.equals(DataType.TYPE_COMPARABLE)) {
                callback.callback(false);
                return;
            }
            String[] dd = this.oracle.getEnumValues(boundFactType,
                    this.fieldName);
            callback.callback(isEnumEquivalent(dd));
            return;
        }

        isBoundVariableApplicable(boundVariable,
                callback);
    }

    private String getFieldTypeClazz() {
        String fieldClassName = oracle.getFieldClassName(factType, fieldName);
        return fieldClassName;
    }
}
