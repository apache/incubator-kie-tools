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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.Label;
import org.uberfire.ext.layout.editor.client.components.*;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;
import org.uberfire.ext.layout.editor.client.row.RowView;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;

public class DropColumnPanel extends FlowPanel {

    private DndDataJSONConverter converter = new DndDataJSONConverter();

    private final ColumnEditorWidget parent;

    public DropColumnPanel( final ColumnEditorWidget parent ) {
        super();
        this.parent = parent;

        Label label = GWT.create(Label.class);
        label.setText( CommonConstants.INSTANCE.Column());
        this.add(label);

        addCSSClass(WebAppResource.INSTANCE.CSS().dropInactive());
        addDragOverHandler( new DragOverHandler() {
            @Override
            public void onDragOver( DragOverEvent event ) {
                dragOverHandler();
            }
        } );
        addDragLeaveHandler( new DragLeaveHandler() {
            @Override
            public void onDragLeave( DragLeaveEvent event ) {
                dragLeaveHandler();
            }
        } );
        addDropHandler( new DropHandler() {
            @Override
            public void onDrop( DropEvent event ) {
                dropHandler( event );
            }
        } );
    }

    void dropHandler( DropEvent event ) {
        event.preventDefault();

        LayoutDragComponent component = converter.readJSONDragComponent( event.getData( LayoutDragComponent.FORMAT ) );

        if ( component instanceof GridLayoutDragComponent ) {
            handleGridDrop( ((GridLayoutDragComponent) component).getSpan() );
        } else {
            handleExternalLayoutDragComponent( component );
        }

        dragLeaveHandler();
    }

    private void handleExternalLayoutDragComponent( LayoutDragComponent layoutDragComponent ) {
        if ( layoutDragComponent != null ) {
            if ( layoutDragComponent instanceof HasOnDropNotification ) {
                ( ( HasOnDropNotification ) layoutDragComponent ).onDropComponent();
            }
            handleLayoutDrop( layoutDragComponent );
        }
    }

    void dragOverHandler() {
        removeCSSClass(WebAppResource.INSTANCE.CSS().dropInactive());
        addCSSClass(WebAppResource.INSTANCE.CSS().dropBorder());
    }

    void addCSSClass( String className ) {
        getElement().addClassName( className );
    }

    void dragLeaveHandler() {
        removeCSSClass(WebAppResource.INSTANCE.CSS().dropBorder());
        addCSSClass(WebAppResource.INSTANCE.CSS().dropInactive());
    }

    void removeCSSClass( String className ) {
        getElement().removeClassName( className );
    }

    private void handleLayoutDrop( LayoutDragComponent layoutDragComponent ) {
        parent.getWidget().remove( this );
        parent.getWidget().add( new LayoutComponentView( parent, layoutDragComponent, true ) );
    }

    private void handleGridDrop( String grid ) {
        parent.getWidget().remove( this );
        parent.getWidget().add( new RowView( parent, grid, this ) );
    }

    private HandlerRegistration addDropHandler( DropHandler handler ) {
        return addBitlessDomHandler( handler, DropEvent.getType() );
    }

    private HandlerRegistration addDragOverHandler( DragOverHandler handler ) {
        return addBitlessDomHandler( handler, DragOverEvent.getType() );
    }

    private HandlerRegistration addDragLeaveHandler( DragLeaveHandler handler ) {
        return addBitlessDomHandler( handler, DragLeaveEvent.getType() );
    }

    public void setConverter( DndDataJSONConverter converter ) {
        this.converter = converter;
    }
}
