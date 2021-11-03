/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dashbuilder.transfer;

import java.util.Collections;
import java.util.List;

import org.dashbuilder.dataset.def.DataSetDef;

/**
 * Contains the dashboard elements that should be exported
 *
 */
public class DataTransferExportModel {

    private static final DataTransferExportModel EXPORT_ALL = new DataTransferExportModel(Collections.emptyList(), Collections.emptyList(), true, true);

    private List<DataSetDef> datasetDefinitions;
    private List<String> pages;
    private boolean exportNavigation;
    private boolean exportAll;

    public DataTransferExportModel() {}

    public DataTransferExportModel(List<DataSetDef> datasetDefinitions, List<String> pages, boolean exportNavigation) {
        this(datasetDefinitions, pages, exportNavigation, false);
    }

    protected DataTransferExportModel(List<DataSetDef> datasetDefinitions, List<String> pages, boolean exportNavigation, boolean exportAll) {
        this.datasetDefinitions = datasetDefinitions;
        this.pages = pages;
        this.exportNavigation = exportNavigation;
        this.exportAll = exportAll;
    }

    public static DataTransferExportModel exportAll() {
        return EXPORT_ALL;
    }

    public List<DataSetDef> getDatasetDefinitions() {
        return datasetDefinitions;
    }

    public void setDatasetDefinitions(List<DataSetDef> datasetDefinitions) {
        this.datasetDefinitions = datasetDefinitions;
    }

    public List<String> getPages() {
        return pages;
    }

    public void setPages(List<String> pages) {
        this.pages = pages;
    }

    public boolean isExportNavigation() {
        return exportNavigation;
    }

    public void setExportNavigation(boolean exportNavigation) {
        this.exportNavigation = exportNavigation;
    }

    public boolean isExportAll() {
        return exportAll;
    }

    public void setExportAll(boolean exportAll) {
        this.exportAll = exportAll;
    }

}