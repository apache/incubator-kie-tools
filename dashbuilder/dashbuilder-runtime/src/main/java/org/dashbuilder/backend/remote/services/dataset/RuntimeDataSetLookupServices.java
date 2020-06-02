/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend.remote.services.dataset;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetManager;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.service.DataSetLookupServices;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class RuntimeDataSetLookupServices implements DataSetLookupServices {

    @Inject
    DataSetManager manager;

    public RuntimeDataSetLookupServices() {
        // not used, but must exist
    }

    @Override
    public DataSet lookupDataSet(DataSetLookup lookup) throws Exception {
        return manager.lookupDataSet(lookup);
    }

    @Override
    public DataSet lookupDataSet(DataSetDef def, DataSetLookup lookup) throws Exception {
        return manager.lookupDataSet(lookup);
    }

    @Override
    public DataSetMetadata lookupDataSetMetadata(String uuid) throws Exception {
        return manager.getDataSetMetadata(uuid);
    }

}