/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.command;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.impl.DataObjectImpl;
import org.kie.workbench.common.services.datamodeller.core.impl.ObjectPropertyImpl;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemovePropertyCommandTest {

    @Test
    public void execute() {
        DataObject dataObject = new DataObjectImpl("org.test",
                                                   "TestDataObject");
        dataObject.addProperty(new ObjectPropertyImpl("testProperty",
                                                      Integer.class.getName(),
                                                      false));
        DataModelChangeNotifier notifier = mock(DataModelChangeNotifier.class);
        RemovePropertyCommand command = new RemovePropertyCommand(new DataModelerContext(),
                                                                  "source",
                                                                  dataObject,
                                                                  "testProperty",
                                                                  notifier);

        command.execute();

        assertNull(dataObject.getProperty("testProperty"));
        verify(notifier,
               times(1)).notifyChange(any(DataObjectFieldDeletedEvent.class));
    }
}
