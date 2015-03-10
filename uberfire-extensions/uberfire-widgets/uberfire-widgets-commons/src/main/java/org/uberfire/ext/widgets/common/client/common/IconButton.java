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
package org.uberfire.ext.widgets.common.client.common;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.base.HasType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public class IconButton extends Button implements HasVisibility,
        EventListener,
        HasAttachHandlers,
        IsWidget,
        HasClickHandlers,
        HasDoubleClickHandlers,
        HasEnabled,
        HasType<ButtonType>,
        HasAllKeyHandlers {

    IconButton( IconType iconType, String tooltip ) {
        super();

        if ( tooltip == null || tooltip.length() == 0 ) throw new IllegalArgumentException( "A button tooltip is required!" );

        setType( ButtonType.DEFAULT );
        setSize( ButtonSize.DEFAULT );
        setCaret( false );

        setIcon( iconType );
        setIconSize( IconSize.DEFAULT );
        setTitle( tooltip );
    }
}
