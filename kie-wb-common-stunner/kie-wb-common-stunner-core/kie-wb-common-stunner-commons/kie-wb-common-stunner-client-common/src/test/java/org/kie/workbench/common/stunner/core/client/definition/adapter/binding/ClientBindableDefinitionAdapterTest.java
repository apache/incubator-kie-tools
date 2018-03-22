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
public class ClientBindableDefinitionAdapterTest extends AbstractClientBindableAdapterTest {

    private ClientBindableDefinitionAdapter clientBindableDefinitionAdapter;

    @Before
    @Override
    public void init() {
        super.init();

        clientBindableDefinitionAdapter = new ClientBindableDefinitionAdapter(definitionUtils,
                                                                              translationService);
    }

    @Test
    public void test() {
        String description = clientBindableDefinitionAdapter.getDescription(model);

        verify(translationService).getDefinitionDescription(model.getClass().getName());

        assertEquals(DEFINITION_DESCRIPTION,
                     description);

        String title = clientBindableDefinitionAdapter.getTitle(model);

        verify(translationService).getDefinitionTitle(model.getClass().getName());

        assertEquals(DEFINITION_TITLE,
                     title);
    }
}
