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

package org.dashbuilder.transfer;

import java.util.List;

import org.dashbuilder.dataset.def.DataSetDef;

public class DataTransferAssets {

    private List<DataSetDef> datasetsDefinitions;

    private List<String> pages;

    public DataTransferAssets() {}

    public DataTransferAssets(List<DataSetDef> datasetsDefinitions, List<String> pages) {
        this.datasetsDefinitions = datasetsDefinitions;
        this.pages = pages;
    }

    public List<DataSetDef> getDatasetsDefinitions() {
        return datasetsDefinitions;
    }

    public List<String> getPages() {
        return pages;
    }

    @Override
    public String toString() {
        return "DataTransferAssetsExport [datasetsDefinitions=" + datasetsDefinitions + ", pages=" + pages + "]";
    }

}
