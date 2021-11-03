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
package org.dashbuilder.dsl.model;

import org.dashbuilder.navigation.NavTree;

public class Navigation {

    private NavTree navTree;

    private Navigation(NavTree navTree) {
        this.navTree = navTree;
    }

    public static Navigation of(NavTree navTree) {
        return new Navigation(navTree);
    }

    public NavTree getNavTree() {
        return navTree;
    }

}