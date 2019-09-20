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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.overview;

import java.util.Comparator;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequest;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestComment;
import org.guvnor.structure.repositories.changerequest.portable.ChangeRequestStatus;
import org.guvnor.structure.repositories.changerequest.portable.PaginatedChangeRequestCommentList;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.ChangeRequestUtils;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.comment.CommentItemPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.rpc.SessionInfo;

@Dependent
public class OverviewScreenPresenter {

    private static final int COMMENTS_PAGE_SIZE = 10;
    private final View view;
    private final TranslationService ts;
    private final ManagedInstance<CommentItemPresenter> commentItemPresenterInstances;
    private final Caller<ChangeRequestService> changeRequestService;
    private final LibraryPlaces libraryPlaces;
    private final ChangeRequestUtils changeRequestUtils;
    private final SessionInfo sessionInfo;

    private WorkspaceProject workspaceProject;
    private long currentChangeRequestId;
    private String changeRequestAuthorId;

    private int commentCurrentPage;
    private int commentTotalPages;

    @Inject
    public OverviewScreenPresenter(final View view,
                                   final TranslationService ts,
                                   final ManagedInstance<CommentItemPresenter> commentItemPresenterInstances,
                                   final Caller<ChangeRequestService> changeRequestService,
                                   final LibraryPlaces libraryPlaces,
                                   final ChangeRequestUtils changeRequestUtils,
                                   final SessionInfo sessionInfo) {
        this.view = view;
        this.ts = ts;
        this.commentItemPresenterInstances = commentItemPresenterInstances;
        this.changeRequestService = changeRequestService;
        this.libraryPlaces = libraryPlaces;
        this.changeRequestUtils = changeRequestUtils;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void postConstruct() {
        this.workspaceProject = libraryPlaces.getActiveWorkspace();

        this.view.init(this);
        this.view.setRevertFailedTooltipText(ts.getTranslation(LibraryConstants.RevertFailedTooltip));
    }

    public View getView() {
        return view;
    }

    public void addComment() {
        final String commentText = view.getCommentText();

        if (isInvalidContent(commentText)) {
            view.setCommentInputError(ts.getTranslation(LibraryConstants.MissingCommentText));
        } else {
            changeRequestService.call(v -> {
                this.view.clearCommentInputError();
                this.view.clearCommentInputField();
            }).addComment(workspaceProject.getSpace().getName(),
                                workspaceProject.getRepository().getAlias(),
                                currentChangeRequestId,
                                commentText);
        }
    }

    public void reset() {
        this.view.resetAll();

        this.commentCurrentPage = 1;
    }

    public void setup(final ChangeRequest changeRequest,
                      final Consumer<Boolean> finishLoadingCallback) {
        this.currentChangeRequestId = changeRequest.getId();
        this.changeRequestAuthorId = changeRequest.getAuthorId();

        this.view.setStatus(changeRequestUtils.formatStatus(changeRequest.getStatus()));
        this.view.setAuthor(changeRequest.getAuthorId());
        this.view.setCreatedDate(changeRequestUtils.formatCreatedDate(changeRequest.getCreatedDate()));
        this.view.setSummary(changeRequest.getSummary());
        this.view.setDescription(changeRequest.getDescription());
        this.view.setSourceBranch(changeRequest.getSourceBranch());
        this.view.setTargetBranch(changeRequest.getTargetBranch());
        this.view.setCommentInputPlaceHolder(ts.getTranslation(LibraryConstants.LeaveAComment));
        this.view.enableSummaryEditMode(false);
        this.view.enableDescriptionEditMode(false);
        this.view.showEditModes(isUserAuthor());
        this.view.showRevertFailedTooltip(changeRequest.getStatus() == ChangeRequestStatus.REVERT_FAILED);

        this.refreshCommentList(finishLoadingCallback);
    }

    public void checkWarnConflict(final ChangeRequest changeRequest) {
        this.view.showConflictWarning(changeRequest.isConflict() &&
                                              changeRequest.getStatus() == ChangeRequestStatus.OPEN);
    }

    public void startEditSummary() {
        view.enableSummaryEditMode(true);
    }

    public void saveSummaryEdition() {
        if (isUserAuthor()) {
            final String summaryInputText = view.getSummaryInputText();

            if (!isInvalidContent(summaryInputText)) {
                changeRequestService.call(v -> {
                    view.setSummary(summaryInputText);
                    view.enableSummaryEditMode(false);
                }).updateChangeRequestSummary(workspaceProject.getSpace().getName(),
                                              workspaceProject.getRepository().getAlias(),
                                              currentChangeRequestId,
                                              summaryInputText);
            }
        }
    }

    public void cancelSummaryEdition() {
        view.enableSummaryEditMode(false);
    }

    public void startEditDescription() {
        view.enableDescriptionEditMode(true);
    }

    public void saveDescriptionEdition() {
        if (isUserAuthor()) {
            final String descriptionInputText = view.getDescriptionInputText();

            if (!isInvalidContent(descriptionInputText)) {
                changeRequestService.call(v -> {
                    view.setDescription(descriptionInputText);
                    view.enableDescriptionEditMode(false);
                }).updateChangeRequestDescription(workspaceProject.getSpace().getName(),
                                                  workspaceProject.getRepository().getAlias(),
                                                  currentChangeRequestId,
                                                  descriptionInputText);
            }
        }
    }

    public void cancelDescriptionEdition() {
        view.enableDescriptionEditMode(false);
    }

    public void nextCommentPage() {
        if (this.commentCurrentPage + 1 <= this.commentTotalPages) {
            this.commentCurrentPage++;
            this.refreshCommentList(b -> {
            });
        }
    }

    public void prevCommentPage() {
        if (this.commentCurrentPage - 1 >= 1) {
            this.commentCurrentPage--;
            this.refreshCommentList(b -> {
            });
        }
    }

    public void setCommentCurrentPage(final int currentCommentPage) {
        if (currentCommentPage <= commentTotalPages && currentCommentPage > 0) {
            this.commentCurrentPage = currentCommentPage;
            refreshCommentList(b -> {
            });
        } else {
            this.view.setCommentCurrentPage(this.commentCurrentPage);
        }
    }

    private void refreshCommentList(final Consumer<Boolean> finishLoadingCallback) {
        changeRequestService.call((final PaginatedChangeRequestCommentList paginatedList) -> {
            this.setupCommentToolbar(paginatedList.getTotal(),
                                     paginatedList.getChangeRequestComments().size());

            this.view.clearCommentList();

            paginatedList.getChangeRequestComments().stream()
                    .sorted(Comparator.comparing(ChangeRequestComment::getCreatedDate))
                    .forEach(comment -> {
                        CommentItemPresenter item = commentItemPresenterInstances.get();
                        item.setup(currentChangeRequestId,
                                   comment.getId(),
                                   comment.getAuthorId(),
                                   comment.getCreatedDate(),
                                   comment.getText());
                        this.view.addCommentItem(item.getView());
                    });

            finishLoadingCallback.accept(true);
        }).getComments(workspaceProject.getSpace().getName(),
                       workspaceProject.getRepository().getAlias(),
                       currentChangeRequestId,
                       Math.max(0, commentCurrentPage - 1),
                       COMMENTS_PAGE_SIZE);
    }

    private void setupCommentToolbar(final int total,
                                     final int paginatedCount) {
        this.setupCommentCounters(total);

        final boolean isEmpty = paginatedCount == 0;

        this.view.setCommentsHeader(isEmpty ?
                                            ts.getTranslation(LibraryConstants.NoComments) :
                                            ts.getTranslation(LibraryConstants.Comments));

        this.view.showCommentsToolbar(!isEmpty);
    }

    private boolean isInvalidContent(final String content) {
        return content == null || content.trim().isEmpty();
    }

    private boolean isUserAuthor() {
        return this.changeRequestAuthorId.equals(this.sessionInfo.getIdentity().getIdentifier());
    }

    private void setupCommentCounters(final int count) {
        int offset = (this.commentCurrentPage - 1) * COMMENTS_PAGE_SIZE;

        final int fromCount = count > 0 ? offset + 1 : offset;
        final int toCount = this.resolveCommentCounter(count,
                                                       offset + COMMENTS_PAGE_SIZE);
        final int totalCount = this.resolveCommentCounter(count,
                                                          0);

        final String indicatorText = ts.format(LibraryConstants.ItemCountIndicatorText,
                                               fromCount,
                                               toCount,
                                               totalCount);
        this.view.setCommentPageIndicator(indicatorText);

        this.commentTotalPages = (int) Math.ceil(count / (float) COMMENTS_PAGE_SIZE);

        final String totalText = ts.format(LibraryConstants.OfN,
                                           Math.max(this.commentTotalPages, 1));
        this.view.setCommentTotalPages(totalText);

        this.view.setCommentCurrentPage(this.commentCurrentPage);
        this.checkCommentPaginationButtons();
    }

    private void checkCommentPaginationButtons() {
        boolean isPreviousButtonEnabled = this.commentCurrentPage > 1;
        boolean isNextButtonEnabled = this.commentCurrentPage < this.commentTotalPages;

        this.view.enableCommentPreviousButton(isPreviousButtonEnabled);
        this.view.enableCommentNextButton(isNextButtonEnabled);
    }

    private int resolveCommentCounter(final int numberOfComments,
                                      final int otherCounter) {
        if (numberOfComments < otherCounter || otherCounter == 0) {
            return numberOfComments;
        } else {
            return otherCounter;
        }
    }

    public interface View extends UberElemental<OverviewScreenPresenter> {

        void setStatus(final String status);

        void setAuthor(final String author);

        void setCreatedDate(final String createdDate);

        void setSummary(final String summary);

        String getSummaryInputText();

        void setDescription(final String description);

        String getDescriptionInputText();

        void setSourceBranch(final String sourceBranch);

        void setTargetBranch(final String targetBranch);

        void setCommentInputPlaceHolder(final String placeHolder);

        void addCommentItem(final CommentItemPresenter.View item);

        void clearCommentList();

        void setCommentInputError(final String errorMsg);

        void clearCommentInputError();

        String getCommentText();

        void clearCommentInputField();

        void enableSummaryEditMode(final boolean isEnabled);

        void enableDescriptionEditMode(final boolean isEnabled);

        void showEditModes(final boolean isVisible);

        void showConflictWarning(final boolean isVisible);

        void resetAll();

        void setCommentsHeader(final String header);

        void setCommentCurrentPage(final int page);

        void setCommentPageIndicator(final String pageIndicatorText);

        void setCommentTotalPages(final String totalText);

        void enableCommentPreviousButton(final boolean isEnabled);

        void enableCommentNextButton(final boolean isEnabled);

        void showCommentsToolbar(final boolean isVisible);

        void setRevertFailedTooltipText(final String tooltip);

        void showRevertFailedTooltip(final boolean isVisible);
    }
}
