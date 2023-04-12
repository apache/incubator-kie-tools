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
package org.dashbuilder.dataset.client;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.jboss.errai.common.client.api.RemoteCallback;

/**
 * Data set services for clients.
 * <p>It hides to client widgets where the data sets are stored and how they are fetched and processed.</p>
 */
@ApplicationScoped
public interface DataSetClientServices {

    /**
     * Enable/disable the ability to push remote data sets from server.
     */
    void setPushRemoteDataSetEnabled(boolean pushRemoteDataSetEnabled);

    /**
     * Fetch the metadata instance for the specified data set.
     *
     * @param uuid The UUID of the data set
     * @throws Exception It there is an unexpected error trying to execute the lookup request.
     */
    void fetchMetadata(final String uuid,
                              final DataSetMetadataCallback listener) throws Exception ;

    /**
     * Get the cached metadata instance for the specified data set.
     *
     * @param uuid The UUID of the data set. Null if the metadata is not stored on client yet.
     */
    DataSetMetadata getMetadata(String uuid);

    /**
     * Export a data set, specified by a data set lookup request, to CSV format.
     *
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error during the export.
     */
    void exportDataSetCSV(final DataSetLookup request,
                                 final DataSetExportReadyCallback listener) throws Exception;

    /**
     * Export a data set, specified by a data set lookup request, to Excel format.
     *
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error during the export.
     */
    void exportDataSetExcel(final DataSetLookup request,
                                   final DataSetExportReadyCallback listener) throws Exception;

    /**
     * Creates a brand new data set definition for the provider type specified
     *
     * @param type The provider type
     * @return A data set definition instance
     */
    void newDataSet(DataSetProviderType type,
                           RemoteCallback<DataSetDef> callback) throws Exception;
    /**
     * Process the specified data set lookup request for a given definition.
     *
     * @param def     The data set definition
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error trying to execute the lookup request.
     */
    void lookupDataSet(final DataSetDef def,
                              final DataSetLookup request,
                              final DataSetReadyCallback listener) throws Exception;

    /**
     * Process the specified data set lookup request.
     *
     * @param request The data set lookup request
     * @throws Exception It there is an unexpected error trying to execute the lookup request.
     */
    void lookupDataSet(final DataSetLookup request,
                              final DataSetReadyCallback listener) throws Exception;

    void getPublicDataSetDefs(RemoteCallback<List<DataSetDef>> callback);

}