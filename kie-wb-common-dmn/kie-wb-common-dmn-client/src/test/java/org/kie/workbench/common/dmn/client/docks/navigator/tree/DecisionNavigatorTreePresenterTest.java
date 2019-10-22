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

package org.kie.workbench.common.dmn.client.docks.navigator.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ITEM;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ROOT;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DecisionNavigatorTreePresenterTest {

    @Mock
    private DecisionNavigatorTreePresenter.View view;

    @Mock
    private Map<String, DecisionNavigatorItem> indexedItems;

    private DecisionNavigatorTreePresenter presenter;

    @Before
    public void setup() {
        presenter = spy(new DecisionNavigatorTreePresenter(view));
        doReturn(indexedItems).when(presenter).getIndexedItems();
    }

    @Test
    public void testSetup() {

        presenter.setup();

        verify(view).init(presenter);
    }

    @Test
    public void testSetupItems() {

        final ArrayList<DecisionNavigatorItem> items = new ArrayList<>();

        doNothing().when(presenter).index(items);

        presenter.setupItems(items);

        verify(presenter).index(items);
        verify(view).clean();
        verify(view).setup(items);
    }

    @Test
    public void testAddOrUpdateItemWhenItemIsNotChanged() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(true).when(presenter).hasParent(item);
        doReturn(false).when(presenter).isChanged(item);

        presenter.addOrUpdateItem(item);

        verify(view, never()).hasItem(any());
        verify(presenter, never()).updateItem(item);
        verify(presenter, never()).addItem(item);
    }

    @Test
    public void testAddOrUpdateItemWhenItemDoesNotHaveParent() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        doReturn(false).when(presenter).hasParent(item);
        doReturn(true).when(presenter).isChanged(item);

        presenter.addOrUpdateItem(item);

        verify(view, never()).hasItem(any());
        verify(presenter, never()).updateItem(item);
        verify(presenter, never()).addItem(item);
    }

    @Test
    public void testAddOrUpdateItemWhenViewHasTheItem() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        when(view.hasItem(item)).thenReturn(true);
        doReturn(true).when(presenter).hasParent(item);
        doReturn(true).when(presenter).isChanged(item);
        doNothing().when(presenter).updateItem(any());

        presenter.addOrUpdateItem(item);

        verify(presenter).updateItem(item);
        verify(presenter, never()).addItem(any());
    }

    @Test
    public void testAddOrUpdateItemWhenViewDoesNotHaveTheItem() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        when(view.hasItem(item)).thenReturn(false);
        doReturn(true).when(presenter).hasParent(item);
        doReturn(true).when(presenter).isChanged(item);
        doNothing().when(presenter).addItem(any());

        presenter.addOrUpdateItem(item);

        verify(presenter).addItem(item);
        verify(presenter, never()).updateItem(any());
    }

    @Test
    public void testHasParentWhenItemHasParent() {

        final String parentUUID = "parentUUID";
        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem parent = mock(DecisionNavigatorItem.class);
        final Map<String, DecisionNavigatorItem> indexedItems = new HashMap<String, DecisionNavigatorItem>() {{
            put(parentUUID, parent);
        }};

        when(item.getParentUUID()).thenReturn(parentUUID);
        doReturn(indexedItems).when(presenter).getIndexedItems();

        final boolean hasParent = presenter.hasParent(item);

        assertTrue(hasParent);
    }

    @Test
    public void testHasParentWhenItemDoesNotHaveParent() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final Map<String, DecisionNavigatorItem> indexedItems = new HashMap<>();

        doReturn(indexedItems).when(presenter).getIndexedItems();

        final boolean hasParent = presenter.hasParent(item);

        assertFalse(hasParent);
    }

    @Test
    public void testIsChangedWhenItemIsUpdated() {

        final String uuid = "uuid";
        final DecisionNavigatorItem item1 = new DecisionNavigatorItem(uuid, "Node0", null, null, null);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem(uuid, "NodeJS", null, null, null);
        final Map<String, DecisionNavigatorItem> indexedItems = new HashMap<String, DecisionNavigatorItem>() {{
            put(uuid, item2);
        }};

        doReturn(indexedItems).when(presenter).getIndexedItems();

        final boolean isChanged = presenter.isChanged(item1);

        assertTrue(isChanged);
    }

    @Test
    public void testIsChangedWhenItemIsNotUpdated() {

        final String uuid = "uuid";
        final DecisionNavigatorItem item1 = new DecisionNavigatorItem(uuid, "Node0", null, null, null);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem(uuid, "Node0", null, null, null);
        final Map<String, DecisionNavigatorItem> indexedItems = new HashMap<String, DecisionNavigatorItem>() {{
            put(uuid, item2);
        }};

        doReturn(indexedItems).when(presenter).getIndexedItems();

        final boolean isChanged = presenter.isChanged(item1);

        assertFalse(isChanged);
    }

    @Test
    public void testAddItem() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem nextItem = mock(DecisionNavigatorItem.class);

        doReturn(nextItem).when(presenter).nextItem(item);

        presenter.addItem(item);

        verify(presenter).index(item);
        verify(view).addItem(item, nextItem);
    }

    @Test
    public void testUpdateItem() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem nextItem = mock(DecisionNavigatorItem.class);

        doReturn(nextItem).when(presenter).nextItem(item);

        presenter.updateItem(item);

        verify(presenter).index(item);
        verify(view).update(item, nextItem);
    }

    @Test
    public void testRemove() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);

        presenter.remove(item);

        verify(presenter).unIndex(item);
        verify(view).remove(item);
    }

    @Test
    public void testRemoveAllItems() {

        presenter.removeAllItems();

        verify(view).clean();
        verify(indexedItems).clear();
    }

    @Test
    public void testGetActiveParent() {

        final DecisionNavigatorItem expectedItem = mock(DecisionNavigatorItem.class);
        final String uuid = "uuid";

        when(indexedItems.get(uuid)).thenReturn(expectedItem);
        presenter.setActiveParentUUID(uuid);

        final DecisionNavigatorItem actualItem = presenter.getActiveParent();

        assertEquals(expectedItem, actualItem);
    }

    @Test
    public void testSelectItem() {

        final String uuid = "uuid";

        presenter.selectItem(uuid);

        verify(view).select(uuid);
    }

    @Test
    public void testDeselectItem() {

        presenter.deselectItem();

        verify(view).deselect();
    }

    @Test
    public void testNextItem() {

        final String parentUUID = "parentUUID";
        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("item1", "AAA", null, null, parentUUID);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("item2", "BBB", null, null, parentUUID);
        final DecisionNavigatorItem item3 = new DecisionNavigatorItem("item3", "CCC", null, null, parentUUID);
        final DecisionNavigatorItem parent = mock(DecisionNavigatorItem.class);
        final TreeSet<DecisionNavigatorItem> children = spy(asTreeSet(item1, item2, item3));

        when(parent.getChildren()).thenReturn(children);
        when(indexedItems.get(parentUUID)).thenReturn(parent);
        doReturn(children).when(parent).getChildren();

        assertEquals(item3, presenter.nextItem(item2));
    }

    @Test
    public void testListIndex() {

        final DecisionNavigatorItem item1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item2 = mock(DecisionNavigatorItem.class);
        final List<DecisionNavigatorItem> items = Arrays.asList(item1, item2);

        presenter.index(items);

        verify(presenter).index(item1);
        verify(presenter).index(item2);
    }

    @Test
    public void testItemIndex() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem child1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem child2 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem parent = mock(DecisionNavigatorItem.class);
        final TreeSet<DecisionNavigatorItem> children = asTreeSet(child1, child2);
        final String uuid = "uuid";

        doReturn(Optional.of(parent)).when(presenter).parent(item);
        when(item.getUUID()).thenReturn(uuid);
        when(item.getChildren()).thenReturn(children);

        presenter.index(item);

        verify(parent).addChild(item);
        verify(indexedItems).put(uuid, item);
        verify(presenter).index(children);
    }

    @Test
    public void testUnIndex() {

        final DecisionNavigatorItem item = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem parent = mock(DecisionNavigatorItem.class);
        final String uuid = "uuid";

        doReturn(Optional.of(parent)).when(presenter).parent(item);
        when(item.getUUID()).thenReturn(uuid);

        presenter.unIndex(item);

        verify(parent).removeChild(item);
        verify(indexedItems).remove(uuid);
    }

    @Test
    public void testFindRoot() {

        final DecisionNavigatorItem expectedRoot = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item1 = mock(DecisionNavigatorItem.class);
        final DecisionNavigatorItem item2 = mock(DecisionNavigatorItem.class);
        final List<DecisionNavigatorItem> values = Arrays.asList(expectedRoot, item1, item2);

        when(expectedRoot.getType()).thenReturn(ROOT);
        when(item1.getType()).thenReturn(ITEM);
        when(item2.getType()).thenReturn(DECISION_TABLE);
        when(indexedItems.values()).thenReturn(values);

        final DecisionNavigatorItem actualRoot = presenter.findRoot();

        assertEquals(expectedRoot, actualRoot);
    }

    private TreeSet<DecisionNavigatorItem> asTreeSet(final DecisionNavigatorItem... items) {
        return new TreeSet<DecisionNavigatorItem>() {{
            addAll(Arrays.asList(items));
        }};
    }
}
