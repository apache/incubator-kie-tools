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

package org.kie.workbench.common.stunner.bpmn.client.shape.view.handler;

import java.util.Collections;

import com.ait.lienzo.client.core.image.ImageProxy;
import com.ait.lienzo.client.core.shape.Attributes;
import com.ait.lienzo.client.core.shape.Picture;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.bpmn.workitem.IconDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.ServiceTask;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGPrimitive;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGShapeViewImpl;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ServiceTaskShapeViewHandlerTest {

    private static final String WID_ID = "id1";
    private static final String WID_ICON_DATA = "iconData1";
    private static final WorkItemDefinition DEF = new WorkItemDefinition()
            .setName(WID_ID)
            .setIconDefinition(new IconDefinition().setIconData(WID_ICON_DATA));
    public static final double HEIGHT = 10d;
    public static final double WIDTH = 10d;

    @Mock
    private WorkItemDefinitionRegistry registry;

    @Mock
    private SVGShapeViewImpl view;

    @Mock
    private SVGPrimitive imageSvgPrimitive;

    @Mock
    private Picture imagePrimitive;

    @Mock
    private ImageProxy imageProxy;

    private ServiceTaskShapeViewHandler tested;

    private ServiceTask task;

    @Mock
    private Attributes svgAttr;
    private Width width;
    private Height height;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        task = new ServiceTask();
        task.setName(WID_ID);
        task.setDimensionsSet(new RectangleDimensionsSet());
        width = spy(new Width(0d));
        height = spy(new Height(0d));
        task.getDimensionsSet().setWidth(width);
        task.getDimensionsSet().setHeight(height);

        when(registry.items()).thenReturn(Collections.singleton(DEF));
        when(registry.get(eq(WID_ID))).thenReturn(DEF);
        when(view.getChildren()).thenReturn(Collections.singletonList(imageSvgPrimitive));
        when(imageSvgPrimitive.getId()).thenReturn(ServiceTaskShapeViewHandler.WID_ICON_ID);
        when(imageSvgPrimitive.get()).thenReturn(imagePrimitive);
        when(imageSvgPrimitive.getPrimitiveId()).thenReturn(ServiceTaskShapeViewHandler.WID_ICON_ID);
        when(imagePrimitive.getID()).thenReturn(ServiceTaskShapeViewHandler.WID_ICON_ID);
        when(imagePrimitive.getImageProxy()).thenReturn(imageProxy);
        when(view.getPrimitive()).thenReturn(imageSvgPrimitive);
        when(imagePrimitive.getAttributes()).thenReturn(svgAttr);
        when(svgAttr.getHeight()).thenReturn(HEIGHT);
        when(svgAttr.getWidth()).thenReturn(WIDTH);

        this.tested = new ServiceTaskShapeViewHandler(() -> registry);
    }

    @Test
    public void testHandle() {
        when(imageProxy.isLoaded()).thenReturn(true);
        tested.handle(task, view);
        verify(imageProxy, times(1)).load(eq(WID_ICON_DATA));
        verify(width).setValue(WIDTH);
        verify(height).setValue(HEIGHT);
    }
}
