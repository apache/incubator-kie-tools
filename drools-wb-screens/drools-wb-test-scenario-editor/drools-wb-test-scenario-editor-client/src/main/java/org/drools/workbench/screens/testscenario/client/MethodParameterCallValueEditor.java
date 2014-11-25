/*
 * Copyright 2010 JBoss Inc
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

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.rule.FieldNature;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.testscenarios.shared.CallFieldValue;
import org.drools.workbench.models.testscenarios.shared.ExecutionTrace;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.FieldData;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.drools.workbench.screens.testscenario.client.resources.i18n.TestScenarioConstants;
import org.drools.workbench.screens.testscenario.client.resources.images.TestScenarioAltedImages;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.CommonAltedImages;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

/**
 * This provides for editing of fields in the RHS of a rule.
 */
public class MethodParameterCallValueEditor extends Composite {

    private CallFieldValue methodParameter;
    private DropDownData enums;
    private SimplePanel root;
    private Scenario model = null;
    private String parameterType = null;
    private AsyncPackageDataModelOracle oracle;
    private ExecutionTrace ex;

    public MethodParameterCallValueEditor( final CallFieldValue val,
                                           final DropDownData enums,
                                           final ExecutionTrace ex,
                                           final Scenario model,
                                           final String parameterType,
                                           final AsyncPackageDataModelOracle oracle ) {
        if ( val.type.equals( DataType.TYPE_BOOLEAN ) ) {
            this.enums = DropDownData.create( new String[]{ "true", "false" } );
        } else {
            this.enums = enums;
        }
        this.root = new SimplePanel();
        this.ex = ex;
        this.methodParameter = val;
        this.model = model;
        this.parameterType = parameterType;
        this.oracle = oracle;

        refresh();
        initWidget( root );
    }

    private void refresh() {
        root.clear();
        if ( enums != null && ( enums.getFixedList() != null || enums.getQueryExpression() != null ) ) {
            root.add( new EnumDropDown( methodParameter.value,
                                        new DropDownValueChanged() {
                                            public void valueChanged( String newText,
                                                                      String newValue ) {
                                                methodParameter.value = newValue;
                                            }
                                        },
                                        enums,
                                        oracle.getResourcePath() ) );
        } else {

            if ( methodParameter.nature == FieldNatureType.TYPE_UNDEFINED ) {
                // we have a blank slate..
                // have to give them a choice
                root.add( choice() );
            } else {
                if ( methodParameter.nature == FieldNatureType.TYPE_VARIABLE ) {
                    ListBox list = boundVariable( methodParameter );
                    root.add( list );
                } else {
                    TextBox box = boundTextBox( this.methodParameter );
                    root.add( box );
                }

            }

        }
    }

    private ListBox boundVariable( final FieldNature c ) {
        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then propose a list
         */
        final ListBox listVariable = new ListBox();
        List<String> vars = model.getFactNamesInScope( ex,
                                                       true );
        for ( String v : vars ) {
            FactData factData = (FactData) model.getFactTypes().get( v );
            if ( factData.getType().equals( this.methodParameter.type ) ) {
                // First selection is empty
                if ( listVariable.getItemCount() == 0 ) {
                    listVariable.addItem( "..." );
                }

                listVariable.addItem( "=" + v );
            }
        }
        if ( methodParameter.value.equals( "=" ) ) {
            listVariable.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listVariable.getItemCount(); i++ ) {
                if ( listVariable.getItemText( i ).equals( methodParameter.value ) ) {
                    listVariable.setSelectedIndex( i );
                }
            }
        }
        if ( listVariable.getItemCount() > 0 ) {

            listVariable.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    methodParameter.value = listVariable.getValue( listVariable.getSelectedIndex() );
                    refresh();
                }
            } );

        }
        return listVariable;
    }

    private TextBox boundTextBox( final CallFieldValue c ) {

        final TextBox box = TextBoxFactory.getTextBox( methodParameter.type );
        box.setStyleName( "constraint-value-Editor" );
        if ( c.value == null ) {
            box.setText( "" );
        } else {
            if ( c.value.trim().equals( "" ) ) {
                c.value = "";
            }
            box.setText( c.value );
        }

        if ( c.value == null || c.value.length() < 5 ) {
            box.setVisibleLength( 6 );
        } else {
            box.setVisibleLength( c.value.length() - 1 );
        }

        box.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange( final ValueChangeEvent<String> event ) {
                c.value = event.getValue();
            }

        } );

        box.addKeyUpHandler( new KeyUpHandler() {

            public void onKeyUp( KeyUpEvent event ) {
                box.setVisibleLength( box.getText().length() );
            }
        } );

        return box;
    }

    private Widget choice() {
        Image clickme = CommonAltedImages.INSTANCE.Edit();
        clickme.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                showTypeChoice( (Widget) event.getSource() );
            }
        } );
        return clickme;
    }

    protected void showTypeChoice( final Widget w ) {
        final FormStylePopup form = new FormStylePopup( TestScenarioAltedImages.INSTANCE.Wizard(),
                                                        TestScenarioConstants.INSTANCE.FieldValue() );
        Button lit = new Button( TestScenarioConstants.INSTANCE.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                methodParameter.nature = FieldData.TYPE_LITERAL;
                methodParameter.value = " ";
                refresh();
                form.hide();
            }

        } );
        form.addAttribute( TestScenarioConstants.INSTANCE.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( TestScenarioConstants.INSTANCE.Literal(),
                                                   TestScenarioConstants.INSTANCE.LiteralValTip() ) ) );
        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( TestScenarioConstants.INSTANCE.AdvancedSection() ) );

        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then show a button
         */

        List<String> vars = model.getFactNamesInScope( ex,
                                                       true );
        for ( String v : vars ) {
            boolean createButton = false;
            Button variable = new Button( TestScenarioConstants.INSTANCE.BoundVariable() );
            FactData factData = (FactData) model.getFactTypes().get( v );
            if ( factData.getType().equals( this.parameterType ) ) {
                createButton = true;
            }
            if ( createButton == true ) {
                form.addAttribute( TestScenarioConstants.INSTANCE.BoundVariable() + ":",
                                   variable );
                variable.addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        methodParameter.nature = FieldData.TYPE_VARIABLE;
                        methodParameter.value = "=";
                        refresh();
                        form.hide();
                    }

                } );
                break;
            }

        }
        form.show();
    }

    private Widget widgets( final Button lit,
                            final InfoPopup popup ) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( lit );
        h.add( popup );
        return h;
    }

}
