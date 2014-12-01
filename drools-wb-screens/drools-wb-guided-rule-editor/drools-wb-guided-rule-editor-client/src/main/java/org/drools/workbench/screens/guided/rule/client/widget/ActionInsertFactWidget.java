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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.datamodel.oracle.FieldAccessorsAndMutators;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.ActionInsertLogicalFact;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.screens.guided.rule.client.editor.ActionValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.editor.events.TemplateVariablesChangedEvent;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.common.client.common.ClickableLabel;
import org.uberfire.ext.widgets.common.client.common.SmallLabel;
import org.uberfire.ext.widgets.common.client.common.popups.FormStylePopup;

/**
 * This is used when asserting a new fact into working memory.
 */
public class ActionInsertFactWidget extends RuleModellerWidget {

    private final FlexTable layout;
    private final ActionInsertFact model;
    private ModelField[] fieldCompletions;
    private final String factType;
    private boolean readOnly;

    private boolean isFactTypeKnown;

    private final Map<ActionFieldValue, ActionValueEditor> actionValueEditors = new HashMap<ActionFieldValue, ActionValueEditor>();

    public ActionInsertFactWidget( final RuleModeller mod,
                                   final EventBus eventBus,
                                   final ActionInsertFact set,
                                   final Boolean readOnly ) {
        super( mod,
               eventBus );
        this.model = set;
        this.layout = new FlexTable();
        this.factType = set.getFactType();

        AsyncPackageDataModelOracle oracle = this.getModeller().getDataModelOracle();
        oracle.getFieldCompletions( set.getFactType(),
                                    FieldAccessorsAndMutators.MUTATOR,
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            fieldCompletions = fields;
                                        }
                                    } );

        layout.setStyleName( "model-builderInner-Background" ); //NON-NLS

        this.isFactTypeKnown = oracle.isFactTypeRecognized( set.getFactType() );
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
        layout.setWidget( 0,
                          0,
                          getAssertLabel() );
        layout.setWidget( 1,
                          0,
                          new HTML( "&nbsp;&nbsp;&nbsp;&nbsp;" ) );
        layout.getFlexCellFormatter().setColSpan( 0,
                                                  0,
                                                  2 );

        FlexTable inner = new FlexTable();
        int col = 0;

        for ( int i = 0; i < model.getFieldValues().length; i++ ) {
            ActionFieldValue val = model.getFieldValues()[ i ];

            inner.setWidget( i,
                             0 + col,
                             fieldSelector( val ) );
            inner.setWidget( i,
                             1 + col,
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
                inner.setWidget( i,
                                 2 + col,
                                 remove );
            }

        }

        layout.setWidget( 1,
                          1,
                          inner );

    }

    private Widget valueEditor( final ActionFieldValue val ) {
        ActionValueEditor actionValueEditor = new ActionValueEditor( factType,
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

    private Widget getAssertLabel() {

        ClickHandler cl = new ClickHandler() {

            public void onClick( ClickEvent event ) {
                Widget w = (Widget) event.getSource();
                showAddFieldPopup( w );

            }
        };

        String assertType = "assert"; //NON-NLS
        if ( this.model instanceof ActionInsertLogicalFact ) {
            assertType = "assertLogical"; //NON-NLS
        }

        String lbl = ( model.isBound() == false ) ? HumanReadable.getActionDisplayName( assertType ) + " <b>" + this.model.getFactType() + "</b>" : HumanReadable.getActionDisplayName( assertType ) + " <b>" + this.model.getFactType() + "</b>" + " <b>["
                + model.getBoundName() + "]</b>";
        if ( this.model.getFieldValues() != null && model.getFieldValues().length > 0 ) {
            lbl = lbl + ":";
        }
        return new ClickableLabel( lbl,
                                   cl,
                                   !this.readOnly );

    }

    protected void showAddFieldPopup( Widget w ) {
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
                String fieldType = oracle.getFieldType( model.getFactType(),
                                                        fieldName );
                model.addFieldValue( new ActionFieldValue( fieldName,
                                                           "",
                                                           fieldType ) );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );
        /*
         * Propose a textBox to the user to make him set a variable name
         */
        final HorizontalPanel vn = new HorizontalPanel();
        final TextBox varName = new TextBox();
        if ( this.model.getBoundName() != null ) {
            varName.setText( this.model.getBoundName() );
        }
        final Button ok = new Button( HumanReadableConstants.INSTANCE.Set() );
        vn.add( varName );
        vn.add( ok );
        ok.addClickHandler( new ClickHandler() {

            public void onClick( ClickEvent event ) {
                String var = varName.getText();
                if ( getModeller().isVariableNameUsed( var ) && ( ( model.getBoundName() != null && model.getBoundName().equals( var ) == false ) || model.getBoundName() == null ) ) {
                    Window.alert( GuidedRuleEditorResources.CONSTANTS.TheVariableName0IsAlreadyTaken( var ) );
                    return;
                }
                model.setBoundName( var );
                setModified( true );
                getModeller().refreshWidget();
                popup.hide();
            }
        } );

        popup.addAttribute( GuidedRuleEditorResources.CONSTANTS.BoundVariable(),
                            vn );
        popup.show();

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
