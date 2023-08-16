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


package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.core.client.shape.TextWrapperStrategy;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.HorizontalAlignment;
import static org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Orientation;
import static org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.ReferencePosition;
import static org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.Size;
import static org.kie.workbench.common.stunner.core.client.shape.view.HasTitle.VerticalAlignment;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class BPMNShapeViewHandlersTest {

    private UserTask task;

    private FontHandler<BPMNViewDefinition, ShapeView> fontHandler;

    @Mock
    private WiresShapeViewExt text;

    @Captor
    private ArgumentCaptor<TextWrapperStrategy> wrapper;

    @Captor
    private ArgumentCaptor<Size> sizeConstraints;

    @Before
    public void setUp() {
        task = new UserTask();
        task.getFontSet().getFontBorderSize().setValue(0.0);
        fontHandler = new BPMNShapeViewHandlers.FontHandlerBuilder<>().build();
    }

    @Test
    public void testFontHandler() {
        fontHandler.handle(task, text);
        verify(text).setTextWrapper(wrapper.capture());
        assertEquals(wrapper.getValue(), TextWrapperStrategy.TRUNCATE_WITH_LINE_BREAK);
        verify(text).setTitleStrokeWidth(0);
        verify(text).setTitleStrokeAlpha(0);
        verify(text).setTitlePosition(VerticalAlignment.MIDDLE, HorizontalAlignment.CENTER,
                                      ReferencePosition.INSIDE, Orientation.HORIZONTAL);
        verify(text, never()).setMargins(anyMap());
        verify(text).setTextSizeConstraints(sizeConstraints.capture());

        final Size size = sizeConstraints.getValue();
        assertEquals(size.getHeight(), 100, 0d);
        assertEquals(size.getWidth(), 100, 0d);
        assertEquals(size.getType(), Size.SizeType.PERCENTAGE);
    }
}