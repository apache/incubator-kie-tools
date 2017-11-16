/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.edit;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget.ContributorsManagementPresenter;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class EditContributorsPopUpPresenter {

    public interface View extends UberElement<EditContributorsPopUpPresenter>,
                                  HasBusyIndicator {

        void show(OrganizationalUnit organizationalUnit);

        void hide();

        String getSavingMessage();

        String getSaveSuccessMessage();

        void append(HTMLElement child);
    }

    private View view;

    private ContributorsManagementPresenter contributorsManagementPresenter;

    private Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent;

    private Event<NotificationEvent> notificationEvent;

    private OrganizationalUnitController organizationalUnitController;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    OrganizationalUnit organizationalUnit;

    @Inject
    public EditContributorsPopUpPresenter(final View view,
                                          final ContributorsManagementPresenter contributorsManagementPresenter,
                                          final Event<AfterEditOrganizationalUnitEvent> afterEditOrganizationalUnitEvent,
                                          final Event<NotificationEvent> notificationEvent,
                                          final OrganizationalUnitController organizationalUnitController,
                                          final Caller<OrganizationalUnitService> organizationalUnitService) {
        this.view = view;
        this.contributorsManagementPresenter = contributorsManagementPresenter;
        this.afterEditOrganizationalUnitEvent = afterEditOrganizationalUnitEvent;
        this.notificationEvent = notificationEvent;
        this.organizationalUnitController = organizationalUnitController;
        this.organizationalUnitService = organizationalUnitService;
    }

    @PostConstruct
    public void setup() {
        view.init(this);
    }

    public void show(final OrganizationalUnit organizationalUnit) {
        if (organizationalUnitController.canUpdateOrgUnit(organizationalUnit)) {
            contributorsManagementPresenter.setup(organizationalUnit);
            view.append(contributorsManagementPresenter.getView().getElement());
            view.show(organizationalUnit);
            this.organizationalUnit = organizationalUnit;
        }
    }

    public void save() {
        final List<String> contributors = contributorsManagementPresenter.getSelectedContributorsUserNames();

        view.showBusyIndicator(view.getSavingMessage());
        organizationalUnitService.call((OrganizationalUnit newOrganizationalUnit) -> {
                                           afterEditOrganizationalUnitEvent.fire(new AfterEditOrganizationalUnitEvent(EditContributorsPopUpPresenter.this.organizationalUnit,
                                                                                                                      newOrganizationalUnit));
                                           view.hideBusyIndicator();
                                           notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(),
                                                                                        NotificationEvent.NotificationType.SUCCESS));
                                           view.hide();
                                       },
                                       new HasBusyIndicatorDefaultErrorCallback(view)).updateOrganizationalUnit(organizationalUnit.getName(),
                                                                                                                organizationalUnit.getOwner(),
                                                                                                                organizationalUnit.getDefaultGroupId(),
                                                                                                                contributors);
    }

    public void cancel() {
        view.hide();
    }
}
