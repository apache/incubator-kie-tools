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

package org.kie.workbench.common.stunner.bpmn.project.backend.service.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringMapPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.ProcessDataEvent;
import org.kie.workbench.common.stunner.bpmn.project.backend.service.ProcessesDataService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessesDataServiceTest {

    private static final String ID1 = "P1.Process1";
    private static final String ID2 = "P1.Process2";

    @Mock
    private RefactoringQueryService queryService;

    @Mock
    private Event<ProcessDataEvent> processesUpdatedEvent;

    @Mock
    private Path path1;

    @Mock
    private Path path2;

    private ProcessesDataService tested;

    @Before
    public void setup() {
        List<RefactoringPageRow> results = new ArrayList<RefactoringPageRow>();
        RefactoringMapPageRow refactoringMapPageRow = new RefactoringMapPageRow();
        Map<String, Path> map = new HashMap<String, Path>();
        map.put(ID1,
                path1);
        map.put(ID2,
                path2);
        refactoringMapPageRow.setValue(map);
        results.add(refactoringMapPageRow);
        when(queryService.query(anyString(),
                                anyObject())).thenReturn(results);
        tested = new ProcessesDataService(queryService, processesUpdatedEvent);
    }

    @Test
    public void testGetBusinessProcessIDs() {
        Collection<String> results = tested.getBusinessProcessIDs();
        assertEquals(results.size(), 2);
        assertTrue(results.contains(ID1));
        assertTrue(results.contains(ID2));
    }
}
