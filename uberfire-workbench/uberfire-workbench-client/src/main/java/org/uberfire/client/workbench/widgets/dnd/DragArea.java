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

package org.uberfire.client.workbench.widgets.dnd;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class DragArea extends SimplePanel implements HasClickHandlers,
                                                     HasMouseDownHandlers {

    public DragArea() {
        super();
    }

    public DragArea( final Widget child ) {
        super( child );
    }

    public void add( final Element element ) {
        getElement().appendChild( element );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return addDomHandler( handler, ClickEvent.getType() );
    }

    @Override
    public HandlerRegistration addMouseDownHandler( MouseDownHandler handler ) {
        return addDomHandler( handler, MouseDownEvent.getType() );
    }
}
