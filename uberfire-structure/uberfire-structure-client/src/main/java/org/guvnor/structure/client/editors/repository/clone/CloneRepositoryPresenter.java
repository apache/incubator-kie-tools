/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.client.editors.repository.clone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.events.AfterCreateOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.util.URIUtil;

@Dependent
public class CloneRepositoryPresenter implements CloneRepositoryView.Presenter {

    private RepositoryPreferences repositoryPreferences;

    private CloneRepositoryView view;

    private Caller<RepositoryService> repositoryService;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private PlaceManager placeManager;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();

    @Inject
    public CloneRepositoryPresenter(final RepositoryPreferences repositoryPreferences,
                                    final CloneRepositoryView view,
                                    final Caller<RepositoryService> repositoryService,
                                    final Caller<OrganizationalUnitService> organizationalUnitService,
                                    final PlaceManager placeManager) {
        this.repositoryPreferences = repositoryPreferences;
        this.view = view;
        this.repositoryService = repositoryService;
        this.organizationalUnitService = organizationalUnitService;
        this.placeManager = placeManager;
    }

    @PostConstruct
    public void init() {
        view.init(this,
                  isOuMandatory());
        populateOrganizationalUnits();
    }

    @Override
    public void handleCancelClick() {
        view.hide();
    }

    @Override
    public void handleCloneClick() {
        boolean urlConditionsMet = setUrl();
        boolean ouConditionsMet = setOrganizationalUnitGroupType();
        boolean nameConditionsMet = setNameGroupType();

        if (urlConditionsMet && ouConditionsMet && nameConditionsMet) {
            repositoryService.call(getNormalizeRepositoryNameCallback()).normalizeRepositoryName(view.getName());
        }
    }

    public void onCreateOrganizationalUnit(@Observes final AfterCreateOrganizationalUnitEvent event) {
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if (organizationalUnit == null) {
            return;
        }
        view.addOrganizationalUnit(organizationalUnit);
        availableOrganizationalUnits.put(organizationalUnit.getName(),
                                         organizationalUnit);
    }

    public void onDeleteOrganizationalUnit(@Observes final RemoveOrganizationalUnitEvent event) {
        final OrganizationalUnit organizationalUnit = event.getOrganizationalUnit();
        if (organizationalUnit == null) {
            return;
        }
        view.deleteOrganizationalUnit(organizationalUnit);
        availableOrganizationalUnits.remove(organizationalUnit.getName());
    }

    private RemoteCallback<String> getNormalizeRepositoryNameCallback() {
        return new RemoteCallback<String>() {
            @Override
            public void callback(final String normalizedName) {
                if (!view.getName().equals(normalizedName)) {
                    if (!view.showAgreeNormalizeNameWindow(normalizedName)) {
                        return;
                    }
                    view.setName(normalizedName);
                }

                lockScreen();

                final String scheme = "git";
                final String alias = view.getName().trim();
                repositoryService.call(getCreateRepositoryCallback(),
                                       getErrorCallback()).createRepository(availableOrganizationalUnits.get(view.getSelectedOrganizationalUnit()),
                                                                            scheme,
                                                                            alias,
                                                                            getRepositoryConfiguration(view.getSelectedOrganizationalUnit()));
            }
        };
    }

    private RepositoryEnvironmentConfigurations getRepositoryConfiguration(String selectedOrganizationalUnit) {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();

        configuration.setUserName(view.getUsername().trim());
        configuration.setPassword(view.getPassword().trim());
        configuration.setOrigin(view.getGitUrl());
        configuration.setSpace(selectedOrganizationalUnit);
        return configuration;
    }

    private RemoteCallback<Repository> getCreateRepositoryCallback() {
        return new RemoteCallback<Repository>() {
            @Override
            public void callback(final Repository o) {
                view.alertRepositoryCloned();
                unlockScreen();
                view.hide();
                placeManager.goTo(new DefaultPlaceRequest("RepositoryEditor").addParameter("alias",
                                                                                           o.getAlias()));
            }
        };
    }

