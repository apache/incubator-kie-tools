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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Collections;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.mocks.MockHasDOMElementResourcesHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.BaseDOMElementSingletonColumnTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextAreaDOMElement;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class LiteralExpressionColumnTest extends BaseDOMElementSingletonColumnTest<TextAreaSingletonDOMElementFactory, TextAreaDOMElement, TextArea, LiteralExpressionColumn, LiteralExpressionGrid> {

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    @Mock
    private TextAreaDOMElement domElement;

    @Mock
    private TextArea widget;

    @Override
    protected TextAreaSingletonDOMElementFactory getFactory() {
        return factory;
    }

    @Override
    protected TextAreaDOMElement getDomElement() {
        return domElement;
    }

    @Override
    protected TextArea getWidget() {
        return widget;
    }

    @Override
    protected LiteralExpressionGrid getGridWidget() {
        return mock(LiteralExpressionGrid.class);
    }

    @Override
    protected LiteralExpressionColumn getColumn() {
        return new LiteralExpressionColumn(Collections.singletonList(headerMetaData),
                                           factory,
                                           LiteralExpressionColumn.DEFAULT_WIDTH,
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
