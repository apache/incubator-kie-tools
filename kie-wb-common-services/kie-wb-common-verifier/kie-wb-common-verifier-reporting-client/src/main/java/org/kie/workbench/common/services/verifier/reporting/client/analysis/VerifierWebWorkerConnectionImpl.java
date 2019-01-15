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

import java.util.logging.Logger;

import com.google.gwt.webworker.client.Worker;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.verifier.api.client.api.Initialize;
import org.kie.workbench.common.services.verifier.api.client.api.RequestStatus;

public class VerifierWebWorkerConnectionImpl
        implements VerifierWebWorkerConnection {

    private static final Logger LOGGER = Logger.getLogger("DTable Analyzer");

    private Worker worker = null;

    private final Poster poster;
    private final Receiver receiver;
    private final Initialize initialize;
    private final String pathToVerifier;

    public VerifierWebWorkerConnectionImpl(final Initialize initialize,
                                           final String pathToVerifier,
                                           final Poster poster,
                                           final Receiver receiver) {

        this.initialize = PortablePreconditions.checkNotNull("initialize",
                                                             initialize);

        this.pathToVerifier = PortablePreconditions.checkNotNull("pathToVerifier",
                                                                 pathToVerifier);
        this.poster = PortablePreconditions.checkNotNull("poster",
                                                         poster);
        this.receiver = PortablePreconditions.checkNotNull("receiver",
                                                           receiver);

        LOGGER.finest("Created Web Worker");
    }

    private void startWorker() {
        worker = Worker.create(pathToVerifier);

        poster.setUp(worker);
        receiver.setUp(worker);
    }

    @Override
    public void activate() {

        receiver.activate();

        if (worker == null) {
            startWorker();
            poster.post(initialize);
        } else {
            poster.post(new RequestStatus());
        }
    }

    @Override
    public void terminate() {
        if (worker != null) {
            worker.terminate();
        }
    }
}
