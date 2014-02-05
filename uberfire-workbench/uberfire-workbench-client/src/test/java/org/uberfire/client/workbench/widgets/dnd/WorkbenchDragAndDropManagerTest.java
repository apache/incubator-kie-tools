package org.uberfire.client.workbench.widgets.dnd;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.panels.WorkbenchPanelView;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchDragAndDropManagerTest {

    private WorkbenchDragAndDropManagerUnitTestWrapper wrapper;

    @GwtMock
    private WorkbenchPanelView workbenchPanelView;

    @GwtMock
    private WorkbenchPickupDragController dragController;

    @GwtMock
    private DropController dropController;

    @GwtMock
    private BeanFactory factory;

    @Before
    public void setup() {
        wrapper = new WorkbenchDragAndDropManagerUnitTestWrapper();
        wrapper.setupMocks( dragController, factory );
    }

    @Test
    public void assertMakeDraggableDelegation() {
        wrapper.makeDraggable( mock( IsWidget.class ), mock( IsWidget.class ) );
        verify( dragController ).makeDraggable( any( Widget.class ), any( Widget.class ) );
    }

    @Test
    public void registerAndUnregisterDropController() {
        wrapper.registerDropController( workbenchPanelView, dropController );
        assertEquals( wrapper.dropControllerMap.get( workbenchPanelView ), dropController );
        verify( dragController ).registerDropController( dropController );

        wrapper.unregisterDropController( workbenchPanelView );
        verify( dragController ).unregisterDropController( dropController );
        verify( factory ).destroy( dropController );
    }

    @Test
    public void registerAndUnregisterDropControllers() {
        wrapper.registerDropController( workbenchPanelView, dropController );
        assertEquals( wrapper.dropControllerMap.get( workbenchPanelView ), dropController );

        WorkbenchPanelView workbenchPanelView2 = GWT.create( WorkbenchPanelView.class );
        DropController dropController2 = GWT.create( DropController.class );

        wrapper.registerDropController( workbenchPanelView2, dropController2 );
        assertEquals( wrapper.dropControllerMap.get( workbenchPanelView2 ), dropController2 );

        wrapper.unregisterDropControllers();
        verify( factory, Mockito.times(1 )).destroy( dropController );
        verify( factory, Mockito.times(1 )).destroy( dropController2 );
        verify( dragController ).unregisterDropController( dropController );
        verify( dragController ).unregisterDropController( dropController2 );
        assertTrue(wrapper.dropControllerMap.isEmpty());

    }
}
