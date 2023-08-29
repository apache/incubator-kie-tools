/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.fields.shared.AbstractFieldDefinition;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public abstract class AbstractFormGroupTest<D extends AbstractFormGroup, V extends FormGroupView> {

    @GwtMock
    protected Widget widget;

    @Mock
    protected AbstractFieldDefinition fieldDefinition;

    protected D formGroup;

    protected V view;

    @Before
    public void setUp() {
        view = mock(getViewClass());

        formGroup = getFormGroupInstance(view);
    }

    @Test
    public void testFunctionallity() {
        formGroup.render(widget,
                         fieldDefinition);

        verify(view).render(widget,
                            fieldDefinition);

        formGroup.getElement();

        verify(view).getElement();
    }

    protected abstract Class<V> getViewClass();

    protected abstract D getFormGroupInstance(V view);
}
