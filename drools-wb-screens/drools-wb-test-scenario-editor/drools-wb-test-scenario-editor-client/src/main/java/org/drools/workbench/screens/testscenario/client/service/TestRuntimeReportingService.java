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

package org.drools.workbench.screens.testscenario.client.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;

@ApplicationScoped
public class TestRuntimeReportingService {

    private PlaceManager placeManager;

    protected User identity;

    private ListDataProvider<Failure> dataProvider = new ListDataProvider<Failure>();

    public TestRuntimeReportingService() {
    }

    @Inject
    public TestRuntimeReportingService(PlaceManager placeManager,
                                       User identity) {
        this.placeManager = placeManager;
        this.identity = identity;
    }

    public void addBuildMessages(final @Observes TestResultMessage message) {
        if (message.getIdentifier().equals(identity.getIdentifier())) {
            dataProvider.getList().clear();

            if (!message.wasSuccessful()) {
                dataProvider.getList().addAll(message.getFailures());
                dataProvider.flush();
            }

            placeManager.goTo("org.kie.guvnor.TestResults");
        }
    }

    public void addDataDisplay(HasData<Failure> failures) {
        dataProvider.addDataDisplay(failures);
    }
}
