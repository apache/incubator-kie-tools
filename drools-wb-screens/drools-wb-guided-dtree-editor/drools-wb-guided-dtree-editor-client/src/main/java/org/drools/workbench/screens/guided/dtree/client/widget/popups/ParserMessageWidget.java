/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtree.client.widget.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.widgets.client.resources.CommonImages;

/**
 * A widget to display a single conversion result message
 */
public class ParserMessageWidget extends Composite implements HasClickHandlers {

    @UiField
    protected Image image;

    @UiField
    protected InlineLabel label;

    interface ParserMessageWidgetBinder
            extends
            UiBinder<Widget, ParserMessageWidget> {

    }

    private static ParserMessageWidgetBinder uiBinder = GWT.create( ParserMessageWidgetBinder.class );

    public ParserMessageWidget( final String message ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        sinkEvents( Event.ONCLICK );

        this.image.setResource( CommonImages.INSTANCE.information() );
        this.label.setText( message );
    }

    @Override
    public HandlerRegistration addClickHandler( final ClickHandler handler ) {
        return addDomHandler( handler,
                              ClickEvent.getType() );
    }
}
