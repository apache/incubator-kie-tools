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


package org.kie.workbench.common.stunner.bpmn.client.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class GraphUtilsTest {

    @Test
    public void testContentIsNotSet() {
        Element<Object> element = mock(Element.class);
        assertFalse(GraphUtils.isReusableSubProcess(element));
    }

    @Test
    public void testNonView() {
        Element<Object> element = new NodeImpl<>("UUID");
        element.setContent(new Object());
        assertFalse(GraphUtils.isReusableSubProcess(element));
    }

    @Test
    public void testNonReusableSubprocess() {
        Element<View<UserTask>> element = new NodeImpl<>("UUID");
        View<UserTask> userTaskView = new ViewImpl<>(new UserTask(), Bounds.create());
        element.setContent(userTaskView);
        assertFalse(GraphUtils.isReusableSubProcess(element));
    }

    @Test
    public void testReusableSubprocess() {
        Element<View<ReusableSubprocess>> element = new NodeImpl<>("UUID");
        View<ReusableSubprocess> reusableSubprocessView = new ViewImpl<>(new ReusableSubprocess(), Bounds.create());
        element.setContent(reusableSubprocessView);
        assertTrue(GraphUtils.isReusableSubProcess(element));
    }
}