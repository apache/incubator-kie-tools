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

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPaletteUtilsTest {

    public static final int ICON_SIZE = 1;

    private DefaultPaletteDefinition paletteDefinition;

    @Before
    public void setup() {
        DefaultPaletteItem item11 = new DefaultPaletteItem("item11", "def11", "item11", "item11", "item11", ICON_SIZE);
        DefaultPaletteItem item12 = new DefaultPaletteItem("item12", "def12", "item12", "item12", "item12", ICON_SIZE);
        DefaultPaletteGroup group1 = new DefaultPaletteGroup("group1", "defGroup1", "group1", "group1", "group1", ICON_SIZE, Arrays.asList(item11, item12));
        DefaultPaletteItem item21 = new DefaultPaletteItem("item21", "def21", "item21", "item21", "item21", ICON_SIZE);
        DefaultPaletteItem item22 = new DefaultPaletteItem("item22", "def22", "item22", "item22", "item22", ICON_SIZE);
        DefaultPaletteGroup group2 = new DefaultPaletteGroup("group2", "defGroup2", "group2", "group2", "group2", ICON_SIZE, Arrays.asList(item21, item22));
        DefaultPaletteCategory category1 = new DefaultPaletteCategory("cat1", "cat1", "cat1", "cat1", "cat1", ICON_SIZE, Arrays.asList(group1, group2), mock(Glyph.class));
        DefaultPaletteItem item31 = new DefaultPaletteItem("item31", "def31", "item31", "item31", "item31", ICON_SIZE);
        DefaultPaletteGroup group3 = new DefaultPaletteGroup("group3", "defGroup3", "group3", "group3", "group3", ICON_SIZE, Collections.singletonList(item31));
        DefaultPaletteItem item4 = new DefaultPaletteItem("item4", "def4", "item4", "item4", "item4", ICON_SIZE);
        DefaultPaletteCategory category2 = new DefaultPaletteCategory("cat2", "cat2", "cat2", "cat2", "cat2", ICON_SIZE, Arrays.asList(group3, item4), mock(Glyph.class));
        paletteDefinition = new DefaultPaletteDefinition(Arrays.asList(category1, category2),
                                                         "defSet1");
    }

    @Test
    public void testGetItemDefinitionId() {
        assertEquals("def11", getItemDefinitionId("item11"));
        assertEquals("def12", getItemDefinitionId("item12"));
        assertEquals("def21", getItemDefinitionId("item21"));
        assertEquals("def22", getItemDefinitionId("item22"));
        assertEquals("def31", getItemDefinitionId("item31"));
        assertEquals("def4", getItemDefinitionId("item4"));
    }

    private String getItemDefinitionId(final String id) {
        return DefaultPaletteUtils.getPaletteItemDefinitionId(paletteDefinition,
                                                              id);
    }
}
