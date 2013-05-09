/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.RadioButton;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.models.commons.shared.oracle.DataType;
import org.drools.guvnor.models.commons.shared.rule.BaseSingleFieldConstraint;
import org.drools.guvnor.models.commons.shared.rule.HasCEPWindow;
import org.drools.guvnor.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.guvnor.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.guvnor.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.guvnor.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.guvnor.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.guvnor.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.guvnor.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.guvnor.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.ImageResources508;
import org.drools.workbench.screens.guided.dtable.client.widget.table.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableUtils;
import org.drools.workbench.screens.guided.rule.client.editor.BindingTextBox;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.kie.guvnor.commons.ui.client.resources.HumanReadable;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.InfoPopup;
import org.uberfire.client.common.SmallLabel;

/**
 * This is a configuration editor for a column in a the guided decision table.
 */
public class ConditionPopup extends FormStylePopup {

    private SmallLabel patternLabel = new SmallLabel();
    private TextBox fieldLabel = getFieldLabel();
    private TextBox binding = new BindingTextBox();
    private Label operatorLabel = new Label();
    private SimplePanel limitedEntryValueWidgetContainer = new SimplePanel();
    private int limitedEntryValueAttributeIndex = -1;
    private TextBox valueListWidget = null;
    private SimplePanel defaultValueWidgetContainer = new SimplePanel();
    private int defaultValueWidgetContainerIndex = -1;
    private ImageButton editField;
    private ImageButton editOp;

    private RadioButton literal = new RadioButton( "constraintValueType",
                                                   Constants.INSTANCE.LiteralValue() );
    private RadioButton formula = new RadioButton( "constraintValueType",
                                                   Constants.INSTANCE.Formula() );
    private RadioButton predicate = new RadioButton( "constraintValueType",
                                                     Constants.INSTANCE.Predicate() );

    private CEPWindowOperatorsDropdown cwo;
    private TextBox entryPointName;
    private int cepWindowRowIndex;

    private final GuidedDecisionTable52 model;
    private final PackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;
    private final DTCellValueWidgetFactory factory;
    private final Validator validator;
    private final BRLRuleModel rm;
    private final DTCellValueUtilities utilities;

    private Pattern52 editingPattern;
    private ConditionCol52 editingCol;

    private final boolean isReadOnly;

    private InfoPopup fieldLabelInterpolationInfo = getPredicateHint();

