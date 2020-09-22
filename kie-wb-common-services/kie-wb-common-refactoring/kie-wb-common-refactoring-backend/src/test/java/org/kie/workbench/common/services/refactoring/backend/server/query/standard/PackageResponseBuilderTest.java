/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PackageResponseBuilderTest {

    private ResponseBuilder responseBuilder;

    @Before
    public void setUp() throws Exception {
        responseBuilder = new FindPackageNamesQuery().getResponseBuilder();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNotSupported() {
        responseBuilder.buildResponse(0, 0, new ArrayList<>());
    }

    @Test
    public void testBuildResponseNoKObjects() {
        assertTrue(responseBuilder.buildResponse(new ArrayList<>()).isEmpty());
    }

    @Test
    public void testbuildResponse() {
        final List<KObject> kObjects = new ArrayList<>();
        final KObject kObject = mock(KObject.class);
        final ArrayList<KProperty> kProperties = new ArrayList<>();
        final KProperty kProperty = mock(KProperty.class);
        doReturn("packageName").when(kProperty).getName();
        doReturn("org.test").when(kProperty).getValue();
        kProperties.add(kProperty);
        kProperties.add(mock(KProperty.class));
        doReturn(kProperties).when(kObject).getProperties();
        kObjects.add(kObject);

        final List<RefactoringPageRow> rowList = responseBuilder.buildResponse(kObjects);

        assertEquals(1, rowList.size());
        assertEquals("org.test", rowList.get(0).getValue());
    }
}
