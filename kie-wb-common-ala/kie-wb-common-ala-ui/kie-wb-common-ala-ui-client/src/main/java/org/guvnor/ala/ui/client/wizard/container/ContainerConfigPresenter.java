/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.ala.ui.client.wizard.container;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.util.PopupHelper;
import org.guvnor.ala.ui.client.validation.ProvisioningClientValidationService;
import org.guvnor.ala.ui.client.widget.FormStatus;
import org.guvnor.ala.ui.client.widget.artifact.ArtifactSelectorPresenter;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.editor.commons.client.validation.ValidatorCallback;

import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_AllFieldsMustBeCompletedErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_ContainerNameAlreadyInUseErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_InvalidContainerNameErrorMessage;
import static org.guvnor.ala.ui.client.resources.i18n.GuvnorAlaUIConstants.ContainerConfigPresenter_LoadGAVErrorMessage;
import static org.guvnor.ala.ui.client.util.UIUtil.EMPTY_STRING;
import static org.guvnor.ala.ui.client.util.UIUtil.trimOrGetEmpty;

@Dependent
public class ContainerConfigPresenter {

    public interface View
            extends UberElement<ContainerConfigPresenter> {

        String getContainerName();

        void setContainerName(final String containerName);

        String getGroupId();

        void setGroupId(final String groupId);

        String getArtifactId();

        void setArtifactId(final String artifactId);

        String getVersion();

        void setVersion(final String version);

        void setArtifactSelectorPresenter(final IsElement artifactSelector);

        void clear();

        void setContainerNameStatus(final FormStatus status);

        void setGroupIdStatus(final FormStatus status);

        void setArtifactIdStatus(final FormStatus status);

        void setVersionStatus(final FormStatus status);

        void setContainerNameHelpText(final String containerNameHelpText);

        void clearContainerNameHelpText();

        void showFormError(final String error);

        void clearFormError();
    }

    private final View view;

    private final ArtifactSelectorPresenter artifactSelector;

    private PopupHelper popupHelper;

    private TranslationService translationService;

    private final Caller<M2RepoService> m2RepoService;

    private final ProvisioningClientValidationService validationService;

    private List<String> alreadyInUseNames;

    boolean containerNameValid = false;

    @Inject
    public ContainerConfigPresenter(final View view,
                                    final ArtifactSelectorPresenter artifactSelector,
                                    final TranslationService translationService,
                                    final PopupHelper popupHelper,
                                    final Caller<M2RepoService> m2RepoService,
                                    final ProvisioningClientValidationService validationService) {
        this.view = view;
        this.artifactSelector = artifactSelector;
        this.popupHelper = popupHelper;
        this.translationService = translationService;
        this.m2RepoService = m2RepoService;
        this.validationService = validationService;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        artifactSelector.setArtifactSelectHandler(this::onArtifactSelected);
        view.setArtifactSelectorPresenter(artifactSelector.getView());
    }

    public IsElement getView() {
        return view;
    }

    public void clear() {
        artifactSelector.clear();
        view.clear();
        containerNameValid = false;
    }

    public void setup(final List<String> alreadyInUseNames) {
        this.alreadyInUseNames = new ArrayList<>(alreadyInUseNames);
    }

    public boolean validateForSubmit() {
        boolean isValid = isValidContainerName() &&
                isValidGroupId() &&
                isValidArtifactId() &&
                isValidVersion();
        if (!isValid) {
            view.showFormError(translationService.getTranslation(ContainerConfigPresenter_AllFieldsMustBeCompletedErrorMessage));
        } else {
            view.clearFormError();
        }
        return isValid;
    }

    public ContainerConfig getContainerConfig() {
        return new ContainerConfig(getContainerName(),
                                   getGroupId(),
                                   getArtifactId(),
                                   getVersion());
    }

    private String getContainerName() {
        return view.getContainerName();
    }

    private String getGroupId() {
        return trimOrGetEmpty(view.getGroupId());
    }

    private String getArtifactId() {
        return trimOrGetEmpty(view.getArtifactId());
    }

    private String getVersion() {
        return trimOrGetEmpty(view.getVersion());
    }

    private boolean isValidContainerName() {
        return containerNameValid;
    }

    private boolean isAlreadyInUse(final String containerName) {
        return alreadyInUseNames != null && alreadyInUseNames.contains(containerName);
    }

    private boolean isValidGroupId() {
        return !getGroupId().isEmpty();
    }

    private boolean isValidArtifactId() {
        return !getArtifactId().isEmpty();
    }

    private boolean isValidVersion() {
        return !getVersion().isEmpty();
    }

    protected void onArtifactSelected(final String path) {
        view.clearFormError();
        m2RepoService.call(getLoadGAVSuccessCallback(),
                           getLoadGAVErrorCallback()).loadGAVFromJar(path);
    }

    protected void onContainerNameChange() {
        view.clearContainerNameHelpText();
        view.clearFormError();
        validationService.isValidContainerName(getContainerName(),
                                               new ValidatorCallback() {
                                                   @Override
                                                   public void onSuccess() {
                                                       view.setContainerNameStatus(FormStatus.VALID);
                                                       containerNameValid = true;
                                                       if (isAlreadyInUse(getContainerName())) {
                                                           containerNameValid = false;
                                                           view.setContainerNameHelpText(translationService.getTranslation(ContainerConfigPresenter_ContainerNameAlreadyInUseErrorMessage));
                                                           view.setContainerNameStatus(FormStatus.ERROR);
                                                       }
                                                   }

                                                   @Override
                                                   public void onFailure() {
                                                       view.setContainerNameStatus(FormStatus.ERROR);
                                                       containerNameValid = false;
                                                       view.setContainerNameHelpText(translationService.getTranslation(ContainerConfigPresenter_InvalidContainerNameErrorMessage));
                                                   }
                                               });
    }

    protected void onGroupIdChange() {
        if (isValidGroupId()) {
            view.setGroupIdStatus(FormStatus.VALID);
        } else {
            view.setGroupIdStatus(FormStatus.ERROR);
        }
        view.clearFormError();
    }

    protected void onArtifactIdChange() {
        if (isValidArtifactId()) {
            view.setArtifactIdStatus(FormStatus.VALID);
        } else {
            view.setArtifactIdStatus(FormStatus.ERROR);
        }
        view.clearFormError();
    }

    protected void onVersionChange() {
        if (isValidVersion()) {
            view.setVersionStatus(FormStatus.VALID);
        } else {
            view.setVersionStatus(FormStatus.ERROR);
        }
        view.clearFormError();
    }

    private RemoteCallback<GAV> getLoadGAVSuccessCallback() {
        return (gav) -> {
            view.setGroupId(gav.getGroupId());
            view.setArtifactId(gav.getArtifactId());
            view.setVersion(gav.getVersion());
            onGroupIdChange();
            onArtifactIdChange();
            onVersionChange();
        };
    }

    private ErrorCallback<Message> getLoadGAVErrorCallback() {
        return (message, throwable) -> {
            view.setGroupId(EMPTY_STRING);
            view.setArtifactId(EMPTY_STRING);
            view.setVersion(EMPTY_STRING);
            popupHelper.showErrorPopup(translationService.format(ContainerConfigPresenter_LoadGAVErrorMessage,
                                                                 throwable.getMessage()));
            return false;
        };
    }
}