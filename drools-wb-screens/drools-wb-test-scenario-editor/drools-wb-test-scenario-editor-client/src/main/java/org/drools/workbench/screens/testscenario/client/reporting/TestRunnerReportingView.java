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

}
