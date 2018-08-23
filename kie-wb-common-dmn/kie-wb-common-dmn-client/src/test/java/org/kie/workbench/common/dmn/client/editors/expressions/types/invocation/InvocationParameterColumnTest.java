/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.mocks.MockHasDOMElementResourcesHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.BaseDOMElementSingletonColumnTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextBoxSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class InvocationParameterColumnTest extends BaseDOMElementSingletonColumnTest<TextBoxSingletonDOMElementFactory, TextBoxDOMElement, TextBox, InvocationParameterColumn, InvocationGrid> {

    @Mock
    private TextBoxSingletonDOMElementFactory factory;

    @Mock
    private TextBoxDOMElement domElement;

    @Mock
    private TextBox widget;

    @Override
    protected TextBoxSingletonDOMElementFactory getFactory() {
        return factory;
    }

    @Override
    protected TextBoxDOMElement getDomElement() {
        return domElement;
    }

    @Override
    protected TextBox getWidget() {
        return widget;
    }

    @Override
    protected InvocationGrid getGridWidget() {
        return mock(InvocationGrid.class);
    }

    @Override
    protected InvocationParameterColumn getColumn() {
        return new InvocationParameterColumn(headerMetaData,
                                             factory,
                                             gridWidget);
    }

    @Test
    public void testHeaderDOMElementsAreDestroyed() {
        final MockHasDOMElementResourcesHeaderMetaData mockHeaderMetaData = mock(MockHasDOMElementResourcesHeaderMetaData.class);
        column.getHeaderMetaData().add(mockHeaderMetaData);

        column.destroyResources();

        verify(mockHeaderMetaData).destroyResources();
    }
}
