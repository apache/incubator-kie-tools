/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.dnd;

import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.Modal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.layout.editor.client.components.GridLayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.components.LayoutComponentView;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.dnd.mocks.DndDataJSONConverterMock;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.EditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.RowEditorWidget;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DropColumnPanelTest {

    public static final String SPAN = "12";

    private DropColumnPanel dropColumnPanel;
    private FlowPanel columnContainer;
    private GridLayoutDragComponent gridLayoutComponent;
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

        gridLayoutComponent = mock( GridLayoutDragComponent.class );
        when( gridLayoutComponent.getSpan() ).thenReturn( SPAN );
        when( gridLayoutComponent.getSettingsKeys() ).thenReturn( new String[]{GridLayoutDragComponent.SPAN} );
        when( gridLayoutComponent.getSettingValue( GridLayoutDragComponent.SPAN ) ).thenReturn( SPAN );
        when( gridLayoutComponent.getSpan() ).thenReturn( SPAN );

        layoutDragComponent = mock( LayoutDragComponent.class );

        modalDragComponent = mock( ModalDragComponent.class );
        componentConfigureModal = mock( Modal.class );

        when( modalDragComponent.getConfigurationModal( any( ModalConfigurationContext.class ) ) ).thenReturn( componentConfigureModal );
        columnContainer = mock( FlowPanel.class );
        ColumnEditorWidget columnEditorWidget = new ColumnEditorWidget( mock( RowEditorWidget.class ), columnContainer, "12" ) {
            @Override
            public EditorWidget getParent() {
                return new LayoutEditorWidget();
            }
        };
        dropColumnPanel = spy( new DropColumnPanel( columnEditorWidget ) );
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

        DndDataJSONConverterMock converter = new DndDataJSONConverterMock( );
        String data = converter.generateDragComponentJSON( gridLayoutComponent );

        when( event.getData( LayoutDragComponent.FORMAT ) ).thenReturn( data );

        dropColumnPanel.setConverter( converter );
        dropColumnPanel.dropHandler( event );
        verify( gridLayoutComponent, atLeastOnce() ).getSettingsKeys();
        verify( gridLayoutComponent ).getSettingValue( GridLayoutDragComponent.SPAN );

        verify( columnContainer ).remove( dropColumnPanel );
        //dropped view
        verify( columnContainer, times( 1 ) ).add( any( RowView.class ) );
    }

    @Test
    public void handleExternalLayoutDropComponent() {
        DropEvent event = mock( DropEvent.class );

        DndDataJSONConverterMock converter = new DndDataJSONConverterMock( );
        String data = converter.generateDragComponentJSON( layoutDragComponent );

        when( event.getData( LayoutDragComponent.FORMAT ) ).thenReturn( data );

        dropColumnPanel.setConverter( converter );
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

        DndDataJSONConverterMock converter = new DndDataJSONConverterMock( );
        String data = converter.generateDragComponentJSON( modalDragComponent );

        when( event.getData( LayoutDragComponent.FORMAT ) ).thenReturn( data );

        dropColumnPanel.setConverter( converter );
        dropColumnPanel.dropHandler( event );
        verify( columnContainer ).remove( dropColumnPanel );
        //dropped view
        verify( columnContainer, times( 1 ) ).add( any( LayoutComponentView.class ) );
        //show configure modal
        verify( componentConfigureModal, times( 1 ) ).show();
    }
}
