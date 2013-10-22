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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class POMEditorPanelTest {

    private POMEditorPanelView view;
    private POMEditorPanel panel;

    @Before
    public void setUp() throws Exception {
        view = mock(POMEditorPanelView.class);
        panel = new POMEditorPanel(view);
    }

    @Test
    public void testLoad() throws Exception {
        POM gavModel = createTestModel("group", "artifact", "1.1.1");
        panel.setPOM(gavModel, false);

        verify(view).setGAV(gavModel.getGav());
        verify(view).setTitleText("artifact");

        gavModel = createTestModel("pomName", "pomDescription", "group", "artifact", "1.1.1");
        panel.setPOM(gavModel, false);

        verify(view).setName("pomName");
        verify(view).setDescription("pomDescription");
    }

    private POM createTestModel(String group, String artifact, String version) {
        return new POM(new GAV(group, artifact, version));
    }

    private POM createTestModel(String name, String description, String group, String artifact, String version) {
        return new POM(name, description, new GAV(group, artifact, version));
    }
}
