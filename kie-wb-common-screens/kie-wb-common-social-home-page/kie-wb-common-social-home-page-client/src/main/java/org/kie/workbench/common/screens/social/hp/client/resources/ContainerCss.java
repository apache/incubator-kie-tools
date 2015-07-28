/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.social.hp.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface ContainerCss extends CssResource {

    String clickable();

    String box();

    String contained();

    String red();

    String orange();

    String yellow();

    String green();

    String blue();

    String container();

    @ClassName("child-container")
    String childContainer();

    @ClassName("c-wide")
    String cWide();

    @ClassName("box-middle")
    String boxMiddle();

    @ClassName("box-right")
    String boxRight();

    @ClassName("box-left")
    String boxLeft();

    String inline();

    String selected();

    String invisible();

    @ClassName("active-profile")
    String activeProfile();

    @ClassName("profile-select")
    String profileSelect();

    @ClassName("no-fade")
    String noFade();

    @ClassName("section-header")
    String sectionHeader();

    @ClassName("nav-danger")
    String navDanger();

    @ClassName("section-controls")
    String sectionControls();

    @ClassName("selection-controls")
    String selectionControls();

    @ClassName("search-query")
    String searchQuery();

    @ClassName("section-filter")
    String sectionFilter();

    @ClassName("icon-to-remove")
    String iconToRemove();

    @ClassName("cell-icon")
    String cellIcon();
}
