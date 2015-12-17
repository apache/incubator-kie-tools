/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

public class DataObjectNameChangeCommand extends AbstractDataModelCommand {

    private String newName;

    public DataObjectNameChangeCommand( DataModelerContext context, String source, DataObject dataObject,
            String newName,
            DataModelChangeNotifier notifier ) {
        super( context, source, dataObject, notifier );
        this.newName = newName;
    }

    @Override
    public void execute() {
        String oldName = dataObject.getName();

        getDataObject().setName( newName );

        DataObjectChangeEvent event = new DataObjectChangeEvent( ChangeType.OBJECT_NAME_CHANGE,
                getContext().getContextId(), getSource(), getDataObject(), null, oldName, newName );

        notifyChange( event );

    }
}
