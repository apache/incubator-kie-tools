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

package org.kie.workbench.common.screens.server.management.client.wizard;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.m2repo.model.JarListPageRequest;
import org.guvnor.m2repo.model.JarListPageRow;
import org.guvnor.m2repo.service.M2RepoService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.controller.api.model.spec.Capability;
import org.kie.server.controller.api.model.spec.ContainerConfig;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.events.ServerTemplateSelected;
import org.kie.workbench.common.screens.server.management.client.wizard.config.process.ProcessConfigPagePresenter;
import org.kie.workbench.common.screens.server.management.client.wizard.container.NewContainerFormPresenter;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.uberfire.paging.PageResponse;
import org.uberfire.workbench.events.NotificationEvent;

@ApplicationScoped
public class NewContainerWizard extends AbstractMultiPageWizard {

    private final NewContainerFormPresenter newContainerFormPresenter;
    private final ProcessConfigPagePresenter processConfigPagePresenter;
    private final Caller<SpecManagementService> specManagementService;
    private final Event<NotificationEvent> notification;
    private final Event<ServerTemplateSelected> serverTemplateSelectedEvent;
    private ServerTemplate serverTemplate;
    private Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent;
    private final Caller<M2RepoService> m2RepoService;
    protected boolean isSelected = false;

    @Inject
    public NewContainerWizard(final NewContainerFormPresenter newContainerFormPresenter,
                              final ProcessConfigPagePresenter processConfigPagePresenter,
                              final Caller<SpecManagementService> specManagementService,
                              final Event<NotificationEvent> notification,
                              final Event<ServerTemplateSelected> serverTemplateSelectedEvent,
                              final Event<DependencyPathSelectedEvent> dependencyPathSelectedEvent,
                              final Caller<M2RepoService> m2RepoService) {
        this.newContainerFormPresenter = newContainerFormPresenter;
        this.processConfigPagePresenter = processConfigPagePresenter;
        this.specManagementService = specManagementService;
        this.notification = notification;
        this.serverTemplateSelectedEvent = serverTemplateSelectedEvent;
        this.dependencyPathSelectedEvent = dependencyPathSelectedEvent;
        this.m2RepoService = m2RepoService;
    }

    @Override
    public void start() {
        newContainerFormPresenter.initialise();
        newContainerFormPresenter.addContentChangeHandler(() -> isSelected = false);
        super.start();
    }

    @Override
    public String getTitle() {
        return newContainerFormPresenter.getView().getNewContainerWizardTitle();
    }

    @Override
    public int getPreferredHeight() {
        return 550;
    }

    @Override
    public int getPreferredWidth() {
        return 800;
    }

    public void setServerTemplate( final ServerTemplate serverTemplate ) {
        this.serverTemplate = serverTemplate;
        newContainerFormPresenter.setServerTemplate( serverTemplate );
        pages.clear();
        pages.add( newContainerFormPresenter );
        if ( serverTemplate.getCapabilities().contains( Capability.PROCESS.toString() ) ) {
            pages.add( processConfigPagePresenter );
        }
    }

    public void clear() {
        newContainerFormPresenter.clear();
        processConfigPagePresenter.clear();
        pages.clear();
        pages.add( newContainerFormPresenter );
        isSelected = false;
    }

    @Override
    public void close() {
        super.close();
        clear();
    }

    @Override
    public void complete() {
        final Map<Capability, ContainerConfig> mapConfig = new HashMap<Capability, ContainerConfig>();
        if (getPages().size() == 2) {
            mapConfig.put(Capability.PROCESS, processConfigPagePresenter.buildProcessConfig());
        }
        mapConfig.put(Capability.RULE, new RuleConfig(null, KieScannerStatus.STOPPED));
        final ContainerSpec newContainer = newContainerFormPresenter.buildContainerSpec(newContainerFormPresenter.getServerTemplate().getId(),
                                                                                        mapConfig);
        GAV gav = new GAV(newContainer.getReleasedId().getGroupId(), newContainer.getReleasedId().getArtifactId(), newContainer.getReleasedId().getVersion());
        JarListPageRequest request = new JarListPageRequest(0, Integer.MAX_VALUE, null, Arrays.asList("jar"), null, false);

        m2RepoService.call(response -> {
            List<JarListPageRow> jarListPageRowList = ((PageResponse<JarListPageRow>) response).getPageRowList();
            Optional<JarListPageRow> optionalJarListPageRow = jarListPageRowList.stream().filter(jarListPageRow -> jarListPageRow.getGav().equals(gav)).findFirst();
            if (optionalJarListPageRow.isPresent()) {
                specManagementService.call(new RemoteCallback<Void>() {
                    @Override
                    public void callback(final Void o) {
                        notification.fire(new NotificationEvent(newContainerFormPresenter.getView().getNewContainerWizardSaveSuccess(), NotificationEvent.NotificationType.SUCCESS));
                        clear();
                        NewContainerWizard.super.complete();
                        serverTemplateSelectedEvent.fire(new ServerTemplateSelected(serverTemplate, newContainer.getId()));
                    }
                }, new ErrorCallback<Object>() {
                    @Override
                    public boolean error(final Object o,
                                         final Throwable throwable) {
                        notification.fire(new NotificationEvent(newContainerFormPresenter.getView().getNewContainerWizardSaveError(), NotificationEvent.NotificationType.ERROR));
                        NewContainerWizard.this.pageSelected(0);
                        NewContainerWizard.this.start();
                        return false;
                    }
                }).saveContainerSpec(newContainerFormPresenter.getServerTemplate().getId(), newContainer);
            } else {
                notification.fire(new NotificationEvent(newContainerFormPresenter.getView().getNewContainerGAVNotExist(gav.toString()), NotificationEvent.NotificationType.ERROR));
            }
        }).listArtifacts(request);
    }

    public void onDependencyPathSelectedEvent(@Observes DependencyPathSelectedEvent event){
            this.isSelected = true;
    }

    @Override
    public void pageSelected(final int pageNumber) {
        if (pageNumber == 1 && !isSelected) {
            GAV gav = newContainerFormPresenter.getCurrentGAV();
            JarListPageRequest request = new JarListPageRequest(0, Integer.MAX_VALUE, null, Arrays.asList("jar"), null, false);

            m2RepoService.call(response -> {
                List<JarListPageRow> jarListPageRowList = ((PageResponse<JarListPageRow>) response).getPageRowList();
                Optional<JarListPageRow> optionalJarListPageRow = jarListPageRowList.stream().filter(jarListPageRow -> jarListPageRow.getGav().equals(gav))
                        .sorted(Comparator.comparing(JarListPageRow::getLastModified).reversed()).findFirst();
                if (optionalJarListPageRow.isPresent()) {
                    if (optionalJarListPageRow.get().getPath() != null && !optionalJarListPageRow.get().getPath().isEmpty()) {
                        dependencyPathSelectedEvent.fire(new DependencyPathSelectedEvent(this, optionalJarListPageRow.get().getPath()));
                    }
                } else {
                    notification.fire(new NotificationEvent(newContainerFormPresenter.getView().getNewContainerGAVNotExist(gav.toString()), NotificationEvent.NotificationType.ERROR));
                }
            }).listArtifacts(request);
        }
        super.pageSelected(pageNumber);
    }
}
