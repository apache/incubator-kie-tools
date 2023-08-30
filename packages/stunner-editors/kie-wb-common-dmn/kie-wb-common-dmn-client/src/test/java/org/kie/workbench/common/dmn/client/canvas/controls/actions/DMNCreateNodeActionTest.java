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

package org.kie.workbench.common.dmn.client.canvas.controls.actions;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasLayoutUtils;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;

@RunWith(GwtMockitoTestRunner.class)
public class DMNCreateNodeActionTest {

    @Mock
    private DMNCreateNodeAction dmnCreateNodeAction;

    @Test
    public void testGetNodeOrientation_WhenDefinitionIsDecision() {

        doCallRealMethod().when(dmnCreateNodeAction).getNodeOrientation(any(Node.class));

        final NodeImpl targetNode = new NodeImpl<>("node");

        targetNode.setContent(new ViewImpl<>(new Decision(), Bounds.createEmpty()));

        final CanvasLayoutUtils.Orientation actual = dmnCreateNodeAction.getNodeOrientation(targetNode);

        assertEquals(CanvasLayoutUtils.Orientation.UpRight, actual);
    }

    @Test
    public void testGetNodeOrientation_WhenDefinitionIsBusinessKnowledgeModel() {

        doCallRealMethod().when(dmnCreateNodeAction).getNodeOrientation(any(Node.class));

        final NodeImpl targetNode = new NodeImpl<>("node");

        targetNode.setContent(new ViewImpl<>(new BusinessKnowledgeModel(), Bounds.createEmpty()));

        final CanvasLayoutUtils.Orientation actual = dmnCreateNodeAction.getNodeOrientation(targetNode);

        assertEquals(CanvasLayoutUtils.Orientation.UpRight, actual);
    }

    @Test
    public void testGetNodeOrientation() {

        doCallRealMethod().when(dmnCreateNodeAction).getNodeOrientation(any(Node.class));

        final NodeImpl targetNode = new NodeImpl<>("node");

        targetNode.setContent(new ViewImpl<>(new Object(), Bounds.createEmpty()));

        final CanvasLayoutUtils.Orientation actual = dmnCreateNodeAction.getNodeOrientation(targetNode);

        assertEquals(CanvasLayoutUtils.DEFAULT_NEW_NODE_ORIENTATION, actual);
    }
}
