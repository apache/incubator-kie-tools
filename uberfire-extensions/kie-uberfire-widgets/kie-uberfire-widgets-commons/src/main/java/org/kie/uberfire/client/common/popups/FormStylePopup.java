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

package org.kie.uberfire.client.common.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.client.common.FormStyleLayout;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 */
public class FormStylePopup extends KieBaseModal {

    interface FormStylePopupWidgetBinder
            extends
            UiBinder<Widget, FormStylePopup> {

    }

    private static FormStylePopupWidgetBinder uiBinder = GWT.create( FormStylePopupWidgetBinder.class );

    @UiField(provided = true)
    protected FormStyleLayout form;

    public FormStylePopup( final String title ) {
        setTitle( title );
        form = new FormStyleLayout();
        add( uiBinder.createAndBindUi( this ) );
    }

    public FormStylePopup( final Image image,
                           final String title ) {
        setTitle( title );
        form = new FormStyleLayout( image,
                                    title );
        add( uiBinder.createAndBindUi( this ) );
    }

    public FormStylePopup( final ImageResource image,
                           final String title ) {
        setTitle( title );
        form = new FormStyleLayout( image,
                                    title );
        add( uiBinder.createAndBindUi( this ) );
    }

    public void clear() {
        this.form.clear();
    }

    public int addAttribute( final String label,
                             final IsWidget wid ) {
        return form.addAttribute( label,
                                  wid );
    }

    public int addAttribute( final String label,
                             final Widget wid,
                             final boolean isVisible ) {
        return form.addAttribute( label,
                                  wid,
                                  isVisible );
    }

    public int addRow( final Widget wid ) {
        return form.addRow( wid );
    }

    /**
     * Set the visibility of an Attribute
     * @param row
     * @param isVisible
     */
    public void setAttributeVisibility( final int row,
                                        final boolean isVisible ) {
        form.setAttributeVisibility( row,
                                     isVisible );
    }

}
