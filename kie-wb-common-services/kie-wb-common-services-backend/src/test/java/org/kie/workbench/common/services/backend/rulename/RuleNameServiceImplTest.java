/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.services.backend.rulename;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.backend.source.BaseSourceService;
import org.kie.workbench.common.services.backend.source.SourceService;
import org.kie.workbench.common.services.backend.source.SourceServices;
import org.kie.workbench.common.services.backend.source.SourceServicesImpl;
import org.kie.workbench.common.services.shared.rulename.RuleNameService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RuleNameServiceImplTest {

    private RuleNameService service;
    private SimpleFileSystemProvider simpleFileSystemProvider;
    private ArrayList<SourceService> sourceServicesList = new ArrayList<SourceService>();

    @Before
    public void setUp() throws Exception {
        simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        sourceServicesList.add(new MockSourceService("rdrl"));
        sourceServicesList.add(new MockSourceService("drl"));

        Instance instance = mock(Instance.class);
        when(instance.iterator()).thenReturn(sourceServicesList.iterator());

        SourceServices sourceServices = new SourceServicesImpl(instance);
        service = new RuleNameServiceImpl(sourceServices);
    }

    @Test
    public void testEmpty() throws Exception {
        assertEquals(0, service.getRuleNames("some.pgk").size());
    }

    @Test
    public void testDRLAdded() throws Exception {
        final Path testPath = simpleFileSystemProvider.getPath(this.getClass().getResource("test.drl").toURI());

        fireResourceAddedEvent(testPath);

        assertEquals(1, service.getRuleNames("some.pkg").size());
        assertEquals("test", service.getRuleNames("some.pkg").get(0));
    }

    @Test
    public void testRDRLAdded() throws Exception {
        final Path testPath = simpleFileSystemProvider.getPath(this.getClass().getResource("hello.rdrl").toURI());

        fireResourceAddedEvent(testPath);

        assertEquals(1, service.getRuleNames("org.test").size());
        assertEquals("hello", service.getRuleNames("org.test").get(0));
    }

    @Test
    public void testTwoDRLLAddedDifferentPackages() throws Exception {
        final Path drlPath = simpleFileSystemProvider.getPath(this.getClass().getResource("test.drl").toURI());
        final Path rdrlPath = simpleFileSystemProvider.getPath(this.getClass().getResource("hello.rdrl").toURI());

        fireResourceAddedEvent(drlPath);
        fireResourceAddedEvent(rdrlPath);

        assertEquals(1, service.getRuleNames("some.pkg").size());
        assertEquals("test", service.getRuleNames("some.pkg").get(0));
        assertEquals(1, service.getRuleNames("org.test").size());
        assertEquals("hello", service.getRuleNames("org.test").get(0));
    }

    @Test
    public void testNoSourceServiceForFile() throws Exception {
        final Path testPath = simpleFileSystemProvider.getPath(this.getClass().getResource("test.someunknownformat").toURI());

        fireResourceAddedEvent(testPath);

        assertEquals(0, service.getRuleNames("some.package").size());
    }

    @Test
    public void testDelete() throws Exception {
        final Path testPath = simpleFileSystemProvider.getPath(this.getClass().getResource("hello.rdrl").toURI());
        final Path testPath2 = simpleFileSystemProvider.getPath(this.getClass().getResource("hello.drl").toURI());

        fireResourceAddedEvent(testPath);
        fireResourceAddedEvent(testPath2);

        assertEquals(5, service.getRuleNames("org.test").size());
        assertTrue(service.getRuleNames("org.test").contains("hello"));

        fireResourceDeletedEvent(testPath);

        assertEquals(4, service.getRuleNames("org.test").size());
        assertFalse(service.getRuleNames("org.test").contains("hello"));
    }

    @Test
    public void testNoSourceServiceForFileDelete() throws Exception {
        final Path testPath = simpleFileSystemProvider.getPath(this.getClass().getResource("test.someunknownformat").toURI());

        fireResourceDeletedEvent(testPath);

        assertEquals(0, service.getRuleNames("some.package").size());
    }

    @Test
    public void testUpdate() throws Exception {

        DTableSourceServiceMock sourceService = new DTableSourceServiceMock("gdst");
        sourceServicesList.add(sourceService);

        final Path testPath = simpleFileSystemProvider.getPath(this.getClass().getResource("empty.gdst").toURI());

        fireResourceAddedEvent(testPath);

        assertEquals(1, service.getRuleNames("org.test").size());
        assertEquals("test", service.getRuleNames("org.test").get(0));

        sourceService.source =
                "package test.org\n"
                        + "rule newName\n"
                        + "when\n"
                        + "then\n"
                        + "end\n";

        fireResourceUpdatedEvent(testPath);

        assertEquals(1, service.getRuleNames("org.test").size());
        assertEquals("newName", service.getRuleNames("org.test").get(0));
    }

    // COPIED
    // RENAME
    // BATCH CHANGE

    private void fireResourceUpdatedEvent(Path path) {
        ((RuleNameServiceImpl) service).processResourceUpdate(getResourceUpdateEvent(path));
    }

    private ResourceUpdatedEvent getResourceUpdateEvent(Path path) {
        SessionInfo sessionInfo = mock(SessionInfo.class);
        return new ResourceUpdatedEvent(Paths.convert(path), sessionInfo);
    }

    private void fireResourceDeletedEvent(Path path) {
        ((RuleNameServiceImpl) service).processResourceDelete(getResourceDeletedEvent(path));
    }

    private void fireResourceAddedEvent(Path path) {
        ((RuleNameServiceImpl) service).processResourceAdd(getResourceAddedEvent(path));
    }

    private ResourceDeletedEvent getResourceDeletedEvent(Path path) {
        SessionInfo sessionInfo = mock(SessionInfo.class);
        return new ResourceDeletedEvent(Paths.convert(path), sessionInfo);
    }

    private ResourceAddedEvent getResourceAddedEvent(Path path) {
        SessionInfo sessionInfo = mock(SessionInfo.class);
        return new ResourceAddedEvent(Paths.convert(path), sessionInfo);
    }

    class DTableSourceServiceMock extends MockSourceService {

        String source =
                "package test.org\n"
                        + "rule test\n"
                        + "when\n"
                        + "then\n"
                        + "end\n";

        public DTableSourceServiceMock(String pattern) {
            super(pattern);
        }

        @Override
        public String getSource(Path path) {
            return source;
        }
    }

    class MockSourceService extends BaseSourceService {

        private String pattern;

        public MockSourceService(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getSource(Path path, Object model) {
            return null;
        }

        @Override
        public String getSource(Path path) {
            InputStream resourceAsStream = getClass().getResourceAsStream(path.getFileName().toString());

            StringBuilder drl = new StringBuilder();
            try {
                for (int c = resourceAsStream.read(); c != -1; c = resourceAsStream.read()) {
                    drl.append((char) c);
                }
                resourceAsStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return drl.toString();
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }
}
