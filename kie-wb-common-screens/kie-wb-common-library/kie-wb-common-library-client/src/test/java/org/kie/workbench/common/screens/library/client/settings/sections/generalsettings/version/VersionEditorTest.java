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

package org.kie.workbench.common.screens.library.client.settings.sections.generalsettings.version;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.mvp.ParameterizedCommand;

import static org.kie.workbench.common.screens.library.client.settings.sections.generalsettings.version.VersionEditor.SNAPSHOT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VersionEditorTest {

    private static final String VERSION = "1.0";

    @Mock
    private VersionEditorView view;

    @Mock
    private ParameterizedCommand<String> changeVersionCommand;

    @Mock
    private ParameterizedCommand<String> changeServerCommand;

    private Boolean isSnapshot;

    private VersionEditor editor;

    @Before
    public void init() {

        doAnswer(invocationOnMock -> {
            isSnapshot = (Boolean) invocationOnMock.getArguments()[0];
            return null;
        }).when(view).setDevelopmentMode(anyBoolean());

        when(view.isDevelopmentMode()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return isSnapshot;
            }
        });

        editor = new VersionEditor(view);

        verify(view).init(eq(editor));
    }

    @Test
    public void testSetDevelopmentMode() {

        editor.setUpVersion(VERSION, changeVersionCommand);

        verify(view).setVersion(any());
        verify(view).setDevelopmentMode(eq(false));
        verify(changeVersionCommand, never()).execute(anyString());

        // Setting development mode to false again, no change should happen
        editor.toggleDevelopmentMode(false);

        verify(view).setVersion(anyString());
        verify(view).setDevelopmentMode(anyBoolean());
        verify(changeVersionCommand, never()).execute(anyString());

        // Setting development mode to true, expecting changes
        editor.toggleDevelopmentMode(true);

        verify(view, times(2)).setVersion(anyString());
        verify(view, times(2)).setDevelopmentMode(anyBoolean());
        verify(changeVersionCommand).execute(eq(VERSION + SNAPSHOT));

        // Setting development mode to true again, no change should happen
        editor.toggleDevelopmentMode(true);

        verify(view, times(2)).setVersion(anyString());
        verify(view, times(2)).setDevelopmentMode(anyBoolean());
        verify(changeVersionCommand, times(1)).execute(anyString());

        // Setting development mode to false, expecting changes
        editor.toggleDevelopmentMode(false);

        verify(view, times(3)).setVersion(anyString());
        verify(view, times(3)).setDevelopmentMode(anyBoolean());
        verify(changeVersionCommand, times(1)).execute(eq(VERSION));
    }

    @Test
    public void testSetVersion() {

        editor.setUpVersion(VERSION, changeVersionCommand);

        verify(view).setVersion(eq(VERSION));
        verify(view).setDevelopmentMode(eq(false));
        verify(changeVersionCommand, never()).execute(anyString());

        final String snapshotVersion = VERSION + SNAPSHOT;

        editor.notifyVersionChange(snapshotVersion);

        verify(view, times(2)).setVersion(eq(VERSION));
        verify(view).setDevelopmentMode(eq(true));
        verify(changeVersionCommand).execute(eq(snapshotVersion));

        final String newVersion = "2.0";

        editor.notifyVersionChange(newVersion);

        verify(view).setVersion(eq(newVersion));
        verify(view, times(2)).setDevelopmentMode(eq(true));
        verify(changeVersionCommand).execute(eq(newVersion + SNAPSHOT));

        editor.notifyVersionChange(VERSION);

        verify(view, times(3)).setVersion(eq(VERSION));
        verify(view, times(3)).setDevelopmentMode(eq(true));
        verify(changeVersionCommand, times(2)).execute(eq(VERSION + SNAPSHOT));

        final String expectedVersion = "3.0";
        final String multipleSnapshotVersion = expectedVersion + SNAPSHOT + SNAPSHOT;

        editor.notifyVersionChange(multipleSnapshotVersion);

        verify(view).setVersion(eq(expectedVersion));
        verify(view, times(4)).setDevelopmentMode(eq(true));
        verify(changeVersionCommand).execute(eq(expectedVersion + SNAPSHOT));
    }
}
