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
package org.kie.workbench.common.dmn.client.canvas.controls.inlineeditor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNCanvasInlineTextEditorControlTest {

    @Mock
    private DMNCanvasInlineTextEditorControl inlineTextEditor;

    @Mock
    private InputData imported;

    @Mock
    private InputData notImported;

    @Test
    public void testIsEditable() {
        doCallRealMethod().when(inlineTextEditor).isFiltered(any());
        when(imported.isAllowOnlyVisualChange()).thenReturn(true);
        assertEquals(false, inlineTextEditor.isFiltered(imported));
    }

    @Test
    public void testIsNotEditable() {
        doCallRealMethod().when(inlineTextEditor).isFiltered(any());
        when(notImported.isAllowOnlyVisualChange()).thenReturn(false);
        assertEquals(true, inlineTextEditor.isFiltered(notImported));
    }

}