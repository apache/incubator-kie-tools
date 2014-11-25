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
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.models.testscenarios.shared.CollectionFieldData;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fact;
import org.drools.workbench.models.testscenarios.shared.FactAssignmentField;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.FieldPlaceHolder;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import com.google.gwt.user.client.ui.FlexTable;
import org.uberfire.ext.widgets.common.client.common.ImageButton;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;

public class FactDataWidgetFactory {

    private final FlexTable widget;
    private final Scenario scenario;
    private final AsyncPackageDataModelOracle oracle;
    private final FixtureList definitionList;
    private final ExecutionTrace executionTrace;

    private final RowIndexByFieldName rowIndexByFieldName = new RowIndexByFieldName();
    private int col = 0;
    private final ScenarioParentWidget parent;

    public FactDataWidgetFactory( final Scenario scenario,
                                  final AsyncPackageDataModelOracle oracle,
                                  final FixtureList definitionList,
                                  final ExecutionTrace executionTrace,
                                  final ScenarioParentWidget parent,
                                  final FlexTable widget ) {
        this.scenario = scenario;
        this.oracle = oracle;
        this.definitionList = definitionList;
        this.executionTrace = executionTrace;
        this.parent = parent;
        this.widget = widget;
    }

    public void build( final String headerText,
                       final Fact fact ) {

        if ( fact instanceof FactData ) {
            FactData factData = (FactData) fact;
            widget.setWidget( 0,
                              ++col,
                              new SmallLabel( "[" + factData.getName() + "]" ) );
        } else {
            col++;
        }

        widget.setWidget(
                0,
                0,
                new ClickableLabel( headerText,
                                    createAddFieldButton( fact ) ) );

        Map<FieldData, FieldDataConstraintEditor> enumEditorMap
                = new HashMap<FieldData, FieldDataConstraintEditor>();
        // Sets row name and delete button.
        for ( final Field field : fact.getFieldData() ) {
            // Avoid duplicate field rows, only one for each name.
            if ( rowIndexByFieldName.doesNotContain( field.getName() ) ) {
                newRow( fact, field.getName() );
            }

            // Sets row data
            int fieldRowIndex = rowIndexByFieldName.getRowIndex( field.getName() );
            IsWidget editableCell = editableCell(
                    field,
                    fact,
                    fact.getType() );
            widget.setWidget( fieldRowIndex,
                              col,
                              editableCell );
            if ( field instanceof FieldData ) {
                FieldData fieldData = (FieldData) field;
                if ( fieldData.getNature() == FieldData.TYPE_ENUM ) {
                    enumEditorMap.put( fieldData, (FieldDataConstraintEditor) editableCell );
                }
            }
        }
        for ( FieldDataConstraintEditor outerEnumEditor : enumEditorMap.values() ) {
            for ( FieldDataConstraintEditor innerEnumEditor : enumEditorMap.values() ) {
                if ( outerEnumEditor != innerEnumEditor ) {
                    outerEnumEditor.addIfDependentEnumEditor( innerEnumEditor );
                }
            }
        }

        if ( fact instanceof FactData ) {
            DeleteFactColumnButton deleteFactColumnButton = new DeleteFactColumnButton( (FactData) fact );

            widget.setWidget(
                    rowIndexByFieldName.amountOrRows() + 1,
                    col,
                    deleteFactColumnButton );
        }

    }

    private ClickHandler createAddFieldButton( final Fact fact ) {

        if ( fact instanceof FactData ) {
            return new AddFieldToFactDataClickHandler(
                    definitionList,
                    oracle,
                    parent );
        } else {
            return new AddFieldToFactClickHandler(
                    fact,
                    oracle,
                    parent );
        }
    }

    private void newRow( final Fact fact,
                         final String fieldName ) {
        rowIndexByFieldName.addRow( fieldName );

        int rowIndex = rowIndexByFieldName.getRowIndex( fieldName );

        widget.setWidget( rowIndex,
                          0,
                          createFieldNameWidget( fieldName ) );
        widget.setWidget( rowIndex,
                          definitionList.size() + 1,
                          new DeleteFieldRowButton( fact,
                                                    fieldName ) );
        widget.getCellFormatter().setHorizontalAlignment( rowIndex,
                                                          0,
                                                          HasHorizontalAlignment.ALIGN_RIGHT );
    }

