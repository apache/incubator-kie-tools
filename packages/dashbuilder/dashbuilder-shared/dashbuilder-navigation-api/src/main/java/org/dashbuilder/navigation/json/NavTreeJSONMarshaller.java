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
package org.dashbuilder.navigation.json;

import java.util.List;

import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonException;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.navigation.NavFactory;
import org.dashbuilder.navigation.NavGroup;
import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;

public class NavTreeJSONMarshaller {

    private static final String NAV_ITEM_ID = "id";
    private static final String NAV_ITEM_TYPE = "type";
    private static final String NAV_ITEM_NAME = "name";
    private static final String NAV_ITEM_DESC = "description";
    private static final String NAV_ITEM_MODIF = "modifiable";
    private static final String NAV_ITEM_CTX = "context";
    private static final String NAV_TREE_ROOT_ITEMS = "root_items";
    private static final String NAV_GROUP_CHILDREN = "children";

    private static NavTreeJSONMarshaller SINGLETON = new NavTreeJSONMarshaller();

    public static NavTreeJSONMarshaller get() {
        return SINGLETON;
    }

    // To Json

    public JsonObject toJson(NavTree navTree) throws JsonException {
        JsonObject json = Json.createObject();
        if (navTree != null) {
            json.put(NAV_TREE_ROOT_ITEMS, toJson(navTree.getRootItems()));
        }
        return json;
    }

    public JsonObject toJson(NavItem navItem) throws JsonException {
        JsonObject json = Json.createObject();
        if (navItem != null) {
            json.put(NAV_ITEM_ID, navItem.getId());
            json.put(NAV_ITEM_TYPE, navItem.getType().toString());

            if (navItem.getType() != NavItem.Type.DIVIDER) {
                json.put(NAV_ITEM_NAME, navItem.getName());
                json.put(NAV_ITEM_DESC, navItem.getDescription());
                json.put(NAV_ITEM_MODIF, navItem.isModifiable());
                json.put(NAV_ITEM_CTX, navItem.getContext());

                if (navItem.getType() == NavItem.Type.GROUP) {
                    json.put(NAV_GROUP_CHILDREN, toJson(((NavGroup) navItem).getChildren()));

                }
            }
        }
        return json;
    }

    public JsonArray toJson(List<NavItem> navItemList) throws JsonException {
        JsonArray json = Json.createArray();
        if (navItemList != null) {
            for (int i=0; i<navItemList.size(); i++) {
                NavItem navItem = navItemList.get(i);
                json.set(i, toJson(navItem));
            }
        }
        return json;
    }

    // From Json

    public NavTree fromJson(String jsonString) throws JsonException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        JsonObject json = Json.parse(jsonString);
        return fromJson(json);
    }

    public NavTree fromJson(JsonObject json) throws JsonException {
        if (json == null) {
            return null;
        }

        NavTree navTree = NavFactory.get().createNavTree();
        for (int i = 0; i < json.size(); i++) {
            JsonArray rootItemArray = json.getArray(NAV_TREE_ROOT_ITEMS);
            parseNavItemArray(rootItemArray, navTree.getRootItems(), null);
        }
        return navTree;
    }

    private void parseNavItemArray(JsonArray json, List<NavItem> targetList, NavGroup parent) throws JsonException {
        if (json != null) {
            for (int i = 0; i < json.length(); i++) {
                NavItem navItem = parseNavItem(json.getObject(i));
                if (navItem != null) {
                    navItem.setParent(parent);
                    targetList.add(navItem);
                }
            }
        }
    }

    private NavItem parseNavItem(JsonObject json) throws JsonException {
        if (json == null) {
            return null;
        }
        NavItem navItem = null;
        String type = json.getString(NAV_ITEM_TYPE);
        if (type == null) {
            throw new RuntimeException("Nav item type not specified");
        }

        if (NavItem.Type.DIVIDER.toString().equals(type)) {
            navItem = NavFactory.get().createDivider();
        } else if (NavItem.Type.GROUP.toString().equals(type)) {
            navItem = NavFactory.get().createNavGroup();
        } else {
            navItem = NavFactory.get().createNavItem();
        }
        String id = json.getString(NAV_ITEM_ID);
        String name = json.getString(NAV_ITEM_NAME);
        String desc = json.getString(NAV_ITEM_DESC);
        String modif = json.getString(NAV_ITEM_MODIF);
        String ctx = json.getString(NAV_ITEM_CTX);

        navItem.setId(id);
        navItem.setName(name);
        navItem.setDescription(desc);
        navItem.setModifiable(modif != null ? Boolean.parseBoolean(modif) : true);
        navItem.setContext(ctx);

        if (NavItem.Type.GROUP.toString().equals(type)) {
            JsonArray childrenArray = json.getArray(NAV_GROUP_CHILDREN);
            NavGroup navGroup = (NavGroup) navItem;
            parseNavItemArray(childrenArray, navGroup.getChildren(), navGroup);
        }
        return navItem;
    }
}
