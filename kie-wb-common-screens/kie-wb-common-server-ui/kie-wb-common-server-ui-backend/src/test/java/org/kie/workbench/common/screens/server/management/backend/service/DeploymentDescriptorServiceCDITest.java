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
package org.kie.workbench.common.screens.server.management.backend.service;

import java.io.IOException;
import java.text.MessageFormat;

import org.guvnor.m2repo.backend.server.GuvnorM2Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.server.management.model.ProcessConfigModule;
import org.kie.workbench.common.screens.server.management.model.RuntimeStrategy;
import org.kie.workbench.common.screens.server.management.service.DeploymentDescriptorService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeploymentDescriptorServiceCDITest {

    @Mock
    GuvnorM2Repository guvnorM2Repository;

    private DeploymentDescriptorService deploymentDescriptorServiceCDI;

    private final String path = "org:kie:1.0";

    private static final String KIE_DEPLOYMENT_DESCRIPTOR_TEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<deployment-descriptor xsi:schemaLocation=\"http://www.jboss.org/jbpm deployment-descriptor.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "    <persistence-unit>org.jbpm.domain</persistence-unit>\n" +
            "    <audit-persistence-unit>org.jbpm.domain</audit-persistence-unit>\n" +
            "    <audit-mode>JPA</audit-mode>\n" +
            "    <persistence-mode>JPA</persistence-mode>\n" +
            "    <runtime-strategy>{0}</runtime-strategy>\n" +
            "    <marshalling-strategies/>\n" +
            "    <event-listeners/>\n" +
            "    <task-event-listeners/>\n" +
            "    <globals/>\n" +
            "    <work-item-handlers/>\n" +
            "    <environment-entries/>\n" +
            "    <configurations/>\n" +
            "    <required-roles/>\n" +
            "    <remoteable-classes/>\n" +
            "    <limit-serialization-classes>true</limit-serialization-classes>\n" +
            "</deployment-descriptor>";

    private static final String RUNTIME_STRATEGY_SINGLETON = "SINGLETON";
    private static final String RUNTIME_STRATEGY_PER_PROCESS_INSTANCE = "PER_PROCESS_INSTANCE";
    private static final String RUNTIME_STRATEGY_PER_CASE = "PER_CASE";
    private static final String RUNTIME_STRATEGY_PER_REQUEST = "PER_REQUEST";

    private static final String KMODULETEXT = "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <kbase name=\"myKie\" default=\"true\" eventProcessingMode=\"stream\" equalsBehavior=\"identity\">\n" +
            "    <ksession name=\"kiesession\" type=\"stateless\" default=\"true\" clockType=\"realtime\"/>\n" +
            "  </kbase>\n" +
            "</kmodule>";

    @Before
    public void init() {
        deploymentDescriptorServiceCDI = spy(new DeploymentDescriptorServiceCDI(guvnorM2Repository));
        when(guvnorM2Repository.getKModuleText(any())).thenReturn(KMODULETEXT);

        when(guvnorM2Repository.getKieDeploymentDescriptorText(path + RUNTIME_STRATEGY_SINGLETON)).
                thenReturn(MessageFormat.format(KIE_DEPLOYMENT_DESCRIPTOR_TEXT, RUNTIME_STRATEGY_SINGLETON));
        when(guvnorM2Repository.getKieDeploymentDescriptorText(path + RUNTIME_STRATEGY_PER_PROCESS_INSTANCE))
                .thenReturn(MessageFormat.format(KIE_DEPLOYMENT_DESCRIPTOR_TEXT, RUNTIME_STRATEGY_PER_PROCESS_INSTANCE));
        when(guvnorM2Repository.getKieDeploymentDescriptorText(path + RUNTIME_STRATEGY_PER_CASE))
                .thenReturn(MessageFormat.format(KIE_DEPLOYMENT_DESCRIPTOR_TEXT, RUNTIME_STRATEGY_PER_CASE));
        when(guvnorM2Repository.getKieDeploymentDescriptorText(path + RUNTIME_STRATEGY_PER_REQUEST))
                .thenReturn(MessageFormat.format(KIE_DEPLOYMENT_DESCRIPTOR_TEXT, RUNTIME_STRATEGY_PER_REQUEST));
    }

    @Test
    public void testGetProcessConfig() throws IOException {
        ProcessConfigModule processConfigModule = deploymentDescriptorServiceCDI.getProcessConfig(path + RUNTIME_STRATEGY_SINGLETON);

        assertEquals(RuntimeStrategy.SINGLETON, processConfigModule.getRuntimeStrategy());
        assertEquals("myKie", processConfigModule.getKBase());
        assertEquals("kiesession", processConfigModule.getKSession());

        assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, deploymentDescriptorServiceCDI.getProcessConfig(path + RUNTIME_STRATEGY_PER_PROCESS_INSTANCE).getRuntimeStrategy());
        assertEquals(RuntimeStrategy.PER_CASE, deploymentDescriptorServiceCDI.getProcessConfig(path + RUNTIME_STRATEGY_PER_CASE).getRuntimeStrategy());
        assertEquals(RuntimeStrategy.PER_REQUEST, deploymentDescriptorServiceCDI.getProcessConfig(path + RUNTIME_STRATEGY_PER_REQUEST).getRuntimeStrategy());
    }

    @Test
    public void testProcessConfigModule() {
        ProcessConfigModule processConfigModuleSingleton = new ProcessConfigModule();
        processConfigModuleSingleton.setKSession("test-session");
        processConfigModuleSingleton.setRuntimeStrategy(RuntimeStrategy.SINGLETON);
        processConfigModuleSingleton.setKBase("test-kbase");

        ProcessConfigModule processConfigModulePerProcessInstance = new ProcessConfigModule();
        processConfigModulePerProcessInstance.setKSession("test-session");
        processConfigModulePerProcessInstance.setRuntimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);
        processConfigModulePerProcessInstance.setKBase("test-kbase");

        assertNotEquals(processConfigModuleSingleton, processConfigModulePerProcessInstance);
        assertNotEquals(processConfigModuleSingleton.hashCode(), processConfigModulePerProcessInstance.hashCode());

        assertEquals("ProcessConfigModule{runtimeStrategy=SINGLETON, kBase='test-kbase', kSession='test-session'}"
                , processConfigModuleSingleton.toString());

    }
}
