package org.drools.workbench.screens.testscenario.client.reporting;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.testscenario.client.service.TestRuntimeReportingService;
import org.drools.workbench.screens.testscenario.model.Failure;
import org.drools.workbench.screens.testscenario.model.Success;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.Position;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.TestResults")
public class TestRunnerReportingScreen
        implements TestRunnerReportingView.Presenter {

    private final TestRunnerReportingView view;
    private final TestRuntimeReportingService testRuntimeReportingService;

    @Inject
    public TestRunnerReportingScreen(TestRunnerReportingView view,
                                     TestRuntimeReportingService testRuntimeReportingService) {
        this.view = view;
        this.testRuntimeReportingService = testRuntimeReportingService;
        view.setPresenter(this);
        view.bindDataGridToService(testRuntimeReportingService);
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Reporting";
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    public void onSuccess(@Observes Success success){
        view.showSuccess();
        view.setExplanation("");
    }

    @Override
    public void onMessageSelected(Failure failure) {
        view.setExplanation(failure.getMessage());
    }

    @Override
    public void onAddingFailure(Failure failure) {
        view.showFailure();
    }
}
