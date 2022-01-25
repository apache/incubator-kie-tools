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

package org.kie.workbench.common.dmn.client.widgets.grid.columns;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.TextAreaSingletonDOMElementFactory;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCell;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class NameAndDataTypeDOMElementColumnRendererTest extends BaseNameAndDataTypeColumnRendererTest<NameAndDataTypeDOMElementColumnRenderer, String> {

    @Mock
    private TextAreaSingletonDOMElementFactory factory;

    @Override
    protected NameAndDataTypeDOMElementColumnRenderer getColumnRenderer() {
        return new NameAndDataTypeDOMElementColumnRenderer<>(factory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRenderCellWithValue() {
        this.cell = new BaseGridCell<>(new BaseGridCellValue<>(TITLE));

        assertThat(renderer.renderCell(cell, bodyContext)).isNotNull();

        verify(text1).setText(eq(TITLE));
        verify(text1).setX(5);
        verify(text1).setY(5);
    }
}
