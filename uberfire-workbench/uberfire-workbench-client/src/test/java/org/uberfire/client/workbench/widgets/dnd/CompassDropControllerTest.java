package org.uberfire.client.workbench.widgets.dnd;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class CompassDropControllerTest {

    private CompassDropControllerUnitTestWrapper compassDropController;

    @GwtMock
    private WorkbenchPanelView view;

    @GwtMock
    private WorkbenchDragAndDropManager dndManager;
    @GwtMock
    private PanelManager panelManager;

    @Before
    public void setup() {
        compassDropController = new CompassDropControllerUnitTestWrapper();
        compassDropController.setupMocks( dndManager, panelManager );
    }

    @Test
    public void setupTest() {
        compassDropController.setup( view );
        assertEquals( view, compassDropController.dropTarget );
    }

    @Test
    public void compassDelegationTest() {
        CompassWidget compass = compassDropController.mock;

        DragContext dragContext = mock( DragContext.class );

        compassDropController.onEnter( dragContext );
        verify( compass ).onEnter( dragContext );

        compassDropController.onLeave( dragContext );
        verify( compass ).onLeave( dragContext );

        compassDropController.onMove( dragContext );
        verify( compass ).onMove( dragContext );

    }

    @Test
    public void onDropDoesNothing() {
        //Mock If not dropTarget has been identified do nothing
        compassDropController.mockDropTargetPositionNone();

        DragContext dragContext = mock( DragContext.class );
        compassDropController.onDrop( dragContext );

        verify( compassDropController.mock,never() ).onDrop( dragContext );
    }

    @Test
    public void onDropNoEffect() {
        compassDropController.mockDropTargetPosition( CompassPosition.SELF );

        compassDropController.mockSamePositionDrag(view);

        DragContext dragContext = mock( DragContext.class );

        compassDropController.onDrop( dragContext );

        verify( panelManager, never()  ).addWorkbenchPanel( any( PanelDefinition.class), any(Position.class), any(Integer.class) , any(Integer.class), any(Integer.class), any(Integer.class));

    }

    @Test
    public void onDropHappens() {
        compassDropController.mockDropTargetPosition( CompassPosition.WEST );

        compassDropController.mockSamePositionDrag(view);

        DragContext dragContext = mock( DragContext.class );

        compassDropController.onDrop( dragContext );

        verify( panelManager ).addWorkbenchPanel( any( PanelDefinition.class), any(Position.class), any(Integer.class) , any(Integer.class), any(Integer.class), any(Integer.class));

        verify( panelManager ).addWorkbenchPart( any( PlaceRequest.class), any(PartDefinition.class), any(PanelDefinition.class) , any(Menus.class), any(UIPart.class), any(String.class), isNull( Integer.class ), isNull( Integer.class ) );

    }

}
