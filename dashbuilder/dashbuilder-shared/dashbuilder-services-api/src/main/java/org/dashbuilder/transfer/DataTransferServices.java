/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.jboss.errai.bus.server.annotations.Remote;

@Remote
public interface DataTransferServices {

    public static final String FILE_PATH = "dashbuilder-data-transfer";
    public static final String EXPORT_FILE_NAME = "export.zip";
    public static final String IMPORT_FILE_NAME = "import.zip";
    public static final String COMPONENTS_EXPORT_PATH = "dashbuilder/components/";


    public String doExport(DataTransferExportModel exportsModel) throws java.io.IOException;

    public List<String> doImport() throws Exception;

    public DataTransferAssets assetsToExport();

}