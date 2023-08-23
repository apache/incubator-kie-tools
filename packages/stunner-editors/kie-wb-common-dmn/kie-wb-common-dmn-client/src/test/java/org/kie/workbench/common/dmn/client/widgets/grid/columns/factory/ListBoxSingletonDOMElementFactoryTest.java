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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.ListBox;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom.ListBoxDOMElement;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ListBoxSingletonDOMElementFactoryTest extends BaseSingletonDOMElementFactoryTest<ListBoxSingletonDOMElementFactory, ListBoxDOMElement> {

    private static final String VALUE = "value";

    @Mock
    private ListBox listBox;

    @Mock
    private com.google.gwt.user.client.Element listBoxElement;

    @Mock
    private Style listBoxElementStyle;

    @Before
    public void setup() {
        when(listBox.getElement()).thenReturn(listBoxElement);
        when(listBoxElement.getStyle()).thenReturn(listBoxElementStyle);
        when(listBox.getSelectedValue()).thenReturn(VALUE);

        super.setup();
    }

    @Override
    protected ListBoxSingletonDOMElementFactory getFactoryForAttachDomElementTest() {
        return new ListBoxSingletonDOMElementFactory(gridPanel,
                                                     gridLayer,
                                                     gridWidget,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     (gc) -> new DeleteCellValueCommand(gc,
                                                                                        () -> uiModelMapper,
                                                                                        gridLayer::batch),
                                                     (gcv) -> new SetCellValueCommand(gcv,
                                                                                      () -> uiModelMapper,
                                                                                      gridLayer::batch)) {
            @Override
            public ListBoxDOMElement createDomElement(final GridLayer gridLayer,
                                                      final GridWidget gridWidget) {
                return spy(super.createDomElement(gridLayer,
                                                  gridWidget));
            }
        };
    }

    @Override
    protected ListBoxSingletonDOMElementFactory getFactoryForFlushTest() {
        return new ListBoxSingletonDOMElementFactory(gridPanel,
                                                     gridLayer,
                                                     gridWidget,
                                                     sessionManager,
                                                     sessionCommandManager,
                                                     (gc) -> new DeleteCellValueCommand(gc,
                                                                                        () -> uiModelMapper,
                                                                                        gridLayer::batch),
                                                     (gcv) -> new SetCellValueCommand(gcv,
                                                                                      () -> uiModelMapper,
                                                                                      gridLayer::batch)) {
            @Override
            public ListBox createWidget() {
                return listBox;
            }
        };
    }
}
