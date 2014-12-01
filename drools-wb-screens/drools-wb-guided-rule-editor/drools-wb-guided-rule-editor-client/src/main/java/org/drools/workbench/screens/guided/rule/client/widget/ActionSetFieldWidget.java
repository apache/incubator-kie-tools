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

package org.drools.workbench.screens.guided.rule.client.widget;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionSetField;
import org.drools.workbench.models.datamodel.rule.ActionUpdateField;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.screens.guided.rule.client.editor.ActionValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;

/**
 * This widget is for setting fields on a bound fact or global variable.
 */
public class ActionSetFieldWidget extends RuleModellerWidget {

    final private ActionSetField model;
    final private FlexTable layout;
    private boolean isBoundFact = false;
    private ModelField[] fieldCompletions;
    private String variableClass;
    private boolean readOnly;

    private boolean isFactTypeKnown;

    private final Map<ActionFieldValue, ActionValueEditor> actionValueEditors = new HashMap<ActionFieldValue, ActionValueEditor>();

    public ActionSetFieldWidget( RuleModeller mod,
                                 EventBus eventBus,
                                 ActionSetField set,
                                 Boolean readOnly ) {
        super( mod,
               eventBus );
        this.model = set;
        this.layout = new FlexTable();

        layout.setStyleName( "model-builderInner-Background" );

        AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();

        if ( oracle.isGlobalVariable( set.getVariable() ) ) {
            oracle.getFieldCompletionsForGlobalVariable( set.getVariable(),
                                                         new Callback<ModelField[]>() {
                                                             @Override
                                                             public void callback( ModelField[] fieldCompletions ) {
                                                                 ActionSetFieldWidget.this.fieldCompletions = fieldCompletions;
                                                             }
                                                         } );
            this.variableClass = oracle.getGlobalVariable( set.getVariable() );
        } else {
            String type = mod.getModel().getLHSBindingType( set.getVariable() );
            if ( type != null ) {
                oracle.getFieldCompletions( type,
                                            FieldAccessorsAndMutators.MUTATOR,
                                            new Callback<ModelField[]>() {
                                                @Override
                                                public void callback( final ModelField[] fields ) {
                                                    fieldCompletions = fields;
                                                }
                                            } );
                this.variableClass = type;
                this.isBoundFact = true;
            } else {
                ActionInsertFact patternRhs = mod.getModel().getRHSBoundFact( set.getVariable() );
                if ( patternRhs != null ) {
                    oracle.getFieldCompletions( patternRhs.getFactType(),
                                                FieldAccessorsAndMutators.MUTATOR,
                                                new Callback<ModelField[]>() {
                                                    @Override
                                                    public void callback( final ModelField[] fields ) {
                                                        fieldCompletions = fields;
                                                    }
                                                }
                                              );
                    this.variableClass = patternRhs.getFactType();
                    this.isBoundFact = true;
                }
            }
        }

        if ( this.variableClass == null ) {
            readOnly = true;
            ErrorPopup.showMessage( GuidedRuleEditorResources.CONSTANTS.CouldNotFindTheTypeForVariable0( set.getVariable() ) );
        }

        this.isFactTypeKnown = oracle.isFactTypeRecognized( this.variableClass );
        if ( readOnly == null ) {
            this.readOnly = !this.isFactTypeKnown;
        } else {
            this.readOnly = readOnly;
        }

        if ( this.readOnly ) {
            layout.addStyleName( "editor-disabled-widget" );
        }

        doLayout();

        initWidget( this.layout );
    }

