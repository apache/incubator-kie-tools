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


package org.kie.workbench.common.forms.common.rendering.client.widgets.typeahead;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.extras.typeahead.client.base.Dataset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.common.rendering.client.widgets.model.Guitar;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BindableTypeAheadTest {

    public static final String MASK = "{brand} {model}: {body} body with {neck} neck";

    protected BindableTypeAheadView<Guitar> view;

    @GwtMock
    protected Dataset<Guitar> dataset;

    @GwtMock
    protected GwtEvent<?> event;

    @GwtMock
    protected Widget viewWidget;

    protected BindableTypeAhead<Guitar> typeAhead;

    protected Guitar guitar = new Guitar("50's style",
                                         "T-Shape",
                                         "alder",
                                         "mapple");

    @Before
    public void setup() {
        view = mock(BindableTypeAheadView.class);

        when(view.asWidget()).thenReturn(viewWidget);

        typeAhead = new BindableTypeAhead<>(view);

        verify(view).setPresenter(typeAhead);

        typeAhead.init(MASK,
                       dataset);

        verify(view).init(dataset,
                          MASK);

        typeAhead.asWidget();

        verify(view).asWidget();
    }

    @Test
    public void testSetValueWithoutEvents() {

        typeAhead.setValue(guitar);

        verify(view).setValue(guitar);
    }

    @Test
    public void testSetValueWithEvents() {

        typeAhead.setValue(guitar,
                           true);

        verify(view).setValue(guitar);
    }

    @Test
    public void testEvents() {
        ValueChangeHandler handler = mock(ValueChangeHandler.class);
        typeAhead.addValueChangeHandler(handler);
        verify(view,
               atLeast(1)).asWidget();
        verify(viewWidget).addHandler(any(),
                                      any());

        typeAhead.fireEvent(event);
        verify(view,
               atLeast(2)).asWidget();
        verify(viewWidget).fireEvent(event);
    }

    @Test
    public void testReadOnlyTrue() {
        testReadonly(true);
    }

    @Test
    public void testReadOnlyFalse() {
        testReadonly(false);
    }

    private void testReadonly(boolean readOnly) {
        typeAhead.setReadOnly(readOnly);
        verify(view).setReadOnly(readOnly);
    }
}
