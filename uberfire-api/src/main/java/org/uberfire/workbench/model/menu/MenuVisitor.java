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
 * Visitor interface for implementing arbitrary operations over menus. For example, a visitor could filter a menu tree
 * for items that the current user has permission to see; it could build widgets in a particular view module; it could
 * simply dump the menu structure to a string.
 */
public interface MenuVisitor {

    /**
     * Visits the top-level menu container. This is the first method invoked when visiting a complete menu tree.
     *
     * @param menus
     *            the top-level container of the menus that will be visited.
     * @return true if the visitor would like to continue down the tree and visit all children; false if it wants to
     *         skip this node. Since this is the root node, returning false from this call will result in no more calls
     *         to the visitor.
     */
    boolean visitEnter( Menus menus );

    /**
     * Ends the visit of the top-level menu container. This is the last method invoked when visiting a complete menu tree.
     * <p>
     * <i>Note that this method is not called if {@link #visitEnter(Menus)} returns false.</i>
     *
     * @param menus the top-level container of the menus that will be visited.
     */
    void visitLeave( Menus menus );

    /**
     * Visits a menu group in the tree of menus. A menu group has zero or more MenuItem children.
     *
     * @param menuGroup
     *            the menu group to visit.
     * @return true if the visitor would like to visit all children of this node; false if it wants to skip this node. A
     *         visitor that returns false from this node will not receive any further calls for this node or its
     *         descendants. In particular, there will be no corresponding {@link #visitLeave(MenuGroup)} call for this
     *         node.
     */
    boolean visitEnter( MenuGroup menuGroup );

    /**
     * Visits a menu group in the tree of menus. All descendants of the given menu group have been visited before this method is called.
     * <p>
     * <i>Note that this method is not called for a MenuGroup where the {@link #visitEnter(MenuGroup)} method returned false.</i>
     *
     * @param menuGroup the menu group to leave.
     */
    void visitLeave( MenuGroup menuGroup );

    /**
     * Visits a plain menu item in the tree.
     *
     * @param menuItemPlain the plain menu item to visit.
     */
    void visit( MenuItemPlain menuItemPlain );

    /**
     * Visits a menu item that has an associated command.
     *
     * @param menuItemCommand the command menu item to visit.
     */
    void visit( MenuItemCommand menuItemCommand );

    /**
     * Visits a menu item that has an associated perspective.
     *
     * @param menuItemPerspective the command menu item to visit.
     */
    void visit( MenuItemPerspective menuItemPerspective );

    /**
     * Visits a custom menu item in the menu tree.
     *
     * @param menuCustom the custom (application provides the widget) menu item to visit.
     */
    void visit( MenuCustom<?> menuCustom );
}
