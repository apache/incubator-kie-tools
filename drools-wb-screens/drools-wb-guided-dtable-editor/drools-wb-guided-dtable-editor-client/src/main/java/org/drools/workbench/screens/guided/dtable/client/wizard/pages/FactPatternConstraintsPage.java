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
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.models.commons.shared.oracle.DataType;
import org.drools.workbench.models.commons.shared.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.Constants;
import org.drools.workbench.screens.guided.dtable.client.widget.DTCellValueWidgetFactory;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.ConditionsDefinedEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.DuplicatePatternsEvent;
import org.kie.workbench.widgets.common.client.widget.HumanReadableDataTypes;
import org.uberfire.client.wizards.WizardPageStatusChangeEvent;

/**
 * A page for the guided Decision Table Wizard to define Fact Pattern
 * Constraints
 */
@Dependent
public class FactPatternConstraintsPage extends AbstractGuidedDecisionTableWizardPage
        implements
        FactPatternConstraintsPageView.Presenter {

    @Inject
    private FactPatternConstraintsPageView view;

    @Inject
    private Event<ConditionsDefinedEvent> conditionsDefinedEvent;

    @Inject
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Override
    public String getTitle() {
        return Constants.INSTANCE.DecisionTableWizardFactPatternConstraints();
    }

    @Override
    public void initialise() {
        view.init( this );
        view.setValidator( getValidator() );

        view.setChosenConditions( new ArrayList<ConditionCol52>() );

        //Set-up a factory for value editors
        view.setDTCellValueWidgetFactory( DTCellValueWidgetFactory.getInstance( model,
                                                                                oracle,
                                                                                false,
                                                                                allowEmptyValues() ) );
        content.setWidget( view );
    }

    @Override
    public void prepareView() {
        //Setup the available patterns, that could have changed each time this page is visited
        view.setAvailablePatterns( this.model.getPatterns() );
    }

    @Override
    public boolean isComplete() {

        //Have all patterns conditions been defined?
        boolean areConditionsDefined = true;
        for ( Pattern52 p : model.getPatterns() ) {
            for ( ConditionCol52 c : p.getChildColumns() ) {
                if ( !getValidator().isConditionValid( c ) ) {
                    areConditionsDefined = false;
                    break;
                }
            }
        }

        //Signal Condition definitions to other pages
        final ConditionsDefinedEvent event = new ConditionsDefinedEvent( areConditionsDefined );
        conditionsDefinedEvent.fire( event );

        return areConditionsDefined;
    }

    public void onDuplicatePatterns( final @Observes DuplicatePatternsEvent event ) {
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    public void onConditionsDefined( final @Observes ConditionsDefinedEvent event ) {
        view.setAreConditionsDefined( event.getAreConditionsDefined() );
    }

    @Override
    public void selectPattern( final Pattern52 pattern ) {

        //Pattern is null when programmatically deselecting an item
        if ( pattern == null ) {
            return;
        }

        //Add Fact fields
        final String type = pattern.getFactType();
        final String[] fieldNames = oracle.getFieldCompletions( type );
        final List<AvailableField> availableFields = new ArrayList<AvailableField>();
        for ( String fieldName : fieldNames ) {
            final String fieldType = oracle.getFieldType( type,
                                                          fieldName );
            final String fieldDisplayType = HumanReadableDataTypes.getUserFriendlyTypeName( fieldType );
            final AvailableField field = new AvailableField( fieldName,
                                                             fieldType,
                                                             fieldDisplayType,
                                                             BaseSingleFieldConstraint.TYPE_LITERAL );
            availableFields.add( field );
        }

        //Add predicates
        if ( model.getTableFormat() == GuidedDecisionTable52.TableFormat.EXTENDED_ENTRY ) {
            final AvailableField field = new AvailableField( Constants.INSTANCE.DecisionTableWizardPredicate(),
                                                             BaseSingleFieldConstraint.TYPE_PREDICATE );
            availableFields.add( field );
        }

        view.setAvailableFields( availableFields );
        view.setChosenConditions( pattern.getChildColumns() );
    }

    @Override
    public void setChosenConditions( final Pattern52 pattern,
                                     final List<ConditionCol52> conditions ) {
        pattern.getChildColumns().clear();
        pattern.getChildColumns().addAll( conditions );
    }

    @Override
    public String[] getOperatorCompletions( final Pattern52 selectedPattern,
                                            final ConditionCol52 selectedCondition ) {

        final String factType = selectedPattern.getFactType();
        final String factField = selectedCondition.getFactField();
        final String[] ops = this.oracle.getOperatorCompletions( factType,
                                                                 factField );

        //Operators "in" and "not in" are only allowed if the Calculation Type is a Literal
        final List<String> filteredOps = new ArrayList<String>();
        for ( String op : ops ) {
            filteredOps.add( op );
        }
        if ( BaseSingleFieldConstraint.TYPE_LITERAL != selectedCondition.getConstraintValueType() ) {
            filteredOps.remove( "in" );
        }

        //But remove "in" if the Fact\Field is enumerated
        if ( oracle.hasEnums( factType,
                              factField ) ) {
            filteredOps.remove( "in" );
        }

        final String[] displayOps = new String[ filteredOps.size() ];
        filteredOps.toArray( displayOps );

        return displayOps;
    }

    @Override
    public GuidedDecisionTable52.TableFormat getTableFormat() {
        return model.getTableFormat();
    }

    @Override
    public boolean hasEnum( final Pattern52 selectedPattern,
                            final ConditionCol52 selectedCondition ) {
        final String factType = selectedPattern.getFactType();
        final String factField = selectedCondition.getFactField();
        return oracle.hasEnums( factType,
                                factField );
    }

    @Override
    public boolean requiresValueList( final Pattern52 selectedPattern,
                                      final ConditionCol52 selectedCondition ) {
        //Don't show a Value List if either the Fact\Field is empty
        final String factType = selectedPattern.getFactType();
        final String factField = selectedCondition.getFactField();
        boolean enableValueList = !( ( factType == null || "".equals( factType ) ) || ( factField == null || "".equals( factField ) ) );

        //Don't show Value List if operator does not accept one
        if ( enableValueList ) {
            enableValueList = validator.doesOperatorAcceptValueList( selectedCondition );
        }

        //Don't show a Value List if the Fact\Field has an enumeration
        if ( enableValueList ) {
            enableValueList = !oracle.hasEnums( factType,
                                                factField );
        }
        return enableValueList;
    }

    @Override
    public void assertDefaultValue( final Pattern52 selectedPattern,
                                    final ConditionCol52 selectedCondition ) {
        final List<String> valueList = Arrays.asList( modelUtils.getValueList( selectedCondition ) );
        if ( valueList.size() > 0 ) {
            final String defaultValue = cellUtils.asString( selectedCondition.getDefaultValue() );
            if ( !valueList.contains( defaultValue ) ) {
                selectedCondition.getDefaultValue().clearValues();
            }
        } else {
            //Ensure the Default Value has been updated to represent the column's data-type.
            final DTCellValue52 defaultValue = selectedCondition.getDefaultValue();
            final DataType.DataTypes dataType = cellUtils.getDataType( selectedPattern,
                                                                       selectedCondition );
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
