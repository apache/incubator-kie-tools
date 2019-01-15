/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.verifier.reporting.client.analysis;

import java.util.HashSet;
import java.util.logging.Logger;

import com.google.gwt.webworker.client.Worker;
import org.drools.verifier.api.Status;
import org.drools.verifier.api.reporting.Issues;
import org.jboss.errai.enterprise.client.jaxrs.MarshallingWrapper;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.verifier.api.client.api.WebWorkerException;
import org.kie.workbench.common.services.verifier.api.client.api.WebWorkerLogMessage;

public class Receiver {

    private static final Logger LOGGER = Logger.getLogger("DTable Analyzer");

    private AnalysisReporter reporter;

    public Receiver(final AnalysisReporter reporter) {
        this.reporter = PortablePreconditions.checkNotNull("reporter",
                                                           reporter);
    }

    public void activate() {
        reporter.activate();
    }

    private void received(final String json) {

        try {

            LOGGER.finest("Receiving: " + json);

            final Object o = MarshallingWrapper.fromJSON(json);

            if (o instanceof WebWorkerLogMessage) {
                LOGGER.info("Web Worker log message: " + ((WebWorkerLogMessage) o).getMessage());
            } else if (o instanceof WebWorkerException) {
                LOGGER.severe("Web Worker failed: " + ((WebWorkerException) o).getMessage());
            } else if (o instanceof Status) {
                reporter.sendStatus((Status) o);
            } else if (o instanceof Issues) {
                reporter.sendReport(new HashSet<>(((Issues) o).getSet()));
            }
        } catch (Exception e) {
            LOGGER.severe("Could not manage received json: " + e.getMessage()
                                  + " JSON: " + json);
        }
    }

    public void setUp(final Worker worker) {
        worker.setOnMessage(messageEvent -> received(messageEvent.getDataAsString()));
    }
}
