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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RendererUtils.class)
public class InvocationColumnExpressionHeaderMetaDataTest {

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

    private Optional<String> placeHolder = Optional.empty();

    private InvocationColumnExpressionHeaderMetaData headerMetaData;

    @Before
    public void setup() {
        this.headerMetaData = new InvocationColumnExpressionHeaderMetaData(titleGetter,
                                                                           titleSetter,
                                                                           factory,
                                                                           placeHolder);
    }

    @Test
    public void testRender() {
        mockStatic(RendererUtils.class);

        headerMetaData.render(context, BLOCK_WIDTH, BLOCK_HEIGHT);

        verifyStatic();
        RendererUtils.getExpressionHeaderText(eq(headerMetaData),
                                              eq(context));
    }

    @Test
    public void testRenderPlaceHolder() {
        mockStatic(RendererUtils.class);

        headerMetaData.renderPlaceHolder(context, BLOCK_WIDTH, BLOCK_HEIGHT);

        verifyStatic();
        RendererUtils.getEditableHeaderPlaceHolderText(eq(headerMetaData),
                                                       eq(context),
                                                       eq(BLOCK_WIDTH),
                                                       eq(BLOCK_HEIGHT));
    }

    @Test
    public void testGetPlaceHolder() {
        assertThat(headerMetaData.getPlaceHolder()).isEqualTo(placeHolder);
    }
}
