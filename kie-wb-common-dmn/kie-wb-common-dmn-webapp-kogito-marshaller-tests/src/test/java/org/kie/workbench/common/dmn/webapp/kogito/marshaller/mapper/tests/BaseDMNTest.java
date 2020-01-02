/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.tests;

import java.util.logging.Logger;

import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.DMNTest;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.kogito.client.service.KogitoClientDiagramService;

import static junit.framework.TestCase.fail;

public abstract class BaseDMNTest implements DMNTest {

    private static final Logger LOGGER = Logger.getLogger(BaseDMNTest.class.getName());

    protected void test(final KogitoClientDiagramService service,
                        final String xml) {
        LOGGER.info("Running " + getTestName() + "...");

        try {
            service.transform(xml,
                              new ServiceCallback<Diagram>() {
                                  @Override
                                  public void onSuccess(final Diagram diagram) {
                                      logDiagramBasics(diagram);
                                      doAssertions(diagram);
                                  }

                                  @Override
                                  public void onError(final ClientRuntimeError error) {
                                      LOGGER.info(error.getMessage());
                                      fail(error.getMessage());
                                  }
                              });
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

    protected void logDiagramBasics(final Diagram diagram) {
        final StringBuilder sb = new StringBuilder("\n");
        sb.append("-> Name: " + diagram.getName()).append("\n");
        sb.append("-> DefinitionSetId: " + diagram.getMetadata().getDefinitionSetId()).append("\n");
        sb.append("-> Nodes").append("\n");
        for (Object node : diagram.getGraph().nodes()) {
            sb.append("---> Node: " + node).append("\n");
        }
        LOGGER.info(sb.toString());
    }
}
