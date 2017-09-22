/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.service.impl;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectFinderServiceImplFinderTest extends AbstractDataObjectFinderTest {

    @Test
    public void testGetDataObjectProperties() {
        List<ObjectProperty> properties = service.getDataObjectProperties(TYPE_NAME, path);

        assertNotNull(properties);
        assertTrue(!properties.isEmpty());
        assertEquals(DATA_OBJECT_VALID_FIELDS, properties.size());

        properties.forEach(property -> {
            assertNotEquals(DataObjectFormModelHandler.SERIAL_VERSION_UID, property.getName());
            assertNotEquals(PERSISTENCE_ID_PROPERTY, property.getName());
        });
    }

    @Test
    public void testFindDataObject() {
        DataObject result = service.getDataObject(TYPE_NAME, path);

        assertEquals(dataObject, result);
    }

    @Test
    public void testFindDataObjecFormModels() {
        List<DataObjectFormModel> dataObjects = service.getAvailableDataObjects(path);

        assertNotNull(dataObjects);
        assertTrue(!dataObjects.isEmpty());
        assertEquals(1, dataObjects.size());
    }
}
