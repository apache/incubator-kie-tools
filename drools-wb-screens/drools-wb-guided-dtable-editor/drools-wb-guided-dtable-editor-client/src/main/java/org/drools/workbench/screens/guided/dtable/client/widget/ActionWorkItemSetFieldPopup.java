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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.resources.images.GuidedDecisionTableImageResources508;
import org.drools.workbench.screens.guided.dtable.client.utils.GuidedDecisionTableUtils;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.ImageButton;
import org.uberfire.client.common.InfoPopup;
import org.uberfire.client.common.SmallLabel;

/**
 * A popup to define an Action to set a field on an existing Fact to the value
 * of a Work Item Result parameter
 */
public class ActionWorkItemSetFieldPopup extends FormStylePopup {

    private SmallLabel bindingLabel = new SmallLabel();
    private TextBox fieldLabel = getFieldLabel();
    private ListBox workItemResultParameters = new ListBox();
    private Map<String, WorkItemParameter> workItemResultParametersMap = new HashMap<String, WorkItemParameter>();

    private final ActionWorkItemSetFieldCol52 editingCol;
    private final GuidedDecisionTable52 model;
    private final AsyncPackageDataModelOracle oracle;
    private final GuidedDecisionTableUtils utils;

    private final boolean isReadOnly;

    //Container to contain WorkItem and WorkItem Parameters associations
    private static class WorkItemParameter {

        WorkItemParameter( PortableWorkDefinition workDefinition,
                           PortableParameterDefinition workParameterDefinition ) {
            this.workDefinition = workDefinition;
            this.workParameterDefinition = workParameterDefinition;
        }

        PortableWorkDefinition workDefinition;
        PortableParameterDefinition workParameterDefinition;
    }

