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

package org.uberfire.workbench.model.menu.impl;

import org.uberfire.workbench.model.menu.MenuCustom;
import org.uberfire.workbench.model.menu.MenuGroup;
import org.uberfire.workbench.model.menu.MenuItemCommand;
import org.uberfire.workbench.model.menu.MenuItemPerspective;
import org.uberfire.workbench.model.menu.MenuItemPlain;
import org.uberfire.workbench.model.menu.MenuVisitor;
import org.uberfire.workbench.model.menu.Menus;

public abstract class BaseMenuVisitor implements MenuVisitor {

    @Override
    public boolean visitEnter( Menus menus ) {
        return true;
    }

    @Override
    public void visitLeave( Menus menus ) {

    }

    @Override
    public boolean visitEnter( MenuGroup menuGroup ) {
        return true;
    }

    @Override
    public void visitLeave( MenuGroup menuGroup ) {

    }

    @Override
    public void visit( MenuItemPlain menuItemPlain ) {

    }

    @Override
    public void visit( MenuItemCommand menuItemCommand ) {

    }

    @Override
    public void visit( MenuItemPerspective menuItemPerspective ) {

    }

    @Override
    public void visit( MenuCustom<?> menuCustom ) {

    }
}
