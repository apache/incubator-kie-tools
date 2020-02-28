/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.branchmanagement;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOption;

@Templated
public class BranchManagementView implements BranchManagementPresenter.View {

    private BranchManagementPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    @DataField("error")
    private HTMLDivElement error;

    @Inject
    @Named("span")
    @DataField("error-message")
    private HTMLElement errorMessage;

    @Inject
    @DataField("empty-state-container")
    private HTMLDivElement emptyStateContainer;

    @Inject
    @DataField("role-access-container")
    private HTMLDivElement roleAccessContainer;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    @DataField("branches-select")
    private KieSelectElement branchesSelect;

    @Inject
    @Named("tbody")
    @DataField("role-access-table")
    private HTMLTableSectionElement roleAccessTable;

    @Inject
    @Named("span")
    @DataField("read-tooltip")
    private HTMLElement readTooltip;

    @Inject
    @Named("span")
    @DataField("write-tooltip")
    private HTMLElement writeTooltip;

    @Inject
    @Named("span")
    @DataField("delete-tooltip")
    private HTMLElement deleteTooltip;

    @Inject
    @Named("span")
    @DataField("deploy-tooltip")
    private HTMLElement deployTooltip;

    @Override
    public void init(final BranchManagementPresenter presenter) {
        this.presenter = presenter;
        hideError();

        this.emptyStateContainer.hidden = true;

        this.readTooltip.title = translationService.getTranslation(LibraryConstants.BranchManagementReadPermissionTooltip);
        this.writeTooltip.title = translationService.getTranslation(LibraryConstants.BranchManagementWritePermissionTooltip);
        this.deleteTooltip.title = translationService.getTranslation(LibraryConstants.BranchManagementDeletePermissionTooltip);
        this.deployTooltip.title = translationService.getTranslation(LibraryConstants.BranchManagementDeployPermissionTooltip);
    }

    @Override
    public void showError(final String errorMessage) {
        this.errorMessage.innerHTML += errorMessage;
        this.errorMessage.innerHTML += "<br/>";
        this.error.hidden = false;
    }

    @Override
    public void hideError() {
        elemental2DomUtil.removeAllElementChildren(errorMessage);
        this.error.hidden = true;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }

    @Override
    public Element getRoleAccessTable() {
        return roleAccessTable;
    }

    @Override
    public void setupBranchSelect(List<KieSelectOption> options, String initialValue, Consumer<String> onChange) {
        branchesSelect.setup(options, initialValue, onChange);
    }

    @Override
    public void showEmptyState() {
        this.emptyStateContainer.hidden = false;
        this.roleAccessContainer.hidden = true;
    }
}
