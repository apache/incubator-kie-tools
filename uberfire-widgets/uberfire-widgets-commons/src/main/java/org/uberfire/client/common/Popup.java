/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.client.common;

import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Popup extends PopupPanel {

    private boolean dragged = false;
    private int dragStartX;
    private int dragStartY;

    private Command afterShowEvent;
    private Command afterCloseEvent;
    private boolean fixedLocation = false;

    private VerticalPanel container = new VerticalPanel();
    private boolean initialized = false;
    private PopupTitleBar titleBar;

    public Popup() {
        setGlassEnabled( true );
        getElement().getStyle().setZIndex( Integer.MAX_VALUE );

        container.setHorizontalAlignment( VerticalPanel.ALIGN_RIGHT );

        this.titleBar = new PopupTitleBar( getTitle() );

        this.titleBar.closeButton.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                hide();
                if ( afterCloseEvent != null ) {
                    afterCloseEvent.execute();
                }
            }
        } );
        this.titleBar.addMouseDownHandler( new MouseDownHandler() {

            public void onMouseDown( MouseDownEvent event ) {
                dragged = true;
                dragStartX = event.getRelativeX( getElement() );
                dragStartY = event.getRelativeY( getElement() );
                DOM.setCapture( titleBar.getElement() );
            }
        } );
        this.titleBar.addMouseMoveHandler( new MouseMoveHandler() {

            public void onMouseMove( MouseMoveEvent event ) {
                if ( dragged ) {
                    setPopupPosition( event.getClientX() - dragStartX,
                                      event.getClientY() - dragStartY );
                }
            }
        } );
        this.titleBar.addMouseUpHandler( new MouseUpHandler() {

            public void onMouseUp( MouseUpEvent event ) {
                dragged = false;
                DOM.releaseCapture( titleBar.getElement() );
            }
        } );

        container.add( titleBar );

        add( container );
        add( createKeyListeningFocusPanel( container ) );

    }

    public void setAfterShow( Command afterShowEvent ) {
        this.afterShowEvent = afterShowEvent;
    }

    public void setAfterCloseEvent( Command afterCloseEvent ) {
        this.afterCloseEvent = afterCloseEvent;
    }

    @Override
    public void show() {

        if ( afterShowEvent != null ) {
            afterShowEvent.execute();
        }

        final Widget content = getContent();
        addContent( content );

        super.show();

        focusFirstTextBox( content );

        if ( !fixedLocation ) {
            center();
        }
    }

    //Lazy initialization of content as sub-classes may not have created content before super-classes' Constructor is invoked
    private void addContent( final Widget content ) {
        if ( initialized ) {
            return;
        }
        content.setWidth( "100%" );
        container.add( content );
        initialized = true;
    }

    private FocusPanel createKeyListeningFocusPanel( VerticalPanel verticalPanel ) {
        FocusPanel focusPanel = new FocusPanel( verticalPanel );

        focusPanel.addKeyDownHandler( new KeyDownHandler() {
            public void onKeyDown( KeyDownEvent event ) {
                if ( event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE ) {
                    hide();
                }
            }
        } );

        focusPanel.setStyleName( "" );
        focusPanel.setFocus( true );
        focusPanel.setWidth( "100%" );
        return focusPanel;
    }

    private void focusFirstTextBox( Widget content ) {
        if ( content instanceof FormStyleLayout ) {
            FormStyleLayout fsl = (FormStyleLayout) content;
            Widget ow = fsl.getWidget();
            if ( ow instanceof HasWidgets ) {
                focusFirstTextBox( (HasWidgets) ow );
            }
        } else if ( content instanceof HasWidgets ) {
            focusFirstTextBox( ( (HasWidgets) content ) );
        }
    }

    //It's tempting to check for Focusable, but Buttons don't look good when the focus is set
    //We therefore use a white-list of widgets that respond well to having the focus set
    private boolean focusFirstTextBox( HasWidgets container ) {
        boolean bFocused = false;
        Iterator<Widget> iw = container.iterator();
        while ( iw.hasNext() ) {
            Widget w = iw.next();
            if ( w instanceof HasWidgets ) {
                bFocused = focusFirstTextBox( (HasWidgets) w );
            } else if ( w instanceof TextBox ) {
                TextBox tb = (TextBox) w;
                if ( tb.isEnabled() ) {
                    tb.setFocus( true );
                    bFocused = true;
                    break;
                }
            } else if ( w instanceof RadioButton ) {
                RadioButton rb = (RadioButton) w;
                if ( rb.isEnabled() ) {
                    rb.setFocus( true );
                    bFocused = true;
                    break;
                }
            } else if ( w instanceof CheckBox ) {
                CheckBox cb = (CheckBox) w;
                if ( cb.isEnabled() ) {
                    cb.setFocus( true );
                    bFocused = true;
                    break;
                }
            }
        }
        return bFocused;
    }

    @Override
    public void setPopupPosition( int left,
                                  int top ) {
        super.setPopupPosition( left,
                                top );

        if ( left != 0 && top != 0 ) {
            fixedLocation = true;
        }
    }

    /**
     * This returns the height of the usable client space, excluding title bar.
     * @return
     */
    public int getClientHeight() {
        return this.getWidget().getOffsetHeight() - this.titleBar.getOffsetHeight();
    }

    abstract public Widget getContent();

}
