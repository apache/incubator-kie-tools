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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.list.listitem;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.user.client.ui.IsWidget;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.TemplateUtil;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ChangeRequestListItemView implements IsElement {

    @Inject
    @DataField("change-request-title-link")
    private HTMLAnchorElement titleLink;

    @Inject
    @DataField("change-request-author")
    @Named("span")
    private HTMLElement author;

    @Inject
    @DataField("change-request-created-date")
    @Named("span")
    private HTMLElement createdDate;

    @Inject
    @DataField("change-request-file-counter")
    @Named("span")
    private HTMLElement fileCounter;

    @Inject
    @DataField("change-request-comment-counter")
    @Named("span")
    private HTMLElement commentCounter;

    @Inject
    @DataField("item-icon")
    private HTMLDivElement itemIcon;

    public void init(final IsWidget icon,
                     final String title,
                     final String author,
                     final String createdDate,
                     final String fileCounter,
                     final String commentCounter,
                     final Command select) {
        if (icon != null) {
            final HTMLElement itemIconHtml = TemplateUtil.nativeCast(icon.asWidget().getElement());
            this.itemIcon.appendChild(itemIconHtml);
        }

        this.titleLink.textContent = title;
        this.author.textContent = author;
        this.createdDate.textContent = createdDate;
        this.fileCounter.textContent = fileCounter;
        this.commentCounter.textContent = commentCounter;
        this.titleLink.onclick = e -> {
            select.execute();
            return null;
        };
    }
}