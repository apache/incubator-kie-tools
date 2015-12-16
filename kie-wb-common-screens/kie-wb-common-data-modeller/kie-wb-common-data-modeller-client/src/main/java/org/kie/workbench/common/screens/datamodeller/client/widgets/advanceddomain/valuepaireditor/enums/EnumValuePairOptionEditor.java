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

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class EnumValuePairOptionEditor
    implements EnumValuePairOptionEditorView.Presenter,
                IsWidget {

    private EnumValuePairOptionEditorView view;

    private EnumValuePairOptionEditorView.EnumValuePairOptionEditorHandler handler;

    public EnumValuePairOptionEditor() {
        this( new EnumValuePairOptionEditorViewImpl() );
    }

    public EnumValuePairOptionEditor( EnumValuePairOptionEditorView view ) {
        this.view = view;
        view.init( this );
    }

    public EnumValuePairOptionEditor( String optionLabel ) {
        this();
        view.setOptionLabel( optionLabel );
    }

    public void setOptionLabel( String optionLabel ) {
        view.setOptionLabel( optionLabel );
    }

    public void setValue( boolean value ) {
        view.setValue( value );
    }

    public boolean getValue() {
        return view.getValue();
    }

    public void addEnumValuePairOptionEditorHandler(
            EnumValuePairOptionEditorView.EnumValuePairOptionEditorHandler handler ) {
        this.handler = handler;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onValueChange() {
        if ( handler != null ) {
            handler.onValueChange( );
        }
    }
}
