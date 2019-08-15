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
package org.dashbuilder.client.navigation;

import java.util.List;

import org.dashbuilder.navigation.NavItem;
import org.dashbuilder.navigation.NavTree;
import org.uberfire.mvp.Command;

public interface NavigationManager {

    void init(Command afterInit);

    void setDefaultNavTree(NavTree navTree);

    NavTree getDefaultNavTree();

    NavTree getNavTree();

    boolean hasNavTree();

    void saveNavTree(NavTree navTree, Command afterSave);

    NavTree secure(NavTree navTree, boolean removeEmptyGroups);

    void secure(List<NavItem> itemList, boolean removeEmptyGroups);

    void navItemClicked(NavItem navItem);

    void update(NavTree navTree);
}
