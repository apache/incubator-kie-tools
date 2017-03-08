/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.gwttest;

import com.google.gwt.junit.client.GWTTestCase;
import org.uberfire.client.workbench.widgets.panel.MaximizeToggleButton;
import org.uberfire.mvp.Command;
import org.uberfire.wbtest.testutil.CountingCommand;

public class MaximizeToggleButtonTest extends GWTTestCase {

    MaximizeToggleButton maximizeButton;
    CountingCommand maximizeCommand = new CountingCommand();
    CountingCommand unmaximizeCommand = new CountingCommand();

    @Override
    public String getModuleName() {
        return "org.uberfire.wbtest.UberFireClientGwtTest";
    }

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();

        maximizeButton = new MaximizeToggleButton();
        maximizeButton.setMaximizeCommand(maximizeCommand);
        maximizeButton.setUnmaximizeCommand(unmaximizeCommand);
    }

    public void testMaximizeWhenClicked() throws Exception {
        maximizeButton.click();
        assertTrue(maximizeButton.isMaximized());
        assertEquals(1,
                     maximizeCommand.executeCount);
        assertEquals(0,
                     unmaximizeCommand.executeCount);
    }

    public void testUnaximizeWhenClickedAgain() throws Exception {
        maximizeButton.click();
        maximizeButton.click();
        assertFalse(maximizeButton.isMaximized());
        assertEquals(1,
                     maximizeCommand.executeCount);
        assertEquals(1,
                     unmaximizeCommand.executeCount);
    }

    public void testSetMaximizedDoesNotInvokeCommands() throws Exception {
        maximizeButton.setMaximized(true);
        maximizeButton.setMaximized(false);

        assertFalse(maximizeButton.isMaximized());
        assertEquals(0,
                     maximizeCommand.executeCount);
        assertEquals(0,
                     unmaximizeCommand.executeCount);
    }

    public void testSetMaximizedFromCallbackIsSafe() throws Exception {
        maximizeButton.setMaximizeCommand(new Command() {
            @Override
            public void execute() {
                maximizeButton.setMaximized(true);
            }
        });

        maximizeButton.click();

        assertTrue(maximizeButton.isMaximized());
        assertEquals(0,
                     maximizeCommand.executeCount);
        assertEquals(0,
                     unmaximizeCommand.executeCount);
    }
}
