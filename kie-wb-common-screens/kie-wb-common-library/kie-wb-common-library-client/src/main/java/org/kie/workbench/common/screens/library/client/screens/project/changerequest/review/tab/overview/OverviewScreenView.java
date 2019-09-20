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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLTextAreaElement;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.comment.CommentItemPresenter;
import org.uberfire.client.views.pfly.widgets.ValidationState;

@Templated
public class OverviewScreenView implements OverviewScreenPresenter.View,
                                           IsElement {

    private static final String PLACE_HOLDER = "placeholder";
    private static final String EMPTY_CONTENT = "";

    private OverviewScreenPresenter presenter;

    @Inject
    @DataField("status")
    @Named("span")
    private HTMLElement status;

    @Inject
    @DataField("author")
    @Named("span")
    private HTMLElement author;

    @Inject
    @DataField("created-date")
    @Named("span")
    private HTMLElement createdDate;

    @Inject
    @DataField("summary")
    @Named("span")
    private HTMLElement summary;

    @Inject
    @DataField("edit-summary")
    private HTMLAnchorElement editSummary;

    @Inject
    @DataField("summary-edit-group")
    private HTMLDivElement summaryEditGroup;

    @Inject
    @DataField("summary-edit-input")
    private HTMLInputElement summaryEditInput;

    @Inject
    @DataField("summary-edit-save")
    private HTMLButtonElement summaryEditSave;

    @Inject
    @DataField("summary-edit-cancel")
    private HTMLButtonElement summaryEditCancel;

    @Inject
    @DataField("clear-summary-button")
    private HTMLButtonElement clearSummary;

    @Inject
    @DataField("description")
    @Named("pre")
    private HTMLElement description;

    @Inject
    @DataField("edit-description")
    private HTMLAnchorElement editDescription;

    @Inject
    @DataField("description-edit-group")
    private HTMLDivElement descriptionEditGroup;

    @Inject
    @DataField("description-edit-input")
    private HTMLTextAreaElement descriptionEditInput;

    @Inject
    @DataField("description-edit-save")
    private HTMLButtonElement descriptionEditSave;

    @Inject
    @DataField("description-edit-cancel")
    private HTMLButtonElement descriptionEditCancel;

    @Inject
    @DataField("source-branch")
    @Named("span")
    private HTMLElement sourceBranch;

    @Inject
    @DataField("target-branch")
    @Named("span")
    private HTMLElement targetBranch;

    @Inject
    @DataField("comment-list")
    private HTMLDivElement commentList;

    @Inject
    @DataField("add-comment-button")
    private HTMLButtonElement addCommentButton;

    @Inject
    @DataField("comment-input-group")
    private HTMLDivElement commentInputGroup;

    @Inject
    @DataField("comment-input-help-inline")
    private HelpBlock commentInputHelpInline;

    @Inject
    @DataField("comment-input")
    private HTMLTextAreaElement commentInput;

    @Inject
    @DataField("conflict-warning")
    private HTMLDivElement conflictWarning;

    @Inject
    @DataField("comment-page-indicator")
    private HTMLLabelElement commentPageIndicator;

    @Inject
    @DataField("comment-total-pages")
    @Named("span")
    private HTMLElement commentTotalPages;

    @Inject
    @DataField("comment-current-page")
    private HTMLInputElement commentCurrentPage;

    @Inject
    @DataField("comment-prev-page")
    private HTMLButtonElement commentPrevPage;

    @Inject
    @DataField("comment-next-page")
    private HTMLButtonElement commentNextPage;

    @Inject
    @DataField("comments-toolbar")
    private HTMLDivElement commentsToolbar;

    @Inject
    @DataField("comments-header")
    @Named("h3")
    private HTMLElement commentsHeader;

    @Inject
    @DataField("revert-failed-tooltip")
    @Named("span")
    private HTMLElement revertFailedTooltip;

    @Inject
    private Elemental2DomUtil domUtil;

    @Override
    public void init(final OverviewScreenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setStatus(final String status) {
        this.status.textContent = status;
    }

    @Override
    public void setAuthor(final String author) {
        this.author.textContent = author;
    }

    @Override
    public void setCreatedDate(final String createdDate) {
        this.createdDate.textContent = createdDate;
    }

    @Override
    public void setSummary(final String summary) {
        this.summary.textContent = summary;
    }

    @Override
    public String getSummaryInputText() {
        return summaryEditInput.value;
    }

    @Override
    public void setDescription(final String description) {
        this.description.textContent = description;
    }

    @Override
    public String getDescriptionInputText() {
        return descriptionEditInput.value;
    }

    @Override
    public void setSourceBranch(final String sourceBranch) {
        this.sourceBranch.textContent = sourceBranch;
    }

    @Override
    public void setTargetBranch(final String targetBranch) {
        this.targetBranch.textContent = targetBranch;
    }

    @Override
    public void setCommentsHeader(final String header) {
        commentsHeader.textContent = header;
    }

    @Override
    public void setCommentInputPlaceHolder(final String placeHolder) {
        this.commentInput.setAttribute(PLACE_HOLDER, placeHolder);
    }

    @Override
    public void addCommentItem(final CommentItemPresenter.View item) {
        this.commentList.appendChild(item.getElement());
    }

    @Override
    public void clearCommentList() {
        this.domUtil.removeAllElementChildren(this.commentList);
    }

    @Override
    public void setCommentInputError(final String errorMsg) {
        commentInputGroup.classList.add(ValidationState.ERROR.getCssName());
        commentInputHelpInline.setText(errorMsg);
    }

    @Override
    public void clearCommentInputError() {
        commentInputGroup.classList.remove(ValidationState.ERROR.getCssName());
        commentInputHelpInline.clearError();
    }

    @Override
    public String getCommentText() {
        return commentInput.value;
    }

    @Override
    public void clearCommentInputField() {
        this.commentInput.value = "";
        this.clearCommentInputError();
    }

    @Override
    public void enableSummaryEditMode(final boolean isEnabled) {
        if (isEnabled) {
            summaryEditSave.disabled = true;
            summaryEditInput.value = summary.textContent;
            summary.hidden = true;
            editSummary.hidden = true;
            summaryEditGroup.hidden = false;
        } else {
            summary.hidden = false;
            editSummary.hidden = false;
            summaryEditGroup.hidden = true;
        }
    }

    @Override
    public void enableDescriptionEditMode(final boolean isEnabled) {
        if (isEnabled) {
            descriptionEditSave.disabled = true;
            descriptionEditInput.value = description.textContent;
            description.hidden = true;
            editDescription.hidden = true;
            descriptionEditGroup.hidden = false;
        } else {
            description.hidden = false;
            editDescription.hidden = false;
            descriptionEditGroup.hidden = true;
        }
    }

    @Override
    public void showEditModes(final boolean isVisible) {
        editSummary.hidden = !isVisible;
        editDescription.hidden = !isVisible;
    }

    @Override
    public void showConflictWarning(final boolean isVisible) {
        this.conflictWarning.hidden = !isVisible;
    }

    @Override
    public void resetAll() {
        this.showCommentsToolbar(false);
        this.showConflictWarning(false);
        this.clearCommentInputField();
        this.clearCommentList();
        this.enableSummaryEditMode(false);
        this.enableDescriptionEditMode(false);
        this.showRevertFailedTooltip(false);

        summary.textContent = EMPTY_CONTENT;
        description.textContent = EMPTY_CONTENT;
        status.textContent = EMPTY_CONTENT;
        author.textContent = EMPTY_CONTENT;
        createdDate.textContent = EMPTY_CONTENT;
        sourceBranch.textContent = EMPTY_CONTENT;
        targetBranch.textContent = EMPTY_CONTENT;
        commentsHeader.textContent = EMPTY_CONTENT;
    }

    @Override
    public void setCommentCurrentPage(final int currentPage) {
        this.commentCurrentPage.value = String.valueOf(currentPage);
    }

    @Override
    public void setCommentPageIndicator(final String pageIndicatorText) {
        this.commentPageIndicator.textContent = pageIndicatorText;
    }

    @Override
    public void setCommentTotalPages(final String totalText) {
        this.commentTotalPages.textContent = totalText;
    }

    @Override
    public void enableCommentPreviousButton(final boolean isEnabled) {
        this.commentPrevPage.disabled = !isEnabled;
    }

    @Override
    public void enableCommentNextButton(final boolean isEnabled) {
        this.commentNextPage.disabled = !isEnabled;
    }

    @Override
    public void showCommentsToolbar(final boolean isVisible) {
        this.commentsToolbar.hidden = !isVisible;
    }

    @Override
    public void setRevertFailedTooltipText(final String tooltip) {
        this.revertFailedTooltip.title = tooltip;
    }

    @Override
    public void showRevertFailedTooltip(final boolean isVisible) {
        this.revertFailedTooltip.hidden = !isVisible;
    }

    @EventHandler("add-comment-button")
    public void onAddCommentButtonClicked(final ClickEvent event) {
        this.presenter.addComment();
    }

    @EventHandler("edit-summary")
    public void onEditSummaryClicked(final ClickEvent event) {
        presenter.startEditSummary();
    }

    @EventHandler("edit-description")
    public void onEditDescriptionClicked(final ClickEvent event) {
        presenter.startEditDescription();
    }

    @EventHandler("summary-edit-save")
    public void onSummaryEditSaveClicked(final ClickEvent event) {
        presenter.saveSummaryEdition();
    }

    @EventHandler("summary-edit-cancel")
    public void onSummaryEditCancelClicked(final ClickEvent event) {
        presenter.cancelSummaryEdition();
    }

    @EventHandler("description-edit-save")
    public void onDescriptionEditSaveClicked(final ClickEvent event) {
        presenter.saveDescriptionEdition();
    }

    @EventHandler("description-edit-cancel")
    public void onDescriptionEditCancelClicked(final ClickEvent event) {
        presenter.cancelDescriptionEdition();
    }

    @EventHandler("summary-edit-input")
    public void onSummaryEditInputKeyUp(final KeyUpEvent event) {
        checkSummarySaveButtonState();
    }

    @EventHandler("description-edit-input")
    public void onDescriptionEditInputKeyUp(final KeyUpEvent event) {
        checkDescriptionSaveButtonState();
    }

    @EventHandler("clear-summary-button")
    public void onClearSummaryClicked(final ClickEvent event) {
        summaryEditInput.value = "";
        checkSummarySaveButtonState();
        summaryEditInput.focus();
    }

    @EventHandler("comment-current-page")
    public void onCommentCurrentPageKeyUp(final KeyUpEvent event) {
        String pageNumber = commentCurrentPage.value;
        if (pageNumber.matches("\\d+")) {
            presenter.setCommentCurrentPage(Integer.parseInt(pageNumber));
        }
    }

    @EventHandler("comment-next-page")
    public void onCommentNextPageClicked(final ClickEvent event) {
        this.presenter.nextCommentPage();
    }

    @EventHandler("comment-prev-page")
    public void onCommentPrevPageClicked(final ClickEvent event) {
        this.presenter.prevCommentPage();
    }

    private void checkSummarySaveButtonState() {
        summaryEditSave.disabled = summaryEditInput.value == null ||
                summaryEditInput.value.trim().equals("") ||
                summaryEditInput.value.equals(summary.textContent);
    }

    private void checkDescriptionSaveButtonState() {
        descriptionEditSave.disabled = descriptionEditInput.value == null ||
                descriptionEditInput.value.trim().equals("") ||
                descriptionEditInput.value.equals(description.textContent);
    }
}
