/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.advanceddomain.valuepaireditor.enums;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;

public class EnumValuePairOptionEditorViewImpl
    implements EnumValuePairOptionEditorView {

    private CheckBox checkBox;

    private Presenter presenter;

    public EnumValuePairOptionEditorViewImpl() {
        checkBox = new CheckBox( );
        checkBox.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                presenter.onValueChange();
            }
        } );
    }

    @Override
    public void init( Presenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return checkBox.asWidget();
    }

    @Override
    public void setOptionLabel( String optionLabel ) {
        checkBox.setText( optionLabel );
    }

    @Override
    public void setValue( boolean value ) {
        checkBox.setValue( value );
    }

    @Override
    public boolean getValue() {
        return checkBox.getValue() != null ? checkBox.getValue() : false;
    }
}
