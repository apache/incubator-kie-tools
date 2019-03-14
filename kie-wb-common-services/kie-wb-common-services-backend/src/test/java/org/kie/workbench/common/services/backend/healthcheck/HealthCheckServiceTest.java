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

package org.kie.workbench.common.services.backend.healthcheck;

import org.junit.Before;
import org.junit.Test;

import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.HEALTHY;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.NOT_READY;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.READY;
import static org.kie.workbench.common.services.backend.healthcheck.ServiceStatus.UNHEALTHY;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class HealthCheckServiceTest {

    private HealthCheckService healthCheckService;

    @Before
    public void before() {
        healthCheckService = spy(new HealthCheckService());
    }

    @Test
    public void testReady() {
        doReturn(singletonMap("foo", READY)).when(healthCheckService).getServicesByStatus();
        assertEquals(OK.getStatusCode(), healthCheckService.isReady().getStatus());
    }

    @Test
    public void testNotReady() {
        doReturn(singletonMap("foo", NOT_READY)).when(healthCheckService).getServicesByStatus();
        assertEquals(SERVICE_UNAVAILABLE.getStatusCode(), healthCheckService.isReady().getStatus());
    }

    @Test
    public void testHealthy() {
        doReturn(singletonMap("foo", HEALTHY)).when(healthCheckService).getServicesByStatus();
        assertEquals(OK.getStatusCode(), healthCheckService.isHealthy().getStatus());
    }

    @Test
    public void testNotHealthy() {
        doReturn(singletonMap("foo", UNHEALTHY)).when(healthCheckService).getServicesByStatus();
        assertEquals(SERVICE_UNAVAILABLE.getStatusCode(), healthCheckService.isHealthy().getStatus());
    }
}