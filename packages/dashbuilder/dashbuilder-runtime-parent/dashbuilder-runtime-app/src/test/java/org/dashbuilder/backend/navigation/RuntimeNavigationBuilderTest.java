/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.backend.navigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.workbench.NavWorkbenchCtx;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RuntimeNavigationBuilderTest {

    private static final String ITEM = "item1";
    private static final String GROUP = "group";
    private static final String EMPTY_GROUP = "emptyGroup";

    RuntimeNavigationBuilder runtimeNavigationBuilder;

    @Before
    public void init() {
        runtimeNavigationBuilder = new RuntimeNavigationBuilder();
    }

    /**
     * Checks if groups are pruned correctly
     */
    @Test
    public void groupPruneTest() {
        NavTreeBuilder builder = new NavTreeBuilder();
        LayoutTemplate lt = new LayoutTemplate("lt1");

        final String NOT_PRUNED_GROUP = "notemptygroup";
        final String EMPTYCHILDGROUP = "emptychildgroup";
        final String EMPTYONANONEMPTY = "empty_on_a_not_empty";

        builder.group(EMPTY_GROUP, "empty", "", false);
        builder.group(EMPTYCHILDGROUP, "", "", false);
        builder.endGroup();
        builder.endGroup();

        builder.group(NOT_PRUNED_GROUP, "notemptygroup", "", false);
        builder.item(ITEM, ITEM, "", false, NavWorkbenchCtx.perspective(lt.getName()));
        builder.group(EMPTYONANONEMPTY, EMPTYONANONEMPTY, "", false);
        builder.endGroup();
        builder.endGroup();

        List<LayoutTemplate> templates = new ArrayList<>();
        templates.add(lt);
        NavTree originalTree = builder.build();

        assertEquals(2, originalTree.getRootItems().size());
        assertNotNull(originalTree.getItemById(EMPTYONANONEMPTY));
        assertNotNull(originalTree.getItemById(EMPTY_GROUP));
        assertNotNull(originalTree.getItemById(EMPTYCHILDGROUP));

        NavTree runtimeTree = runtimeNavigationBuilder.buildRuntimeTree(originalTree, templates);

        assertNull(runtimeTree.getItemById(EMPTYONANONEMPTY));
        assertNull(runtimeTree.getItemById(EMPTY_GROUP));
        assertNull(runtimeTree.getItemById(EMPTYCHILDGROUP));

        assertEquals(1, runtimeTree.getRootItems().size());
        NavGroup notPrunedGroup = (NavGroup) runtimeTree.getItemById(NOT_PRUNED_GROUP);
        NavItem item = runtimeTree.getItemById(ITEM);
        assertNotNull(notPrunedGroup);
        assertNotNull(item);
        assertEquals(notPrunedGroup, item.getParent());
        assertEquals(1, notPrunedGroup.getChildren().size());
    }

    @Test
    public void removeItemWithoutTemplateTest() {
        NavTreeBuilder builder = new NavTreeBuilder();
        LayoutTemplate lt = new LayoutTemplate("lt1");

        final String ITEM_TO_REMOVE = "item2";

        builder.group(GROUP, GROUP, "", false);
        builder.item(ITEM, ITEM, "", false, NavWorkbenchCtx.perspective(lt.getName()));
        builder.item(ITEM_TO_REMOVE, ITEM_TO_REMOVE, "", false);
        builder.endGroup();

        NavTree originalTree = builder.build();

        assertNotNull(originalTree.getItemById(ITEM_TO_REMOVE));

        List<LayoutTemplate> templates = new ArrayList<>();
        templates.add(lt);
        NavTree runtimeTree = runtimeNavigationBuilder.buildRuntimeTree(originalTree, templates);

        assertEquals(1, runtimeTree.getRootItems().size());
        NavGroup group = (NavGroup) runtimeTree.getItemById(GROUP);
        assertNotNull(group);
        assertEquals(1, group.getChildren().size());
        assertNull(runtimeTree.getItemById(ITEM_TO_REMOVE));
        assertNotNull(runtimeTree.getItemById(ITEM));
    }

    @Test
    public void groupForOrphanItemsTest() {
        final String ORPHAN_ITEM = "orphanitem";

        NavTreeBuilder builder = new NavTreeBuilder();
        LayoutTemplate lt = new LayoutTemplate("lt1");
        LayoutTemplate lt2 = new LayoutTemplate(ORPHAN_ITEM);

        final String ITEM_TO_REMOVE = "item2";

        builder.group(GROUP, GROUP, "", false);
        builder.item(ITEM, ITEM, "", false, NavWorkbenchCtx.perspective(lt.getName()));
        builder.item(ITEM_TO_REMOVE, ITEM_TO_REMOVE, "", false);
        builder.endGroup();

        List<LayoutTemplate> templates = Arrays.asList(lt, lt2);
        NavTree originalTree = builder.build();
        assertNull(originalTree.getItemById(RuntimeNavigationBuilder.ORPHAN_GROUP_ID));
        assertEquals(1, originalTree.getRootItems().size());

        NavTree runtimeTree = runtimeNavigationBuilder.buildRuntimeTree(originalTree, templates);
        assertEquals(2, runtimeTree.getRootItems().size());

        NavGroup orphanItemsGroup = (NavGroup) runtimeTree.getItemById(RuntimeNavigationBuilder.ORPHAN_GROUP_ID);
        assertNotNull(orphanItemsGroup);
        assertEquals(1, orphanItemsGroup.getChildren().size());

        NavItem orphanItem = runtimeTree.getItemById(ORPHAN_ITEM);
        String resourceId = NavWorkbenchCtx.get(orphanItem).getResourceId();
        assertEquals(ORPHAN_ITEM, resourceId);
        assertEquals(orphanItemsGroup, orphanItem.getParent());
    }

    @Test
    public void treeForEmptyNavigationTest() {
        LayoutTemplate lt1 = new LayoutTemplate("lt1");
        LayoutTemplate lt2 = new LayoutTemplate("lt2");
        LayoutTemplate lt3 = new LayoutTemplate("lt3");

        List<LayoutTemplate> templates = Arrays.asList(lt1, lt2, lt3);
        NavTree runtimeTree = runtimeNavigationBuilder.build(Optional.empty(), templates);

        NavGroup orphanItemsGroup = (NavGroup) runtimeTree.getItemById(RuntimeNavigationBuilder.ORPHAN_GROUP_ID);
        assertNotNull(orphanItemsGroup);

        assertEquals(3, orphanItemsGroup.getChildren().size());
        assertTrue(containsLayoutTemplate(orphanItemsGroup, lt1));
        assertTrue(containsLayoutTemplate(orphanItemsGroup, lt2));
        assertTrue(containsLayoutTemplate(orphanItemsGroup, lt3));
    }

    private boolean containsLayoutTemplate(NavGroup group, LayoutTemplate lt) {
        return group.getChildren().stream()
                    .anyMatch(lt1 -> lt1.getName().equals(lt.getName()));
    }

}