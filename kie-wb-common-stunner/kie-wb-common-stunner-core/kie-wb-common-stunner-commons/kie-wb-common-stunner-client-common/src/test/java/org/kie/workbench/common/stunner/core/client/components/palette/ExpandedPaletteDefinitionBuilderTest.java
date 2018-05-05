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
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPaletteDefinitionBuilder.ItemMessageProvider;
import org.kie.workbench.common.stunner.core.client.components.palette.DefaultPaletteDefinitionBuilders.CategoryBuilder;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpandedPaletteDefinitionBuilderTest {

    private static final String DEF1_ID = "def1";
    private static final String DEF1_CATEGORY = "cat1";
    private static final String DEF1_TITLE = "defITitle";
    private static final String DEF1_DESC = "defIDesc";
    private static final String CAT_ID = "cat1";
    private static final String CAT_DEFID = "cat1DefId";
    private static final String CAT_TITLE = "cat1Title";
    private static final String CAT_DESC = "cat1Desc";
    private static final Glyph CAT_GLYPH = mock(Glyph.class);
    private static final DefaultPaletteCategory SOME_CATEGORY = new CategoryBuilder()
            .setItemId(CAT_ID)
            .setDefinitionId(CAT_ID)
            .setTitle(CAT_TITLE)
            .setDescription(CAT_DESC)
            .setTooltip(CAT_TITLE)
            .setGlyph(CAT_GLYPH)
            .build();
    private static final Function<String, String> CAT_DEFID_PROVIDER = cat -> CAT_ID.equals(cat) ? CAT_DEFID : null;
    private static final Function<String, Glyph> CAT_GLYPH_PROVIDER = cat -> CAT_ID.equals(cat) ? CAT_GLYPH : null;
    private static final ItemMessageProvider CAT_MSG_PROVIDER = new ItemMessageProvider() {
        @Override
        public String getTitle(String id) {
            return CAT_ID.equals(id) ? CAT_TITLE : null;
        }

        @Override
        public String getDescription(String id) {
            return CAT_ID.equals(id) ? CAT_DESC : null;
        }
    };

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

    private ExpandedPaletteDefinitionBuilder tested;

    @Before
    public void setup() {
        when(definitionUtils.getDefinitionManager()).thenReturn(definitionManager);
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter1);
        when(definitionAdapter1.getId(eq(definition1))).thenReturn(DEF1_ID);
        when(definitionAdapter1.getCategory(eq(definition1))).thenReturn(DEF1_CATEGORY);
        when(definitionAdapter1.getTitle(eq(definition1))).thenReturn(DEF1_TITLE);
        when(definitionAdapter1.getDescription(eq(definition1))).thenReturn(DEF1_DESC);
        tested = new ExpandedPaletteDefinitionBuilder(definitionUtils,
                                                      definitionsRegistry,
                                                      translationService);
    }

    @Test
    public void testCrateItemAndAppendIntoCategory() {
        when(itemSupplier.apply(eq(CAT_ID))).thenReturn(SOME_CATEGORY);
        DefaultPaletteItem category = tested.createItem(definition1,
                                                        CAT_ID,
                                                        metadata,
                                                        itemSupplier);
        assertNull(category);
        assertFalse(SOME_CATEGORY.getItems().isEmpty());
        DefaultPaletteItem item = SOME_CATEGORY.getItems().iterator().next();
        assertNotNull(item);
        assertEquals(DEF1_ID, item.getId());
        assertEquals(DEF1_ID, item.getDefinitionId());
        assertEquals(DEF1_TITLE, item.getTitle());
        assertEquals(DEF1_DESC, item.getDescription());
        assertEquals(DefaultPaletteDefinitionBuilders.DEFAULT_ICON_SIZE, item.getIconSize());
        assertTrue(null == item.getTooltip() || item.getTooltip().trim().length() == 0);
    }

    @Test
    public void testCrateItemAndAppendIntoNewCategory() {
        when(itemSupplier.apply(eq(CAT_ID))).thenReturn(null);
        DefaultPaletteItem _category =
                tested.categoryDefinitionIdProvider(CAT_DEFID_PROVIDER)
                        .categoryGlyphProvider(CAT_GLYPH_PROVIDER)
                        .categoryMessages(CAT_MSG_PROVIDER)
                        .createItem(definition1,
                                    CAT_ID,
                                    metadata,
                                    itemSupplier);
        assertNotNull(_category);
        assertEquals(CAT_ID, _category.getId());
        assertEquals(CAT_DEFID, _category.getDefinitionId());
        assertEquals(CAT_TITLE, _category.getTitle());
        assertEquals(CAT_DESC, _category.getDescription());
        assertTrue(DefaultPaletteCategory.class.isInstance(_category));
        DefaultPaletteCategory category = (DefaultPaletteCategory) _category;
        assertFalse(category.getItems().isEmpty());
        DefaultPaletteItem item = category.getItems().iterator().next();
        assertNotNull(item);
        assertEquals(DEF1_ID, item.getId());
        assertEquals(DEF1_ID, item.getDefinitionId());
        assertEquals(DEF1_TITLE, item.getTitle());
        assertEquals(DEF1_DESC, item.getDescription());
        assertEquals(DefaultPaletteDefinitionBuilders.DEFAULT_ICON_SIZE, item.getIconSize());
        assertTrue(null == item.getTooltip() || item.getTooltip().trim().length() == 0);
    }
}
