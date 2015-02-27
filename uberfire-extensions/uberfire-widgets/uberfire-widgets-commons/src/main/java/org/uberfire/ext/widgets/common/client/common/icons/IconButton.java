/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.widgets.common.client.common.icons;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.base.HasType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.workbench.model.toolbar.IconType;

public class IconButton extends Composite implements HasVisibility,
                                                     EventListener,
                                                     HasAttachHandlers,
                                                     IsWidget,
                                                     HasClickHandlers,
                                                     HasDoubleClickHandlers,
                                                     HasEnabled,
                                                     HasType<ButtonType>,
                                                     HasAllKeyHandlers {
    private final FlowPanel container;

    private Tooltip tooltip;

    private Button button;

    IconButton( IconType iconType, String _tooltip ) {

        container = new FlowPanel();

        button = new Button( "", IconTypeAdapter.adapt( iconType ) );
        button.setType( ButtonType.DEFAULT );
        button.setSize( ButtonSize.MINI );
        button.setCaret( false );

        tooltip = new Tooltip( _tooltip );
        tooltip.setWidget( button );

        container.add( tooltip );

        initWidget( container );
    }

    public void displayCaret( boolean visible ) {
        button.setCaret( visible );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return button.addClickHandler( handler );
    }

    @Override
    public HandlerRegistration addDoubleClickHandler( DoubleClickHandler handler ) {
        return button.addDoubleClickHandler( handler );
    }

    @Override
    public boolean isEnabled() {
        return button.isEnabled();
    }

    @Override
    public void setEnabled( boolean enabled ) {
        button.setEnabled( enabled );
    }

    @Override
    public HandlerRegistration addKeyDownHandler( KeyDownHandler handler ) {
        return button.addKeyDownHandler( handler );
    }

    @Override
    public HandlerRegistration addKeyPressHandler( KeyPressHandler handler ) {
        return button.addKeyPressHandler( handler );
    }

    @Override
    public HandlerRegistration addKeyUpHandler( KeyUpHandler handler ) {
        return button.addKeyUpHandler( handler );
    }

    @Override
    public void setType( ButtonType style ) {
        button.setType( style );
    }

    public void setSize(ButtonSize size) {
        button.setSize( size );
    }
}
