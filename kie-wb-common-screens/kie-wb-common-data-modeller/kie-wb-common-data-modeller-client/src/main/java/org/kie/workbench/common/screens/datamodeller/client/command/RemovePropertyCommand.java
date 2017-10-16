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

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectFieldDeletedEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

public class RemovePropertyCommand extends AbstractDataModelCommand {

    private String propertyName;

    public RemovePropertyCommand(final DataModelerContext context,
                                 final String source,
                                 final DataObject dataObject,
                                 final String propertyName,
                                 final DataModelChangeNotifier notifier) {
        super(context,
              source,
              dataObject,
              notifier);
        this.propertyName = PortablePreconditions.checkNotNull("propertyName",
                                                               propertyName);
    }

    @Override
    public void execute() {

        if (dataObject != null) {
            ObjectProperty property = dataObject.getProperty(propertyName);
            if (property != null) {
                dataObject.removeProperty(propertyName);
                notifyChange(new DataObjectFieldDeletedEvent(getContext().getContextId(),
                                                             getSource(),
                                                             getDataObject(),
                                                             property));
            }
        }
    }
}
