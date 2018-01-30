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
package org.drools.workbench.screens.guided.dtable.client.widget.table.columns;

import java.util.HashMap;
import java.util.Map;

import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.widget.table.columns.dom.listbox.ListBoxDOMElement;
import org.gwtbootstrap3.client.ui.ListBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.wires.core.grids.client.widget.dom.single.SingletonDOMElementFactory;

import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ValueListUiColumnCellRendererTest {

    @Mock
    private SingletonDOMElementFactory<ListBox, ListBoxDOMElement<String, ListBox>> factory;

    @Mock
    private Text text;

    private Map<String, String> valueListLookup = new HashMap<>();
    private ValueListUiColumnCellRenderer renderer;

    @Before
    public void setUp() throws Exception {
        renderer = new ValueListUiColumnCellRenderer(factory,
                                                     valueListLookup,
                                                     true);
    }

    @Test
    public void noValueInList() throws Exception {
        renderer.doRenderCellContent(text,
                                     "test",
                                     null);

        verify(text).setText("test");
    }

    @Test
    public void hasValueInList() throws Exception {

        valueListLookup.put("test", "tadaa");

        renderer.doRenderCellContent(text,
                                     "test",
                                     null);

        verify(text).setText("tadaa");
    }
}