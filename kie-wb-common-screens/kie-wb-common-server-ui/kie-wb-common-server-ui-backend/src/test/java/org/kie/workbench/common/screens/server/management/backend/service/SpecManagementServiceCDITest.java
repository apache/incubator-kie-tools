/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Test;
import org.kie.server.controller.api.KieServerControllerIllegalArgumentException;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SpecManagementServiceCDITest {

    @Test
    public void testIsContainerIdValid() {
        final SpecManagementServiceCDI specManagementService = spy(new SpecManagementServiceCDI());

        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        when(serverTemplate.getContainerSpec(any())).thenReturn(null);
        doReturn(serverTemplate).when(specManagementService).getServerTemplate("templateId");

        assertTrue(specManagementService.isContainerIdValid("templateId",
                                                            "111"));
        assertTrue(specManagementService.isContainerIdValid("templateId",
                                                            "xxx"));
        assertTrue(specManagementService.isContainerIdValid("templateId",
                                                            "aaa:bbb:ccc"));
        assertTrue(specManagementService.isContainerIdValid("templateId",
                                                            "org.jbpm:Evaluation:1.0"));
        assertTrue(specManagementService.isContainerIdValid("templateId",
                                                            "org.jbpm:Evaluation:1.0-SNAPSHOT"));
        assertTrue(specManagementService.isContainerIdValid("templateId",
                                                            "org.jbpm:Evaluation:1.0_demo"));

        assertFalse(specManagementService.isContainerIdValid("templateId",
                                                             "org.jbpm:Evaluation:1.0/SNAPSHOT"));
        assertFalse(specManagementService.isContainerIdValid("templateId",
                                                             "org.jbpm:Evaluation:1.0&SNAPSHOT"));
        assertFalse(specManagementService.isContainerIdValid("templateId",
                                                             "org.jbpm:Evaluation:1.0+SNAPSHOT"));
        assertFalse(specManagementService.isContainerIdValid("templateId",
                                                             "org.jbpm:Evaluation:1.0`SNAPSHOT"));
        assertFalse(specManagementService.isContainerIdValid("templateId",
                                                             "org.jbpm:Evaluation:1.0~SNAPSHOT"));
        assertFalse(specManagementService.isContainerIdValid("templateId",
                                                             "aa&&aa"));
    }

    @Test
    public void isNewServerTemplateIdValidTest() {
        final SpecManagementServiceCDI specManagementService = spy(new SpecManagementServiceCDI());

        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        when(serverTemplate.getContainerSpec(any())).thenReturn(null);
        doThrow(KieServerControllerIllegalArgumentException.class).when(specManagementService).getServerTemplate("noDoraId");
        doReturn(serverTemplate).when(specManagementService).getServerTemplate("doraId");

        assertTrue(specManagementService.isNewServerTemplateIdValid("noDoraId"));
        assertFalse(specManagementService.isNewServerTemplateIdValid("doraId"));
    }

    @Test
    public void testValidContainerIdWhenContainerIdIsValidInTheFirstAttempt() {
        final SpecManagementServiceCDI service = spy(new SpecManagementServiceCDI());
        final ServerTemplate template = mock(ServerTemplate.class);

        when(template.getContainerSpec("org.jbpm:Evaluation:1.0")).thenReturn(null);
        doReturn(template).when(service).getServerTemplate("templateId");

        final String containerId = service.validContainerId("templateId",
                                                            "org.jbpm:Evaluation:1.0");

        assertEquals(containerId,
                     "org.jbpm:Evaluation:1.0");
    }

    @Test
    public void testValidContainerIdWhenContainerIdIsValidInTheSecondAttempt() {
        final SpecManagementServiceCDI service = spy(new SpecManagementServiceCDI());
        final ServerTemplate template = mock(ServerTemplate.class);
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        when(template.getContainerSpec("org.jbpm:Evaluation:1.0")).thenReturn(containerSpec);
        when(template.getContainerSpec("org.jbpm:Evaluation:1.0-2")).thenReturn(null);
        doReturn(template).when(service).getServerTemplate("templateId");

        final String containerId = service.validContainerId("templateId",
                                                            "org.jbpm:Evaluation:1.0");

        assertEquals(containerId,
                     "org.jbpm:Evaluation:1.0-2");
    }

    @Test
    public void testValidContainerIdWhenContainerIdIsValidInTheThirdAttempt() {
        final SpecManagementServiceCDI service = spy(new SpecManagementServiceCDI());
        final ServerTemplate template = mock(ServerTemplate.class);
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        when(template.getContainerSpec("org.jbpm:Evaluation:1.0")).thenReturn(containerSpec);
        when(template.getContainerSpec("org.jbpm:Evaluation:1.0-2")).thenReturn(containerSpec);
        when(template.getContainerSpec("org.jbpm:Evaluation:1.0-3")).thenReturn(null);
        doReturn(template).when(service).getServerTemplate("templateId");

        final String containerId = service.validContainerId("templateId",
                                                            "org.jbpm:Evaluation:1.0");

        assertEquals(containerId,
                     "org.jbpm:Evaluation:1.0-3");
    }
}
