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

package org.kie.workbench.common.screens.server.management.client.container;

import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieContainerStatus;
import org.kie.server.controller.api.model.runtime.Container;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.workbench.common.screens.server.management.client.container.config.process.ContainerProcessConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.config.rules.ContainerRulesConfigPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.ContainerRemoteStatusPresenter;
import org.kie.workbench.common.screens.server.management.client.container.status.empty.ContainerStatusEmptyPresenter;
import org.kie.workbench.common.screens.server.management.client.events.ContainerSpecSelected;
import org.kie.workbench.common.screens.server.management.client.events.RefreshRemoteServers;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation;
import org.kie.workbench.common.screens.server.management.model.ContainerSpecData;
import org.kie.workbench.common.screens.server.management.model.ContainerUpdateEvent;
import org.kie.workbench.common.screens.server.management.service.RuntimeManagementService;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberView;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation.STOP_CONTAINER;

@Dependent
public class ContainerPresenter {

    private final Logger logger;
    private final View view;
    private final ContainerRemoteStatusPresenter containerRemoteStatusPresenter;
    private final ContainerStatusEmptyPresenter containerStatusEmptyPresenter;
    private final ContainerProcessConfigPresenter containerProcessConfigPresenter;
    private final ContainerRulesConfigPresenter containerRulesConfigPresenter;
    private final Caller<RuntimeManagementService> runtimeManagementService;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;
    private final Event<NotificationEvent> notification;
    private ContainerSpec containerSpec;

    @Inject
    public ContainerPresenter(final Logger logger,
                              final View view,
                              final ContainerRemoteStatusPresenter containerRemoteStatusPresenter,
                              final ContainerStatusEmptyPresenter containerStatusEmptyPresenter,
                              final ContainerProcessConfigPresenter containerProcessConfigPresenter,
                              final ContainerRulesConfigPresenter containerRulesConfigPresenter,
                              final Caller<RuntimeManagementService> runtimeManagementService,
                              final Caller<SpecManagementService> specManagementService,
                              final Event<ServerTemplateSelected> serverTemplateSelectedEvent,
                              final Event<NotificationEvent> notification) {
        this.logger = logger;
        this.view = view;
        this.containerRemoteStatusPresenter = containerRemoteStatusPresenter;
        this.containerStatusEmptyPresenter = containerStatusEmptyPresenter;
        this.containerProcessConfigPresenter = containerProcessConfigPresenter;
        this.containerRulesConfigPresenter = containerRulesConfigPresenter;
        this.runtimeManagementService = runtimeManagementService;
        this.specManagementService = specManagementService;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init(this);
        view.setStatus(containerRemoteStatusPresenter.getView());
        view.setRulesConfig(containerRulesConfigPresenter.getView());
        view.setProcessConfig(containerProcessConfigPresenter.getView());
    }

    public View getView() {
        return view;
    }

    protected void setContainerSpec(ContainerSpec containerSpec){
        this.containerSpec = containerSpec;
    }

    public void onRefresh(@Observes final RefreshRemoteServers refresh) {
        if (refresh != null && refresh.getContainerSpecKey() != null) {
            load(refresh.getContainerSpecKey());
        } else {
            logger.warn("Illegal event argument.");
        }
    }

    public void load(@Observes final ContainerSpecSelected containerSpecSelected) {
        if (containerSpecSelected != null &&
                containerSpecSelected.getContainerSpecKey() != null) {
            load(containerSpecSelected.getContainerSpecKey());
        } else {
            logger.warn("Illegal event argument.");
        }
    }

    public void loadContainers(@Observes final ContainerSpecData content) {
        if (content != null &&
                content.getContainerSpec() != null &&
                content.getContainers() != null &&
                containerSpec!=null &&
                containerSpec.getId()!=null &&
                containerSpec.getId().equals(content.getContainerSpec().getId())) {
            setup(content.getContainerSpec(),
                  content.getContainers());
        } else {
            logger.warn("Illegal event argument.");
        }
    }

    public void refreshOnContainerUpdateEvent(@Observes final ContainerUpdateEvent updateEvent) {
        final ContainerRuntimeOperation runtimeOperation = updateEvent.getContainerRuntimeOperation();

        if (updateEvent.getContainerSpec().equals(containerSpec) && runtimeOperation != STOP_CONTAINER) {
            refresh();
        }
    }

    public void refresh() {
        load(containerSpec);
    }

    public void load(final ContainerSpecKey containerSpecKey) {
        checkNotNull("containerSpecKey", containerSpecKey);
        runtimeManagementService.call((RemoteCallback<ContainerSpecData>) content -> {
            checkNotNull("content", content);
            setContainerSpec(content.getContainerSpec());
            loadContainers(content);
        }).getContainersByContainerSpec(containerSpecKey.getServerTemplateKey().getId(),
                                        containerSpecKey.getId());
    }

    private void setup(final ContainerSpec containerSpec,
                       final Collection<Container> containers) {
        this.containerSpec = checkNotNull("containerSpec",
                                          containerSpec);
        updateView(containers);
    }

