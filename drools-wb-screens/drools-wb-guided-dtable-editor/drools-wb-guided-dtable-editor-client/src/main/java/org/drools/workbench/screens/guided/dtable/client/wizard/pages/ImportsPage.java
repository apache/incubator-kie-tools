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
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.imports.Import;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.Validator;
import org.kie.workbench.common.widgets.client.datamodel.ImportAddedEvent;
import org.kie.workbench.common.widgets.client.datamodel.ImportRemovedEvent;
import org.uberfire.client.callbacks.Callback;

/**
 * A page for the guided Decision Table Wizard to define imports
 */
@Dependent
public class ImportsPage extends AbstractGuidedDecisionTableWizardPage
        implements
        ImportsPageView.Presenter {

    @Inject
    private ImportsPageView view;

    @Inject
    private Event<ImportAddedEvent> importAddedEvent;

    @Inject
    private Event<ImportRemovedEvent> importRemovedEvent;

    private Validator validator;

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardImports();
    }

    @Override
    public void initialise() {
        view.init( this );

        final List<String> chosenImports = getChosenImports();
        final List<String> availableImports = Arrays.asList( oracle.getExternalFactTypes() );
        view.setChosenImports( chosenImports );
        view.setAvailableImports( availableImports );

        validator = getValidator();
        content.setWidget( view );
    }

    private List<String> getChosenImports() {
        final List<String> imports = new ArrayList<String>();
        for ( Import imp : model.getImports().getImports() ) {
            imports.add( imp.getType() );
        }
        return imports;
    }

    @Override
    public void prepareView() {
        // Nothing needs to be done when the page is viewed; it is setup in initialise
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        //Imports are optional
        callback.callback( true );
    }

    @Override
    public void addImport( final String fqcn ) {
        //Filter DMO
        final Import addedImport = new Import( fqcn );
        model.getImports().addImport( addedImport );
        oracle.filter( model.getImports() );

        //Signal change to any other interested consumers (e.g. some editors support rendering of unknown fact-types)
        importAddedEvent.fire( new ImportAddedEvent( oracle,
                                                     addedImport ) );
    }

    @Override
    public boolean removeImport( final String fqcn ) {
        //Check import can be removed
        if ( validator.isTypeUsed( fqcn ) ) {
            return false;
        }

        //Filter DMO
        final Import removedImport = new Import( fqcn );
        model.getImports().removeImport( removedImport );
        oracle.filter( model.getImports() );

        //Signal change to any other interested consumers (e.g. some editors support rendering of unknown fact-types)
        importRemovedEvent.fire( new ImportRemovedEvent( oracle,
                                                         removedImport ) );
        return true;
    }

    @Override
    public void makeResult( final GuidedDecisionTable52 model ) {
        //Nothing to do; imports are adjusted as and when they're removed in the UI
    }

}
