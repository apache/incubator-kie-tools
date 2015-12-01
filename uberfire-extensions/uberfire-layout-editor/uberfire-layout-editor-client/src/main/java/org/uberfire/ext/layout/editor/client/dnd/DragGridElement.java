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
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;

public class DragGridElement extends Composite {

    private DndDataJSONConverter converter = new DndDataJSONConverter();

    private LayoutDragComponent type;

    @UiField
    InputGroup move;

    public DragGridElement( LayoutDragComponent type ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.type = type;
        build();
    }

    private void build() {
        createComponentWidget();
        createMoveIcon( type );
    }

    private void createMoveIcon( final LayoutDragComponent type ) {
        move.setTitle( CommonConstants.INSTANCE.DragAndDrop() );
        move.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                createDragStart( event, type );
            }
        }, DragStartEvent.getType() );

        move.getElement().setDraggable( Element.DRAGGABLE_TRUE );
    }

    void createDragStart( DragStartEvent event,
                          LayoutDragComponent type ) {

        event.setData( LayoutDragComponent.FORMAT,  converter.generateDragComponentJSON( type ) );

        event.getDataTransfer().setDragImage( move.getElement(), 10, 10 );
    }

    private void createComponentWidget() {
        move.add( type.getDragWidget() );
    }

    interface MyUiBinder extends UiBinder<Widget, DragGridElement> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

    public void setConverter( DndDataJSONConverter converter ) {
        this.converter = converter;
    }
}
