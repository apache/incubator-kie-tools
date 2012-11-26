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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Really just an image, but tacks on the image-Button style name.
 */
public class ImageButton extends FocusPanel {

    private final Image               img;
    private final Image               disabledImg;
    private       ClickHandler        clickHandler;
    private       HandlerRegistration handlerRegistration;

    @UiConstructor
    public ImageButton( Image img ) {
        add( img );
        this.img = img;
        this.disabledImg = img;
        setStyleName( "image-Button" );
    }

    public ImageButton( Image img,
                        Image disabledImg,
                        String tooltip ) {
        add( img );
        this.img = img;
        this.disabledImg = disabledImg;
        setStyleName( "image-Button" );
        setTitle( tooltip );
    }

    public ImageButton( Image img,
                        Image disabledImg ) {
        add( img );
        this.img = img;
        this.disabledImg = disabledImg;
        setStyleName( "image-Button" );
    }

    public ImageButton( Image img,
                        String tooltip ) {
        add( img );
        this.img = img;
        this.disabledImg = img;
        setStyleName( "image-Button" );
        setTitle( tooltip );
    }

    public ImageButton( Image img,
                        String tooltip,
                        ClickHandler clickHandler ) {
        this( img,
              tooltip );
        this.clickHandler = clickHandler;
        assertClickHandler();
    }

    public ImageButton( Image img,
                        Image disabledImg,
                        String tooltip,
                        ClickHandler clickHandler ) {
        this( img,
              disabledImg,
              tooltip );
        this.clickHandler = clickHandler;
        assertClickHandler();
    }

    public void setEnabled( boolean enabled ) {
        clear();
        if ( enabled ) {
            add( img );
            assertClickHandler();
        } else {
            add( disabledImg );
            removeClickHandler();
        }
    }

    private void assertClickHandler() {
        if ( this.clickHandler != null ) {
            if ( this.handlerRegistration == null ) {
                this.handlerRegistration = addClickHandler( this.clickHandler );
            }
        }
    }

    private void removeClickHandler() {
        if ( this.handlerRegistration != null ) {
            this.handlerRegistration.removeHandler();
            this.handlerRegistration = null;
        }
    }
}
