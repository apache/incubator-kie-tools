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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext.WiresShapeViewExt;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.FontHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class BPMNViewHandlersTest {

    @Mock
    private WiresShapeViewExt view;

    @Test
    @SuppressWarnings("unchecked")
    public void testFontHandler() {
        final FontHandler<BPMNViewDefinition, ShapeView> fontHandler =
                new BPMNShapeViewHandlers.FontHandlerBuilder<>().build();
        final StartNoneEvent bean = new StartNoneEvent.StartNoneEventBuilder().build();
        bean.getFontSet().getFontColor().setValue("fontColor");
        bean.getFontSet().getFontFamily().setValue("fontFamily");
        bean.getFontSet().getFontSize().setValue(12d);
        bean.getFontSet().getFontBorderColor().setValue("borderColor");
        bean.getFontSet().getFontBorderSize().setValue(8d);
        fontHandler.handle(bean, view);
        verify(view, times(1)).setTitleFontColor(eq("fontColor"));
        verify(view, times(1)).setTitleFontFamily(eq("fontFamily"));
        verify(view, times(1)).setTitleFontSize(eq(12d));
        verify(view, times(1)).setTitleStrokeColor(eq("borderColor"));
        verify(view, times(1)).setTitleStrokeWidth(eq(8d));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testViewHandler() {
        final ViewAttributesHandler<BPMNViewDefinition, ShapeView> fontHandler =
                new BPMNShapeViewHandlers.ViewAttributesHandlerBuilder<>().build();
        final StartNoneEvent bean = new StartNoneEvent.StartNoneEventBuilder().build();
        bean.getBackgroundSet().getBgColor().setValue("bgColor");
        bean.getBackgroundSet().getBorderColor().setValue("borderColor");
        bean.getBackgroundSet().getBorderSize().setValue(5d);
        fontHandler.handle(bean, view);
        verify(view, times(1))
                .setFillGradient(any(HasFillGradient.Type.class),
                                 eq("bgColor"),
                                 anyString());
        verify(view, times(1)).setStrokeColor(eq("borderColor"));
        verify(view, times(1)).setStrokeWidth(eq(5d));
    }
}
