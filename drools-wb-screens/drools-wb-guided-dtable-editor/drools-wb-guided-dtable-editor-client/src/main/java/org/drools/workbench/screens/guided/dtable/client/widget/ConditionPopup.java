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
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.HasCEPWindow;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.CompositeColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.drools.workbench.screens.guided.rule.client.editor.BindingTextBox;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.CEPWindowOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.client.callbacks.Callback;
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
                                                   GuidedDecisionTableConstants.INSTANCE.LiteralValue() );
    private RadioButton formula = new RadioButton( "constraintValueType",
                                                   GuidedDecisionTableConstants.INSTANCE.Formula() );
    private RadioButton predicate = new RadioButton( "constraintValueType",
                                                     GuidedDecisionTableConstants.INSTANCE.Predicate() );

    private CEPWindowOperatorsDropdown cwo;
    private TextBox entryPointName;
    private int cepWindowRowIndex;

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;
    private final DTCellValueWidgetFactory factory;
    private final Validator validator;
    private final BRLRuleModel rm;
    private final DTCellValueUtilities utilities;

    private Pattern52 editingPattern;
    private ConditionCol52 editingCol;

    private final boolean isReadOnly;

    private InfoPopup fieldLabelInterpolationInfo = getPredicateHint();

    public ConditionPopup( final GuidedDecisionTable52 model,
                           final AsyncPackageDataModelOracle oracle,
                           final ConditionColumnCommand refreshGrid,
                           final ConditionCol52 col,
                           final boolean isNew,
                           final boolean isReadOnly ) {
        this.rm = new BRLRuleModel( model );
        Pattern52 originalPattern = model.getPattern( col );
        this.editingPattern = originalPattern != null ? originalPattern.clonePattern() : null;
        this.editingCol = cloneConditionColumn( col );
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
        this.isReadOnly = isReadOnly;
        this.validator = new Validator( model.getConditions() );
        this.utilities = new DTCellValueUtilities( model,
                                                   oracle );

        //Set-up a factory for value editors
        factory = DTCellValueWidgetFactory.getInstance( model,
                                                        oracle,
                                                        isReadOnly,
                                                        allowEmptyValues() );

        setTitle( GuidedDecisionTableConstants.INSTANCE.ConditionColumnConfiguration() );
        setModal( false );

        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        //Pattern selector
        ImageButton changePattern = new ImageButton( GuidedDecisionTableImageResources508.INSTANCE.Edit(),
                                                     GuidedDecisionTableImageResources508.INSTANCE.EditDisabled(),
                                                     GuidedDecisionTableConstants.INSTANCE.ChooseAnExistingPatternThatThisColumnAddsTo(),
                                                     new ClickHandler() {
                                                         public void onClick( ClickEvent w ) {
                                                             showChangePattern( w );
                                                         }
                                                     } );
        changePattern.setEnabled( !isReadOnly );
        pattern.add( changePattern );

        addAttribute( GuidedDecisionTableConstants.INSTANCE.Pattern(),
                      pattern );

        //Radio buttons for Calculation Type
        switch ( model.getTableFormat() ) {
            case EXTENDED_ENTRY:
                HorizontalPanel valueTypes = new HorizontalPanel();
                valueTypes.add( literal );
                valueTypes.add( formula );
                valueTypes.add( predicate );
                addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.CalculationType()).append(GuidedDecisionTableConstants.COLON).toString(),
                              valueTypes );

                switch ( editingCol.getConstraintValueType() ) {
                    case BaseSingleFieldConstraint.TYPE_LITERAL:
                        literal.setValue( true );
                        break;
                    case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                        formula.setValue( true );
                        break;
                    case BaseSingleFieldConstraint.TYPE_PREDICATE:
                        predicate.setValue( true );
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

                break;

            case LIMITED_ENTRY:
                binding.setEnabled( !isReadOnly );
        }

        //Fact field
        HorizontalPanel field = new HorizontalPanel();
        fieldLabel.setEnabled( !isReadOnly );
        field.add( fieldLabel );
        field.add( fieldLabelInterpolationInfo );
        this.editField = new ImageButton( GuidedDecisionTableImageResources508.INSTANCE.Edit(),
                                          GuidedDecisionTableImageResources508.INSTANCE.EditDisabled(),
                                          GuidedDecisionTableConstants.INSTANCE.EditTheFieldThatThisColumnOperatesOn(),
                                          new ClickHandler() {
                                              public void onClick( ClickEvent w ) {
                                                  showFieldChange();
                                              }
                                          } );
        editField.setEnabled( !isReadOnly );
        field.add( editField );
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.Field()).append(GuidedDecisionTableConstants.COLON).toString(),
                      field );
        doFieldLabel();

        //Operator
        HorizontalPanel operator = new HorizontalPanel();
        operator.add( operatorLabel );
        this.editOp = new ImageButton( GuidedDecisionTableImageResources508.INSTANCE.Edit(),
                                       GuidedDecisionTableImageResources508.INSTANCE.EditDisabled(),
                                       GuidedDecisionTableConstants.INSTANCE.EditTheOperatorThatIsUsedToCompareDataWithThisField(),
                                       new ClickHandler() {
                                           public void onClick( ClickEvent w ) {
                                               showOperatorChange();
                                           }
                                       } );
        editOp.setEnabled( !isReadOnly );
        operator.add( editOp );
        addAttribute( GuidedDecisionTableConstants.INSTANCE.Operator(),
                      operator );
        doOperatorLabel();
        doImageButtons();

        //Add CEP fields for patterns containing Facts declared as Events
        cepWindowRowIndex = addAttribute( GuidedDecisionTableConstants.INSTANCE.DTLabelOverCEPWindow(),
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
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.DTLabelFromEntryPoint()).append(GuidedDecisionTableConstants.COLON).toString(),
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
        addAttribute( GuidedDecisionTableConstants.INSTANCE.ColumnHeaderDescription(),
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
            vl.add( new InfoPopup( GuidedDecisionTableConstants.INSTANCE.ValueList(),
                                   GuidedDecisionTableConstants.INSTANCE.ValueListsExplanation() ) );
            addAttribute( GuidedDecisionTableConstants.INSTANCE.optionalValueList(),
                          vl );
        }

        //Default value
        if ( model.getTableFormat() == TableFormat.EXTENDED_ENTRY ) {
            defaultValueWidgetContainerIndex = addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.DefaultValue()).append(GuidedDecisionTableConstants.COLON).toString(),
                                                             defaultValueWidgetContainer );
            makeDefaultValueWidget();
        }

        //Limited entry value widget
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            limitedEntryValueAttributeIndex = addAttribute( GuidedDecisionTableConstants.INSTANCE.LimitedEntryValue(),
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
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.Binding()).append(GuidedDecisionTableConstants.COLON).toString(),
                      binding );

        //Hide column tick-box
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.HideThisColumn()).append(GuidedDecisionTableConstants.COLON).toString(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        //Initialise view
        doValueList();
        doCalculationType();
        initialiseViewForConstraintValueType( editingCol.getConstraintValueType() );

        //Apply button
        Button apply = new Button( GuidedDecisionTableConstants.INSTANCE.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }
                if ( editingCol.getConstraintValueType() != BaseSingleFieldConstraint.TYPE_PREDICATE ) {

                    //Field mandatory for Literals and Formulae
                    if ( null == editingCol.getFactField() || "".equals( editingCol.getFactField() ) ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.PleaseSelectOrEnterField() );
                        return;
                    }

                    //Operator optional for Literals and Formulae
                    if ( editingCol.getOperator() == null ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.NotifyNoSelectedOperator() );
                        return;
                    }

                } else {

                    //Clear operator for predicates, but leave field intact for interpolation of $param values
                    editingCol.setOperator( null );
                }

                //Check for unique binding
                if ( isNew ) {
                    if ( editingCol.isBound() && !isBindingUnique( editingCol.getBinding() ) ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                        return;
                    }
                } else {
                    if ( col.isBound() && editingCol.isBound() ) {
                        if ( !col.getBinding().equals( editingCol.getBinding() ) ) {
                            if ( editingCol.isBound() && !isBindingUnique( editingCol.getBinding() ) ) {
                                Window.alert( GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
                                return;
                            }
                        }
                    }
                }

                //Check column header is unique
                if ( isNew ) {
                    if ( !unique( editingCol.getHeader() ) ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
                        return;
                    }
                } else {
                    if ( !col.getHeader().equals( editingCol.getHeader() ) ) {
                        if ( !unique( editingCol.getHeader() ) ) {
                            Window.alert( GuidedDecisionTableConstants.INSTANCE.ThatColumnNameIsAlreadyInUsePleasePickAnother() );
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

        //Correct comma-separated Default Value if operator does not support it
        if ( !validator.doesOperatorAcceptCommaSeparatedValues( editingCol ) ) {
            utilities.removeCommaSeparatedValue( defaultValue );
        }

        defaultValueWidgetContainer.setWidget( factory.getWidget( editingPattern,
                                                                  editingCol,
                                                                  defaultValue ) );
    }

    private void applyConsTypeChange( int newConstraintValueType ) {
        editingCol.setConstraintValueType( newConstraintValueType );
        initialiseViewForConstraintValueType( newConstraintValueType );
    }

    private void initialiseViewForConstraintValueType( int constraintValueType ) {
        binding.setEnabled( constraintValueType == BaseSingleFieldConstraint.TYPE_LITERAL && !isReadOnly );
        doFieldLabel();
        doValueList();
        doOperatorLabel();
        doImageButtons();
        makeDefaultValueWidget();
    }

    private void doImageButtons() {
        int constraintType = editingCol.getConstraintValueType();
        boolean enableField = !( nil( editingPattern.getFactType() ) || constraintType == BaseSingleFieldConstraint.TYPE_PREDICATE || isReadOnly );
        boolean enableOp = !( nil( editingCol.getFactField() ) || constraintType == BaseSingleFieldConstraint.TYPE_PREDICATE || isReadOnly );
        this.editField.setEnabled( enableField );
        this.editOp.setEnabled( enableOp );
    }

    private boolean isBindingUnique( String binding ) {
        return !rm.isVariableNameUsed( binding );
    }

    private void doFieldLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            if ( this.editingCol.getFactField() == null || this.editingCol.getFactField().equals( "" ) ) {
                fieldLabel.setText( GuidedDecisionTableConstants.INSTANCE.notNeededForPredicate() );
            } else {
                fieldLabel.setText( this.editingCol.getFactField() );
            }
            fieldLabelInterpolationInfo.setVisible( true );
        } else if ( nil( editingPattern.getFactType() ) ) {
            fieldLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseSelectAPatternFirst() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else if ( nil( editingCol.getFactField() ) ) {
            fieldLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseSelectAField() );
            fieldLabelInterpolationInfo.setVisible( false );
        } else {
            fieldLabel.setText( this.editingCol.getFactField() );
        }
    }

    private void doOperatorLabel() {
        if ( editingCol.getConstraintValueType() == BaseSingleFieldConstraint.TYPE_PREDICATE ) {
            operatorLabel.setText( GuidedDecisionTableConstants.INSTANCE.notNeededForPredicate() );
        } else if ( nil( editingPattern.getFactType() ) ) {
            operatorLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseSelectAPatternFirst() );
        } else if ( nil( editingCol.getFactField() ) ) {
            operatorLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseChooseAFieldFirst() );
        } else if ( nil( editingCol.getOperator() ) ) {
            operatorLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseSelectAField() );
        } else {
            operatorLabel.setText( HumanReadable.getOperatorDisplayName( editingCol.getOperator() ) );
        }
    }

    private void doPatternLabel() {
        if ( editingPattern.getFactType() != null ) {
            StringBuilder patternLabel = new StringBuilder();
            String factType = editingPattern.getFactType();
            String boundName = editingPattern.getBoundName();
            if ( factType != null && factType.length() > 0 ) {
                if ( editingPattern.isNegated() ) {
                    patternLabel.append( GuidedDecisionTableConstants.INSTANCE.negatedPattern() ).append( " " ).append( factType );
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
        return new InfoPopup( GuidedDecisionTableConstants.INSTANCE.Predicates(),
                              GuidedDecisionTableConstants.INSTANCE.PredicatesInfo() );
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
        } else {
            valueListWidget.setText( editingCol.getValueList() );
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
                patterns.addItem( ( p.isNegated() ? GuidedDecisionTableConstants.INSTANCE.negatedPattern() + " " : "" )
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
        final String factType = editingPattern.getFactType();
        final String factField = editingCol.getFactField();
        this.oracle.getOperatorCompletions( factType,
                                            factField,
                                            new Callback<String[]>() {
                                                @Override
                                                public void callback( final String[] ops ) {
                                                    doShowOperatorChange( factType,
                                                                          factField,
                                                                          ops );
                                                }
                                            } );
    }

    private void doShowOperatorChange( final String factType,
                                       final String factField,
                                       final String[] ops ) {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( GuidedDecisionTableConstants.INSTANCE.SetTheOperator() );
        pop.setModal( false );

        //Operators "in" and "not in" are only allowed if the Calculation Type is a Literal
        final List<String> filteredOps = new ArrayList<String>();
        for ( String op : ops ) {
            filteredOps.add( op );
        }
        if ( BaseSingleFieldConstraint.TYPE_LITERAL != this.editingCol.getConstraintValueType() ) {
            filteredOps.remove( "in" );
            filteredOps.remove( "not in" );
        }

        final String[] displayOps = new String[ filteredOps.size() ];
        filteredOps.toArray( displayOps );

        final CEPOperatorsDropdown box = new CEPOperatorsDropdown( displayOps,
                                                                   editingCol );

        box.insertItem( GuidedDecisionTableConstants.INSTANCE.noOperator(),
                        "",
                        1 );
        pop.addAttribute( GuidedDecisionTableConstants.INSTANCE.Operator(),
                          box );
        Button b = new Button( GuidedDecisionTableConstants.INSTANCE.OK() );
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
        Button ok = new Button( GuidedDecisionTableConstants.INSTANCE.OK() );
        HorizontalPanel hp = new HorizontalPanel();
        hp.add( pats );
        hp.add( ok );

        pop.addAttribute( GuidedDecisionTableConstants.INSTANCE.ChooseExistingPatternToAddColumnTo(),
                          hp );
        pop.addAttribute( "",
                          new HTML( GuidedDecisionTableConstants.INSTANCE.ORwithEmphasis() ) );

        Button createPattern = new Button( GuidedDecisionTableConstants.INSTANCE.CreateNewFactPattern() );
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
                doImageButtons();

                pop.hide();
            }
        } );

        pop.show();
    }

    protected void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        final ListBox box = new ListBox();
        pop.setModal( false );

        this.oracle.getFieldCompletions( this.editingPattern.getFactType(),
                                         FieldAccessorsAndMutators.ACCESSOR,
                                         new Callback<ModelField[]>() {
                                             @Override
                                             public void callback( final ModelField[] fields ) {
                                                 switch ( editingCol.getConstraintValueType() ) {
                                                     case BaseSingleFieldConstraint.TYPE_LITERAL:
                                                         //Literals can be on any field
                                                         for ( int i = 0; i < fields.length; i++ ) {
                                                             box.addItem( fields[ i ].getName() );
                                                         }
                                                         break;

                                                     case BaseSingleFieldConstraint.TYPE_RET_VALUE:
                                                         //Formulae can only consume fields that do not have enumerations
                                                         for ( int i = 0; i < fields.length; i++ ) {
                                                             final String fieldName = fields[ i ].getName();
                                                             if ( !oracle.hasEnums( editingPattern.getFactType(),
                                                                                    fieldName ) ) {
                                                                 box.addItem( fieldName );
                                                             }
                                                         }
                                                         break;

                                                     case BaseSingleFieldConstraint.TYPE_PREDICATE:
                                                         //Predicates don't need a field (this should never be reachable as the
                                                         //field selector is disabled when the Calculation Type is Predicate)
                                                         break;

                                                 }
                                             }
                                         } );

        pop.addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.Field()).append(GuidedDecisionTableConstants.COLON).toString(),
                          box );
        Button b = new Button( GuidedDecisionTableConstants.INSTANCE.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                editingCol.setFactField( box.getItemText( box.getSelectedIndex() ) );
                editingCol.setFieldType( oracle.getFieldType( editingPattern.getFactType(),
                                                              editingCol.getFactField() ) );

                //Clear Operator when field changes
                editingCol.setOperator( null );
                editingCol.setValueList( null );

                //Setup UI
                doFieldLabel();
                doValueList();
                doCalculationType();
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doOperatorLabel();
                doImageButtons();

                pop.hide();
            }
        } );
        pop.show();
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( GuidedDecisionTableConstants.INSTANCE.CreateANewFactPattern() );
        final ListBox types = new ListBox();
        for ( int i = 0; i < oracle.getFactTypes().length; i++ ) {
            types.addItem( oracle.getFactTypes()[ i ] );
        }
        pop.addAttribute( GuidedDecisionTableConstants.INSTANCE.FactType(),
                          types );
        final TextBox binding = new BindingTextBox();
        binding.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                binding.setText( binding.getText().replace( " ",
                                                            "" ) );
            }
        } );
        pop.addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.Binding()).append(GuidedDecisionTableConstants.COLON).toString(),
                          binding );

        //Patterns can be negated, i.e. "not Pattern(...)"
        final CheckBox chkNegated = new CheckBox();
        chkNegated.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                boolean isPatternNegated = chkNegated.getValue();
                binding.setEnabled( !isPatternNegated );
            }

        } );
        pop.addAttribute( GuidedDecisionTableConstants.INSTANCE.negatePattern(),
                          chkNegated );

        Button ok = new Button( GuidedDecisionTableConstants.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {

                boolean isPatternNegated = chkNegated.getValue();
                String ft = types.getItemText( types.getSelectedIndex() );
                String fn = isPatternNegated ? "" : binding.getText();
                if ( !isPatternNegated ) {
                    if ( fn.equals( "" ) ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameForFact() );
                        return;
                    } else if ( fn.equals( ft ) ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameThatIsNotTheSameAsTheFactType() );
                        return;
                    } else if ( !isBindingUnique( fn ) ) {
                        Window.alert( GuidedDecisionTableConstants.INSTANCE.PleaseEnterANameThatIsNotAlreadyUsedByAnotherPattern() );
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
                doImageButtons();

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
        Label lbl = new Label( GuidedDecisionTableConstants.INSTANCE.OverCEPWindow() );
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
        oracle.isFactTypeAnEvent( editingPattern.getFactType(),
                                  new Callback<Boolean>() {
                                      @Override
                                      public void callback( final Boolean result ) {
                                          setAttributeVisibility( cepWindowRowIndex,
                                                                  Boolean.TRUE.equals( result ) );
                                      }
                                  } );
    }

}
