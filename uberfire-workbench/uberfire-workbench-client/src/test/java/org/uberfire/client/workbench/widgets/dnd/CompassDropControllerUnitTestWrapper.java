package org.uberfire.client.workbench.widgets.dnd;

import static org.mockito.Mockito.*;

import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.Position;

public class CompassDropControllerUnitTestWrapper extends CompassDropController {

    CompassWidget mock;
    WorkbenchDragContext workDragContextMock;
    PanelDefinition positionMock;

    public void setupMocks( WorkbenchDragAndDropManager dndManager, PanelManager panelManager ) {
        this.dndManager = dndManager;
        this.panelManager = panelManager;
        workDragContextMock = mock( WorkbenchDragContext.class );

        when( dndManager.getWorkbenchContext() ).thenReturn( workDragContextMock );
    }

    @Override
    void firePartDroppedEvent( PlaceRequest place ) {

    }

    @Override
    CompassWidget getCompassWidgetInstance() {
        if ( mock == null ) {
            mock = mock( CompassWidget.class );
        }
        return mock;
    }

    public void mockDropTargetPositionNone() {
        when( mock.getDropPosition() ).thenReturn( CompassPosition.NONE );
    }

    public void mockDropTargetPosition(Position position) {
        when( mock.getDropPosition() ).thenReturn( position);
    }

    public void mockSamePositionDrag( WorkbenchPanelView dropTarget ) {
        this.dropTarget = dropTarget;
        positionMock = mock( PanelDefinition.class );

        when( workDragContextMock.getSourcePanel() ).thenReturn( positionMock );
        when( dropTarget.getPresenter() ).thenReturn( mock( WorkbenchPanelPresenter.class ) );
        WorkbenchPanelPresenter presenter = dropTarget.getPresenter();
        when( presenter.getDefinition() ).thenReturn( positionMock );

    }
}
