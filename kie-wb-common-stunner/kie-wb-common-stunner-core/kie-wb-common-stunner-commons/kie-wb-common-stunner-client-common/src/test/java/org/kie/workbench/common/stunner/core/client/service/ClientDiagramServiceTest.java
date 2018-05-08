/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.service;

import org.jboss.errai.common.client.api.Caller;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ClientDiagramServiceTest extends AbstractClientDiagramServiceTest<Metadata, Diagram<Graph, Metadata>, DiagramService, ClientDiagramServiceImpl<Metadata, Diagram<Graph, Metadata>, DiagramService>> {

    @Override
    protected Metadata makeTestMetadata() {
        return mock(Metadata.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Diagram<Graph, Metadata> makeTestDiagram() {
        return mock(Diagram.class);
    }

    @Override
    protected DiagramService makeTestDiagramService() {
        return mock(DiagramService.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ClientDiagramServiceImpl makeTestClientDiagramService() {
        final Caller<DiagramService> diagramServiceCaller = new CallerMock<>(diagramService);
        final Caller<DiagramLookupService> diagramLookupServiceCaller = new CallerMock<>(diagramLookupService);
        return new ClientDiagramServiceImpl(shapeManager,
                                        diagramServiceCaller,
                                        diagramLookupServiceCaller,
                                        saveDiagramSessionCommandExecutedEventEvent);
    }
}
