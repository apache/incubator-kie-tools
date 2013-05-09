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

package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.oracle.DataType;
import org.drools.workbench.models.commons.shared.rule.ActionFieldFunction;
import org.drools.workbench.models.commons.shared.rule.ActionFieldValue;
import org.drools.workbench.models.commons.shared.rule.ActionInsertFact;
import org.drools.workbench.models.commons.shared.rule.FactPattern;
import org.drools.workbench.models.commons.shared.rule.FieldNature;
import org.drools.workbench.models.commons.shared.rule.FieldNatureType;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.kie.guvnor.commons.ui.client.widget.TextBoxFactory;
import org.kie.guvnor.datamodel.model.DropDownData;
import org.uberfire.client.common.DirtyableComposite;
import org.uberfire.client.common.DropDownValueChanged;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.InfoPopup;
import org.uberfire.client.common.SmallLabel;

/**
 * This provides for editing of fields in the RHS of a rule.
 */
public class MethodParameterValueEditor
        extends DirtyableComposite {

    private ActionFieldFunction methodParameter;
    private DropDownData enums;
    private SimplePanel root;
    private RuleModeller model = null;
    private String parameterType = null;
    private Command onValueChangeCommand = null;

    public MethodParameterValueEditor( final ActionFieldFunction val,
                                       final DropDownData enums,
                                       RuleModeller model,
                                       String parameterType,
                                       Command onValueChangeCommand ) {
        if ( val.getType().equals( DataType.TYPE_BOOLEAN ) ) {
            this.enums = DropDownData.create( new String[]{ "true", "false" } );
        } else {
            this.enums = enums;
        }
        this.root = new SimplePanel();
        this.methodParameter = val;
        this.model = model;
        this.parameterType = parameterType;
        this.onValueChangeCommand = onValueChangeCommand;
        refresh();
        initWidget( root );
    }

    private void refresh() {
        root.clear();
        if ( enums != null && ( enums.getFixedList() != null || enums.getQueryExpression() != null ) ) {
            root.add( new EnumDropDown( methodParameter.getValue(),
                                        new DropDownValueChanged() {
                                            public void valueChanged( String newText,
                                                                      String newValue ) {
                                                methodParameter.setValue( newValue );
                                                if ( onValueChangeCommand != null ) {
                                                    onValueChangeCommand.execute();
                                                }
                                                makeDirty();
                                            }
                                        },
                                        enums ) );
        } else {

            if ( methodParameter.getNature() == FieldNatureType.TYPE_UNDEFINED ) {
                // we have a blank slate..
                // have to give them a choice
                root.add( choice() );
            } else {
                if ( methodParameter.getNature() == FieldNatureType.TYPE_VARIABLE ) {
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
        ListBox listVariable = new ListBox();
        List<String> vars = model.getModel().getLHSBoundFacts();
        for ( String v : vars ) {
            FactPattern factPattern = model.getModel().getLHSBoundFact( v );
            if ( factPattern.getFactType().equals( this.methodParameter.getType() ) ) {
                // First selection is empty
                if ( listVariable.getItemCount() == 0 ) {
                    listVariable.addItem( "..." );
                }

                listVariable.addItem( v );
            }
        }
        /*
         * add the bound variable of the rhs
         */
        List<String> vars2 = model.getModel().getRHSBoundFacts();
        for ( String v : vars2 ) {
            ActionInsertFact factPattern = model.getModel().getRHSBoundFact( v );
            if ( factPattern.getFactType().equals( this.methodParameter.getType() ) ) {
                // First selection is empty
                if ( listVariable.getItemCount() == 0 ) {
                    listVariable.addItem( "..." );
                }
                listVariable.addItem( v );
            }
        }
        if ( methodParameter.getValue().equals( "=" ) ) {
            listVariable.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listVariable.getItemCount(); i++ ) {
                if ( listVariable.getItemText( i ).equals( methodParameter.getValue() ) ) {
                    listVariable.setSelectedIndex( i );
                }
            }
        }
        if ( listVariable.getItemCount() > 0 ) {

            listVariable.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    ListBox w = (ListBox) event.getSource();
                    methodParameter.setValue( w.getValue( w.getSelectedIndex() ) );
                    if ( onValueChangeCommand != null ) {
                        onValueChangeCommand.execute();
                    }
                    makeDirty();
                    refresh();
                }

            } );
        }
        return listVariable;
    }

    private TextBox boundTextBox( final ActionFieldValue c ) {
        final TextBox box = TextBoxFactory.getTextBox( methodParameter.getType() );
        box.setStyleName( "constraint-value-Editor" );
        if ( c.getValue() == null ) {
            box.setText( "" );
        } else {
            if ( c.getValue().trim().equals( "" ) ) {
                c.setType( "" );
            }
            box.setText( c.getValue() );
        }

        if ( c.getValue() == null || c.getValue().length() < 5 ) {
            box.setVisibleLength( 6 );
        } else {
            box.setVisibleLength( c.getValue().length() - 1 );
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                c.setValue( box.getText() );
                if ( onValueChangeCommand != null ) {
                    onValueChangeCommand.execute();
                }
                makeDirty();
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
        Image clickme = GuidedRuleEditorImages508.INSTANCE.Edit();
        clickme.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                showTypeChoice( (Widget) event.getSource() );
            }
        } );
        return clickme;
    }

    protected void showTypeChoice( Widget w ) {
        final FormStylePopup form = new FormStylePopup( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                        Constants.INSTANCE.FieldValue() );
        Button lit = new Button( Constants.INSTANCE.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                methodParameter.setNature( FieldNatureType.TYPE_LITERAL );
                methodParameter.setValue( " " );
                makeDirty();
                refresh();
                form.hide();
            }

        } );

        form.addAttribute( Constants.INSTANCE.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( Constants.INSTANCE.Literal(),
                                                   Constants.INSTANCE.LiteralValTip() ) ) );
        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( Constants.INSTANCE.AdvancedSection() ) );

        /*
         * If there is a bound variable that is the same type of the current
         * variable type, then show abutton
         */
        List<String> vars = model.getModel().getLHSBoundFacts();
        List<String> vars2 = model.getModel().getRHSBoundFacts();
        for ( String i : vars2 ) {
            vars.add( i );
        }
        for ( String v : vars ) {
            boolean createButton = false;
            Button variable = new Button( Constants.INSTANCE.BoundVariable() );
            if ( vars2.contains( v ) == false ) {
                FactPattern factPattern = model.getModel().getLHSBoundFact( v );
                if ( factPattern.getFactType().equals( this.parameterType ) ) {
                    createButton = true;
                }
            } else {
                ActionInsertFact factPattern = model.getModel().getRHSBoundFact( v );
                if ( factPattern.getFactType().equals( this.parameterType ) ) {
                    createButton = true;
                }
            }
            if ( createButton == true ) {
                form.addAttribute( Constants.INSTANCE.BoundVariable() + ":",
                                   variable );
                variable.addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent event ) {
                        methodParameter.setNature( FieldNatureType.TYPE_VARIABLE );
                        methodParameter.setValue( "=" );
                        makeDirty();
                        refresh();
                        form.hide();
                    }

                } );
                break;
            }

        }

        form.show();
    }

    private Widget widgets( Button lit,
                            InfoPopup popup ) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( lit );
        h.add( popup );
        return h;
    }

}
