package org.uberfire.client.workbench.panels.impl;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class StaticWorkbenchPanelViewTest {

    private StaticWorkbenchPanelViewUnitTestWrapper view;

    private StaticWorkbenchPanelPresenter presenter;

    @GwtMock
    private PanelManager panelManager;
    @GwtMock
    private PlaceManager placeManager;

    @Before
    public void setup() {
        view = new StaticWorkbenchPanelViewUnitTestWrapper();
        presenter = new StaticWorkbenchPanelPresenter( view, panelManager, null, null ){
            @Override
            public void onResize( final int width,
                                  final int height ) {

            }
        };
        view.setupMocks( presenter, placeManager );


    }

    @Test
    public void addPresenterOnInit() {
        view.init( presenter );
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void checkEventsOnInstantiating() {
        //events add
        //verify( view.getPanel() ).addFocusHandler(any(FocusHandler.class ));
        //verify( view.getPanel() ).addSelectionHandler( any( SelectionHandler.class ) );

        assertTrue(view.initWidgetCalled);

    }

    @Test
    public void addPartToPanelWhenPartViewIsNull() {

        view.mockPanelGetPartView( null );

        WorkbenchPartPresenter.View viewWbPartPresenter = mock( WorkbenchPartPresenter.View.class );
        view.addPart( viewWbPartPresenter );

        verify( view.getPanel() ).setPart( viewWbPartPresenter );
    }

    @Test
    public void addPart() {
        WorkbenchPartPresenter.View viewWbPartPresenter = mock( WorkbenchPartPresenter.View.class );
        view.mockPanelGetPartView( viewWbPartPresenter );

        view.addPart( viewWbPartPresenter );

        verify( placeManager).tryClosePlace( any( PlaceRequest.class ), any(Command.class) );
    }

    @Test
    public void removePart() {
        view.removePart( null );
        verify( view.getPanel() ).clear();
    }

    @Test
    public void onResize() {
        final int width = 42;
        final int height = 10;

        view.mockWidget(width, height);

        view.onResize();

        verify( view.getPanel() ).setPixelSize( width, height );

        assertTrue( view.resizeSuperCalled );

        assertTrue( view.setPixelSizeCalledRightParameters );
    }


}
