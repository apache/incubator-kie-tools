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

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import org.gwtbootstrap3.client.ui.TextBox;
import org.kie.workbench.common.dmn.client.editors.expressions.util.RendererUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.EditableTextHeaderMetaData;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.TextBoxDOMElement;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.HasCellEditorControls;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderColumnRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

public class InvocationColumnExpressionHeaderMetaData extends EditableTextHeaderMetaData<TextBox, TextBoxDOMElement> implements HasCellEditorControls,
                                                                                                                                HasListSelectorControl {

    static final String EXPRESSION_COLUMN_GROUP = "InvocationColumnExpressionHeaderMetaData$Expression";

    private final Optional<String> placeHolder;
    private final ListSelectorView.Presenter listSelector;
    private final BiFunction<Integer, Integer, List<ListSelectorItem>> listSelectorItemsSupplier;
    private final Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer;

    public InvocationColumnExpressionHeaderMetaData(final Supplier<String> titleGetter,
                                                    final Consumer<String> titleSetter,
                                                    final SingletonDOMElementFactory<TextBox, TextBoxDOMElement> factory,
                                                    final Optional<String> placeHolder,
                                                    final ListSelectorView.Presenter listSelector,
                                                    final BiFunction<Integer, Integer, List<HasListSelectorControl.ListSelectorItem>> listSelectorItemsSupplier,
                                                    final Consumer<HasListSelectorControl.ListSelectorItem> listSelectorItemConsumer) {
        super(titleGetter,
              titleSetter,
              factory,
              EXPRESSION_COLUMN_GROUP);
        this.placeHolder = placeHolder;
        this.listSelector = listSelector;
        this.listSelectorItemsSupplier = listSelectorItemsSupplier;
        this.listSelectorItemConsumer = listSelectorItemConsumer;
    }

    @Override
    public Group render(final GridHeaderColumnRenderContext context,
                        final double blockWidth,
                        final double blockHeight) {
        return RendererUtils.getExpressionHeaderText(this,
                                                     context);
    }

    @Override
    public Optional<String> getPlaceHolder() {
        return placeHolder;
    }

    @Override
    public Optional<Editor> getEditor() {
        return Optional.of(listSelector);
    }

    @Override
    public List<ListSelectorItem> getItems(final int uiRowIndex,
                                           final int uiColumnIndex) {
        return listSelectorItemsSupplier.apply(uiRowIndex, uiColumnIndex);
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        listSelectorItemConsumer.accept(item);
    }
}