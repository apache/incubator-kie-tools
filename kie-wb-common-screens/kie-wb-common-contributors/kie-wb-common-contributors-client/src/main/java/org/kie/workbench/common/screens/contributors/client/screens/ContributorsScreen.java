/**
 * Copyright (C) 2014 JBoss Inc
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
package org.kie.workbench.common.screens.contributors.client.screens;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.dataset.events.DataSetModifiedEvent;
import org.kie.workbench.common.screens.contributors.client.resources.i18n.ContributorsConstants;
import org.kie.workbench.common.screens.contributors.model.ContributorsDataSets;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.events.NotificationEvent;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.*;

@Dependent
@WorkbenchScreen(identifier = "ContributorsScreen")
public class ContributorsScreen {


    @Inject
    private Event<NotificationEvent> workbenchNotification;

    ContributorsView contributorsView = new ContributorsView();

    @WorkbenchPartTitle
    public String getTitle() {
        return "Contributors";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return contributorsView;
    }

    /**
     * Catch any changes on the contributors data set and update the dashboard properly.
     */
    private void onContributorsDataSetOutdated(@Observes DataSetModifiedEvent event) {
        checkNotNull("event", event);

        String targetUUID = event.getDataSetUUID();
        if (ContributorsDataSets.ALL.equals(targetUUID)) {
            workbenchNotification.fire(new NotificationEvent(ContributorsConstants.INSTANCE.contributorsDataSetOutdated(), INFO));
            contributorsView.redraw();
        }
    }
}
