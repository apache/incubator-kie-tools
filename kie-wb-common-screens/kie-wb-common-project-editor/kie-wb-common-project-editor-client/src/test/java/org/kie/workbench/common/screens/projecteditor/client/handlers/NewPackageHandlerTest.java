/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.handlers;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.validation.ValidatorWithReasonCallback;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.type.AnyResourceTypeDefinition;

@RunWith(GwtMockitoTestRunner.class)
public class NewPackageHandlerTest {

    @Mock
    private ValidationService validationService;
    @Mock
    private ValidatorWithReasonCallback callback;

    @InjectMocks
    private NewPackageHandler handler;

    private org.guvnor.common.services.project.model.Package mockpkg;

    @Before
    public void setUp() {
        handler = new NewPackageHandler(mock(Caller.class),
                                        new CallerMock<>(validationService),
                                        mock(AnyResourceTypeDefinition.class)) {
            @Override
            public Package getPackage() {
                return mockpkg;
            }
        };
    }

    @Test
    public void validate_noPackageSelected() {
        mockpkg = null;

        handler.validate("mockpkg", callback);

        verify(callback).onFailure();
        verify(callback, never()).onFailure(anyString());
        verify(callback, never()).onSuccess();
    }

    @Test
    public void validate_invalidPackageName() {
        mockpkg = mock(org.guvnor.common.services.project.model.Package.class);
        when(validationService.isPackageNameValid(anyString())).thenReturn(false);

        handler.validate("mockpkg", callback);

        verify(callback, never()).onFailure();
        verify(callback).onFailure(anyString());
        verify(callback, never()).onSuccess();
    }

    @Test
    public void validate_validPackageName() {
        mockpkg = mock(org.guvnor.common.services.project.model.Package.class);
        when(validationService.isPackageNameValid(anyString())).thenReturn(true);

        handler.validate("mockpkg", callback);

        verify(callback, never()).onFailure();
        verify(callback, never()).onFailure(anyString());
        verify(callback).onSuccess();
    }
}