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

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextArea;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.commands.general.DeleteCellValueCommand;
import org.kie.workbench.common.dmn.client.commands.general.SetCellValueCommand;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class TextAreaDOMElementTest extends BaseDOMElementTest<TextArea, TextAreaDOMElement> {

    @Mock
    private TextArea ta;

    @Override
    protected TextArea getWidget() {
        return ta;
    }

    @Override
    protected TextAreaDOMElement getDomElement() {
        return new TextAreaDOMElement(widget,
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
        domElement.setValue(VALUE);
        verify(widget).setValue(VALUE);
    }

    @Test
    public void testGetValue() {
        domElement.getValue();
        verify(widget).getValue();
    }
}