    public ConditionPopup( final PackageDataModelOracle oracle,
                           final GuidedDecisionTable52 model,
                           final ConditionColumnCommand refreshGrid,
                           final ConditionCol52 col,
                           final boolean isNew,
                           final boolean isReadOnly ) {
        this.rm = new BRLRuleModel( model );
        this.editingPattern = model.getPattern( col );
        this.editingCol = cloneConditionColumn( col );
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( oracle,
                                                   model );
        this.isReadOnly = isReadOnly;
        this.validator = new Validator( model.getConditions() );
        this.utilities = new DTCellValueUtilities( model,
                                                   oracle );

        //Set-up a factory for value editors
        factory = DTCellValueWidgetFactory.getInstance( model,
                                                        oracle,
                                                        isReadOnly,
                                                        allowEmptyValues() );

        setTitle( Constants.INSTANCE.ConditionColumnConfiguration() );
        setModal( false );

        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        //Pattern selector
        ImageButton changePattern = new ImageButton( ImageResources508.INSTANCE.Edit(),
                                                     ImageResources508.INSTANCE.EditDisabled(),
                                                     Constants.INSTANCE.ChooseAnExistingPatternThatThisColumnAddsTo(),
                                                     new ClickHandler() {
                                                         public void onClick( ClickEvent w ) {
                                                             showChangePattern( w );
                                                         }
                                                     } );
        changePattern.setEnabled( !isReadOnly );
        pattern.add( changePattern );

        addAttribute( Constants.INSTANCE.Pattern(),
                      pattern );

        //Radio buttons for Calculation Type
        switch ( model.getTableFormat() ) {
            case EXTENDED_ENTRY:
                HorizontalPanel valueTypes = new HorizontalPanel();
                valueTypes.add( literal );
                valueTypes.add( formula );
                valueTypes.add( predicate );
                addAttribute( Constants.INSTANCE.CalculationType(),
                              valueTypes );

                switch ( editingCol.getConstraintValueType() ) {
                    case BaseSingleFieldConstraint.TYPE_LITERAL:
                        literal.setValue( true );
                        binding.setEnabled( !isReadOnly );
                        break;
                    case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                        formula.setValue( true );
                        binding.setEnabled( false );
                        break;
                    case BaseSingleFieldConstraint.TYPE_PREDICATE:
                        predicate.setValue( true );
                        binding.setEnabled( false );
                }

                if ( !isReadOnly ) {
                    literal.addClickHandler( new ClickHandler() {
                        public void onClick( ClickEvent w ) {
                            editingCol.setFactField( null );
                            applyConsTypeChange( BaseSingleFieldConstraint.TYPE_LITERAL );
                        }
                    } );
                }

                if ( !isReadOnly ) {
                    formula.addClickHandler( new ClickHandler() {
                        public void onClick( ClickEvent w ) {
                            editingCol.setFactField( null );
                            applyConsTypeChange( BaseSingleFieldConstraint.TYPE_RET_VALUE );
                        }
                    } );
                }

                if ( !isReadOnly ) {
                    predicate.addClickHandler( new ClickHandler() {
                        public void onClick( ClickEvent w ) {
                            editingCol.setFactField( null );
                            applyConsTypeChange( BaseSingleFieldConstraint.TYPE_PREDICATE );
                        }
                    } );
                }

                doCalculationType();
                break;

            case LIMITED_ENTRY:
                binding.setEnabled( !isReadOnly );
        }

        //Fact field
        HorizontalPanel field = new HorizontalPanel();
        fieldLabel.setEnabled( !isReadOnly );
        field.add( fieldLabel );
        field.add( fieldLabelInterpolationInfo );
        this.editField = new ImageButton( ImageResources508.INSTANCE.Edit(),
                                          ImageResources508.INSTANCE.EditDisabled(),
                                          Constants.INSTANCE.EditTheFieldThatThisColumnOperatesOn(),
                                          new ClickHandler() {
                                              public void onClick( ClickEvent w ) {
                                                  showFieldChange();
                                              }
                                          } );
        editField.setEnabled( !isReadOnly );
        field.add( editField );
        addAttribute( Constants.INSTANCE.Field(),
                      field );
        doFieldLabel();

        //Operator
        HorizontalPanel operator = new HorizontalPanel();
        operator.add( operatorLabel );
        this.editOp = new ImageButton( ImageResources508.INSTANCE.Edit(),
                                       ImageResources508.INSTANCE.EditDisabled(),
                                       Constants.INSTANCE.EditTheOperatorThatIsUsedToCompareDataWithThisField(),
                                       new ClickHandler() {
                                           public void onClick( ClickEvent w ) {
                                               showOperatorChange();
                                           }
                                       } );
        editOp.setEnabled( !isReadOnly );
        operator.add( editOp );
        addAttribute( Constants.INSTANCE.Operator(),
                      operator );
        doOperatorLabel();
        doImageButtons();

        //Add CEP fields for patterns containing Facts declared as Events
        cepWindowRowIndex = addAttribute( Constants.INSTANCE.DTLabelOverCEPWindow(),
                                          createCEPWindowWidget( editingPattern ) );
        displayCEPOperators();

        //Entry point
        entryPointName = new TextBox();
        entryPointName.setText( editingPattern.getEntryPointName() );
        entryPointName.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            entryPointName.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    editingPattern.setEntryPointName( entryPointName.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.DTLabelFromEntryPoint(),
                      entryPointName );

        //Column header
        final TextBox header = new TextBox();
        header.setText( col.getHeader() );
        header.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            header.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    editingCol.setHeader( header.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.ColumnHeaderDescription(),
                      header );

        //Optional value list
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            valueListWidget = new TextBox();
            valueListWidget.setText( editingCol.getValueList() );
            valueListWidget.setEnabled( !isReadOnly );
            if ( !isReadOnly ) {

                //Copy value back to model
                valueListWidget.addChangeHandler( new ChangeHandler() {
                    public void onChange( ChangeEvent event ) {
                        editingCol.setValueList( valueListWidget.getText() );
                    }
                } );

                //Update Default Value widget if necessary
                valueListWidget.addBlurHandler( new BlurHandler() {
                    public void onBlur( BlurEvent event ) {
                        assertDefaultValue();
                        makeDefaultValueWidget();
                    }

                    private void assertDefaultValue() {
                        final List<String> valueList = Arrays.asList( utils.getValueList( editingCol ) );
                        if ( valueList.size() > 0 ) {
                            final String defaultValue = utilities.asString( editingCol.getDefaultValue() );
                            if ( !valueList.contains( defaultValue ) ) {
                                editingCol.getDefaultValue().clearValues();
                            }
                        } else {
                            //Ensure the Default Value has been updated to represent the column's data-type.
                            final DTCellValue52 defaultValue = editingCol.getDefaultValue();
                            final DataType.DataTypes dataType = utilities.getDataType( editingPattern,
                                                                                       editingCol );
                            utilities.assertDTCellValue( dataType,
                                                         defaultValue );
                        }
                    }

                } );

            }
            HorizontalPanel vl = new HorizontalPanel();
            vl.add( valueListWidget );
            vl.add( new InfoPopup( Constants.INSTANCE.ValueList(),
                                   Constants.INSTANCE.ValueListsExplanation() ) );
            addAttribute( Constants.INSTANCE.optionalValueList(),
                          vl );
        }
        doValueList();

