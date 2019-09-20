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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.review.tab.changedfiles;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;

@Templated
public class ChangedFilesScreenView implements ChangedFilesScreenPresenter.View,
                                               IsElement {

    private ChangedFilesScreenPresenter presenter;

    @Inject
    @DataField("files-summary")
    @Named("h3")
    private HTMLElement filesSummary;

    @Inject
    @DataField("diff-list")
    private HTMLDivElement diffList;

    @Inject
    private Elemental2DomUtil domUtil;

    @Override
    public void init(final ChangedFilesScreenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addDiffItem(final DiffItemPresenter.View item,
                            final Runnable draw) {
        this.diffList.appendChild(item.getElement());
        draw.run();
    }

    @Override
    public void clearDiffList() {
        this.domUtil.removeAllElementChildren(this.diffList);
    }

    @Override
    public void setFilesSummary(final String filesSummary) {
        this.filesSummary.textContent = filesSummary;
    }

    @Override
    public void resetAll() {
        filesSummary.textContent = "";

        showDiffList(false);
        clearDiffList();
    }

    @Override
    public void showDiffList(final boolean isVisible) {
        this.diffList.hidden = !isVisible;
    }
}
