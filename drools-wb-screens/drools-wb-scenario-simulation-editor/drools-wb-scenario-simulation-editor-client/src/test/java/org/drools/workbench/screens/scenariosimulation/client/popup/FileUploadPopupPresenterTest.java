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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationEditorPresenter;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.client.utils.ViewsProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class FileUploadPopupPresenterTest {

    @Mock
    private FileUploadPopupView fileUploadPopupViewMock;

    @Mock
    private ScenarioSimulationEditorPresenter scenarioSimulationPresenterMock;

    @Mock
    private ViewsProvider viewsProviderMock;


    private FileUploadPopupPresenter fileUploadPopupPresenter;

    @Before
    public void setup() {
        when(viewsProviderMock.getFileUploadPopup()).thenReturn(fileUploadPopupViewMock);
        fileUploadPopupPresenter = spy(new FileUploadPopupPresenter() {
            {
                this.viewsProvider = viewsProviderMock;
                this.fileUploadPopup = fileUploadPopupViewMock;
            }
        });
    }

    @Test
    public void show() {
        Command okCommand = mock(Command.class);
        fileUploadPopupPresenter.show(ScenarioSimulationEditorConstants.INSTANCE.selectImportFile(), ScenarioSimulationEditorConstants.INSTANCE.importLabel(), okCommand);
        verify(fileUploadPopupViewMock, times(1)).show(eq(ScenarioSimulationEditorConstants.INSTANCE.selectImportFile()), eq(ScenarioSimulationEditorConstants.INSTANCE.importLabel()), eq(okCommand));
    }

    @Test
    public void getFileContents() {
        String FILE_CONTENTS = "FILE_CONTENTS";
        when(fileUploadPopupViewMock.getFileContents()).thenReturn(FILE_CONTENTS);
        assertEquals(FILE_CONTENTS, fileUploadPopupPresenter.getFileContents());
        verify(fileUploadPopupViewMock, times(1)).getFileContents();
    }
}