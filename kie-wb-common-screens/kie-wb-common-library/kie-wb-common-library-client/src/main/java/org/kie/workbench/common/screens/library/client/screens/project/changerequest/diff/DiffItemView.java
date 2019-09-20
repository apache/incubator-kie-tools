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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.diff2html.Diff2Html;
import org.uberfire.ext.widgets.common.client.diff2html.DiffOutputFormat;

@Dependent
@Templated
public class DiffItemView implements DiffItemPresenter.View,
                                     IsElement {

    private static final String SIZE_100P = "100%";
    private static final int DIFF_CONTAINER_HEIGHT = 396;
    private static final String DIFF_CONTAINER_HEIGHT_PX = DIFF_CONTAINER_HEIGHT + "px";
    private static int nextId = 0;
    private DiffItemPresenter presenter;

    @Inject
    @DataField("conflict-label")
    @Named("span")
    private HTMLElement conflictLabel;

    @Inject
    @DataField("change-type")
    @Named("span")
    private HTMLElement changeType;

    @Inject
    @DataField("filename")
    @Named("span")
    private HTMLElement filename;

    @Inject
    @DataField("collapse-container")
    private HTMLDivElement collapseContainer;

    @Inject
    @DataField("collapse-link")
    private HTMLAnchorElement collapseLink;

    @Inject
    @DataField("textual-diff-container")
    private HTMLDivElement textualDiffContainer;

    @Inject
    @DataField("custom-diff-container")
    private HTMLDivElement customDiffContainer;

    @Inject
    @DataField("left-container")
    private HTMLDivElement customLeftContainer;

    @Inject
    @DataField("right-container")
    private HTMLDivElement customRightContainer;

    @Inject
    @DataField("unmodified-file")
    private HTMLDivElement unmodifiedFile;

    @Inject
    @DataField("binary-file")
    private HTMLDivElement binaryFile;

    @Inject
    private Elemental2DomUtil domUtil;

    private Diff2Html diff2Html;

    @Override
    public void init(DiffItemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setupTextualContent(final String filename,
                                    final String changeType,
                                    final boolean conflict) {
        this.setup(filename, changeType, conflict);

        this.textualDiffContainer.hidden = false;
    }

    @Override
    public void setupCustomContent(final String filename,
                                   final String changeType,
                                   final boolean conflict) {
        this.setup(filename, changeType, conflict);

        this.customDiffContainer.hidden = false;
    }

    @Override
    public HTMLElement getCustomLeftContainer() {
        return customLeftContainer;
    }

    @Override
    public HTMLElement getCustomRightContainer() {
        return customRightContainer;
    }

    @Override
    public void clearCustomLeftContainer() {
        domUtil.removeAllElementChildren(customLeftContainer);
    }

    @Override
    public void clearCustomRightContainer() {
        domUtil.removeAllElementChildren(customRightContainer);
    }

    @Override
    public void expandCollapsibleContainer(final boolean isOpened) {
        if (isOpened) {
            collapseLink.classList.remove("collapsed");
            collapseContainer.classList.add("in");
            collapseContainer.setAttribute("aria-expanded", String.valueOf(true));
        } else {
            collapseLink.classList.add("collapsed");
            collapseContainer.classList.remove("in");
            collapseContainer.setAttribute("aria-expanded", String.valueOf(false));
        }
    }

    @Override
    public void drawBinaryContent() {
        binaryFile.hidden = false;
    }

    @Override
    public void drawUnmodifiedContent() {
        unmodifiedFile.hidden = false;
    }

    @Override
    public void removeTextualContent() {
        if (this.diff2Html != null) {
            domUtil.removeAllElementChildren(textualDiffContainer);
            this.diff2Html = null;
        }
    }

    @Override
    public void removeCollapseLink() {
        this.collapseLink.href = "";
        this.collapseLink.style.cursor = "auto";
    }

    @Override
    public void drawTextualContent(final String diffText,
                                   final boolean isUnified) {
        if (this.diff2Html == null) {
            this.diff2Html = new Diff2Html(this.textualDiffContainer.id,
                                           isUnified ? DiffOutputFormat.LINE_BY_LINE : DiffOutputFormat.SIDE_BY_SIDE,
                                           diffText);
            this.diff2Html.draw();
            this.diff2Html.configContainerHeight(DIFF_CONTAINER_HEIGHT);
        }
    }

    @Override
    public void expandCustomLeftContainer() {
        this.customLeftContainer.hidden = false;
        this.customLeftContainer.style.width = CSSProperties.WidthUnionType.of(SIZE_100P);

        this.customRightContainer.hidden = true;
    }

    @Override
    public void expandCustomRightContainer() {
        this.customLeftContainer.hidden = true;

        this.customRightContainer.hidden = false;
        this.customRightContainer.style.width = CSSProperties.WidthUnionType.of(SIZE_100P);
    }

    @EventHandler("collapse-link")
    public void onCollapseLinkClicked(final ClickEvent event) {
        if (!this.collapseLink.href.equals("")) {
            presenter.toggleCollapsibleContainerState();
        }
    }

    private void setup(final String filename,
                       final String changeType,
                       final boolean conflict) {
        final String elementId = "_" + DiffItemView.class.getSimpleName() + "GWT" + nextId++;
        final String containerId = "_collapsible-container" + elementId;

        this.filename.textContent = filename;
        this.changeType.textContent = changeType + " - ";

        this.textualDiffContainer.id = "_textual-diff-container" + elementId;
        this.textualDiffContainer.style.maxHeight = CSSProperties.MaxHeightUnionType.of(DIFF_CONTAINER_HEIGHT_PX);

        this.customDiffContainer.id = "_custom-diff-container" + elementId;
        this.customLeftContainer.style.height = CSSProperties.HeightUnionType.of(DIFF_CONTAINER_HEIGHT_PX);
        this.customRightContainer.style.height = CSSProperties.HeightUnionType.of(DIFF_CONTAINER_HEIGHT_PX);

        this.textualDiffContainer.hidden = true;
        this.customDiffContainer.hidden = true;

        this.binaryFile.hidden = true;
        this.unmodifiedFile.hidden = true;

        this.collapseContainer.id = containerId;
        this.collapseLink.href = "#" + containerId;

        this.conflictLabel.hidden = !conflict;
    }
}