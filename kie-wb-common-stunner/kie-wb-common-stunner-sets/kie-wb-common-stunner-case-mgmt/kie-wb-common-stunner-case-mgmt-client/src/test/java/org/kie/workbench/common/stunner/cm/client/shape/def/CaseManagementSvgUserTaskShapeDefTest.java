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
import org.kie.workbench.common.stunner.cm.definition.UserTask;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementSvgUserTaskShapeDefTest {

    private CaseManagementSvgUserTaskShapeDef tested = new CaseManagementSvgUserTaskShapeDef();

    private CaseManagementSVGViewFactory factory = mock(CaseManagementSVGViewFactory.class);

    @Before
    public void setUp() throws Exception {
        SVGShapeViewResource task = mock(SVGShapeViewResource.class);
        when(factory.task()).thenReturn(task);
    }

    @Test
    public void testNewViewInstance() throws Exception {
        tested.newViewInstance(factory, new UserTask());

        verify(factory, times(1)).task();
    }

    @Test
    public void testGetGlyph() throws Exception {
        Glyph glyph = tested.getGlyph(UserTask.class, "");

        assertEquals(CaseManagementSVGGlyphFactory.TASK_GLYPH, glyph);
    }
}