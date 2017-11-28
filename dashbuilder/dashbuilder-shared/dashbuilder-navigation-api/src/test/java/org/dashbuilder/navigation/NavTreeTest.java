/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation;

import org.assertj.core.api.Assertions;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class NavTreeTest {

    public static final String ITEM_HOME_ID = "home";
    public static final String ITEM_ADMIN_ID = "admin";
    public static final String ITEM_SECURITY_ID = "security";
    public static final String ITEM_DATASETS_ID = "datasets";
    public static final String NONEXISTENT_ITEM_ID = "item with this id does not exist";

    NavTree tree;

    @Before
    public void setUp() {
        tree = new NavTreeBuilder()
                .item(ITEM_HOME_ID, "Home", null, false)
                .divider()
                .group(ITEM_ADMIN_ID, "Administration", null, false)
                .item(ITEM_SECURITY_ID, "Security", null, false)
                .item(ITEM_DATASETS_ID, "Data sets", null, false)
                .build();
    }

    @Test
    public void testTreeStructure() {
        List<NavItem> rootNavItems = tree.getRootItems();
        assertEquals(rootNavItems.size(), 3);

        NavItem home = tree.getItemById(ITEM_HOME_ID);
        assertTrue(home instanceof NavItem);
        assertEquals(home.getId(), ITEM_HOME_ID);
        assertEquals(home.getName(), "Home");
        assertEquals(home.isModifiable(), false);
        assertNull(home.getParent());

        NavItem admin = tree.getItemById(ITEM_ADMIN_ID);
        assertTrue(admin instanceof NavGroup);
        assertEquals(((NavGroup) admin).getChildren().size(), 2);

        NavItem security = tree.getItemById(ITEM_SECURITY_ID);
        assertTrue(security instanceof NavItem);
        assertEquals(security.getParent(), admin);
    }

    @Test
    public void testDeleteItem() {
        tree.deleteItem(ITEM_HOME_ID);
        List<NavItem> rootNavItems = tree.getRootItems();
        assertEquals(rootNavItems.size(), 2);

        tree.deleteItem(ITEM_DATASETS_ID);
        NavItem admin = tree.getItemById(ITEM_ADMIN_ID);
        assertEquals(((NavGroup) admin).getChildren().size(), 1);
    }


    @Test(expected = RuntimeException.class)
    public void testParentNotFound() {
        tree.moveItem("none", "parent");
    }

    @Test(expected = RuntimeException.class)
    public void testAvoidLoops() {
        tree.moveItem(ITEM_HOME_ID, "home");
    }

    @Test
    public void testItemContext() {
        tree.setItemContext(ITEM_HOME_ID, "p1");
        NavItem home = tree.getItemById(ITEM_HOME_ID);

        assertTrue(home instanceof NavItem);
        assertEquals(home.getContext(), "p1");
    }

    @Test
    public void testSearchItems() {
        NavItemContext ctx1 = NavItemContext.create().setProperty("p1", "v1").setProperty("p2", "v2");
        NavItemContext ctx2 = NavItemContext.create().setProperty("p1", "v1").setProperty("p3", "v3");
        NavItemContext ctx3 = NavItemContext.create().setProperty("p1", "v1").setProperty("p2", "v2");

        tree.setItemContext(ITEM_HOME_ID, ctx1.toString());
        tree.setItemContext(ITEM_DATASETS_ID, ctx2.toString());

        List<NavItem> navItems = tree.searchItems(ctx3);
        assertEquals(navItems.size(), 1);
        assertEquals(navItems.get(0).getId(), ITEM_HOME_ID);
    }

    @Test(expected = RuntimeException.class)
    public void testMoveUpNotAllowed() {
        tree.moveItemUp(ITEM_SECURITY_ID);
        tree.moveItemDown(ITEM_DATASETS_ID);
    }

    @Test(expected = RuntimeException.class)
    public void testMoveDownNotAllowed() {
        tree.moveItemDown(ITEM_DATASETS_ID);
    }

    @Test
    public void testMoveUpAndDown() {
        NavGroup admin = (NavGroup) tree.getItemById(ITEM_ADMIN_ID);
        List<NavItem> navItems = admin.getChildren();
        assertEquals(navItems.get(0).getId(), ITEM_SECURITY_ID);
        assertEquals(navItems.get(1).getId(), ITEM_DATASETS_ID);

        tree.moveItemUp(ITEM_DATASETS_ID);
        assertEquals(navItems.get(0).getId(), ITEM_DATASETS_ID);
        assertEquals(navItems.get(1).getId(), ITEM_SECURITY_ID);

        tree.moveItemDown(ITEM_DATASETS_ID);
        assertEquals(navItems.get(0).getId(), ITEM_SECURITY_ID);
        assertEquals(navItems.get(1).getId(), ITEM_DATASETS_ID);

        tree.moveItemFirst(ITEM_DATASETS_ID);
        assertEquals(navItems.get(0).getId(), ITEM_DATASETS_ID);
        assertEquals(navItems.get(1).getId(), ITEM_SECURITY_ID);

        tree.moveItemLast(ITEM_DATASETS_ID);
        assertEquals(navItems.get(0).getId(), ITEM_SECURITY_ID);
        assertEquals(navItems.get(1).getId(), ITEM_DATASETS_ID);
    }

    @Test
    public void testSubtreeCreation() {
        NavTree subtree = tree.getItemAsTree(ITEM_ADMIN_ID);
        assertEquals(subtree.getRootItems().size(), 2);
        subtree.getRootItems().forEach(i -> assertNull(i.getParent()));
    }

    @Test
    public void testCloneTree() {
        NavTree clone = tree.cloneTree();
        clone.getRootItems().forEach(i -> assertNull(i.getParent()));
    }

    @Test
    public void setItemName_worksWhenItemPresent() {
        NavItem modifiedItem = tree.setItemName(ITEM_HOME_ID, "NEW NAME!");

        assertEquals("NEW NAME!", modifiedItem.getName());
        assertEquals("NEW NAME!", tree.getItemById(ITEM_HOME_ID).getName());
    }

    @Test
    public void setItemModifiable_worksWhenItemPresent() {
        assertFalse(tree.getItemById(ITEM_HOME_ID).isModifiable());

        NavItem modifiedItem = tree.setItemModifiable(ITEM_HOME_ID, true);

        assertTrue(modifiedItem.isModifiable());
        assertTrue(tree.getItemById(ITEM_HOME_ID).isModifiable());
    }

    @Test
    public void setItemDescription_worksWhenItemPresent() {
        NavItem modifiedItem = tree.setItemDescription(ITEM_HOME_ID, "new description");

        assertEquals("new description", modifiedItem.getDescription());
        assertEquals("new description", tree.getItemById(ITEM_HOME_ID).getDescription());
    }

    @Test(expected = RuntimeException.class)
    public void setItemName_throwsExceptionWhenItemNotPresent() {
        tree.setItemName(NONEXISTENT_ITEM_ID, "anything");
    }

    @Test(expected = RuntimeException.class)
    public void setItemModifiable_throwsExceptionWhenItemNotPresent() {
        tree.setItemModifiable(NONEXISTENT_ITEM_ID, true);
    }

    @Test(expected = RuntimeException.class)
    public void setItemDescription_throwsExceptionWhenItemNotPresent() {
        tree.setItemDescription(NONEXISTENT_ITEM_ID, "doesn't matter");
    }

    @Test
    public void addGroupTest() {
        final String id = "id", name = "name", description = "desc", parentId = ITEM_ADMIN_ID;
        final boolean modifiable = false;
        tree.addGroup(id, name, description, parentId, modifiable);

        NavItem newGroup = tree.getItemById(id);
        assertEquals(id, newGroup.getId());
        assertEquals(name, newGroup.getName());
        assertEquals(parentId, newGroup.getParent().getId());
        assertEquals(description, newGroup.getDescription());
        assertFalse(newGroup.isModifiable());
    }

    @Test
    public void addGroupThrowsException_whenParentIsNotGroup() {
        String idOfParentWhichIsNotGroup = ITEM_SECURITY_ID;
        try {
            tree.addGroup(null, null, null, idOfParentWhichIsNotGroup, true);
            Assertions.fail("Exception should be thrown when using something else as parent than NavGroup");
        } catch (RuntimeException e) {
            assertEquals("Parent '" + idOfParentWhichIsNotGroup + "' is not a group", e.getMessage());
        }
    }

    @Test
    public void addItemTest() {
        final String id = "id", name = "name", description = "desc", parentId = ITEM_ADMIN_ID, context = "a=1";
        final boolean modifiable = false;
        tree.addItem(id, name, description, parentId, modifiable, context);

        NavItem item = tree.getItemById(id);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(parentId, item.getParent().getId());
        assertEquals(description, item.getDescription());
        assertFalse(item.isModifiable());
        assertEquals(context, item.getContext());
    }

    @Test
    public void addItemShouldThrowException_whenParentDoesNotExist() {
        try {
            tree.addItem(null, null, null, NONEXISTENT_ITEM_ID, true, null);
            Assertions.fail("Exception should be thrown when parent with given ID does not exist");
        } catch (RuntimeException e) {
            assertEquals("Parent '" + NONEXISTENT_ITEM_ID + "' not found", e.getMessage());
        }
    }

    @Test
    public void moveItemTest() {
        final String newGroupId = "ngi";
        tree.addGroup(newGroupId, "name", "desc", ITEM_ADMIN_ID, true);
        tree.moveItem(ITEM_SECURITY_ID, newGroupId);

        assertEquals(newGroupId, tree.getItemById(ITEM_SECURITY_ID).getParent().getId());
    }

    @Test
    public void moveItemShouldThrowException_whenParentDoesNotExist() {
        try {
            tree.moveItem(ITEM_SECURITY_ID, NONEXISTENT_ITEM_ID);
            Assertions.fail("Exception should be thrown when parent with given ID does not exist");
        } catch (RuntimeException e) {
            assertEquals("Parent not found: " + NONEXISTENT_ITEM_ID, e.getMessage());
        }
    }

    @Test
    public void moveItemShouldThrowException_whenParentIdSameAsItemId() {
        final String sameId = ITEM_ADMIN_ID;
        try {
            tree.moveItem(sameId, sameId);
            Assertions.fail("Exception should be thrown when parentId same as item Id");
        } catch (RuntimeException e) {
            assertEquals("The parent can't be the item itself: " + sameId, e.getMessage());
        }
    }
}
