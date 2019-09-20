/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.comment;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.kie.workbench.common.screens.library.client.util.DateUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.rpc.SessionInfo;

@Dependent
public class CommentItemPresenter {

    private final View view;
    private final DateUtils dateUtils;
    private final Caller<ChangeRequestService> changeRequestService;
    private final SessionInfo sessionInfo;
    private final LibraryPlaces libraryPlaces;
    private Long changeRequestId;
    private Long commentId;
    private String authorId;

    @Inject
    public CommentItemPresenter(final View view,
                                final DateUtils dateUtils,
                                final Caller<ChangeRequestService> changeRequestService,
                                final SessionInfo sessionInfo,
                                final LibraryPlaces libraryPlaces) {
        this.view = view;
        this.dateUtils = dateUtils;
        this.changeRequestService = changeRequestService;
        this.sessionInfo = sessionInfo;
        this.libraryPlaces = libraryPlaces;
    }

    @PostConstruct
    public void postConstruct() {
        this.view.init(this);
    }

    public void setup(final Long changeRequestId,
                      final Long commentId,
                      final String authorId,
                      final Date date,
                      final String text) {
        this.changeRequestId = changeRequestId;
        this.commentId = commentId;
        this.authorId = authorId;

        this.view.setAuthor(authorId);
        this.view.setDate(this.dateUtils.format(date));
        this.view.setText(text);
        this.view.showActions(isUserAuthor());
    }

    public View getView() {
        return view;
    }

    public void delete() {
        if (isUserAuthor()) {
            final WorkspaceProject workspaceProject = this.libraryPlaces.getActiveWorkspace();
            changeRequestService.call()
                    .deleteComment(workspaceProject.getSpace().getName(),
                                   workspaceProject.getRepository().getAlias(),
                                   this.changeRequestId,
                                   this.commentId);
        }
    }

    private boolean isUserAuthor() {
        return this.authorId.equals(this.sessionInfo.getIdentity().getIdentifier());
    }

    public interface View extends UberElemental<CommentItemPresenter> {

        void delete();

        void setAuthor(final String author);

        void setDate(final String date);

        void setText(final String text);

        void showActions(final boolean isVisible);
    }
}
