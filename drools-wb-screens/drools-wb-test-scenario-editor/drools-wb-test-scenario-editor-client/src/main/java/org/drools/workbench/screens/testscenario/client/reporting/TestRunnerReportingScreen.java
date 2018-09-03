/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.testscenario.client.reporting;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.TestResults")
public class TestRunnerReportingScreen
        implements TestRunnerReportingView.Presenter {

    private TestRunnerReportingView view;

    public TestRunnerReportingScreen() {
        //Zero argument constructor for CDI
    }

    @Inject
    public TestRunnerReportingScreen( TestRunnerReportingView view,
                                      TestRuntimeReportingService testRuntimeReportingService ) {
        this.view = view;
        view.setPresenter( this );
        view.bindDataGridToService( testRuntimeReportingService );
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Reporting";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onSuccess( @Observes TestResultMessage testResultMessage ) {
        if ( testResultMessage.wasSuccessful() ) {
            view.showSuccess();
            view.setExplanation( "" );
        }

        view.setRunStatus( testResultMessage.getRunCount(), testResultMessage.getRunTime() );

    }

    @Override
    public void onMessageSelected( Failure failure ) {
        view.setExplanation( failure.getMessage() );
    }

    @Override
    public void onAddingFailure( Failure failure ) {
        view.showFailure();
    }
}
