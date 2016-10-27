/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.client.screens;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientSessionManager;
import org.mockito.Mock;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class ProjectDiagramExplorerScreenTest {

    @Mock
    AbstractClientSessionManager clientSessionManager;
    @Mock
    TreeExplorer treeExplorer;
    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotificationEvent;
    @Mock
    Widget treeExplorerWidget;
    @Mock
    AbstractClientSession session;
    @Mock
    AbstractCanvasHandler canvasHandler;

    private ProjectDiagramExplorerScreen tested;

    @Before
    public void setup() throws Exception {
        when( treeExplorer.asWidget() ).thenReturn( treeExplorerWidget );
        when( session.getCanvasHandler() ).thenReturn( canvasHandler );
        this.tested = new ProjectDiagramExplorerScreen( clientSessionManager, treeExplorer, changeTitleNotificationEvent );
    }

    @Test
    public void testView() {
        assertEquals( treeExplorerWidget, tested.getWidget() );
    }

    @Test
    public void testShow() {
        tested.show( session );
        verify( treeExplorer, times( 1 ) ).show( eq( canvasHandler ) );
        verify( treeExplorer, times( 0 ) ).clear();
    }

    @Test
    public void testClose() {
        tested.close();
        verify( treeExplorer, times( 1 ) ).clear();
        verify( treeExplorer, times( 0 ) ).show( any( AbstractCanvasHandler.class ) );
    }

}
