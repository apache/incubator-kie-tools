/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.ListBoxDOMElement;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(ListBoxDOMElement.class)
public class ListBoxSingletonDOMElementFactoryTest extends BaseSingletonDOMElementFactoryTest {

    public static final String VALUE = "val";

    @Mock
    private ListBoxDOMElement domElementMock;

    @Mock
    private ListBox listBoxMock;

    @Override
    public BaseSingletonDOMElementFactory getTestedFactory() {
        final ListBoxSingletonDOMElementFactory factory = spy(new ListBoxSingletonDOMElementFactory(gridLienzoPanelMock,
                                                                                                    gridLayerMock,
                                                                                                    gridWidgetMock));

        when(listBoxMock.getValue(anyInt())).thenReturn(VALUE);

        doReturn(listBoxMock).when(factory).createWidget();
        doReturn(domElementMock).when(factory).createDomElementInternal(listBoxMock, gridLayerMock, gridWidgetMock);

        return factory;
    }
}
