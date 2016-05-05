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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.html.Paragraph;

public class InfoCube
        extends Composite
        implements HasClickHandlers {

    interface PerspectiveButtonBinder extends UiBinder<Widget, InfoCube> {
    }

    private static PerspectiveButtonBinder uiBinder = GWT.create( PerspectiveButtonBinder.class );

    @UiField
    Heading title;

    @UiField
    Paragraph content;

    public InfoCube() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }

    @Override
    public void setTitle( String title ) {
        super.setTitle( title );
        this.title.setText( title );
    }

    public void setContent( String text ) {
        content.setText( text );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return addDomHandler( handler, ClickEvent.getType() );
    }
}
