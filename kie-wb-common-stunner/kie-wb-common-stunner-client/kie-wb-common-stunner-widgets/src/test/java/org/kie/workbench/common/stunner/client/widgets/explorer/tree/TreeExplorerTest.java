/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.explorer.tree;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasElementSelectedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.AbstractChildrenTraverseCallback;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TreeExplorerTest {

    @Mock
    ChildrenTraverseProcessor childrenTraverseProcessor;

    @Mock
    EventSourceMock<CanvasElementSelectedEvent> elementSelectedEvent;

    @Mock
    DefinitionUtils definitionUtils;

    @Mock
    ShapeManager shapeManager;

    @Mock
    TreeExplorerView view;

    @Mock
    Graph graph;

    @Mock
    Diagram diagram;

    @Mock
    CanvasHandler canvasHandler;

    private TreeExplorer testedTree;

    @Before
    public void setup() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(canvasHandler.getDiagram().getGraph()).thenReturn(graph);
        this.testedTree = new TreeExplorer(childrenTraverseProcessor,
                                           elementSelectedEvent,
                                           definitionUtils,
                                           shapeManager,
                                           view);
    }

    @Test
    public void testInit() {
        testedTree.init();
        verify(view,
               times(1)).init(eq(testedTree));
    }

    @Test
    public void testClear() {
        testedTree.clear();
        verify(view,
               times(1)).clear();
    }

    @Test
    public void testDestroy() {
        testedTree.destroy();
        verify(view,
               times(1)).destroy();
    }

    @Test
    public void testShow() {
        testedTree.show(canvasHandler);
        verify(childrenTraverseProcessor,
               times(1)).traverse(eq(graph),
                                  any(AbstractChildrenTraverseCallback.class));
    }
}
