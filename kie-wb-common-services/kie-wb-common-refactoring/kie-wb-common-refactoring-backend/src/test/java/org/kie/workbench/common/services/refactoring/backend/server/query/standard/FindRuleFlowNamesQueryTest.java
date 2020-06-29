/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringMapPageRow;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FindRuleFlowNamesQueryTest {

    @Mock
    private KObject kObject;
    @Mock
    private KProperty property;
    @Mock
    private IOService ioService;
    private static final String GROUP_NAME = "Group_Name";
    private static final String FILE_NOT_EXIST_PATH = "default://master@SpaceA/ProjectA/src/main/resources/Rule_not_exist.rdrl";
    private static final String FILE_NAME = "Rule_number_two.rdrl";
    private static final String FILE_PATH = "default://master@SpaceA/ProjectA/src/main/resources/" + FILE_NAME;
    private static final URI FILE_NOT_EXIST_URI = URI.create(FILE_NOT_EXIST_PATH);
    private static final URI FILE_URI = URI.create(FILE_PATH);
    private static final SimpleFileSystemProvider fileSystemProvider = new SimpleFileSystemProvider();
    private static final Path path = fileSystemProvider.getPath(FILE_URI);
    private List<KObject> kObjects = new ArrayList<>();
    private List<KProperty<?>> properties = new ArrayList<>();

    private ResponseBuilder testedBuilder;

    @Before
    public void init() {
        // IO Service mock
        when(ioService.get(FILE_NOT_EXIST_URI)).thenThrow(new FileSystemNotFoundException(format("No filesystem for uri %s found.", FILE_NOT_EXIST_URI.toString())));
        when(ioService.get(FILE_URI)).thenReturn(path);
        // Tested classes
        FindRuleFlowNamesQuery query = new FindRuleFlowNamesQuery(ioService);

        // Indexed RuleFlow groups mock
        when(property.getName()).thenReturn(FindRuleFlowNamesQuery.SHARED_TERM);
        when(property.getValue()).thenReturn(GROUP_NAME);
        when(kObject.getProperties()).thenReturn(properties);

        // Class under test
        testedBuilder = query.getResponseBuilder();
    }

    @Test
    public void testNullObject() {
        List<RefactoringPageRow> list = testedBuilder.buildResponse(kObjects);

        assertEquals(0, list.size());
    }

    @Test
    public void testNoProperties() {
        kObjects.add(kObject);
        List<RefactoringPageRow> list = testedBuilder.buildResponse(kObjects);

        assertEquals(0, list.size());
    }

    @Test
    public void testNonGroupTermIsIgnored() {
        when(property.getName()).thenReturn("Random value");
        properties.add(property);
        kObjects.add(kObject);
        List<RefactoringPageRow> list = testedBuilder.buildResponse(kObjects);

        assertEquals(0, list.size());
    }

    @Test
    public void testNewGroupAdded() {
        properties.add(property);
        kObjects.add(kObject);
        when(kObject.getKey()).thenReturn(FILE_PATH);

        List<RefactoringPageRow> list = testedBuilder.buildResponse(kObjects);
        RefactoringMapPageRow row = (RefactoringMapPageRow) list.get(0);
        assertEquals(1, list.size());
        Map<?, ?> map = row.getValue();
        assertEquals(3, map.size());
        assertEquals(FILE_NAME, map.get("filename"));
        assertEquals(path.toUri().toString(), map.get("pathuri"));
        assertEquals(GROUP_NAME, map.get("name"));
    }

    @Test
    public void testExistentGroup() {
        properties.add(property);
        properties.add(property);

        kObjects.add(kObject);
        when(kObject.getKey()).thenReturn(FILE_PATH);

        List<RefactoringPageRow> list = testedBuilder.buildResponse(kObjects);
        assertEquals(2, list.size());

        RefactoringMapPageRow row = (RefactoringMapPageRow) list.get(0);
        assertMapPageRow(row.getValue());
        row = (RefactoringMapPageRow) list.get(1);
        assertMapPageRow(row.getValue());
    }

    private void assertMapPageRow(Map<?, ?> map) {
        assertEquals(3, map.size());
        assertEquals(FILE_NAME, map.get("filename"));
        assertEquals(path.toUri().toString(), map.get("pathuri"));
        assertEquals(GROUP_NAME, map.get("name"));
    }

    @Test
    public void testFileWithGroupIsDeletedOrNotExist() {
        properties.add(property);
        when(kObject.getKey()).thenReturn(FILE_NOT_EXIST_PATH);
        kObjects.add(kObject);
        List<RefactoringPageRow> list = testedBuilder.buildResponse(kObjects);

        assertEquals(0, list.size());
    }
}
