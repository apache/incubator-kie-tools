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

package org.drools.workbench.screens.guided.scorecard.backend.server;

import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GuidedScoreCardEditorServiceImplTest {

    @Mock
    private SaveAndRenameServiceImpl<ScoreCardModel, Metadata> saveAndRenameService;

    @InjectMocks
    private GuidedScoreCardEditorServiceImpl service = new GuidedScoreCardEditorServiceImpl();

    @Test
    public void testInit() throws Exception {
        service.init();

        verify(saveAndRenameService).init(service);
    }

    @Test
    public void testSaveAndRename() throws Exception {

        final Path path = mock(Path.class);
        final String newFileName = "newFileName";
        final Metadata metadata = mock(Metadata.class);
        final ScoreCardModel content = mock(ScoreCardModel.class);
        final String comment = "comment";

        service.saveAndRename(path, newFileName, metadata, content, comment);

        verify(saveAndRenameService).saveAndRename(path, newFileName, metadata, content, comment);
    }
}
