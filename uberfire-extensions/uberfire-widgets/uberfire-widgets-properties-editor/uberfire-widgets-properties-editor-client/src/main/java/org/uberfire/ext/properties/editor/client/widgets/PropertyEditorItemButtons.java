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

package org.uberfire.ext.properties.editor.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;

public class PropertyEditorItemButtons extends Composite implements HasClickHandlers {

    @UiField
    Button removalButton;

    public PropertyEditorItemButtons() {
        initWidget( uiBinder.createAndBindUi( this ) );
        removalButton.setType( ButtonType.DANGER );
        removalButton.setSize( ButtonSize.EXTRA_SMALL );
        removalButton.setIcon( IconType.MINUS );
//        removalButton.addClickHandler( clickHandler );
    }

    public void addRemovalButton( ClickHandler clickHandler ) {
        removalButton.setVisible( true );
        removalButton.setType( ButtonType.DANGER );
        removalButton.setSize( ButtonSize.EXTRA_SMALL );
        removalButton.setIcon( IconType.MINUS );
        removalButton.addClickHandler( clickHandler );
    }

    @Override
    public HandlerRegistration addClickHandler( ClickHandler handler ) {
        return removalButton.addClickHandler( handler );
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorItemButtons> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}