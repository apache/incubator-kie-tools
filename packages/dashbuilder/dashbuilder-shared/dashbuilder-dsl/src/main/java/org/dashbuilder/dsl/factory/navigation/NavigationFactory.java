/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.factory.navigation;

import org.dashbuilder.dsl.model.Navigation;
import org.dashbuilder.dsl.model.NavigationGroup;
import org.dashbuilder.dsl.model.NavigationItem;
import org.dashbuilder.dsl.model.Page;

public class NavigationFactory {

    private static final Navigation EMPTY_NAVIGATION = NavigationBuilder.newBuilder().build();

    private NavigationFactory() {
        // empty
    }

    public static Navigation emptyNavigation() {
        return EMPTY_NAVIGATION;
    }

    public static Navigation navigation(NavigationGroup... rootGroups) {
        return NavigationBuilder.newBuilder(rootGroups).build();
    }

    public static NavigationGroup group(String name, NavigationItem... items) {
        return NavigationGroupBuilder.newBuilder(name, items).build();
    }

    public static NavigationItem item(Page page) {
        return NavigationItemBuilder.newBuilder(page).build();
    }

}