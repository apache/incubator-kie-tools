package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.RequiresResizeFlowPanel;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelViewTest {

    private SimpleWorkbenchPanelViewUnitTestWrapper view;

    private SimpleWorkbenchPanelPresenter presenter;

    @GwtMock
    private PanelManager panelManager;

    @GwtMock
    private ListBarWidget listBar;

    @GwtMock
    private RequiresResizeFlowPanel container;

    @GwtMock
    private WorkbenchDragAndDropManager dndManager;

    @Before
    public void setup() {
        view = new SimpleWorkbenchPanelViewUnitTestWrapper();
        presenter = new SimpleWorkbenchPanelPresenter( view, panelManager, null, null ){

            public void onResize( final int width,
                                  final int height ) {
            }
        };
        view.setupMocks( listBar, container, dndManager, presenter );
    }

    @Test
    public void addPresenterOnInit() {
        view.init( presenter );
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void setupDragAndDrop() {
        view.setupDragAndDrop();
        verify( listBar ).setDndManager( eq( dndManager ) );
        verify( listBar ).setup( false, false );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addOnFocusHandler( any( Command.class ) );
    }

    @Test
    public void onResizeShouldOnlyResizeParentWhenWidthAndHeightAreZero() {

        final String width = "10";
        final String height = "100";

        view.setWidth( width );
        view.setHeight( height );

        view.onResize();

        verify( listBar, never () ).onResize();

    }


    @Test
    public void onResize() {

        view.changeWidgetSizeMock( 10, 10 );
        view.onResize();

        verify( listBar ).onResize();

    }


}