    public ActionWorkItemSetFieldPopup( final GuidedDecisionTable52 model,
                                        final AsyncPackageDataModelOracle oracle,
                                        final GenericColumnCommand refreshGrid,
                                        final ActionWorkItemSetFieldCol52 col,
                                        final boolean isNew,
                                        final boolean isReadOnly ) {
        this.editingCol = cloneActionSetColumn( col );
        this.model = model;
        this.oracle = oracle;
        this.utils = new GuidedDecisionTableUtils( model,
                                                   oracle );
        this.isReadOnly = isReadOnly;

        setTitle( GuidedDecisionTableConstants.INSTANCE.ColumnConfigurationWorkItemSetField() );
        setModal( false );

        //Fact on which field will be set
        HorizontalPanel pattern = new HorizontalPanel();
        pattern.add( bindingLabel );
        doBindingLabel();

        Image edit = GuidedDecisionTableImageResources508.INSTANCE.Edit();
        edit.setAltText( GuidedDecisionTableConstants.INSTANCE.ChooseABoundFactThatThisColumnPertainsTo() );
        Image editDisabled = GuidedDecisionTableImageResources508.INSTANCE.EditDisabled();
        editDisabled.setAltText( GuidedDecisionTableConstants.INSTANCE.ChooseABoundFactThatThisColumnPertainsTo() );
        ImageButton changePattern = new ImageButton( edit,
                                                     editDisabled,
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
        ImageButton editField = createEditField();
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

        //Update Engine with changes
        addAttribute( new StringBuilder(GuidedDecisionTableConstants.INSTANCE.UpdateEngineWithChanges()).append(GuidedDecisionTableConstants.COLON).toString(),
                      doUpdate() );

        //Bind field to a WorkItem result parameter
        addAttribute( GuidedDecisionTableConstants.INSTANCE.BindActionFieldToWorkItem(),
                      doBindFieldToWorkItem() );
        if ( !isReadOnly ) {
            workItemResultParameters.addChangeHandler( new ChangeHandler() {

                public void onChange( ChangeEvent event ) {
                    int index = workItemResultParameters.getSelectedIndex();
                    if ( index >= 0 ) {
                        String key = workItemResultParameters.getValue( index );
                        WorkItemParameter wip = workItemResultParametersMap.get( key );
                        editingCol.setWorkItemName( wip.workDefinition.getName() );
                        editingCol.setWorkItemResultParameterName( wip.workParameterDefinition.getName() );
                        editingCol.setParameterClassName( wip.workParameterDefinition.getClassName() );
                    }
                }

            } );
        }

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

    private ImageButton createEditField() {
        Image edit = GuidedDecisionTableImageResources508.INSTANCE.Edit();
        edit.setAltText( GuidedDecisionTableConstants.INSTANCE.EditTheFieldThatThisColumnOperatesOn() );
        Image editDisabled = GuidedDecisionTableImageResources508.INSTANCE.EditDisabled();
        editDisabled.setAltText( GuidedDecisionTableConstants.INSTANCE.EditTheFieldThatThisColumnOperatesOn() );

        return new ImageButton( edit,
                                editDisabled,
                                GuidedDecisionTableConstants.INSTANCE.EditTheFieldThatThisColumnOperatesOn(),
                                new ClickHandler() {
                                    public void onClick( ClickEvent w ) {
                                        showFieldChange();
                                    }
                                } );
    }

    private ActionWorkItemSetFieldCol52 cloneActionSetColumn( ActionWorkItemSetFieldCol52 col ) {
        ActionWorkItemSetFieldCol52 clone = new ActionWorkItemSetFieldCol52();
        clone.setBoundName( col.getBoundName() );
        clone.setFactField( col.getFactField() );
        clone.setHeader( col.getHeader() );
        clone.setType( col.getType() );
        clone.setValueList( col.getValueList() );
        clone.setUpdate( col.isUpdate() );
        clone.setDefaultValue( col.getDefaultValue() );
        clone.setHideColumn( col.isHideColumn() );
        clone.setWorkItemName( col.getWorkItemName() );
        clone.setWorkItemResultParameterName( col.getWorkItemResultParameterName() );
        clone.setParameterClassName( col.getParameterClassName() );
        return clone;
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

    //Populate list of WorkItem Result Parameters for the Fact\Fields data-type
    private Widget doBindFieldToWorkItem() {
        workItemResultParameters.clear();
        workItemResultParametersMap.clear();

        //Get list of Work Items executed by Actions
        List<PortableWorkDefinition> actionWorkItems = new ArrayList<PortableWorkDefinition>();
        for ( ActionCol52 ac : model.getActionCols() ) {
            if ( ac instanceof ActionWorkItemCol52 ) {
                PortableWorkDefinition pwd = ( (ActionWorkItemCol52) ac ).getWorkItemDefinition();
                actionWorkItems.add( pwd );
            }
        }

        //Populate list of available result parameters
        if ( actionWorkItems.size() == 0 ) {
            workItemResultParameters.setEnabled( false );
            workItemResultParameters.addItem( GuidedDecisionTableConstants.INSTANCE.NoWorkItemsAvailable() );
            editingCol.setWorkItemName( null );
            editingCol.setWorkItemResultParameterName( null );
            editingCol.setParameterClassName( null );
        } else {
            int selectedItemIndex = -1;
            String selectedItemKey = editingCol.getWorkItemName() + "" + editingCol.getWorkItemResultParameterName();
            workItemResultParameters.setEnabled( true && !isReadOnly );
            for ( PortableWorkDefinition pwd : actionWorkItems ) {
                for ( PortableParameterDefinition ppd : pwd.getResults() ) {
                    if ( acceptParameterType( ppd ) ) {
                        String key = pwd.getName() + "" + ppd.getName();
                        String parameterDisplayName = pwd.getDisplayName() + "" + ppd.getName();

                        //Pre-select item if applicable
                        if ( key.equals( selectedItemKey ) ) {
                            selectedItemIndex = workItemResultParameters.getItemCount();
                        }
                        workItemResultParametersMap.put( key,
                                                         new WorkItemParameter( pwd,
                                                                                ppd ) );
                        workItemResultParameters.addItem( parameterDisplayName,
                                                          key );
                    }
                }
            }

            //Disable selection if no suitable parameters were found
            if ( workItemResultParameters.getItemCount() == 0 ) {
                workItemResultParameters.setEnabled( false );
                workItemResultParameters.addItem( GuidedDecisionTableConstants.INSTANCE.NoWorkItemsAvailable() );
                editingCol.setWorkItemName( null );
                editingCol.setWorkItemResultParameterName( null );
                editingCol.setParameterClassName( null );
            } else {

                //Select first item if none were pre-selected
                if ( selectedItemIndex == -1 ) {
                    selectedItemIndex = 0;
                    selectedItemKey = workItemResultParameters.getValue( selectedItemIndex );
                    WorkItemParameter wip = workItemResultParametersMap.get( selectedItemKey );
                    editingCol.setWorkItemName( wip.workDefinition.getName() );
                    editingCol.setWorkItemResultParameterName( wip.workParameterDefinition.getName() );
                    editingCol.setParameterClassName( wip.workParameterDefinition.getClassName() );
                }
                workItemResultParameters.setSelectedIndex( selectedItemIndex );
            }
        }

        return workItemResultParameters;
    }

    private boolean acceptParameterType( PortableParameterDefinition ppd ) {
        if ( nil( editingCol.getFactField() ) ) {
            return false;
        }
        if ( ppd.getClassName() == null ) {
            return false;
        }
        Pattern52 p = model.getConditionPattern( editingCol.getBoundName() );
        String fieldClassName = oracle.getFieldClassName( p.getFactType(),
                                                          editingCol.getFactField() );
        return fieldClassName.equals( ppd.getClassName() );
    }

    private String getFactType() {
        if ( oracle.isGlobalVariable( editingCol.getBoundName() ) ) {
            return oracle.getGlobalVariable( editingCol.getBoundName() );
        }
        return getFactType( this.editingCol.getBoundName() );
    }

    private String getFactType( String boundName ) {
        for ( Pattern52 p : model.getPatterns() ) {
            if ( p.getBoundName().equals( boundName ) ) {
                return p.getFactType();
            }
        }
        return "";
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

    private ListBox loadBoundFacts() {
        Set<String> facts = new HashSet<String>();
        for ( Pattern52 p : model.getPatterns() ) {
            if ( !p.isNegated() ) {
                facts.add( p.getBoundName() );
            }
        }

        ListBox box = new ListBox();
        for ( Iterator<String> iterator = facts.iterator(); iterator.hasNext(); ) {
            String b = (String) iterator.next();
            box.addItem( b );
        }

        String[] globs = this.oracle.getGlobalVariables();
        for ( int i = 0; i < globs.length; i++ ) {
            box.addItem( globs[ i ] );
        }

        return box;
    }

    private boolean nil( String s ) {
        return s == null || s.equals( "" );
    }

    private void showChangeFact( ClickEvent w ) {
        final FormStylePopup pop = new FormStylePopup();

        final ListBox pats = this.loadBoundFacts();
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
                doBindFieldToWorkItem();
                doBindingLabel();
                doFieldLabel();
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
                doBindFieldToWorkItem();
                doFieldLabel();
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
