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

package org.guvnor.ala.ui.wildfly.client.provider;

import org.guvnor.ala.ui.client.handler.BaseClientProviderHandlerTest;
import org.guvnor.ala.ui.client.handler.FormResolver;
import org.guvnor.ala.ui.wildfly.client.handler.WildflyClientProviderHandler;
import org.guvnor.ala.ui.wildfly.client.handler.WildflyFormResolver;
import org.junit.Before;

import static org.mockito.Mockito.*;

public class WildflyClientProviderHandlerTest
        extends BaseClientProviderHandlerTest<WildflyClientProviderHandler> {

    private static final String PROVIDER_TYPE_NAME = "wildfly";

    private static final String IMAGE = "IMAGE";

    private WildflyFormResolver formResolver;

    @Override
    @Before
    public void setUp() {
        formResolver = mock(WildflyFormResolver.class);
        super.setUp();
    }

    @Override
    protected FormResolver expectedFormResolver() {
        return formResolver;
    }

    @Override
    protected String expectedProviderTypeImageURL() {
        return IMAGE;
    }

    @Override
    protected String getProviderTypeName() {
        return PROVIDER_TYPE_NAME;
    }

    @Override
    protected WildflyClientProviderHandler createProviderHandler() {
        return new WildflyClientProviderHandler(formResolver) {
            @Override
            public String getProviderTypeImageURL() {
                return IMAGE;
            }
        };
    }

    @Override
    protected int expectedPriority() {
        return 1;
    }
}
