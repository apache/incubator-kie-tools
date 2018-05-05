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

package org.kie.workbench.common.stunner.core.client.components.palette;

import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollapsedPaletteDefinitionBuilderTest {

    private static final String DEF1_ID = "def1";
    private static final String DEF1_CATEGORY = "cat1";
    private static final String DEF1_TITLE = "defITitle";
    private static final String DEF1_DESC = "defIDesc";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private AdapterManager adapterManager;

    @Mock
    private DefinitionsCacheRegistry definitionsRegistry;

    @Mock
    private StunnerTranslationService translationService;

    @Mock
    private Metadata metadata;

    @Mock
    private Function<String, DefaultPaletteItem> itemSupplier;

    @Mock
    private Object definition1;

    @Mock
    private DefinitionAdapter<Object> definitionAdapter1;

    private CollapsedPaletteDefinitionBuilder tested;

    @Before
    public void setup() {
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter1);
        when(definitionAdapter1.getId(eq(definition1))).thenReturn(DEF1_ID);
        when(definitionAdapter1.getCategory(eq(definition1))).thenReturn(DEF1_CATEGORY);
        when(definitionAdapter1.getTitle(eq(definition1))).thenReturn(DEF1_TITLE);
        when(definitionAdapter1.getDescription(eq(definition1))).thenReturn(DEF1_DESC);
        tested = new CollapsedPaletteDefinitionBuilder(definitionUtils,
                                                       definitionsRegistry,
                                                       translationService);
    }

    @Test
    public void testCrateItem() {
        DefaultPaletteItem item = tested.createItem(definition1,
                                                    DEF1_CATEGORY,
                                                    metadata,
                                                    itemSupplier);
        assertNotNull(item);
        assertEquals(DEF1_ID, item.getId());
        assertEquals(DEF1_ID, item.getDefinitionId());
        assertEquals(DEF1_TITLE, item.getTooltip());
        assertEquals(DEF1_DESC, item.getDescription());
        assertEquals(CollapsedPaletteDefinitionBuilder.ICON_SIZE, item.getIconSize());
        assertTrue(null == item.getTitle() || item.getTitle().trim().length() == 0);
    }
}
