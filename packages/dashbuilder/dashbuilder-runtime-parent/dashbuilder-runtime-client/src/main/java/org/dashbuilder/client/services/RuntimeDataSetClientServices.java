/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.dashbuilder.client.RuntimeClientLoader;
import org.dashbuilder.client.error.DefaultRuntimeErrorCallback;
import org.dashbuilder.client.error.ErrorResponseVerifier;
import org.dashbuilder.client.external.ExternalDataSetClientProvider;
import org.dashbuilder.client.marshalling.ClientDataSetMetadataJSONMarshaller;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetExportReadyCallback;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.jboss.errai.common.client.api.RemoteCallback;

@Alternative
@ApplicationScoped
public class RuntimeDataSetClientServices implements DataSetClientServices {

    @Inject
    ClientDataSetMetadataJSONMarshaller dataSetMetadataJsonMarshaller;

    @Inject
    ErrorResponseVerifier verifier;

    @Inject
    DefaultRuntimeErrorCallback errorCallback;

    @Inject
    ClientDataSetManager clientDataSetManager;

    @Inject
    RuntimeClientLoader loader;

    @Inject
    ExternalDataSetClientProvider externalDataSetClientProvider;

    public RuntimeDataSetClientServices() {
        // empty
    }

    @Override
    public void setPushRemoteDataSetEnabled(boolean pushRemoteDataSetEnabled) {
        // ignored
    }

    @Override
    public void fetchMetadata(String uuid, DataSetMetadataCallback listener) throws Exception {
        // empty
    }

    @Override
    public DataSetMetadata getMetadata(String uuid) {
        // empty
        return null;
    }

    @Override
    public void lookupDataSet(DataSetDef def, DataSetLookup lookup, DataSetReadyCallback listener) throws Exception {
        var clientDataSet = clientDataSetManager.lookupDataSet(lookup);
        if (!isAccumulate(lookup.getDataSetUUID()) && clientDataSet != null) {
            listener.callback(clientDataSet);
            return;
        }

        externalDataSetClientProvider.fetchAndRegister(lookup.getDataSetUUID(), lookup, listener);
    }

    private boolean isAccumulate(String uuid) {
        return externalDataSetClientProvider.get(uuid).map(def -> def.isAccumulate()).orElse(false);
    }

    @Override
    public void lookupDataSet(DataSetLookup request, DataSetReadyCallback listener) throws Exception {
        this.lookupDataSet(null, request, listener);
    }

    @Override
    public void exportDataSetCSV(DataSetLookup request, DataSetExportReadyCallback listener) throws Exception {
        throw new IllegalArgumentException("Export to CSV not supported");
    }

    @Override
    public void exportDataSetExcel(DataSetLookup request, DataSetExportReadyCallback listener) throws Exception {
        throw new IllegalArgumentException("Export to excel not supported");
    }

    @Override
    public void newDataSet(DataSetProviderType type, RemoteCallback<DataSetDef> callback) throws Exception {
        throw new IllegalArgumentException("New data sets are not supported");
    }

    @Override
    public void getPublicDataSetDefs(RemoteCallback<List<DataSetDef>> callback) {
        // ignored in runtime
    }

    void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent evt) {
        if (evt.getDataSetDef() != null) {
            var uuid = evt.getDataSetDef().getUUID();
            externalDataSetClientProvider.unregister(uuid);
            clientDataSetManager.removeDataSet(uuid);
        }

    }

}
