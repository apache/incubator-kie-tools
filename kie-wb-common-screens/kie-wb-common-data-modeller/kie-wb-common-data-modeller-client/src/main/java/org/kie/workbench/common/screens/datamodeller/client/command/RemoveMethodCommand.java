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

package org.kie.workbench.common.screens.datamodeller.client.command;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.events.ChangeType;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectChangeEvent;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.datamodeller.core.Method;

public class RemoveMethodCommand extends AbstractDataModelCommand {

    private final Method method;

    public RemoveMethodCommand(final DataModelerContext context,
                               final String source,
                               final DataObject dataObject,
                               final Method method,
                               final DataModelChangeNotifier notifier) {
        super(context,
              source,
              dataObject,
              notifier);
        this.method = PortablePreconditions.checkNotNull("method",
                                                         method);
    }

    @Override
    public void execute() {
        if (method != null) {
            dataObject.removeMethod(method);

            DataModelerEvent event = new DataObjectChangeEvent()
                    .withChangeType(ChangeType.METHOD_REMOVE_CHANGE)
                    .withOldValue(method)
                    .withNewValue(null)
                    .withContextId(getContext().getContextId())
                    .withSource(getSource())
                    .withCurrentDataObject(getDataObject())
                    .withCurrentMethod(method);

            notifyChange(event);
        }
    }
}