    private void doLayout() {
        layout.clear();

        for ( int i = 0; i < model.getFieldValues().length; i++ ) {
            ActionFieldValue val = model.getFieldValues()[ i ];

            layout.setWidget( i,
                              0,
                              getSetterLabel() );
            layout.setWidget( i,
                              1,
                              fieldSelector( val ) );
            layout.setWidget( i,
                              2,
                              valueEditor( val ) );
            final int idx = i;
            Image remove = GuidedRuleEditorImages508.INSTANCE.DeleteItemSmall();
            remove.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    if ( Window.confirm( GuidedRuleEditorResources.CONSTANTS.RemoveThisItem() ) ) {
                        model.removeField( idx );
                        setModified( true );
                        getModeller().refreshWidget();

                        //Signal possible change in Template variables
                        TemplateVariablesChangedEvent tvce = new TemplateVariablesChangedEvent( getModeller().getModel() );
                        getEventBus().fireEventFromSource( tvce,
                                                           getModeller().getModel() );
                    }
                }
            } );
            if ( !this.readOnly ) {
                layout.setWidget( i,
                                  3,
                                  remove );
            }

        }

        if ( model.getFieldValues().length == 0 ) {
            HorizontalPanel h = new HorizontalPanel();
            h.add( getSetterLabel() );
            if ( !this.readOnly ) {
                Image image = GuidedRuleEditorImages508.INSTANCE.Edit();
                image.setAltText( GuidedRuleEditorResources.CONSTANTS.AddFirstNewField() );
                image.setTitle( GuidedRuleEditorResources.CONSTANTS.AddFirstNewField() );
                image.addClickHandler( new ClickHandler() {

                    public void onClick( ClickEvent sender ) {
                        showAddFieldPopup( sender );
                    }
                } );
                h.add( image );
            }
            layout.setWidget( 0,
                              0,
                              h );
        }

        //layout.setWidget( 0, 1, inner );

    }

    private Widget getSetterLabel() {

        ClickHandler clk = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                //Widget w = (Widget)event.getSource();
                showAddFieldPopup( event );

            }
        };
        String modifyType = "set";
        if ( this.model instanceof ActionUpdateField ) {
            modifyType = "modify";
        }

        String type = this.getModeller().getModel().getLHSBindingType( model.getVariable() );

        String descFact = ( type != null ) ? type + " <b>[" + model.getVariable() + "]</b>" : model.getVariable();

        String sl = GuidedRuleEditorResources.CONSTANTS.setterLabel( HumanReadable.getActionDisplayName( modifyType ),
                                                                     descFact );
        return new ClickableLabel( sl,
                                   clk,
                                   !this.readOnly );//HumanReadable.getActionDisplayName(modifyType) + " value of <b>[" + model.variable + "]</b>", clk);
    }

    protected void showAddFieldPopup( ClickEvent w ) {
        final AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();
        final FormStylePopup popup = new FormStylePopup( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                         GuidedRuleEditorResources.CONSTANTS.AddAField() );

        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletions.length; i++ ) {
            box.addItem( fieldCompletions[ i ].getName() );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( GuidedRuleEditorResources.CONSTANTS.AddField(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                String fieldName = box.getItemText( box.getSelectedIndex() );

                String fieldType = oracle.getFieldType( variableClass,
                                                        fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName,
                                                           "",
                                                           fieldType ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.show();

    }

    private Widget valueEditor( final ActionFieldValue val ) {
        AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();
        String type = "";
        if ( oracle.isGlobalVariable( this.model.getVariable() ) ) {
            type = oracle.getGlobalVariable( this.model.getVariable() );
        } else {
            type = this.getModeller().getModel().getLHSBindingType( this.model.getVariable() );
            /*
             * to take in account if the using a rhs bound variable
             */
            if ( type == null && !this.readOnly ) {
                type = this.getModeller().getModel().getRHSBoundFact( this.model.getVariable() ).getFactType();
            }
        }

        ActionValueEditor actionValueEditor = new ActionValueEditor( type,
                                                                     val,
                                                                     model.getFieldValues(),
                                                                     this.getModeller(),
                                                                     this.getEventBus(),
                                                                     val.getType(),
                                                                     this.readOnly );
        actionValueEditor.setOnChangeCommand( new Command() {

            public void execute() {
                refreshActionValueEditorsDropDownData( val );
                setModified( true );
            }
        } );

        //Keep a reference to the value editors so they can be refreshed for dependent enums
        actionValueEditors.put( val,
                                actionValueEditor );

        return actionValueEditor;
    }

    private void refreshActionValueEditorsDropDownData( final ActionFieldValue modifiedField ) {
        for ( Map.Entry<ActionFieldValue, ActionValueEditor> e : actionValueEditors.entrySet() ) {
            final ActionFieldValue afv = e.getKey();
            if ( afv.getNature() == FieldNatureType.TYPE_LITERAL || afv.getNature() == FieldNatureType.TYPE_ENUM ) {
                if ( !afv.equals( modifiedField ) ) {
                    e.getValue().refresh();
                }
            }
        }
    }

    private Widget fieldSelector( final ActionFieldValue val ) {
        return new SmallLabel( val.getField() );
    }

    /**
     * This returns true if the values being set are on a fact.
     */
    public boolean isBoundFact() {
        return isBoundFact;
    }

    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }

    @Override
    public boolean isFactTypeKnown() {
        return this.isFactTypeKnown;
    }

}
