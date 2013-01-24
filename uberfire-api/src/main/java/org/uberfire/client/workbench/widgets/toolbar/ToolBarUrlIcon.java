package org.uberfire.client.workbench.widgets.toolbar;

public interface ToolBarUrlIcon extends ToolBarIcon {

    /**
     * @return The relative URL for the image for the ToolBarItem. Images must
     *         be within the application WAR, i.e. not an external location.
     */
    String getUrl();

}
