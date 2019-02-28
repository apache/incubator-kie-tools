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

import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMProcessModel;
import org.kie.workbench.common.services.backend.project.ModuleClassLoaderHelper;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMFinderServiceImplTest {

    public static final String RESOURCES_PATH = "/definitions/";

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

    private SimpleFileSystemProvider simpleFileSystemProvider = null;

    private Path rootPath;

    @Mock
    private IOService ioService;

    @Mock
    private KieModule module;

    @Mock
    private KieModuleService moduleService;

    @Mock
    private ModuleClassLoaderHelper moduleClassLoaderHelper;

    @Mock
    private ClassLoader classLoader;

    private BPMNFormModelGeneratorImpl bpmnFormModelGenerator;

    private BPMFinderServiceImpl finderService;

    @Mock
    private org.uberfire.backend.vfs.Path testPath;

    @Before
    public void initialize() throws URISyntaxException, ClassNotFoundException {

        when(ioService.newDirectoryStream(any(),
                                          any())).thenAnswer(invocationOnMock -> Files.newDirectoryStream((Path) invocationOnMock.getArguments()[0],
                                                                                                          (DirectoryStream.Filter<Path>) invocationOnMock.getArguments()[1]));
        when(ioService.newInputStream(any())).thenAnswer(invocationOnMock -> new FileInputStream(((Path) invocationOnMock.getArguments()[0]).toFile()));

        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        rootPath = simpleFileSystemProvider.getPath(this.getClass().getResource(RESOURCES_PATH).toURI());

        when(moduleService.resolveModule(any())).thenReturn(module);
        when(module.getRootPath()).thenReturn(Paths.convert(rootPath));

        when(classLoader.loadClass(any())).thenAnswer((Answer<Class>) invocation -> String.class);

        when(moduleClassLoaderHelper.getModuleClassLoader(any())).thenReturn(classLoader);

        bpmnFormModelGenerator = new BPMNFormModelGeneratorImpl(moduleService,
                                                                moduleClassLoaderHelper);

        finderService = spy(new BPMFinderServiceImpl(ioService,
                                                     moduleService,
                                                     bpmnFormModelGenerator));

        finderService.init();
    }

    @Test
    public void testFindAllProcessFormModels() {
        List<JBPMProcessModel> models = finderService.getAvailableProcessModels(testPath);

        assertNotNull(models);

        assertEquals(EXPECTED_PROCESSES,
                     models.size());

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

        ResourceType.getResourceType("BPMN2").getAllExtensions().stream()
                .forEach(ext -> verify(finderService, times(1))
                        .scannProcessesForType(any(org.uberfire.backend.vfs.Path.class),
                                               eq(ext),
                                               any(BPMFinderServiceImpl.GenerationConfig.class)));
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
        JBPMProcessModel model = finderService.getModelForProcess(processId,
                                                                  testPath);

        assertNotNull(model);

        assertNotNull(model.getProcessFormModel());
        assertEquals(processId,
                     model.getProcessFormModel().getProcessId());
        assertEquals(expectedTasks,
                     model.getTaskFormModels().size());

        final ArgumentCaptor<String> extCaptor = ArgumentCaptor.forClass(String.class);
        verify(finderService, atLeast(0)).scannProcessesForType(any(org.uberfire.backend.vfs.Path.class),
                                                                extCaptor.capture(),
                                                                any(BPMFinderServiceImpl.GenerationConfig.class));
        final List<String> extValues = extCaptor.getAllValues();
        assertFalse(extValues.isEmpty());
        assertTrue(ResourceType.getResourceType("BPMN2").getAllExtensions().containsAll(extValues));
    }
}
