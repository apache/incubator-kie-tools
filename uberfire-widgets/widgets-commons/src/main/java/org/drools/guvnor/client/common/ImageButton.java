/*
 * Copyright 2005 JBoss Inc
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

package org.drools.guvnor.client.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Image;

/**
 * Really just an image, but tacks on the image-Button style name.
 */
public class ImageButton extends Image {

    private ImageResource       img;
    private ImageResource       disabledImg;
    private ClickHandler        clickHandler;
    private HandlerRegistration handlerRegistration;

    public @UiConstructor
    ImageButton(ImageResource img) {
        super( img );
        this.img = img;
        this.disabledImg = img;
        setStyleName( "image-Button" );
    }

    public ImageButton(ImageResource img,
                       ImageResource disabledImg,
                       String tooltip) {
        super( img );
        this.img = img;
        this.disabledImg = disabledImg;
        setStyleName( "image-Button" );
    }

    public ImageButton(ImageResource img,
                       ImageResource disabledImg) {
        super( img );
        this.img = img;
        this.disabledImg = disabledImg;
        setStyleName( "image-Button" );
    }

    public ImageButton(ImageResource img,
                       String tooltip) {
        super( img );
        this.img = img;
        this.disabledImg = img;
        setStyleName( "image-Button" );
        setTitle( tooltip );
    }

    public ImageButton(ImageResource img,
                       String tooltip,
                       ClickHandler clickHandler) {
        this( img,
              tooltip );
        this.clickHandler = clickHandler;
        assertClickHandler();
    }

    public ImageButton(ImageResource img,
                       ImageResource disabledImg,
                       String tooltip,
                       ClickHandler clickHandler) {
        this( img,
              disabledImg,
              tooltip );
        this.clickHandler = clickHandler;
        assertClickHandler();
    }

    public void setEnabled(boolean enabled) {
        if ( enabled ) {
            super.setResource( img );
            assertClickHandler();
        } else {
            super.setResource( disabledImg );
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
