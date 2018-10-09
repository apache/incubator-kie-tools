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
import org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.messages.DataTypeFlashMessage.Type.ERROR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ErrorMessageTest {

    @Mock
    private TranslationService translationService;

    @Test
    public void testGetFlashMessage() {

        final String uuid = "uuid";
        final DataTypeFlashMessage.Type expectedType = ERROR;
        final String expectedStrongMessage = "expectedStrongMessage";
        final String expectedRegularMessage = "expectedRegularMessage";
        final String expectedErrorElementSelector = "[data-row-uuid=\"uuid\"] [data-field=\"name-input\"]";
        final DataType dataType = mock(DataType.class);
        final ErrorMessage errorMessage = new ErrorMessage(translationService) {

            @Override
            String getStrongMessage(final DataType dataType) {
                return expectedStrongMessage;
            }

            @Override
            String getRegularMessage() {
                return expectedRegularMessage;
            }
        };

        when(dataType.getUUID()).thenReturn(uuid);

        final DataTypeFlashMessage flashMessage = errorMessage.getFlashMessage(dataType);

        final DataTypeFlashMessage.Type actualType = flashMessage.getType();
        final String actualStrongMessage = flashMessage.getStrongMessage();
        final String actualRegularMessage = flashMessage.getRegularMessage();
        final String actualErrorElementSelector = flashMessage.getErrorElementSelector();

        assertEquals(expectedType, actualType);
        assertEquals(expectedStrongMessage, actualStrongMessage);
        assertEquals(expectedRegularMessage, actualRegularMessage);
        assertEquals(expectedErrorElementSelector, actualErrorElementSelector);
    }
}
