/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectOpenReusableSubprocessServiceImplTest {

    private final static String PROCESS_ID = "processId";
    private final static String NOT_REGISTERED_PROCESS_ID = "not_registered_process_id";
    private final static String PROCESS_FILE_NAME = "File Name";
    private final static String PROCESS_URI = "URI";

    @Mock
    private RefactoringQueryService serviceQuery;
    @Mock
    private Path path;

    private ProjectOpenReusableSubprocessServiceImpl service;
    private List<RefactoringPageRow> rows;

    @Before
    public void setUp() {
        when(path.getFileName()).thenReturn(PROCESS_FILE_NAME);
        when(path.toURI()).thenReturn(PROCESS_URI);

        RefactoringPageRow<Map<String, Path>> row = new RefactoringPageRow<Map<String, Path>>() {
            @Override
            public void setValue(Map<String, Path> value) {
                super.setValue(value);
            }

            @Override
            public Map<String, Path> getValue() {
                Map<String, Path> subprocess = new HashMap<>();
                subprocess.put(PROCESS_ID, path);
                return subprocess;
            }
        };

        rows = new ArrayList<>();
        rows.add(row);

        service = new ProjectOpenReusableSubprocessServiceImpl(serviceQuery);
    }

    @Test
    public void testNotFound() {
        assertTrue(service.openReusableSubprocess(PROCESS_ID).isEmpty());
    }

    @Test
    public void testReusableSubprocessFound() {
        when(serviceQuery.query(service.getQueryName(), service.createQueryTerms())).thenReturn(rows);

        List<String> answer = service.openReusableSubprocess(PROCESS_ID);
        assertEquals(2, answer.size());
        assertEquals(PROCESS_FILE_NAME, answer.get(0));
        assertEquals(PROCESS_URI, answer.get(1));
    }

    @Test
    public void testProcessWithIdNotFound() {
        when(serviceQuery.query(service.getQueryName(), service.createQueryTerms())).thenReturn(rows);

        List<String> answer = service.openReusableSubprocess(NOT_REGISTERED_PROCESS_ID);
        assertEquals(0, answer.size());
    }
}