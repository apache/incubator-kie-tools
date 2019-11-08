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

package org.kie.workbench.common.screens.server.management.client.widget.config.process;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.ProcessConfig;
import org.kie.workbench.common.screens.server.management.client.events.DependencyPathSelectedEvent;
import org.kie.workbench.common.screens.server.management.client.util.ClientMergeMode;
import org.kie.workbench.common.screens.server.management.client.util.ClientRuntimeStrategy;
import org.kie.workbench.common.screens.server.management.model.ProcessConfigModule;
import org.kie.workbench.common.screens.server.management.service.DeploymentDescriptorService;
import org.uberfire.client.mvp.UberView;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Dependent
public class ProcessConfigPresenter {

    public interface View extends UberView<ProcessConfigPresenter> {

        void setContent(final String runtimeStrategy,
                        final String kbase,
                        final String ksession,
                        final String mergeMode);

        String getKBase();

        String getKSession();

        String getMergeMode();

        String getRuntimeStrategy();

        void disable();

        void clear();

        TranslationService getTranslationService();

        String getConfigPageTitle();
    }

    private final View view;
    private ContainerSpecKey containerSpecKey;
    private ProcessConfig processConfig;
    private final Caller<DeploymentDescriptorService> deploymentDescriptorService;

    @Inject
    public ProcessConfigPresenter(final View view, Caller<DeploymentDescriptorService> deploymentDescriptorService) {
        this.view = view;
        this.deploymentDescriptorService = deploymentDescriptorService;
    }

    @PostConstruct
    public void init() {
        this.view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setup(final ContainerSpecKey containerSpecKey,
                      final ProcessConfig processConfig) {
        this.processConfig = checkNotNull("processConfig",
                                          processConfig);
        this.containerSpecKey = checkNotNull("containerSpecKey",
                                             containerSpecKey);
        setupView(processConfig);
    }

    public void clear() {
        processConfig = null;
        containerSpecKey = null;
        view.clear();
    }

    public void setProcessConfig(final ProcessConfig processConfig) {
        this.processConfig = processConfig;
        setupView(processConfig);
    }

    public ContainerSpecKey getContainerSpecKey() {
        return containerSpecKey;
    }

    public ProcessConfig getProcessConfig() {
        return processConfig;
    }

    private void setupView(final ProcessConfig processConfig) {
        final String runtimeStrategy = ClientRuntimeStrategy.valueOf(processConfig.getRuntimeStrategy()).getValue(view.getTranslationService());
        final String mergeMode = ClientMergeMode.valueOf(processConfig.getMergeMode()).getValue(view.getTranslationService());

        this.view.setContent(runtimeStrategy,
                             processConfig.getKBase(),
                             processConfig.getKSession(),
                             mergeMode);
    }

    void onDependencyPathSelectedEvent(@Observes final DependencyPathSelectedEvent event) throws IOException {
        if (event != null && event.getContext() != null && event.getPath() != null) {
            deploymentDescriptorService.call(processConfigModule -> {
                ProcessConfigModule module = (ProcessConfigModule) processConfigModule;
                view.setContent(ClientRuntimeStrategy.convert(module.getRuntimeStrategy()).getValue(view.getTranslationService()),
                                module.getKBase(),
                                module.getKSession(),
                                ClientMergeMode.MERGE_COLLECTIONS.getValue(view.getTranslationService()));
            }).getProcessConfig(event.getPath());
        }
    }


    public void disable() {
        view.disable();
    }

    public void cancel() {
        setupView(this.processConfig);
    }

    public ProcessConfig buildProcessConfig() {
        return new ProcessConfig(ClientRuntimeStrategy.convert(view.getRuntimeStrategy(),
                                                               view.getTranslationService()).getRuntimeStrategy().toString(),
                                 view.getKBase(),
                                 view.getKSession(),
                                 ClientMergeMode.convert(view.getMergeMode(),
                                                         view.getTranslationService()).getMergeMode().toString());
    }

    public List<String> getRuntimeStrategies() {
        return ClientRuntimeStrategy.listRuntimeStrategiesValues(view.getTranslationService());
    }

    public List<String> getMergeModes() {
        return ClientMergeMode.listMergeModeValues(view.getTranslationService());
    }
}
