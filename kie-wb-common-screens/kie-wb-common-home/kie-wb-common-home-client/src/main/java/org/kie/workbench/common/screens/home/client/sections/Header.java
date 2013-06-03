package org.kie.workbench.common.screens.home.client.sections;

import com.google.gwt.user.client.ui.Label;
import org.kie.workbench.common.screens.home.client.resources.HomeResources;

/**
 * A Section header
 */
public class Header extends Label {

    public Header() {
        setStyleName( HomeResources.INSTANCE.CSS().sectionHeader());
    }

}