    /**
     * This will provide a cell editor. It will filter non numerics, show choices etc as appropriate.
     * @param field
     * @param factType
     * @return
     */
    private IsWidget editableCell( final Field field,
                                   final Fact fact,
                                   final String factType ) {
        if ( field instanceof FieldData ) {
            FieldDataConstraintEditor fieldDataConstraintEditor = new FieldDataConstraintEditor( factType,
                                                                                                 (FieldData) field,
                                                                                                 fact,
                                                                                                 oracle,
                                                                                                 scenario,
                                                                                                 executionTrace );
            fieldDataConstraintEditor.addValueChangeHandler( new ValueChangeHandler<String>() {
                @Override
                public void onValueChange( ValueChangeEvent<String> stringValueChangeEvent ) {
                    ( (FieldData) field ).setValue( stringValueChangeEvent.getValue() );
                }
            } );
            return fieldDataConstraintEditor;
        } else if ( field instanceof CollectionFieldData ) {
            return new CollectionFieldDataConstraintEditor( factType,
                                                            (CollectionFieldData) field,
                                                            fact,
                                                            oracle,
                                                            scenario,
                                                            executionTrace );
        } else if ( field instanceof FactAssignmentField ) {
            return new FactAssignmentFieldWidget( (FactAssignmentField) field,
                                                  definitionList,
                                                  scenario,
                                                  oracle,
                                                  parent,
                                                  executionTrace );
        } else if ( field instanceof FieldPlaceHolder ) {

            return new FieldSelectorWidget(
                    field,
                    new FieldConstraintHelper( scenario,
                                               executionTrace,
                                               oracle,
                                               factType,
                                               field,
                                               fact ),
                    parent );
        }

        throw new IllegalArgumentException( "Unknown field type: " + field.getClass() );
    }

    private IsWidget createFieldNameWidget( final String fieldName ) {
        return new FieldNameWidgetImpl( fieldName );
    }

    public int amountOrRows() {
        return rowIndexByFieldName.amountOrRows();
    }

    class DeleteFactColumnButton extends ImageButton {

        public DeleteFactColumnButton( final FactData fact ) {
            super( CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   TestScenarioConstants.INSTANCE.RemoveTheColumnForScenario( fact.getName() ) );

            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    if ( scenario.isFactDataReferenced( fact ) ) {
                        Window.alert( TestScenarioConstants.INSTANCE.CanTRemoveThisColumnAsTheName0IsBeingUsed( fact.getName() ) );
                    } else if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveColumn0( fact.getName() ) ) ) {
                        scenario.removeFixture( fact );
                        definitionList.remove( fact );

                        parent.renderEditor();
                    }
                }
            } );
        }
    }

    class DeleteFieldRowButton extends ImageButton {

        public DeleteFieldRowButton( final Fact fact,
                                     final String fieldName ) {
            super( CommonAltedImages.INSTANCE.DeleteItemSmall(),
                   TestScenarioConstants.INSTANCE.RemoveThisRow() );

            addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    if ( fact instanceof FactData ) {
                        if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveRow0( fieldName ) ) ) {
                            ScenarioHelper.removeFields( definitionList,
                                                         fieldName );
                        }
                    } else if ( fact instanceof Fact ) {
                        if ( Window.confirm( TestScenarioConstants.INSTANCE.AreYouSureYouWantToRemoveRow0( fieldName ) ) ) {
                            fact.removeField( fieldName );
                        }
                    }

                    parent.renderEditor();
                }
            } );
        }
    }

    static class RowIndexByFieldName {

        private Map<String, Integer> rows = new HashMap<String, Integer>();

        public void addRow( final String fieldName ) {
            rows.put( fieldName,
                      rows.size() + 1 );
        }

        public boolean doesNotContain( final String fieldName ) {
            return !rows.containsKey( fieldName );
        }

        public Integer getRowIndex( final String fieldName ) {
            return rows.get( fieldName );
        }

        public int amountOrRows() {
            return rows.size();
        }
    }
}
