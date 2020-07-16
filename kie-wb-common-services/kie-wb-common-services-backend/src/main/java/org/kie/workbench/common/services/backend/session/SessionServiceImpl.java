/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.session;

import javax.inject.Inject;

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.workbench.common.services.backend.builder.core.Builder;
import org.kie.workbench.common.services.backend.builder.service.BuildInfo;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoImpl;
import org.kie.workbench.common.services.backend.builder.service.BuildInfoService;
import org.kie.workbench.common.services.shared.project.KieModule;

public class SessionServiceImpl
        implements SessionService {

    private BuildInfoService buildInfoService;

    public SessionServiceImpl() {
        //Empty constructor for Weld
    }

    @Inject
    public SessionServiceImpl(final BuildInfoService buildInfoService) {
        this.buildInfoService = buildInfoService;
    }

    @Override
    public KieSession newKieSession(KieModule project, String ksessionName) {

        final KieContainer kieContainer = getKieContainer(project);

        //If a KieContainer could not be built there is a build error somewhere; so return null to be handled elsewhere
        if (kieContainer == null) {
            return null;
        }

        return kieContainer.newKieSession(ksessionName);
    }

    @Override
    public KieSession newDefaultKieSessionWithPseudoClock(final KieModule project) {

        final KieContainer kieContainer = getKieContainer(project);

        //If a KieContainer could not be built there is a build error somewhere; so return null to be handled elsewhere
        if (kieContainer == null) {
            return null;
        }

        //We always need a pseudo clock
        final SessionConfiguration conf = SessionConfiguration.newInstance();
        conf.setClockType(ClockType.PSEUDO_CLOCK);

        return kieContainer.getKieBase().newKieSession(conf, null);
    }

    private KieContainer getKieContainer(final KieModule project) {
        final BuildInfo buildInfo = buildInfoService.getBuildInfo(project);
        if (buildInfo instanceof BuildInfoImpl) {
            // The kie builder needs to be cloned so that the original does not get altered.
            final Builder clone = ((BuildInfoImpl) buildInfo).getBuilder().clone();
            
            clone.build();
            return clone.getKieContainer();
        }
        throw new IllegalStateException("Failed to clone Builder.");
    }
}
