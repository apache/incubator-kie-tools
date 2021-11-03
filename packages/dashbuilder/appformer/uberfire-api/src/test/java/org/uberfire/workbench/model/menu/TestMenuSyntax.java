/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.workbench.model.menu;

/**
 *
 */
public class TestMenuSyntax {

    public static Object main(final String... args) {
        return MenuFactory
                .newContributedMenu("x")
                .contributeTo("xx")
                .submenu("xx")
                .menu("cc")
                .respondsWith(null)
                .endMenu()
                .endMenus()
                .endMenu().
                        newTopLevelMenu("")
                .submenu("")
                .menu("xx")
                .respondsWith(null)
                .endMenu()
                .menu("x")
                .submenu("xxx")
                .menu("xx")
                .respondsWith(null)
                .endMenu()
                .endMenus()
                .submenu("xxx")
                .menu("xx")
                .respondsWith(null)
                .endMenu()
                .endMenus()
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu("x")
                .respondsWith(null)
                .endMenu()
                .newTopLevelMenu("x")
                .submenu("x")
                .menu("x")
                .respondsWith(null)
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu("xx")
                .respondsWith(null)
                .endMenu()
                .newTopLevelMenu("xx")
                .withItems(null)
                .endMenu()
                .newContributedMenu("x")
                .contributeTo("xx")
                .submenu("xx")
                .menu("cc")
                .respondsWith(null)
                .endMenu()
                .endMenus()
                .endMenu()
                .build();
    }
}
