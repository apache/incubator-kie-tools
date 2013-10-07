/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.testscenario.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fact;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;

public class FieldConstraintHelper {

    private final Scenario scenario;
    private final ExecutionTrace executionTrace;
    private final AsyncPackageDataModelOracle oracle;
    private final String factType;
    private Field field;
    private final Fact fact;

    private boolean parentIsAList = false;

    public FieldConstraintHelper( final Scenario scenario,
                                  final ExecutionTrace executionTrace,
                                  final AsyncPackageDataModelOracle oracle,
                                  final String factType,
                                  final Field field,
                                  final Fact fact ) {
        this.scenario = scenario;
        this.executionTrace = executionTrace;
        this.oracle = oracle;
        this.factType = factType;
        this.field = field;
        this.fact = fact;
    }

    boolean isThereABoundVariableToSet() {
        List<?> vars = scenario.getFactNamesInScope( executionTrace, true );

        if ( vars.size() > 0 ) {
            for ( int i = 0; i < vars.size(); i++ ) {
                if ( scenario.getFactTypes().get( vars.get( i ) ).getType().equals( resolveFieldType() ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    String resolveFieldType() {
        String className = oracle.getFieldClassName( factType, field.getName() );

        if ( className == null ) {
            return null;
        } else if ( className.equals( "Collection" ) ) {
            return oracle.getParametricFieldType(
                    factType,
                    field.getName() );
        } else {
            return className;
        }
    }

    boolean isItAList() {
        String fieldType = oracle.getFieldType( factType, field.getName() );

        if ( fieldType != null && fieldType.equals( "Collection" ) ) {
            return true;
        }

        return false;
    }

    List<String> getFactNamesInScope() {
        return this.scenario.getFactNamesInScope( this.executionTrace, true );
    }

    FactData getFactTypeByVariableName( final String var ) {
        return this.scenario.getFactTypes().get( var );
    }

    DropDownData getEnums() {
        Map<String, String> currentValueMap = new HashMap<String, String>();
        for ( Field f : fact.getFieldData() ) {
            if ( f instanceof FieldData ) {
                FieldData otherFieldData = (FieldData) f;
                currentValueMap.put( otherFieldData.getName(),
                                     otherFieldData.getValue() );
            }
        }
        return oracle.getEnums(
                factType,
                field.getName(),
                currentValueMap );
    }

    String getFieldType() {
        return oracle.getFieldType( factType,
                                    field.getName() );
    }

    public FieldDataConstraintEditor createFieldDataConstraintEditor( final FieldData fieldData ) {
        return new FieldDataConstraintEditor( factType,
                                              fieldData,
                                              fact,
                                              oracle,
                                              scenario,
                                              executionTrace );
    }

    public void replaceFieldWith( final Field newField ) {
        boolean notCollection = true;
        for ( Field factsField : fact.getFieldData() ) {
            if ( factsField instanceof CollectionFieldData ) {
                CollectionFieldData fData = (CollectionFieldData) factsField;

                notCollection = false;
                List<FieldData> list = fData.getCollectionFieldList();
                boolean aNewItem = true;
                for ( FieldData aField : list ) {
                    if ( aField.getNature() == 0 ) {
                        aNewItem = false;
                        aField.setNature( ( (FieldData) newField ).getNature() );
                    }
                }
                if ( aNewItem ) {
                    list.set( list.indexOf( field ),
                              (FieldData) newField );
                }
            }

        }
        if ( notCollection ) {
            fact.getFieldData().set( fact.getFieldData().indexOf( field ),
                                     newField );
            field = newField;
        }
    }

    public boolean isDependentEnum( final FieldConstraintHelper child ) {
        if ( !fact.getType().equals( child.fact.getType() ) ) {
            return false;
        }
        return oracle.isDependentEnum( fact.getType(),
                                       field.getName(),
                                       child.field.getName() );
    }

    public boolean isTheParentAList() {
        return parentIsAList;
    }

    public void setParentIsAList( final boolean parentIsAList ) {
        this.parentIsAList = parentIsAList;
    }

}
