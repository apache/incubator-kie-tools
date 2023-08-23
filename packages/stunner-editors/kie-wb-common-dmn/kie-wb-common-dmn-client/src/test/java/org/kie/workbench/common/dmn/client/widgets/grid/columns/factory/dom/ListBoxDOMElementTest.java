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

package org.kie.workbench.common.dmn.client.widgets.grid.columns.factory.dom;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.user.client.ui.ListBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ListBoxDOMElementTest extends BaseDOMElementTest<ListBox, ListBoxDOMElement> {

    @Mock
    private ListBox lb;

    @Override
    protected ListBox getWidget() {
        return lb;
    }

    @Override
    protected ListBoxDOMElement getDomElement() {
        return new ListBoxDOMElement(widget,
                                     gridLayer,
                                     gridWidget,
                                     sessionManager,
                                     sessionCommandManager,
                                     (gc) -> new DeleteCellValueCommand(gc,
                                                                        () -> uiModelMapper,
                                                                        gridLayer::batch),
                                     (gcv) -> new SetCellValueCommand(gcv,
                                                                      () -> uiModelMapper,
                                                                      gridLayer::batch));
    }

    @Test
    public void testSetValue() {
        final List<String> values = Stream.of("value1", "value2", VALUE).collect(Collectors.toList());
        final String[] varArgs = new String[values.size() - 1];
        values.subList(1, values.size()).toArray(varArgs);
        when(widget.getItemCount()).thenReturn(values.size());
        when(widget.getItemText(anyInt())).thenReturn(values.get(0), varArgs);

        domElement.setValue(VALUE);
        verify(widget).setSelectedIndex(values.indexOf(VALUE));
    }

    @Test
    public void testGetValue() {
        domElement.getValue();
        verify(widget).getSelectedValue();
    }
}
