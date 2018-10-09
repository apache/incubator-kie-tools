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

package org.kie.workbench.common.dmn.client.editors.types.persistence.validation;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.NameIsBlankErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.NameIsBlankErrorMessage_StrongMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NameIsBlankErrorMessageTest {

    @Mock
    private TranslationService translationService;

    @Test
    public void testGetStrongMessage() {

        final DataType dataType = mock(DataType.class);
        final String expectedErrorMessage = "*Expected strong message.*";
        final NameIsBlankErrorMessage errorMessage = new NameIsBlankErrorMessage(translationService);

        when(translationService.format(NameIsBlankErrorMessage_StrongMessage)).thenReturn(expectedErrorMessage);

        final String actualErrorMessage = errorMessage.getStrongMessage(dataType);

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testGetRegularMessage() {

        final String expectedErrorMessage = "Expected regular message.";
        final NameIsBlankErrorMessage errorMessage = new NameIsBlankErrorMessage(translationService);

        when(translationService.format(NameIsBlankErrorMessage_RegularMessage)).thenReturn(expectedErrorMessage);

        final String actualErrorMessage = errorMessage.getRegularMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }
}
