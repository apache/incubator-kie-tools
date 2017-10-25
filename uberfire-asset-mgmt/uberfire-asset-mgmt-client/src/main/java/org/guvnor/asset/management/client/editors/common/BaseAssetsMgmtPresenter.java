/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.asset.management.client.editors.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.asset.management.client.i18n.Constants;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.asset.management.service.RepositoryStructureService;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryEnvironmentUpdatedEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.security.authz.AuthorizationManager;

public abstract class BaseAssetsMgmtPresenter {

    protected Constants constants = Constants.INSTANCE;

    @Inject
    protected Caller<AssetManagementService> assetManagementServices;

    @Inject
    protected Caller<RepositoryService> repositoryServices;

    @Inject
    protected Caller<RepositoryStructureService> repositoryStructureServices;

    @Inject
    protected User identity;

    @Inject
    protected AuthorizationManager authorizationManager;

    @Inject
    protected PlaceManager placeManager;

    protected Map<String, Repository> repositories = new TreeMap<String, Repository>();

    protected BaseAssetsMgmtView baseView;

    protected PlaceRequest place;

    public void init() {

    }

    public void loadRepositories() {
        repositoryServices.call(new RemoteCallback<List<Repository>>() {
            @Override
            public void callback(final List<Repository> repositoriesResults) {
                repositories.clear();
                baseView.getChooseRepositoryBox().clear();
                baseView.getChooseRepositoryBox().addItem(constants.Select_Repository());
                for (Repository r : repositoriesResults) {
                    repositories.put(r.getAlias(),
                                     r);
                }

                for (Map.Entry<String, Repository> entry : repositories.entrySet()) {
                    if (authorizationManager.authorize(entry.getValue(),
                                                       identity) && isManaged(entry.getValue())) {
                        baseView.getChooseRepositoryBox().addItem(entry.getKey(),
                                                                  entry.getValue().getAlias());
                    }
                }
            }
        }).getRepositories();
    }

    public boolean isManaged(Repository value) {
        return value != null && value.getEnvironment() != null && Boolean.TRUE.equals(value.getEnvironment().get("managed"));
    }

    public Repository getRepository(String alias) {
        return repositories.get(alias);
    }

    public Collection<Repository> getRepositories() {
        return repositories.values();
    }

    private void onRepositoryAdded(@Observes final NewRepositoryEvent event) {
        loadRepositories();
    }

    private void onRepositoryRemovedEvent(@Observes RepositoryRemovedEvent event) {
        loadRepositories();
    }

    private void onRepositoryUpdatedEvent(@Observes RepositoryEnvironmentUpdatedEvent event) {
        loadRepositories();
    }
}