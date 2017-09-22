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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(LienzoMockitoTestRunner.class)
public class CaseManagementShapeViewFactoryTest {

    private CaseManagementShapeViewFactory tested;

    @Before
    public void setup() {
        this.tested = new CaseManagementShapeViewFactory();
    }

    @Test
    public void testBuildViews() {
        final NullView nullView = tested.newNullView();
        assertNotNull(nullView);
        final ActivityView activityView = tested.newActivityView(100,
                                                                 200);
        assertNotNull(nullView);
        assertEquals(100,
                     activityView.getWidth(),
                     0);
        assertEquals(200,
                     activityView.getHeight(),
                     0);
        final StageView stageView = tested.newStageView(100,
                                                        200,
                                                        25);
        assertNotNull(stageView);
        assertEquals(100,
                     stageView.getWidth(),
                     0);
        assertEquals(200,
                     stageView.getHeight(),
                     0);
        final DiagramView diagramView = tested.newDiagramView(100,
                                                              200);
        assertNotNull(diagramView);
        assertEquals(100,
                     diagramView.getWidth(),
                     0);
        assertEquals(200,
                     diagramView.getHeight(),
                     0);
    }
}