    private ErrorCallback<Message> getErrorCallback() {
        return new ErrorCallback<Message>() {
            @Override
            public boolean error(final Message message,
                                 final Throwable throwable) {
                try {
                    throw throwable;
                } catch (RepositoryAlreadyExistsException ex) {
                    view.errorRepositoryAlreadyExist();
                } catch (Throwable ex) {
                    view.errorCloneRepositoryFail(ex);
                }
                unlockScreen();
                return true;
            }
        };
    }

    private boolean setNameGroupType() {
        if (view.isNameEmpty()) {
            view.setNameGroupType(ValidationState.ERROR);
            view.showNameHelpMandatoryMessage();
            return false;
        } else {
            view.setNameGroupType(ValidationState.NONE);
            return true;
        }
    }

    private boolean setOrganizationalUnitGroupType() {
        if (isOuMandatory() && !availableOrganizationalUnits.containsKey(view.getSelectedOrganizationalUnit())) {
            view.setOrganizationalUnitGroupType(ValidationState.ERROR);
            view.showOrganizationalUnitHelpMandatoryMessage();
            return false;
        } else {
            view.setOrganizationalUnitGroupType(ValidationState.NONE);
            return true;
        }
    }

    private boolean setUrl() {
        if (view.isGitUrlEmpty()) {
            view.setUrlGroupType(ValidationState.ERROR);
            view.showUrlHelpMandatoryMessage();
            return false;
        } else if (!URIUtil.isValid(view.getGitUrl())) {
            view.setUrlGroupType(ValidationState.ERROR);
            view.showUrlHelpInvalidFormatMessage();
            return false;
        } else {
            view.setUrlGroupType(ValidationState.NONE);
            return true;
        }
    }

    public void showForm() {
        view.reset();
        view.show();
    }

    private void populateOrganizationalUnits() {
        //populate Organizational Units list box
        organizationalUnitService.call(new RemoteCallback<Collection<OrganizationalUnit>>() {
                                           @Override
                                           public void callback(final Collection<OrganizationalUnit> organizationalUnits) {
                                               view.addOrganizationalUnitSelectEntry();
                                               if (organizationalUnits != null && !organizationalUnits.isEmpty()) {
                                                   for (OrganizationalUnit organizationalUnit : organizationalUnits) {
                                                       view.addOrganizationalUnit(organizationalUnit);
                                                       availableOrganizationalUnits.put(organizationalUnit.getName(),
                                                                                        organizationalUnit);
                                                   }
                                               }
                                           }
                                       },
                                       new ErrorCallback<Message>() {
                                           @Override
                                           public boolean error(final Message message,
                                                                final Throwable throwable) {
                                               view.errorLoadOrganizationalUnitsFail(throwable);
                                               return false;
                                           }
                                       }).getOrganizationalUnits();
    }

    private void lockScreen() {
        view.showBusyPopupMessage();
        view.setPopupCloseVisible(false);
        view.setCloneEnabled(false);
        view.setCancelEnabled(false);
        view.setPasswordEnabled(false);
        view.setUsernameEnabled(false);
        view.setGitUrlEnabled(false);
        view.setOrganizationalUnitEnabled(false);
        view.setNameEnabled(false);
    }

    private void unlockScreen() {
        view.closeBusyPopup();
        view.setPopupCloseVisible(true);
        view.setCloneEnabled(true);
        view.setCancelEnabled(true);
        view.setPasswordEnabled(true);
        view.setUsernameEnabled(true);
        view.setGitUrlEnabled(true);
        view.setOrganizationalUnitEnabled(true);
        view.setNameEnabled(true);
    }

    private boolean isOuMandatory() {
        return repositoryPreferences == null || repositoryPreferences.isOUMandatory();
    }
}