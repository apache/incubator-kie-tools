/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

/**
 * A summary page for the guided Decision Table Wizard
 */
@Dependent
public class SummaryPage extends AbstractGuidedDecisionTableWizardPage
        implements
        SummaryPageView.Presenter {

    @Inject
    private SummaryPageView view;

    @Inject
    private Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Inject
    private Caller<ValidationService> fileNameValidationService;

    private boolean isBaseFileNameValid = false;

    @Override
    public String getTitle() {
        return GuidedDecisionTableConstants.INSTANCE.DecisionTableWizardSummary();
    }

    @Override
    public void isComplete( final Callback<Boolean> callback ) {
        callback.callback( isBaseFileNameValid );
    }

    @Override
    public void initialise() {
        view.init( this );
        view.setBaseFileName( baseFileName );
        view.setContextPath( contextPath );
        view.setTableFormat( tableFormat );
        view.setHitPolicy( hitPolicy );
        content.setWidget( view );
        stateChanged();
    }

    @Override
    public void prepareView() {
        //Nothing required
    }

    @Override
    public void stateChanged() {
        fileNameValidationService.call( new RemoteCallback<Boolean>() {
            @Override
            public void callback( final Boolean response ) {
                isBaseFileNameValid = Boolean.TRUE.equals( response );
                view.setValidBaseFileName( isBaseFileNameValid );
                fireEvent();
            }
        } ).isFileNameValid( view.getBaseFileName() );
    }

    // package protected to allow overriding in tests
    void fireEvent() {
        final WizardPageStatusChangeEvent event = new WizardPageStatusChangeEvent( this );
        wizardPageStatusChangeEvent.fire( event );
    }

    @Override
    public String getBaseFileName() {
        return view.getBaseFileName();
    }

}
