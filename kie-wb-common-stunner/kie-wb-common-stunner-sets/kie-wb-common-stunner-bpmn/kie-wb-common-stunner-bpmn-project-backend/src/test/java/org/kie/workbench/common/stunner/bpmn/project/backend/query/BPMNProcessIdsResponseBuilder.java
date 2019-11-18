/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNProcessIdsResponseBuilder {

    @Mock
    private KObject kObject1;

    @Mock
    private KObject kObject2;

    @Mock
    private KProperty kProperty1;

    @Mock
    private KProperty kProperty2;

    @Mock
    private IOService ioService;

    @Mock
    private Path validPath;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private Logger logger;

    @Mock
    private FileSystemNotFoundException exception;

    @Test
    public void testJBPM8828() {
        final FindBpmnProcessIdsQuery findBpmnProcessIdsQuery = new FindBpmnProcessIdsQuery();

        final List<KObject> kObjects = new ArrayList<>();
        kObjects.add(kObject1);
        kObjects.add(kObject2);

        final List<KProperty<?>> kProperties1 = new ArrayList<>();
        kProperties1.add(kProperty1);

        final List<KProperty<?>> kProperties2 = new ArrayList<>();
        kProperties2.add(kProperty2);

        final String invalidFile = "ProcessA1";
        final String validFile = "ProcessB1";

        final URI validURI = URI.create(validFile);

        when(kObject1.getProperties()).thenReturn(kProperties1);
        when(kObject1.getKey()).thenReturn(invalidFile);

        when(kObject2.getProperties()).thenReturn(kProperties2);
        when(kObject2.getKey()).thenReturn(validFile);

        when(kProperty1.getName()).thenReturn(findBpmnProcessIdsQuery.getProcessIdResourceType().toString());
        when(kProperty1.getValue()).thenReturn(invalidFile);

        when(kProperty2.getName()).thenReturn(findBpmnProcessIdsQuery.getProcessIdResourceType().toString());
        when(kProperty2.getValue()).thenReturn(validFile);

        when(validPath.getFileName()).thenReturn(validPath);
        when(validPath.toUri()).thenReturn(validURI);
        when(validPath.getFileSystem()).thenReturn(fileSystem);

        String exceptionString = "FILE_SYSTEM_NOT_FOUND_EXCEPTION";
        when(exception.toString()).thenReturn(exceptionString);

        Set<String> attribViews = new HashSet<>();
        when(fileSystem.supportedFileAttributeViews()).thenReturn(attribViews);

        when(ioService.get(any(URI.class))).thenAnswer(
                new Answer<Path>() {
                    public Path answer(InvocationOnMock invocation) throws FileSystemNotFoundException {
                        Object[] args = invocation.getArguments();
                        URI uri = (URI) args[0];

                        if (uri.getPath().compareTo(invalidFile) == 0) {
                            throw exception;
                        }

                        return validPath;
                    }
                }
        );

        AbstractFindIdsQuery.BpmnProcessIdsResponseBuilder responseBuilder =
                new AbstractFindIdsQuery.BpmnProcessIdsResponseBuilder(ioService,
                                                                       findBpmnProcessIdsQuery.getProcessIdResourceType());
        responseBuilder.LOGGER = logger;

        List<RefactoringPageRow> response = responseBuilder.buildResponse(kObjects);
        RefactoringPageRow pageRow = response.get(0);
        Map<String, Path> pageRowValue = (Map<String, Path>) pageRow.getValue();

        verify(logger, times(1)).error(exceptionString);
        assertEquals(1, response.size(), 0);
        assertEquals(1, pageRowValue.size(), 0);
        assertTrue(validPath.compareTo(pageRowValue.get(validFile)) == 0);
    }
}