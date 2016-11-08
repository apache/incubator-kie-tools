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

package org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.impl;

import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayerView;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayerWidgetAware;
import org.kie.workbench.common.forms.model.FieldDefinition;

public abstract class AbstractFormGroupDisplayer<V extends FormGroupDisplayerView> implements FormGroupDisplayerWidgetAware {

    protected V view;

    public AbstractFormGroupDisplayer( V view ) {
        this.view = view;
    }

    public void render( Widget widget, FieldDefinition field ) {
        view.render( widget, field );
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
