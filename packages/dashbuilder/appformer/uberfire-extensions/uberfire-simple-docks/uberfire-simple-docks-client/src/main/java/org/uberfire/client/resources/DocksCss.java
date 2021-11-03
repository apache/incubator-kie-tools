/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface DocksCss extends CssResource {

    @ClassName("gradientTopBottom")
    String gradientTopBottom();

    @ClassName("gradientBottomTop")
    String gradientBottomTop();

    @ClassName("sideDockItem")
    String sideDockItem();

    @ClassName("singleDockItem")
    String singleDockItem();

    @ClassName("hideElement")
    String hideElement();

    @ClassName("dockExpanded")
    String dockExpanded();

    @ClassName("dockExpandedLabelWest")
    String dockExpandedLabelWest();

    @ClassName("dockExpandedButtonWest")
    String dockExpandedButtonWest();

    @ClassName("dockExpandedLabelEast")
    String dockExpandedLabelEast();

    @ClassName("dockExpandedButtonEast")
    String dockExpandedButtonEast();

    @ClassName("dockExpandedButtonSouth")
    String dockExpandedButtonSouth();

    @ClassName("dockExpandedLabelSouth")
    String dockExpandedLabelSouth();

    @ClassName("dockExpandedTitlePanel")
    String dockExpandedTitlePanel();

    @ClassName("dockExpandedContentPanel")
    String dockExpandedContentPanel();

    @ClassName("dockExpandedContentPanelSouth")
    String dockExpandedContentPanelSouth();

    @ClassName("buttonFocused")
    String buttonFocused();

    @ClassName("southDockItem")
    String southDockItem();

    @ClassName("resizableBar")
    String resizableBar();

    @ClassName("dockExpandedContentButton")
    String dockExpandedContentButton();
}
