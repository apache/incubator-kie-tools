/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdHocOrderingProviderTest {

    private static String SEQUENTIAL_KEY = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.AdHocOrderingProvider.sequential";

    private static String PARALELL_KEY = "org.kie.workbench.common.stunner.bpmn.client.dataproviders.AdHocOrderingProvider.parallel";

    private static final String PARALELL_LABEL = "PARALELL";

    private static final String SEQUENTIAL_LABEL = "SEQUENTIAL";

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private FormRenderingContext context;

    private AdHocOrderingProvider provider;

    @Before
    public void setUp() {
        when(translationService.getKeyValue(PARALELL_KEY)).thenReturn(PARALELL_LABEL);
        when(translationService.getKeyValue(SEQUENTIAL_KEY)).thenReturn(SEQUENTIAL_LABEL);
        provider = new AdHocOrderingProvider(translationService);
        provider.init();
    }

    @Test
    public void testGetSelectorData() {
        SelectorData selectorData = provider.getSelectorData(context);
        assertNotNull(selectorData.getValues());
        assertEquals(2,
                     selectorData.getValues().size());
        assertEquals("Sequential",
                     selectorData.getSelectedValue());
        assertEquals(SEQUENTIAL_LABEL,
                     selectorData.getValues().get("Sequential"));
        assertEquals(PARALELL_LABEL,
                     selectorData.getValues().get("Parallel"));
    }
}
