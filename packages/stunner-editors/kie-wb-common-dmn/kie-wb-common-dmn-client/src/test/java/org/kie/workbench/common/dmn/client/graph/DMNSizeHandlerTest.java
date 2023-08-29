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
package org.kie.workbench.common.dmn.client.graph;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNSizeHandlerTest {

    @Captor
    private ArgumentCaptor<Height> heightArgumentCaptor;

    @Captor
    private ArgumentCaptor<Width> widthArgumentCaptor;

    @Test
    public void testSetSize() {

        final double width = 300;
        final double height = 100;
        final DMNSizeHandler sizeHandler = new DMNSizeHandler();
        final RectangleDimensionsSet rectangle = mock(RectangleDimensionsSet.class);
        final Node node = createNode(rectangle);

        sizeHandler.setSize(node, width, height);

        verify(rectangle).setWidth(widthArgumentCaptor.capture());
        verify(rectangle).setHeight(heightArgumentCaptor.capture());

        final Width capturedWidth = widthArgumentCaptor.getValue();
        final Height capturedHeight = heightArgumentCaptor.getValue();

        assertEquals(width, capturedWidth.getValue(), 0.01);
        assertEquals(height, capturedHeight.getValue(), 0.01);
    }

    private Node createNode(final RectangleDimensionsSet rectangle) {
        final Node node = mock(Node.class);
        final Definition content = mock(Definition.class);
        final DMNViewDefinition viewDefinition = mock(DMNViewDefinition.class);
        when(node.getContent()).thenReturn(content);
        when(content.getDefinition()).thenReturn(viewDefinition);
        when(viewDefinition.getDimensionsSet()).thenReturn(rectangle);

        return node;
    }
}