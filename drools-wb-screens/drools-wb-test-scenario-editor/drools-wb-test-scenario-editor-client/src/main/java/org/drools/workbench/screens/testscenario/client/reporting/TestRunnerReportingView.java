package org.drools.workbench.screens.testscenario.client.reporting;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.drools.workbench.screens.testscenario.model.Failure;

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
