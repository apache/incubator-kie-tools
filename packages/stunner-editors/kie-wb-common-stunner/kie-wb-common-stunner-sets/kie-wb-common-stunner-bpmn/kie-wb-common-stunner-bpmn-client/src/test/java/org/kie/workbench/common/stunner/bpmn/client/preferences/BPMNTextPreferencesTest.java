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


package org.kie.workbench.common.stunner.bpmn.client.preferences;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BPMNTextPreferencesTest {

    private BPMNTextPreferences tested;

    @Before
    public void setUp() throws Exception {
        tested = new BPMNTextPreferences();
    }

    @Test
    public void testAttributes() {
        //values from CSS -> BPMNSVGViewFactory#PATH_CSS
        assertEquals(tested.getTextAlpha(), BPMNTextPreferences.TEXT_ALPHA, 0);
        assertEquals(tested.getTextFillColor(), BPMNTextPreferences.TEXT_FILL_COLOR);
        assertEquals(tested.getTextFontFamily(), BPMNTextPreferences.TEXT_FONT_FAMILY);
        assertEquals(tested.getTextStrokeColor(), BPMNTextPreferences.TEXT_STROKE_COLOR);
        assertEquals(tested.getTextStrokeWidth(), BPMNTextPreferences.TEXT_STROKE_WIDTH, 0);
        assertEquals(tested.getTextFontSize(), BPMNTextPreferences.TEXT_FONT_SIZE, 0);
    }
}