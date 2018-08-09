/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.packageselector;

import java.util.HashMap;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.validation.ValidatorService;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NewPackagePopupTest {

    private static final String PACKAGE_NAME = "somePackageName";

    private ValidatorService validatorService;

    @Mock
    private ValidationService validationService;

    private CallerMock<ValidationService> validationServiceCallerMock;

    @Mock
    private NewPackagePopupView view;

    private NewPackagePopup newPackagePopup;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Before
    public void initTest() {
        validationServiceCallerMock = new CallerMock<>(validationService);
        validatorService = new ValidatorService(validationServiceCallerMock);
        newPackagePopup = new NewPackagePopup(view,
                                              validatorService);
    }

    @Test
    public void showAndCreateValidPackageTest() {
        Command command = mock(Command.class);
        newPackagePopup.show(command);

        Map<String, Boolean> validationResult = new HashMap<>();
        validationResult.put(PACKAGE_NAME.toLowerCase(),
                             true);
        when(validationService.evaluateJavaIdentifiers(any(String[].class))).thenReturn(validationResult);

        when(view.getPackageName()).thenReturn(PACKAGE_NAME);

        newPackagePopup.onCreatePackage();

        verify(view,
               times(1)).hide();
        verify(command,
               times(1)).execute();
        assertEquals(PACKAGE_NAME.toLowerCase(),
                     newPackagePopup.getPackageName());
    }

    @Test
    public void showAndCreateInvalidValidPackageTest() {
        Command command = mock(Command.class);
        newPackagePopup.show(command);

        Map<String, Boolean> validationResult = new HashMap<>();
        validationResult.put(PACKAGE_NAME.toLowerCase(),
                             false);
        when(validationService.evaluateJavaIdentifiers(any(String[].class))).thenReturn(validationResult);

        when(view.getPackageName()).thenReturn(PACKAGE_NAME);

        newPackagePopup.onCreatePackage();

        verify(view,
               times(1)).setErrorMessage(messageCaptor.capture());
        assertEquals("validation_error_invalid_package_identifier(" + PACKAGE_NAME.toLowerCase() + ")",
                     messageCaptor.getValue());
        verify(view,
               never()).hide();
        verify(command,
               never()).execute();
    }

    @Test
    public void testOnValueTyped() {
        String value1 = "VALUE1";
        String value2 = "ValuE2";
        String value3 = "value3";

        when(view.getPackageName()).thenReturn(value1);
        newPackagePopup.onValueTyped();
        verify(view,
               times(1)).setPackageName(value1.toLowerCase());

        when(view.getPackageName()).thenReturn(value2);
        newPackagePopup.onValueTyped();
        verify(view,
               times(1)).setPackageName(value2.toLowerCase());

        when(view.getPackageName()).thenReturn(value3);
        newPackagePopup.onValueTyped();
        verify(view,
               times(1)).setPackageName(value3.toLowerCase());
    }
}
