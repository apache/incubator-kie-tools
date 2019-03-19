/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.net.URL;
import java.util.List;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.m2repo.backend.server.repositories.ArtifactRepositoryService;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.literal.NamedLiteral;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProviderConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BPMFinderServiceImplTest {

    private static final int EXPECTED_PROCESSES = 5;

    private static final String PROCESS_WITHOUT_VARIABLES_ID = "myProject.process-without-variables";
    private static final int PROCESS_WITHOUT_VARIABLES_TASKS = 0;

    private static final String PROCESS_WITH_ALL_VARIABLES_ID = "myProject.process-with-all-possible-variables";
    private static final int PROCESS_WITH_ALL_VARIABLES_TASKS = 5;

    private static final String PROCESS_WITH_SHARED_FORMS_ID = "myProject.processTaskSharedForms";
    private static final int PROCESS_WITH_SHARED_FORMS_TASKS = 2;

    private static final String PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_ID = "myProject.processTaskSharedFormsWrongMappings";
    private static final int PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_TASKS = 1;

    private static final String PROCESS_WITH_WRONG_TYPES = "myProject.process-with-wrong-types";
    private static final int PROCESS_WITH_WRONG_TYPES_TASKS = 1;

    private static final JGitFileSystemProvider fs = new JGitFileSystemProvider();

    protected static WeldContainer weldContainer;

    protected static IOService ioService;
    protected static BuildService buildService;
    protected static KieModuleService moduleService;
    protected static ModuleClassLoaderHelper classLoaderHelper;

    protected static KieModule currentModule;
    protected static org.uberfire.backend.vfs.Path currentModulePath;

    private static BPMFinderServiceImpl bpmFinderService;

    @BeforeClass
    public static void setUp() throws Exception {

        System.setProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED, "false");
        System.setProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED, "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");

        weldContainer = new Weld().initialize();

        ioService = weldContainer.select(IOService.class, new NamedLiteral("ioStrategy")).get();

        moduleService = weldContainer.select(KieModuleService.class).get();
        classLoaderHelper = weldContainer.select(ModuleClassLoaderHelper.class).get();
        bpmFinderService = weldContainer.select(BPMFinderServiceImpl.class).get();

        fs.forceAsDefault();

        buildModules("module");
    }

    @Test
    public void testFindAllProcessFormModels() {
        List<JBPMProcessModel> models = bpmFinderService.getAvailableProcessModels(currentModulePath);

        assertNotNull(models);

        assertEquals(EXPECTED_PROCESSES, models.size());

        models.forEach(model -> {
            assertNotNull(model.getProcessFormModel());
            if (model.getProcessFormModel().getProcessId().equals(PROCESS_WITH_ALL_VARIABLES_ID)) {
                assertEquals(PROCESS_WITH_ALL_VARIABLES_TASKS,
                             model.getTaskFormModels().size());
            } else if (model.getProcessFormModel().getProcessId().equals(PROCESS_WITHOUT_VARIABLES_ID)) {
                assertEquals(PROCESS_WITHOUT_VARIABLES_TASKS,
                             model.getTaskFormModels().size());
            } else if (model.getProcessFormModel().getProcessId().equals(PROCESS_WITH_SHARED_FORMS_ID)) {
                assertEquals(PROCESS_WITH_SHARED_FORMS_TASKS,
                             model.getTaskFormModels().size());
            } else if (model.getProcessFormModel().getProcessId().equals(PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_ID)) {
                assertEquals(PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_TASKS,
                             model.getTaskFormModels().size());
            } else if (model.getProcessFormModel().getProcessId().equals(PROCESS_WITH_WRONG_TYPES)) {
                assertEquals(PROCESS_WITH_WRONG_TYPES_TASKS,
                             model.getTaskFormModels().size());
            } else {
                fail("Unexpected process: " + model.getProcessFormModel().getProcessId());
            }
        });
    }

    @Test
    public void testFindProcessWithAllVariablesFormModel() {
        testFindProcess(PROCESS_WITH_ALL_VARIABLES_ID,
                        PROCESS_WITH_ALL_VARIABLES_TASKS);
    }

    @Test
    public void testFindProcessWithoutVariablesFormModel() {
        testFindProcess(PROCESS_WITHOUT_VARIABLES_ID,
                        PROCESS_WITHOUT_VARIABLES_TASKS);
    }

    @Test
    public void testFindProcessWithoutSharedTaskFormFormModel() {
        testFindProcess(PROCESS_WITH_SHARED_FORMS_ID,
                        PROCESS_WITH_SHARED_FORMS_TASKS);
    }

    @Test
    public void testFindProcessWithoutSharedTaskFormWithWrongMappingsFormModel() {
        testFindProcess(PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_ID,
                        PROCESS_WITH_SHARED_FORMS_WRONG_MAPPINGS_TASKS);
    }

    protected void testFindProcess(String processId,
                                   int expectedTasks) {
        JBPMProcessModel model = bpmFinderService.getModelForProcess(processId, currentModulePath);

        assertNotNull(model);

        assertNotNull(model.getProcessFormModel());
        assertEquals(processId, model.getProcessFormModel().getProcessId());
        assertEquals(expectedTasks, model.getTaskFormModels().size());
    }

    protected static void buildModules(String... moduleFolders) throws Exception {
        for (String moduleFolder : moduleFolders) {
            final URL pomUrl = BPMFinderServiceImplTest.class.getResource("/" + moduleFolder + "/pom.xml");
            final org.uberfire.java.nio.file.Path nioPomPath = ioService.get(pomUrl.toURI());

            currentModulePath = Paths.convert(nioPomPath);

            currentModule = moduleService.resolveModule(currentModulePath);
        }
    }

    @AfterClass
    public static void tearDown() {
        if (weldContainer != null) {
            weldContainer.shutdown();
        }

        System.clearProperty(JGitFileSystemProviderConfiguration.GIT_DAEMON_ENABLED);
        System.clearProperty(JGitFileSystemProviderConfiguration.GIT_SSH_ENABLED);
        System.clearProperty("org.uberfire.sys.repo.monitor.disabled");
        System.clearProperty(ArtifactRepositoryService.ORG_GUVNOR_M2REPO_DIR_PROPERTY);
    }
}
