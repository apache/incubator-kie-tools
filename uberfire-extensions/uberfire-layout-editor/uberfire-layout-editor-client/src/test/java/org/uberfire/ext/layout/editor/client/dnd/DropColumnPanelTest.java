package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Modal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.components.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.components.InternalDragComponent;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidget;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DropColumnPanelTest {

    private DropColumnPanel dropColumnPanel;
    private FlowPanel columnContainer;
    private LayoutDragComponent layoutDragComponent;
    private ModalDragComponent modalDragComponent;
    private Modal componentConfigureModal;

    class ModalDragComponent implements LayoutDragComponent,
                                        HasModalConfiguration {

        @Override
        public Modal getConfigurationModal( ModalConfigurationContext ctx ) {
            return componentConfigureModal;
        }

        @Override
        public IsWidget getDragWidget() {
            return null;
        }

        @Override
        public IsWidget getPreviewWidget( RenderingContext ctx ) {
            return null;
        }

        @Override
        public IsWidget getShowWidget( RenderingContext ctx ) {
            return null;
        }
    }

    @Before
    public void setup() {
        modalDragComponent = mock( ModalDragComponent.class );
        layoutDragComponent = mock( LayoutDragComponent.class );
        componentConfigureModal = mock( Modal.class );

        when( modalDragComponent.getConfigurationModal( any( ModalConfigurationContext.class ) ) ).thenReturn( componentConfigureModal );
        columnContainer = mock( FlowPanel.class );
        ColumnEditorWidget columnEditorWidget = new ColumnEditorWidget( mock( RowEditorWidget.class ), columnContainer, "12" ) {
            @Override
            public EditorWidget getParent() {
                return new LayoutEditorWidget();
            }
        };
        dropColumnPanel = spy( new DropColumnPanel( columnEditorWidget ) {
            @Override
            LayoutDragComponent getLayoutDragComponent( String dragTypeClassName ) {
                if ( ModalDragComponent.class.getName().equals( dragTypeClassName ) ) {
                    return modalDragComponent;
                }
                return layoutDragComponent;
            }
        } );
    }

    @Test
    public void onDragOverShouldCreateABorderAndDragLeaveShouldRemoveTheBorder() {
        dropColumnPanel.dragOverHandler();
        verify( dropColumnPanel ).removeCSSClass( WebAppResource.INSTANCE.CSS().dropInactive() );
        verify( dropColumnPanel ).addCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
        dropColumnPanel.dragLeaveHandler();
        verify( dropColumnPanel ).removeCSSClass( WebAppResource.INSTANCE.CSS().dropBorder() );
        verify( dropColumnPanel ).addCSSClass( WebAppResource.INSTANCE.CSS().dropInactive() );
    }

    @Test
    public void dropHandlerOfAGridTest() {
        DropEvent event = mock( DropEvent.class );
        when( event.getData( InternalDragComponent.INTERNAL_DRAG_COMPONENT ) ).thenReturn( "12" );
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
    }

    @Test
    public void handleExternalLayoutDropComponentWithConfigureModal() {
        DropEvent event = mock( DropEvent.class );
        when( event.getData( LayoutDragComponent.class.toString() ) ).thenReturn( ModalDragComponent.class.getName() );

        dropColumnPanel.dropHandler( event );
        verify( columnContainer ).remove( dropColumnPanel );
        //dropped view
        verify( columnContainer, times( 1 ) ).add( any( LayoutComponentView.class ) );
        //show configure modal
        verify( componentConfigureModal, times( 1 ) ).show();
    }
}