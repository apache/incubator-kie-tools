/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.Role;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.security.management.client.widgets.management.events.HomePerspectiveChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PriorityChangedEvent;
import org.uberfire.ext.widgets.common.client.dropdown.PerspectiveDropDown;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

@Dependent
public class ACLSettings implements IsWidget {

    View view;
    PermissionManager permissionManager;
    PerspectiveDropDown homePerspectiveDropDown;
    PerspectiveTreeProvider perspectiveTreeProvider;
    PriorityDropDown priorityDropDown;
    Event<HomePerspectiveChangedEvent> homePerspectiveChangedEvent;
    Event<PriorityChangedEvent> priorityChangedEvent;
    AuthorizationPolicy authzPolicy;
    boolean isEditMode;

    @Inject
    public ACLSettings(View view,
                       PermissionManager permissionManager,
                       PerspectiveDropDown homePerspectiveDropDown,
                       PerspectiveTreeProvider perspectiveTreeProvider,
                       PriorityDropDown priorityDropDown,
                       Event<HomePerspectiveChangedEvent> homePerspectiveChangedEvent,
                       Event<PriorityChangedEvent> priorityChangedEvent) {
        this.view = view;
        this.permissionManager = permissionManager;
        this.homePerspectiveDropDown = homePerspectiveDropDown;
        this.perspectiveTreeProvider = perspectiveTreeProvider;
        this.priorityDropDown = priorityDropDown;
        this.homePerspectiveChangedEvent = homePerspectiveChangedEvent;
        this.priorityChangedEvent = priorityChangedEvent;
        this.authzPolicy = permissionManager.getAuthorizationPolicy();

        homePerspectiveDropDown.setMaxItems(50);
        homePerspectiveDropDown.setOnChange(this::onHomePerspectiveSelected);
        homePerspectiveDropDown.setPerspectiveNameProvider(perspectiveTreeProvider::getPerspectiveName);
        homePerspectiveDropDown.setPerspectiveIdsExcluded(perspectiveTreeProvider.getPerspectiveIdsExcluded());

        view.setHomePerspectiveSelector(homePerspectiveDropDown);

        priorityDropDown.setOnChange(this::onPrioritySelected);
        view.setPrioritySelector(priorityDropDown);

        this.view.init(this);
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public PerspectiveActivity getHomePerspective() {
        return homePerspectiveDropDown.getSelectedPerspective();
    }

    public int getPriority() {
        return priorityDropDown.getSelectedPriority();
    }

    public void show(Role role) {
        isEditMode = false;
        open(role);
    }

    public void show(Group group) {
        isEditMode = false;
        open(group);
    }

    public void edit(Role role) {
        isEditMode = true;
        open(role);
    }

    public void edit(Group group) {
        isEditMode = true;
        open(group);
    }

    private void open(Role role) {
        String homePerspectiveId = authzPolicy.getHomePerspective(role);
        int priority = authzPolicy.getPriority(role);
        open(homePerspectiveId,
             priority);
    }

    private void open(Group group) {
        String homePerspectiveId = authzPolicy.getHomePerspective(group);
        int priority = authzPolicy.getPriority(group);
        open(homePerspectiveId,
             priority);
    }

    private void open(String homePerspectiveId,
                      int priority) {
        if (homePerspectiveId == null) {
            homePerspectiveId = homePerspectiveDropDown.getDefaultPerspective().getIdentifier();
        }

        view.setHomePerspectiveSelectorEnabled(isEditMode);
        view.setPrioritySelectorEnabled(isEditMode);

        if (isEditMode) {
            if (homePerspectiveId != null) {
                homePerspectiveDropDown.setSelectedPerspective(homePerspectiveId);
            }
            priorityDropDown.setSelectedPriority(priority);
        } else {
            if (homePerspectiveId != null) {
                String itemName = homePerspectiveDropDown.getItemName(homePerspectiveId);
                view.setHomePerspectiveName(itemName);
                view.setHomePerspectiveTitle(homePerspectiveId);
            } else {
                view.setNoHomePerspectiveDefined();
            }
            view.setPriorityName(priorityDropDown.getPriorityName(priority));
        }
    }

    void onHomePerspectiveSelected() {
        PerspectiveActivity p = getHomePerspective();
        homePerspectiveChangedEvent.fire(new HomePerspectiveChangedEvent(this,
                                                                         p));
    }

    void onPrioritySelected() {
        int priority = getPriority();
        priorityChangedEvent.fire(new PriorityChangedEvent(this,
                                                           priority));
    }

    public void updateAuthzPolicy(@Observes AuthorizationPolicySavedEvent authzPolicySavedEvent) {
        this.authzPolicy = authzPolicySavedEvent.getPolicy();
    }

    public interface View extends UberView<ACLSettings> {

        View setHomePerspectiveName(String name);

        View setHomePerspectiveTitle(String name);

        View setHomePerspectiveSelectorEnabled(boolean enabled);

        View setHomePerspectiveSelector(IsWidget widget);

        View setNoHomePerspectiveDefined();

        View setPriorityName(String name);

        View setPrioritySelectorEnabled(boolean enabled);

        View setPrioritySelector(IsWidget widget);
    }
}
