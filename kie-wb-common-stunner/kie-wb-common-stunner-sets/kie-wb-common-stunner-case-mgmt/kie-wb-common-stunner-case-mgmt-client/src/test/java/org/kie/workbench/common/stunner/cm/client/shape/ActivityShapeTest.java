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
package org.kie.workbench.common.stunner.cm.client.shape;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;
import org.mockito.Mock;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ActivityShapeTest {

    @Mock
    private ActivityView shapeView;

    @Mock
    private CaseManagementTaskShapeDef shapeDef;

    @Mock
    private Node node;

    @Mock
    private View nodeView;

    @Mock
    private BusinessRuleTask nodeDef;

    @Mock
    private PictureShapeView iconView;

    private ActivityShape shape;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        this.shape = new ActivityShape(shapeDef,
                                       iconView,
                                       shapeView);
        when(node.getContent()).thenReturn(nodeView);
        when(nodeView.getDefinition()).thenReturn(nodeDef);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void checkSizeIsDefault() {
        shape.applyProperties(node,
                              MutationContext.STATIC);

        verify(shapeView).setSize(eq(BaseTask.BaseTaskBuilder.WIDTH),
                                  eq(BaseTask.BaseTaskBuilder.HEIGHT));
    }
}
