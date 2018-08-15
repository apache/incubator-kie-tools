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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.exports.TextContent;
import org.uberfire.ext.editor.commons.client.file.exports.TextFileExport;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ExportToBpmnSessionCommandTest extends AbstractExportSessionCommandTest {

    private static final String FILE_RAW_CONTENT = "FILE_RAW_CONTENT";

    private static final String ERROR = "ERROR";

    @Mock
    private ClientDiagramService clientDiagramService;

    @Mock
    private ErrorPopupPresenter errorPopupPresenter;

    @Mock
    private TextFileExport textFileExport;

    private ExportToBpmnSessionCommand command;

    private ArgumentCaptor<ServiceCallback> callbackCaptor;

    private ArgumentCaptor<TextContent> textContentCaptor;

    @Before
    public void setUp() {
        super.setup();
        callbackCaptor = ArgumentCaptor.forClass(ServiceCallback.class);
        textContentCaptor = ArgumentCaptor.forClass(TextContent.class);
        command = new ExportToBpmnSessionCommand(clientDiagramService,
                                                 errorPopupPresenter,
                                                 textFileExport);
        command.bind(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExportSuccessful() {
        command.execute(callback);

        verify(clientDiagramService,
               times(1)).getRawContent(eq(diagram),
                                       callbackCaptor.capture());
        callbackCaptor.getValue().onSuccess(FILE_RAW_CONTENT);

        verify(textFileExport,
               times(1)).export(textContentCaptor.capture(),
                                eq(FILE_NAME));
        assertEquals(FILE_RAW_CONTENT,
                     textContentCaptor.getValue().getText());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExportUnSuccessful() {
        command.execute(callback);

        verify(clientDiagramService,
               times(1)).getRawContent(eq(diagram),
                                       callbackCaptor.capture());
        callbackCaptor.getValue().onError(new ClientRuntimeError(ERROR));

        verify(textFileExport,
               never()).export(anyObject(),
                               anyObject());
        verify(errorPopupPresenter,
               times(1)).showMessage(ERROR);
    }

    @Override
    protected AbstractClientSessionCommand getCommand() {
        return command;
    }
}
