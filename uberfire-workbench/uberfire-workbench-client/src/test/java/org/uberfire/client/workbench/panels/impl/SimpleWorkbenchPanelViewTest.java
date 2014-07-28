package org.uberfire.client.workbench.panels.impl;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.widgets.dnd.WorkbenchDragAndDropManager;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.client.workbench.widgets.panel.RequiresResizeFlowPanel;
import org.uberfire.mvp.Command;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelViewTest {

    @InjectMocks
    private SimpleWorkbenchPanelView view;

    @Mock
    private PanelManager panelManager;

    @Mock
    private ListBarWidget listBar;
    private final Element listBarElement = mock( Element.class );
    private final Style listBarElementStyle = mock( Style.class );

    @Mock(answer=Answers.RETURNS_MOCKS)
    private RequiresResizeFlowPanel container;

    @Mock
    private WorkbenchDragAndDropManager dndManager;

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        when( listBar.asWidget() ).thenReturn( listBar );
        when( listBar.getElement() ).thenReturn( listBarElement );
        when( listBarElement.getStyle() ).thenReturn( listBarElementStyle );

        presenter = mock( SimpleWorkbenchPanelPresenter.class );

        view.setup(); // this is the PostConstruct method
        view.init( presenter );
    }

    @Test
    public void shouldAddPresenterOnInit() {
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void shouldSetupDragAndDropOnListBar() {
        verify( listBar ).setDndManager( eq( dndManager ) );
        verify( listBar ).setup( false, false );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addOnFocusHandler( any( Command.class ) );
    }

    @Test
    public void onResize() {

        view.setPixelSize( 10, 10 );
        view.onResize();

        // unfortunately, setPixelSize() doesn't have any side effects during unit tests so we can't verify the arguments
        verify( presenter ).onResize( any( Integer.class ), any( Integer.class ) );

        verify( container ).onResize();
    }


}
