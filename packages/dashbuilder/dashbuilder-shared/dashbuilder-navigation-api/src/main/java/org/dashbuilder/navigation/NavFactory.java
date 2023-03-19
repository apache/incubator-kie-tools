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

import org.dashbuilder.navigation.impl.NavFactoryImpl;

/**
 * A factory interface for the creation of {@link NavTree} and {@link NavItem} instances.
 */
public interface NavFactory {

    NavFactory[] _instance = new NavFactory[] {new NavFactoryImpl()};

    static NavFactory get() {
        return _instance[0];
    }

    static void set(NavFactory factory) {
        _instance[0] = factory;
    }

    NavTree createNavTree(NavItem navItem);

    NavTree createNavTree();

    NavGroup createNavGroup();

    NavGroup createNavGroup(NavTree navTree);

    NavItem createNavItem();

    NavDivider createDivider();
}
