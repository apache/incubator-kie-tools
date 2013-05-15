/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.projecteditor.client.forms;


import org.junit.Before;
import org.junit.Test;
import org.kie.guvnor.project.model.GAV;
import org.kie.guvnor.project.model.POM;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.mockito.ArgumentCaptor;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Command;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class POMEditorPanelTest {

    private POMEditorPanelView view;
    private POMEditorPanel panel;
    private MockPomServiceCaller pomServiceCaller;

    @Before
    public void setUp() throws Exception {
        view = mock(POMEditorPanelView.class);
        pomServiceCaller = new MockPomServiceCaller();
        panel = new POMEditorPanel(pomServiceCaller, view);
    }

    @Test
    public void testLoad() throws Exception {
        POM gavModel = createTestModel("group", "artifact", "1.1.1");
        pomServiceCaller.setGav(gavModel);
        Path path = mock(Path.class);
        panel.init(path, false);

        verify(view).setGAV(gavModel.getGav());
        verify(view).setTitleText("artifact");
        verify(view).setDependencies(gavModel.getDependencies());
    }

    @Test
    public void testSave() throws Exception {
        POM gavModel = createTestModel("my.group", "my.artifact", "1.0-SNAPSHOT");
        pomServiceCaller.setGav(gavModel);
        Path path = mock(Path.class);
        panel.init(path, false);

        verify(view).setGAV(gavModel.getGav());
        verify(view).setTitleText("my.artifact");

        gavModel.getGav().setGroupId("group2");
        gavModel.getGav().setArtifactId("artifact2");

        ArgumentCaptor<ArtifactIdChangeHandler> captor = ArgumentCaptor.forClass(ArtifactIdChangeHandler.class);
        verify(view).addArtifactIdChangeHandler(captor.capture());
        gavModel.getGav().setVersion("2.2.2");
        captor.getValue().onChange("2.2.2");

        verify(view).setTitleText("2.2.2");

        Metadata metadata = mock(Metadata.class);
        panel.save(
                "Commit message",
                new Command() {
                    @Override
                    public void execute() {
                        //TODO -Rikkola-
                    }
                },
                metadata);

        POM savedGav = pomServiceCaller.getSavedPOM();
        assertNotNull(savedGav);
        assertEquals("group2", savedGav.getGav().getGroupId());
        assertEquals("artifact2", savedGav.getGav().getArtifactId());
        assertEquals("2.2.2", savedGav.getGav().getVersion());

        verify(view).showSaveSuccessful("pom.xml");
    }

    private POM createTestModel(String group, String artifact, String version) {
        return new POM(new GAV(group, artifact, version));
    }
}
