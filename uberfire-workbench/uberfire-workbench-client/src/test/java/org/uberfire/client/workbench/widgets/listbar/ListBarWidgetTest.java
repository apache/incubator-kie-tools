package org.uberfire.client.workbench.widgets.listbar;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.panels.WorkbenchPanelPresenter;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
    public class ListBarWidgetTest {

    private ListBarWidgetUnitTestWrapper widget;

    private FlowPanel menuArea;

    @GwtMock
    private Button closeButton;

    @GwtMock
    private Pair<PartDefinition, FlowPanel> currentPart;

    @GwtMock
    private WorkbenchPanelPresenter presenter;

    @GwtMock
    private PanelManager panelManager;

    @GwtMock
    private FocusPanel container;

    private FlowPanel contextMenu;

    private Button contextDisplay;

    private FlowPanel content;

    @GwtMock
    private SimplePanel title;

    @GwtMock
    private Map<PartDefinition, FlowPanel> partContentView;

    @GwtMock
    private LinkedHashSet<PartDefinition> parts;

    @Before
    public void setup() {
        //Workaround for gwt mock works (apparently doesnt allow to @gwtmock for same type and
        // init mocks only works on that way)
        MockitoAnnotations.initMocks( this );
        contextDisplay = GWT.create( Button.class );
        contextMenu = GWT.create( FlowPanel.class );
        menuArea = GWT.create( FlowPanel.class );
        content = GWT.create( FlowPanel.class );

        widget = new ListBarWidgetUnitTestWrapper().setupMocks( menuArea, closeButton, currentPart, presenter, panelManager, container, contextDisplay, contextMenu, title, content, parts );
    }

    @Test
    public void verifyNewInstanceCreationSequenceHappyCase() {
        //setup assertions
        assertTrue( widget.isDndEnabled() );
        assertTrue( widget.isMultiPart() );
        verify( closeButton ).addClickHandler( any( ClickHandler.class ) );
        verify( container ).addFocusHandler( any( FocusHandler.class ) );
        verify( contextDisplay ).removeFromParent();

    }

    @Test
    public void clearCallSequence() {

        widget.clear();
        verify( contextMenu ).clear();
        verify( menuArea ).setVisible( false );
        verify( title ).clear();
        verify( content ).clear();
        verify( parts ).clear();
        assertTrue( widget.isCustomListNull() );

    }

}
