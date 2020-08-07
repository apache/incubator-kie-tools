/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.server.management.client.wizard.container;

import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.api.model.KieServerMode;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.server.controller.api.model.spec.ServerTemplateKey;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.util.ContentChangeHandler;
import org.kie.workbench.common.screens.server.management.client.widget.artifact.ArtifactListWidgetPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.core.client.wizards.WizardPage;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class NewContainerFormPresenter implements WizardPage {

    public static final String SNAPSHOT = "-SNAPSHOT";

    private final Logger logger;
    private final View view;
    private final Caller<M2RepoService> m2RepoService;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;
    private final ManagedInstance<ArtifactListWidgetPresenter> artifactListWidgetPresenterProvider;
    //lazy load due init issues
    private ArtifactListWidgetPresenter artifactListWidgetPresenter;
    private ServerTemplate serverTemplate;
    private Mode mode = Mode.OPTIONAL;

    @Inject
    public NewContainerFormPresenter(final Logger logger,
                                     final View view,
                                     final ManagedInstance<ArtifactListWidgetPresenter> artifactListWidgetPresenterProvider,
                                     final Caller<M2RepoService> m2RepoService,
                                     final Caller<SpecManagementService> specManagementService,
                                     final Event<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent) {
        this.logger = logger;
        this.view = view;
        this.artifactListWidgetPresenterProvider = artifactListWidgetPresenterProvider;
        this.m2RepoService = m2RepoService;
        this.specManagementService = specManagementService;
        this.wizardPageStatusChangeEvent = wizardPageStatusChangeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.addContentChangeHandler(new ContentChangeHandler() {
            @Override
            public void onContentChange() {
                wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(NewContainerFormPresenter.this));
            }
        });
    }

    public void addContentChangeHandler(final ContentChangeHandler contentChangeHandler) {
        view.addContentChangeHandler(checkNotNull("contentChangeHandler", contentChangeHandler));
    }

    @Override
    public String getTitle() {
        return view.getTitle();
    }

    @Override
    public void isComplete(final Callback<Boolean> callback) {
        if (isValid()) {
            if (serverTemplate == null) {
                callback.callback(true);
            } else {
                specManagementService.call(new RemoteCallback<Boolean>() {
                    @Override
                    public void callback(final Boolean result) {
                        if (result.equals(Boolean.FALSE)) {
                            view.errorOnContainerName(view.getInvalidErrorMessage());
                        }
                        callback.callback(result);
                    }
                }).isContainerIdValid(serverTemplate.getId(), view.getContainerName());
            }
        } else {
            callback.callback(false);
        }
    }

    @Override
    public void initialise() {
        if (artifactListWidgetPresenter != null) {
            artifactListWidgetPresenter.clear();
        }
    }

    @Override
    public void prepareView() {
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public Widget asWidget() {
        if (artifactListWidgetPresenter == null) {
            artifactListWidgetPresenter = artifactListWidgetPresenterProvider.get();
            view.setArtifactListWidgetView(artifactListWidgetPresenter.getView());
        }
        return view.asWidget();
    }

    public void clear() {
        serverTemplate = null;
        mode = Mode.OPTIONAL;
        view.clear();
    }

    void onDependencyPathSelectedEvent(@Observes final DependencyPathSelectedEvent event) {
        if (event != null &&
                event.getContext() != null &&
                event.getPath() != null) {
            if (event.getContext().equals(artifactListWidgetPresenter)) {
                m2RepoService.call(new RemoteCallback<GAV>() {
                    @Override
                    public void callback(GAV gav) {
                        setAValidContainerName(gav.toString());

                        view.setGroupId(gav.getGroupId());
                        view.setArtifactId(gav.getArtifactId());
                        view.setVersion(gav.getVersion());

                        wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(NewContainerFormPresenter.this));
                    }
                }).loadGAVFromJar(event.getPath());
            }
        } else {
            logger.warn("Illegal event argument.");
        }
    }

    private void setAValidContainerName(final String containerId) {
        final boolean isContainerNameEmpty = view.getContainerName().isEmpty();

        if (isContainerNameEmpty) {
            setContainerName(containerId);
        }
    }

    private void setContainerName(final String containerId) {

        final Optional<ServerTemplate> serverTemplate = Optional.ofNullable(getServerTemplate());
        final RemoteCallback<String> setContainerName = (validContainerId) -> {
            view.setContainerName(validContainerId);
            wizardPageStatusChangeEvent.fire(new WizardPageStatusChangeEvent(this));
        };

        if (serverTemplate.isPresent()) {
            specManagementService
                    .call(setContainerName)
                    .validContainerId(serverTemplate.get().getId(), containerId);
        } else {
            setContainerName.callback(containerId);
        }
    }

    public boolean isContainerNameValid() {
        if (mode.equals(Mode.OPTIONAL) && isEmpty()) {
            return true;
        }
        return !view.getContainerName().trim().isEmpty();
    }

    public boolean isGroupIdValid() {
        if (mode.equals(Mode.OPTIONAL) && isEmpty()) {
            return true;
        }
        return !view.getGroupId().trim().isEmpty();
    }

    public boolean isArtifactIdValid() {
        if (mode.equals(Mode.OPTIONAL) && isEmpty()) {
            return true;
        }
        return !view.getArtifactId().trim().isEmpty();
    }

    public boolean isVersionValid() {
        if (mode.equals(Mode.OPTIONAL) && isEmpty()) {
            return true;
        }
        return !view.getVersion().trim().isEmpty();
    }

    public boolean isArtifactSupportedByServer() {
        String version = view.getVersion().trim();
        if (!version.isEmpty()
        && serverTemplate != null
        && serverTemplate.getMode().equals(KieServerMode.PRODUCTION)) {
            return !isSnapshot(version);
        }
        return true;
    }

    private boolean isSnapshot(String version) {
        return version.toUpperCase().endsWith(SNAPSHOT);
    }

    public boolean isValid() {
        if (mode.equals(Mode.OPTIONAL) && isEmpty()) {
            view.noErrors();
            return true;
        }
        boolean hasError = false;
        if (isContainerNameValid()) {
            view.noErrorOnContainerName();
        } else {
            view.errorOnContainerName();
            hasError = true;
        }

        if (isGroupIdValid()) {
            view.noErrorOnGroupId();
        } else {
            view.errorOnGroupId();
            hasError = true;
        }

        if (isArtifactIdValid()) {
            view.noErrorOnArtifactId();
        } else {
            view.errorOnArtifactId();
            hasError = true;
        }

        if (isVersionValid()) {
            if (isArtifactSupportedByServer()) {
                view.noErrorOnVersion();
            } else {
                view.errorOnVersion();

                view.errorProductionModeSupportsDoesntSnapshots();

                hasError = true;
            }
        } else {
            view.errorOnVersion();
            hasError = true;
        }

        return !hasError;
    }

    public boolean isEmpty() {
        return view.getContainerName().trim().isEmpty() &&
                view.getGroupId().trim().isEmpty() &&
                view.getArtifactId().trim().isEmpty() &&
                view.getVersion().trim().isEmpty();
    }

    public ServerTemplate getServerTemplate() {
        return serverTemplate;
    }

    public void setServerTemplate(final ServerTemplate serverTemplate) {
        this.serverTemplate = serverTemplate;
        this.mode = Mode.MANDATORY;
    }

    public ContainerSpec buildContainerSpec(final String serverTemplateId,
                                            final Map<Capability, ContainerConfig> configs) {
        return new ContainerSpec(view.getContainerName(),
                                 view.getContainerAlias(),
                                 new ServerTemplateKey(serverTemplateId, null),
                                 new ReleaseId(view.getGroupId(), view.getArtifactId(), view.getVersion()),
                                 view.isStartContainer() ? KieContainerStatus.STARTED : KieContainerStatus.STOPPED,
                                 configs);
    }

    public View getView() {
        return this.view;
    }

    public enum Mode {
        OPTIONAL,
        MANDATORY
    }

    public GAV getCurrentGAV() {
        return new GAV(view.getGroupId(), view.getArtifactId(), view.getVersion());
    }

    public interface View extends UberView<NewContainerFormPresenter> {

        String getTitle();

        void addContentChangeHandler(final ContentChangeHandler contentChangeHandler);

        String getContainerName();

        void setContainerName(final String containerName);

        String getContainerAlias();

        String getGroupId();

        void setGroupId(final String groupId);

        String getArtifactId();

        void setArtifactId(final String artifactId);

        String getVersion();

        void setVersion(final String version);

        boolean isStartContainer();

        void setStartContainer(boolean startContainer);

        void errorOnContainerName();

        void errorOnContainerName(String s);

        void errorOnGroupId();

        void errorOnArtifactId();

        void errorOnVersion();

        void errorProductionModeSupportsDoesntSnapshots();

        void setArtifactListWidgetView(ArtifactListWidgetPresenter.View view);

        void clear();

        void noErrors();

        void noErrorOnContainerName();

        void noErrorOnGroupId();

        void noErrorOnArtifactId();

        void noErrorOnVersion();

        String getInvalidErrorMessage();

        String getNewContainerWizardTitle();

        String getNewContainerWizardSaveSuccess();

        String getNewContainerWizardSaveError();

        String getNewContainerGAVNotExist(String gav);

        String getNewContainerGAVNotExistSave(String gav);

        String getNewContainerSaveContainerSpec();

        String getNewContainerSave();
    }
}
