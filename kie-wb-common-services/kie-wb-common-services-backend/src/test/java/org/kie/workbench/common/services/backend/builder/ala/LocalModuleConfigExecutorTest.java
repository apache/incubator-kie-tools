/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.builder.ala;

import java.util.Optional;

import org.guvnor.ala.config.ProjectConfig;
import org.guvnor.ala.source.Source;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LocalModuleConfigExecutorTest
        implements BuildPipelineTestConstants {

    @Mock
    private KieModuleService moduleService;

    @Mock
    private Source sourceConfig;

    @Mock
    private LocalProjectConfig projectConfig;

    @Mock
    private KieModule module;

    private LocalModuleConfigExecutor executor;

    @Before
    public void setUp() {
        executor = new LocalModuleConfigExecutor(moduleService);
    }

    @Test
    public void testApply() {
        Path pomPath = Paths.convert(POM_PATH);
        when(sourceConfig.getPath()).thenReturn(ROOT_PATH);
        when(moduleService.resolveModule(pomPath)).thenReturn(module);

        Optional<ProjectConfig> result = executor.apply(sourceConfig,
                                                        projectConfig);
        assertTrue(result.isPresent());
        assertEquals(module,
                     ((LocalModule) result.get()).getModule());
    }
}