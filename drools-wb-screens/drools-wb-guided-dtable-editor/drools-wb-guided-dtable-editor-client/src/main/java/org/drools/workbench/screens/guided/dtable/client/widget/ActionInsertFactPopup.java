/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.oracle.DataType;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.ImageResources508;
import org.drools.workbench.screens.guided.dtable.client.widget.table.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.model.GuidedDecisionTableUtils;
import org.drools.workbench.screens.guided.rule.client.editor.BindingTextBox;
import org.kie.guvnor.datamodel.model.FieldAccessorsAndMutators;
import org.kie.guvnor.datamodel.oracle.PackageDataModelOracle;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.InfoPopup;
import org.uberfire.client.common.SmallLabel;

/**
 * This is an editor for columns that are for inserting facts.
 */
public class ActionInsertFactPopup extends FormStylePopup {

    private SmallLabel patternLabel = new SmallLabel();
    private TextBox fieldLabel = getFieldLabel();
    private SimplePanel limitedEntryValueWidgetContainer = new SimplePanel();
    private int limitedEntryValueAttributeIndex = -1;
    private TextBox valueListWidget = null;
    private SimplePanel defaultValueWidgetContainer = new SimplePanel();
    private int defaultValueWidgetContainerIndex = -1;

    private final GuidedDecisionTable52 model;
    private final PackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;
    private final DTCellValueWidgetFactory factory;
    private final BRLRuleModel validator;
    private final DTCellValueUtilities utilities;

    private ActionInsertFactCol52 editingCol;

    private final boolean isReadOnly;

