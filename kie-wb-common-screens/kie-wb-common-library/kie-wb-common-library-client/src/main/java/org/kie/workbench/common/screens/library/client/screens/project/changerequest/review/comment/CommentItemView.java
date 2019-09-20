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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class CommentItemView implements CommentItemPresenter.View,
                                        IsElement {

    private CommentItemPresenter presenter;

    @Inject
    @DataField("delete")
    private HTMLAnchorElement delete;

    @Inject
    @DataField("container")
    private HTMLDivElement container;

    @Inject
    @DataField("comment-author")
    @Named("span")
    private HTMLElement commentAuthor;

    @Inject
    @DataField("comment-date")
    @Named("span")
    private HTMLElement commentDate;

    @Inject
    @DataField("comment-text")
    @Named("pre")
    private HTMLElement commentText;

    @Inject
    @DataField("kebab-actions")
    private HTMLDivElement kebabActions;

    @Override
    public void init(final CommentItemPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("delete")
    public void onDeleteClicked(final ClickEvent event) {
        presenter.delete();
    }

    @Override
    public void delete() {
        // Use of removeChild() instead of remove() for IE compatibility purposes
        for (int i = container.childNodes.getLength() - 1; i > 0; i--) {
            container.removeChild(container.childNodes.item(i));
        }
    }

    @Override
    public void setAuthor(final String author) {
        commentAuthor.textContent = author;
    }

    @Override
    public void setDate(final String date) {
        commentDate.textContent = date;
    }

    @Override
    public void setText(final String text) {
        commentText.textContent = text;
    }

    @Override
    public void showActions(final boolean isVisible) {
        kebabActions.hidden = !isVisible;
    }
}
