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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.events.AfterEditOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class ContributorsListItemPresenter {

    public interface View extends UberElemental<ContributorsListItemPresenter>,
                                  HasBusyIndicator {

        void setupAddMode();

        void setupViewMode(Contributor contributor);

        void removeContributor();

        void showActions();

        void hideActions();

        void viewMode();

        void editMode();

        String getName();

        ContributorType getRole();

        String getSavingMessage();

        String getSaveSuccessMessage();

        String getInvalidNameMessage();

        String getInvalidRoleMessage();

        String getRemoveSuccessMessage();

        String getSpaceOwnerChangedMessage();

        String getSingleOwnerIsMandatoryMessage();

        String getDuplicatedContributorMessage();
    }

    private View view;

    private LibraryPlaces libraryPlaces;

    private Event<NotificationEvent> notificationEvent;

    private Contributor persistedContributor;

    private ContributorsListPresenter parentPresenter;

    private ContributorsListService contributorsListService;

    @Inject
    public ContributorsListItemPresenter(final View view,
                                         final LibraryPlaces libraryPlaces,
                                         final Event<NotificationEvent> notificationEvent) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.notificationEvent = notificationEvent;
    }

    public void setupNew(final ContributorsListPresenter parentPresenter,
                         final ContributorsListService contributorsListService) {
        this.parentPresenter = parentPresenter;
        this.contributorsListService = contributorsListService;

        view.init(this);
        view.setupAddMode();
    }

    public void setup(final Contributor contributor,
                      final ContributorsListPresenter parentPresenter,
                      final ContributorsListService contributorsListService) {
        this.persistedContributor = contributor;
        this.parentPresenter = parentPresenter;
        this.contributorsListService = contributorsListService;

        view.init(this);
        view.setupViewMode(contributor);
    }

    public void edit() {
        parentPresenter.itemIsBeingEdited();
        view.editMode();
    }

    public void save() {
        final Contributor contributor = new Contributor(view.getName(), view.getRole());

        contributorsListService.getContributors(currentContributors -> {
            final boolean isValid = isValid(contributor, currentContributors);

            if (isValid) {
                final List<Contributor> updatedContributors = new ArrayList<>();

                if (persistedContributor == null) {
                    updatedContributors.addAll(currentContributors);
                } else {
                    updatedContributors.addAll(currentContributors.stream().filter(c -> !c.equals(persistedContributor)).collect(Collectors.toList()));
                }
                updatedContributors.add(contributor);

                view.showBusyIndicator(view.getSavingMessage());
                contributorsListService.saveContributors(updatedContributors,
                                                         () -> {
                                                             persistedContributor = contributor;
                                                             view.setupViewMode(contributor);
                                                             parentPresenter.itemIsNotBeingEdited();
                                                             view.hideBusyIndicator();

                                                             notificationEvent.fire(new NotificationEvent(view.getSaveSuccessMessage(),
                                                                                                          NotificationEvent.NotificationType.SUCCESS));
                                                             parentPresenter.refresh();
                                                         },
                                                         new HasBusyIndicatorDefaultErrorCallback(view));
            }
        });
    }

    private boolean isValid(final Contributor contributor,
                            final List<Contributor> currentContributors) {
        final boolean emptyName = contributor.getUsername() == null;
        final boolean validUsername = !emptyName && parentPresenter.getValidUsernames().contains(contributor.getUsername());
        if (emptyName || !validUsername) {
            notificationEvent.fire(new NotificationEvent(view.getInvalidNameMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return false;
        }

        final boolean newContributor = persistedContributor == null;
        final boolean wasOwner = !newContributor && ContributorType.OWNER.equals(persistedContributor.getType());
        final boolean isOwner = ContributorType.OWNER.equals(contributor.getType());
        if (!newContributor && wasOwner && !isOwner && isLastOwner(persistedContributor, currentContributors)) {
            notificationEvent.fire(new NotificationEvent(view.getSingleOwnerIsMandatoryMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return false;
        }

        final boolean userIsAlreadyAContributor = currentContributors.stream().anyMatch(c -> c.getUsername().equals(contributor.getUsername()));
        final boolean userChanged = !newContributor && !persistedContributor.getUsername().equals(contributor.getUsername());
        if ((newContributor && userIsAlreadyAContributor) || (!newContributor && userChanged && userIsAlreadyAContributor)) {
            notificationEvent.fire(new NotificationEvent(view.getDuplicatedContributorMessage(),
                                                         NotificationEvent.NotificationType.ERROR));
            return false;
        }

        return true;
    }

    public void remove() {
        contributorsListService.getContributors(contributors -> {
            if (isLastOwner(persistedContributor, contributors)) {
                notificationEvent.fire(new NotificationEvent(view.getSingleOwnerIsMandatoryMessage(),
                                                             NotificationEvent.NotificationType.ERROR));
            } else {
                contributors.remove(persistedContributor);

                view.showBusyIndicator(view.getSavingMessage());
                contributorsListService.saveContributors(contributors,
                                                         () -> {
                                                             view.hideBusyIndicator();
                                                             notificationEvent.fire(new NotificationEvent(view.getRemoveSuccessMessage(),
                                                                                                          NotificationEvent.NotificationType.SUCCESS));
                                                             view.removeContributor();
                                                             parentPresenter.refresh();
                                                         },
                                                         new HasBusyIndicatorDefaultErrorCallback(view));
            }
        });
    }

    private boolean isLastOwner(final Contributor contributor,
                                final List<Contributor> contributors) {
        return contributors.stream().noneMatch(c -> !c.getUsername().equals(contributor.getUsername()) && ContributorType.OWNER.equals(c.getType()));
    }

    public void cancel() {
        if (persistedContributor != null) {
            view.setupViewMode(persistedContributor);
        } else {
            view.removeContributor();
        }

        parentPresenter.itemIsNotBeingEdited();
    }

    public void showActions() {
        view.showActions();
    }

    public void hideActions() {
        view.hideActions();
    }

    public Contributor getContributor() {
        return persistedContributor;
    }

    public List<String> getUserNames() {
        return parentPresenter.getValidUsernames();
    }

    public boolean canRemoveContributor() {
        return canEditContributors();
    }

    public boolean canEditContributors() {
        return parentPresenter.canEditContributors();
    }

    public View getView() {
        return view;
    }
}
