package org.uberfire.client.resources;

import com.google.gwt.resources.client.CssResource;

public interface DocksCss extends CssResource {

    @ClassName("dock")
    String dock();

    @ClassName("westDockInnerPanel")
    String westDockInnerPanel();

    @ClassName("eastDockInnerPanel")
    String eastDockInnerPanel();

    @ClassName("southDockInnerPanel")
    String southDockInnerPanel();

    @ClassName("avaliableDocksPanel")
    String avaliableDocksPanel();

    @ClassName("dockCollapsed")
    String dockCollapsed();

    @ClassName("dockItem")
    String dockItem();

    @ClassName("dockItemSelected")
    String dockItemSelected();

    @ClassName("dockLabel")
    String dockLabel();

    @ClassName("dockLabelSelected")
    String dockLabelSelected();

    @ClassName("dockExpanded")
    String dockExpanded();

    @ClassName("dockExpandedButton")
    String dockExpandedButton();

    @ClassName("dockExpandedTitlePanel")
    String dockExpandedTitlePanel();

}
