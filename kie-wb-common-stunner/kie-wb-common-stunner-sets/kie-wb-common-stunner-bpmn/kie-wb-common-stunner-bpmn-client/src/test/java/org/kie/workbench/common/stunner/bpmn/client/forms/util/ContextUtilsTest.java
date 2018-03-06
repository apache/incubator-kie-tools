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

package org.kie.workbench.common.stunner.bpmn.client.forms.util;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ParallelGateway;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

public class ContextUtilsTest {

    @Test
    public void testIsFormGenerationSupported() {
        Assert.assertTrue(ContextUtils.isFormGenerationSupported(createNode(new UserTask())));
        Assert.assertFalse(ContextUtils.isFormGenerationSupported(createNode(new ScriptTask())));
        Assert.assertFalse(ContextUtils.isFormGenerationSupported(createNode(new BusinessRuleTask())));
        Assert.assertFalse(ContextUtils.isFormGenerationSupported(createNode(new NoneTask())));
        Assert.assertFalse(ContextUtils.isFormGenerationSupported(createNode(new StartNoneEvent())));
        Assert.assertFalse(ContextUtils.isFormGenerationSupported(createNode(new EndNoneEvent())));
        Assert.assertFalse(ContextUtils.isFormGenerationSupported(createNode(new ParallelGateway())));
    }

    private static Node createNode(final Object def) {
        NodeImpl<Object> node = new NodeImpl<>("id1");
        node.setContent(new ViewImpl<>(def,
                                       BoundsImpl.build()));
        return node;
    }
}
