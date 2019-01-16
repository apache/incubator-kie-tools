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
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGGlyphFactory;
import org.kie.workbench.common.stunner.cm.client.resources.CaseManagementSVGViewFactory;
import org.kie.workbench.common.stunner.cm.definition.CaseReusableSubprocess;
import org.kie.workbench.common.stunner.cm.definition.ProcessReusableSubprocess;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementSvgSubprocessShapeDefTest {

    private CaseManagementSvgSubprocessShapeDef tested;

    private CaseManagementSVGViewFactory factory = mock(CaseManagementSVGViewFactory.class);

    @Before
    public void setUp() throws Exception {
        tested = new CaseManagementSvgSubprocessShapeDef();

        SVGShapeViewResource subcase = mock(SVGShapeViewResource.class);
        when(factory.subcase()).thenReturn(subcase);

        SVGShapeViewResource subprocess = mock(SVGShapeViewResource.class);
        when(factory.subprocess()).thenReturn(subprocess);
    }

    @Test
    public void testNewViewInstance() throws Exception {
        tested.newViewInstance(factory, new CaseReusableSubprocess());
        verify(factory, times(1)).subcase();

        tested.newViewInstance(factory, new ProcessReusableSubprocess());
        verify(factory, times(1)).subprocess();
    }

    @Test
    public void testGetGlyph() throws Exception {
        Glyph subcaseGlyph = tested.getGlyph(CaseReusableSubprocess.class, "");
        assertEquals(CaseManagementSVGGlyphFactory.SUBCASE_GLYPH, subcaseGlyph);

        Glyph subprocessGlyph = tested.getGlyph(ProcessReusableSubprocess.class, "");
        assertEquals(CaseManagementSVGGlyphFactory.SUBPROCESS_GLYPH, subprocessGlyph);
    }
}