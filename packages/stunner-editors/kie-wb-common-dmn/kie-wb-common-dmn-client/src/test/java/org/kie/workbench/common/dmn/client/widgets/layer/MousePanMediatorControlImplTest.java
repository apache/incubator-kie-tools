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

package org.kie.workbench.common.dmn.client.widgets.layer;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.session.DMNSession;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MousePanMediatorControlImplTest {

    @Mock
    private DMNSession session;

    @Mock
    private DMNGridLayer gridLayer;

    private MousePanMediatorControlImpl control;

    @Before
    public void setup() {
        this.control = new MousePanMediatorControlImpl();

        when(session.getGridLayer()).thenReturn(gridLayer);
    }

    @Test
    public void testBind() {
        control.bind(session);

        assertNotNull(control.getMousePanMediator());
    }

    @Test
    public void testDoInit() {
        assertNull(control.getMousePanMediator());

        control.doInit();

        assertNull(control.getMousePanMediator());
    }

    @Test
    public void testDoDestroy() {
        control.bind(session);

        control.doDestroy();

        assertNull(control.getMousePanMediator());
    }
}
