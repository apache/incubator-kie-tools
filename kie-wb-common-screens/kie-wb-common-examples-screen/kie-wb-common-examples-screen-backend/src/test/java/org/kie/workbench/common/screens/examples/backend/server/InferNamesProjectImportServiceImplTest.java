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
 * See the License dir the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kie.workbench.common.screens.examples.backend.server;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.screens.examples.validation.ImportProjectValidators;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.io.IOService;

import javax.enterprise.event.Event;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class InferNamesProjectImportServiceImplTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"https://github.com/guvnorngtestuser1/guvnorng-playground.git", "guvnorng-playground"},
                {"https://github.com/guvnorngtestuser1/guvnorng-playground/", "guvnorng-playground"},
                {"https://github.com/guvnorngtestuser1/guvnorng-playground/.git", "guvnorng-playground"},
                {"http://github.com/guvnorngtestuser1/guvnorng-playground.git", "guvnorng-playground"},
                {"http://github.com/guvnorngtestuser1/guvnorng-playground/", "guvnorng-playground"},
                {"http://github.com/guvnorngtestuser1/guvnorng-playground/.git", "guvnorng-playground"},
                {"ssh://github.com/guvnorngtestuser1/guvnorng-playground.git", "guvnorng-playground"},
                {"ssh://github.com/guvnorngtestuser1/guvnorng-playground.git", "guvnorng-playground"},
                {"ssh://user:pass@github.com/guvnorngtestuser1/guvnorng-playground.git", "guvnorng-playground"},
                {"ssh://github.com/guvnorngtestuser1/guvnorng-playground", "guvnorng-playground"},
                {"ssh://github.com/guvnorngtestuser1/guvnorng-playground/.git", "guvnorng-playground"},
                {"ssh://user:pass@github.com/guvnorngtestuser1/guvnorng-playground/", "guvnorng-playground"},
                {"file:///path/to/some/dir/guvnorng-playground.git", "guvnorng-playground"},
                {"file:///path/to/some/dir/guvnorng-playground/.git", "guvnorng-playground"},
                {"file:///path/to/some/dir/guvnorng-playground", "guvnorng-playground"},
                {"file:///path/to/some/dir/guvnorng-playground/", "guvnorng-playground"},
                {"file://C:\\path\\to\\some\\dir\\guvnorng-playground.git", "guvnorng-playground"},
                {"file://C:\\path\\to\\some\\dir\\guvnorng-playground\\.git", "guvnorng-playground"},
                {"file://C:\\path\\to\\some\\dir\\guvnorng-playground", "guvnorng-playground"},
                {"file://C:\\path\\to\\some\\dir\\guvnorng-playground\\", "guvnorng-playground"},
                {"file://C:/path/to/some/dir/guvnorng-playground.git", "guvnorng-playground"},
                {"file://C:/path/to/some/dir/guvnorng-playground/.git", "guvnorng-playground"},
                {"file://C:/path/to/some/dir/guvnorng-playground", "guvnorng-playground"},
                {"file://C:/path/to/some/dir/guvnorng-playground/", "guvnorng-playground"},
                {"git@github.com:guvnorngtestuser1/guvnorng-playground.git", "guvnorng-playground"},
                {"git@github.com:guvnorngtestuser1/guvnorng-playground/", "guvnorng-playground"},
                {"git@github.com:guvnorngtestuser1/guvnorng-playground/.git", "guvnorng-playground"},
                {"/", "new-project"},
                {"\\", "new-project"},
                {".git", "new-project"},
                {"/a", "a"},
        });
    }

    private String url;

    private String expectedName;

    public InferNamesProjectImportServiceImplTest(final String url,
                                                  final String expectedName) {
        this.url = url;
        this.expectedName = expectedName;
    }

    private final ProjectImportServiceImpl service = new ProjectImportServiceImpl(mock(IOService.class),
            mock(MetadataService.class),
            mock(RepositoryFactory.class),
            mock(KieModuleService.class),
            mock(ImportProjectValidators.class),
            mock(PathUtil.class),
            mock(WorkspaceProjectService.class),
            mock(ProjectScreenService.class),
            mock(Event.class),
            mock(RepositoryService.class));

    @Test
    public void test() {
        assertEquals(expectedName, service.inferProjectName(url));
    }

}
