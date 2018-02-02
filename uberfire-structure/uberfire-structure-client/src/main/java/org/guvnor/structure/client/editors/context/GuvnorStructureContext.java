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

package org.guvnor.structure.client.editors.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.spaces.Space;

/**
 * Context that keeps track of repositories and branches. Used in Guvnor Administration perspective,
 * more specifically by the Guvnor Structure module.
 */
@ApplicationScoped
public class GuvnorStructureContext {

    private final HashMap<GuvnorStructureContextChangeHandler.HandlerRegistration, GuvnorStructureContextChangeHandler> handlers = new HashMap<>();
    private final HashMap<GuvnorStructureContextBranchChangeHandler.HandlerRegistration, GuvnorStructureContextBranchChangeHandler> branchChangeHandlers = new HashMap<>();
    private final HashMap<String, String> aliasBranch = new HashMap<>();

    private Caller<RepositoryService> repositoryService;
    private WorkspaceProjectContext context;

    public GuvnorStructureContext() {
    }

    @Inject
    public GuvnorStructureContext(final Caller<RepositoryService> repositoryService, final WorkspaceProjectContext context) {
        this.repositoryService = repositoryService;
        this.context = context;
    }

    public void getRepositories(final Callback<Collection<Repository>> callback) {
        String ouName = context.getActiveOrganizationalUnit()
                               .map(ou -> ou.getName())
                               .orElseThrow(() -> new IllegalStateException("Cannot lookup repositories without active organizational unit."));
        repositoryService.call(new RemoteCallback<Collection<Repository>>() {
            @Override
            public void callback(final Collection<Repository> response) {

                final Collection<String> foundAliases = updateRepositories(response);

                removeMissingAliases(foundAliases);

                callback.callback(response);
            }
        }).getRepositories(new Space(ouName));
    }

    private Collection<String> updateRepositories(final Collection<Repository> response) {
        final Collection<String> foundAliases = new ArrayList<>();

        for (final Repository repository : response) {

            foundAliases.add(repository.getAlias());

            updateRepository(repository);
        }

        return foundAliases;
    }

    private void updateRepository(final Repository repository) {
        if (!repository.getDefaultBranch().isPresent()) {
            return;
        }

        if (isNewRepository(repository)) {
            aliasBranch.put(repository.getAlias(),
                            repository.getDefaultBranch().get().getName());
        } else {
            updateBranch(repository);
        }
    }

    private void updateBranch(final Repository repository) {
        if (!repository.getDefaultBranch().isPresent()) {
            return;
        }
        final String branchName = aliasBranch.get(repository.getAlias());

        if (branchName == null || hasBranchBeenRemoved(repository,
                                                       branchName)) {
            aliasBranch.put(repository.getAlias(),
                            repository.getDefaultBranch().get().getName());
        }
    }

    private boolean isNewRepository(final Repository repository) {
        return !aliasBranch.containsKey(repository.getAlias());
    }

    private boolean hasBranchBeenRemoved(final Repository repository,
                                         final String branchName) {
        return !repository
                .getBranches()
                .stream()
                .anyMatch(branch -> branch.getName().equals(branchName));
    }

    private void removeMissingAliases(final Collection<String> foundAliases) {
        for (final String missingAlias : getMissingAliases(foundAliases)) {
            aliasBranch.remove(missingAlias);
        }
    }

    private Collection<String> getMissingAliases(final Collection<String> foundAliases) {
        final Collection<String> missingAliases = new ArrayList<>();

        for (final String alias : aliasBranch.keySet()) {
            if (!foundAliases.contains(alias)) {
                missingAliases.add(alias);
            }
        }

        return missingAliases;
    }

    public GuvnorStructureContextChangeHandler.HandlerRegistration addGuvnorStructureContextChangeHandler(final GuvnorStructureContextChangeHandler handler) {
        final GuvnorStructureContextChangeHandler.HandlerRegistration handlerRegistration = new GuvnorStructureContextChangeHandler.HandlerRegistration();

        handlers.put(handlerRegistration,
                     handler);

        return handlerRegistration;
    }

    public GuvnorStructureContextBranchChangeHandler.HandlerRegistration addGuvnorStructureContextBranchChangeHandler(final GuvnorStructureContextBranchChangeHandler handler) {
        final GuvnorStructureContextBranchChangeHandler.HandlerRegistration handlerRegistration = new GuvnorStructureContextBranchChangeHandler.HandlerRegistration();

        branchChangeHandlers.put(handlerRegistration,
                                 handler);

        return handlerRegistration;
    }

    public void changeBranch(final String alias,
                             final String branch) {

        aliasBranch.put(alias,
                        branch);

        for (final GuvnorStructureContextBranchChangeHandler handler : branchChangeHandlers.values()) {
            handler.onBranchChange(alias,
                                   branch);
        }
    }

    public void onNewRepository(final @Observes NewRepositoryEvent event) {

        final Repository newRepository = event.getNewRepository();
        if (newRepository.getDefaultBranch().isPresent()) {
            aliasBranch.put(newRepository.getAlias(),
                            newRepository.getDefaultBranch().get().getName());
        }

        for (final GuvnorStructureContextChangeHandler handler : handlers.values()) {
            handler.onNewRepositoryAdded(newRepository);
        }
    }

    public void onNewBranch(final @Observes NewBranchEvent event) {
        for (final GuvnorStructureContextChangeHandler handler : handlers.values()) {
            final Optional<Branch> branchOptional = event.getRepository().getBranch(event.getNewBranchName());
            if (branchOptional.isPresent()) {
                handler.onNewBranchAdded(event.getRepository().getAlias(),
                                         event.getNewBranchName(),
                                         branchOptional.get().getPath());
            }
        }
    }

    public void onRepositoryRemoved(final @Observes RepositoryRemovedEvent event) {

        aliasBranch.remove(event.getRepository().getAlias());

        for (final GuvnorStructureContextChangeHandler handler : handlers.values()) {
            handler.onRepositoryDeleted(event.getRepository());
        }
    }

    public void removeHandler(final GuvnorStructureContextChangeHandler.HandlerRegistration handlerRegistration) {
        handlers.remove(handlerRegistration);
    }

    public void removeHandler(final GuvnorStructureContextBranchChangeHandler.HandlerRegistration handlerRegistration) {
        branchChangeHandlers.remove(handlerRegistration);
    }

    public String getCurrentBranch(final String alias) {
        return aliasBranch.get(alias);
    }
}
