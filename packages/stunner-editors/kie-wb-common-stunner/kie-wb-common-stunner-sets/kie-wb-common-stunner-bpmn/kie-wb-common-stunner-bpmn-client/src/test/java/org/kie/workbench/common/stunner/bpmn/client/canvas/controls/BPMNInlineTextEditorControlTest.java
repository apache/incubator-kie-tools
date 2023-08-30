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

package org.kie.workbench.common.stunner.bpmn.client.canvas.controls;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BaseCatchingIntermediateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventGateway;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;

@RunWith(MockitoJUnitRunner.class)
public class BPMNInlineTextEditorControlTest {

    @Mock
    private BPMNInlineTextEditorControl inline;

    @Mock
    private EventGateway gateway;

    @Mock
    private EndCompensationEvent endEvent;

    @Mock
    private StartCompensationEvent startEvent;

    @Mock
    private BaseCatchingIntermediateEvent intermediateEvent;

    @Mock
    private UserTask userTask;

    @Mock
    private EmbeddedSubprocess embeddedSubprocess;

    @Mock
    private DataObject dataObject;

    @Mock
    private TextAnnotation textAnnotation;

    @Test
    public void testEditableUserTask() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(true, inline.isFiltered(userTask));
    }

    @Test
    public void testEditableEmbeddedSubprocess() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(true, inline.isFiltered(embeddedSubprocess));
    }

    @Test
    public void testEditableDataObject() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(true, inline.isFiltered(dataObject));
    }

    @Test
    public void testEditableTextAnnotation() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(true, inline.isFiltered(textAnnotation));
    }

    @Test
    public void testEditableGateway() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(false, inline.isFiltered(gateway));
    }

    @Test
    public void testEditableEndEvent() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(false, inline.isFiltered(endEvent));
    }

    @Test
    public void testEditableStartEvent() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(false, inline.isFiltered(startEvent));
    }

    @Test
    public void testEditableIntermediateEvent() {
        doCallRealMethod().when(inline).isFiltered(any());
        assertEquals(false, inline.isFiltered(intermediateEvent));
    }
}