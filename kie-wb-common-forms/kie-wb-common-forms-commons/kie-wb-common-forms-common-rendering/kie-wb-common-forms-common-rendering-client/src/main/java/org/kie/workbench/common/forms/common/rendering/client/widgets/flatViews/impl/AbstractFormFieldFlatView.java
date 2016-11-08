/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.common.rendering.client.widgets.flatViews.impl;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import org.kie.workbench.common.forms.common.rendering.client.widgets.flatViews.FormFieldFlatView;

public abstract class AbstractFormFieldFlatView<T> extends Composite implements FormFieldFlatView<T> {

    protected HTML html = new HTML();

    public AbstractFormFieldFlatView() {
        initWidget( html );
    }

    protected T value;

    @Override
    public void setValue( T value ) {
        this.value = value;

        String text = renderValue( value );

        if ( text == null ) {
            text = "";
        }

        html.setText( text );
    }

    @Override
    public T getValue() {
        return value;
    }
}
