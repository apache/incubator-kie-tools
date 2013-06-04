package org.kie.workbench.common.screens.home.client.widgets.sections;

import com.google.gwt.user.client.ui.Label;
import org.kie.workbench.common.screens.home.client.resources.HomeResources;

/**
 * A Section header
 */
public class HeaderWidget extends Label {

    public HeaderWidget() {
        setStyleName( HomeResources.INSTANCE.CSS().sectionHeader());
    }

}
