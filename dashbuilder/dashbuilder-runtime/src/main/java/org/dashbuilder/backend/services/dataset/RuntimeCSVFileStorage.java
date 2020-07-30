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

package org.dashbuilder.backend.services.dataset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.dashbuilder.dataprovider.csv.CSVFileStorage;
import org.dashbuilder.dataset.def.CSVDataSetDef;

/**
 * In Memory CSV storage.
 *
 */
@ApplicationScoped
public class RuntimeCSVFileStorage implements CSVFileStorage {

    Map<String, String> csvStorage;

    public RuntimeCSVFileStorage() {
        // not used
    }

    @PostConstruct
    public void init() {
        csvStorage = new HashMap<>();
    }

    @Override
    public InputStream getCSVInputStream(CSVDataSetDef def) {
        String csvStr = getCSVString(def);
        return new ByteArrayInputStream(csvStr.getBytes());
    }

    @Override
    public String getCSVString(CSVDataSetDef def) {
        return csvStorage.getOrDefault(def.getUUID(), "");
    }

    public void storeCSV(String uuid, String csvContent) {
        csvStorage.put(uuid, csvContent);
    }

    @Override
    public void saveCSVFile(CSVDataSetDef def) {
        // not going to save

    }

    @Override
    public void deleteCSVFile(CSVDataSetDef def) {
        csvStorage.remove(def.getUUID());
    }
    
    public void deleteCSVFile(String uuid) {
        csvStorage.remove(uuid);
    }

}