    private void updateView(final Collection<Container> containers) {
        containerStatusEmptyPresenter.setup(containerSpec);
        containerRemoteStatusPresenter.setup(containerSpec,
                                             containers);
        view.clear();
        if (isEmpty(containers)) {
            view.setStatus(containerStatusEmptyPresenter.getView());
        } else {
            view.setStatus(containerRemoteStatusPresenter.getView());
        }

        view.setContainerName(containerSpec.getContainerName());
        view.setGroupIp(containerSpec.getReleasedId().getGroupId());
        view.setArtifactId(containerSpec.getReleasedId().getArtifactId());
        containerRulesConfigPresenter.setVersion(containerSpec.getReleasedId().getVersion());
        containerProcessConfigPresenter.disable();

        updateStatus(containerSpec.getStatus() != null ? containerSpec.getStatus() : KieContainerStatus.STOPPED);

        for (Map.Entry<Capability, ContainerConfig> entry : containerSpec.getConfigs().entrySet()) {
            switch (entry.getKey()) {
                case RULE:
                    setupRuleConfig((RuleConfig) entry.getValue());
                    break;
                case PROCESS:
                    setupProcessConfig((ProcessConfig) entry.getValue());
                    break;
            }
        }
    }

    private boolean isEmpty(final Collection<Container> containers) {
        for (final Container container : containers) {
            if (!container.getStatus().equals(KieContainerStatus.STOPPED)) {
                return false;
            }
        }
        return true;
    }

    private void updateStatus(final KieContainerStatus status) {
        switch (status) {
            case CREATING:
            case STARTED:
                view.disableRemoveButton();
                view.setContainerStartState(State.ENABLED);
                view.setContainerStopState(State.DISABLED);
                break;
            case STOPPED:
            case DISPOSING:
            case FAILED:
                view.enableRemoveButton();
                view.setContainerStartState(State.DISABLED);
                view.setContainerStopState(State.ENABLED);
                break;
        }
    }

    private void setupProcessConfig(final ProcessConfig value) {
        containerProcessConfigPresenter.setup(containerSpec,
                                              value);
    }

    private void setupRuleConfig(final RuleConfig value) {
        containerRulesConfigPresenter.setup(containerSpec,
                                            value);
    }

    public void removeContainer() {
        view.confirmRemove(new Command() {
            @Override
            public void execute() {
                specManagementService.call(new RemoteCallback<Void>() {
                                               @Override
                                               public void callback(final Void response) {
                                                   notification.fire(new NotificationEvent(view.getRemoveContainerSuccessMessage(),
                                                                                           NotificationEvent.NotificationType.SUCCESS));
                                                   serverTemplateSelectedEvent.fire(new ServerTemplateSelected(containerSpec.getServerTemplateKey()));
                                               }
                                           },
                                           new ErrorCallback<Object>() {
                                               @Override
                                               public boolean error(final Object o,
                                                                    final Throwable throwable) {
                                                   notification.fire(new NotificationEvent(view.getRemoveContainerErrorMessage(),
                                                                                           NotificationEvent.NotificationType.ERROR));
                                                   serverTemplateSelectedEvent.fire(new ServerTemplateSelected(containerSpec.getServerTemplateKey()));
                                                   return false;
                                               }
                                           }).deleteContainerSpec(containerSpec.getServerTemplateKey().getId(),
                                                                  containerSpec.getId());
            }
        });
    }

    public void stopContainer() {
        specManagementService.call(new RemoteCallback<Void>() {
                                       @Override
                                       public void callback(final Void response) {
                                           updateStatus(KieContainerStatus.STOPPED);
                                       }
                                   },
                                   new ErrorCallback<Object>() {
                                       @Override
                                       public boolean error(final Object o,
                                                            final Throwable throwable) {
                                           notification.fire(new NotificationEvent(view.getStopContainerErrorMessage(),
                                                                                   NotificationEvent.NotificationType.ERROR));
                                           updateStatus(KieContainerStatus.STARTED);
                                           return false;
                                       }
                                   }).stopContainer(containerSpec);
    }

    public void startContainer() {
        specManagementService.call(new RemoteCallback<Void>() {
                                       @Override
                                       public void callback(final Void response) {
                                           updateStatus(KieContainerStatus.STARTED);
                                       }
                                   },
                                   new ErrorCallback<Object>() {
                                       @Override
                                       public boolean error(final Object o,
                                                            final Throwable throwable) {
                                           notification.fire(new NotificationEvent(view.getStartContainerErrorMessage(),
                                                                                   NotificationEvent.NotificationType.ERROR));
                                           updateStatus(KieContainerStatus.STOPPED);
                                           return false;
                                       }
                                   }).startContainer(containerSpec);
    }

    public interface View extends UberView<ContainerPresenter> {

        void clear();

        void disableRemoveButton();

        void enableRemoveButton();

        void setContainerName(final String containerName);

        void setGroupIp(final String groupIp);

        void setArtifactId(final String artifactId);

        void setStatus(final IsWidget view);

        void setProcessConfig(final ContainerProcessConfigPresenter.View view);

        void setRulesConfig(final ContainerRulesConfigPresenter.View view);

        void setContainerStopState(final State state);

        void setContainerStartState(final State state);

        void confirmRemove(final Command command);

        String getRemoveContainerSuccessMessage();

        String getRemoveContainerErrorMessage();

        String getStopContainerErrorMessage();

        String getStartContainerErrorMessage();
    }
}
