package org.drools.workbench.screens.testscenario.client.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.drools.workbench.screens.testscenario.model.Success;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.uberfire.client.mvp.PlaceManager;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

@ApplicationScoped
public class TestRuntimeReportingService {

    @Inject
    private PlaceManager placeManager;

    @Inject
    private Event<Success> successEvent;

    private ListDataProvider<Failure> dataProvider = new ListDataProvider<Failure>();


    public TestRuntimeReportingService() {

    }

    public void addBuildMessages(final @Observes TestResultMessage message) {
        dataProvider.getList().clear();

        if (message.wasSuccessful()) {
            successEvent.fire(new Success());
        } else {
            dataProvider.getList().addAll(message.getFailures());
            dataProvider.flush();
        }

        placeManager.goTo("org.kie.guvnor.TestResults");
    }

    public void addDataDisplay(HasData<Failure> failures) {
        dataProvider.addDataDisplay(failures);
    }
}
