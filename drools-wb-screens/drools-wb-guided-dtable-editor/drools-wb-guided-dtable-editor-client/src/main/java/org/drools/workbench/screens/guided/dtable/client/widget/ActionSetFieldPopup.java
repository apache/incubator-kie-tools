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
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BRLRuleModel;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52.TableFormat;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.utils.DTCellValueUtilities;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.InfoPopup;
import org.uberfire.client.common.SmallLabel;

public class ActionSetFieldPopup extends FormStylePopup {

    private SmallLabel bindingLabel = new SmallLabel();
    private TextBox fieldLabel = getFieldLabel();
    private SimplePanel limitedEntryValueWidgetContainer = new SimplePanel();
    private int limitedEntryValueAttributeIndex = -1;
    private TextBox valueListWidget = null;
    private SimplePanel defaultValueWidgetContainer = new SimplePanel();
    private int defaultValueWidgetContainerIndex = -1;

    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;
    private final DTCellValueWidgetFactory factory;
    private final BRLRuleModel rm;
    private final DTCellValueUtilities utilities;

    private ActionSetFieldCol52 editingCol;

    private final boolean isReadOnly;

    public ActionSetFieldPopup( final GuidedDecisionTable52 model,
                                final AsyncPackageDataModelOracle oracle,
                                final GenericColumnCommand refreshGrid,
                                final ActionSetFieldCol52 col,
                                final boolean isNew,
                                final boolean isReadOnly ) {
        this.rm = new BRLRuleModel( model );
        this.editingCol = cloneActionSetColumn( col );
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
        this.isReadOnly = isReadOnly;
        this.utilities = new DTCellValueUtilities( model,
                                                   oracle );

        //Set-up a factory for value editors
        factory = DTCellValueWidgetFactory.getInstance( model,
                                                        oracle,
                                                        isReadOnly,
                                                        allowEmptyValues() );

        setTitle( GuidedDecisionTableConstants.INSTANCE.ColumnConfigurationSetAFieldOnAFact() );
        setModal( false );

        //Fact on which field will be set
        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( bindingLabel );
        doBindingLabel();

        ImageButton changePattern = new ImageButton( createEnabledEditButton(),
                                                     createDisabledEditButton(),
                                                     GuidedDecisionTableConstants.INSTANCE.ChooseABoundFactThatThisColumnPertainsTo(),
                                                     new ClickHandler() {
                                                         public void onClick( ClickEvent w ) {
                                                             showChangeFact( w );
                                                         }
                                                     } );
        changePattern.setEnabled( !isReadOnly );
        pattern.add( changePattern );
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.Fact()).append(GuidedDecisionTableConstants.COLON).toString(),
                      pattern );

        //Fact Field being set
        HorizontalPanel field = new HorizontalPanel();
        fieldLabel.setEnabled( !isReadOnly );
        field.add( fieldLabel );
        ImageButton editField = new ImageButton( createEnabledEditButton(),
                                                 createDisabledEditButton(),
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
                            final DataType.DataTypes dataType = utilities.getDataType( editingCol );
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
        doValueList();

        //Default Value
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

        //Update Engine with changes
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.UpdateEngineWithChanges()).append(GuidedDecisionTableConstants.COLON).toString(),
                      doUpdate() );

        //Hide column tick-box
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.HideThisColumn()).append(GuidedDecisionTableConstants.COLON).toString(),
                      DTCellValueWidgetFactory.getHideColumnIndicator( editingCol ) );

        Button apply = new Button( GuidedDecisionTableConstants.INSTANCE.ApplyChanges() );
        apply.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                if ( !isValidFactType() ) {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.YouMustEnterAColumnFact() );
                    return;
                }
                if ( !isValidFactField() ) {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.YouMustEnterAColumnField() );
                    return;
                }
                if ( null == editingCol.getHeader() || "".equals( editingCol.getHeader() ) ) {
                    Window.alert( GuidedDecisionTableConstants.INSTANCE.YouMustEnterAColumnHeaderValueDescription() );
                    return;
                }

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

                // Pass new\modified column back for handling
                refreshGrid.execute( editingCol );
                hide();
            }
        } );
        addAttribute( "",
                      apply );

    }

    private Image createDisabledEditButton() {
        Image disabledChangePattern = GuidedDecisionTableImageResources508.INSTANCE.EditDisabled();
        disabledChangePattern.setAltText( GuidedDecisionTableConstants.INSTANCE.ChooseABoundFactThatThisColumnPertainsTo() );
        return disabledChangePattern;
    }

    private Image createEnabledEditButton() {
        Image enabledChangePattern = GuidedDecisionTableImageResources508.INSTANCE.Edit();
        enabledChangePattern.setAltText( GuidedDecisionTableConstants.INSTANCE.ChooseABoundFactThatThisColumnPertainsTo() );
        return enabledChangePattern;
    }

    private boolean allowEmptyValues() {
        return this.model.getTableFormat() == TableFormat.EXTENDED_ENTRY;
    }

    private ActionSetFieldCol52 cloneActionSetColumn( ActionSetFieldCol52 col ) {
        ActionSetFieldCol52 clone = null;
        if ( col instanceof LimitedEntryActionSetFieldCol52 ) {
            clone = new LimitedEntryActionSetFieldCol52();
            DTCellValue52 dcv = cloneLimitedEntryValue( ( (LimitedEntryCol) col ).getValue() );
            ( (LimitedEntryCol) clone ).setValue( dcv );
        } else {
            clone = new ActionSetFieldCol52();
        }
        clone.setBoundName( col.getBoundName() );
        clone.setFactField( col.getFactField() );
        clone.setHeader( col.getHeader() );
        clone.setType( col.getType() );
        clone.setValueList( col.getValueList() );
        clone.setUpdate( col.isUpdate() );
        clone.setDefaultValue( new DTCellValue52( col.getDefaultValue() ) );
        clone.setHideColumn( col.isHideColumn() );
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
        if ( !( editingCol instanceof LimitedEntryActionSetFieldCol52 ) ) {
            return;
        }
        if ( nil( editingCol.getFactField() ) ) {
            setAttributeVisibility( limitedEntryValueAttributeIndex,
                                    false );
            return;
        }
        LimitedEntryActionSetFieldCol52 lea = (LimitedEntryActionSetFieldCol52) editingCol;
        setAttributeVisibility( limitedEntryValueAttributeIndex,
                                true );
        if ( lea.getValue() == null ) {
            lea.setValue( factory.makeNewValue( editingCol ) );
        }
        limitedEntryValueWidgetContainer.setWidget( factory.getWidget( model.getConditionPattern( editingCol.getBoundName() ),
                                                                       editingCol,
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
        defaultValueWidgetContainer.setWidget( factory.getWidget( model.getConditionPattern( editingCol.getBoundName() ),
                                                                  editingCol,
                                                                  defaultValue ) );
    }

    private void doBindingLabel() {
        if ( this.editingCol.getBoundName() != null ) {
            this.bindingLabel.setText( "" + this.editingCol.getBoundName() );
        } else {
            this.bindingLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseChooseABoundFactForThisColumn() );
        }
    }

    private void doFieldLabel() {
        if ( this.editingCol.getFactField() != null ) {
            this.fieldLabel.setText( this.editingCol.getFactField() );
        } else {
            this.fieldLabel.setText( GuidedDecisionTableConstants.INSTANCE.pleaseChooseAFactPatternFirst() );
        }
    }

    private void doValueList() {
        if ( model.getTableFormat() == TableFormat.LIMITED_ENTRY ) {
            return;
        }

        //Don't show a Value List if either the Fact\Field is empty
        final String factType = utils.getBoundFactType( editingCol.getBoundName() );
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

    private Widget doUpdate() {
        HorizontalPanel hp = new HorizontalPanel();

        final CheckBox cb = new CheckBox();
        cb.setValue( editingCol.isUpdate() );
        cb.setText( "" );
        cb.setEnabled( !isReadOnly );
        if ( !isReadOnly ) {
            cb.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent arg0 ) {
                    if ( oracle.isGlobalVariable( editingCol.getBoundName() ) ) {
                        cb.setEnabled( false );
                        editingCol.setUpdate( false );
                    } else {
                        editingCol.setUpdate( cb.getValue() );
                    }
                }
            } );
        }
        hp.add( cb );
        hp.add( new InfoPopup( GuidedDecisionTableConstants.INSTANCE.UpdateFact(),
                               GuidedDecisionTableConstants.INSTANCE.UpdateDescription() ) );
        return hp;
    }

    private String getFactType() {
        if ( oracle.isGlobalVariable( editingCol.getBoundName() ) ) {
            return oracle.getGlobalVariable( editingCol.getBoundName() );
        }
        return getFactType( this.editingCol.getBoundName() );
    }

    private String getFactType( String boundName ) {
        return rm.getLHSBoundFact( boundName ).getFactType();
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

    private ListBox loadBoundFacts( String binding ) {
        ListBox listBox = new ListBox();
        listBox.addItem( GuidedDecisionTableConstants.INSTANCE.Choose() );
        List<String> factBindings = rm.getLHSBoundFacts();

        for ( int index = 0; index < factBindings.size(); index++ ) {
            String boundName = factBindings.get( index );
            if ( !"".equals( boundName ) ) {
                listBox.addItem( boundName );
                if ( boundName.equals( binding ) ) {
                    listBox.setSelectedIndex( index + 1 );
                }
            }
        }

        String[] globs = this.oracle.getGlobalVariables();
        for ( int i = 0; i < globs.length; i++ ) {
            listBox.addItem( globs[ i ] );
        }

        listBox.setEnabled( listBox.getItemCount() > 1 );
        if ( listBox.getItemCount() == 1 ) {
            listBox.clear();
            listBox.addItem( GuidedDecisionTableConstants.INSTANCE.NoPatternBindingsAvailable() );
        }

        return listBox;
    }

    private boolean nil( String s ) {
        return s == null || s.equals( "" );
    }

    private void showChangeFact( ClickEvent w ) {
        final FormStylePopup pop = new FormStylePopup();

        final ListBox pats = this.loadBoundFacts( editingCol.getBoundName() );
        pop.addAttribute( GuidedDecisionTableConstants.INSTANCE.ChooseFact(),
                          pats );
        Button ok = new Button( GuidedDecisionTableConstants.INSTANCE.OK() );
        pop.addAttribute( "",
                          ok );

        ok.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent w ) {
                String val = pats.getValue( pats.getSelectedIndex() );
                editingCol.setBoundName( val );
                editingCol.setFactField( null );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doBindingLabel();
                doFieldLabel();
                doValueList();
                pop.hide();
            }
        } );

        pop.show();

    }

    private void showFieldChange() {
        final FormStylePopup pop = new FormStylePopup();
        final ListBox box = new ListBox();
        pop.setModal( false );

        final String factType = getFactType();
        this.oracle.getFieldCompletions( factType,
                                         new Callback<ModelField[]>() {
                                             @Override
                                             public void callback( final ModelField[] fields ) {
                                                 for ( int i = 0; i < fields.length; i++ ) {
                                                     box.addItem( fields[ i ].getName() );
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
                editingCol.setType( oracle.getFieldType( factType,
                                                         editingCol.getFactField() ) );
                makeLimitedValueWidget();
                makeDefaultValueWidget();
                doFieldLabel();
                doValueList();
                pop.hide();
            }
        } );
        pop.show();
    }

    private boolean isValidFactType() {
        return !( editingCol.getBoundName() == null || "".equals( editingCol.getBoundName() ) );
    }

    private boolean isValidFactField() {
        return !( editingCol.getFactField() == null || "".equals( editingCol.getFactField() ) );
    }

    private boolean unique( String header ) {
        for ( ActionCol52 o : model.getActionCols() ) {
            if ( o.getHeader().equals( header ) ) {
                return false;
            }
        }
        return true;
    }

}
