/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.client.editor.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.DropDownData;
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

    public void isBoundVariableApplicable(final String boundVariable,
                                          final Callback<Boolean> callback) {

        isBoundVariableApplicableByField(boundVariable, new Callback<Boolean>() {
            @Override
            public void callback(Boolean result) {
                if (result) {
                    callback.callback(true);
                    return;
                } else {
                    isBoundVariableApplicableByFactType(boundVariable,
                                                        callback);
                }
            }
        });
    }

    public void isApplicableBindingsInScope(final String binding,
                                            final Callback<Boolean> callback) {

        //LHS FactPattern
        isLHSFactTypeEquivalent(binding,
                                new Callback<Boolean>() {
                                    @Override
                                    public void callback(Boolean result) {
                                        if (result) {
                                            callback.callback(true);
                                            return;
                                        } else {
                                            //LHS FieldConstraint
                                            isBoundVariableApplicableByField(binding,
                                                                             callback);
                                        }
                                    }
                                });
    }

    private void isBoundVariableApplicableByField(final String boundVariable,
                                                  final Callback<Boolean> callback) {

        isLHSFieldTypeEquivalent(boundVariable,
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(Boolean result) {
                                         if (result) {
                                             callback.callback(true);
                                         } else {
                                             SingleFieldConstraint lhsBoundField = model.getLHSBoundField(boundVariable);

                                             if (lhsBoundField != null) {
                                                 final String boundClassName = oracle.getFieldClassName(lhsBoundField.getFactType(), lhsBoundField.getFieldName());

                                                 if (Objects.equals(getFieldTypeClazz(), boundClassName)) {
                                                     callback.callback(true);
                                                     return;
                                                 }

                                                 oracle.getSuperTypes(boundClassName, new Callback<List<String>>() {
                                                     @Override
                                                     public void callback(List<String> superTypes) {
                                                         callback.callback(checkSuperTypes(superTypes));
                                                         return;
                                                     }
                                                 });
                                             } else {
                                                 callback.callback(false);
                                             }
                                         }
                                     }
                                 });
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
        callback.callback(false);
    }

    private void isBoundVariableApplicableByFieldType(final String boundFieldType,
                                                      final Callback<Boolean> callback) {

        //'this' can be compared to bound events if using a CEP operator
        if (this.fieldName.equals(DataType.TYPE_THIS)) {
            oracle.isFactTypeAnEvent(boundFieldType,
                                     new Callback<Boolean>() {
                                         @Override
                                         public void callback(final Boolean result) {
                                             if (Boolean.TRUE.equals(result)) {
                                                 oracle.isFactTypeAnEvent(fieldType,
                                                                          new Callback<Boolean>() {
                                                                              @Override
                                                                              public void callback(final Boolean result) {
                                                                                  if (Boolean.TRUE.equals(result)) {
                                                                                      if (CEPOracle.isCEPOperator(constraint.getOperator())) {
                                                                                          callback.callback(true);
                                                                                          return;
                                                                                      }
                                                                                  } else {
                                                                                      callback.callback(false);
                                                                                      return;
                                                                                  }
                                                                              }
                                                                          });
                                             } else {
                                                 callback.callback(false);
                                                 return;
                                             }
                                         }
                                     });
        }

        //'this' can be compared to bound Dates if using a CEP operator
        if (this.fieldName.equals(DataType.TYPE_THIS) && boundFieldType.equals(DataType.TYPE_DATE)) {
            if (CEPOracle.isCEPOperator(constraint.getOperator())) {
                callback.callback(true);
                return;
            }
        }

        //Dates can be compared to bound events if using a CEP operator
        if (this.fieldType.equals(DataType.TYPE_DATE)) {
            oracle.isFactTypeAnEvent(boundFieldType,
                                     new Callback<Boolean>() {
                                         @Override
                                         public void callback(final Boolean result) {
                                             if (Boolean.TRUE.equals(result)) {
                                                 if (CEPOracle.isCEPOperator(constraint.getOperator())) {
                                                     callback.callback(true);
                                                     return;
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
        if (Objects.equals(boundFieldType, DataType.TYPE_COMPARABLE)) {
            if (!Objects.equals(fieldType, DataType.TYPE_COMPARABLE)) {
                callback.callback(false);
                return;
            }
            SingleFieldConstraint fc = this.model.getLHSBoundField(boundVariable);
            String fieldName = fc.getFieldName();
            String parentFactTypeForBinding = this.model.getLHSParentFactPatternForBinding(boundVariable).getFactType();
            String[] dd = this.oracle.getEnumValues(parentFactTypeForBinding,
                                                    fieldName);
            callback.callback(isEnumEquivalent(dd, dropDownData));
            return;
        }

        isBoundVariableApplicableByFieldType(boundFieldType,
                                             callback);
    }

    public static boolean isEnumEquivalent(final String[] values, final DropDownData dropDownData) {
        if (dropDownData != null) {
            return Arrays.equals(values, dropDownData.getFixedList());
        } else {
            // dropDownData is null, so check if also values are null
            return values == null;
        }
    }

    private void isLHSFactTypeEquivalent(final String boundVariable,
                                         final Callback<Boolean> callback) {
        FactPattern factPattern = model.getLHSBoundFact(boundVariable);

        if (factPattern == null) {
            callback.callback(false);
            return;
        }

        //Both types are identical
        final String boundFactType = factPattern.getFactType();
        final String fieldType = getFieldTypeClazz();
        if (fieldType.equals(boundFactType)) {
            callback.callback(true);
            return;
        } else {
            isLHSFactTypeAnEvent(boundVariable,
                                 boundFactType,
                                 fieldType,
                                 callback);
        }
    }

    //Both types are events
    private void isLHSFactTypeAnEvent(final String boundVariable,
                                      final String boundFactType,
                                      final String fieldType,
                                      final Callback<Boolean> callback) {
        oracle.isFactTypeAnEvent(boundFactType,
                                 new Callback<Boolean>() {
                                     @Override
                                     public void callback(final Boolean result) {
                                         if (Boolean.TRUE.equals(result)) {
                                             oracle.isFactTypeAnEvent(fieldType,
                                                                      new Callback<Boolean>() {
                                                                          @Override
                                                                          public void callback(final Boolean result) {
                                                                              if (Boolean.TRUE.equals(result)) {
                                                                                  if (CEPOracle.isCEPOperator(constraint.getOperator())) {
                                                                                      callback.callback(true);
                                                                                      return;
                                                                                  } else {
                                                                                      isLHSFactTypeAssignable(boundVariable,
                                                                                                              boundFactType,
                                                                                                              callback);
                                                                                  }
                                                                              } else {
                                                                                  isLHSFactTypeAssignable(boundVariable,
                                                                                                          boundFactType,
                                                                                                          callback);
                                                                              }
                                                                          }
                                                                      });
                                         } else {
                                             isLHSFactTypeAssignable(boundVariable,
                                                                     boundFactType,
                                                                     callback);
                                         }
                                     }
                                 });
    }

    private void isLHSFactTypeAssignable(final String boundVariable,
                                         final String boundFactType,
                                         final Callback<Boolean> callback) {
        if (boundFactType.equals(DataType.TYPE_COMPARABLE)) {
            //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
            if (!fieldType.equals(DataType.TYPE_COMPARABLE)) {
                callback.callback(false);
                return;
            }
            String[] dd = oracle.getEnumValues(boundFactType,
                                               fieldName);
            callback.callback(isEnumEquivalent(dd, dropDownData));
        } else {

            this.oracle.getSuperTypes(boundFactType, new Callback<List<String>>() {
                @Override
                public void callback(List<String> superTypes) {
                    if (checkSuperTypes(superTypes)) {
                        callback.callback(true);
                        return;
                    } else {
                        isBoundVariableApplicable(boundVariable,
                                                  callback);
                    }
                }
            });
        }
    }

    private boolean checkSuperTypes(List<String> superTypes) {
        if (superTypes != null) {
            for (String superType : superTypes) {
                if (Objects.equals(getFieldTypeClazz(), superType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getFieldTypeClazz() {
        String fieldClassName = oracle.getFieldClassName(factType, fieldName);
        return fieldClassName;
    }
}
