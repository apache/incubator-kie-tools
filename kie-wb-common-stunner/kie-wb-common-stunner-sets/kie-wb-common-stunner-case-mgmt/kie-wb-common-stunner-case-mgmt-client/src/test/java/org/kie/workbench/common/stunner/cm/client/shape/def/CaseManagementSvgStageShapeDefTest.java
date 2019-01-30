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

package org.kie.workbench.common.stunner.cm.client.shape.def;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Height;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Width;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGGlyphFactory;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.cm.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementSvgStageShapeDefTest {

    private CaseManagementSvgStageShapeDef tested = new CaseManagementSvgStageShapeDef();

    private CaseManagementSVGViewFactory factory = mock(CaseManagementSVGViewFactory.class);

    @Before
    public void setup() {
        SVGShapeViewResource stage = mock(SVGShapeViewResource.class);
        when(factory.stage()).thenReturn(stage);
    }

    @Test
    public void testNewViewInstance() throws Exception {
        tested.newViewInstance(factory, new AdHocSubprocess());

        verify(factory, times(1)).stage();
    }

    @Test
    public void testNewViewInstance_zeroDimension() throws Exception {
        final Width width = new Width(0.0);
        final Height height = new Height(0.0);

        final RectangleDimensionsSet dimensionsSet = new RectangleDimensionsSet();
        dimensionsSet.setWidth(width);
        dimensionsSet.setHeight(height);

        final AdHocSubprocess adHocSubprocess = new AdHocSubprocess();
        adHocSubprocess.setDimensionsSet(dimensionsSet);

        tested.newViewInstance(factory, new AdHocSubprocess());

        verify(factory, times(1)).stage();
    }

    @Test
    public void testGetGlyph() throws Exception {
        Glyph glyph = tested.getGlyph(AdHocSubprocess.class, "");

        assertEquals(CaseManagementSVGGlyphFactory.STAGE_GLYPH, glyph);
    }
}