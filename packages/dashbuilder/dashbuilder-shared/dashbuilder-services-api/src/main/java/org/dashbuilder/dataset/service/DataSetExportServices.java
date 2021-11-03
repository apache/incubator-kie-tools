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
package org.dashbuilder.dataset.service;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * Data set export (CSV & Excel) services
 */
@Remote
public interface DataSetExportServices {

    /**
     * Export a dataset, specified by a DataSetLookup, to CSV format.
     * @param dataSetLookup The dataSetLookup that defines the dataset to be exported.
     * @return The VFS path to the export file generated
     */
    org.uberfire.backend.vfs.Path exportDataSetCSV(DataSetLookup dataSetLookup);

    /**
     * Export a dataset, specified by a DataSetLookup, to Excel format.
     * @param dataSetLookup The dataSetLookup that defines the dataset to be exported.
     * @return The VFS path to the export file generated
     */
    org.uberfire.backend.vfs.Path exportDataSetExcel(DataSetLookup dataSetLookup);

    /**
     * Export a dataset to CSV format.
     * @param dataSet The dataset to export.
     * @return The VFS path to the export file generated
     */
    org.uberfire.backend.vfs.Path exportDataSetCSV(DataSet dataSet);

    /**
     * Export a dataset to Excel format.
     * @param dataSet The dataset to export.
     * @return The VFS path to the export file generated
     */
    org.uberfire.backend.vfs.Path exportDataSetExcel(DataSet dataSet);
}
