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

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionServiceImpl.WorkItemDefinitions;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.backend.lookup.impl.VFSLookupManager;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionServiceImplTest {

    private static final String PATH_URI = "default://somePath";
    private static final String WID_EMAIL_CONTENT = loadStream("org/kie/workbench/common/stunner/bpmn/backend/workitem/Email.wid");

    @Mock
    private IOService ioService;

    @Mock
    private VFSLookupManager<WorkItemDefinitions> vfsLookupManager;

    @Mock
    private WorkItemDefinitionResources resources;

    @Mock
    private Path path;

    @Mock
    private org.uberfire.java.nio.file.Path nioPath;

    private WorkItemDefinitionServiceImpl tested;
    private Function<org.uberfire.backend.vfs.Path, WorkItemDefinition> itemSupplier;
    private Predicate<Path> pathAcceptor;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(path.toURI()).thenReturn(PATH_URI);
        when(resources.resolvePath(eq(path))).thenReturn(nioPath);
        when(resources.resolveSearchPath(eq(path))).thenReturn(nioPath);
        doAnswer(invocationOnMock -> {
            final org.uberfire.java.nio.file.Path path1 = (org.uberfire.java.nio.file.Path) invocationOnMock.getArguments()[0];
            return nioPath.equals(path1) ? WID_EMAIL_CONTENT : null;
        }).when(ioService).readAllString(any(org.uberfire.java.nio.file.Path.class));
        doAnswer(invocationOnMock -> {
            pathAcceptor = (Predicate<Path>) invocationOnMock.getArguments()[0];
            return vfsLookupManager;
        }).when(vfsLookupManager).setPathAcceptor(any(Predicate.class));
        doAnswer(invocationOnMock -> {
            itemSupplier = (Function<Path, WorkItemDefinition>) invocationOnMock.getArguments()[0];
            return vfsLookupManager;
        }).when(vfsLookupManager).setItemSupplier(any(Function.class));
        doAnswer(invocationOnMock -> Collections.singletonList(itemSupplier.apply(path))).when(vfsLookupManager).getItemsByPath(eq(nioPath));
        this.tested = new WorkItemDefinitionServiceImpl(ioService,
                                                        resources,
                                                        s -> vfsLookupManager);
        this.tested.init();
    }

    @Test
    public void testPathAcceptor() {
        assertNotNull(pathAcceptor);
        Path p1 = mock(Path.class);
        when(p1.getFileName()).thenReturn("email.test");
        assertFalse(pathAcceptor.test(p1));
        Path p2 = mock(Path.class);
        when(p2.getFileName()).thenReturn("email.wid");
        assertTrue(pathAcceptor.test(p2));
        Path p3 = mock(Path.class);
        when(p3.getFileName()).thenReturn("email.WID");
        assertTrue(pathAcceptor.test(p3));
    }

    @Test
    public void testSearch() {
        Collection<WorkItemDefinition> items = tested.search(path);
        assertNotNull(items);
        assertEquals(1, items.size());
        WorkItemDefinition item = items.iterator().next();
        assertNotNull(item);
        assertEquals("Email", item.getName());
    }

    @Test
    public void testGet() {
        Collection<WorkItemDefinition> items = tested.get(path);
        assertNotNull(items);
        assertEquals(1, items.size());
        WorkItemDefinition item = items.iterator().next();
        assertNotNull(item);
        assertEquals("Email", item.getName());
    }

    private static String loadStream(String path) {
        final StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(Thread.currentThread()
                                 .getContextClassLoader()
                                 .getResourceAsStream(path),
                         writer,
                         WorkItemDefinitionParser.ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}