        //Default value
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            defaultValueWidgetContainerIndex = addAttribute( Constants.INSTANCE.DefaultValue(),
                                                             defaultValueWidgetContainer );
            makeDefaultValueWidget();
        }

        //Limited entry value widget
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            limitedEntryValueAttributeIndex = addAttribute( Constants.INSTANCE.LimitedEntryValue(),
                                                            limitedEntryValueWidgetContainer );
            makeLimitedValueWidget();
        }

        //Field Binding
        binding.setText( col.getBinding() );
        if ( !isReadOnly ) {
            binding.addChangeHandler( new ChangeHandler() {
                public void onChange( ChangeEvent event ) {
                    editingCol.setBinding( binding.getText() );
                }
            } );
        }
        addAttribute( Constants.INSTANCE.Binding(),
                      binding );

        //Hide column tick-box
        addAttribute( Constants.INSTANCE.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        //Apply button
        Button apply = new Button( Constants.INSTANCE.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( Constants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {

                    //Field mandatory for Literals and Formulae
                    if ( null == editingCol.getFactField() || "".equals( editingCol.getFactField() ) ) {
                        Window.alert( Constants.INSTANCE.PleaseSelectOrEnterField() );
                        return;
                    }

                    //Operator optional for Literals and Formulae
                    if ( null == editingCol.getOperator() || "".equals( editingCol.getOperator() ) ) {
                        Window.alert( Constants.INSTANCE.NotifyNoSelectedOperator() );
                    }

                } else {

                    //Clear operator for predicates, but leave field intact for interpolation of $param values
                    editingCol.setOperator( null );
                }

                //Check for unique binding
                if ( editingCol.isBound() && !isBindingUnique( editingCol.getBinding() ) ) {
                    Window.alert( Constants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                    return;
                }

                //Check column header is unique
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }
                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( Constants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                            return;
                        }
                    }
                }

                //Clear binding if column is not a literal
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_LITERAL ) {
                    editingCol.setBinding( null );
                }

                // Pass new\modified column back for handling
                refreshGrid.execute( editingPattern,
                                     editingCol );
                hide();

            }
        } );
        addAttribute( "",
                      apply );

    }

    private boolean allowEmptyValues() {
        return this.model.getTableFormat() == TableFormat.EXTENDED_ENTRY;
    }

    private ConditionCol52 cloneConditionColumn( ConditionCol52 col ) {
        ConditionCol52 clone = null;
        if ( col instanceof LimitedEntryConditionCol52 ) {
            clone = new LimitedEntryConditionCol52();
            DTCellValue52 dcv = cloneLimitedEntryValue( ( (LimitedEntryCol) col ).getValue() );
            ( (LimitedEntryCol) clone ).setValue( dcv );
        } else {
            clone = new ConditionCol52();
        }
        clone.setConstraintValueType( col.getConstraintValueType() );
        clone.setFactField( col.getFactField() );
        clone.setFieldType( col.getFieldType() );
        clone.setHeader( col.getHeader() );
        clone.setOperator( col.getOperator() );
        clone.setValueList( col.getValueList() );
        clone.setDefaultValue( new DTCellValue52( col.getDefaultValue() ) );
        clone.setHideColumn( col.isHideColumn() );
        clone.setParameters( col.getParameters() );
        clone.setWidth( col.getWidth() );
        clone.setBinding( col.getBinding() );
        return clone;
    }

    private DTCellValue52 cloneLimitedEntryValue( DTCellValue52 dcv ) {
        if ( dcv == null ) {
            return null;
        }
        DTCellValue52 clone = new DTCellValue52( dcv );
        return clone;
    }

    private void makeLimitedValueWidget() {
        if ( !( editingCol instanceof LimitedEntryConditionCol52 ) ) {
            return;
        }
        LimitedEntryConditionCol52 lec = (LimitedEntryConditionCol52) editingCol;
        boolean doesOperatorNeedValue = validator.doesOperatorNeedValue( editingCol );
        if ( !doesOperatorNeedValue ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            lec.setValue( null );
            return;
        }
        setAttributeVisibility( limitedEntryValueAttributeIndex,
                                true );
        if ( lec.getValue() == null ) {
            lec.setValue( factory.makeNewValue( editingPattern,
                                                editingCol ) );
        }
        limitedEntryValueWidgetContainer.setWidget( factory.getWidget( editingPattern,
                                                                       editingCol,
                                                                       lec.getValue() ) );
    }

    private void makeDefaultValueWidget() {
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            return;
        }
        if ( nil( editingCol.getFactField() ) ) {
            setAttributeVisibility( defaultValueWidgetContainerIndex,
                                    false );
            return;
        }

        //Don't show Default Value if operator does not require a value
        if ( !validator.doesOperatorNeedValue( editingCol ) ) {
            setAttributeVisibility( defaultValueWidgetContainerIndex,
                                    false );
            return;
        }

        setAttributeVisibility( defaultValueWidgetContainerIndex,
                                true );
        if ( editingCol.getDefaultValue() == null ) {
            editingCol.setDefaultValue( factory.makeNewValue( editingPattern,
                                                              editingCol ) );
        }

        //Ensure the Default Value has been updated to represent the column's 
        //data-type. Legacy Default Values are all String-based and need to be 
        //coerced to the correct type
        final DTCellValue52 defaultValue = editingCol.getDefaultValue();
        final DataType.DataTypes dataType = utilities.getDataType( editingPattern,
                                                                   editingCol );
        utilities.assertDTCellValue( dataType,
                                     defaultValue );
        defaultValueWidgetContainer.setWidget( factory.getWidget( editingPattern,
                                                                  editingCol,
                                                                  defaultValue ) );
    }

    private void applyConsTypeChange( int newType ) {
        editingCol.setConstraintValueType( newType );
        binding.setEnabled( newType == BaseSingleFieldConstraint.TYPE_LITERAL && !isReadOnly );
        doFieldLabel();
        doValueList();
        doOperatorLabel();
        doImageButtons();
        makeDefaultValueWidget();
    }

    private void doImageButtons() {
        int constraintType = editingCol.getConstraintValueType();
        this.editField.setEnabled( constraintType != BaseSingleFieldConstraint.TYPE_PREDICATE && !isReadOnly );
        this.editOp.setEnabled( constraintType != BaseSingleFieldConstraint.TYPE_PREDICATE && !isReadOnly );
    }

    private boolean isBindingUnique( String binding ) {
        return !rm.isVariableNameUsed( binding );
    }

    private void doFieldLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            if ( this.editingCol.getFactField() == null || this.editingCol.getFactField().equals( "" ) ) {
                fieldLabel.setText( Constants.INSTANCE.notNeededForPredicate() );
            } else {
                fieldLabel.setText( this.editingCol.getFactField() );
            }
            fieldLabelInterpolationInfo.setVisible( true );
        } else if ( nil( editingPattern.getFactType() ) ) {
            fieldLabel.setText( Constants.INSTANCE.pleaseSelectAPatternFirst() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else if ( nil( editingCol.getFactField() ) ) {
            fieldLabel.setText( Constants.INSTANCE.pleaseSelectAField() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else {
            fieldLabel.setText( this.editingCol.getFactField() );
        }
    }

    private void doOperatorLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            operatorLabel.setText( Constants.INSTANCE.notNeededForPredicate() );
        } else if ( nil( editingPattern.getFactType() ) ) {
            operatorLabel.setText( Constants.INSTANCE.pleaseSelectAPatternFirst() );
        } else if ( nil( editingCol.getFactField() ) ) {
            operatorLabel.setText( Constants.INSTANCE.pleaseChooseAFieldFirst() );
        } else if ( nil( editingCol.getOperator() ) ) {
            operatorLabel.setText( Constants.INSTANCE.pleaseSelectAField() );
        } else {
            operatorLabel.setText( HumanReadable.getOperatorDisplayName(editingCol.getOperator()) );
        }
    }

    private void doPatternLabel() {
        if ( editingPattern.getFactType() != null ) {
            StringBuilder patternLabel = new StringBuilder();
            String factType = editingPattern.getFactType();
            String boundName = editingPattern.getBoundName();
            if ( factType != null && factType.length() > 0 ) {
                if ( editingPattern.isNegated() ) {
                    patternLabel.append( Constants.INSTANCE.negatedPattern() ).append( " " ).append( factType );
                } else {
                    patternLabel.append( factType ).append( " [" ).append( boundName ).append( "]" );
                }
            }
            this.patternLabel.setText( patternLabel.toString() );
        }
        doFieldLabel();
        doOperatorLabel();
    }

    private TextBox getFieldLabel() {
        final TextBox box = new TextBox();
        box.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                editingCol.setFactField( box.getText() );
            }
        } );
        return box;
    }

    private InfoPopup getPredicateHint() {
        return new InfoPopup( Constants.INSTANCE.Predicates(),
                              Constants.INSTANCE.PredicatesInfo() );
    }

    private void doValueList() {
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            return;
        }

        //Don't show a Value List if either the Fact\Field is empty
        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        boolean enableValueList = !( ( factType == null || "".equals( factType ) ) || ( factField == null || "".equals( factField ) ) );

        //Don't show Value List if operator does not accept one
        if ( enableValueList ) {
            enableValueList = validator.doesOperatorAcceptValueList( editingCol );
        }

        //Don't show a Value List if the Fact\Field has an enumeration
        if ( enableValueList ) {
            enableValueList = !oracle.hasEnums( factType,
                                                factField );
        }
        valueListWidget.setEnabled( enableValueList );
        if ( !enableValueList ) {
            valueListWidget.setText( "" );
        }
    }

    private void doCalculationType() {
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            return;
        }

        //Disable Formula and Predicate if the Fact\Field has enums
        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        final boolean hasEnums = oracle.hasEnums( factType,
                                                  factField );
        this.literal.setEnabled( hasEnums || !isReadOnly );
        this.formula.setEnabled( !( hasEnums || isReadOnly ) );
        this.predicate.setEnabled( !( hasEnums || isReadOnly ) );

        //If Fact\Field has enums the Value Type has to be a literal
        if ( hasEnums ) {
            this.editingCol.setConstraintValueType( BaseSingleFieldConstraint.TYPE_LITERAL );
        }
    }

    private ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();
        for ( Pattern52 p : model.getPatterns() ) {
            if ( !vars.contains( p.getBoundName() ) ) {
                patterns.addItem( ( p.isNegated() ? Constants.INSTANCE.negatedPattern() + " " : "" )
                                          + p.getFactType()
                                          + " [" + p.getBoundName() + "]",
                                  p.getFactType()
                                          + " " + p.getBoundName()
                                          + " " + p.isNegated() );
                vars.add( p.getBoundName() );
            }
        }

        return patterns;

    }

    private boolean nil( String s ) {
        return s == null || s.equals( "" );
    }

    private void showOperatorChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( Constants.INSTANCE.SetTheOperator() );
        pop.setModal( false );

        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        String[] ops = this.oracle.getOperatorCompletions( factType,
                                                           factField );

        //Operators "in" and "not in" are only allowed if the Calculation Type is a Literal
        final List<String> filteredOps = new ArrayList<String>();
        for ( String op : ops ) {
            filteredOps.add( op );
        }
        if ( BaseSingleFieldConstraint.TYPE_LITERAL != this.editingCol.getConstraintValueType() ) {
            filteredOps.remove( "in" );
            filteredOps.remove( "not in" );
        }

        //Remove "in" and "not in" if the Fact\Field is enumerated
        if ( oracle.hasEnums( factType,
                              factField ) ) {
            filteredOps.remove( "in" );
            filteredOps.remove( "not in" );
        }

        final String[] displayOps = new String[ filteredOps.size() ];
        filteredOps.toArray( displayOps );

        final CEPOperatorsDropdown box = new CEPOperatorsDropdown( displayOps,
                                                                   editingCol );

        box.addItem( Constants.INSTANCE.noOperator(),
                     "" );
        pop.addAttribute( Constants.INSTANCE.Operator(),
                          box );
        Button b = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                editingCol.setOperator( box.getValue( box.getSelectedIndex() ) );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doOperatorLabel();
                doValueList();
                pop.hide();
            }
        } );
        pop.show();

    }

    private boolean unique( String header ) {
        for ( CompositeColumn<?> cc : model.getConditions() ) {
            for ( int iChild = 0; iChild < cc.getChildColumns().size(); iChild++ ) {
                if ( cc.getChildColumns().get( iChild ).getHeader().equals( header ) ) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void showChangePattern( ClickEvent w ) {

        final ListBox pats = this.loadPatterns();
        if ( pats.getItemCount() == 0 ) {
            showNewPatternDialog();
            return;
        }
        final FormStylePopup pop = new FormStylePopup();
        Button ok = new Button( Constants.INSTANCE.OK() );
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( pats );
        hp.add( ok );

        pop.addAttribute( Constants.INSTANCE.ChooseExistingPatternToAddColumnTo(),
                          hp );
        pop.addAttribute( "",
                          new HTML( Constants.INSTANCE.ORwithEmphasis() ) );

        Button createPattern = new Button( Constants.INSTANCE.CreateNewFactPattern() );
        createPattern.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                pop.hide();
                showNewPatternDialog();
            }
        } );
        pop.addAttribute( "",
                          createPattern );

        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {

                String[] val = pats.getValue( pats.getSelectedIndex() ).split( "\\s" );
                editingPattern = model.getConditionPattern( val[ 1 ] );

                //Clear Field and Operator when pattern changes
                editingCol.setFactField( null );
                editingCol.setOperator( null );

                //Set-up UI
                entryPointName.setText( editingPattern.getEntryPointName() );
                cwo.selectItem( editingPattern.getWindow().getOperator() );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                displayCEPOperators();
                doPatternLabel();
                doValueList();
                doCalculationType();

                pop.hide();
            }
        } );

        pop.show();
    }

    protected void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setModal( false );
        String[] fields = this.oracle.getFieldCompletions( FieldAccessorsAndMutators.ACCESSOR,
                                                           this.editingPattern.getFactType() );

        final ListBox box = new ListBox();

        switch ( this.editingCol.getConstraintValueType() ) {
            case BaseSingleFieldConstraint.TYPE_LITERAL:
                //Literals can be on any field
                for ( int i = 0; i < fields.length; i++ ) {
                    box.addItem( fields[ i ] );
                }
                break;

            case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                //Formulae can only consume fields that do not have enumerations
                for ( int i = 0; i < fields.length; i++ ) {
                    if ( !oracle.hasEnums( this.editingPattern.getFactType(),
                                           fields[ i ] ) ) {
                        box.addItem( fields[ i ] );
                    }
                }
                break;

            case BaseSingleFieldConstraint.TYPE_PREDICATE:
                //Predicates don't need a field (this should never be reachable as the
                //field selector is disabled when the Calculation Type is Predicate)
                break;

        }
        pop.addAttribute( Constants.INSTANCE.Field(),
                          box );
        Button b = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                editingCol.setFactField( box.getItemText( box.getSelectedIndex() ) );
                editingCol.setFieldType( oracle.getFieldType( editingPattern.getFactType(),
                                                              editingCol.getFactField() ) );

                //Clear Operator when field changes
                editingCol.setOperator( null );

                //Setup UI
                doFieldLabel();
                doValueList();
                doCalculationType();
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doOperatorLabel();

                pop.hide();
            }
        } );
        pop.show();
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( Constants.INSTANCE.CreateANewFactPattern() );
        final ListBox types = new ListBox();
        for ( int i = 0; i < oracle.getFactTypes().length; i++ ) {
            types.addItem( oracle.getFactTypes()[ i ] );
        }
        pop.addAttribute( Constants.INSTANCE.FactType(),
                          types );
        final TextBox binding = new BindingTextBox();
        binding.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                binding.setText( binding.getText().replace( " ",
                                                            "" ) );
            }
        } );
        pop.addAttribute( Constants.INSTANCE.Binding(),
                          binding );

        //Patterns can be negated, i.e. "not Pattern(...)"
        final CheckBox chkNegated = new CheckBox();
        chkNegated.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                boolean isPatternNegated = chkNegated.getValue();
                binding.setEnabled( !isPatternNegated );
            }

        } );
        pop.addAttribute( Constants.INSTANCE.negatePattern(),
                          chkNegated );

        Button ok = new Button( Constants.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {

                boolean isPatternNegated = chkNegated.getValue();
                String ft = types.getItemText( types.getSelectedIndex() );
                String fn = isPatternNegated ? "" : binding.getText();
                if ( !isPatternNegated ) {
                    if ( fn.equals( "" ) ) {
                        Window.alert( Constants.INSTANCE.PleaseEnterANameForFact() );
                        return;
                    } else if ( fn.equals( ft ) ) {
                        Window.alert( Constants.INSTANCE.PleaseEnterANameThatIsNotTheSameAsTheFactType() );
                        return;
                    } else if ( !isBindingUnique( fn ) ) {
                        Window.alert( Constants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                        return;
                    }
                }

                //Create new pattern
                editingPattern = new Pattern52();
                editingPattern.setFactType( ft );
                editingPattern.setBoundName( fn );
                editingPattern.setNegated( isPatternNegated );

                //Clear Field and Operator when pattern changes
                editingCol.setFactField( null );
                editingCol.setOperator( null );

                //Set-up UI
                entryPointName.setText( editingPattern.getEntryPointName() );
                cwo.selectItem( editingPattern.getWindow().getOperator() );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                displayCEPOperators();
                doPatternLabel();
                doValueList();
                doCalculationType();
                doOperatorLabel();

                pop.hide();
            }
        } );
        pop.addAttribute( "",
                          ok );

        pop.show();

    }

    //Widget for CEP 'windows'
    private Widget createCEPWindowWidget( final HasCEPWindow c ) {
        HorizontalPanel hp = new HorizontalPanel();
        Label lbl = new Label( Constants.INSTANCE.OverCEPWindow() );
        lbl.setStyleName( "paddedLabel" );
        hp.add( lbl );

        cwo = new CEPWindowOperatorsDropdown( c,
                                              isReadOnly );
        if ( !isReadOnly ) {
            cwo.addValueChangeHandler( new ValueChangeHandler<OperatorSelection>() {

                public void onValueChange( ValueChangeEvent<OperatorSelection> event ) {
                    OperatorSelection selection = event.getValue();
                    String selected = selection.getValue();
                    c.getWindow().setOperator( selected );
                }
            } );
        }

        hp.add( cwo );
        return hp;
    }

    private void displayCEPOperators() {
        boolean isVisible = oracle.isFactTypeAnEvent( editingPattern.getFactType() );
        setAttributeVisibility( cepWindowRowIndex,
                                isVisible );
    }

}
