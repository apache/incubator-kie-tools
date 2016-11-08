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
import com.google.gwtmockito.GwtMock;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroupDisplayers.FormGroupDisplayerView;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

public abstract class AbstractFormGroupDisplayerTest<D extends AbstractFormGroupDisplayer, V extends FormGroupDisplayerView> {

    @GwtMock
    protected Widget widget;

    @Mock
    protected FieldDefinition fieldDefinition;

    protected D displayer;

    protected V view;

    @Before
    public void setUp() {
        view = mock( getViewClass() );

        displayer = getDisplayerInstance( view );
    }

    @Test
    public void testFunctionallity() {
        displayer.render( widget, fieldDefinition );

        verify( view ).render( widget, fieldDefinition );

        displayer.asWidget();

        verify( view ).asWidget();
    }

    protected abstract Class<V> getViewClass();

    protected abstract D getDisplayerInstance( V view );
}
