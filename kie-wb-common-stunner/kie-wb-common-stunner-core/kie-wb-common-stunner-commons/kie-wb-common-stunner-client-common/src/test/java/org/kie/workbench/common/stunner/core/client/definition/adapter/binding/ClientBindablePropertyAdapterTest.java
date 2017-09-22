/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.definition.adapter.binding;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClientBindablePropertyAdapterTest extends AbstractClientBindableAdapterTest {

    private ClientBindablePropertyAdapter clientBindablePropertyAdapter;

    @Before
    @Override
    public void init() {
        super.init();

        clientBindablePropertyAdapter = new ClientBindablePropertyAdapter(translationService);
    }

    @Test
    public void testFunctionallity() {
        String description = clientBindablePropertyAdapter.getDescription(model);

        verify(translationService).getPropertyDescription(model.getClass().getName());

        assertEquals(PROPERTY_DESCRIPTION,
                     description);

        String caption = clientBindablePropertyAdapter.getCaption(model);

        verify(translationService).getPropertyCaption(model.getClass().getName());

        assertEquals(PROPERTY_CAPTION,
                     caption);
    }
}
