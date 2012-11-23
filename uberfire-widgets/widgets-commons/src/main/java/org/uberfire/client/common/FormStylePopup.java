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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 */
public class FormStylePopup extends Popup {

    protected FormStyleLayout form;

    public FormStylePopup( final Image image,
                           final String title ) {
        setup( image, title );
    }

    protected void setup( final Image image,
                          final String title ) {
        form = new FormStyleLayout( image, title );

        setModal( true );

        setTitle( title );
    }

    public FormStylePopup( final Image image,
                           final String title,
                           final Integer width ) {
        this( image, title );
        setWidth( width + "px" );
    }

    public FormStylePopup( final ImageResource image,
                           final String title ) {

        setup( image, title );
    }

    protected void setup( final ImageResource image,
                        final String title ) {
        form = new FormStyleLayout( image, title );

        setModal( true );

        setTitle( title );
    }

    public FormStylePopup() {
        form = new FormStyleLayout();
    }

    public FormStylePopup( ImageResource image,
                           final String title,
                           Integer width ) {
        this( image,
              title );
        setWidth( width + "px" );
    }

    @Override
    public Widget getContent() {
        return form;
    }

    public void clear() {
        this.form.clear();
    }

    public int addAttribute( String label,
                             IsWidget wid ) {
        return form.addAttribute( label,
                                  wid );
    }

    public int addAttribute( String label,
                             Widget wid,
                             boolean isVisible ) {
        return form.addAttribute( label,
                                  wid,
                                  isVisible );
    }

    public int addRow( Widget wid ) {
        return form.addRow( wid );
    }

    /**
     * Set the visibility of an Attribute
     * @param row
     * @param isVisible
     */
    public void setAttributeVisibility( int row,
                                        boolean isVisible ) {
        form.setAttributeVisibility( row, isVisible );
    }

}
