/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.screens.explorer.client.widgets.business;

import com.github.gwtbootstrap.client.ui.NavHeader;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.workbench.common.screens.explorer.client.resources.ProjectExplorerResources;

/**
 * Trigger Widget for ResourceType groups
 */
public class TriggerWidget extends HorizontalPanel {

    public TriggerWidget( final String caption ) {
        add( makeNavHeader( caption ) );
    }

    public TriggerWidget( final IsWidget icon,
                          final String caption ) {
        add( icon );
        add( makeNavHeader( caption ) );
    }

    private NavHeader makeNavHeader( final String caption ) {
        final NavHeader nh = new NavHeader( caption );
        nh.addStyleName( ProjectExplorerResources.INSTANCE.CSS().triggerCaption() );
        return nh;
    }

}
