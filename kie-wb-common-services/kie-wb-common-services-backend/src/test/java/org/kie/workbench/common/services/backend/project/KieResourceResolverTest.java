/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.project;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.ModuleResourcePathResolver;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.POMService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.kmodule.KModuleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class KieResourceResolverTest {

    @Mock
    IOService ioService;

    @Mock
    POMService pomService;

    @Mock
    CommentedOptionFactory commentedOptionFactory;

    @Mock
    KModuleService kModuleService;

    @Mock
    Instance<ModuleResourcePathResolver> resourcePathResolversInstance;

    @Captor
    ArgumentCaptor<Path> pathArgumentCaptor;

    private KieResourceResolver resolver;

    @Before
    public void setUp() throws Exception {

        resolver = new KieResourceResolver(ioService,
                                           pomService,
                                           commentedOptionFactory,
                                           kModuleService,
                                           resourcePathResolversInstance);
    }

    @Test
    public void returnModule() throws Exception {

        doReturn(new POM()).when(pomService).load(pathArgumentCaptor.capture());

        assertNotNull(resolver.makeModule(Paths.convert(PathFactory.newPath("testFile",
                                                                            "file:///testFile"))));

        final String pathThatWasUsedToLoadTheModulePomXml = pathArgumentCaptor.getValue().toURI();
        assertThat(pathThatWasUsedToLoadTheModulePomXml).endsWith(":///testFile/pom.xml");
    }

    @Test
    public void returnNullWhenSomethingGoesWrong() throws Exception {
        assertNull(resolver.makeModule(null));
    }
}