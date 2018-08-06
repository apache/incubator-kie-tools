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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.container;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(LienzoMockitoTestRunner.class)
public class CellEditorControlImplTest {

    @Mock
    private DMNSession session;

    @Mock
    private CellEditorControlsView view;

    private CellEditorControlImpl control;

    @Before
    public void setup() {
        this.control = new CellEditorControlImpl(view);
    }

    @Test
    public void testBind() {
        control.bind(session);

        assertNotNull(control.getCellEditorControls());
    }

    @Test
    public void testDoInit() {
        assertNull(control.getCellEditorControls());

        control.doInit();

        assertNull(control.getCellEditorControls());
    }

    @Test
    public void testDoDestroy() {
        control.bind(session);

        control.doDestroy();

        assertNull(control.getCellEditorControls());
    }
}
