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

package org.kie.workbench.common.dmn.client.editors.types.listview.validation;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.common.messages.FlashMessage;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.errors.DataTypeNameIsInvalidErrorMessage;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeNameFormatValidatorTest {

    @Mock
    private DMNClientServicesProxy clientServicesProxy;

    @Mock
    private EventSourceMock<FlashMessage> flashMessageEvent;

    @Mock
    private DataTypeNameIsInvalidErrorMessage nameIsInvalidErrorMessage;

    private DataTypeNameFormatValidator validator;

    @Before
    public void setup() {
        validator = spy(new DataTypeNameFormatValidator(clientServicesProxy,
                                                        flashMessageEvent,
                                                        nameIsInvalidErrorMessage));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIfIsValid() {
        final DataType dataType = mock(DataType.class);
        final Command onSuccess = mock(Command.class);
        final String dataTypeName = "name";

        when(dataType.getName()).thenReturn(dataTypeName);

        validator.ifIsValid(dataType, onSuccess);

        verify(clientServicesProxy).isValidVariableName(eq(dataTypeName),
                                                        any(ServiceCallback.class));
    }

    @Test
    public void testGetCallbackWhenIsValid() {
        final DataType dataType = mock(DataType.class);
        final Command onSuccess = mock(Command.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);

        when(nameIsInvalidErrorMessage.getFlashMessage(dataType)).thenReturn(flashMessage);

        validator.getCallback(dataType, onSuccess).onSuccess(true);

        verify(onSuccess).execute();
        verify(flashMessageEvent, never()).fire(flashMessage);
    }

    @Test
    public void testGetCallbackWhenIsNotValid() {
        final DataType dataType = mock(DataType.class);
        final Command onSuccess = mock(Command.class);
        final FlashMessage flashMessage = mock(FlashMessage.class);

        when(nameIsInvalidErrorMessage.getFlashMessage(dataType)).thenReturn(flashMessage);

        validator.getCallback(dataType, onSuccess).onSuccess(false);

        verify(flashMessageEvent).fire(flashMessage);
        verify(onSuccess, never()).execute();
    }
}
