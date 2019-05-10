/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.projecteditor.client.editor;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.widgets.client.widget.KieSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOption;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collection;
import java.util.stream.Collectors;

@Templated
public class DeploymentPopupViewImpl implements DeploymentPopupView, IsElement {

    @Inject
    @DataField
    private HTMLDivElement containerIdGroup;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement containerIdLabel;

    @Inject
    @DataField
    private HTMLInputElement containerIdText;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement containerIdHelpBlock;

    @Inject
    @DataField
    private HTMLDivElement containerAliasGroup;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement containerAliasLabel;

    @Inject
    @DataField
    private HTMLInputElement containerAliasText;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement containerAliasHelpBlock;

    @Inject
    @DataField
    private HTMLDivElement serverTemplateGroup;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement serverTemplateLabel;

    @Inject
    @DataField
    private HTMLDivElement serverTemplateDropdownContainer;

    @Inject
    private KieSelectElement serverTemplateDropdown;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement serverTemplateHelpBlock;

    @Inject
    @DataField
    private HTMLDivElement startContainerGroup;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement startContainerLabel;

    @Inject
    @DataField
    private HTMLInputElement startContainerCheck;

    @Inject
    private Elemental2DomUtil domUtil;

    private Presenter presenter;

    private BaseModal baseModal;

    @PostConstruct
    public void init() {
        baseModal = new BaseModal();

        baseModal.setTitle(ProjectEditorResources.CONSTANTS.BuildAndDeploy());
        baseModal.setDataBackdrop(ModalBackdrop.STATIC);
        baseModal.setDataKeyboard(true);
        baseModal.setFade(true);
        baseModal.setRemoveOnHide(true);

        baseModal.setBody(ElementWrapperWidget.getWidget(this.getElement()));
        baseModal.add(new ModalFooterOKCancelButtons(this::onOk, this::onCancel));

        containerIdLabel.textContent = ProjectEditorResources.CONSTANTS.ContainerId();
        containerIdText.placeholder = ProjectEditorResources.CONSTANTS.ContainerId();
        containerAliasLabel.textContent = ProjectEditorResources.CONSTANTS.ContainerAlias();
        containerAliasText.placeholder = ProjectEditorResources.CONSTANTS.ContainerAlias();
        serverTemplateLabel.textContent = ProjectEditorResources.CONSTANTS.ServerTemplate();
        startContainerLabel.textContent = ProjectEditorResources.CONSTANTS.StartContainer();
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        clearValidations();
        baseModal.show();
    }

    @Override
    public void hide() {
        baseModal.hide();
    }

    @Override
    public void initContainerId(String containerId, boolean modifyConfig) {
        containerIdText.value = containerId;
        containerIdText.readOnly = !modifyConfig;
    }

    @Override
    public void initContainerAlias(String containerAlias, boolean modifyConfig) {
        containerAliasText.value = containerAlias;
        containerAliasText.readOnly = !modifyConfig;
    }

    @Override
    public void initStartContainer(boolean startContainer, boolean modifyConfig) {
        startContainerCheck.checked = startContainer;
        startContainerCheck.disabled = !modifyConfig;
    }

    @Override
    public void disableServerTemplates() {
        serverTemplateGroup.hidden = true;
    }

    @Override
    public void initServerTemplates(final Collection<ServerTemplate> allServerTemplates, final ServerTemplate serverTemplate) {
        serverTemplateGroup.hidden = false;
        serverTemplateDropdown.setup(serverTemplateDropdownContainer,
                allServerTemplates.stream().map(template -> new KieSelectOption(template.getId(), template.getId())).collect(Collectors.toList()),
                serverTemplate.getId(),
                s -> {
                });
    }

    @Override
    public String getContainerId() {
        return containerIdText.value;
    }

    @Override
    public String getContainerAlias() {
        return containerAliasText.value;
    }

    @Override
    public String getServerTemplate() {
        return serverTemplateDropdown.getValue();
    }

    @Override
    public boolean isStartContainer() {
        return startContainerCheck.checked;
    }

    @Override
    public void invalidateContainerId(String message) {
        invalidate(containerIdGroup, containerIdHelpBlock, message);
    }

    @Override
    public void invalidateContainerAlias(String message) {
        invalidate(containerAliasGroup, containerAliasHelpBlock, message);
    }

    @Override
    public void invalidateContainerServerTemplate(String message) {
        invalidate(serverTemplateGroup, serverTemplateHelpBlock, message);
    }

    private void invalidate(final HTMLDivElement group, final HTMLElement helpBlock, final String message) {
        group.classList.add(ValidationState.ERROR.getCssName());
        helpBlock.textContent = message;
    }

    @Override
    public void clearValidations() {
        clearValidation(containerIdGroup, containerIdHelpBlock);
        clearValidation(containerAliasGroup, containerAliasHelpBlock);
        clearValidation(serverTemplateGroup, serverTemplateHelpBlock);
    }

    private void clearValidation(HTMLDivElement group, HTMLElement helpBlock) {
        group.classList.remove(ValidationState.ERROR.getCssName());
        helpBlock.textContent = "";
    }

    private void onCancel() {
        presenter.onCancel();
    }

    private void onOk() {
        presenter.onOk();
    }
}
