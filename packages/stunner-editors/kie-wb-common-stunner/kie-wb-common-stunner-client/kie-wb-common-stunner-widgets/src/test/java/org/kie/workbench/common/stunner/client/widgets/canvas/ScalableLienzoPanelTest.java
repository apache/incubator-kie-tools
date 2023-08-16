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


package org.kie.workbench.common.stunner.client.widgets.canvas;

import java.util.function.Supplier;

import com.ait.lienzo.client.widget.panel.LienzoBoundsPanel;
import com.ait.lienzo.client.widget.panel.impl.ScalablePanel;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class ScalableLienzoPanelTest {

    @Mock
    private StunnerLienzoBoundsPanel panel;

    private ScalableLienzoPanel tested;

    @Before
    public void init() {
        tested = new ScalableLienzoPanel(panel);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInit() {
        tested.init();
        ArgumentCaptor<Supplier> builderCaptor = ArgumentCaptor.forClass(Supplier.class);
        verify(panel, times(1)).setPanelBuilder(builderCaptor.capture());
        Supplier<LienzoBoundsPanel> builder = builderCaptor.getValue();
        LienzoBoundsPanel result = builder.get();
        assertTrue(result instanceof ScalablePanel);
        assertEquals(0, result.getWidePx());
        assertEquals(0, result.getHighPx());
    }
}
