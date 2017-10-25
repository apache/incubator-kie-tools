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

package org.guvnor.structure.client.editors.repository.edit;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.guvnor.structure.client.security.RepositoryController;
import org.guvnor.structure.repositories.PublicURI;
import org.guvnor.structure.repositories.RepositoryInfo;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryServiceEditor;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;
import org.uberfire.java.nio.base.version.VersionRecord;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@WorkbenchScreen(identifier = "RepositoryEditor")
public class RepositoryEditorPresenter {

    private View view;
    private Caller<RepositoryService> repositoryService;
    private Caller<RepositoryServiceEditor> repositoryServiceEditor;
    private Event<NotificationEvent> notification;
    private PlaceManager placeManager;
    private RepositoryController repositoryController;

    private String alias = null;
    private Path root = null;
    private PlaceRequest place;

    public interface View
            extends UberView<RepositoryEditorPresenter> {

        void setRepositoryInfo(final String repositoryName,
                               final String owner,
                               final boolean readOnly,
                               final List<PublicURI> publicURIs,
                               final String description,
                               final List<VersionRecord> initialVersionList);

        void reloadHistory(final List<VersionRecord> versionList);

        void addHistory(final List<VersionRecord> versionList);
    }

    public RepositoryEditorPresenter() {
    }

    @Inject
    public RepositoryEditorPresenter(final View view,
                                     final Caller<RepositoryService> repositoryService,
                                     final Caller<RepositoryServiceEditor> repositoryServiceEditor,
                                     final Event<NotificationEvent> notification,
                                     final PlaceManager placeManager,
                                     final RepositoryController repositoryController) {
        this.view = view;
        this.repositoryService = repositoryService;
        this.repositoryServiceEditor = repositoryServiceEditor;
        this.notification = notification;
        this.placeManager = placeManager;
        this.repositoryController = repositoryController;
    }

    @OnStartup
    public void onStartup(final PlaceRequest place) {
        this.place = place;
        this.alias = place.getParameters().get("alias");

        repositoryService.call(new RemoteCallback<RepositoryInfo>() {
            @Override
            public void callback(final RepositoryInfo repo) {
                root = repo.getRoot();
                view.setRepositoryInfo(repo.getAlias(),
                                       repo.getOwner(),
                                       !repositoryController.canUpdateRepository(repo.getId()),
                                       repo.getPublicURIs(),
                                       CoreConstants.INSTANCE.Empty(),
                                       repo.getInitialVersionList());
            }
        }).getRepositoryInfo(alias);
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return CoreConstants.INSTANCE.RepositoryEditor() + " [" + alias + "]";
    }

    @WorkbenchPartView
    public UberView<RepositoryEditorPresenter> getView() {
        return view;
    }

    void onLoadMoreHistory(final int lastIndex) {
        repositoryService.call(new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback(final List<VersionRecord> versionList) {
                view.addHistory(versionList);
            }
        }).getRepositoryHistory(alias,
                                lastIndex);
    }

    void onRevert(final VersionRecord record) {
        onRevert(record,
                 null);
    }

    void onRevert(final VersionRecord record,
                  final String comment) {
        repositoryServiceEditor.call(new RemoteCallback<List<VersionRecord>>() {
            @Override
            public void callback(final List<VersionRecord> content) {
                view.reloadHistory(content);
            }
        }).revertHistory(alias,
                         root,
                         comment,
                         record);
    }

    void onGitUrlCopied(final String uri) {
        notification.fire(new NotificationEvent(CommonConstants.INSTANCE.GitUriCopied(uri)));
    }

    public void onRepositoryRemovedEvent(@Observes RepositoryRemovedEvent event) {
        if (alias.equals(event.getRepository().getAlias())) {
            placeManager.closePlace(place);
        }
    }
}