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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.StartMessageEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class EventInterruptingViewHandlerTest extends EventViewHandlerTestBase {

    private EventInterruptingViewHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        super.init();
        when(child1.getId()).thenReturn(EventInterruptingViewHandler.ID_START);
        when(child2.getId()).thenReturn(EventInterruptingViewHandler.ID_START_NON_INTERRUPTING);
        tested = new EventInterruptingViewHandler();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleTimerIsNotInterrupting() {
        final StartTimerEvent bean = new StartTimerEvent.StartTimerEventBuilder().build();
        bean.getExecutionSet().getIsInterrupting().setValue(false);
        tested.handle(bean, view);
        verify(prim1).setAlpha(eq(0d));
        verify(prim2).setAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleTimerIsInterrupting() {
        final StartTimerEvent bean = new StartTimerEvent.StartTimerEventBuilder().build();
        bean.getExecutionSet().getIsInterrupting().setValue(true);
        tested.handle(bean, view);
        verify(prim1).setAlpha(eq(1d));
        verify(prim2).setAlpha(eq(0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleMessageEventIsNotInterrupting() {
        final StartMessageEvent bean = new StartMessageEvent.StartMessageEventBuilder().build();
        bean.getExecutionSet().getIsInterrupting().setValue(false);
        tested.handle(bean, view);
        verify(prim1).setAlpha(eq(0d));
        verify(prim2).setAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleMessageEventIsInterrupting() {
        final StartMessageEvent bean = new StartMessageEvent.StartMessageEventBuilder().build();
        bean.getExecutionSet().getIsInterrupting().setValue(true);
        tested.handle(bean, view);
        verify(prim1).setAlpha(eq(1d));
        verify(prim2).setAlpha(eq(0d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleSignalEventIsNotInterrupting() {
        final StartSignalEvent bean = new StartSignalEvent.StartSignalEventBuilder().build();
        bean.getExecutionSet().getIsInterrupting().setValue(false);
        tested.handle(bean, view);
        verify(prim1).setAlpha(eq(0d));
        verify(prim2).setAlpha(eq(1d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleSignalEventIsInterrupting() {
        final StartSignalEvent bean = new StartSignalEvent.StartSignalEventBuilder().build();
        bean.getExecutionSet().getIsInterrupting().setValue(true);
        tested.handle(bean, view);
        verify(prim1).setAlpha(eq(1d));
        verify(prim2).setAlpha(eq(0d));
    }
}
