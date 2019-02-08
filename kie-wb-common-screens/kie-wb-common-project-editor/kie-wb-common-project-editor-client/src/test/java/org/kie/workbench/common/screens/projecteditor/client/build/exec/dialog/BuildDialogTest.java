/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.build.exec.dialog;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class BuildDialogTest {

    private static final String MESSAGE = "message";

    @Mock
    private BuildDialogView view;

    private BuildDialog dialog;

    @Before
    public void init() {
        dialog = new BuildDialog(view);
    }

    @Test
    public void testFunctionality() {

        checkStartBuild();

        dialog.stopBuild();

        verify(view).hideBusyIndicator();

        assertFalse(dialog.isBuilding());
    }

    @Test
    public void testShowBuildIsAlreadyRunning() {

        dialog.showBuildIsAlreadyRunning();

        verify(view, never()).showABuildIsAlreadyRunning();

        checkStartBuild();

        dialog.showBuildIsAlreadyRunning();

        verify(view).showABuildIsAlreadyRunning();
    }

    @Test
    public void testStartBuildWithBuidAlreadyRunning() {

        checkStartBuild();

        Assertions.assertThatThrownBy(() -> dialog.startBuild())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Build already running");
    }

    private void checkStartBuild() {
        dialog.startBuild();

        dialog.showBusyIndicator(MESSAGE);

        verify(view).showBusyIndicator(MESSAGE);

        assertTrue(dialog.isBuilding());
    }
}
