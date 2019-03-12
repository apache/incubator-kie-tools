/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.server.management.backend;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.junit.After;
import org.junit.Test;
import org.kie.server.controller.api.model.spec.ServerTemplateList;
import org.kie.server.controller.client.KieServerControllerClient;
import org.kie.workbench.common.screens.server.management.service.SpecManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAutoControllerIT extends AbstractControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAutoControllerIT.class);

    @Inject
    SpecManagementService specManagementService;

    KieServerControllerClient client;

    @After
    public void closeControllerClient() {
        if (client != null) {
            try {
                LOGGER.info("Closing Kie Server Management Controller client");
                client.close();
            } catch (IOException e) {
                LOGGER.warn("Error trying to close Kie Server Management Controller Client: {}",
                            e.getMessage(),
                            e);
            }
        }
    }

    @Test
    @OperateOnDeployment("workbench")
    public void testSpecManagementService() throws Exception {
        final ServerTemplateList serverTemplateList = specManagementService.listServerTemplates();
        assertServerTemplateList(serverTemplateList);
    }
}
