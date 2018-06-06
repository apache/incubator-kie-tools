/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import java.net.URL;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.backend.server.utils.POMContentHandler;
import org.guvnor.common.services.project.model.Dependency;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.m2repo.service.M2RepoService;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(WeldJUnitRunner.class)
public class POMServiceImplLoadTest {

    @Inject
    @Named("ioStrategy")
    IOService ioService;

    @Inject
    POMContentHandler pomContentHandler;

    @Mock
    M2RepoService m2RepoService;

    @Mock
    MetadataService metadataService;

    @Mock
    PomEnhancer pomEnhancer;

    private POMService service;

    private IOService ioServiceSpy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ioServiceSpy = spy(ioService);

        service = new POMServiceImpl(ioServiceSpy,
                                     pomContentHandler,
                                     m2RepoService,
                                     metadataService,
                                     new EventSourceMock<>(),
                                     mock(ModuleService.class),
                                     mock(CommentedOptionFactory.class),
                                     pomEnhancer);
    }

    @Test
    public void testLoad() throws Exception {
        final URL url = this.getClass().getResource("/TestProject/pom.xml");

        final Path path = ioService.get(url.toURI());

        POM pom = service.load(Paths.convert(path));

        assertEquals("org.test",
                     pom.getGav().getGroupId());
        assertEquals("my-test",
                     pom.getGav().getArtifactId());
        assertEquals("1.0",
                     pom.getGav().getVersion());

        assertEquals(2,
                     pom.getDependencies().size());

        assertContainsDependency("org.apache.commons",
                                 "commons-lang3",
                                 "compile",
                                 pom.getDependencies());
        assertContainsDependency("org.jboss.weld",
                                 "weld-core",
                                 "test",
                                 pom.getDependencies());
    }

    private void assertContainsDependency(String groupID,
                                          String artifactID,
                                          String scope,
                                          List<Dependency> dependencies) {
        boolean foundOne = false;
        for (Dependency dependency : dependencies) {
            if (groupID.equals(dependency.getGroupId())
                    && artifactID.equals(dependency.getArtifactId())
                    &&
                    (
                            scope.equals(dependency.getScope())
                                    || (scope.equals("compile") && dependency.getScope() == null)
                    )) {
                foundOne = true;
            }
        }

        assertTrue("Did not find dependency: " + groupID + ":" + artifactID + ":" + scope,
                   foundOne);
    }
}