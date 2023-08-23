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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.InformationItemCell.HasNameCell;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.model.GridCellEditAction;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class InformationItemCellNameTest {

    protected static final String VALUE1 = "value1";

    protected static final String VALUE2 = "value2";

    @Mock
    protected ListSelectorView.Presenter listSelector;

    @Mock
    protected GridBodyCellRenderContext cellRenderContext;

    @Mock
    private GridRenderer gridRenderer;

    @Mock
    private GridRendererTheme gridRendererTheme;

    @Mock
    protected Text text1;

    @Mock
    protected Text text2;

    @Mock
    protected Group group;

    protected InformationItemCell cell;

    @Before
    public void setup() {
        GwtMockito.useProviderForType(Group.class, aClass -> group);

        when(cellRenderContext.getRenderer()).thenReturn(gridRenderer);
        when(gridRenderer.getTheme()).thenReturn(gridRendererTheme);
        when(gridRendererTheme.getHeaderText()).thenReturn(text1, text2);
        when(gridRendererTheme.getBodyText()).thenReturn(text1);

        this.cell = makeInformationItemCell();
    }

    protected InformationItemCell makeInformationItemCell() {
        return new InformationItemCell(() -> HasNameCell.wrap(VALUE1),
                                       listSelector);
    }

    @Test
    public void testSupportedEditAction() {
        assertThat(cell.getSupportedEditAction()).isEqualTo(GridCellEditAction.SINGLE_CLICK);
    }

    @Test
    public void testGetName() {
        assertThat(cell.getValue()).isNotNull();
        assertThat(cell.getValue().getValue().getName().getValue()).isEqualTo(VALUE1);
    }

    public void testSetName() {
        assertThatThrownBy(() -> cell.getValue().getValue().setName(new Name(VALUE2)))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining(InformationItemCell.SET_NAME_UNSUPPORTED_MESSAGE);
    }

    @Test
    public void testRenderCell() {
        cell.getValue().getValue().render(cellRenderContext);

        verify(group).add(text1);
    }
}
