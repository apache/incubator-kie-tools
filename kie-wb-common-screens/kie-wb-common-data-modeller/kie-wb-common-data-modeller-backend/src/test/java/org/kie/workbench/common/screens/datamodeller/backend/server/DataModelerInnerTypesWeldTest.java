/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.datamodeller.backend.server;

import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.core.DataModel;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.project.KieModule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataModelerInnerTypesWeldTest extends AbstractDataModelerServiceWeldTest {

    @Test
    public void dataModelerShouldIgnoreEnumFieldsOfInnerClasses() throws Exception {
        KieModule module = loadProjectFromResources("/TestInnerTypes");

        DataModel dataModel = dataModelService.loadModel(module);
        DataObject dataObject = dataModel.getDataObject("test.Outer");
        assertNotNull("DataObject test.Outer should be loaded",
                      dataObject);
        assertEquals("Enum fields of inner classes of test.Outer DataObject should be ignored",
                     0,
                     dataObject.getProperties().size());
    }
}
