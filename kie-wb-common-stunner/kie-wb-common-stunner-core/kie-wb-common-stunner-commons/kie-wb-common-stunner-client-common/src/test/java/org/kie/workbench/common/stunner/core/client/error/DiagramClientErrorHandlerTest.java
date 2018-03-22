/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.error;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.definition.exception.DefinitionNotFoundException;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DiagramClientErrorHandlerTest {

    private DiagramClientErrorHandler diagramClientErrorHandler;

    @Mock
    private ClientTranslationService clientTranslationService;

    @Mock
    private ClientRuntimeError clientRuntimeError;

    private static final String ERROR_MESSAGE = "Error on the response";
    private static final String DEFINITION_ID = "testId";

    @Before
    public void setUp() {
        diagramClientErrorHandler = new DiagramClientErrorHandler(clientTranslationService);
        when(clientTranslationService.getValue(CoreTranslationMessages.DIAGRAM_LOAD_FAIL_UNSUPPORTED_ELEMENTS,
                                               DEFINITION_ID)).thenReturn(ERROR_MESSAGE);
    }

    @Test
    public void handleErrorTest() {
        reset(clientRuntimeError);

        when(clientRuntimeError.getThrowable()).thenReturn(new DefinitionNotFoundException("Error", DEFINITION_ID));
        diagramClientErrorHandler.handleError(clientRuntimeError, message -> Assert.assertEquals(ERROR_MESSAGE, message));

        when(clientRuntimeError.getThrowable()).thenReturn(new RuntimeException());
        when(clientRuntimeError.toString()).thenReturn("runtime");
        diagramClientErrorHandler.handleError(clientRuntimeError, message -> Assert.assertEquals("runtime", message));
    }
}