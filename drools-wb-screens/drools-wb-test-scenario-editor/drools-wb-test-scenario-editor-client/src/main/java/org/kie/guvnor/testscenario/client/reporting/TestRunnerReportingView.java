package org.kie.guvnor.testscenario.client.reporting;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.testscenario.client.service.TestRuntimeReportingService;
import org.kie.guvnor.testscenario.model.Failure;
import org.kie.guvnor.testscenario.model.TestResultMessage;

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
