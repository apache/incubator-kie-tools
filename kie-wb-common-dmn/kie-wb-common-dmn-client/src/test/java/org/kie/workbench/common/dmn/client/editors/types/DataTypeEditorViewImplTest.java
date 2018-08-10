/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.client.editors.types;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.dom.DOMTokenList;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeEditorViewImplTest {

    @Mock
    private DataTypePickerWidget dataTypeEditor;

    @Mock
    private DataTypeEditorView.Presenter presenter;

    @Mock
    private HTMLElement element;

    @Mock
    private DOMTokenList domTokenList;

    @Mock
    private Decision decision;

    @Mock
    private QName typeRef;

    @Mock
    private ValueChangeEvent<QName> valueChangeEvent;

    @Captor
    private ArgumentCaptor<ValueChangeHandler<QName>> valueChangeHandlerCaptor;

    private DataTypeEditorViewImpl view;

    @Before
    public void setUp() throws Exception {
        view = spy(new DataTypeEditorViewImpl(dataTypeEditor));
        view.init(presenter);

        doReturn(element).when(view).getElement();
        when(element.getClassList()).thenReturn(domTokenList);
        when(valueChangeEvent.getValue()).thenReturn(typeRef);
    }

    @Test
    public void testInit() {
        verify(dataTypeEditor).addValueChangeHandler(valueChangeHandlerCaptor.capture());

        valueChangeHandlerCaptor.getValue().onValueChange(valueChangeEvent);

        verify(presenter).setTypeRef(eq(typeRef));
    }

    @Test
    public void testSetDMNModel() {
        view.setDMNModel(decision);

        verify(dataTypeEditor).setDMNModel(eq(decision));
    }

    @Test
    public void testInitSelectedTypeRef() {
        view.initSelectedTypeRef(typeRef);

        verify(dataTypeEditor).setValue(eq(typeRef), eq(false));
    }

    @Test
    public void testShow() {
        view.show();

        verify(domTokenList).add(DataTypeEditorViewImpl.OPEN);
    }

    @Test
    public void testHide() {
        view.hide();

        verify(domTokenList).remove(DataTypeEditorViewImpl.OPEN);
    }
}
