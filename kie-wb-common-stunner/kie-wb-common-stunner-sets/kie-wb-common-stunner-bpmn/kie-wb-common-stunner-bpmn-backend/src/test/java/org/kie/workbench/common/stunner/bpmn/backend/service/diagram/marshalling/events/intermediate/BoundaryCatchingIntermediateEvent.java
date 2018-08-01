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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.events.intermediate;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.Marshaller;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BoundaryCatchingIntermediateEvent<T extends BaseCatchingIntermediateEvent> extends CatchingIntermediateEvent<T> {

    private static final int DEFAULT_AMOUNT_OF_INCOME_EDGES = 2;

    public BoundaryCatchingIntermediateEvent(Marshaller marshallerType) {
        super(marshallerType);
    }

    @Test
    public void testDockerInfo() throws Exception {
        Diagram<Graph, Metadata> initialDiagram = unmarshall(marshaller, getBpmnCatchingIntermediateEventFilePath());
        String resultXml = marshaller.marshall(initialDiagram);
        XMLReader xr = XMLReaderFactory.createXMLReader();

        // load all (id, dockerinfos) pairs for this file
        Map<String, String> dockerInfos = new HashMap<>();
        xr.setContentHandler(new DefaultHandler() {
            public void startElement(String s, String s1, String s2, Attributes attributes) {
                if (s1.equals("boundaryEvent")) {
                    dockerInfos.put(
                            attributes.getValue("id"),
                            attributes.getValue("drools:dockerinfo"));
                }
            }
        });
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = contextClassLoader.getResourceAsStream(getBpmnCatchingIntermediateEventFilePath());
        xr.parse(new InputSource(new InputStreamReader(resourceAsStream)));

        // check whether they're all present in the new file,
        // and remove it from the collection
        xr.setContentHandler(new DefaultHandler() {
            public void startElement(String s, String s1, String s2, Attributes attributes) {
                if (s1.equals("boundaryEvent")) {
                    String id = attributes.getValue("id");
                    assertThat(attributes.getValue("drools:dockerinfo"))
                            .as("Attributes should match for %s", id)
                            .isEqualTo(dockerInfos.remove(id));
                }
            }
        });
        xr.parse(new InputSource(new StringReader(resultXml)));

        assertThat(dockerInfos).as("the collection should be empty at the end (100% match)")
                .isEmpty();
    }

    @Test
    @Override
    public void testMarshallTopLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getFilledTopLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptyTopLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventFilledProperties() throws Exception {
        checkEventMarshalling(getFilledSubprocessLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptySubprocessLevelEventId(), HAS_NO_INCOME_EDGE, HAS_NO_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventWithEdgesFilledProperties() throws Exception {
        checkEventMarshalling(getFilledTopLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallTopLevelEventWithEdgesEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptyTopLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventWithEdgesFilledProperties() throws Exception {
        checkEventMarshalling(getFilledSubprocessLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Test
    @Override
    public void testMarshallSubprocessLevelEventWithEdgesEmptyProperties() throws Exception {
        checkEventMarshalling(getEmptySubprocessLevelEventWithEdgesId(), HAS_NO_INCOME_EDGE, HAS_OUTGOING_EDGE);
    }

    @Override
    protected int getDefaultAmountOfIncomdeEdges() {
        return DEFAULT_AMOUNT_OF_INCOME_EDGES;
    }
}
