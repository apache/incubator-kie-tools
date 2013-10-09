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
import java.util.HashMap;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.DropDownData;
import org.drools.workbench.models.datamodel.oracle.OperatorsOracle;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.ConnectiveConstraint;
import org.drools.workbench.models.datamodel.rule.ExpressionFormLine;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.FieldConstraint;
import org.drools.workbench.models.datamodel.rule.HasOperator;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraintEBLeftSide;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.drools.workbench.screens.guided.rule.client.widget.ExpressionBuilder;
import org.guvnor.common.services.workingset.client.WorkingSetManager;
import org.guvnor.common.services.workingset.client.factconstraints.customform.CustomFormConfiguration;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.datamodel.CEPOracle;
import org.kie.workbench.common.widgets.client.widget.PopupDatePicker;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.client.common.DirtyableComposite;
import org.uberfire.client.common.DropDownValueChanged;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.InfoPopup;
import org.uberfire.client.common.SmallLabel;

/**
 * This is an editor for constraint values. How this behaves depends on the
 * constraint value type. When the constraint value has no type, it will allow
 * the user to choose the first time.
 */
public class ConstraintValueEditor
        extends DirtyableComposite {

    private WorkingSetManager workingSetManager = null;

    private String factType;
    private CompositeFieldConstraint constraintList;
    private String fieldName;
    private String fieldType;

    private final AsyncPackageDataModelOracle oracle;
    private final BaseSingleFieldConstraint constraint;
    private final Panel panel;
    private final RuleModel model;
    private final RuleModeller modeller;
    private final EventBus eventBus;

    private DropDownData dropDownData;
    private boolean readOnly;
    private Command onValueChangeCommand;
    private boolean isDropDownDataEnum;
    private Widget constraintWidget = null;

    public ConstraintValueEditor( BaseSingleFieldConstraint con,
                                  CompositeFieldConstraint constraintList,
                                  RuleModeller modeller,
                                  EventBus eventBus,
                                  boolean readOnly ) {
        this.constraint = con;
        this.constraintList = constraintList;
        this.oracle = modeller.getDataModelOracle();
        this.model = modeller.getModel();

        this.modeller = modeller;
        this.eventBus = eventBus;
        this.readOnly = readOnly;

        this.panel = new SimplePanel();

        if ( con instanceof SingleFieldConstraintEBLeftSide ) {
            SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) con;
            this.factType = sfexp.getExpressionLeftSide().getPreviousGenericType();
            if ( this.factType == null ) {
                this.factType = sfexp.getExpressionLeftSide().getGenericType();
            }
            this.fieldName = sfexp.getExpressionLeftSide().getFieldName();
            this.fieldType = sfexp.getExpressionLeftSide().getGenericType();

        } else if ( con instanceof ConnectiveConstraint ) {
            ConnectiveConstraint cc = (ConnectiveConstraint) con;
            this.factType = cc.getFactType();
            this.fieldName = cc.getFieldName();
            this.fieldType = cc.getFieldType();

        } else if ( con instanceof SingleFieldConstraint ) {
            SingleFieldConstraint sfc = (SingleFieldConstraint) con;
            this.factType = sfc.getFactType();
            this.fieldName = sfc.getFieldName();
            this.fieldType = oracle.getFieldType( factType,
                                                  fieldName );
        }

        refreshEditor();
        initWidget( panel );
    }

    public BaseSingleFieldConstraint getConstraint() {
        return constraint;
    }

    private void refreshEditor() {
        panel.clear();
        constraintWidget = null;

        //Expressions' fieldName and hence fieldType can change without creating a new ConstraintValueEditor. 
        //SingleFieldConstraints and their ConnectiveConstraints cannot have the fieldName or fieldType changed 
        //without first deleting and re-creating.
        if ( this.constraint instanceof SingleFieldConstraintEBLeftSide ) {
            SingleFieldConstraintEBLeftSide sfexp = (SingleFieldConstraintEBLeftSide) this.constraint;
            this.factType = sfexp.getExpressionLeftSide().getPreviousGenericType();
            if ( this.factType == null ) {
                this.factType = sfexp.getExpressionLeftSide().getGenericType();
            }
            this.fieldName = sfexp.getExpressionLeftSide().getFieldName();
            this.fieldType = sfexp.getExpressionLeftSide().getGenericType();
        }

        //Initialise drop-down data
        getDropDownData();

        //Show an editor for the constraint value type
        if ( constraint.getConstraintValueType() == SingleFieldConstraint.TYPE_UNDEFINED ) {
            ImageButton clickme = new ImageButton( GuidedRuleEditorImages508.INSTANCE.Edit(),
                                                   GuidedRuleEditorImages508.INSTANCE.EditDisabled(),
                                                   Constants.INSTANCE.Edit(),
                                                   new ClickHandler() {
                                                       public void onClick( ClickEvent event ) {
                                                           showTypeChoice( (Widget) event.getSource(),
                                                                           constraint );
                                                       }
                                                   } );
            clickme.setEnabled( !this.readOnly );
            constraintWidget = clickme;

        } else {
            switch ( constraint.getConstraintValueType() ) {
                case SingleFieldConstraint.TYPE_LITERAL:
                case SingleFieldConstraint.TYPE_ENUM:
                    constraintWidget = wrap( literalEditor() );
                    break;
                case SingleFieldConstraint.TYPE_RET_VALUE:
                    constraintWidget = wrap( returnValueEditor() );
                    break;
                case SingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE:
                    constraintWidget = wrap( expressionEditor() );
                    break;
                case SingleFieldConstraint.TYPE_VARIABLE:
                    constraintWidget = wrap( variableEditor() );
                    break;
                case BaseSingleFieldConstraint.TYPE_TEMPLATE:
                    constraintWidget = wrap( templateKeyEditor() );
                    break;
                default:
                    break;
            }
        }

        panel.add( constraintWidget );
    }

    //Wrap a Constraint Value Editor with an icon to remove the type 
    private Widget wrap( Widget w ) {
        if ( this.readOnly ) {
            return w;
        }
        HorizontalPanel wrapper = new HorizontalPanel();
        Image clear = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
        clear.setTitle( Constants.INSTANCE.RemoveConstraintValueDefinition() );
        clear.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                //Reset Constraint's value and value type
                if ( Window.confirm( Constants.INSTANCE.RemoveConstraintValueDefinitionQuestion() ) ) {
                    constraint.setConstraintValueType( BaseSingleFieldConstraint.TYPE_UNDEFINED );
                    constraint.setValue( null );
                    constraint.clearParameters();
                    constraint.setExpressionValue( new ExpressionFormLine() );
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

    private String assertValue() {
        if ( constraint.getValue() == null ) {
            return "";
        }
        return constraint.getValue();
    }

    private Widget literalEditor() {

        //Custom screen
        if ( this.constraint instanceof SingleFieldConstraint ) {
            final SingleFieldConstraint con = (SingleFieldConstraint) this.constraint;
            CustomFormConfiguration customFormConfiguration = getWorkingSetManager().getCustomFormConfiguration( modeller.getPath(),
                                                                                                                 factType,
                                                                                                                 fieldName );
            if ( customFormConfiguration != null ) {
                Button btnCustom = new Button( con.getValue(),
                                               new ClickHandler() {

                                                   public void onClick( ClickEvent event ) {
                                                       showTypeChoice( (Widget) event.getSource(),
                                                                       constraint );
                                                   }
                                               } );
                btnCustom.setEnabled( !this.readOnly );
                return btnCustom;
            }
        }

        //Label if read-only
        if ( this.readOnly ) {
            return new SmallLabel( assertValue() );
        }

        //Enumeration (these support multi-select for "in" and "not in", so check before comma separated lists) 
        if ( this.dropDownData != null ) {
            final String operator = constraint.getOperator();
            final boolean multipleSelect = OperatorsOracle.operatorRequiresList( operator );
            EnumDropDown enumDropDown = new EnumDropDown( constraint.getValue(),
                                                          new DropDownValueChanged() {

                                                              public void valueChanged( String newText,
                                                                                        String newValue ) {

                                                                  //Prevent recursion once value change has been applied
                                                                  if ( !newValue.equals( constraint.getValue() ) ) {
                                                                      constraint.setValue( newValue );
                                                                      executeOnValueChangeCommand();
                                                                      makeDirty();
                                                                  }
                                                              }
                                                          },
                                                          dropDownData,
                                                          multipleSelect );
            return enumDropDown;
        }

        //Comma separated value list (this will become a dedicated Widget but for now a TextBox suffices)
        String operator = null;
        if ( this.constraint instanceof SingleFieldConstraint ) {
            SingleFieldConstraint sfc = (SingleFieldConstraint) this.constraint;
            operator = sfc.getOperator();
        }
        if ( OperatorsOracle.operatorRequiresList( operator ) ) {
            final TextBox box = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
            box.setStyleName( "constraint-value-Editor" );
            box.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    constraint.setValue( box.getText() );
                    executeOnValueChangeCommand();
                    makeDirty();
                }
            } );

            box.setText( assertValue() );
            attachDisplayLengthHandler( box );
            return box;
        }

        //Date picker
        boolean isCEPOperator = false;
        if ( this.constraint instanceof HasOperator ) {
            isCEPOperator = CEPOracle.isCEPOperator( ( (HasOperator) this.constraint ).getOperator() );
        }
        if ( DataType.TYPE_DATE.equals( this.fieldType ) || ( DataType.TYPE_THIS.equals( this.fieldName ) && isCEPOperator ) ) {

            if ( this.readOnly ) {
                return new SmallLabel( constraint.getValue() );
            }

            final PopupDatePicker dp = new PopupDatePicker( false );

            // Wire up update handler
            dp.addValueChangeHandler( new ValueChangeHandler<Date>() {

                public void onValueChange( ValueChangeEvent<Date> event ) {
                    constraint.setValue( PopupDatePicker.convertToString( event ) );
                    executeOnValueChangeCommand();
                }

            } );

            dp.setValue( assertValue() );
            return dp;
        }

        //Default editor for all other literals
        final TextBox box = TextBoxFactory.getTextBox( fieldType );
        box.setStyleName( "constraint-value-Editor" );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                constraint.setValue( box.getText() );
                executeOnValueChangeCommand();
                makeDirty();
            }
        } );

        box.setText( assertValue() );
        attachDisplayLengthHandler( box );
        return box;
    }

    private Widget variableEditor() {

        if ( this.readOnly ) {
            return new SmallLabel( this.constraint.getValue() );
        }

        final ListBox box = new ListBox();
        box.addItem( Constants.INSTANCE.Choose() );

        List<String> bindingsInScope = this.model.getBoundVariablesInScope( this.constraint );
        List<String> applicableBindingsInScope = getApplicableBindingsInScope( bindingsInScope );
        for ( String var : applicableBindingsInScope ) {
            box.addItem( var );
            if ( this.constraint.getValue() != null && this.constraint.getValue().equals( var ) ) {
                box.setSelectedIndex( box.getItemCount() - 1 );
            }
        }

        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                executeOnValueChangeCommand();
                int selectedIndex = box.getSelectedIndex();
                if ( selectedIndex > 0 ) {
                    constraint.setValue( box.getItemText( selectedIndex ) );
                } else {
                    constraint.setValue( null );
                }
            }
        } );

        return box;
    }

    /**
     * An editor for the retval "formula" (expression).
     */
    private Widget returnValueEditor() {
        TextBox box = new BoundTextBox( constraint );

        if ( this.readOnly ) {
            return new SmallLabel( box.getText() );
        }

        String msg = Constants.INSTANCE.FormulaEvaluateToAValue();
        Image img = new Image( GuidedRuleEditorResources.INSTANCE.images().functionAssets() );
        img.setTitle( msg );
        box.setTitle( msg );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                executeOnValueChangeCommand();
            }
        } );

        Widget ed = widgets( img,
                             box );
        return ed;
    }

    private Widget expressionEditor() {
        ExpressionBuilder builder = null;
        builder = new ExpressionBuilder( this.modeller,
                                         this.eventBus,
                                         this.constraint.getExpressionValue(),
                                         this.readOnly );

        builder.addExpressionTypeChangeHandler( new ExpressionTypeChangeHandler() {

            public void onExpressionTypeChanged( ExpressionTypeChangeEvent event ) {
                System.out.println( "type changed: " + event.getOldType() + " -> " + event.getNewType() );
            }
        } );
        builder.addOnModifiedCommand( new Command() {

            public void execute() {
                executeOnValueChangeCommand();
            }
        } );
        Widget ed = widgets( new HTML( "&nbsp;" ),
                             builder );
        return ed;
    }

    /**
     * An editor for Template Keys
     */
    private Widget templateKeyEditor() {
        if ( this.readOnly ) {
            return new SmallLabel( assertValue() );
        }

        TemplateKeyTextBox box = new TemplateKeyTextBox();
        box.setStyleName( "constraint-value-Editor" );
        box.addValueChangeHandler( new ValueChangeHandler<String>() {

            @Override
            public void onValueChange( ValueChangeEvent<String> event ) {
                constraint.setValue( event.getValue() );
                executeOnValueChangeCommand();
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

    /**
     * Show a list of possibilities for the value type.
     */
    private void showTypeChoice( Widget w,
                                 final BaseSingleFieldConstraint con ) {

        CustomFormConfiguration customFormConfiguration = getWorkingSetManager().getCustomFormConfiguration( modeller.getPath(),
                                                                                                             factType,
                                                                                                             fieldName );

        if ( customFormConfiguration != null ) {
            if ( !( con instanceof SingleFieldConstraint ) ) {
                Window.alert( "Unexpected constraint type!" );
                return;
            }
            final CustomFormPopUp customFormPopUp = new CustomFormPopUp( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                                         Constants.INSTANCE.FieldValue(),
                                                                         customFormConfiguration );

            final SingleFieldConstraint sfc = (SingleFieldConstraint) con;

            customFormPopUp.addOkButtonHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    sfc.setConstraintValueType( SingleFieldConstraint.TYPE_LITERAL );
                    sfc.setId( customFormPopUp.getFormId() );
                    sfc.setValue( customFormPopUp.getFormValue() );
                    doTypeChosen( customFormPopUp );
                }
            } );

            customFormPopUp.show( sfc.getId(),
                                  sfc.getValue() );
            return;
        }

        final FormStylePopup form = new FormStylePopup( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                        Constants.INSTANCE.FieldValue() );

        Button lit = new Button( Constants.INSTANCE.LiteralValue() );
        lit.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                con.setConstraintValueType( isDropDownDataEnum && dropDownData != null ? SingleFieldConstraint.TYPE_ENUM : SingleFieldConstraint.TYPE_LITERAL );
                doTypeChosen( form );
            }
        } );

        boolean showLiteralSelector = true;
        boolean showFormulaSelector = !OperatorsOracle.operatorRequiresList( con.getOperator() );
        boolean showVariableSelector = !OperatorsOracle.operatorRequiresList( con.getOperator() );
        boolean showExpressionSelector = !OperatorsOracle.operatorRequiresList( con.getOperator() );

        if ( con instanceof SingleFieldConstraint ) {
            SingleFieldConstraint sfc = (SingleFieldConstraint) con;
            String fieldName = sfc.getFieldName();
            if ( fieldName.equals( DataType.TYPE_THIS ) ) {
                showLiteralSelector = CEPOracle.isCEPOperator( sfc.getOperator() );
                showFormulaSelector = showFormulaSelector && showLiteralSelector;
            }
        } else if ( con instanceof ConnectiveConstraint ) {
            ConnectiveConstraint cc = (ConnectiveConstraint) con;
            String fieldName = cc.getFieldName();
            if ( fieldName.equals( DataType.TYPE_THIS ) ) {
                showLiteralSelector = CEPOracle.isCEPOperator( cc.getOperator() );
                showFormulaSelector = showFormulaSelector && showLiteralSelector;
            }
        }

        //Literal value selector
        if ( showLiteralSelector ) {
            form.addAttribute( Constants.INSTANCE.LiteralValue() + ":",
                               widgets( lit,
                                        new InfoPopup( Constants.INSTANCE.LiteralValue(),
                                                       Constants.INSTANCE.LiteralValTip() ) ) );
        }

        //Template key selector
        if ( modeller.isTemplate() ) {
            String templateKeyLabel = Constants.INSTANCE.TemplateKey();
            Button templateKeyButton = new Button( templateKeyLabel );
            templateKeyButton.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    con.setConstraintValueType( BaseSingleFieldConstraint.TYPE_TEMPLATE );
                    doTypeChosen( form );
                }
            } );

            form.addAttribute( templateKeyLabel + ":",
                               widgets( templateKeyButton,
                                        new InfoPopup( templateKeyLabel,
                                                       Constants.INSTANCE.LiteralValTip() ) ) );
        }

        //Divider, if we have any advanced options
        if ( showVariableSelector || showFormulaSelector || showExpressionSelector ) {
            form.addRow( new HTML( "<hr/>" ) );
            form.addRow( new SmallLabel( Constants.INSTANCE.AdvancedOptions() ) );
        }

        //Show variables selector, if there are any variables in scope
        if ( showVariableSelector ) {
            List<String> bindingsInScope = this.model.getBoundVariablesInScope( this.constraint );
            if ( bindingsInScope.size() > 0
                    || DataType.TYPE_COLLECTION.equals( this.fieldType ) ) {

                List<String> applicableBindingsInScope = getApplicableBindingsInScope( bindingsInScope );
                if ( applicableBindingsInScope.size() > 0 ) {

                    Button variable = new Button( Constants.INSTANCE.BoundVariable() );
                    variable.addClickHandler( new ClickHandler() {

                        public void onClick( ClickEvent event ) {
                            con.setConstraintValueType( SingleFieldConstraint.TYPE_VARIABLE );
                            doTypeChosen( form );
                        }
                    } );
                    form.addAttribute( Constants.INSTANCE.AVariable(),
                                       widgets( variable,
                                                new InfoPopup( Constants.INSTANCE.ABoundVariable(),
                                                               Constants.INSTANCE.BoundVariableTip() ) ) );
                }
            }
        }

        //Formula selector
        if ( showFormulaSelector ) {
            Button formula = new Button( Constants.INSTANCE.NewFormula() );
            formula.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_RET_VALUE );
                    doTypeChosen( form );
                }
            } );

            form.addAttribute( Constants.INSTANCE.AFormula() + ":",
                               widgets( formula,
                                        new InfoPopup( Constants.INSTANCE.AFormula(),
                                                       Constants.INSTANCE.FormulaExpressionTip() ) ) );
        }

        //Expression selector
        if ( showExpressionSelector ) {
            Button expression = new Button( Constants.INSTANCE.ExpressionEditor() );
            expression.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    con.setConstraintValueType( SingleFieldConstraint.TYPE_EXPR_BUILDER_VALUE );
                    doTypeChosen( form );
                }
            } );

            form.addAttribute( Constants.INSTANCE.ExpressionEditor() + ":",
                               widgets( expression,
                                        new InfoPopup( Constants.INSTANCE.ExpressionEditor(),
                                                       Constants.INSTANCE.ExpressionEditor() ) ) );
        }

        form.show();
    }

    private void doTypeChosen() {
        executeOnValueChangeCommand();
        executeOnTemplateVariablesChange();
        refreshEditor();
    }

    private void doTypeChosen( final FormStylePopup form ) {
        doTypeChosen();
        form.hide();
    }

    private Panel widgets( Widget left,
                           Widget right ) {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
        panel.add( left );
        panel.add( right );
        panel.setWidth( "100%" );
        return panel;
    }

    private void executeOnValueChangeCommand() {
        if ( this.onValueChangeCommand != null ) {
            this.onValueChangeCommand.execute();
        }
    }

    public boolean isDirty() {
        return super.isDirty();
    }

    public void setOnValueChangeCommand( Command onValueChangeCommand ) {
        this.onValueChangeCommand = onValueChangeCommand;
    }

    private List<String> getApplicableBindingsInScope( List<String> bindingsInScope ) {
        List<String> applicableBindingsInScope = new ArrayList<String>();

        //Examine LHS Fact and Field bindings and RHS (new) Fact bindings
        for ( String v : bindingsInScope ) {

            //LHS FactPattern
            FactPattern fp = model.getLHSBoundFact( v );
            if ( fp != null ) {
                if ( isLHSFactTypeEquivalent( v ) ) {
                    applicableBindingsInScope.add( v );
                }
            }

            //LHS FieldConstraint
            FieldConstraint fc = model.getLHSBoundField( v );
            if ( fc != null ) {
                if ( isLHSFieldTypeEquivalent( v ) ) {
                    applicableBindingsInScope.add( v );
                }
            }

        }

        return applicableBindingsInScope;
    }

    private boolean isLHSFactTypeEquivalent( String boundVariable ) {
        String boundFactType = model.getLHSBoundFact( boundVariable ).getFactType();
        String boundFieldType = model.getLHSBindingType( boundVariable );

        //If the types are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFactType.equals( DataType.TYPE_COMPARABLE ) ) {
            if ( !this.fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
                return false;
            }
            String[] dd = this.oracle.getEnumValues( boundFactType,
                                                     this.fieldName );
            return isEnumEquivalent( dd );
        }
        return isBoundVariableApplicable( boundFactType,
                                          boundFieldType );
    }

    private boolean isLHSFieldTypeEquivalent( String boundVariable ) {
        String boundFieldType = this.model.getLHSBindingType( boundVariable );

        //If the fieldTypes are SuggestionCompletionEngine.TYPE_COMPARABLE check the enums are equivalent
        if ( boundFieldType.equals( DataType.TYPE_COMPARABLE ) ) {
            if ( !this.fieldType.equals( DataType.TYPE_COMPARABLE ) ) {
                return false;
            }
            FieldConstraint fc = this.model.getLHSBoundField( boundVariable );
            if ( fc instanceof SingleFieldConstraint ) {
                String fieldName = ( (SingleFieldConstraint) fc ).getFieldName();
                String parentFactTypeForBinding = this.model.getLHSParentFactPatternForBinding( boundVariable ).getFactType();
                String[] dd = this.oracle.getEnumValues( parentFactTypeForBinding,
                                                         fieldName );
                return isEnumEquivalent( dd );
            }
            return false;
        }

        return isBoundVariableApplicable( boundFieldType );
    }

    private boolean isEnumEquivalent( String[] values ) {
        if ( values == null && this.dropDownData.getFixedList() != null ) {
            return false;
        }
        if ( values != null && this.dropDownData.getFixedList() == null ) {
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

    private boolean isBoundVariableApplicable( String boundFactType,
                                               String boundFieldType ) {

        //Fields of the same type as the bound variable can be compared
        if ( boundFactType != null && boundFactType.equals( this.fieldType ) ) {
            return true;
        }

        //'this' can be compared to bound facts of the same type
        if ( this.fieldName.equals( DataType.TYPE_THIS ) ) {
            if ( boundFactType != null && boundFactType.equals( this.factType ) ) {
                return true;
            }
        }

        //For collection, present the list of possible bound variable
        String factCollectionType = oracle.getParametricFieldType( this.factType,
                                                                   this.fieldName );
        if ( boundFactType != null && factCollectionType != null && boundFactType.equals( factCollectionType ) ) {
            return true;
        }

        return isBoundVariableApplicable( boundFieldType );
    }

    private boolean isBoundVariableApplicable( String boundFieldType ) {

        //Field-types can be simply compared
        if ( boundFieldType != null && boundFieldType.equals( this.fieldType ) ) {
            return true;
        }

        //'this' can be compared to bound fields of the same type
        if ( this.fieldName.equals( DataType.TYPE_THIS ) ) {
            if ( boundFieldType != null && boundFieldType.equals( this.factType ) ) {
                return true;
            }
        }

        //'this' can be compared to bound events if using a CEP operator
        if ( this.fieldName.equals( DataType.TYPE_THIS ) && oracle.isFactTypeAnEvent( boundFieldType ) ) {
            if ( this.constraint instanceof HasOperator ) {
                HasOperator hop = (HasOperator) this.constraint;
                if ( CEPOracle.isCEPOperator( hop.getOperator() ) ) {
                    return true;
                }
            }
        }

        //'this' can be compared to bound Dates if using a CEP operator
        if ( this.fieldName.equals( DataType.TYPE_THIS ) && boundFieldType.equals( DataType.TYPE_DATE ) ) {
            if ( this.constraint instanceof HasOperator ) {
                HasOperator hop = (HasOperator) this.constraint;
                if ( CEPOracle.isCEPOperator( hop.getOperator() ) ) {
                    return true;
                }
            }
        }

        //Dates can be compared to bound events if using a CEP operator
        if ( ( this.fieldType.equals( DataType.TYPE_DATE ) && oracle.isFactTypeAnEvent( boundFieldType ) ) ) {
            if ( this.constraint instanceof HasOperator ) {
                HasOperator hop = (HasOperator) this.constraint;
                if ( CEPOracle.isCEPOperator( hop.getOperator() ) ) {
                    return true;
                }
            }
        }

        //For collection, present the list of possible bound variable
        String factCollectionType = oracle.getParametricFieldType( this.factType,
                                                                   this.fieldName );
        if ( factCollectionType != null && factCollectionType.equals( boundFieldType ) ) {
            return true;
        }

        return false;
    }

    private DropDownData getDropDownData() {
        //Set applicable flags and reference data depending upon type
        if ( DataType.TYPE_BOOLEAN.equals( this.fieldType ) ) {
            this.isDropDownDataEnum = false;
            this.dropDownData = DropDownData.create( new String[]{ "true", "false" } );
        } else {
            this.isDropDownDataEnum = true;

            final Map<String, String> currentValueMap = new HashMap<String, String>();

            if ( constraintList != null && constraintList.getConstraints() != null ) {
                for ( FieldConstraint con : constraintList.getConstraints() ) {
                    if ( con instanceof SingleFieldConstraint ) {
                        SingleFieldConstraint sfc = (SingleFieldConstraint) con;
                        String fieldName = sfc.getFieldName();
                        currentValueMap.put( fieldName,
                                             sfc.getValue() );
                    }
                }
            }

            this.dropDownData = oracle.getEnums( this.factType,
                                                 fieldName,
                                                 currentValueMap );
        }
        return dropDownData;
    }

    /**
     * Refresh the displayed drop-down
     */
    public void refreshDropDownData() {
        if ( this.dropDownData == null ) {
            return;
        }
        if ( this.constraintWidget instanceof HorizontalPanel ) {
            HorizontalPanel hp = (HorizontalPanel) this.constraintWidget;
            for ( int iChildIndex = 0; iChildIndex < hp.getWidgetCount(); iChildIndex++ ) {
                Widget w = hp.getWidget( iChildIndex );
                if ( w instanceof EnumDropDown ) {
                    EnumDropDown edd = (EnumDropDown) w;
                    edd.setDropDownData( constraint.getValue(),
                                         getDropDownData() );
                }
            }
        }
    }

    //Signal (potential) change in Template variables
    private void executeOnTemplateVariablesChange() {
        TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent( model );
        eventBus.fireEventFromSource( tvce, model );
    }

    private WorkingSetManager getWorkingSetManager() {
        if ( workingSetManager == null ) {
            workingSetManager = IOC.getBeanManager().lookupBean( WorkingSetManager.class ).getInstance();
        }
        return workingSetManager;
    }

}
