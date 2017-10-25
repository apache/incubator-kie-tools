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

package org.guvnor.ala.ui.client.handler;

import org.guvnor.ala.ui.handler.BaseProviderHandlerTest;
import org.junit.Test;

import static org.junit.Assert.*;

public abstract class BaseClientProviderHandlerTest<T extends ClientProviderHandler>
        extends BaseProviderHandlerTest<T> {

    @Test
    public void testGetFormResolver() {
        assertEquals(expectedFormResolver(),
                     providerHandler.getFormResolver());
    }

    @Test
    public void testGetProviderTypeImageURL() {
        assertEquals(expectedProviderTypeImageURL(),
                     providerHandler.getProviderTypeImageURL());
    }

    protected abstract FormResolver expectedFormResolver();

    protected abstract String expectedProviderTypeImageURL();
}