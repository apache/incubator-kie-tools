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

package org.kie.workbench.common.dmn.client.editors.types.listview.validation;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.types.DMNValidationService;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsInvalidErrorMessage;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeNameFormatValidatorTest {

    @Mock
    private Caller<DMNValidationService> serviceCaller;

    @Mock
    private DMNValidationService service;

    @Mock
    private EventSourceMock<FlashMessage> flashMessageEvent;

    @Mock
    private DataTypeNameIsInvalidErrorMessage nameIsInvalidErrorMessage;

    private DataTypeNameFormatValidator validator;

    @Before
    public void setup() {
        validator = spy(new DataTypeNameFormatValidator(serviceCaller, flashMessageEvent, nameIsInvalidErrorMessage));
    }

    @Test
    public void testIfIsValid() {

        final DataType dataType = mock(DataType.class);
        final Command onSuccess = mock(Command.class);
        final RemoteCallback<Boolean> callback = (b) -> { /* Nothing. */ };
        final String dataTypeName = "name";

        doReturn(callback).when(validator).getCallback(dataType, onSuccess);
        when(dataType.getName()).thenReturn(dataTypeName);
        when(serviceCaller.call(callback)).thenReturn(service);

        validator.ifIsValid(dataType, onSuccess);

        verify(service).isValidVariableName(dataTypeName);
    }

    @Test
    public void testGetCallbackWhenIsValid() {

        final DataType dataType = mock(DataType.class);
        final Command onSuccess = mock(Command.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);

        when(nameIsInvalidErrorMessage.getFlashMessage(dataType)).thenReturn(flashMessage);

        validator.getCallback(dataType, onSuccess).callback(true);

        verify(onSuccess).execute();
        verify(flashMessageEvent, never()).fire(flashMessage);
    }

    @Test
    public void testGetCallbackWhenIsNotValid() {

        final DataType dataType = mock(DataType.class);
        final Command onSuccess = mock(Command.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);

        when(nameIsInvalidErrorMessage.getFlashMessage(dataType)).thenReturn(flashMessage);

        validator.getCallback(dataType, onSuccess).callback(false);

        verify(flashMessageEvent).fire(flashMessage);
        verify(onSuccess, never()).execute();
    }
}
