/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.views.pfly.tab;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.client.views.pfly.mock.MockPlaceManager;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;

@RunWith(GwtMockitoTestRunner.class)
public class UberTabPanelTest {

    private final MockPlaceManager mockPlaceManager = new MockPlaceManager();
    private UberTabPanel uberTabPanel;
    @GwtMock
    private ResizeTabPanel resizeTabPanel;

    @Before
    public void gwtSetUp() throws Exception {
        uberTabPanel = new UberTabPanel(mockPlaceManager,
                                        resizeTabPanel);
    }

    @Test
    public void testFireFocusEventWhenClickedWhenUnfocused() throws Exception {
        uberTabPanel.setFocus(false);

        final int[] focusEventCount = new int[1];
        uberTabPanel.addOnFocusHandler(new Command() {
            @Override
            public void execute() {
                focusEventCount[0]++;
            }
        });

        uberTabPanel.onClick(null);
        assertEquals(1,
                     focusEventCount[0]);
    }
}
