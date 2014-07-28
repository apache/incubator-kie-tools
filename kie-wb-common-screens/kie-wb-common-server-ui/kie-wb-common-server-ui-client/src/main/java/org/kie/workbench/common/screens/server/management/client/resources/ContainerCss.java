package org.kie.workbench.common.screens.server.management.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface ContainerCss extends
                              CssResource {

    String clickable();

    String box();

    String contained();

    String red();

    String orange();

    String yellow();

    String green();

    String blue();

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
}
