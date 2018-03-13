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
package org.kie.workbench.common.screens.datamodeller.client;

import java.util.ArrayList;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.Modal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.javaeditor.client.widget.EditJavaSourceWidget;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.test.MockProvider;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.java.nio.IOException;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Modal.class})
public class DataModelerScreenPresenterValidationTest {

    private DataModelerScreenPresenter presenter;

    @Mock
    private DataModelerService modelerService;

    @Before
    public void setUp() throws Exception {
        presenter = new DataModelerScreenPresenter(mock(DataModelerScreenPresenter.DataModelerScreenView.class),
                                                   mock(SessionInfo.class)) {
            {
                validationPopup = MockProvider.getMockValidationPopup();
                context = mock(DataModelerContext.class);
                javaSourceEditor = mock(EditJavaSourceWidget.class);
                modelerService = new CallerMock<>(DataModelerScreenPresenterValidationTest.this.modelerService);
                versionRecordManager = mock(VersionRecordManager.class);
            }
        };
    }

    @Test
    public void commandIsCalled() throws Exception {

        doReturn(new ArrayList<ValidationMessage>()).when(modelerService).validate(anyString(),
                                                                                   any(Path.class),
                                                                                   any(DataObject.class));

        final Command afterValidation = mock(Command.class);
        presenter.onValidate(afterValidation);
        verify(afterValidation).execute();
    }

    @Test
    public void callFailsAndCommandIsCalled() throws Exception {

        doThrow(new IOException()).when(modelerService).validate(anyString(),
                                                                 any(Path.class),
                                                                 any(DataObject.class));

        final Command afterValidation = mock(Command.class);
        presenter.onValidate(afterValidation);
        verify(afterValidation).execute();
    }
}