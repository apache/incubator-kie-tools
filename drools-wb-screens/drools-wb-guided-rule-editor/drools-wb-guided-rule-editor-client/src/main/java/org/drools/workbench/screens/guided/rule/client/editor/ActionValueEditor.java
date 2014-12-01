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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.drools.workbench.screens.guided.rule.client.util.FieldNatureUtil;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.PopupDatePicker;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.ext.widgets.common.client.common.DropDownValueChanged;
import org.uberfire.ext.widgets.common.client.common.InfoPopup;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

/**
 * This provides for editing of fields in the RHS of a rule.
 */
public class ActionValueEditor
        extends Composite {

    private String factType;
    private ActionFieldValue value;
    private ActionFieldValue[] values;
    private DropDownData dropDownData;
    private SimplePanel root;
    private RuleModeller modeller;
    private RuleModel model;
    private AsyncPackageDataModelOracle oracle;
    private EventBus eventBus;
    private String variableType = null;
    private boolean readOnly;
    private Command onChangeCommand;

    public ActionValueEditor( final String factType,
                              final ActionFieldValue value,
                              final ActionFieldValue[] values,
                              final RuleModeller modeller,
                              final EventBus eventBus,
                              final String variableType,
                              final boolean readOnly ) {
        this.readOnly = readOnly;
        this.root = new SimplePanel();
        this.factType = factType;
        this.value = value;
        this.values = values;
        this.modeller = modeller;
        this.model = modeller.getModel();
        this.oracle = modeller.getDataModelOracle();
        this.eventBus = eventBus;
        this.variableType = variableType;

        refresh();
        initWidget( root );
    }

    public void refresh() {
        root.clear();

        //Initialise drop-down data
        getDropDownData();

        //If undefined let the user pick
        if ( value.getNature() == FieldNatureType.TYPE_UNDEFINED ) {

            //Automatic decisions regarding FieldNature
            if ( value.getValue() != null && value.getValue().length() > 0 ) {
                if ( value.getValue().charAt( 0 ) == '=' ) {
                    value.setNature( FieldNatureType.TYPE_VARIABLE );
                } else {
                    value.setNature( FieldNatureType.TYPE_LITERAL );
                }
            } else {
                root.add( choice() );
                return;
            }
        }

        //Template TextBoxes are always Strings as they hold the template key for the actual value
        if ( value.getNature() == FieldNatureType.TYPE_TEMPLATE ) {
            Widget box = wrap( templateKeyEditor() );
            root.add( box );
            return;
        }

        //Variable fields (including bound enumeration fields)
        if ( value.getNature() == FieldNatureType.TYPE_VARIABLE ) {
            Widget list = wrap( boundVariable() );
            root.add( list );
            return;
        }

        //Enumerations - since this does not use FieldNature it should follow those that do
        if ( dropDownData != null && ( dropDownData.getFixedList() != null || dropDownData.getQueryExpression() != null ) ) {
            Widget list = wrap( enumEditor() );
            root.add( list );
            return;
        }

        //Formula require a 
        if ( value.getNature() == FieldNatureType.TYPE_FORMULA ) {
            Widget box = wrap( formulaEditor() );
            root.add( box );
            return;
        }

        //Fall through for all remaining FieldNatures
        Widget box = wrap( literalEditor() );
        root.add( box );

    }

    //Wrap a Constraint Value Editor with an icon to remove the type 
    private Widget wrap( Widget w ) {
        HorizontalPanel wrapper = new HorizontalPanel();
        Image clear = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
        clear.setAltText( GuidedRuleEditorResources.CONSTANTS.RemoveActionValueDefinition() );
        clear.setTitle( GuidedRuleEditorResources.CONSTANTS.RemoveActionValueDefinition() );
        clear.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                //Reset Constraint's value and value type
                if ( Window.confirm( GuidedRuleEditorResources.CONSTANTS.RemoveActionValueDefinitionQuestion() ) ) {
                    value.setNature( FieldNatureType.TYPE_UNDEFINED );
                    value.setValue( null );
                    doTypeChosen();
                }
            }

        } );

        wrapper.add( w );
        if ( !this.readOnly ) {
            wrapper.add( clear );
            wrapper.setCellVerticalAlignment( clear,
                                              HasVerticalAlignment.ALIGN_MIDDLE );
        }
        return wrapper;
    }

    private void doTypeChosen() {
        executeOnChangeCommand();
        executeOnTemplateVariablesChange();
        refresh();
    }

    private void doTypeChosen( FormStylePopup form ) {
        doTypeChosen();
        form.hide();
    }

    private Widget boundVariable() {
        // If there is a bound variable that is the same type of the current variable type, then display a list
        ListBox listVariable = new ListBox();
        listVariable.addItem( GuidedRuleEditorResources.CONSTANTS.Choose() );
        List<String> bindings = getApplicableBindings();
        for ( String v : bindings ) {
            listVariable.addItem( v );
        }

        //Pre-select applicable item
        if ( value.getValue().equals( "=" ) ) {
            listVariable.setSelectedIndex( 0 );
        } else {
            for ( int i = 0; i < listVariable.getItemCount(); i++ ) {
                if ( listVariable.getItemText( i ).equals( value.getValue().substring( 1 ) ) ) {
                    listVariable.setSelectedIndex( i );
                }
            }
        }

        //Add event handler
        if ( listVariable.getItemCount() > 0 ) {
            listVariable.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    ListBox w = (ListBox) event.getSource();
                    value.setValue( "=" + w.getValue( w.getSelectedIndex() ) );
                    executeOnChangeCommand();
                    refresh();
                }
            } );
        }

        if ( this.readOnly ) {
            return new SmallLabel( listVariable.getItemText( listVariable.getSelectedIndex() ) );
        }

        return listVariable;
    }

    private String assertValue() {
        if ( value.getValue() == null ) {
            return "";
        }
        return value.getValue();
    }

    private Widget enumEditor() {
        if ( this.readOnly ) {
            return new SmallLabel( assertValue() );
        }

        EnumDropDown enumDropDown = new EnumDropDown( value.getValue(),
                                                      new DropDownValueChanged() {

                                                          public void valueChanged( String newText,
                                                                                    String newValue ) {
                                                              value.setValue( newValue );
                                                              executeOnChangeCommand();
                                                          }
                                                      },
                                                      dropDownData,
                                                      modeller.getPath() );

        return enumDropDown;
    }

    private Widget literalEditor() {
        if ( this.readOnly ) {
            return new SmallLabel( assertValue() );
        }

        //Date picker
        if ( DataType.TYPE_DATE.equals( value.getType() ) ) {
            final PopupDatePicker dp = new PopupDatePicker( false );

            // Wire up update handler
            dp.addValueChangeHandler( new ValueChangeHandler<Date>() {

                public void onValueChange( ValueChangeEvent<Date> event ) {
                    value.setValue( PopupDatePicker.convertToString( event ) );
                }

            } );

            dp.setValue( assertValue() );
            return dp;
        }

        //Default editor for all other literals
        final TextBox box = TextBoxFactory.getTextBox( value.getType() );
        box.setStyleName( "constraint-value-Editor" );
        box.addValueChangeHandler( new ValueChangeHandler<String>() {

            public void onValueChange( final ValueChangeEvent<String> event ) {
                value.setValue( event.getValue() );
                executeOnChangeCommand();
            }
        } );
        box.setText( assertValue() );
        attachDisplayLengthHandler( box );
        return box;
    }

    /**
     * An editor for Template Keys
     */
    private Widget templateKeyEditor() {
        if ( this.readOnly ) {
            return new SmallLabel( assertValue() );
        }

        TemplateKeyTextBox box = new TemplateKeyTextBox();
        box.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                value.setValue( event.getValue() );
                executeOnChangeCommand();
            }

        } );
        //FireEvents as the box could assume a default value
        box.setValue( assertValue(),
                      true );
        attachDisplayLengthHandler( box );
        return box;
    }

    /**
     * An editor for formula
     * @return
     */
    private Widget formulaEditor() {
        if ( this.readOnly ) {
            return new SmallLabel( assertValue() );
        }

        final TextBox box = new TextBox();
        box.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                value.setValue( event.getValue() );
                executeOnChangeCommand();
            }

        } );
        //FireEvents as the box could assume a default value
        box.setValue( assertValue(),
                      true );
        attachDisplayLengthHandler( box );
        return box;
    }

    //Only display the number of characters that have been entered
    private void attachDisplayLengthHandler( final TextBox box ) {
        int length = box.getText().length();
        box.setVisibleLength( length > 0 ? length : 1 );
        box.addKeyUpHandler( new KeyUpHandler() {

            public void onKeyUp( KeyUpEvent event ) {
                int length = box.getText().length();
                box.setVisibleLength( length > 0 ? length : 1 );
            }
        } );
    }

    private Widget choice() {
        if ( this.readOnly ) {
            return new HTML();
        } else {
            Image clickme = GuidedRuleEditorImages508.INSTANCE.Edit();
            clickme.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    showTypeChoice( (Widget) event.getSource() );
                }
            } );
            return clickme;
        }
    }

    protected void showTypeChoice( Widget w ) {
        final FormStylePopup form = new FormStylePopup( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                        GuidedRuleEditorResources.CONSTANTS.FieldValue() );
        Button lit = new Button( GuidedRuleEditorResources.CONSTANTS.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                value.setNature( FieldNatureType.TYPE_LITERAL );
                value.setValue( "" );
                doTypeChosen( form );
            }
        } );

        form.addAttribute( GuidedRuleEditorResources.CONSTANTS.LiteralValue() + ":",
                           widgets( lit,
                                    new InfoPopup( GuidedRuleEditorResources.CONSTANTS.Literal(),
                                                   GuidedRuleEditorResources.CONSTANTS.ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation() ) ) );

        if ( modeller.isTemplate() ) {
            Button templateButton = new Button( GuidedRuleEditorResources.CONSTANTS.TemplateKey() );
            templateButton.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent event ) {
                    value.setNature( FieldNatureType.TYPE_TEMPLATE );
                    value.setValue( "" );
                    doTypeChosen( form );
                }
            } );
            form.addAttribute( GuidedRuleEditorResources.CONSTANTS.TemplateKey() + ":",
                               widgets( templateButton,
                                        new InfoPopup( GuidedRuleEditorResources.CONSTANTS.Literal(),
                                                       GuidedRuleEditorResources.CONSTANTS.ALiteralValueMeansTheValueAsTypedInIeItsNotACalculation() ) ) );
        }

        form.addRow( new HTML( "<hr/>" ) );
        form.addRow( new SmallLabel( GuidedRuleEditorResources.CONSTANTS.AdvancedSection() ) );

        Button formula = new Button( GuidedRuleEditorResources.CONSTANTS.Formula() );
        formula.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                value.setNature( FieldNatureType.TYPE_FORMULA );
                doTypeChosen( form );
            }
        } );

        // If there is a bound Facts or Fields that are of the same type as the current variable type, then show a button
        List<String> bindings = getApplicableBindings();
        if ( bindings.size() > 0 ) {
            Button variable = new Button( GuidedRuleEditorResources.CONSTANTS.BoundVariable() );
            form.addAttribute( GuidedRuleEditorResources.CONSTANTS.BoundVariable() + ":",
                               variable );
            variable.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    value.setNature( FieldNatureType.TYPE_VARIABLE );
                    value.setValue( "=" );
                    doTypeChosen( form );
                }
            } );
        }

        form.addAttribute( GuidedRuleEditorResources.CONSTANTS.Formula() + ":",
                           widgets( formula,
                                    new InfoPopup( GuidedRuleEditorResources.CONSTANTS.Formula(),
                                                   GuidedRuleEditorResources.CONSTANTS.FormulaTip() ) ) );

        form.show();
    }

    private List<String> getApplicableBindings() {
        List<String> bindings = new ArrayList<String>();

        //Examine LHS Fact and Field bindings and RHS (new) Fact bindings
        for ( String v : modeller.getModel().getAllVariables() ) {

            //LHS FactPattern
            FactPattern fp = modeller.getModel().getLHSBoundFact( v );
            if ( fp != null ) {
                if ( isLHSFactTypeEquivalent( v ) ) {
                    bindings.add( v );
                }
            }

            //LHS FieldConstraint
            FieldConstraint fc = modeller.getModel().getLHSBoundField( v );
            if ( fc != null ) {
                if ( isLHSFieldTypeEquivalent( v ) ) {
                    bindings.add( v );
                }
            }

            //RHS ActionInsertFact
            ActionInsertFact aif = modeller.getModel().getRHSBoundFact( v );
            if ( aif != null ) {
                if ( isRHSFieldTypeEquivalent( v ) ) {
                    bindings.add( v );
                }
            }
        }

        return bindings;
    }

    private boolean isLHSFactTypeEquivalent( String boundVariable ) {
        String boundFactType = modeller.getModel().getLHSBoundFact( boundVariable ).getFactType();

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFactType.equals( DataType.TYPE_COMPARABLE ) ) {
            if ( !this.variableType.equals( DataType.TYPE_COMPARABLE ) ) {
                return false;
            }
            String[] dd = this.modeller.getDataModelOracle().getEnumValues( boundFactType,
                                                                            this.value.getField() );
            return isEnumEquivalent( dd );
        }

        //If the types are identical (and not SuggestionCompletionEngine.TYPE_COMPARABLE) then return true
        if ( boundFactType.equals( this.variableType ) ) {
            return true;
        }
        return false;
    }

    private boolean isLHSFieldTypeEquivalent( String boundVariable ) {
        String boundFieldType = modeller.getModel().getLHSBindingType( boundVariable );

        //If the fieldTypes are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            if ( !this.variableType.equals( DataType.TYPE_COMPARABLE ) ) {
                return false;
            }
            SingleFieldConstraint fc = this.modeller.getModel().getLHSBoundField( boundVariable );
            String fieldName = fc.getFieldName();
            String parentFactTypeForBinding = this.modeller.getModel().getLHSParentFactPatternForBinding( boundVariable ).getFactType();
            String[] dd = this.modeller.getDataModelOracle().getEnumValues( parentFactTypeForBinding,
                                                                            fieldName );
            return isEnumEquivalent( dd );
        }

        //If the fieldTypes are identical (and not SuggestionCompletionEngine.TYPE_COMPARABLE) then return true
        if ( boundFieldType.equals( this.variableType ) ) {
            return true;
        }
        return false;
    }

    private boolean isRHSFieldTypeEquivalent( String boundVariable ) {
        String boundFactType = modeller.getModel().getRHSBoundFact( boundVariable ).getFactType();
        if ( boundFactType == null ) {
            return false;
        }
        if ( this.variableType == null ) {
            return false;
        }

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFactType.equals( DataType.TYPE_COMPARABLE ) ) {
            if ( !this.variableType.equals( DataType.TYPE_COMPARABLE ) ) {
                return false;
            }
            String[] dd = this.modeller.getDataModelOracle().getEnumValues( boundFactType,
                                                                            this.value.getField() );
            return isEnumEquivalent( dd );
        }

        //If the types are identical (and not SuggestionCompletionEngine.TYPE_COMPARABLE) then return true
        if ( boundFactType.equals( this.variableType ) ) {
            return true;
        }
        return false;
    }

    private boolean isEnumEquivalent( String[] values ) {
        if ( values == null || this.dropDownData.getFixedList() == null ) {
            return false;
        }
        if ( values.length != this.dropDownData.getFixedList().length ) {
            return false;
        }
        for ( int i = 0; i < values.length; i++ ) {
            if ( !values[ i ].equals( this.dropDownData.getFixedList()[ i ] ) ) {
                return false;
            }
        }
        return true;
    }

    private Widget widgets( Button lit,
                            InfoPopup popup ) {
        HorizontalPanel h = new HorizontalPanel();
        h.add( lit );
        h.add( popup );
        return h;
    }

    private void executeOnChangeCommand() {
        if ( this.onChangeCommand != null ) {
            this.onChangeCommand.execute();
        }
    }

    public Command getOnChangeCommand() {
        return onChangeCommand;
    }

    public void setOnChangeCommand( Command onChangeCommand ) {
        this.onChangeCommand = onChangeCommand;
    }

    private DropDownData getDropDownData() {
        //Set applicable flags and reference data depending upon type
        if ( DataType.TYPE_BOOLEAN.equals( value.getType() ) ) {
            this.dropDownData = DropDownData.create( new String[]{ "true", "false" } );
        } else {
            final Map<String, String> currentValueMap = FieldNatureUtil.toMap( this.values );
            this.dropDownData = oracle.getEnums( factType,
                                                 value.getField(),
                                                 currentValueMap );
        }
        return dropDownData;
    }

    //Signal (potential) change in Template variables
    private void executeOnTemplateVariablesChange() {
        TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent( model );
        eventBus.fireEventFromSource( tvce,
                                      model );
    }

}
