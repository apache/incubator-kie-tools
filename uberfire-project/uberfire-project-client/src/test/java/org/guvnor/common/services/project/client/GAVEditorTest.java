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

package org.guvnor.common.services.project.client;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.GAV;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class GAVEditorTest {

    @Mock
    GroupIdChangeHandler groupIdChangeHandler;

    @Mock
    ArtifactIdChangeHandler artifactIdChangeHandler;

    @Mock
    VersionChangeHandler versionChangeHandler;

    @GwtMock
    GAVEditorViewImpl view;

    private GAVEditor editor;

    private GAV gav;

    @Before
    public void setUp() throws Exception {
        editor = new GAVEditor(view);

        verify(view,
               times(1)).setPresenter(editor);

        gav = new GAV("groupId",
                      "artifactId",
                      "version");
    }

    @Test
    public void testSetGav() {
        editor.setGAV(gav);

        verify(view,
               times(1)).setGroupId(gav.getGroupId());
        verify(view,
               times(1)).setArtifactId(gav.getArtifactId());
        verify(view,
               times(1)).setVersion(gav.getVersion());
    }

    @Test
    public void testSetArtifactID() throws Exception {
        editor.setGAV(gav);
        editor.setArtifactID("changed");

        verify(view,
               times(1)).setArtifactId(eq("changed"));

        assertEquals("changed",
                     gav.getArtifactId());
    }

    @Test
    public void testGroupChangeHandler() {
        editor.addGroupIdChangeHandler(groupIdChangeHandler);
        editor.setGAV(gav);

        editor.onGroupIdChange("changedGroup");

        verify(groupIdChangeHandler,
               times(1)).onChange("changedGroup");
        assertEquals("changedGroup",
                     gav.getGroupId());
    }

    @Test
    public void testArtifactChangeHandler() {
        editor.addArtifactIdChangeHandler(artifactIdChangeHandler);
        editor.setGAV(gav);

        editor.onArtifactIdChange("artifactChanged");

        verify(artifactIdChangeHandler,
               times(1)).onChange("artifactChanged");
        assertEquals("artifactChanged",
                     gav.getArtifactId());
    }

    @Test
    public void testVersionChangeHandler() {
        editor.addVersionChangeHandler(versionChangeHandler);
        editor.setGAV(gav);

        editor.onVersionChange("versionChanged");

        verify(versionChangeHandler,
               times(1)).onChange("versionChanged");
        assertEquals("versionChanged",
                     gav.getVersion());
    }
}
