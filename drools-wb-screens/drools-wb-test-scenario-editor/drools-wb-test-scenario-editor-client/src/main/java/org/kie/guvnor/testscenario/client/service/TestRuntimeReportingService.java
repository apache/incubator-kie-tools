package org.kie.guvnor.testscenario.client.service;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.kie.guvnor.testscenario.model.Failure;
import org.kie.guvnor.testscenario.model.Success;
import org.kie.guvnor.testscenario.model.TestResultMessage;
import org.uberfire.client.mvp.PlaceManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

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
        }

        placeManager.goTo("org.kie.guvnor.TestResults");
    }

    public void addDataDisplay(HasData<Failure> failures) {
        dataProvider.addDataDisplay(failures);
    }
}
