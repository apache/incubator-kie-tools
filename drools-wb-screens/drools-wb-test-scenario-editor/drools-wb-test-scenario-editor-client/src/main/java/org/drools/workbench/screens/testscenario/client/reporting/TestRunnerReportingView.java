/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.workbench.screens.testscenario.client.reporting;

import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.guvnor.common.services.shared.test.Failure;

import com.google.gwt.user.client.ui.IsWidget;

public interface TestRunnerReportingView
        extends IsWidget {


    interface Presenter {

        void onMessageSelected(Failure failure);

        void onAddingFailure(Failure failure);

    }
    void setPresenter(Presenter presenter);

    void bindDataGridToService(TestRuntimeReportingService testRuntimeReportingService);

    void showSuccess();

    void showFailure();

    void setExplanation(String explanation);

    void setRunStatus(int runCount, long runTime);

}
