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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.client.editors.expressions.types.BaseColumnHeaderMetaDataContextMenuTest;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.grids.GridRenderer;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.RuleAnnotationClauseColumnHeaderMetaData.COLUMN_GROUP;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class RuleAnnotationClauseColumnHeaderMetaDataTest extends BaseColumnHeaderMetaDataContextMenuTest<RuleAnnotationClauseColumnHeaderMetaData, Name, HasName> {

    private RuleAnnotationClauseColumnHeaderMetaData column;

    @Mock
    private BiFunction<Integer, Integer, List<HasListSelectorControl.ListSelectorItem>> listSelectorItemsSupplier;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer;

    @Mock
    private Supplier<String> titleGetter;

    @Mock
    private Consumer<String> titleSetter;

    @Mock
    private SingletonDOMElementFactory<TextBox, TextBoxDOMElement> factory;

    private Optional<String> placeHolder = Optional.empty();

    @Mock
    private GridHeaderColumnRenderContext context;

    @Mock
    private GridRenderer gridRendererMock;

    @Mock
    private GridRendererTheme gridRendererThemeMock;

    @Mock
    private Text textMock;

    private String title = "annotation column";

    @Override
    protected RuleAnnotationClauseColumnHeaderMetaData getHeaderMetaData() {
        when(context.getRenderer()).thenReturn(gridRendererMock);
        when(gridRendererMock.getTheme()).thenReturn(gridRendererThemeMock);
        when(gridRendererThemeMock.getBodyText()).thenReturn(textMock);
        when(titleGetter.get()).thenReturn(title);

        column = new RuleAnnotationClauseColumnHeaderMetaData(titleGetter,
                                                              titleSetter,
                                                              factory,
                                                              placeHolder,
                                                              listSelectorItemsSupplier,
                                                              listSelector,
                                                              listSelectorItemConsumer);
        return column;
    }

    @Test
    public void testGetColumnGroup() {

        final String actual = column.getColumnGroup();

        assertEquals(COLUMN_GROUP, actual);
    }

    @Test
    public void testGetItems() {

        final int uiRowIndex = 5;
        final int uiColumnIndex = 6;
        final List<HasListSelectorControl.ListSelectorItem> expectedItems = mock(List.class);
        when(listSelectorItemsSupplier.apply(uiRowIndex, uiColumnIndex)).thenReturn(expectedItems);

        final List<HasListSelectorControl.ListSelectorItem> actualItems = column.getItems(uiRowIndex, uiColumnIndex);

        assertEquals(expectedItems, actualItems);
        verify(listSelectorItemsSupplier).apply(uiRowIndex, uiColumnIndex);
    }

    @Test
    public void testGetEditor() {

        final Optional<HasCellEditorControls.Editor> actual = column.getEditor();

        assertEquals(listSelector, actual.get());
    }

    @Test
    public void testOnItemSelected() {

        final HasListSelectorControl.ListSelectorItem item = mock(HasListSelectorControl.ListSelectorItem.class);

        column.onItemSelected(item);

        verify(listSelectorItemConsumer).accept(item);
    }

    @Test
    public void testGetPlaceHolder() {
        assertThat(headerMetaData.getPlaceHolder()).isEqualTo(placeHolder);
    }

    @Test
    public void testEquals() {

        final Supplier<String> otherGetter = mock(Supplier.class);
        when(otherGetter.get()).thenReturn("");
        final RuleAnnotationClauseColumnHeaderMetaData otherEqualsColumn = new RuleAnnotationClauseColumnHeaderMetaData(titleGetter,
                                                                                                                        titleSetter,
                                                                                                                        factory,
                                                                                                                        placeHolder,
                                                                                                                        listSelectorItemsSupplier,
                                                                                                                        listSelector,
                                                                                                                        listSelectorItemConsumer);

        assertEquals(column, otherEqualsColumn);
        assertEquals(column.hashCode(), otherEqualsColumn.hashCode());

        final RuleAnnotationClauseColumnHeaderMetaData notEqualsColumn = new RuleAnnotationClauseColumnHeaderMetaData(otherGetter,
                                                                                                                      titleSetter,
                                                                                                                      factory,
                                                                                                                      placeHolder,
                                                                                                                      listSelectorItemsSupplier,
                                                                                                                      listSelector,
                                                                                                                      listSelectorItemConsumer);

        assertNotEquals(column, notEqualsColumn);
        assertNotEquals(column.hashCode(), notEqualsColumn.hashCode());
    }
}
