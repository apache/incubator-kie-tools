/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextBranchChangeHandler;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextChangeHandler;
import org.guvnor.structure.config.SystemRepositoryChangedEvent;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.lifecycle.OnShutdown;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceCopiedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

@Dependent
@WorkbenchScreen(identifier = "FileExplorer")
public class FileExplorerPresenter
        implements GuvnorStructureContextChangeHandler,
                   GuvnorStructureContextBranchChangeHandler {

    private FileExplorerView view;

    @Inject
    private Caller<VFSService> vfsService;

    @Inject
    private Event<PathSelectedEvent> pathSelectedEvent;

    @Inject
    private PlaceManager placeManager;

    private GuvnorStructureContext guvnorStructureContext;

    private Map<String, Repository> repositories = new HashMap<String, Repository>();
    private GuvnorStructureContextChangeHandler.HandlerRegistration changeHandlerRegistration;
    private GuvnorStructureContextBranchChangeHandler.HandlerRegistration branchChangeHandlerRegistration;

    public FileExplorerPresenter() {
    }

    @Inject
    public FileExplorerPresenter(final FileExplorerView view,
                                 final GuvnorStructureContext guvnorStructureContext) {
        this.view = view;
        this.guvnorStructureContext = guvnorStructureContext;
        this.changeHandlerRegistration = this.guvnorStructureContext.addGuvnorStructureContextChangeHandler(this);
        this.branchChangeHandlerRegistration = this.guvnorStructureContext.addGuvnorStructureContextBranchChangeHandler(this);

        view.init(this);
    }

    private boolean isDirectory(final Map response) {
        return response != null && response.containsKey("isDirectory") && (Boolean) response.get("isDirectory");
    }

    @OnStartup
    public void reset() {

        view.reset();

        guvnorStructureContext.getRepositories(new Callback<Collection<Repository>>() {
            @Override
            public void callback(final Collection<Repository> response) {
                for (final Repository root : response) {
                    if (repositories.containsKey(root.getAlias())) {
                        view.removeRepository(root);
                    }
                    view.addNewRepository(root,
                                          guvnorStructureContext.getCurrentBranch(root.getAlias()));
                    repositories.put(root.getAlias(),
                                     root);
                }
            }
        });
    }

    @OnShutdown
    public void onShutdown() {
        guvnorStructureContext.removeHandler(changeHandlerRegistration);
        guvnorStructureContext.removeHandler(branchChangeHandlerRegistration);
    }

    public void loadDirectoryContent(final FileExplorerItem item,
                                     final Path path) {
        vfsService.call(new RemoteCallback<DirectoryStream<Path>>() {
            @Override
            public void callback(DirectoryStream<Path> response) {
                for (final Path child : response) {
                    vfsService.call(new RemoteCallback<Map>() {
                        @Override
                        public void callback(final Map response) {
                            if (isDirectory(response)) {
                                item.addDirectory(child);
                            } else {
                                item.addFile(child);
                            }
                        }
                    }).readAttributes(child);
                }
            }
        }).newDirectoryStream(path);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view;
    }

    private boolean isRegularFile(final Map response) {
        return response != null && response.containsKey("isRegularFile") && (Boolean) response.get("isRegularFile");
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.FileExplorer();
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.WEST;
    }

    public void redirect(final Path path) {

        pathSelectedEvent.fire(new PathSelectedEvent(path));

        vfsService.call(new RemoteCallback<Map>() {
            @Override
            public void callback(final Map response) {
                if (isRegularFile(response)) {
                    placeManager.goTo(path);
                }
            }
        }).readAttributes(path);
    }

    public void redirectRepositoryList() {
        placeManager.goTo(new DefaultPlaceRequest("RepositoriesEditor"));
    }

    public void redirect(final Repository repo) {
        placeManager.goTo(new DefaultPlaceRequest("RepositoryEditor").addParameter("alias",
                                                                                   repo.getAlias()));
    }

    @Override
    public void onNewRepositoryAdded(final Repository repository) {
        if (repository == null) {
            return;
        }
        if (repositories.containsKey(repository.getAlias())) {
            view.removeRepository(repository);
        }
        view.addNewRepository(repository,
                              repository.getDefaultBranch().get().getName());
        repositories.put(repository.getAlias(),
                         repository);
    }

    @Override
    public void onRepositoryDeleted(final Repository repository) {
        if (repository == null) {
            return;
        }
        if (repositories.containsKey(repository.getAlias())) {
            view.removeRepository(repository);
            repositories.remove(repository.getAlias());
        }
    }

    @Override
    public void onBranchChange(final String alias,
                               final String branch) {
        if (alias == null) {
            return;
        }

        if (repositories.containsKey(alias)) {
            final Repository repository = repositories.get(alias);
            view.removeRepository(repository);

            // refresh repository
            view.addNewRepository(repository,
                                  branch);
        }
    }

    @Override
    public void onNewBranchAdded(String repositoryAlias,
                                 String branchName,
                                 Path branchPath) {
        //currently no actions needed
    }

    // Refresh when a Resource has been added
    public void onResourceAdded(@Observes final ResourceAddedEvent event) {
        refreshView(event.getPath());
    }

    // Refresh when a Resource has been deleted
    public void onResourceDeleted(@Observes final ResourceDeletedEvent event) {
        refreshView(event.getPath());
    }

    // Refresh when a Resource has been copied
    public void onResourceCopied(@Observes final ResourceCopiedEvent event) {
        refreshView(event.getDestinationPath());
    }

    // Refresh when a Resource has been renamed
    public void onResourceRenamed(@Observes final ResourceRenamedEvent event) {
        refreshView(event.getDestinationPath());
    }

    // Refresh when a batch Resource change has occurred
    public void onBatchResourceChange(@Observes final ResourceBatchChangesEvent event) {
        reset();
    }

    public void onSystemRepositoryChanged(@Observes SystemRepositoryChangedEvent event) {
        reset();
    }

    private void refreshView(final Path path) {
        final String pathUri = path.toURI();
        for (final Repository repository : repositories.values()) {
            if (repository.getDefaultBranch().isPresent()) {
                final String repositoryUri = repository.getDefaultBranch().get().getPath().toURI();
                if (pathUri.startsWith(repositoryUri)) {
                    reset();
                    break;
                }
            }
        }
    }
}