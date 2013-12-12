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
package org.drools.workbench.screens.guided.dtable.client.wizard.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.oracle.DataType;
import org.drools.workbench.models.datamodel.oracle.ModelField;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.ActionSetFieldsDefinedEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.DuplicatePatternsEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.PatternRemovedEvent;
import org.kie.workbench.common.widgets.client.widget.HumanReadableDataTypes;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.wizards.WizardPageStatusChangeEvent;

/**
 * A page for the guided Decision Table Wizard to define Actions setting fields
 * on previously bound patterns. This page does not use the GuidedDecisionTable
 * model directly; instead maintaining its own Pattern-to-Action associations.
 */
@Dependent
public class ActionSetFieldsPage extends AbstractGuidedDecisionTableWizardPage
        implements
        ActionSetFieldsPageView.Presenter {

    @Inject
    private ActionSetFieldsPageView view;

    @Inject
    private Event<ActionSetFieldsDefinedEvent> actionSetFieldsDefinedEvent;

    @Inject
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    //GuidedDecisionTable52 maintains a single collection of Actions, linked to patterns by boundName. Thus if multiple 
    //patterns are bound to the same name we cannot distinguish which Actions relate to which Patterns. The Wizard therefore 
    //maintains it's own internal association of Patterns to Actions. IdentityHashMap is used as it is possible to have two 
    //identically defined Patterns (i.e. they have the same property values) although they represent different instances. 
    //A WeakIdentityHashMap would have been more appropriate, however JavaScript has no concept of a weak reference, and so 
    //it can't be implement in GWT. In the absence of such a Map an Event is raised by FactPatternsPage when a Pattern is 
    //removed that is handled here to synchronise the Pattern lists.
    private Map<Pattern52, List<ActionSetFieldCol52>> patternToActionsMap = new IdentityHashMap<Pattern52, List<ActionSetFieldCol52>>();

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardActionSetFields();
    }

    @Override
    public void initialise() {
        view.init( this );
        view.setValidator( getValidator() );
        patternToActionsMap.clear();

        //Set-up validator for the pattern-to-action mapping voodoo
        getValidator().setPatternToActionSetFieldsMap( patternToActionsMap );

        //Set-up a factory for value editors
        view.setDTCellValueWidgetFactory( DTCellValueWidgetFactory.getInstance( model,
                                                                                oracle,
                                                                                false,
                                                                                allowEmptyValues() ) );

        //Existing ActionSetFieldCols (should be empty for a new Decision Table)
        for ( ActionCol52 a : model.getActionCols() ) {
            if ( a instanceof ActionSetFieldCol52 ) {
                final ActionSetFieldCol52 asf = (ActionSetFieldCol52) a;
                final Pattern52 p = model.getConditionPattern( asf.getBoundName() );
                if ( !patternToActionsMap.containsKey( p ) ) {
                    patternToActionsMap.put( p,
                                             new ArrayList<ActionSetFieldCol52>() );
                }
                final List<ActionSetFieldCol52> actions = patternToActionsMap.get( p );
                actions.add( asf );
            }
        }

        view.setChosenFields( new ArrayList<ActionSetFieldCol52>() );

        content.setWidget( view );
    }

    @Override
    public void prepareView() {
        //Setup the available patterns, that could have changed each time this page is visited
        List<Pattern52> availablePatterns = new ArrayList<Pattern52>();
        for ( Pattern52 p : model.getPatterns() ) {
            if ( p.getChildColumns().size() > 0 ) {
                availablePatterns.add( p );
            } else {
                patternToActionsMap.remove( p );
            }
        }
        view.setAvailablePatterns( availablePatterns );
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        //Have all Actions been defined?
        boolean areActionSetFieldsDefined = true;
        for ( List<ActionSetFieldCol52> actions : patternToActionsMap.values() ) {
            for ( ActionSetFieldCol52 a : actions ) {
                if ( !getValidator().isActionValid( a ) ) {
                    areActionSetFieldsDefined = false;
                    break;
                }
            }
        }

        //Signal Action Set Fields definitions to other pages
        final ActionSetFieldsDefinedEvent event = new ActionSetFieldsDefinedEvent( areActionSetFieldsDefined );
        actionSetFieldsDefinedEvent.fire( event );

        callback.callback( areActionSetFieldsDefined );
    }

    //See comments about use of IdentityHashMap in instance member declaration section
    public void onPatternRemoved( final @Observes PatternRemovedEvent event ) {
        patternToActionsMap.remove( event.getPattern() );
    }

    public void onDuplicatePatterns( final @Observes DuplicatePatternsEvent event ) {
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onActionSetFieldsDefined( final @Observes ActionSetFieldsDefinedEvent event ) {
        view.setAreActionSetFieldsDefined( event.getAreActionSetFieldsDefined() );
    }

    @Override
    public void selectPattern( final Pattern52 pattern ) {

        //Pattern is null when programmatically deselecting an item
        if ( pattern == null ) {
            return;
        }

        //Add fields available
        final String type = pattern.getFactType();
        oracle.getFieldCompletions( type,
                                    new Callback<ModelField[]>() {
                                        @Override
                                        public void callback( final ModelField[] fields ) {
                                            final List<AvailableField> availableFields = new ArrayList<AvailableField>();
                                            for ( ModelField modelField : fields ) {
                                                final String fieldName = modelField.getName();
                                                final String fieldType = oracle.getFieldType( type,
                                                                                              fieldName );
                                                final String fieldDisplayType = HumanReadableDataTypes.getUserFriendlyTypeName( fieldType );
                                                final AvailableField field = new AvailableField( fieldName,
                                                                                                 fieldType,
                                                                                                 fieldDisplayType,
                                                                                                 BaseSingleFieldConstraint.TYPE_LITERAL );
                                                availableFields.add( field );
                                            }
                                            view.setAvailableFields( availableFields );

                                            //Set fields already chosen
                                            List<ActionSetFieldCol52> actionsForPattern = patternToActionsMap.get( pattern );
                                            if ( actionsForPattern == null ) {
                                                actionsForPattern = new ArrayList<ActionSetFieldCol52>();
                                                patternToActionsMap.put( pattern,
                                                                         actionsForPattern );
                                            }
                                            view.setChosenFields( actionsForPattern );
                                        }
                                    } );
    }

    @Override
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Copy actions to decision table model. Assertion of bindings occurs in FactPatternsPage
        for ( Map.Entry<Pattern52, List<ActionSetFieldCol52>> ps : patternToActionsMap.entrySet() ) {
            final Pattern52 p = ps.getKey();

            //Patterns with no conditions don't get created
            if ( p.getChildColumns().size() > 0 ) {
                final String binding = p.getBoundName();
                for ( ActionSetFieldCol52 a : ps.getValue() ) {
                    a.setBoundName( binding );
                    model.getActionCols().add( a );
                }
            }
        }
    }

    @Override
    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return model.getTableFormat();
    }

    @Override
    public boolean hasEnums( final ActionSetFieldCol52 selectedAction ) {
        for ( Map.Entry<Pattern52, List<ActionSetFieldCol52>> e : this.patternToActionsMap.entrySet() ) {
            if ( e.getValue().contains( selectedAction ) ) {
                final String factType = e.getKey().getFactType();
                final String factField = selectedAction.getFactField();
                return this.oracle.hasEnums( factType,
                                             factField );
            }
        }
        return false;
    }

    @Override
    public void assertDefaultValue( final Pattern52 selectedPattern,
                                    final ActionSetFieldCol52 selectedAction ) {
        final List<String> valueList = Arrays.asList( modelUtils.getValueList( selectedAction ) );
        if ( valueList.size() > 0 ) {
            final String defaultValue = cellUtils.asString( selectedAction.getDefaultValue() );
            if ( !valueList.contains( defaultValue ) ) {
                selectedAction.getDefaultValue().clearValues();
            }
        } else {
            //Ensure the Default Value has been updated to represent the column's data-type.
            final DTCellValue52 defaultValue = selectedAction.getDefaultValue();
            final DataType.DataTypes dataType = cellUtils.getDataType( selectedPattern,
                                                                       selectedAction );
            cellUtils.assertDTCellValue( dataType,
                                         defaultValue );
        }
    }

    @Override
    public void stateChanged() {
        final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( this );
        wizardPageStatusChangeEvent.fire( event );
    }

}
