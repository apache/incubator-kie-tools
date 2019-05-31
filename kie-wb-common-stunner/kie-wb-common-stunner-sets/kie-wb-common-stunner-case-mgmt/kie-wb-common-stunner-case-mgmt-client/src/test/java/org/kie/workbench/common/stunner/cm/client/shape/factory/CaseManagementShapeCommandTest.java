/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.shape.CaseManagementShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSvgShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.CaseManagementShapeView;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeCommandTest {

    private CaseManagementSvgShapeDef shapeDef;

    private CaseManagementDiagram def;

    @Mock
    private CaseManagementShapeView shapeView;

    @Before
    public void setUp() {
        def = new CaseManagementDiagram();
        shapeDef = spy(new CaseManagementSvgDiagramShapeDef());
    }

    @Test
    public void create() {
        CaseManagementShape shape = CaseManagementShapeCommand.create(def, shapeView, shapeDef);
        verify(shapeDef).fontHandler();
        verify(shapeView).setTitlePosition(HasTitle.VerticalAlignment.MIDDLE,
                                           HasTitle.HorizontalAlignment.CENTER,
                                           HasTitle.ReferencePosition.INSIDE,
                                           HasTitle.Orientation.HORIZONTAL);
        verify(shapeView).setTextSizeConstraints(new HasTitle.Size(100, 100,
                                                                   HasTitle.Size.SizeType.PERCENTAGE));
    }

    @Test
    public void create_noShape() {
        CaseManagementShapeCommand.create(new Object(), shapeView, shapeDef);

        verify(shapeDef, never()).fontHandler();
        verify(shapeView, never()).setTitlePosition(any(HasTitle.VerticalAlignment.class),
                                                    any(HasTitle.HorizontalAlignment.class),
                                                    any(HasTitle.ReferencePosition.class),
                                                    any(HasTitle.Orientation.class));
        verify(shapeView, never()).setTextSizeConstraints(any(HasTitle.Size.class));
    }
}