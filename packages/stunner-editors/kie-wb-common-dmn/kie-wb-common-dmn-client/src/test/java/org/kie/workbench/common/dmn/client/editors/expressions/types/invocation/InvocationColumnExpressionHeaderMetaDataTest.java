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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseColumnHeaderMetaDataContextMenuTest;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class InvocationColumnExpressionHeaderMetaDataTest extends BaseColumnHeaderMetaDataContextMenuTest<InvocationColumnExpressionHeaderMetaData, Name, HasName> {

    private static final double BLOCK_WIDTH = 10.0;

    private static final double BLOCK_HEIGHT = 20.0;

    @Mock
    private Supplier<String> titleGetter;

    @Mock
    private Consumer<String> titleSetter;

    @Mock
    private SingletonDOMElementFactory<TextBox, TextBoxDOMElement> factory;

    @Mock
    private GridHeaderColumnRenderContext context;

    @Mock
    private GridRenderer gridRendererMock;

    @Mock
    private GridRendererTheme gridRendererThemeMock;

    @Mock
    private Text textMock;

    private Optional<String> placeHolder = Optional.empty();

    private String title = "column title";

    @Override
    protected InvocationColumnExpressionHeaderMetaData getHeaderMetaData() {
        when(context.getRenderer()).thenReturn(gridRendererMock);
        when(gridRendererMock.getTheme()).thenReturn(gridRendererThemeMock);
        when(gridRendererThemeMock.getBodyText()).thenReturn(textMock);
        when(titleGetter.get()).thenReturn(title);

        return new InvocationColumnExpressionHeaderMetaData(titleGetter,
                                                            titleSetter,
                                                            factory,
                                                            placeHolder,
                                                            listSelector,
                                                            listSelectorItemsSupplier,
                                                            listSelectorItemConsumer);
    }

    @Test
    public void testRender() {

        headerMetaData.render(context, BLOCK_WIDTH, BLOCK_HEIGHT);

        verify(textMock).setText(title);
        verify(textMock).setX(RendererUtils.EXPRESSION_TEXT_PADDING);
        verify(textMock).setY(RendererUtils.EXPRESSION_TEXT_PADDING);
        verify(textMock).setTextAlign(TextAlign.LEFT);
    }

    @Test
    public void testRenderPlaceHolder() {
        headerMetaData.renderPlaceHolder(context, BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    @Test
    public void testGetPlaceHolder() {
        assertThat(headerMetaData.getPlaceHolder()).isEqualTo(placeHolder);
    }
}
