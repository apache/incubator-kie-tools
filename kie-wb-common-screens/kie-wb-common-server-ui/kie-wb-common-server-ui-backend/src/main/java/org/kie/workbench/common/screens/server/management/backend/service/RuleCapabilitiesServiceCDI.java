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

package org.kie.workbench.common.screens.server.management.backend.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.workbench.common.screens.server.management.service.RuleCapabilitiesService;

@Service
@ApplicationScoped
public class RuleCapabilitiesServiceCDI implements RuleCapabilitiesService {

    @Inject
    @Any
    private org.kie.server.controller.api.service.RuleCapabilitiesService service;

    @Override
    public void scanNow(final ContainerSpecKey containerSpecKey) {
        service.scanNow(containerSpecKey);
    }

    @Override
    public void startScanner(final ContainerSpecKey containerSpecKey,
                             final Long interval) {
        service.startScanner(containerSpecKey,
                             interval);
    }

    @Override
    public void stopScanner(final ContainerSpecKey containerSpecKey) {
        service.stopScanner(containerSpecKey);
    }

    @Override
    public void upgradeContainer(final ContainerSpecKey containerSpecKey,
                                 final ReleaseId releaseId) {
        service.upgradeContainer(containerSpecKey,
                                 releaseId);
    }

}
