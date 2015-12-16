/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.Fact;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.gwtbootstrap3.client.ui.ListBox;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;

/**
 * Constraint editor for the FieldData in the Given Section
 */
public class FieldDataConstraintEditor
        extends Composite
        implements
        HasValueChangeHandlers<String>,
        ScenarioParentWidget {

    private FieldData field;
    private IsWidget valueEditorWidget;
    private final Panel panel = new SimplePanel();
    private final FieldConstraintHelper helper;

    private List<FieldDataConstraintEditor> dependentEnumEditors = new ArrayList<FieldDataConstraintEditor>();

    public FieldDataConstraintEditor( final String factType,
                                      final FieldData field,
                                      final Fact givenFact,
                                      final AsyncPackageDataModelOracle oracle,
                                      final Scenario scenario,
                                      final ExecutionTrace executionTrace ) {
        this.field = field;
        this.helper = new FieldConstraintHelper( scenario,
                                                 executionTrace,
                                                 oracle,
                                                 factType,
                                                 field,
                                                 givenFact );
        renderEditor();
        initWidget( panel );
    }

    @Override
    public void renderEditor() {
        final String flType = helper.getFieldType();
        panel.clear();

        if ( flType != null && flType.equals( DataType.TYPE_BOOLEAN ) ) {
            addBooleanEditor();

        } else if ( flType != null && flType.equals( DataType.TYPE_DATE ) ) {
            addDateEditor();

        } else {
            final DropDownData dropDownData = helper.getEnums();

            if ( dropDownData != null ) {
                addDropDownEditor( dropDownData );
            } else {

                setFieldNatureIfItIsWasNotSetBefore();

                if ( field.getNature() == FieldData.TYPE_UNDEFINED && ( helper.isThereABoundVariableToSet() == true || helper.isItAList() == true ) ) {
                    addFieldSelectorWidget();
                } else if ( isFieldVariable() ) {
                    addVariableEditor();
                } else {
                    addDropDownEditor( null );
                }
            }
        }

    }

    private void addDateEditor() {
        valueEditorWidget = dateEditor();
        panel.add( valueEditorWidget );
    }

    private void addBooleanEditor() {
        valueEditorWidget = booleanEditor();
        panel.add( valueEditorWidget );
    }

    private void addDropDownEditor( final DropDownData dropDownData ) {
        field.setNature( FieldData.TYPE_ENUM );
        valueEditorWidget = dropDownEditor( dropDownData );
        panel.add( valueEditorWidget );
    }

    private void setFieldNatureIfItIsWasNotSetBefore() {
        if ( field.getValue() != null && field.getValue().length() > 0 && field.getNature() == FieldData.TYPE_UNDEFINED ) {
            if ( field.getValue().length() > 1 && field.getValue().charAt( 1 ) == '[' && field.getValue().charAt( 0 ) == '=' ) {
                field.setNature( FieldData.TYPE_LITERAL );
            } else if ( field.getValue().charAt( 0 ) == '=' ) {
                field.setNature( FieldData.TYPE_VARIABLE );
            } else {
                field.setNature( FieldData.TYPE_LITERAL );
            }
        }
    }

    private void addVariableEditor() {
        valueEditorWidget = variableEditor();
        panel.add( valueEditorWidget );
    }

    private void addFieldSelectorWidget() {
        helper.setParentIsAList( true );
        valueEditorWidget = new FieldSelectorWidget( field,
                                                     helper,
                                                     this );
        panel.add( valueEditorWidget );
    }

    private boolean isFieldVariable() {
        return field.getNature() == FieldData.TYPE_VARIABLE;
    }

    private EnumDropDown booleanEditor() {
        return new EnumDropDown( field.getValue(),
                                 new DropDownValueChanged() {
                                     public void valueChanged( String newText,
                                                               String newValue ) {
                                         valueHasChanged( newValue );
                                     }

                                 },
                                 DropDownData.create( new String[]{ "true", "false" } ),
                                 helper.getDataModelOracle().getResourcePath() );
    }

    private EnumDropDown dropDownEditor( final DropDownData dropDownData ) {
        return new EnumDropDown( field.getValue(),
                                 new DropDownValueChanged() {
                                     public void valueChanged( String newText,
                                                               String newValue ) {
                                         valueHasChanged( newValue );
                                         for ( FieldDataConstraintEditor dependentEnumEditor : dependentEnumEditors ) {
                                             dependentEnumEditor.refreshDropDownData();
                                         }
                                     }
                                 },
                                 dropDownData,
                                 helper.getDataModelOracle().getResourcePath() );

    }

    private Widget dateEditor() {

        FieldDatePicker fieldDatePicker = new FieldDatePicker( new FieldDatePickerViewImpl() );

        fieldDatePicker.setValue( field.getValue() );

        fieldDatePicker.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                field.setValue( event.getValue() );
            }
        } );
        return fieldDatePicker.asWidget();
    }

    private Widget variableEditor() {
        List<String> vars = helper.getFactNamesInScope();

        final ListBox box = new ListBox();

        if ( this.field.getValue() == null ) {
            box.addItem( TestScenarioConstants.INSTANCE.Choose() );
        }
        int j = 0;
        for ( String var : vars ) {
            if ( helper.getFactTypeByVariableName( var ).getType().equals( helper.resolveFieldType() ) ) {
                if ( box.getItemCount() == 0 ) {
                    box.addItem( "..." );
                    j++;
                }
                box.addItem( "=" + var );
                if ( this.field.getValue() != null && this.field.getValue().equals( "=" + var ) ) {
                    box.setSelectedIndex( j );

                }
                j++;
            }
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                field.setValue( box.getItemText( box.getSelectedIndex() ) );
                valueHasChanged( field.getValue() );
            }
        } );

        return box;
    }

    private void valueHasChanged( final String newValue ) {
        ValueChangeEvent.fire( this,
                               newValue );
    }

    @Override
    public HandlerRegistration addValueChangeHandler( final ValueChangeHandler<String> stringValueChangeHandler ) {
        return addHandler( stringValueChangeHandler,
                           ValueChangeEvent.getType() );
    }

    public void addIfDependentEnumEditor( final FieldDataConstraintEditor candidateDependentEnumEditor ) {
        if ( helper.isDependentEnum( candidateDependentEnumEditor.helper ) ) {
            dependentEnumEditors.add( candidateDependentEnumEditor );
        }
    }

    private void refreshDropDownData() {
        if ( this.valueEditorWidget instanceof EnumDropDown ) {
            final EnumDropDown dropdown = (EnumDropDown) this.valueEditorWidget;
            dropdown.setDropDownData( field.getValue(),
                                      helper.getEnums() );
        }
    }

}
