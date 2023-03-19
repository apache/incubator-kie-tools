/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.navigation.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface NavigationConstants extends Messages {

    NavigationConstants INSTANCE = GWT.create(NavigationConstants.class);

    String newItem(String itemName);

    String newItemName(String itemName);

    String itemMenuTitle();

    String editItem();

    String deleteItem();

    String pageIconTitle();

    String perspectiveIconTitle();

    String moveUp();

    String moveDown();

    String moveFirst();

    String moveLast();

    String gotoItem(String itemName);

    String saveChanges();

    String save();

    String cancel();

    String navMenubarDragComponent();

    String navTabListDragComponent();

    String navTreeDragComponent();

    String navTilesDragComponent();

    String navCarouselDragComponent();

    String navMenubarDragComponentNavGroupHelp();

    String navTabListDragComponentNavGroupHelp();

    String navTreeDragComponentNavGroupHelp();

    String navTilesDragComponentNavGroupHelp();

    String navCarouselDragComponentNavGroupHelp();

    String navGroupEmptyError();

    String navCarouselDragComponentEmptyError();

    String navTilesDragComponentInfiniteRecursion();

    String navCarouselDragComponentInfiniteRecursion();

    String targetDivIdPerspectiveInfiniteRecursion();

    String navWidgetTargetDivMissing();

    String navGroupNotFound();

    String navItemsEmpty();

    String openNavItem(String itemName);

    String gotoNavItem(String itemName);

    String showNavItem(String itemName);

    String navConfigHeader();

    String navGroupLabel();

    String navGroupHelp();

    String navGroupSelectorHint();

    String defaultItemLabel();

    String defaultItemHelp();

    String defaultItemSelectorHint();

    String defaultItemsNotFound();

    String targetDivLabel();

    String targetDivHelp();

    String targetDivSelectorHint();

    String targetDivsNotFound();

    String navItemEditorPerspectiveHelp();

    String navItemEditorGroupHelp();

    String navRefPerspective(String name);

    String navRefGroupDefined(String name);

    String navRefGroupContext(String name);

    String navRefPerspectiveFound(String name);

    String navRefPerspectiveDefault(String name);

    String navRefPerspectiveInGroup(String name);

    String navRefComponent(String name);

    String navRefDefaultItemDefined(String name);

    String navRefDefaultItemFound(String name);

    String navRefPerspectiveRecursionEnd();
 }
