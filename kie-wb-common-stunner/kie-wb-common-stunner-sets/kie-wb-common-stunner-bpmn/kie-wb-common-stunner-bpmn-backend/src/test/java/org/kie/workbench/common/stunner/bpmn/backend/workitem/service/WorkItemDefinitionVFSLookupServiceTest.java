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

package org.kie.workbench.common.stunner.bpmn.backend.workitem.service;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionParserTest;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.WorkItemDefinitionResources;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.backend.vfs.impl.DirectoryStreamImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkItemDefinitionVFSLookupServiceTest {

    private static final String WID_EMAIL = "org/kie/workbench/common/stunner/bpmn/backend/workitem/Email.wid";

    @Mock
    private VFSService vfsService;

    @Mock
    private WorkItemDefinitionResources resources;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private Path widPath;

    private WorkItemDefinitionVFSLookupService tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() throws Exception {
        String emailRaw = WorkItemDefinitionParserTest.loadStream(WID_EMAIL);
        when(vfsService.newDirectoryStream(eq(path), any(DirectoryStream.Filter.class)))
                .thenReturn(new DirectoryStreamImpl(Collections.singletonList(widPath)));
        when(vfsService.readAllString(eq(widPath))).thenReturn(emailRaw);
        when(resources.resolveResources(eq(metadata))).thenReturn(Collections.singleton(path));

        this.tested = new WorkItemDefinitionVFSLookupService(vfsService,
                                                             resources);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilter() {
        Collection<WorkItemDefinition> result = tested.execute(metadata);
        ArgumentCaptor<DirectoryStream.Filter> filterCaptor = ArgumentCaptor.forClass(DirectoryStream.Filter.class);
        verify(vfsService, times(1))
                .newDirectoryStream(eq(path),
                                    filterCaptor.capture());
        DirectoryStream.Filter<Path> filter = filterCaptor.getValue();
        Path path1 = mock(Path.class);
        when(path1.getFileName()).thenReturn("someFile.wid");
        assertTrue(filter.accept(path1));
        when(path1.getFileName()).thenReturn("someFile.bpmn");
        assertFalse(filter.accept(path1));
        when(path1.getFileName()).thenReturn("someFile.WID");
        assertTrue(filter.accept(path1));
        when(path1.getFileName()).thenReturn("someFile.WiD");
        assertTrue(filter.accept(path1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExecute() {

        Collection<WorkItemDefinition> result = tested.execute(metadata);

        ArgumentCaptor<DirectoryStream.Filter> filterCaptor = ArgumentCaptor.forClass(DirectoryStream.Filter.class);
        verify(vfsService, times(1))
                .newDirectoryStream(eq(path),
                                    any(DirectoryStream.Filter.class));
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        WorkItemDefinition wid = result.iterator().next();
        assertEquals("Email", wid.getName());
    }
}