    public ActionInsertFactPopup( final PackageDataModelOracle oracle,
                                  final GuidedDecisionTable52 model,
                                  final GenericColumnCommand refreshGrid,
                                  final ActionInsertFactCol52 col,
                                  final boolean isNew,
                                  final boolean isReadOnly ) {
        this.validator = new BRLRuleModel( model );
        this.editingCol = cloneActionInsertColumn( col );
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( oracle,
                                                   model );
        this.isReadOnly = isReadOnly;
        this.utilities = new DTCellValueUtilities( model,
                                                   oracle );

        //Set-up a factory for value editors
        factory = DTCellValueWidgetFactory.getInstance( model,
                                                        oracle,
                                                        isReadOnly,
                                                        allowEmptyValues() );

        setTitle( Constants.INSTANCE.ActionColumnConfigurationInsertingANewFact() );
        setModal( false );

        //Fact being inserted
        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( patternLabel );
        doPatternLabel();

        ImageButton changePattern = createChangePatternButton();
        changePattern.setEnabled( !isReadOnly );
        pattern.add( changePattern );
        addAttribute( Constants.INSTANCE.Pattern(),
                      pattern );

        //Fact field being set
        HorizontalPanel field = new HorizontalPanel();
        fieldLabel.setEnabled( !isReadOnly );
        field.add( fieldLabel );

        ImageButton editField = createEditFieldButton();
        editField.setEnabled( !isReadOnly );
        field.add( editField );
        addAttribute( Constants.INSTANCE.Field(),
                      field );
        doFieldLabel();

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
        if ( model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY ) {
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
                            final DataType.DataTypes dataType = utilities.getDataType( editingCol );
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

        //Default Value
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

        //Logical insertion
        addAttribute( Constants.INSTANCE.LogicallyInsertColon(),
                      doInsertLogical() );

        //Hide column tick-box
        addAttribute( Constants.INSTANCE.HideThisColumn(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        Button apply = new Button( Constants.INSTANCE.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                if ( !isValidFactType() ) {
                    Window.alert( Constants.INSTANCE.YouMustEnterAColumnPattern() );
                    return;
                }
                if ( !isValidFactField() ) {
                    Window.alert( Constants.INSTANCE.YouMustEnterAColumnField() );
                    return;
                }
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( Constants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }

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

                // Pass new\modified column back for handling
                refreshGrid.execute( editingCol );
                hide();
            }
        } );
        addAttribute( "",
                      apply );

    }

    private ImageButton createEditFieldButton() {
        Image edit = ImageResources508.INSTANCE.Edit();
        edit.setAltText( Constants.INSTANCE.EditTheFieldThatThisColumnOperatesOn() );
        Image editDisabled = ImageResources508.INSTANCE.EditDisabled();
        editDisabled.setAltText( Constants.INSTANCE.EditTheFieldThatThisColumnOperatesOn() );
        return new ImageButton( edit,
                                editDisabled,
                                Constants.INSTANCE.EditTheFieldThatThisColumnOperatesOn(),
                                new ClickHandler() {
                                    public void onClick( ClickEvent w ) {
                                        showFieldChange();
                                    }
                                } );
    }

    private ImageButton createChangePatternButton() {
        Image edit = ImageResources508.INSTANCE.Edit();
        edit.setAltText( Constants.INSTANCE.ChooseAPatternThatThisColumnAddsDataTo() );
        Image editDisabled = ImageResources508.INSTANCE.EditDisabled();
        editDisabled.setAltText( Constants.INSTANCE.ChooseAPatternThatThisColumnAddsDataTo() );

        return new ImageButton( edit,
                                editDisabled,
                                Constants.INSTANCE.ChooseAPatternThatThisColumnAddsDataTo(),
                                new ClickHandler() {
                                    public void onClick( ClickEvent w ) {
                                        showChangePattern( w );
                                    }
                                } );
    }

    private boolean allowEmptyValues() {
        return this.model.getTableFormat() == TableFormat.EXTENDED_ENTRY;
    }

    private ActionInsertFactCol52 cloneActionInsertColumn( ActionInsertFactCol52 col ) {
        ActionInsertFactCol52 clone = null;
        if ( col instanceof LimitedEntryActionInsertFactCol52 ) {
            clone = new LimitedEntryActionInsertFactCol52();
            DTCellValue52 dcv = cloneLimitedEntryValue( ( (LimitedEntryCol) col ).getValue() );
            ( (LimitedEntryCol) clone ).setValue( dcv );
        } else {
            clone = new ActionInsertFactCol52();
        }
        clone.setBoundName( col.getBoundName() );
        clone.setType( col.getType() );
        clone.setFactField( col.getFactField() );
        clone.setFactType( col.getFactType() );
        clone.setHeader( col.getHeader() );
        clone.setValueList( col.getValueList() );
        clone.setDefaultValue( new DTCellValue52( col.getDefaultValue() ) );
        clone.setHideColumn( col.isHideColumn() );
        clone.setInsertLogical( col.isInsertLogical() );
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
        if ( !( editingCol instanceof LimitedEntryActionInsertFactCol52 ) ) {
            return;
        }
        if ( nil( editingCol.getFactField() ) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        LimitedEntryActionInsertFactCol52 lea = (LimitedEntryActionInsertFactCol52) editingCol;
        setAttributeVisibility( limitedEntryValueAttributeIndex,
                                true );
        if ( lea.getValue() == null ) {
            lea.setValue( factory.makeNewValue( editingCol ) );
        }
        limitedEntryValueWidgetContainer.setWidget( factory.getWidget( editingCol,
                                                                       lea.getValue() ) );
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
        setAttributeVisibility( defaultValueWidgetContainerIndex,
                                true );
        if ( editingCol.getDefaultValue() == null ) {
            editingCol.setDefaultValue( factory.makeNewValue( editingCol ) );
        }

        //Ensure the Default Value has been updated to represent the column's 
        //data-type. Legacy Default Values are all String-based and need to be 
        //coerced to the correct type
        final DTCellValue52 defaultValue = editingCol.getDefaultValue();
        final DataType.DataTypes dataType = utilities.getDataType( editingCol );
        utilities.assertDTCellValue( dataType,
                                     defaultValue );
        defaultValueWidgetContainer.setWidget( factory.getWidget( editingCol,
                                                                  defaultValue ) );
    }

    private void doFieldLabel() {
        if ( nil( this.editingCol.getFactField() ) ) {
            fieldLabel.setText( Constants.INSTANCE.pleaseChooseFactType() );
        } else {
            fieldLabel.setText( editingCol.getFactField() );
        }

    }

    private void doPatternLabel() {
        if ( this.editingCol.getFactType() != null ) {
            this.patternLabel.setText( this.editingCol.getFactType()
                                               + " ["
                                               + editingCol.getBoundName()
                                               + "]" );
        }
    }

    private void doValueList() {
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            return;
        }

        //Don't show a Value List if either the Fact\Field is empty
        final String factType = editingCol.getFactType();
        final String factField = editingCol.getFactField();
        boolean enableValueList = !( ( factType == null || "".equals( factType ) ) || ( factField == null || "".equals( factField ) ) );

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

    private TextBox getFieldLabel() {
        final TextBox box = new TextBox();
        box.addChangeHandler( new ChangeHandler() {
            public void onChange( ChangeEvent event ) {
                editingCol.setFactField( box.getText() );
            }
        } );
        return box;
    }

    private ListBox loadPatterns() {
        Set<String> vars = new HashSet<String>();
        ListBox patterns = new ListBox();

        for ( Object o : model.getActionCols() ) {
            ActionCol52 col = (ActionCol52) o;
            if ( col instanceof ActionInsertFactCol52 ) {
                ActionInsertFactCol52 c = (ActionInsertFactCol52) col;
                if ( !vars.contains( c.getBoundName() ) ) {
                    patterns.addItem( c.getFactType()
                                              + " ["
                                              + c.getBoundName()
                                              + "]",
                                      c.getFactType()
                                              + " "
                                              + c.getBoundName() );
                    vars.add( c.getBoundName() );
                }
            }

        }

        return patterns;

    }

    private boolean nil( String s ) {
        return s == null || s.equals( "" );
    }

    private void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setModal( false );
        String[] fields = this.oracle.getFieldCompletions( FieldAccessorsAndMutators.MUTATOR,
                                                           this.editingCol.getFactType() );
        final ListBox box = new ListBox();
        for ( int i = 0; i < fields.length; i++ ) {
            box.addItem( fields[ i ] );
        }
        pop.addAttribute( Constants.INSTANCE.Field(),
                          box );
        Button b = new Button( Constants.INSTANCE.OK() );
        pop.addAttribute( "",
                          b );
        b.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                editingCol.setFactField( box.getItemText( box.getSelectedIndex() ) );
                editingCol.setType( oracle.getFieldType( editingCol.getFactType(),
                                                         editingCol.getFactField() ) );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doValueList();
                doFieldLabel();
                pop.hide();
            }
        } );
        pop.show();

    }

    private boolean unique( String header ) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) {
                return false;
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
        Button ok = new Button( "OK" );
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
                String[] val = pats.getValue( pats.getSelectedIndex() ).split( "\\s" ); // NON-NLS
                editingCol.setFactType( val[ 0 ] );
                editingCol.setBoundName( val[ 1 ] );
                editingCol.setFactField( null );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doPatternLabel();
                doFieldLabel();
                doValueList();
                pop.hide();
            }
        } );

        pop.show();
    }

    protected void showNewPatternDialog() {
        final FormStylePopup pop = new FormStylePopup();
        pop.setTitle( Constants.INSTANCE.NewFactSelectTheType() );
        final ListBox types = new ListBox();
        for ( int i = 0; i < oracle.getFactTypes().length; i++ ) {
            types.addItem( oracle.getFactTypes()[ i ] );
        }
        pop.addAttribute( Constants.INSTANCE.FactType(),
                          types );
        final TextBox binding = new BindingTextBox();
        pop.addAttribute( Constants.INSTANCE.Binding(),
                          binding );

        Button ok = new Button( Constants.INSTANCE.OK() );
        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {

                //Validate column configuration
                String ft = types.getItemText( types.getSelectedIndex() );
                String fn = binding.getText();
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

                //Configure column
                editingCol.setBoundName( binding.getText() );
                editingCol.setFactType( types.getItemText( types.getSelectedIndex() ) );
                editingCol.setFactField( null );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doPatternLabel();
                doFieldLabel();
                doValueList();
                pop.hide();
            }
        } );
        pop.addAttribute( "",
                          ok );

        pop.show();
    }

    private boolean isBindingUnique( String binding ) {
        return !validator.isVariableNameUsed( binding );
    }

    private boolean isValidFactType() {
        return !( editingCol.getFactType() == null || "".equals( editingCol.getFactType() ) );
    }

    private boolean isValidFactField() {
        return !( editingCol.getFactField() == null || "".equals( editingCol.getFactField() ) );
    }

    private Widget doInsertLogical() {
        HorizontalPanel hp = new HorizontalPanel();

        final CheckBox cb = new CheckBox();
        cb.setValue( editingCol.isInsertLogical() );
        cb.setText( "" );
        cb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            cb.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent arg0 ) {
                    if ( oracle.isGlobalVariable( editingCol.getBoundName() ) ) {
                        cb.setEnabled( false );
                        editingCol.setInsertLogical( false );
                    } else {
                        editingCol.setInsertLogical( cb.getValue() );
                    }
                }
            } );
        }
        hp.add( cb );
        hp.add( new InfoPopup( Constants.INSTANCE.LogicallyInsertANewFact(),
                               Constants.INSTANCE.LogicallyAssertAFactTheFactWillBeRetractedWhenTheSupportingEvidenceIsRemoved() ) );
        return hp;
    }

}
