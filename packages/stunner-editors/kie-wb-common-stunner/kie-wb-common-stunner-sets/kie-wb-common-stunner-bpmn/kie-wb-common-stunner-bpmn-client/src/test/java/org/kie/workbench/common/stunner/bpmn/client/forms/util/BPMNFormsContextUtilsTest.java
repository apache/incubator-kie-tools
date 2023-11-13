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


package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

import static org.mockito.Mockito.mock;

public class BPMNFormsContextUtilsTest {

    @Test
    public void testIsFormGenerationSupported() {
        Assert.assertTrue(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new UserTask())));
        Assert.assertTrue(BPMNFormsContextUtils.isFormGenerationSupported(createNode(mock(BaseUserTask.class))));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(mock(BaseTask.class))));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new ScriptTask())));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new BusinessRuleTask())));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new NoneTask())));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new StartNoneEvent())));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new EndNoneEvent())));
        Assert.assertFalse(BPMNFormsContextUtils.isFormGenerationSupported(createNode(new ParallelGateway())));
    }

    private static Node createNode(final Object def) {
        NodeImpl<Object> node = new NodeImpl<>("id1");
        node.setContent(new ViewImpl<>(def,
                                       Bounds.create()));
        return node;
    }
}
