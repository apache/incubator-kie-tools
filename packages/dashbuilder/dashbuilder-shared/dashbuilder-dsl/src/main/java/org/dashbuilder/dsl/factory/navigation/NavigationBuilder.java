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
import org.dashbuilder.navigation.NavTree;
import org.dashbuilder.navigation.impl.NavTreeImpl;

public class NavigationBuilder {

    private NavTree navTree;

    private NavigationBuilder(NavTree navTree) {
        this.navTree = navTree;
    }

    public static NavigationBuilder newBuilder(NavigationGroup... groups) {
        NavTree tree = new NavTreeImpl(NavigationGroupBuilder.newBuilder("Top Group", groups).build().getNavGroup());
        return new NavigationBuilder(tree);
    }

    public Navigation build() {
        return Navigation.of(this.navTree);
    }

    static NavigationBuilder newBuilder() {
        NavTree tree = new NavTreeImpl();
        return new NavigationBuilder(tree);
    }

}