/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDOMElement;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.MultiValueSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.SingleValueSingletonDOMElementFactory;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.textbox.TextBoxDOMElement;
import org.gwtbootstrap3.client.ui.ListBox;
import org.uberfire.ext.widgets.common.client.common.BooleanTextBox;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;

public class EnumSingleSelectBooleanUiColumn extends BaseEnumSingleSelectUiColumn<Boolean, ListBox, BooleanTextBox, ListBoxDOMElement<Boolean, ListBox>, TextBoxDOMElement<Boolean, BooleanTextBox>> {

    public EnumSingleSelectBooleanUiColumn(final List<HeaderMetaData> headerMetaData,
                                           final double width,
                                           final boolean isResizable,
                                           final boolean isVisible,
                                           final GuidedDecisionTablePresenter.Access access,
                                           final MultiValueSingletonDOMElementFactory<Boolean, ListBox, ListBoxDOMElement<Boolean, ListBox>> multiValueFactory,
                                           final SingleValueSingletonDOMElementFactory<Boolean, BooleanTextBox, TextBoxDOMElement<Boolean, BooleanTextBox>> singleValueFactory,
                                           final GuidedDecisionTableView.Presenter presenter,
                                           final String factType,
                                           final String factField) {
        super(headerMetaData,
              width,
              isResizable,
              isVisible,
              access,
              multiValueFactory,
              singleValueFactory,
              presenter,
              factType,
              factField);
    }

    @Override
    protected void initialiseMultiValueDomElement(final GridCell<Boolean> cell,
                                                  final GridBodyCellRenderContext context,
                                                  final Map<String, String> enumLookups) {
        factory.attachDomElement(context,
                                 ConsumerFactory.makeOnCreationCallback(factory,
                                                                        cell,
                                                                        enumLookups),
                                 ConsumerFactory.makeOnDisplayListBoxCallback());
    }

    @Override
    protected void initialiseSingleValueDomElement(final GridCell<Boolean> cell,
                                                   final GridBodyCellRenderContext context) {
        singleValueFactory.attachDomElement(context,
                                            ConsumerFactory.makeOnCreationCallback(singleValueFactory,
                                                                                   cell),
                                            ConsumerFactory.makeOnDisplayTextBoxCallback());
    }
}
