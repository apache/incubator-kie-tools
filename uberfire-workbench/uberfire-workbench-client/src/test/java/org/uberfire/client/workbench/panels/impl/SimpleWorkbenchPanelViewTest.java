package org.uberfire.client.workbench.panels.impl;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class SimpleWorkbenchPanelViewTest extends AbstractSimpleWorkbenchPanelViewTest {

    @InjectMocks
    private SimpleWorkbenchPanelViewUnitTestWrapper view;

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private SimpleWorkbenchPanelPresenter presenter;

    @Before
    public void setup() {
        super.setup();

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
    public void shouldDisableCloseParts() {
        verify( listBar ).disableClosePart();
        verify( listBar, never() ).enableClosePart();
    }

}