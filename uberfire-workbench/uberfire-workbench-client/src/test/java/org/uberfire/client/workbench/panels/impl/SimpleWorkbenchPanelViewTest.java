package org.uberfire.client.workbench.panels.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.MaximizeToggleButtonPresenter;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.listbar.ListBarWidget;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleWorkbenchPanelViewTest extends AbstractDockingWorkbenchPanelViewTest {

    @InjectMocks
    private SimpleWorkbenchPanelViewUnitTestWrapper view;

    @Mock PanelManager panelManager;
    @Mock ListBarWidget listBar;

    @Mock Element listBarElement;
    @Mock Style listBarElementStyle;
    @Mock MaximizeToggleButtonPresenter maximizeButton;

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        Widget listBarWidget = mock( Widget.class );
        when( listBar.asWidget() ).thenReturn( listBarWidget );
        when( listBarWidget.getElement() ).thenReturn( listBarElement );
        when( listBarElement.getStyle() ).thenReturn( listBarElementStyle );

        when( listBar.getMaximizeButton() ).thenReturn( maximizeButton );

        final AtomicLong parts = new AtomicLong();
        doAnswer( new Answer() {
            @Override
            public Object answer( InvocationOnMock invocation ) throws Throwable {
                parts.incrementAndGet();
                return null;
            }
        } ).when( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );

        doAnswer( new Answer() {
            @Override
            public Boolean answer( InvocationOnMock invocation ) throws Throwable {
                parts.decrementAndGet();
                return true;
            }
        } ).when( listBar ).remove( any( PartDefinition.class ) );

        when( listBar.getPartsSize() ).thenAnswer( new Answer<Integer>() {
            @Override
            public Integer answer( InvocationOnMock invocation ) throws Throwable {
                return parts.intValue();
            }
        } );

        presenter = mock( SimpleWorkbenchPanelPresenter.class );

        view.setup(); // PostConstruct
        view.init( presenter );
    }

    @Override
    protected AbstractDockingWorkbenchPanelView<?> getViewToTest() {
        return view;
    }

    @Test
    public void shouldAddPresenterOnInit() {
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void shouldSetupDragAndDropOnListBar() {
        verify( listBar ).setDndManager( eq( dndManager ) );
        verify( listBar ).disableDnd();
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addSelectionHandler( any( SelectionHandler.class ) );
        verify( listBar ).addOnFocusHandler( any( Command.class ) );
    }

    @Test
    public void shouldPropagateResizeWhenAttached() {

        view.forceAttachedState( true );
        view.setPixelSize( 10, 10 );
        view.onResize();

        // unfortunately, setPixelSize() doesn't have any side effects during unit tests so we can't verify the arguments
        verify( presenter ).onResize( any( Integer.class ), any( Integer.class ) );

        verify( topLevelWidget ).onResize();
    }

    @Test
    public void shouldNotPropagateResizeWhenNotAttached() {

        view.forceAttachedState( false );
        view.setPixelSize( 10, 10 );
        view.onResize();

        // unfortunately, setPixelSize() doesn't have any side effects during unit tests so we can't verify the arguments
        verify( presenter, never() ).onResize( any( Integer.class ), any( Integer.class ) );

        verify( topLevelWidget ).onResize();
    }

    @Test
    public void shouldOnlyHaveOnePart() {
        assertEquals( 0, listBar.getPartsSize() );
        verify( listBar ).disableClosePart();

        //Add first part
        view.addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );
        assertEquals( 1, listBar.getPartsSize() );

        //Second part will be ignored
        view.addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar ).addPart( any( WorkbenchPartPresenter.View.class ) );
        assertEquals( 1, listBar.getPartsSize() );

        //Remove part
        view.removePart( mock( PartDefinition.class ) );
        assertEquals( 0, listBar.getPartsSize() );

        //Add part again
        view.addPart( mock( WorkbenchPartPresenter.View.class ) );
        verify( listBar, times( 2 ) ).addPart( any( WorkbenchPartPresenter.View.class ) );
        assertEquals( 1, listBar.getPartsSize() );
    }

}