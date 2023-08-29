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


package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.Collections;
import java.util.function.Function;

import com.ait.lienzo.client.core.image.ImageProxy;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.CustomTaskShapeViewHandler;
import org.kie.workbench.common.stunner.bpmn.workitem.CustomTask;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CustomTaskShapeDefTest {

    private static final String WID_ID_1 = "id1";
    private static final String WID_ICON_DATA_1 = "iconData1";
    private static final String DEF_ID_1 = BindableAdapterUtils.getDynamicDefinitionId(CustomTask.class,
                                                                                       WID_ID_1);

    private static final String WID_ID_2 = "id2";
    private static final String WID_ICON_DATA_2 = "serviceNodeIcon";
    private static final String DEF_ID_2 = BindableAdapterUtils.getDynamicDefinitionId(CustomTask.class,
                                                                                       WID_ID_2);

    private static final String WID_ID_3 = null;
    private static final String WID_ICON_DATA_3 = "serviceNodeIcon";
    private static final String DEF_ID_3 = BindableAdapterUtils.getDynamicDefinitionId(CustomTask.class,
                                                                                       WID_ID_3);

    private static final WorkItemDefinition DEF_1 = new WorkItemDefinition()
            .setName(WID_ID_1)
            .setIconDefinition(new IconDefinition().setIconData(WID_ICON_DATA_1));

    private static final double WIDTH = 111d;
    private static final double HEIGHT = 121d;
    private static final Bounds BOUNDS = Bounds.create(0, 0, WIDTH, HEIGHT);

    @Mock
    private WorkItemDefinitionRegistry registry;

    @Mock
    private Function<String, Glyph> iconDataGlyphGenerator;

    @Mock
    private View node;

    @Mock
    private SVGShapeViewImpl view;

    @Mock
    private SVGPrimitive imageSvgPrimitive;

    @Mock
    private Picture imagePrimitive;

    @Mock
    private ImageProxy imageProxy;

    private CustomTaskShapeDef tested;
    private CustomTask task;

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        this.task = new CustomTask();
        task.getDimensionsSet().getWidth().setValue(WIDTH);
        task.getDimensionsSet().getHeight().setValue(HEIGHT);
        this.task.setName(WID_ID_1);
        when(node.getDefinition()).thenReturn(task);
        when(node.getBounds()).thenReturn(BOUNDS);
        when(registry.items()).thenReturn(Collections.singleton(DEF_1));
        when(registry.get(eq(WID_ID_1))).thenReturn(DEF_1);
        when(registry.get(eq(WID_ID_2))).thenReturn(null);
        when(registry.get(eq(WID_ID_3))).thenThrow(new RuntimeException("Just for Testing"));
        when(view.getChildren()).thenReturn(Collections.singletonList(imageSvgPrimitive));
        when(imageSvgPrimitive.getId()).thenReturn(CustomTaskShapeViewHandler.WID_ICON_ID);
        when(imageSvgPrimitive.get()).thenReturn(imagePrimitive);
        when(imageSvgPrimitive.getPrimitiveId()).thenReturn(CustomTaskShapeViewHandler.WID_ICON_ID);
        when(imagePrimitive.getID()).thenReturn(CustomTaskShapeViewHandler.WID_ICON_ID);
        when(imagePrimitive.getImageProxy()).thenReturn(imageProxy);
        this.tested = new CustomTaskShapeDef(() -> registry,
                                             iconDataGlyphGenerator);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSizeHandler() {
        tested.newSizeHandler().handle(node, view);
        verify(view, times(1)).setSize(eq(111d),
                                       eq(121d));
    }

    @Test
    public void testViewHandler() {
        when(imageProxy.isLoaded()).thenReturn(true);
        tested.viewHandler().accept(task,
                                    view);
        verify(imageProxy, times(1)).load(eq(WID_ICON_DATA_1));
    }

    @Test
    public void testGetGlyphCustomTaskWithWID() {
        Glyph expected1 = mock(Glyph.class);

        when(iconDataGlyphGenerator.apply(eq(WID_ICON_DATA_1))).thenReturn(expected1);
        Glyph glyph1 = tested.getGlyph(CustomTask.class, DEF_ID_1);
        assertEquals(expected1, glyph1);
    }

    @Test
    public void testGetGlyphCustomTaskNoWID() {
        Glyph expected2 = mock(Glyph.class);

        when(iconDataGlyphGenerator.apply(eq(WID_ICON_DATA_2))).thenReturn(expected2);
        Glyph glyph2 = tested.getGlyph(CustomTask.class, DEF_ID_2);
        assertEquals(expected2, glyph2);
    }

    @Test
    public void testGetGlyphCustomTaskNoName() {
        Glyph expected3 = mock(Glyph.class);

        when(iconDataGlyphGenerator.apply(eq(WID_ICON_DATA_3))).thenReturn(expected3);
        Glyph glyph3 = tested.getGlyph(CustomTask.class, DEF_ID_3);
        assertEquals(expected3, glyph3);
    }
}
