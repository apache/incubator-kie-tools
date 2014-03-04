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
package org.drools.workbench.screens.guided.scorecard.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.IdentityColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.guided.scorecard.shared.Attribute;
import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.client.resources.i18n.GuidedScoreCardConstants;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.TextBoxFactory;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.DirtyableFlexTable;

public class GuidedScoreCardEditor extends Composite {

    private static final String[] reasonCodeAlgorithms = new String[]{ "none", "pointsAbove", "pointsBelow" };
    private static final String[] typesForAttributes = new String[]{ "String", "int", "double", "boolean", "Integer" };
    private static final String[] typesForScore = new String[]{ "double" };
    private static final String[] typesForRC = new String[]{ "List" };

    private static final String[] stringOperators = new String[]{ "=", "in" };
    private static final String[] booleanOperators = new String[]{ "false", "true" };
    private static final String[] numericOperators = new String[]{ "=", ">", "<", ">=", "<=", ">..<", ">=..<", ">=..<=", ">..<=" };

    private SimplePanel container = new SimplePanel();

    private Button btnAddCharacteristic;
    private VerticalPanel characteristicsPanel;
    private List<DirtyableFlexTable> characteristicsTables = new ArrayList<DirtyableFlexTable>();
    private Map<DirtyableFlexTable, ListDataProvider<Attribute>> characteristicsAttrMap = new HashMap<DirtyableFlexTable, ListDataProvider<Attribute>>();

    private ListBox ddUseReasonCode;
    private ListBox ddReasonCodeAlgorithm;
    private ListBox ddReasonCodeField;
    private TextBox tbBaselineScore;
    private TextBox tbInitialScore;
    private Grid scorecardPropertiesGrid;

    private ListBox dropDownFields = new ListBox();
    private ListBox dropDownFacts = new ListBox();

    private ScoreCardModel model;
    private AsyncPackageDataModelOracle oracle;

    public GuidedScoreCardEditor() {
        initWidget( container );
    }

    public void setContent( final ScoreCardModel model,
                            final AsyncPackageDataModelOracle oracle ) {
        this.model = model;
        this.oracle = oracle;

        final DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel( GuidedScoreCardConstants.INSTANCE.scoreCardTitle0( model.getName() ) );
        disclosurePanel.setWidth( "100%" );
        disclosurePanel.setTitle( GuidedScoreCardConstants.INSTANCE.scorecard() );
        disclosurePanel.setOpen( true );

        final DecoratedDisclosurePanel configPanel = new DecoratedDisclosurePanel( GuidedScoreCardConstants.INSTANCE.setupParameters() );
        configPanel.setWidth( "95%" );
        configPanel.setOpen( true );
        configPanel.add( getScorecardProperties() );

        final DecoratedDisclosurePanel characteristicsPanel = new DecoratedDisclosurePanel( GuidedScoreCardConstants.INSTANCE.characteristics() );
        characteristicsPanel.setOpen( model.getCharacteristics().size() > 0 );
        characteristicsPanel.setWidth( "95%" );
        characteristicsPanel.add( getCharacteristics() );

        final VerticalPanel config = new VerticalPanel();
        config.setWidth( "100%" );
        config.add( configPanel );
        config.add( characteristicsPanel );

        disclosurePanel.add( config );
        container.setWidget( disclosurePanel );

        for ( final Characteristic characteristic : model.getCharacteristics() ) {
            final DirtyableFlexTable flexTable = addCharacteristic( characteristic );
            for ( Attribute attribute : characteristic.getAttributes() ) {
                addAttribute( flexTable,
                              attribute );
            }
        }
    }

