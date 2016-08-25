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

package org.kie.workbench.common.services.datamodel.backend.server;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.guvnor.m2repo.service.M2RepoService;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.picketlink.producer.IdentityManagementConfiguration;

import static org.mockito.Mockito.*;

@Singleton
@Alternative
public class TestAppSetup {

    @Produces
    @Alternative
    public M2RepoService m2RepoService() {
        return mock( M2RepoService.class );
    }

    @Produces
    @Alternative
    public KModuleService kModuleService() {
        return mock( KModuleService.class );
    }

    @Produces
    @Alternative
    public ConfigurationService configurationService() {
        return mock( ConfigurationService.class );
    }

    @Produces
    @Alternative
    public IdentityManagementConfiguration authenticationService() {
        return new IdentityManagementConfiguration();
    }
}
