/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.widgets.dnd;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

import static org.mockito.Mockito.*;

public class CompassDropControllerUnitTestWrapper extends CompassDropController {

    WorkbenchDragContext workDragContextMock;
    PanelDefinition positionMock;

    public void setupMocks(WorkbenchDragAndDropManager dndManager,
                           PanelManager panelManager) {
        this.dndManager = dndManager;
        this.panelManager = panelManager;
        workDragContextMock = mock(WorkbenchDragContext.class);

        when(dndManager.getWorkbenchContext()).thenReturn(workDragContextMock);
        this.compass = mock(CompassWidget.class);
    }

    @Override
    void firePartDroppedEvent(PlaceRequest place) {

    }

    public void mockDropTargetPositionNone() {
        when(this.compass.getDropPosition()).thenReturn(CompassPosition.NONE);
    }

    public void mockDropTargetPosition(Position position) {
        when(this.compass.getDropPosition()).thenReturn(position);
    }

    public void mockSamePositionDrag(WorkbenchPanelView dropTarget) {
        this.dropTarget = dropTarget;
        positionMock = mock(PanelDefinition.class);

        when(workDragContextMock.getSourcePanel()).thenReturn(positionMock);
        when(dropTarget.getPresenter()).thenReturn(mock(WorkbenchPanelPresenter.class));
        WorkbenchPanelPresenter presenter = dropTarget.getPresenter();
        when(presenter.getDefinition()).thenReturn(positionMock);
    }
}
