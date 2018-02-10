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

package org.drools.workbench.screens.guided.scorecard.client.editor;

import java.util.function.Supplier;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(GwtMockitoTestRunner.class)
public class GuidedScoreCardEditorPresenterTest {

    @Mock
    private Caller<GuidedScoreCardEditorService> scoreCardEditorService;

    @Mock
    private GuidedScoreCardEditorView view;

    @InjectMocks
    private GuidedScoreCardEditorPresenter editor = new GuidedScoreCardEditorPresenter(view);

    @Test
    public void testGetContentSupplier() throws Exception {

        final ScoreCardModel content = mock(ScoreCardModel.class);

        doReturn(content).when(view).getModel();

        final Supplier<ScoreCardModel> contentSupplier = editor.getContentSupplier();

        assertEquals(content, contentSupplier.get());
    }

    @Test
    public void testGetSaveAndRenameServiceCaller() throws Exception {

        final Caller<? extends SupportsSaveAndRename<ScoreCardModel, Metadata>> serviceCaller = editor.getSaveAndRenameServiceCaller();

        assertEquals(scoreCardEditorService, serviceCaller);
    }
}
