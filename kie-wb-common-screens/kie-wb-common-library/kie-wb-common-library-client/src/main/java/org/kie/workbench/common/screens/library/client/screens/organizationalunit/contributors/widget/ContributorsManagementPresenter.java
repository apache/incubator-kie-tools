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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.rpc.SessionInfo;

public class ContributorsManagementPresenter {

    public interface View extends UberElement<ContributorsManagementPresenter> {

        String getFilterText();

        void clearFilter();

        void clearUsers();

        void addUser(ContributorsManagementListItemPresenter item);
    }

    private View view;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private Caller<LibraryService> libraryService;

    private ManagedInstance<ContributorsManagementListItemPresenter> contributorsManagementListItemPresenters;

    private SessionInfo sessionInfo;

    private List<SocialUser> allUsers;

    private Map<String, ContributorsManagementListItemPresenter> contributorsByUserName;

    @Inject
    public ContributorsManagementPresenter(final View view,
                                           final Caller<OrganizationalUnitService> organizationalUnitService,
                                           final Caller<LibraryService> libraryService,
                                           final ManagedInstance<ContributorsManagementListItemPresenter> contributorsManagementListItemPresenters,
                                           final SessionInfo sessionInfo) {
        this.view = view;
        this.organizationalUnitService = organizationalUnitService;
        this.libraryService = libraryService;
        this.contributorsManagementListItemPresenters = contributorsManagementListItemPresenters;
        this.sessionInfo = sessionInfo;
    }

    public void setup() {
        setup(null);
    }

    public void setup(final OrganizationalUnit organizationalUnit) {
        view.init(this);
        view.clearFilter();

        contributorsByUserName = new HashMap<>();
        libraryService.call((List<SocialUser> allUsers) -> {
            ContributorsManagementPresenter.this.allUsers = allUsers;

            for (SocialUser user : allUsers) {
                final ContributorsManagementListItemPresenter contributor = contributorsManagementListItemPresenters.get();
                contributor.setup(user);
                contributorsByUserName.put(user.getUserName(),
                                           contributor);
            }

            if (organizationalUnit != null) {
                organizationalUnit.getContributors().forEach(this::selectContributor);
                selectOwner(organizationalUnit.getOwner());
            } else {
                selectOwner(sessionInfo.getIdentity().getIdentifier());
            }

            listUsers(allUsers);
        }).getAllUsers();
    }

    public void filter() {
        final String filterText = view.getFilterText();
        final List<SocialUser> filteredUsers = allUsers.stream()
                .filter(user -> {
                    final String name = user.getName();
                    final String userName = user.getUserName();
                    final boolean nameMatch = name != null && name.contains(filterText);
                    final boolean userNameMatch = userName.contains(filterText);

                    return nameMatch || userNameMatch;
                })
                .collect(Collectors.toList());
        listUsers(filteredUsers);
    }

    public List<String> getSelectedContributorsUserNames() {
        return contributorsByUserName.entrySet().stream()
                .filter(entry -> entry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void listUsers(final List<SocialUser> users) {
        view.clearUsers();
        users.forEach(user -> {
            final ContributorsManagementListItemPresenter contributor = contributorsByUserName.get(user.getUserName());
            view.addUser(contributor);
        });
    }

    private void selectOwner(final String userName) {
        selectUser(userName,
                   true);
    }

    private void selectContributor(final String userName) {
        selectUser(userName,
                   false);
    }

    private void selectUser(final String userName,
                            final boolean owner) {
        final ContributorsManagementListItemPresenter contributor = contributorsByUserName.get(userName);
        if (contributor != null) {
            contributor.setSelected(true);
            contributor.setEnabled(!owner);
        }
    }

    public View getView() {
        return view;
    }
}
