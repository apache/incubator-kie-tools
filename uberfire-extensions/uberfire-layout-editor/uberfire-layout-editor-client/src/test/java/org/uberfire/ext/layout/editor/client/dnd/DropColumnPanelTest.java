package org.uberfire.ext.layout.editor.client.dnd;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.config.ColumnSizeConfigurator;
import com.github.gwtbootstrap.client.ui.config.DefaultColumnSizeConfigurator;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.fakes.FakeProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorUI;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidgetUI;
import org.uberfire.ext.layout.editor.client.util.LayoutDragComponent;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DropColumnPanelTest {

    private DropColumnPanel dropColumnPanel;
    private FlowPanel columnContainer;
    private LayoutDragComponent layoutDragComponent;
    private Modal componentConfigureModal;

    @Before
    public void setup() {
        //Bootstrap Column need this hack (it doesn' allow GWT.CREATE (no default constructor)
        // and need's to register correct column size provider configurator (instead of GWT Mockito MOCK)
        GwtMockito.useProviderForType( ColumnSizeConfigurator.class, new FakeProvider() {
            @Override
            public Object getFake( Class aClass ) {
                return new DefaultColumnSizeConfigurator();
            }
        } );

        layoutDragComponent = mock( LayoutDragComponent.class );
        componentConfigureModal = mock( Modal.class );

        when( layoutDragComponent.getConfigureModal( any( EditorWidget.class ) ) ).thenReturn( componentConfigureModal );
        columnContainer = mock( FlowPanel.class );
        ColumnEditorUI columnEditorUI = new ColumnEditorUI( mock( RowEditorWidgetUI.class ), columnContainer, "12" );
        dropColumnPanel = spy( new DropColumnPanel( columnEditorUI ) {
            @Override
            LayoutDragComponent getLayoutDragComponent( String dragTypeClassName ) {
                return layoutDragComponent;
            }
        } );
    }

    @Test
    public void onDragOverShouldCreateABorderAndDragLeaveShouldRemoveTheBorder() {
        dropColumnPanel.dragOverHandler();
        verify( dropColumnPanel ).addCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
        dropColumnPanel.dragLeaveHandler();
        verify( dropColumnPanel ).removeCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
    }

    @Test
    public void dropHandlerOfAGridTest() {
        DropEvent event = mock( DropEvent.class );
        when( event.getData( LayoutDragComponent.INTERNAL_DRAG_COMPONENT ) ).thenReturn( "12" );
        dropColumnPanel.dropHandler( event );
        verify( columnContainer ).remove( dropColumnPanel );
        //dropped view
        verify( columnContainer, times( 1 ) ).add( any( RowView.class ) );
    }

    @Test
    public void handleExternalLayoutDropComponent() {
        DropEvent event = mock( DropEvent.class );
        when( event.getData( LayoutDragComponent.class.toString() ) ).thenReturn( "dragClass" );
        dropColumnPanel.dropHandler( event );
        verify( columnContainer ).remove( dropColumnPanel );
        //dropped view
        verify( columnContainer, times( 1 ) ).add( any( LayoutComponentView.class ) );
        //if component doesn't have a configure modal, should not be displayed
        verify( componentConfigureModal, never() ).show();

        when( layoutDragComponent.hasConfigureModal() ).thenReturn( true );
        dropColumnPanel.dropHandler( event );
        verify( componentConfigureModal, times( 1 ) ).show();
    }

    @Test
    public void handleExternalLayoutDropComponentWithConfigureModal() {
        DropEvent event = mock( DropEvent.class );
        when( event.getData( LayoutDragComponent.class.toString() ) ).thenReturn( "dragClass" );
        when( layoutDragComponent.hasConfigureModal() ).thenReturn( true );

        dropColumnPanel.dropHandler( event );
        verify( columnContainer ).remove( dropColumnPanel );
        //dropped view
        verify( columnContainer, times( 1 ) ).add( any( LayoutComponentView.class ) );
        //show configure modal
        verify( componentConfigureModal, times( 1 ) ).show();
    }

}