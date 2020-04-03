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

package org.kie.workbench.common.dmn.client.docks.navigator;

import java.util.Collections;
import java.util.TreeSet;

import org.junit.Test;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type;
import org.uberfire.mvp.Command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.CONTEXT;
import static org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem.Type.ITEM;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DecisionNavigatorItemTest {

    @Test
    public void testEqualsWhenItemsAreEqual() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123");

        assertEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentUUIDs() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("456");

        assertNotEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentParentUUIDs() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123", null, null, null, "456");
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123", null, null, null, "789");

        assertNotEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentTypes() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123", null, ITEM, null, null);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123", null, CONTEXT, null, null);

        assertNotEquals(item1, item2);
    }

    @Test
    public void testEqualsWhenItemsHaveDifferentLabels() {

        final DecisionNavigatorItem item1 = new DecisionNavigatorItem("123", "Node1", null, null, null);
        final DecisionNavigatorItem item2 = new DecisionNavigatorItem("123", "Node0", null, null, null);

        assertNotEquals(item1, item2);
    }

    @Test
    public void testOnClick() {

        final Command command = mock(Command.class);
        final DecisionNavigatorItem item = new DecisionNavigatorItem("uuid", "label", ITEM, command, null);

        item.onClick();

        verify(command).execute();
    }

    @Test
    public void testRemoveChild() {

        final DecisionNavigatorItem item = new DecisionNavigatorItem("item");
        final DecisionNavigatorItem child = new DecisionNavigatorItem("child");

        item.getChildren().add(child);
        item.removeChild(child);

        assertEquals(Collections.emptySet(), item.getChildren());
    }

    @Test
    public void testAddChild() {

        final DecisionNavigatorItem item = new DecisionNavigatorItem("item");
        final DecisionNavigatorItem child = new DecisionNavigatorItem("child");
        final TreeSet<DecisionNavigatorItem> expectedChildren = new TreeSet<DecisionNavigatorItem>() {{
            add(child);
        }};

        item.addChild(child);
        item.addChild(child);

        assertEquals(expectedChildren, item.getChildren());
    }

    @Test
    public void testCompareToWhenObjectIsNotADecisionNavigatorItem() {

        final DecisionNavigatorItem item = new DecisionNavigatorItem("123");
        final Object object = null;

        final int result = item.compareTo(object);

        assertTrue(result > 0);
    }

    @Test
    public void testCompareToWhenItemAndObjectAreEqual() {

        final DecisionNavigatorItem item = new DecisionNavigatorItem("123");
        final Object object = new DecisionNavigatorItem("123");

        final int result = item.compareTo(object);

        assertEquals(0, result);
    }

    @Test
    public void testCompareToWhenItemOrderingNameIsGreaterThanObjectOrderingName() {

        final DecisionNavigatorItem item = new DecisionNavigatorItem("123", "Hat", null, null, null);
        final Object object = new DecisionNavigatorItem("456", "Red", null, null, null);

        final int result = item.compareTo(object);

        assertTrue(result < 0);
    }

    @Test
    public void testCompareToWhenItemOrderingNameIsLessThanObjectOrderingName() {

        final DecisionNavigatorItem item = new DecisionNavigatorItem("123", "Red", null, null, null);
        final Object object = new DecisionNavigatorItem("456", "Hat", null, null, null);

        final int result = item.compareTo(object);

        assertTrue(result > 0);
    }

    @Test
    public void testTypeEnumWhenRetrievingByExistingItemClassName() {
        assertThat(Type.ofExpressionNodeClassName("InputData")).isEqualTo(Type.INPUT_DATA);
    }

    @Test
    public void testTypeEnumWhenRetrievingByNotExistingItemClassName() {
        assertThat(Type.ofExpressionNodeClassName("NOT_EXISTING")).isEqualTo(ITEM);
    }

    @Test
    public void testTypeEnumWhenRetrievingByEmptyClassName() {
        assertThat(Type.ofExpressionNodeClassName("")).isEqualTo(ITEM);
    }

    @Test
    public void testTypeEnumWhenRetrievingByNullClassName() {
        assertThat(Type.ofExpressionNodeClassName(null)).isEqualTo(ITEM);
    }
}
