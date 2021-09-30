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
package org.dashbuilder.dataprovider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.dataprovider.csv.CSVDataSetProvider;
import org.dashbuilder.dataprovider.csv.CSVFileStorage;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;

@ApplicationScoped
public class CSVDataSetProviderCDI extends CSVDataSetProvider {

    public CSVDataSetProviderCDI() {
    }

    @Inject
    public CSVDataSetProviderCDI(StaticDataSetProviderCDI staticDataSetProvider,
                                 CSVFileStorage csvStorage) {

        super(staticDataSetProvider,
                csvStorage);
    }

    // Listen to changes on the data set definition registry

    protected void onDataSetStaleEvent(@Observes DataSetStaleEvent event) {
        DataSetDef def = event.getDataSetDef();
        if (DataSetProviderType.CSV.equals(def.getProvider())) {
            staticDataSetProvider.removeDataSet(def.getUUID());
        }
    }

    protected void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent  event) {
        DataSetDef def = event.getDataSetDef();
        if (DataSetProviderType.CSV.equals(def.getProvider())) {
            staticDataSetProvider.removeDataSet(def.getUUID());
        }
    }

    protected void onDataSetDefModifiedEvent(@Observes DataSetDefModifiedEvent event) {
        DataSetDef def = event.getOldDataSetDef();
        if (DataSetProviderType.CSV.equals(def.getProvider())) {
            staticDataSetProvider.removeDataSet(def.getUUID());
        }
    }
}
