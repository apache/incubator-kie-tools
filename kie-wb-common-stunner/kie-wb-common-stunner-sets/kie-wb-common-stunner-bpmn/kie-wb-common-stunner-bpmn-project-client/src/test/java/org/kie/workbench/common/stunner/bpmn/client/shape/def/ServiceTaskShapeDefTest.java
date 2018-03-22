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

package org.kie.workbench.common.stunner.bpmn.client.shape.def;

import java.util.Collections;
import java.util.function.Function;

import com.ait.lienzo.client.core.image.ImageProxy;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.shape.view.handler.ServiceTaskShapeViewHandler;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ServiceTaskShapeDefTest {

    private static final String WID_ID = "id1";
    private static final String WID_ICON_DATA = "iconData1";
    private static final String DEF_ID = BindableAdapterUtils.getDynamicDefinitionId(ServiceTask.class,
                                                                                     WID_ID);
    private static final WorkItemDefinition DEF = new WorkItemDefinition()
            .setName(WID_ID)
            .setIconData(WID_ICON_DATA);

    private static final double WIDTH = 111d;
    private static final double HEIGHT = 121d;
    private static final BoundsImpl BOUNDS = BoundsImpl.build(0, 0, WIDTH, HEIGHT);

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

    private ServiceTaskShapeDef tested;
    private ServiceTask task;

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        this.task = new ServiceTask();
        task.getDimensionsSet().getWidth().setValue(WIDTH);
        task.getDimensionsSet().getHeight().setValue(HEIGHT);
        this.task.setName(WID_ID);
        when(node.getDefinition()).thenReturn(task);
        when(node.getBounds()).thenReturn(BOUNDS);
        when(registry.items()).thenReturn(Collections.singleton(DEF));
        when(registry.get(eq(WID_ID))).thenReturn(DEF);
        when(view.getChildren()).thenReturn(Collections.singletonList(imageSvgPrimitive));
        when(imageSvgPrimitive.getId()).thenReturn(ServiceTaskShapeViewHandler.WID_ICON_ID);
        when(imageSvgPrimitive.get()).thenReturn(imagePrimitive);
        when(imagePrimitive.getID()).thenReturn(ServiceTaskShapeViewHandler.WID_ICON_ID);
        when(imagePrimitive.getImageProxy()).thenReturn(imageProxy);
        this.tested = new ServiceTaskShapeDef(() -> registry,
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
        verify(imageProxy, times(1)).load(eq(WID_ICON_DATA));
    }

    @Test
    public void testGetGlyph() {
        Glyph expected = mock(Glyph.class);
        when(iconDataGlyphGenerator.apply(eq(WID_ICON_DATA))).thenReturn(expected);
        Glyph glyph = tested.getGlyph(ServiceTask.class,
                                      DEF_ID);
        assertEquals(expected, glyph);
    }
}
