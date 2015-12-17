/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodeller.core.DataObject;

@Portable
public class DataModelReload extends DataModelerEvent {

    public DataModelReload() {
    }

    public DataModelReload( String contextId, String source, DataObject currentDataObject ) {
        super( contextId, source, currentDataObject );
    }
}
