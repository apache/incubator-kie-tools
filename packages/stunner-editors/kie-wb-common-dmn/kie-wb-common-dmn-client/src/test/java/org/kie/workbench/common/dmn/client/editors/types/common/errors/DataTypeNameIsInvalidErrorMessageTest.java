/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.editors.types.common.errors;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeNameIsInvalidErrorMessage_RegularMessage;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeNameIsInvalidErrorMessage_StrongMessage;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeNameIsInvalidErrorMessageTest {

    @Mock
    private TranslationService translationService;

    @Test
    public void testGetStrongMessage() {

        final DataType dataType = mock(DataType.class);
        final String expectedErrorMessage = "*Expected strong message.*";
        final DataTypeNameIsInvalidErrorMessage errorMessage = new DataTypeNameIsInvalidErrorMessage(translationService);

        when(translationService.format(DataTypeNameIsInvalidErrorMessage_StrongMessage)).thenReturn(expectedErrorMessage);

        final String actualErrorMessage = errorMessage.getStrongMessage(dataType);

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testGetRegularMessage() {

        final String expectedErrorMessage = "Expected regular message.";
        final DataTypeNameIsInvalidErrorMessage errorMessage = new DataTypeNameIsInvalidErrorMessage(translationService);

        when(translationService.format(DataTypeNameIsInvalidErrorMessage_RegularMessage)).thenReturn(expectedErrorMessage);

        final String actualErrorMessage = errorMessage.getRegularMessage();

        assertEquals(expectedErrorMessage, actualErrorMessage);
    }
}