    public ScoreCardModel getModel() {
        model.setBaselineScore( Double.parseDouble( tbBaselineScore.getValue() ) );
        model.setInitialScore( Double.parseDouble( tbInitialScore.getValue() ) );
        model.setReasonCodesAlgorithm( ddReasonCodeAlgorithm.getValue( ddReasonCodeAlgorithm.getSelectedIndex() ) );
        model.setUseReasonCodes( ddUseReasonCode.getSelectedIndex() == 1 );

        ListBox enumDropDown = (ListBox) scorecardPropertiesGrid.getWidget( 1,
                                                                            0 );
        if ( enumDropDown.getSelectedIndex() > -1 ) {
            final String simpleFactName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
            model.setFactName( simpleFactName );
            oracle.getFieldCompletions( simpleFactName,
                                        new Callback<ModelField[]>() {
                                            @Override
                                            public void callback( final ModelField[] fields ) {
                                                if ( fields != null ) {
                                                    for ( final ModelField mf : fields ) {
                                                        if ( mf.getType().equals( simpleFactName ) ) {
                                                            model.setFactName( mf.getClassName() );
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } );
        }

        enumDropDown = (ListBox) scorecardPropertiesGrid.getWidget( 1,
                                                                    1 );
        if ( enumDropDown.getSelectedIndex() > -1 ) {
            String fieldName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
            fieldName = fieldName.substring( 0, fieldName.indexOf( ":" ) ).trim();
            model.setFieldName( fieldName );
        } else {
            model.setFieldName( "" );
        }

        if ( ddReasonCodeField.getSelectedIndex() > -1 ) {
            String rcField = ddReasonCodeField.getValue( ddReasonCodeField.getSelectedIndex() );
            rcField = rcField.substring( 0, rcField.indexOf( ":" ) ).trim();
            model.setReasonCodeField( rcField );
        }

        model.getCharacteristics().clear();
        for ( final DirtyableFlexTable flexTable : characteristicsTables ) {
            final Characteristic characteristic = new Characteristic();
            characteristic.setName( ( (TextBox) flexTable.getWidget( 0,
                                                                     1 ) ).getValue() );

            //Characteristic Fact Type
            enumDropDown = (ListBox) flexTable.getWidget( 2,
                                                          0 );
            if ( enumDropDown.getSelectedIndex() > -1 ) {
                final String simpleFactName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
                characteristic.setFact( simpleFactName );
                oracle.getFieldCompletions( simpleFactName,
                                            new Callback<ModelField[]>() {
                                                @Override
                                                public void callback( final ModelField[] fields ) {
                                                    if ( fields != null ) {
                                                        for ( ModelField mf : fields ) {
                                                            if ( mf.getType().equals( simpleFactName ) ) {
                                                                characteristic.setFact( mf.getClassName() );
                                                                break;
                                                            }
                                                        }
                                                    }
                                                }
                                            } );

                //Characteristic Field (cannot be set if no Fact Type has been set)
                enumDropDown = (ListBox) flexTable.getWidget( 2,
                                                              1 );
                if ( enumDropDown.getSelectedIndex() > -1 ) {
                    String fieldName = enumDropDown.getValue( enumDropDown.getSelectedIndex() );
                    fieldName = fieldName.substring( 0, fieldName.indexOf( ":" ) ).trim();
                    characteristic.setField( fieldName );
                } else {
                    characteristic.setField( "" );
                }
                getDataTypeForField( simpleFactName,
                                     characteristic.getField(),
                                     new Callback<String>() {
                                         @Override
                                         public void callback( final String result ) {
                                             characteristic.setDataType( result );
                                         }
                                     } );
            }

            //Characteristic Reason Code
            characteristic.setReasonCode( ( (TextBox) flexTable.getWidget( 2,
                                                                           3 ) ).getValue() );

            //Characteristic Base Line Score
            final String baselineScore = ( (TextBox) flexTable.getWidget( 2,
                                                                          2 ) ).getValue();
            try {
                characteristic.setBaselineScore( Double.parseDouble( baselineScore ) );
            } catch ( Exception e ) {
                characteristic.setBaselineScore( 0.0d );
            }

            //Characteristic Attributes
            characteristic.getAttributes().clear();
            characteristic.getAttributes().addAll( characteristicsAttrMap.get( flexTable ).getList() );

            model.getCharacteristics().add( characteristic );
        }

        return model;
    }

    public void refreshFactTypes() {
        dropDownFacts.clear();
        final String[] eligibleFacts = oracle.getFactTypes();
        for ( final String factType : eligibleFacts ) {
            dropDownFacts.addItem( factType );
        }

        String factName = model.getFactName();
        // if fact is a fully qualified className, strip off the packageName
        if ( factName.lastIndexOf( "." ) > -1 ) {
            factName = factName.substring( factName.lastIndexOf( "." ) + 1 );
        }

        final int selectedFactIndex = Arrays.asList( eligibleFacts ).indexOf( factName );
        dropDownFacts.setSelectedIndex( selectedFactIndex >= 0 ? selectedFactIndex : 0 );
        scoreCardPropertyFactChanged( dropDownFacts,
                                      dropDownFields );

    }

    private Widget getScorecardProperties() {

        scorecardPropertiesGrid = new Grid( 4,
                                            4 );
        scorecardPropertiesGrid.setCellSpacing( 5 );
        scorecardPropertiesGrid.setCellPadding( 5 );

        tbInitialScore = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_DOUBLE );
        tbInitialScore.setText( Double.toString( model.getInitialScore() ) );

        String factName = model.getFactName();
        // if fact is a fully qualified className, strip off the packageName
        if ( factName.lastIndexOf( "." ) > -1 ) {
            factName = factName.substring( factName.lastIndexOf( "." ) + 1 );
        }

        final String[] eligibleFacts = oracle.getFactTypes();
        for ( final String factType : eligibleFacts ) {
            dropDownFacts.addItem( factType );
        }
        dropDownFacts.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( final ChangeEvent event ) {
                scoreCardPropertyFactChanged( dropDownFacts,
                                              dropDownFields );
            }

        } );
        final int selectedFactIndex = Arrays.asList( eligibleFacts ).indexOf( factName );
        dropDownFacts.setSelectedIndex( selectedFactIndex >= 0 ? selectedFactIndex : 0 );
        scoreCardPropertyFactChanged( dropDownFacts,
                                      dropDownFields );

        //Reason Codes List Box
        ddReasonCodeField = new ListBox();
        getEligibleFields( factName,
                           typesForRC,
                           new Callback<String[]>() {
                               @Override
                               public void callback( final String[] eligibleReasonCodeFields ) {
                                   for ( final String field : eligibleReasonCodeFields ) {
                                       ddReasonCodeField.addItem( field );
                                   }
                                   final String rcField = model.getReasonCodeField() + " : List";
                                   final int selectedReasonCodeIndex = Arrays.asList( eligibleReasonCodeFields ).indexOf( rcField );
                                   ddReasonCodeField.setSelectedIndex( selectedReasonCodeIndex >= 0 ? selectedReasonCodeIndex : 0 );
                               }
                           } );

        final boolean useReasonCodes = model.isUseReasonCodes();
        String reasonCodesAlgo = model.getReasonCodesAlgorithm();
        if ( reasonCodesAlgo == null || reasonCodesAlgo.trim().length() == 0 ) {
            reasonCodesAlgo = "none";
        }

        ddUseReasonCode = booleanEditor( Boolean.toString( useReasonCodes ) );
        ddReasonCodeAlgorithm = listBoxEditor( reasonCodeAlgorithms,
                                               reasonCodesAlgo );
        tbBaselineScore = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_DOUBLE );

        scorecardPropertiesGrid.setText( 0,
                                         0,
                                         GuidedScoreCardConstants.INSTANCE.facts() );
        scorecardPropertiesGrid.setText( 0,
                                         1,
                                         GuidedScoreCardConstants.INSTANCE.resultantScoreField() );
        scorecardPropertiesGrid.setText( 0,
                                         2,
                                         GuidedScoreCardConstants.INSTANCE.initialScore() );

        scorecardPropertiesGrid.setWidget( 1,
                                           0,
                                           dropDownFacts );
        scorecardPropertiesGrid.setWidget( 1,
                                           1,
                                           dropDownFields );
        scorecardPropertiesGrid.setWidget( 1,
                                           2,
                                           tbInitialScore );

        scorecardPropertiesGrid.setText( 2,
                                         0,
                                         GuidedScoreCardConstants.INSTANCE.useReasonCodes() );
        scorecardPropertiesGrid.setText( 2,
                                         1,
                                         GuidedScoreCardConstants.INSTANCE.resultantReasonCodesField() );
        scorecardPropertiesGrid.setText( 2,
                                         2,
                                         GuidedScoreCardConstants.INSTANCE.reasonCodesAlgorithm() );
        scorecardPropertiesGrid.setText( 2,
                                         3,
                                         GuidedScoreCardConstants.INSTANCE.baselineScore() );

        scorecardPropertiesGrid.setWidget( 3,
                                           0,
                                           ddUseReasonCode );
        scorecardPropertiesGrid.setWidget( 3,
                                           1,
                                           ddReasonCodeField );
        scorecardPropertiesGrid.setWidget( 3,
                                           2,
                                           ddReasonCodeAlgorithm );
        scorecardPropertiesGrid.setWidget( 3,
                                           3,
                                           tbBaselineScore );

        /* TODO : Remove this explicitly Disabled Reasoncode support field*/
        ddUseReasonCode.setEnabled( false );
        ddReasonCodeField.setEnabled( false );

        tbBaselineScore.setText( Double.toString( model.getBaselineScore() ) );

        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             0,
                                                             "200px" );
        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             1,
                                                             "250px" );
        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             2,
                                                             "200px" );
        scorecardPropertiesGrid.getCellFormatter().setWidth( 0,
                                                             3,
                                                             "200px" );

        return scorecardPropertiesGrid;
    }

    private void scoreCardPropertyFactChanged( final ListBox dropDownFacts,
                                               final ListBox dropDownFields ) {
        final int selectedIndex = dropDownFacts.getSelectedIndex();
        dropDownFields.clear();
        if ( selectedIndex < 0 ) {
            return;
        }
        final String selectedFactType = dropDownFacts.getItemText( selectedIndex );
        getEligibleFields( selectedFactType,
                           typesForScore,
                           new Callback<String[]>() {
                               @Override
                               public void callback( final String[] eligibleFieldsForSelectedFactType ) {
                                   for ( final String field : eligibleFieldsForSelectedFactType ) {
                                       dropDownFields.addItem( field );
                                   }
                                   final String qualifiedFieldName = model.getFieldName() + " : double";
                                   final int selectedFieldIndex = Arrays.asList( eligibleFieldsForSelectedFactType ).indexOf( qualifiedFieldName );
                                   dropDownFields.setSelectedIndex( selectedFieldIndex >= 0 ? selectedFieldIndex : 0 );
                               }
                           } );
    }

    private Widget getCharacteristics() {
        characteristicsPanel = new VerticalPanel();
        final HorizontalPanel toolbar = new HorizontalPanel();
        btnAddCharacteristic = new Button( GuidedScoreCardConstants.INSTANCE.addCharacteristic(),
                                           new ClickHandler() {
                                               public void onClick( ClickEvent event ) {
                                                   addCharacteristic( null );
                                               }
                                           } );
        toolbar.add( btnAddCharacteristic );

        toolbar.setHeight( "24" );
        characteristicsPanel.add( toolbar );
        final SimplePanel gapPanel = new SimplePanel();
        gapPanel.add( new HTML( "<br/>" ) );
        characteristicsPanel.add( gapPanel );
        return characteristicsPanel;
    }

    private void removeCharacteristic( final DirtyableFlexTable selectedTable ) {
        if ( selectedTable != null ) {
            final TextBox tbName = (TextBox) selectedTable.getWidget( 0,
                                                                      1 );
            String name = tbName.getValue();
            if ( name == null || name.trim().length() == 0 ) {
                name = "Untitled";
            }
            final String msg = GuidedScoreCardConstants.INSTANCE.promptDeleteCharacteristic0( name );
            if ( Window.confirm( msg ) ) {
                characteristicsTables.remove( selectedTable );
                characteristicsAttrMap.remove( selectedTable );
                final Widget parent = selectedTable.getParent().getParent();
                final int i = characteristicsPanel.getWidgetIndex( parent );
                characteristicsPanel.remove( parent );
                characteristicsPanel.remove( i );
            }
        }
    }

    private void addAttribute( final DirtyableFlexTable selectedTable,
                               final Attribute attribute ) {
        Attribute newAttribute = null;
        if ( attribute != null ) {
            characteristicsAttrMap.get( selectedTable ).getList().add( attribute );
        } else {
            newAttribute = new Attribute();
            characteristicsAttrMap.get( selectedTable ).getList().add( newAttribute );
        }
        characteristicsAttrMap.get( selectedTable ).refresh();

        //Disable the fact & field dropdowns
        ( (ListBox) selectedTable.getWidget( 2,
                                             0 ) ).setEnabled( false );
        ( (ListBox) selectedTable.getWidget( 2,
                                             1 ) ).setEnabled( false );
        final ListBox edd = ( (ListBox) selectedTable.getWidget( 2,
                                                                 1 ) );
        if ( edd.getSelectedIndex() > -1 ) {
            String field = edd.getValue( edd.getSelectedIndex() );
            field = field.substring( field.indexOf( ":" ) + 1 ).trim();
            final CellTable<Attribute> cellTable = (CellTable<Attribute>) characteristicsAttrMap.get( selectedTable ).getDataDisplays().iterator().next();
            final DynamicSelectionCell dynamicSelectionCell = (DynamicSelectionCell) cellTable.getColumn( 0 ).getCell();
            List<String> newOptions = null;
            if ( "double".equalsIgnoreCase( field ) || "int".equalsIgnoreCase( field ) || endsWithIgnoreCase(field, "integer")) {
                newOptions = Arrays.asList( numericOperators );
            } else if ( "boolean".equalsIgnoreCase( field ) ) {
                newOptions = Arrays.asList( booleanOperators );
                CustomEditTextCell etc = (CustomEditTextCell) cellTable.getColumn( 1 ).getCell();
                etc.setEnabled( false );
                ( (Button) selectedTable.getWidget( 0, 3 ) ).setEnabled( characteristicsAttrMap.get( selectedTable ).getList().size() != 2 );
                if ( newAttribute != null ) {
                    newAttribute.setValue( GuidedScoreCardConstants.INSTANCE.notApplicable() );
                }
            } else if ( "String".equalsIgnoreCase( field ) ) {
                newOptions = Arrays.asList( stringOperators );
            }
            if ( newOptions != null ) {
                dynamicSelectionCell.setOptions( newOptions );
            }
            if ( newAttribute != null ) {
                if ( newOptions != null ) {
                    newAttribute.setOperator( newOptions.get( 0 ) );
                }
            }
        }
    }

    private DirtyableFlexTable addCharacteristic( final Characteristic characteristic ) {
        final DirtyableFlexTable cGrid = new DirtyableFlexTable();
        cGrid.setBorderWidth( 0 );
        cGrid.setCellPadding( 1 );
        cGrid.setCellSpacing( 1 );

        cGrid.setStyleName( "rule-ListHeader" );

        Button btnAddAttribute = new Button( GuidedScoreCardConstants.INSTANCE.addAttribute(),
                                             new ClickHandler() {
                                                 public void onClick( final ClickEvent event ) {
                                                     addAttribute( cGrid,
                                                                   null );
                                                 }
                                             } );

        Button btnRemoveCharacteristic = new Button( GuidedScoreCardConstants.INSTANCE.removeCharacteristic(),
                                                     new ClickHandler() {
                                                         public void onClick( ClickEvent event ) {
                                                             removeCharacteristic( cGrid );
                                                         }
                                                     } );

        String selectedFact = "";
        if ( characteristic != null ) {
            selectedFact = characteristic.getFact();
            if ( selectedFact.lastIndexOf( "." ) > -1 ) {
                selectedFact = selectedFact.substring( selectedFact.lastIndexOf( "." ) + 1 );
            }
        }

        //Fields List Box
        final ListBox dropDownFields = new ListBox();

        //Facts List Box
        final ListBox dropDownFacts = new ListBox();
        final String[] eligibleFacts = oracle.getFactTypes();
        for ( final String factType : eligibleFacts ) {
            dropDownFacts.addItem( factType );
        }
        dropDownFacts.addChangeHandler( new ChangeHandler() {

            @Override
            public void onChange( final ChangeEvent event ) {
                characteristicFactChanged( characteristic,
                                           dropDownFacts,
                                           dropDownFields );
            }

        } );
        final int selectedFactIndex = Arrays.asList( eligibleFacts ).indexOf( selectedFact );
        dropDownFacts.setSelectedIndex( selectedFactIndex >= 0 ? selectedFactIndex : 0 );
        characteristicFactChanged( characteristic,
                                   dropDownFacts,
                                   dropDownFields );

        cGrid.setWidget( 0,
                         0,
                         new Label( GuidedScoreCardConstants.INSTANCE.name() ) );
        final TextBox tbName = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
        cGrid.setWidget( 0,
                         1,
                         tbName );
        cGrid.setWidget( 0,
                         2,
                         btnRemoveCharacteristic );
        cGrid.setWidget( 0,
                         3,
                         btnAddAttribute );

        cGrid.setWidget( 1,
                         0,
                         new Label( GuidedScoreCardConstants.INSTANCE.fact() ) );
        cGrid.setWidget( 1,
                         1,
                         new Label( GuidedScoreCardConstants.INSTANCE.characteristic() ) );
        cGrid.setWidget( 1,
                         2,
                         new Label( GuidedScoreCardConstants.INSTANCE.baselineScore() ) );
        cGrid.setWidget( 1,
                         3,
                         new Label( GuidedScoreCardConstants.INSTANCE.reasonCode() ) );

        cGrid.setWidget( 2,
                         0,
                         dropDownFacts );
        cGrid.setWidget( 2,
                         1,
                         dropDownFields );

        final TextBox tbBaseline = TextBoxFactory.getTextBox( DataType.TYPE_NUMERIC_DOUBLE );
        final boolean useReasonCodesValue = "true".equalsIgnoreCase( ddUseReasonCode.getValue( ddUseReasonCode.getSelectedIndex() ) );
        tbBaseline.setEnabled( useReasonCodesValue );
        cGrid.setWidget( 2,
                         2,
                         tbBaseline );

        final TextBox tbReasonCode = TextBoxFactory.getTextBox( DataType.TYPE_STRING );
        tbReasonCode.setEnabled( useReasonCodesValue );
        cGrid.setWidget( 2,
                         3,
                         tbReasonCode );

        final SimplePanel gapPanel = new SimplePanel();
        gapPanel.add( new HTML( "<br/>" ) );

        final VerticalPanel panel = new VerticalPanel();
        panel.add( cGrid );
        panel.add( addAttributeCellTable( cGrid,
                                          characteristic ) );
        panel.setWidth( "100%" );
        DecoratorPanel decoratorPanel = new DecoratorPanel();
        decoratorPanel.add( panel );

        characteristicsPanel.add( decoratorPanel );
        characteristicsPanel.add( gapPanel );
        characteristicsTables.add( cGrid );

        cGrid.getColumnFormatter().setWidth( 0,
                                             "150px" );
        cGrid.getColumnFormatter().setWidth( 1,
                                             "250px" );
        cGrid.getColumnFormatter().setWidth( 2,
                                             "150px" );
        cGrid.getColumnFormatter().setWidth( 3,
                                             "150px" );

        if ( characteristic != null ) {
            tbReasonCode.setValue( characteristic.getReasonCode() );
            tbBaseline.setValue( "" + characteristic.getBaselineScore() );
            tbName.setValue( characteristic.getName() );
        }

        return cGrid;
    }

    private void characteristicFactChanged( final Characteristic characteristic,
                                            final ListBox dropDownFacts,
                                            final ListBox dropDownFields ) {
        final int selectedIndex = dropDownFacts.getSelectedIndex();
        final String selectedFactType = dropDownFacts.getItemText( selectedIndex );
        getEligibleFields( selectedFactType,
                           typesForAttributes,
                           new Callback<String[]>() {
                               @Override
                               public void callback( final String[] eligibleFieldsForSelectedFactType ) {
                                   String selectedField = "";
                                   if ( characteristic != null ) {
                                       selectedField = characteristic.getField();
                                       selectedField = selectedField + " : " + characteristic.getDataType();
                                   }

                                   dropDownFields.clear();
                                   for ( final String field : eligibleFieldsForSelectedFactType ) {
                                       dropDownFields.addItem( field );
                                   }
                                   final int selectedFieldIndex = Arrays.asList( eligibleFieldsForSelectedFactType ).indexOf( selectedField );
                                   dropDownFields.setSelectedIndex( selectedFieldIndex >= 0 ? selectedFieldIndex : 0 );
                               }
                           } );
    }

    private Widget addAttributeCellTable( final DirtyableFlexTable cGrid,
                                          final Characteristic characteristic ) {
        final CellTable<Attribute> attributeCellTable = new CellTable<Attribute>();
        final List<String> operators = new ArrayList<String>();
        String dataType;
        if ( characteristic == null ) {
            dataType = "String";
        } else {
            dataType = characteristic.getDataType();
        }

        if ( "String".equalsIgnoreCase( dataType ) ) {
            operators.addAll( Arrays.asList( stringOperators ) );
        } else if ( "boolean".equalsIgnoreCase( dataType ) ) {
            operators.addAll( Arrays.asList( booleanOperators ) );
        } else {
            operators.addAll( Arrays.asList( numericOperators ) );
        }

        //Operators column
        final DynamicSelectionCell categoryCell = new DynamicSelectionCell( operators );
        final Column<Attribute, String> operatorColumn = new Column<Attribute, String>( categoryCell ) {
            public String getValue( final Attribute object ) {
                return object.getOperator();
            }
        };
        operatorColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                object.setOperator( value );
                attributeCellTable.redraw();
            }
        } );

        //Value column
        final Column<Attribute, String> valueColumn = new Column<Attribute, String>( new CustomEditTextCell() ) {
            public String getValue( final Attribute attribute ) {
                return attribute.getValue();
            }
        };
        valueColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                object.setValue( value );
                attributeCellTable.redraw();
            }
        } );

        //Partial Score column
        final EditTextCell partialScoreCell = new EditTextCell();
        final Column<Attribute, String> partialScoreColumn = new Column<Attribute, String>( partialScoreCell ) {
            public String getValue( final Attribute attribute ) {
                return "" + attribute.getPartialScore();
            }
        };
        partialScoreColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                try {
                    double d = Double.parseDouble( value );
                    object.setPartialScore( d );
                } catch ( Exception e1 ) {
                    partialScoreCell.clearViewData( object );
                }
                attributeCellTable.redraw();
            }
        } );

        //Reason Code column
        final Column<Attribute, String> reasonCodeColumn = new Column<Attribute, String>( new EditTextCell() ) {
            public String getValue( final Attribute attribute ) {
                return attribute.getReasonCode();
            }
        };
        reasonCodeColumn.setFieldUpdater( new FieldUpdater<Attribute, String>() {
            public void update( int index,
                                Attribute object,
                                String value ) {
                object.setReasonCode( value );
                attributeCellTable.redraw();
            }
        } );

        final ActionCell.Delegate<Attribute> delegate = new ActionCell.Delegate<Attribute>() {
            public void execute( final Attribute attribute ) {
                if ( Window.confirm( GuidedScoreCardConstants.INSTANCE.promptDeleteAttribute() ) ) {
                    final List<Attribute> list = characteristicsAttrMap.get( cGrid ).getList();
                    list.remove( attribute );
                    ( (ListBox) cGrid.getWidget( 2,
                                                 0 ) ).setEnabled( list.size() == 0 );
                    ( (ListBox) cGrid.getWidget( 2,
                                                 1 ) ).setEnabled( list.size() == 0 );
                    ( (Button) cGrid.getWidget( 0,
                                                3 ) ).setEnabled( list.size() != 2 );
                    attributeCellTable.redraw();
                }
            }
        };

        final Cell<Attribute> actionCell = new ActionCell<Attribute>( GuidedScoreCardConstants.INSTANCE.remove(),
                                                                      delegate );
        final Column<Attribute, String> actionColumn = new IdentityColumn( actionCell );

        // Add the columns.
        attributeCellTable.addColumn( operatorColumn,
                                      GuidedScoreCardConstants.INSTANCE.operator() );
        attributeCellTable.addColumn( valueColumn,
                                      GuidedScoreCardConstants.INSTANCE.value() );
        attributeCellTable.addColumn( partialScoreColumn,
                                      GuidedScoreCardConstants.INSTANCE.partialScore() );
        attributeCellTable.addColumn( reasonCodeColumn,
                                      GuidedScoreCardConstants.INSTANCE.reasonCode() );
        attributeCellTable.addColumn( actionColumn,
                                      GuidedScoreCardConstants.INSTANCE.actions() );
        attributeCellTable.setWidth( "100%",
                                     true );

        attributeCellTable.setColumnWidth( operatorColumn,
                                           5.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( valueColumn,
                                           10.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( partialScoreColumn,
                                           10.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( reasonCodeColumn,
                                           10.0,
                                           Style.Unit.PCT );
        attributeCellTable.setColumnWidth( actionColumn,
                                           5.0,
                                           Style.Unit.PCT );

        ListDataProvider<Attribute> dataProvider = new ListDataProvider<Attribute>();
        dataProvider.addDataDisplay( attributeCellTable );
        characteristicsAttrMap.put( cGrid,
                                    dataProvider );
        return ( attributeCellTable );
    }

    private void getEligibleFields( final String factName,
                                    final String[] types,
                                    final Callback<String[]> callback ) {
        oracle.getFieldCompletions( factName,
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            final List<String> eligibleFieldNames = new ArrayList<String>();
                                            for ( final ModelField field : fields ) {
                                                String type = field.getClassName();
                                                if ( type.lastIndexOf( "." ) > -1 ) {
                                                    type = type.substring( type.lastIndexOf( "." ) + 1 );
                                                }
                                                for ( final String t : types ) {
                                                    if ( type.equalsIgnoreCase( t ) ) {
                                                        eligibleFieldNames.add( field.getName() + " : " + type );
                                                        break;
                                                    }
                                                }
                                            }
                                            callback.callback( eligibleFieldNames.toArray( new String[]{ } ) );
                                        }
                                    } );
    }

    private void getDataTypeForField( final String factName,
                                      final String fieldName,
                                      final Callback<String> callback ) {
        oracle.getFieldCompletions( factName,
                                    new Callback<ModelField[]>() {

                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            for ( final ModelField field : fields ) {
                                                if ( fieldName.equalsIgnoreCase( field.getName() ) ) {
                                                    String type = field.getClassName();
                                                    if ( type.endsWith( "String" ) ) {
                                                        type = "String";
                                                    } else if ( type.endsWith( "Double" ) ) {
                                                        type = "Double";
                                                    } else if ( endsWithIgnoreCase(type, "integer")) {
                                                        type = "int";
                                                    }
                                                    callback.callback( type );
                                                    return;
                                                }
                                            }
                                            callback.callback( null );
                                        }
                                    } );
    }

    private ListBox booleanEditor( final String currentValue ) {
        final ListBox listBox = listBoxEditor( booleanOperators,
                                               currentValue );
        listBox.addChangeHandler( new ChangeHandler() {
            @Override
            public void onChange( final ChangeEvent event ) {
                final int selectedIndex = listBox.getSelectedIndex();
                final String selectedValue = listBox.getItemText( selectedIndex );
                final boolean enabled = "true".equalsIgnoreCase( selectedValue );
                ddReasonCodeAlgorithm.setEnabled( enabled );
                tbBaselineScore.setEnabled( enabled );
                ddReasonCodeField.setEnabled( enabled );
                for ( final DirtyableFlexTable cGrid : characteristicsTables ) {
                    //baseline score for each characteristic
                    ( (TextBox) cGrid.getWidget( 2,
                                                 2 ) ).setEnabled( enabled );
                    //reason code for each characteristic
                    ( (TextBox) cGrid.getWidget( 2,
                                                 3 ) ).setEnabled( enabled );
                }
            }
        } );
        return listBox;
    }

    private ListBox listBoxEditor( final String[] values,
                                   final String currentValue ) {
        final ListBox listBox = new ListBox();
        for ( final String value : values ) {
            listBox.addItem( value );
        }
        final int selectedIndex = Arrays.asList( values ).indexOf( currentValue );
        listBox.setSelectedIndex( selectedIndex >= 0 ? selectedIndex : 0 );
        return listBox;
    }

    /* from Commons StringUtils.java */
    private static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return (str == null && suffix == null);
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        int strOffset = str.length() - suffix.length();
        return str.regionMatches(true, strOffset, suffix, 0, suffix.length());
    }
}
