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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.models.commons.shared.rule.ActionCallMethod;
import org.drools.workbench.models.commons.shared.rule.ActionFieldFunction;
import org.drools.workbench.models.commons.shared.rule.ActionInsertFact;
import org.drools.workbench.models.commons.shared.rule.FactPattern;
import org.kie.workbench.common.widgets.client.resources.HumanReadable;
import org.kie.workbench.common.services.datamodel.model.DropDownData;
import org.kie.workbench.common.services.datamodel.model.MethodInfo;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.screens.guided.rule.client.util.FieldNatureUtil;
import org.drools.workbench.screens.guided.rule.client.editor.MethodParameterValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.drools.workbench.screens.guided.rule.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.uberfire.client.common.DirtyableFlexTable;
import org.uberfire.client.common.FormStylePopup;
import org.uberfire.client.common.SmallLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * This widget is for modifying facts bound to a variable.
 */
public class ActionCallMethodWidget extends RuleModellerWidget {

    final private ActionCallMethod model;
    final private DirtyableFlexTable layout;
    private boolean isBoundFact = false;

    private String[] fieldCompletionTexts;
    private String[] fieldCompletionValues;
    private String variableClass;

    private boolean readOnly;

    private boolean isFactTypeKnown;

    public ActionCallMethodWidget( RuleModeller mod,
                                   EventBus eventBus,
                                   ActionCallMethod set,
                                   Boolean readOnly ) {
        super( mod,
               eventBus );
        this.model = set;
        this.layout = new DirtyableFlexTable();

        layout.setStyleName( "model-builderInner-Background" ); // NON-NLS

        PackageDataModelOracle completions = this.getModeller().getSuggestionCompletions();
        if ( completions.isGlobalVariable( set.getVariable() ) ) {

            List<MethodInfo> infos = completions.getMethodInfosForGlobalVariable( set.getVariable() );
            if ( infos != null ) {
                this.fieldCompletionTexts = new String[ infos.size() ];
                this.fieldCompletionValues = new String[ infos.size() ];
                int i = 0;
                for ( MethodInfo info : infos ) {
                    this.fieldCompletionTexts[ i ] = info.getName();
                    this.fieldCompletionValues[ i ] = info.getNameWithParameters();
                    i++;
                }

                this.variableClass = completions.getGlobalVariable( set.getVariable() );

            } else {
                this.fieldCompletionTexts = new String[ 0 ];
                this.fieldCompletionValues = new String[ 0 ];
                readOnly = true;
            }

        } else {

            FactPattern pattern = mod.getModel().getLHSBoundFact( set.getVariable() );
            if ( pattern != null ) {
                List<String> methodList = completions.getMethodNames( pattern.getFactType() );
                fieldCompletionTexts = new String[ methodList.size() ];
                fieldCompletionValues = new String[ methodList.size() ];
                int i = 0;
                for ( String methodName : methodList ) {
                    fieldCompletionTexts[ i ] = methodName;
                    fieldCompletionValues[ i ] = methodName;
                    i++;
                }
                this.variableClass = pattern.getFactType();
                this.isBoundFact = true;

            } else {
                /*
                 * if the call method is applied on a bound variable created in
                 * the rhs
                 */
                ActionInsertFact patternRhs = mod.getModel().getRHSBoundFact( set.getVariable() );
                if ( patternRhs != null ) {
                    List<String> methodList = completions.getMethodNames( patternRhs.getFactType() );
                    fieldCompletionTexts = new String[ methodList.size() ];
                    fieldCompletionValues = new String[ methodList.size() ];
                    int i = 0;
                    for ( String methodName : methodList ) {
                        fieldCompletionTexts[ i ] = methodName;
                        fieldCompletionValues[ i ] = methodName;
                        i++;
                    }
                    this.variableClass = patternRhs.getFactType();
                    this.isBoundFact = true;
                } else {
                    readOnly = true;
                }
            }
        }

        this.isFactTypeKnown = completions.isFactTypeRecognized( this.variableClass );
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
                          getSetterLabel() );
        DirtyableFlexTable inner = new DirtyableFlexTable();
        for ( int i = 0; i < model.getFieldValues().length; i++ ) {
            ActionFieldFunction val = model.getFieldValue( i );

            inner.setWidget( i,
                             0,
                             fieldSelector( val ) );
            inner.setWidget( i,
                             1,
                             valueEditor( val ) );

        }
        layout.setWidget( 0,
                          1,
                          inner );
    }

    private Widget getSetterLabel() {
        HorizontalPanel horiz = new HorizontalPanel();

        if ( model.getState() == ActionCallMethod.TYPE_UNDEFINED ) {
            Image edit = GuidedRuleEditorImages508.INSTANCE.AddFieldToFact();
            edit.setAltText( Constants.INSTANCE.AddAnotherFieldToThisSoYouCanSetItsValue() );
            edit.setTitle( Constants.INSTANCE.AddAnotherFieldToThisSoYouCanSetItsValue() );
            edit.addClickHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    Widget w = (Widget) event.getSource();
                    showAddFieldPopup( w );

                }
            } );
            horiz.add( new SmallLabel( HumanReadable.getActionDisplayName("call") + " [" + model.getVariable() + "]" ) ); // NON-NLS
            if ( !this.readOnly ) {
                horiz.add( edit );
            }
        } else {
            horiz.add( new SmallLabel( HumanReadable.getActionDisplayName( "call" ) + " [" + model.getVariable() + "." + model.getMethodName() + "]" ) ); // NON-NLS
        }

        return horiz;
    }

    protected void showAddFieldPopup( Widget w ) {

        final PackageDataModelOracle completions = this.getModeller().getSuggestionCompletions();

        final FormStylePopup popup = new FormStylePopup( GuidedRuleEditorImages508.INSTANCE.Wizard(),
                                                         Constants.INSTANCE.ChooseAMethodToInvoke() );
        final ListBox box = new ListBox();
        box.addItem( "..." );

        for ( int i = 0; i < fieldCompletionTexts.length; i++ ) {
            box.addItem( fieldCompletionTexts[ i ],
                         fieldCompletionValues[ i ] );
        }

        box.setSelectedIndex( 0 );

        popup.addAttribute( Constants.INSTANCE.ChooseAMethodToInvoke(),
                            box );
        box.addChangeHandler( new ChangeHandler() {

            public void onChange( ChangeEvent event ) {
                model.setState( ActionCallMethod.TYPE_DEFINED );

                String methodName = box.getItemText( box.getSelectedIndex() );
                String methodNameWithParams = box.getValue( box.getSelectedIndex() );

                model.setMethodName( methodName );
                List<String> fieldList = new ArrayList<String>();

                fieldList.addAll( completions.getMethodParams( variableClass,
                                                               methodNameWithParams ) );

                int i = 0;
                for ( String fieldParameter : fieldList ) {
                    model.addFieldValue( new ActionFieldFunction( methodName,
                                                                  String.valueOf( i ),
                                                                  fieldParameter ) );
                    i++;
                }

                getModeller().refreshWidget();
                popup.hide();
            }
        } );
        popup.setPopupPosition( w.getAbsoluteLeft(),
                                w.getAbsoluteTop() );
        popup.show();

    }

    private Widget valueEditor( final ActionFieldFunction val ) {

        PackageDataModelOracle completions = this.getModeller().getSuggestionCompletions();

        String type = "";
        if ( completions.isGlobalVariable( this.model.getVariable() ) ) {
            type = completions.getGlobalVariable( this.model.getVariable() );
        } else {
            type = this.getModeller().getModel().getLHSBindingType( this.model.getVariable() );
            if ( type == null ) {
                type = this.getModeller().getModel().getRHSBoundFact( this.model.getVariable() ).getFactType();
            }
        }

        DropDownData enums = completions.getEnums( type,
                                                   val.getField(),
                                                   FieldNatureUtil.toMap( this.model.getFieldValues() ) );

        return new MethodParameterValueEditor( val,
                                               enums,
                                               this.getModeller(),
                                               val.getType(),
                                               new Command() {

                                                   public void execute() {
                                                       setModified( true );
                                                   }
                                               } );
    }

    private Widget fieldSelector( final ActionFieldFunction val ) {
        return new SmallLabel( val.getType() );
    }

    /**
     * This returns true if the values being set are on a fact.
     */
    public boolean isBoundFact() {
        return isBoundFact;
    }

    public boolean isDirty() {
        return layout.hasDirty();
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
