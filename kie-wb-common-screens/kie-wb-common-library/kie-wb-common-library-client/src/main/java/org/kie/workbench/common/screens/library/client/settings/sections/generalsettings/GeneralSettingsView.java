/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.sections.generalsettings;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ChangeEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTextAreaElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.settings.generalsettings.GitUrlsPresenter;

@Templated
public class GeneralSettingsView implements GeneralSettingsPresenter.View {

    private GeneralSettingsPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    @DataField("error")
    private HTMLDivElement error;

    @Inject
    @DataField("name")
    private HTMLInputElement name;

    @Inject
    @Named("span")
    @DataField("error-message")
    private HTMLElement errorMessage;

    @Inject
    @DataField("description")
    private HTMLTextAreaElement description;

    @Inject
    @DataField("git-urls-view")
    private HTMLDivElement gitUrlsView;

    @Inject
    @DataField("disable-gav-conflict-check")
    private HTMLInputElement disableGAVConflictCheck;

    @Inject
    @Named("span")
    @DataField("tooltip-disable-gav-conflict-check")
    private HTMLElement tooltipDisableGAVConflictCheck;

    @Inject
    @DataField("allow-child-gav-edition")
    private HTMLInputElement allowChildGAVEdition;

    @Inject
    @DataField("group-id")
    private HTMLInputElement groupId;

    @Inject
    @DataField("artifact-id")
    private HTMLInputElement artifactId;

    @Inject
    @DataField("version")
    private HTMLInputElement version;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Override
    public void init(final GeneralSettingsPresenter presenter) {
        this.presenter = presenter;
        this.tooltipDisableGAVConflictCheck.title = translationService
                .getTranslation(LibraryConstants.PreferenceDisableGAVConflictCheck_Tooltip);
        hideError();
    }

    @EventHandler("disable-gav-conflict-check")
    public void onDisableGavConflictCheckChanged(final ChangeEvent ignore) {
        presenter.disableGavConflictCheck(disableGAVConflictCheck.checked);
    }

    @EventHandler("allow-child-gav-edition")
    public void onAllowChildGavEditionChanged(final ChangeEvent ignore) {
        presenter.allowChildGavEdition(allowChildGAVEdition.checked);
    }

    @EventHandler("name")
    public void onNameChanged(final ChangeEvent ignore) {
        presenter.setName(name.value);
    }

    @EventHandler("description")
    public void onDescriptionChanged(final ChangeEvent ignore) {
        presenter.setDescription(description.value);
    }

    @EventHandler("group-id")
    public void onGroupIdChanged(final ChangeEvent ignore) {
        presenter.setGroupId(groupId.value);
    }

    @EventHandler("artifact-id")
    public void onArtifactIdChanged(final ChangeEvent ignore) {
        presenter.setArtifactId(artifactId.value);
    }

    @EventHandler("version")
    public void onVersionChanged(final ChangeEvent ignore) {
        presenter.setVersion(version.value);
    }

    @Override
    public String getName() {
        return name.value;
    }

    @Override
    public String getDescription() {
        return description.value;
    }

    @Override
    public String getGroupId() {
        return groupId.value;
    }

    @Override
    public String getArtifactId() {
        return artifactId.value;
    }

    @Override
    public String getVersion() {
        return version.value;
    }

    @Override
    public Boolean getConflictingGAVCheckDisabled() {
        return disableGAVConflictCheck.checked;
    }

    @Override
    public Boolean getChildGavEditEnabled() {
        return allowChildGAVEdition.checked;
    }

    @Override
    public void setGitUrlsView(final GitUrlsPresenter.View gitUrlsView) {
        this.gitUrlsView.appendChild(gitUrlsView.getElement());
    }

    @Override
    public void setName(final String name) {
        this.name.value = name;
    }

    @Override
    public void setDescription(final String description) {
        this.description.value = description;
    }

    @Override
    public void setGroupId(final String groupId) {
        this.groupId.value = groupId;
    }

    @Override
    public void setArtifactId(final String artifactId) {
        this.artifactId.value = artifactId;
    }

    @Override
    public void setVersion(final String version) {
        this.version.value = version;
    }

    @Override
    public void setConflictingGAVCheckDisabled(final boolean value) {
        this.disableGAVConflictCheck.checked = value;
    }

    @Override
    public void setChildGavEditEnabled(final boolean value) {
        this.allowChildGAVEdition.checked = value;
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
    public String getEmptyNameMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Name));
    }

    @Override
    public String getInvalidNameMessage() {
        return translationService.format(LibraryConstants.InvalidProjectName);
    }

    @Override
    public String getEmptyGroupIdMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.GroupId));
    }

    @Override
    public String getInvalidGroupIdMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.GroupId));
    }

    @Override
    public String getEmptyArtifactIdMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.ArtifactId));
    }

    @Override
    public String getInvalidArtifactIdMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.ArtifactId));
    }

    @Override
    public String getEmptyVersionMessage() {
        return translationService.format(LibraryConstants.EmptyFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Version));
    }

    @Override
    public String getInvalidVersionMessage() {
        return translationService.format(LibraryConstants.InvalidFieldValidation,
                                         translationService.getTranslation(LibraryConstants.Version));
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
