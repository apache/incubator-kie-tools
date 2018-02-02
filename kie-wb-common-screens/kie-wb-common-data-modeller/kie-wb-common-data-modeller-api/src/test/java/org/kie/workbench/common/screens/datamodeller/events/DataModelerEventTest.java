/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.events;

import org.guvnor.common.services.project.model.Module;
import org.junit.Test;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.Method;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.MethodImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;

import static org.junit.Assert.*;

public class DataModelerEventTest {

    @Test
    public void createEvent() {
        DataObject currentDataObject = new DataObjectImpl();
        ObjectProperty currentField = new ObjectPropertyImpl();
        Method currentMethod = new MethodImpl();
        Module currentModule = new Module();
        String source = "testSource";
        String contextId = "testContextId";
        Path path = new PathFactory.PathImpl();

        DataModelerEvent event = new DataModelerEvent()
                .withCurrentDataObject(currentDataObject)
                .withCurrentField(currentField)
                .withCurrentMethod(currentMethod)
                .withCurrentProject(currentModule)
                .withSource(source)
                .withContextId(contextId)
                .withPath(path);

        assertEquals(currentDataObject, event.getCurrentDataObject());
        assertEquals(currentField, event.getCurrentField());
        assertEquals(currentMethod, event.getCurrentMethod());
        assertEquals(currentModule, event.getCurrentModule());
        assertEquals(source, event.getSource());
        assertEquals(contextId, event.getContextId());
        assertEquals(path, event.getPath());
    }
}
