/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.client.widgets.actions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.TextBox;

// TODO: Remove event handlers when destroying.
public class NameEditBoxWidgetView extends Composite implements NameEditBoxWidget.View {

    interface ViewBinder extends UiBinder<Widget, NameEditBoxWidgetView> {

    }

    private static ViewBinder uiBinder = GWT.create( ViewBinder.class );

    @UiField
    FlowPanel mainPanel;

    @UiField
    TextBox nameBox;

    @UiField
    Icon closeButton;

    @UiField
    Icon saveButton;

    private NameEditBoxWidget presenter;

    @Override
    public void init( final NameEditBoxWidget presenter ) {
        this.presenter = presenter;
        initWidget( uiBinder.createAndBindUi( this ) );
        nameBox.addValueChangeHandler( event -> {
            final String name = event.getValue();
            presenter.onChangeName( name );
        } );
        nameBox.addKeyPressHandler( keyPressEvent -> presenter.onKeyPress( keyPressEvent.getUnicodeCharCode(), nameBox.getValue() ) );
        nameBox.addKeyDownHandler( keyDownEvent -> presenter.onKeyDown( keyDownEvent.getNativeKeyCode(), nameBox.getValue() ) );
        saveButton.addClickHandler( event -> presenter.onSave() );
        closeButton.addClickHandler( event -> presenter.onClose() );

    }

    @Override
    public NameEditBoxWidget.View show( final String name ) {
        nameBox.setValue( name );
        nameBox.setText( name );
        mainPanel.setVisible( true );
        return this;
    }

    @Override
    public NameEditBoxWidget.View hide() {
        mainPanel.setVisible( false );
        return this;
    }

}
