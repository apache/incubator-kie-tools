/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.validation;

import org.guvnor.ala.ui.service.ProvisioningValidationService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProvisioningClientValidationServiceTest {

    private static final String CONTAINER_NAME = "CONTAINER_NAME";

    @Mock
    private ProvisioningValidationService validationService;

    private Caller<ProvisioningValidationService> validationServiceCaller;

    private ProvisioningClientValidationService service;

    @Mock
    private ValidatorCallback validatorCallback;

    @Before
    public void setUp() {
        validationServiceCaller = new CallerMock<>(validationService);
        service = new ProvisioningClientValidationService(validationServiceCaller);
    }

    @Test
    public void testIsValidContainerNameForValidName() {
        when(validationService.isValidContainerName(CONTAINER_NAME)).thenReturn(true);
        service.isValidContainerName(CONTAINER_NAME,
                                     validatorCallback);
        verify(validationService,
               times(1)).isValidContainerName(CONTAINER_NAME);
        verify(validatorCallback,
               times(1)).onSuccess();
        verify(validatorCallback,
               never()).onFailure();
    }

    @Test
    public void testIsValidContainerNameForInvalidName() {
        when(validationService.isValidContainerName(CONTAINER_NAME)).thenReturn(false);
        service.isValidContainerName(CONTAINER_NAME,
                                     validatorCallback);
        verify(validationService,
               times(1)).isValidContainerName(CONTAINER_NAME);
        verify(validatorCallback,
               times(1)).onFailure();
        verify(validatorCallback,
               never()).onSuccess();
    }
}
