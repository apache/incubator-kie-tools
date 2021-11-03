/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.dashbuilder.navigation;

import org.dashbuilder.json.JsonObject;
import org.dashbuilder.navigation.impl.NavTreeBuilder;
import org.dashbuilder.navigation.json.NavTreeJSONMarshaller;
import org.junit.Test;

import static org.junit.Assert.*;

public class NavTreeJsonTest {

    public static final NavTree NAV_TREE = new NavTreeBuilder()
            .item("1", "name1", "desc1", true, "ctx1")
            .item("2", "name2", "desc2", false, "ctx2")
            .divider()
            .group("3", "name3", "desc3", true)
            .item("4", "name4", "desc4", true, "ctx4")
            .build();

    @Test
    public void testNavTreeMarshalling() {
        NavTreeJSONMarshaller jsonMarshaller = NavTreeJSONMarshaller.get();
        JsonObject _jsonObj = jsonMarshaller.toJson(NAV_TREE);
        assertNotNull(_jsonObj.toString());

        NavTree navTree = jsonMarshaller.fromJson(_jsonObj);
        assertEquals(navTree.getRootItems().size(), 4);

        NavItem navItem = navTree.getItemById("1");
        assertNotNull(navItem);
        assertEquals(navItem.getType(), NavItem.Type.ITEM);
        assertEquals(navItem.getName(), "name1");
        assertEquals(navItem.getDescription(), "desc1");
        assertEquals(navItem.isModifiable(), true);
        assertEquals(navItem.getContext(), "ctx1");

        navItem = navTree.getItemById("2");
        assertNotNull(navItem);
        assertEquals(navItem.getType(), NavItem.Type.ITEM);
        assertEquals(navItem.getName(), "name2");
        assertEquals(navItem.getDescription(), "desc2");
        assertEquals(navItem.isModifiable(), false);
        assertEquals(navItem.getContext(), "ctx2");

        navItem = navTree.getItemById("3");
        assertNotNull(navItem);
        assertEquals(navItem.getType(), NavItem.Type.GROUP);
        assertEquals(navItem.getName(), "name3");
        assertEquals(navItem.getDescription(), "desc3");
        assertEquals(navItem.isModifiable(), true);

        navItem = navTree.getItemById("4");
        assertNotNull(navItem);
        assertEquals(navItem.getType(), NavItem.Type.ITEM);
        assertEquals(navItem.getName(), "name4");
        assertEquals(navItem.getDescription(), "desc4");
        assertEquals(navItem.isModifiable(), true);
        assertEquals(navItem.getContext(), "ctx4");
        assertEquals(navItem.getParent(), navTree.getItemById("3"));
    }
}
