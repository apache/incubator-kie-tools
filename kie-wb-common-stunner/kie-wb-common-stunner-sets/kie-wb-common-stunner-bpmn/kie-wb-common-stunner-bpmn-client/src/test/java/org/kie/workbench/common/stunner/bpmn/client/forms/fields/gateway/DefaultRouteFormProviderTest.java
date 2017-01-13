/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.gateway;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ContextUtils;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.core.client.session.ClientSessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.jgroups.util.Util.assertEquals;
import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextUtils.class)
public class DefaultRouteFormProviderTest {

    @Mock
    ClientSessionManager canvasSessionManager;

    @Mock
    FormRenderingContext context;

    @Mock
    ExclusiveDatabasedGateway gateway;

    @Mock
    DefaultRouteFormProvider defaultRouteFormProvider;

    Node gatewayNode = new NodeImpl<>("node1");

    @Mock
    Edge edge1;
    @Mock
    Edge edge2;
    @Mock
    Edge edge3;

    @Mock
    ViewConnector edge1ViewConnector;
    @Mock
    ViewConnector edge2ViewConnector;
    @Mock
    ViewConnector edge3ViewConnector;

    @Before
    public void setUp() throws Exception {
        PowerMockito.mockStatic(ContextUtils.class);
        PowerMockito.when(ContextUtils.getModel(Mockito.any(FormRenderingContext.class))).
                thenReturn(gateway);

        Mockito.when(defaultRouteFormProvider.getExclusiveDatabasedGatewayNode(Mockito.any(ExclusiveDatabasedGateway.class))).
                thenReturn(gatewayNode);

        Mockito.doCallRealMethod().when(defaultRouteFormProvider).getSelectorData(Mockito.any(FormRenderingContext.class));
        Mockito.doCallRealMethod().when(defaultRouteFormProvider).getGatewayOutEdges(Mockito.any(FormRenderingContext.class));

        Mockito.when(edge1.getUUID()).thenReturn("Edge1");
        Mockito.when(edge2.getUUID()).thenReturn("Edge2");
        Mockito.when(edge3.getUUID()).thenReturn("Edge3");

        Mockito.when(edge1.getContent()).thenReturn(edge1ViewConnector);
        Mockito.when(edge2.getContent()).thenReturn(edge2ViewConnector);
        Mockito.when(edge3.getContent()).thenReturn(edge3ViewConnector);

        Mockito.when(edge1ViewConnector.getDefinition()).thenReturn(new SequenceFlow(new BPMNGeneralSet("sequence"), null, null, null));
        Mockito.when(edge2ViewConnector.getDefinition()).thenReturn(new SequenceFlow(new BPMNGeneralSet("sequence"), null, null, null));
        Mockito.when(edge3ViewConnector.getDefinition()).thenReturn(new SequenceFlow(new BPMNGeneralSet("sequence"), null, null, null));

        UserTask userTask1 = new UserTask(new TaskGeneralSet(new Name("Task1"), null), null, null, null, null, null, null, null, null);
        UserTask userTask2 = new UserTask(new TaskGeneralSet(new Name("Task2"), null), null, null, null, null, null, null, null, null);
        UserTask userTask3 = new UserTask(new TaskGeneralSet(new Name("Task3"), null), null, null, null, null, null, null, null, null);
        Mockito.when(defaultRouteFormProvider.getEdgeTarget(edge1)).thenReturn(userTask1);
        Mockito.when(defaultRouteFormProvider.getEdgeTarget(edge2)).thenReturn(userTask2);
        Mockito.when(defaultRouteFormProvider.getEdgeTarget(edge3)).thenReturn(userTask3);
    }

    @Test
    public void testGetSelectorData() {
        gatewayNode.getOutEdges().add(edge1);
        gatewayNode.getOutEdges().add(edge2);
        gatewayNode.getOutEdges().add(edge3);

        SelectorData selectorData = defaultRouteFormProvider.getSelectorData(context);
        Map<String, String> values = selectorData.getValues();
        assertTrue(values.size() == 3);
        assertEquals("Task1", values.get("sequence : Edge1"));
        assertEquals("Task2", values.get("sequence : Edge2"));
        assertEquals("Task3", values.get("sequence : Edge3"));
    }
}
