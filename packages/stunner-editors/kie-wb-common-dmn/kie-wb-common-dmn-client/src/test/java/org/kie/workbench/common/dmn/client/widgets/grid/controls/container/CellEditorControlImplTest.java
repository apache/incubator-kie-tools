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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import java.util.Optional;
import java.util.function.Supplier;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.kie.workbench.common.dmn.client.widgets.panel.DMNGridPanel;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class CellEditorControlImplTest {

    @Mock
    private DMNSession session;

    @Mock
    private DMNGridPanel gridPanel;

    @Mock
    private CellEditorControls editorControls;

    @Captor
    private ArgumentCaptor<Optional<Supplier<DMNGridPanel>>> gridPanelSupplierArgumentCaptor;

    private CellEditorControlImpl control;

    @Before
    public void setup() {
        this.control = new CellEditorControlImpl(editorControls);

        when(session.getGridPanel()).thenReturn(gridPanel);
    }

    @Test
    public void testBind() {
        control.bind(session);

        verify(editorControls).setGridPanelSupplier(gridPanelSupplierArgumentCaptor.capture());

        final Optional<Supplier<DMNGridPanel>> gridPanelSupplier = gridPanelSupplierArgumentCaptor.getValue();
        assertTrue(gridPanelSupplier.isPresent());
        assertEquals(gridPanel, gridPanelSupplier.get().get());
    }

    @Test
    public void testDoDestroy() {
        control.bind(session);

        reset(editorControls);

        control.doDestroy();

        verify(editorControls).setGridPanelSupplier(gridPanelSupplierArgumentCaptor.capture());

        final Optional<Supplier<DMNGridPanel>> gridPanelSupplier = gridPanelSupplierArgumentCaptor.getValue();
        assertFalse(gridPanelSupplier.isPresent());
    }
}
