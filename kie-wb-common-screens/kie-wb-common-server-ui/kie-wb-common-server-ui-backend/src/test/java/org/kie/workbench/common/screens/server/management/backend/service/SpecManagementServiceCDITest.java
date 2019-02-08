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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.controller.api.KieServerControllerIllegalArgumentException;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ServerTemplate;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SpecManagementServiceCDITest {

    private static final String EVALUATION_GAV_1_0 = "org.jbpm:Evaluation:1.0";
    private static final String EVALUATION_GAV_1_0_2 = "org.jbpm:Evaluation:1.0-2";
    private static final String EVALUATION_GAV_1_0_3 = "org.jbpm:Evaluation:1.0-3";

    private static final String TEMPLATE_ID = "templateId";
    private static final String CONTAINER_ID = "containerId";

    @Mock
    private org.kie.server.controller.api.service.SpecManagementService specManagementService;

    private SpecManagementServiceCDI specManagementServiceCDI;

    @Before
    public void init() {
        specManagementServiceCDI = new SpecManagementServiceCDI(specManagementService);
    }

    @Test
    public void testIsContainerIdValid() {

        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        when(serverTemplate.getContainerSpec(any())).thenReturn(null);
        doReturn(serverTemplate).when(specManagementService).getServerTemplate(TEMPLATE_ID);

        assertTrue(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "111"));
        assertTrue(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "xxx"));
        assertTrue(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "aaa:bbb:ccc"));
        assertTrue(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, EVALUATION_GAV_1_0));
        assertTrue(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0-SNAPSHOT"));
        assertTrue(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0_demo"));
        assertFalse(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0/SNAPSHOT"));
        assertFalse(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0&SNAPSHOT"));
        assertFalse(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0+SNAPSHOT"));
        assertFalse(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0`SNAPSHOT"));
        assertFalse(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "org.jbpm:Evaluation:1.0~SNAPSHOT"));
        assertFalse(specManagementServiceCDI.isContainerIdValid(TEMPLATE_ID, "aa&&aa"));
    }

    @Test
    public void isNewServerTemplateIdValidTest() {

        final ServerTemplate serverTemplate = mock(ServerTemplate.class);
        when(serverTemplate.getContainerSpec(any())).thenReturn(null);
        doThrow(KieServerControllerIllegalArgumentException.class).when(specManagementService).getServerTemplate("noDoraId");
        doReturn(serverTemplate).when(specManagementService).getServerTemplate("doraId");

        assertTrue(specManagementServiceCDI.isNewServerTemplateIdValid("noDoraId"));
        assertFalse(specManagementServiceCDI.isNewServerTemplateIdValid("doraId"));
    }

    @Test
    public void testValidContainerIdWhenContainerIdIsValidInTheFirstAttempt() {
        final ServerTemplate template = mock(ServerTemplate.class);

        when(template.getContainerSpec(EVALUATION_GAV_1_0)).thenReturn(null);
        doReturn(template).when(specManagementService).getServerTemplate(TEMPLATE_ID);

        final String containerId = specManagementServiceCDI.validContainerId(TEMPLATE_ID, EVALUATION_GAV_1_0);

        assertEquals(containerId, EVALUATION_GAV_1_0);
    }

    @Test
    public void testValidContainerIdWhenContainerIdIsValidInTheSecondAttempt() {
        final ServerTemplate template = mock(ServerTemplate.class);
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        when(template.getContainerSpec(EVALUATION_GAV_1_0)).thenReturn(containerSpec);
        when(template.getContainerSpec(EVALUATION_GAV_1_0_2)).thenReturn(null);
        doReturn(template).when(specManagementService).getServerTemplate(TEMPLATE_ID);

        final String containerId = specManagementServiceCDI.validContainerId(TEMPLATE_ID, EVALUATION_GAV_1_0);

        assertEquals(containerId, EVALUATION_GAV_1_0_2);
    }

    @Test
    public void testValidContainerIdWhenContainerIdIsValidInTheThirdAttempt() {
        final ServerTemplate template = mock(ServerTemplate.class);
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        when(template.getContainerSpec(EVALUATION_GAV_1_0)).thenReturn(containerSpec);
        when(template.getContainerSpec(EVALUATION_GAV_1_0_2)).thenReturn(containerSpec);
        when(template.getContainerSpec(EVALUATION_GAV_1_0_3)).thenReturn(null);
        doReturn(template).when(specManagementService).getServerTemplate(TEMPLATE_ID);

        final String containerId = specManagementServiceCDI.validContainerId(TEMPLATE_ID, EVALUATION_GAV_1_0);

        assertEquals(containerId, EVALUATION_GAV_1_0_3);
    }

    @Test
    public void testUpdateContainerSpec() {
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        specManagementServiceCDI.updateContainerSpec(TEMPLATE_ID, containerSpec);

        verify(specManagementService).updateContainerSpec(same(TEMPLATE_ID), same(containerSpec));
    }

    @Test
    public void testUpdateContainerSpecWithReset() {
        final ContainerSpec containerSpec = mock(ContainerSpec.class);

        specManagementServiceCDI.updateContainerSpec(TEMPLATE_ID, CONTAINER_ID, containerSpec, true);

        verify(specManagementService).updateContainerSpec(same(TEMPLATE_ID), same(CONTAINER_ID), same(containerSpec), eq(true));
    }
}
