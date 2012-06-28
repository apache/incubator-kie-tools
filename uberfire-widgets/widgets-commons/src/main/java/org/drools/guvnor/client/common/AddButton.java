/*
 * Copyright 2010 JBoss Inc
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

import org.drools.guvnor.client.resources.CommonWidgetsImages;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class AddButton extends Composite
    implements
    HasClickHandlers {

    private Image plusButton = new Image( CommonWidgetsImages.INSTANCE.newItem() );

    private Label textLabel  = new Label();

    public AddButton() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.add( plusButton );
        panel.add( textLabel );

        initWidget( panel );
        setStyleName( "guvnor-cursor" );
    }

    public void setText(String text) {
        textLabel.setText( text );
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        textLabel.addClickHandler( handler );
        return plusButton.addClickHandler( handler );
    }
}
