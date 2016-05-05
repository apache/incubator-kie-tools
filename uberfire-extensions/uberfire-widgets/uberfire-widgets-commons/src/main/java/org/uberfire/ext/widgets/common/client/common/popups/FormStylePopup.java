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

package org.uberfire.ext.widgets.common.client.common.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ModalHeader;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.uberfire.ext.widgets.common.client.common.FormStyleItem;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 */
public class FormStylePopup extends BaseModal {

    interface FormStylePopupWidgetBinder
            extends
            UiBinder<Widget, FormStylePopup> {

    }

    private static FormStylePopupWidgetBinder uiBinder = GWT.create( FormStylePopupWidgetBinder.class );

    @UiField
    FormStyleLayout form;

    public FormStylePopup( final String title ) {
        setBody( uiBinder.createAndBindUi( this ) );
        setTitle( title );
    }

    public FormStylePopup( final Image icon,
                           final String title ) {
        add( new ModalHeader() {{
            add( new Heading( HeadingSize.H4 ) {{
                add( icon );
                getElement().setInnerText( title );
            }} );
        }} );
        setBody( uiBinder.createAndBindUi( this ) );
    }

    public void clear() {
        this.form.clear();
    }

    public FormStyleItem addAttribute( final String label,
                                       final IsWidget wid ) {
        return form.addAttribute( label, wid );
    }

    public FormStyleItem addAttribute( final String label,
                             final IsWidget wid,
                             final boolean visible ) {
        FormStyleItem formStyleItem = form.addAttribute( label, wid );
        setAttributeVisibility( formStyleItem.getIndex(), visible );
        return formStyleItem;
    }

    public int addRow( final IsWidget wid ) {
        return form.addRow( wid );
    }

    public void setAttributeVisibility( final int index,
                                        final boolean b ) {
        form.setAttributeVisibility( index, b );
    }

}
