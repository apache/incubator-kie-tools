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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.lanes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.Result;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.LanePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.MetaDataAttributes;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LaneConverterTest {

    private LaneConverter tested;

    @Mock
    private PropertyWriterFactory propertyWriterFactory;

    private Node<View<? extends BPMNViewDefinition>, ?> node;

    @Mock
    private View<Lane> laneView;

    @Mock
    private View<DataObject> dataObjectView;

    private Lane lane;

    @Mock
    private MetaDataAttributes metaDataAttributes;

    @Mock
    private LanePropertyWriter writer;

    @Before
    public void setUp() {
        lane = new Lane();
        lane.setAdvancedData(new AdvancedData(metaDataAttributes));

        node = new NodeImpl<>(UUID.uuid());
        node.setContent(laneView);

        when(laneView.getDefinition()).thenReturn(lane);
        when(propertyWriterFactory.of(any(org.eclipse.bpmn2.Lane.class))).thenReturn(writer);

        tested = new LaneConverter(propertyWriterFactory);
    }

    @Test
    public void toElement() {
        Result<LanePropertyWriter> result1 = tested.toElement(node);
        verify(writer).setMetaData(metaDataAttributes);
        assertTrue(result1.isSuccess());

        Node<View<? extends BPMNViewDefinition>, ?> node2 = new NodeImpl<>(UUID.uuid());
        node2.setContent(dataObjectView);
        Result<LanePropertyWriter> result2 = tested.toElement(node2);
        assertTrue(result2.isIgnored());
    }
}
