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

package org.kie.workbench.common.screens.server.management.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.deploy.DeploymentDescriptorIO;
import org.kie.workbench.common.screens.server.management.model.ProcessConfigModule;
import org.kie.workbench.common.screens.server.management.service.DeploymentDescriptorService;
import org.kie.workbench.common.services.backend.kmodule.KModuleContentHandler;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

import static org.kie.workbench.common.screens.server.management.model.RuntimeStrategy.*;

@Service
@ApplicationScoped
public class DeploymentDescriptorServiceCDI implements DeploymentDescriptorService {

    private GuvnorM2Repository guvnorM2Repository;

    @Inject
    public DeploymentDescriptorServiceCDI(GuvnorM2Repository guvnorM2Repository) {
        this.guvnorM2Repository = guvnorM2Repository;
    }

    @Override
    public ProcessConfigModule getProcessConfig(String path) throws IOException {
        ProcessConfigModule processConfig = new ProcessConfigModule();
        KModuleContentHandler kModuleContentHandler = new KModuleContentHandler();
        try (InputStream in = IOUtils.toInputStream(guvnorM2Repository.getKieDeploymentDescriptorText(path), StandardCharsets.UTF_8)) {
            RuntimeStrategy runtimeStrategy = DeploymentDescriptorIO.fromXml(in).getRuntimeStrategy();

            switch (runtimeStrategy) {
                case SINGLETON:
                    processConfig.setRuntimeStrategy(SINGLETON);
                    break;
                case PER_CASE:
                    processConfig.setRuntimeStrategy(PER_CASE);
                    break;
                case PER_PROCESS_INSTANCE:
                    processConfig.setRuntimeStrategy(PER_PROCESS_INSTANCE);
                    break;
                case PER_REQUEST:
                    processConfig.setRuntimeStrategy(PER_REQUEST);
                    break;
            }
        }

        Map<String, KBaseModel> kBaseModelHashMap = kModuleContentHandler.toModel(guvnorM2Repository.getKModuleText(path)).getKBases();

        KBaseModel defaultKBaseModel = null;
        Optional<KBaseModel> optionalKBaseModel = kBaseModelHashMap.values().stream().filter(KBaseModel::isDefault).findFirst();
        if (optionalKBaseModel.isPresent()) {
            defaultKBaseModel = optionalKBaseModel.get();
        }

        if (defaultKBaseModel != null) {
            processConfig.setKBase(defaultKBaseModel.getName());
            Optional<KSessionModel> optionalKSessionModel = defaultKBaseModel.getKSessions().stream().filter(KSessionModel::isDefault).findFirst();
            KSessionModel defaultKSessionModel = null;
            if (optionalKSessionModel.isPresent()) {
                defaultKSessionModel = optionalKSessionModel.get();
                processConfig.setKSession(defaultKSessionModel.getName());
            }
        }
        return processConfig;
    }
}
