/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server;

import java.util.HashSet;
import java.util.Set;

import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ModuleRepositoriesContentHandlerTest {

    private ModuleRepositoriesContentHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new ModuleRepositoriesContentHandler();
    }

    @Test
    public void testNullSourceXml() throws Exception {
        final ModuleRepositories repositories = handler.toModel(null);
        assertNotNull(repositories);
        assertEquals(0,
                     repositories.getRepositories().size());
    }

    @Test
    public void testEmptySourceXml() throws Exception {
        final ModuleRepositories repositories = handler.toModel("");
        assertNotNull(repositories);
        assertEquals(0,
                     repositories.getRepositories().size());
    }

    @Test
    public void testNullModel() throws Exception {
        final String xml = handler.toString(null);
        assertEquals("",
                     xml);
    }

    @Test
    public void testEmptyRepositories() throws Exception {
        final String xml = handler.toString(new ModuleRepositories());
        assertEquals("<project-repositories>\n" +
                             "  <repositories/>\n" +
                             "</project-repositories>",
                     xml);
    }

    @Test
    public void testRepositoriesMarshalling() throws Exception {
        final Set<ModuleRepositories.ModuleRepository> repositories = new HashSet<ModuleRepositories.ModuleRepository>();
        repositories.add(new ModuleRepositories.ModuleRepository(true,
                                                                 new MavenRepositoryMetadata("id",
                                                                                             "url",
                                                                                             MavenRepositorySource.LOCAL)));
        final ModuleRepositories moduleRepositories = new ModuleRepositories(repositories);

        final String xml = handler.toString(moduleRepositories);
        assertEquals("<project-repositories>\n" +
                             "  <repositories>\n" +
                             "    <repository>\n" +
                             "      <include>true</include>\n" +
                             "      <metadata>\n" +
                             "        <id>id</id>\n" +
                             "        <url>url</url>\n" +
                             "        <source>LOCAL</source>\n" +
                             "      </metadata>\n" +
                             "    </repository>\n" +
                             "  </repositories>\n" +
                             "</project-repositories>",
                     xml);
    }

    @Test
    public void testRepositoriesUnmarshalling() throws Exception {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <metadata>\n" +
                "        <id>id</id>\n" +
                "        <url>url</url>\n" +
                "        <source>LOCAL</source>\n" +
                "      </metadata>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final ModuleRepositories repositories = handler.toModel(xml);
        assertNotNull(repositories);
        assertNotNull(repositories.getRepositories());
        assertEquals(1,
                     repositories.getRepositories().size());

        final ModuleRepositories.ModuleRepository repository = repositories.getRepositories().iterator().next();
        assertEquals("id",
                     repository.getMetadata().getId());
        assertEquals("url",
                     repository.getMetadata().getUrl());
        assertEquals(MavenRepositorySource.LOCAL,
                     repository.getMetadata().getSource());
        assertEquals(true,
                     repository.isIncluded());
    }

    @Test
    public void testRepositoriesUnmarshalling_InvalidXml() throws Exception {
        final String xml = "<project-repositories>\n" +
                "  <repositories>\n" +
                "    <repository>\n" +
                "      <include>true</include>\n" +
                "      <cheese>\n" +
                "        <name>cheddar</name>\n" +
                "      </cheese>\n" +
                "    </repository>\n" +
                "  </repositories>\n" +
                "</project-repositories>";

        final ModuleRepositories repositories = handler.toModel(xml);
        assertNotNull(repositories);
        assertNotNull(repositories.getRepositories());
        assertEquals(0,
                     repositories.getRepositories().size());
    }
}
