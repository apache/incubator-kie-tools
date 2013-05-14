/**
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.screens.datamodeller.events;

import org.kie.workbench.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.screens.datamodeller.model.ObjectPropertyTO;

public class DataObjectFieldDeletedEvent extends DataModelerEvent {

    public DataObjectFieldDeletedEvent() {
    }

    public DataObjectFieldDeletedEvent(String source, DataModelTO currentModel, DataObjectTO currentDataObject, ObjectPropertyTO currentField) {
        super(source, currentModel, currentDataObject);
        setCurrentField(currentField);
    }
}
