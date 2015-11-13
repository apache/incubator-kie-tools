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
package org.uberfire.ext.wires.core.scratchpad.client.shapes.fixed;

import com.ait.lienzo.client.core.event.NodeDragEndEvent;
import com.ait.lienzo.client.core.event.NodeDragEndHandler;
import com.ait.lienzo.client.core.event.NodeDragMoveEvent;
import com.ait.lienzo.client.core.event.NodeDragMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.RootPanel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.wires.core.api.containers.ContainerManager;
import org.uberfire.ext.wires.core.api.containers.RequiresContainerManager;
import org.uberfire.ext.wires.core.api.containers.WiresContainer;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;

public class WiresFixedText extends WiresBaseShape implements RequiresContainerManager {

    private static final int BOUNDARY_SIZE = 10;

    //We do not hide the boundary item for Lines as it makes selecting them very difficult
    private static final double ALPHA_DESELECTED = 0.01;
    private static final double ALPHA_SELECTED = 0.1;

    private final Text text;
    private final Text bounding;
    private final TextBox editTextBox = new TextBox();

    private WiresContainer boundContainer;

    protected ContainerManager containerManager;

    public WiresFixedText( final Text shape ) {
        text = shape;
        text.setText( "Text" );

        bounding = new Text( text.getText(),
                             text.getFontFamily(),
                             text.getFontSize() );
        bounding.setTextBaseLine( text.getTextBaseLine() );
        bounding.setTextAlign( text.getTextAlign() );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( ALPHA_DESELECTED );

        add( text );
        add( bounding );

        //This class doesn't extend a super-class that handles Containers, so we add it manually
        //Check for the Shape being added to a Container as it is dragged around
        addNodeDragMoveHandler( new NodeDragMoveHandler() {

            @Override
            public void onNodeDragMove( final NodeDragMoveEvent nodeDragMoveEvent ) {
                boundContainer = containerManager.getContainer( WiresFixedText.this.getX(),
                                                                WiresFixedText.this.getY() );
                if ( boundContainer != null ) {
                    boundContainer.detachShape( WiresFixedText.this );
                }

                getLayer().batch();
            }
        } );

        //When the drag ends; if it was within a Container add this Shape to the Container
        addNodeDragEndHandler( new NodeDragEndHandler() {

            @Override
            public void onNodeDragEnd( final NodeDragEndEvent nodeDragEndEvent ) {
                if ( boundContainer != null ) {
                    boundContainer.attachShape( WiresFixedText.this );
                    boundContainer.setHover( false );
                }

                getLayer().batch();
            }
        } );

        //Add support for "in place" editing of text
        editTextBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( final BlurEvent event ) {
                text.setText( editTextBox.getText() );
                bounding.setText( editTextBox.getText() );
                text.getLayer().batch();
                RootPanel.get().remove( editTextBox );
            }
        } );
        editTextBox.addKeyDownHandler( new KeyDownHandler() {
            @Override
            public void onKeyDown( final KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
                    text.setText( editTextBox.getText() );
                    bounding.setText( editTextBox.getText() );
                    text.getLayer().batch();
                    RootPanel.get().remove( editTextBox );
                }
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE ) {
                    RootPanel.get().remove( editTextBox );
                }
            }
        } );
        addNodeMouseDoubleClickHandler( new NodeMouseDoubleClickHandler() {
            @Override
            public void onNodeMouseDoubleClick( final NodeMouseDoubleClickEvent nodeMouseDoubleClickEvent ) {
                editTextBox.setText( text.getText() );
                editTextBox.getElement().getStyle().setPosition( Style.Position.FIXED );
                editTextBox.getElement().getStyle().setLeft( getX() + getLayer().getCanvasElement().getAbsoluteLeft(),
                                                             Style.Unit.PX );
                editTextBox.getElement().getStyle().setTop( getY() + getLayer().getCanvasElement().getAbsoluteTop(),
                                                            Style.Unit.PX );
                RootPanel.get().add( editTextBox );
                editTextBox.setFocus( true );
            }
        } );
    }

    @Override
    public void setContainerManager( final ContainerManager containerManager ) {
        this.containerManager = containerManager;
    }

    @Override
    public void setSelected( final boolean isSelected ) {
        if ( isSelected ) {
            bounding.setAlpha( ALPHA_SELECTED );
        } else {
            bounding.setAlpha( ALPHA_DESELECTED );
        }
    }

    @Override
    public boolean contains( final double cx,
                             final double cy ) {
        return false;
    }

    public String getText() {
        return text.getText();
    }

    public void setText( final String text ) {
        this.text.setText( text );
        this.bounding.setText( text );
    }

    public TextAlign getTextAlign() {
        return text.getTextAlign();
    }

    public void setTextAlign( final TextAlign align ) {
        text.setTextAlign( align );
        bounding.setTextAlign( align );
    }

    public String getStrokeColour() {
        return text.getStrokeColor();
    }

    public void setStrokeColour( final String strokeColour ) {
        text.setStrokeColor( strokeColour );
    }

    public String getFillColour() {
        return text.getFillColor();
    }

    public void setFillColour( final String fillColour ) {
        text.setFillColor( fillColour );
    }

    public double getFontSize() {
        return text.getFontSize();
    }

    public void setFontSize( final double fontSize ) {
        text.setFontSize( fontSize );
        bounding.setFontSize( fontSize );
    }

}
