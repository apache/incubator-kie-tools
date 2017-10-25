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

package org.guvnor.ala.ui.openshift.backend.handler;

import org.guvnor.ala.openshift.model.OpenShiftProviderType;
import org.guvnor.ala.ui.backend.service.converter.ProviderConfigConverter;
import org.guvnor.ala.ui.backend.service.handler.BaseBackendProviderHandlerTest;
import org.junit.Before;

import static org.mockito.Mockito.*;

public class OpenShiftBackendProviderHandlerTest
        extends BaseBackendProviderHandlerTest<OpenShiftBackendProviderHandler> {

    private OpenShiftProviderConfigConverter configConverter;

    @Override
    @Before
    public void setUp() {
        configConverter = mock(OpenShiftProviderConfigConverter.class);
        super.setUp();
    }

    @Override
    protected ProviderConfigConverter expectedProviderConfigConverter() {
        return configConverter;
    }

    @Override
    protected String getProviderTypeName() {
        return OpenShiftProviderType.instance().getProviderTypeName();
    }

    @Override
    protected OpenShiftBackendProviderHandler createProviderHandler() {
        return new OpenShiftBackendProviderHandler(configConverter);
    }

    @Override
    protected int expectedPriority() {
        return 1;
    }
}
