/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.client;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GAVEditorViewImplTest {

    @Mock
    private KeyUpEvent event;

    @Mock
    private GAVEditorView.Presenter presenter;

    @Mock
    TextBox textBoxVersion;

    @Mock
    TextBox textBoxGroup;

    @Mock
    TextBox textBoxArtifact;

    GAVEditorViewImpl view;

    @Before
    public void init() {
        view = new GAVEditorViewImpl();
        view.setPresenter(presenter);
        view.artifactIdTextBox = textBoxArtifact;
        view.groupIdTextBox = textBoxGroup;
        view.versionTextBox = textBoxVersion;
    }

    @Test
    public void testArtifactChange() {
        when(textBoxArtifact.getText()).thenReturn("artifact");

        view.onArtifactIdChange(event);
        verify(presenter,
               times(1)).onArtifactIdChange("artifact");
    }

    @Test
    public void testGroupChange() {
        when(textBoxGroup.getText()).thenReturn("group");

        view.onGroupIdChange(event);
        verify(presenter,
               times(1)).onGroupIdChange("group");
    }

    @Test
    public void testVersionChange() {
        when(textBoxVersion.getText()).thenReturn("version");

        view.onVersionChange(event);
        verify(presenter,
               times(1)).onVersionChange("version");
    }
}
