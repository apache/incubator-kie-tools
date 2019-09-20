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

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.submit;

import java.util.List;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLTextAreaElement;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff.DiffItemPresenter;
import org.uberfire.client.views.pfly.widgets.ValidationState;

@Templated
public class SubmitChangeRequestScreenView implements SubmitChangeRequestScreenPresenter.View,
                                                      IsElement {

    private SubmitChangeRequestScreenPresenter presenter;

    @Inject
    @DataField("conflict-warning")
    private HTMLDivElement conflictWarning;

    @Inject
    @DataField("title")
    private HTMLDivElement title;

    @Inject
    @DataField("cancel")
    private HTMLButtonElement cancelButton;

    @Inject
    @DataField("submit")
    private HTMLButtonElement submitButton;

    @Inject
    @DataField("summary-group")
    private HTMLDivElement summaryGroup;

    @Inject
    @DataField("summary-help-inline")
    private HelpBlock summaryHelpInline;

    @Inject
    @DataField("summary")
    private HTMLInputElement summary;

    @Inject
    @DataField("description-group")
    private HTMLDivElement descriptionGroup;

    @Inject
    @DataField("description-help-inline")
    private HelpBlock descriptionHelpInline;

    @Inject
    @DataField("description")
    private HTMLTextAreaElement description;

    @Inject
    @DataField("branch-select")
    private HTMLSelectElement branchSelect;

    @Inject
    @DataField("files-summary")
    private HTMLLabelElement filesSummary;

    @Inject
    @DataField("diff-list")
    private HTMLDivElement diffList;

    @Inject
    private ManagedInstance<SubmitChangeRequestScreenView.BranchOptionView> options;

    @Inject
    private Elemental2DomUtil domUtil;

    @Inject
    private TranslationService ts;

    @Override
    public void init(SubmitChangeRequestScreenPresenter presenter) {
        this.presenter = presenter;

        branchSelect.onchange = this::onBranchSelectChange;
    }

    @Override
    public void setTitle(final String title) {
        this.title.textContent = title;
    }

    @Override
    public void setDestinationBranches(final List<String> branches,
                                       int selectedIdx) {
        branchSelect.innerHTML = "";

        branches.forEach(branch -> {
            final BranchOptionView option = options.get();
            option.setup(branch);
            branchSelect.appendChild(option.getElement());
        });

        branchSelect.selectedIndex = selectedIdx;
    }

    @Override
    public void showWarning(final boolean isVisible) {
        this.conflictWarning.hidden = !isVisible;
    }

    @Override
    public void addDiffItem(final DiffItemPresenter.View item,
                            final Runnable draw) {
        this.diffList.appendChild(item.getElement());
        draw.run();
    }

    @Override
    public String getSummary() {
        return summary.value;
    }

    @Override
    public String getDescription() {
        return description.value;
    }

    @Override
    public void setDescription(final String description) {
        this.description.textContent = description;
    }

    @Override
    public void clearErrors() {
        summaryGroup.classList.remove(ValidationState.ERROR.getCssName());
        descriptionGroup.classList.remove(ValidationState.ERROR.getCssName());
        summaryHelpInline.clearError();
        descriptionHelpInline.clearError();
    }

    @Override
    public void clearDiffList() {
        this.domUtil.removeAllElementChildren(this.diffList);
    }

    @Override
    public void setSummaryError() {
        summaryGroup.classList.add(ValidationState.ERROR.getCssName());
        summaryHelpInline.setText(ts.getTranslation(LibraryConstants.MissingSummaryForChangeRequestHelp));
    }

    @Override
    public void setDescriptionError() {
        descriptionGroup.classList.add(ValidationState.ERROR.getCssName());
        descriptionHelpInline.setText(ts.getTranslation(LibraryConstants.MissingDescriptionForChangeRequestHelp));
    }

    @Override
    public void showDiff(final boolean isVisible) {
        diffList.hidden = !isVisible;
    }

    @Override
    public void clearInputFields() {
        this.summary.value = "";
        this.description.value = "";
        this.clearErrors();
    }

    @Override
    public void resetAll() {
        clearInputFields();
        showWarning(false);
        setFilesSummary("");
        showDiff(false);
    }

    @Override
    public void setFilesSummary(final String text) {
        filesSummary.textContent = text;
    }

    @Override
    public void enableSubmitButton(final boolean isEnabled) {
        this.submitButton.disabled = !isEnabled;
    }

    @EventHandler("cancel")
    public void onCancelClicked(final ClickEvent event) {
        presenter.cancel();
    }

    @EventHandler("submit")
    public void onSubmitClicked(final ClickEvent event) {
        presenter.submit();
    }

    private Object onBranchSelectChange(Object event) {
        presenter.selectBranch(branchSelect.value);
        return null;
    }

    @Templated("SubmitChangeRequestScreenView.html#branch-select-option")
    public static class BranchOptionView implements IsElement {

        @Inject
        @DataField("branch-select-option")
        HTMLOptionElement option;

        public void setup(final String branchName) {
            option.value = branchName;
            option.innerHTML = branchName;
        }
    }
}