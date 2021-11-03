/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.events;

import org.dashbuilder.dataset.def.DataSetDef;

public class DataSetDefModifiedEvent {

    DataSetDef oldDataSetDef;
    DataSetDef newDataSetDef;

    public DataSetDefModifiedEvent() {
    }

    public DataSetDefModifiedEvent(DataSetDef oldDataSetDef, DataSetDef newDataSetDef) {
        this.oldDataSetDef = oldDataSetDef;
        this.newDataSetDef = newDataSetDef;
    }

    public DataSetDef getOldDataSetDef() {
        return oldDataSetDef;
    }

    public void setOldDataSetDef(DataSetDef oldDataSetDef) {
        this.oldDataSetDef = oldDataSetDef;
    }

    public DataSetDef getNewDataSetDef() {
        return newDataSetDef;
    }

    public void setNewDataSetDef(DataSetDef newDataSetDef) {
        this.newDataSetDef = newDataSetDef;
    }
}
