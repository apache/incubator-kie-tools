/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.definition.service;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingMessage;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingRequest;
import org.kie.workbench.common.stunner.core.marshaller.MarshallingResponse;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DiagramMarshallerTest {

    public static final String MESSAGE = "error unmarshalling";
    private DiagramMarshaller tested;

    @Mock
    private Graph graph;

    @Mock
    private InputStream input;

    @Mock
    private Metadata metadata;

    private MarshallingRequest request;

    @Before
    public void setUp() {
        request = MarshallingRequest.builder()
                .metadata(metadata)
                .input(input)
                .mode(MarshallingRequest.Mode.ERROR)
                .build();
    }

    @Test
    public void unmarshallWithValidation() throws Exception {
        tested = spy(new DiagramMarshaller() {
            @Override
            public Graph unmarshall(Metadata metadata, InputStream input) {
                return graph;
            }

            @Override
            public String marshall(Diagram diagram) {
                return null;
            }

            @Override
            public DiagramMetadataMarshaller getMetadataMarshaller() {
                return null;
            }
        });

        final MarshallingResponse response = tested.unmarshallWithValidation(request);
        verify(tested).unmarshall(metadata, input);
        assertEquals(response.getResult(), graph);
        assertEquals(response.getState(), MarshallingResponse.State.SUCCESS);
    }

    @Test
    public void unmarshallWithValidationError() throws Exception {
        tested = spy(new DiagramMarshaller() {
            @Override
            public Graph unmarshall(Metadata metadata, InputStream input) {
                throw new RuntimeException(MESSAGE);
            }

            @Override
            public String marshall(Diagram diagram) {
                return null;
            }

            @Override
            public DiagramMetadataMarshaller getMetadataMarshaller() {
                return null;
            }
        });

        final MarshallingResponse response = tested.unmarshallWithValidation(request);
        verify(tested).unmarshall(metadata, input);
        assertNull(response.getResult());
        assertEquals(response.getState(), MarshallingResponse.State.ERROR);
        assertEquals(response.getMessages().size(), 1);
        final MarshallingMessage marshallingMessage =
                (MarshallingMessage) response.getMessages().stream().findFirst().get();
        assertEquals(marshallingMessage.getMessage(), MESSAGE);
    }
}