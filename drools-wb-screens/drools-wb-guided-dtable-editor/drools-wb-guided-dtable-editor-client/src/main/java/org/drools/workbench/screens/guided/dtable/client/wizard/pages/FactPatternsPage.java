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

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.DuplicatePatternsEvent;
import org.drools.workbench.screens.guided.dtable.client.wizard.pages.events.PatternRemovedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.wizards.WizardPageStatusChangeEvent;

/**
 * A page for the guided Decision Table Wizard to define Fact Patterns
 */
@Dependent
public class FactPatternsPage extends AbstractGuidedDecisionTableWizardPage
        implements
        FactPatternsPageView.Presenter {

    @Inject
    private FactPatternsPageView view;

    @Inject
    private Event<DuplicatePatternsEvent> duplicatePatternsEvent;

    @Inject
    private Event<PatternRemovedEvent> patternRemovedEvent;

    @Inject
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardFactPatterns();
    }

    @Override
    public void initialise() {
        view.init( this );
        view.setValidator( getValidator() );

        final List<String> availableTypes = Arrays.asList( oracle.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );
        view.setChosenPatterns( new ArrayList<Pattern52>() );

        content.setWidget( view );
    }

    @Override
    public void prepareView() {
        // Nothing needs to be done when the page is viewed; it is setup in initialise
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        //Are the patterns valid?
        final boolean arePatternBindingsUnique = getValidator().arePatternBindingsUnique();

        //Signal duplicates to other pages
        final DuplicatePatternsEvent event = new DuplicatePatternsEvent( arePatternBindingsUnique );
        duplicatePatternsEvent.fire( event );

        callback.callback( arePatternBindingsUnique );
    }

    public void handleImportAddedEvent( @Observes ImportAddedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        final List<String> availableTypes = Arrays.asList( oracle.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );
    }

    public void handleImportRemovedEvent( @Observes ImportRemovedEvent event ) {
        if ( !event.getDataModelOracle().equals( this.oracle ) ) {
            return;
        }
        final List<String> availableTypes = Arrays.asList( oracle.getFactTypes() );
        view.setAvailableFactTypes( availableTypes );
    }

    public void onDuplicatePatterns( final @Observes DuplicatePatternsEvent event ) {
        view.setArePatternBindingsUnique( event.getArePatternBindingsUnique() );
    }

    @Override
    public void isPatternEvent( final Pattern52 pattern,
                                final Callback<Boolean> callback ) {
        oracle.isFactTypeAnEvent( pattern.getFactType(),
                                  callback );
    }

    @Override
    public void signalRemovalOfPattern( final Pattern52 pattern ) {
        final PatternRemovedEvent event = new PatternRemovedEvent( pattern );
        patternRemovedEvent.fire( event );
    }

    @Override
    public void setConditionPatterns( final List<Pattern52> patterns ) {
        model.getConditions().clear();
        model.getConditions().addAll( patterns );
    }

    @Override
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Ensure every Pattern is bound
        int fi = 1;
        for ( Pattern52 p : model.getPatterns() ) {
            if ( !getValidator().isPatternValid( p ) ) {
                String binding = NEW_FACT_PREFIX + ( fi++ );
                p.setBoundName( binding );
                while ( !getValidator().isPatternBindingUnique( p ) ) {
                    binding = NEW_FACT_PREFIX + ( fi++ );
                    p.setBoundName( binding );
                }
            }
        }
    }

    @Override
    public void stateChanged() {
        final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( this );
        wizardPageStatusChangeEvent.fire( event );
    }

}
