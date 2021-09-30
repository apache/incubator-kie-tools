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
package org.dashbuilder.dataset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.dashbuilder.DataSetCore;
import org.dashbuilder.config.Config;
import org.dashbuilder.dataprovider.DataSetProviderRegistryCDI;
import org.dashbuilder.dataprovider.csv.CSVFileStorage;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.events.DataSetStaleEvent;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.dataset.uuid.UUIDGenerator;
import org.dashbuilder.exception.ExceptionManager;
import org.dashbuilder.project.storage.ProjectStorageServices;
import org.dashbuilder.scheduler.SchedulerCDI;
import org.uberfire.backend.vfs.PathFactory;

/**
 * Data set definition registry implementation which stores data sets under GIT
 * <p>It's provided as an extension to the default in-memory based registry and it's
 * also the default CDI implementation available.</p>
 */
@ApplicationScoped
public class DataSetDefRegistryCDI extends DataSetDefRegistryImpl implements CSVFileStorage {

    public static final String DATASET_EXT = ".dset";
    public static final String CSV_EXT = ".csv";

    protected int maxCsvLength;
    protected ExceptionManager exceptionManager;
    protected UUIDGenerator uuidGenerator;
    protected Event<DataSetDefModifiedEvent> dataSetDefModifiedEvent;
    protected Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent;
    protected Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent;
    protected Event<DataSetStaleEvent> dataSetStaleEvent;

    private ProjectStorageServices projectStorageServices;

    public DataSetDefRegistryCDI() {
        super();
    }

    @Inject
    public DataSetDefRegistryCDI(@Config("10485760" /* 10 Mb */) int maxCsvLength,
                                 ProjectStorageServices projectStorageServices,
                                 DataSetProviderRegistryCDI dataSetProviderRegistry,
                                 SchedulerCDI scheduler,
                                 ExceptionManager exceptionManager,
                                 Event<DataSetDefModifiedEvent> dataSetDefModifiedEvent,
                                 Event<DataSetDefRegisteredEvent> dataSetDefRegisteredEvent,
                                 Event<DataSetDefRemovedEvent> dataSetDefRemovedEvent,
                                 Event<DataSetStaleEvent> dataSetStaleEvent) {

        super(dataSetProviderRegistry, scheduler);
        this.uuidGenerator = DataSetCore.get().getUuidGenerator();
        this.maxCsvLength = maxCsvLength;
        this.projectStorageServices = projectStorageServices;
        this.exceptionManager = exceptionManager;
        this.dataSetDefModifiedEvent = dataSetDefModifiedEvent;
        this.dataSetDefRegisteredEvent = dataSetDefRegisteredEvent;
        this.dataSetDefRemovedEvent = dataSetDefRemovedEvent;
        this.dataSetStaleEvent = dataSetStaleEvent;
    }

    @PostConstruct
    public void init() {
        var dataSetDefJSONMarshaller = new DataSetDefJSONMarshaller(dataSetProviderRegistry);
        DataSetCore.get().setDataSetDefJSONMarshaller(dataSetDefJSONMarshaller);
        registerDataSetDefs();
    }

    public DataSetDefJSONMarshaller getDataSetDefJsonMarshaller() {
        return DataSetCore.get().getDataSetDefJSONMarshaller();
    }

    @Override
    protected void onDataSetDefStale(DataSetDef def) {
        dataSetStaleEvent.fire(new DataSetStaleEvent(def));
    }

    @Override
    protected void onDataSetDefModified(DataSetDef olDef,
                                        DataSetDef newDef) {
        dataSetDefModifiedEvent.fire(new DataSetDefModifiedEvent(olDef,
                newDef));
    }

    @Override
    protected void onDataSetDefRegistered(DataSetDef newDef) {
        dataSetDefRegisteredEvent.fire(new DataSetDefRegisteredEvent(newDef));
    }

    @Override
    protected void onDataSetDefRemoved(DataSetDef oldDef) {
        dataSetDefRemovedEvent.fire(new DataSetDefRemovedEvent(oldDef));
    }

    protected void registerDataSetDefs() {
        for (DataSetDef def : listDataSetDefs()) {
            super.dataSetDefMap.put(def.getUUID(),
                    new DataSetDefEntry(def));
        }
    }

    @Override
    public void registerDataSetDef(DataSetDef def,
                                   String subjectId,
                                   String message) {

        if (def.getUUID() == null) {
            final String uuid = uuidGenerator.newUuid();
            def.setUUID(uuid);
        }

        try {
            String defJson = getDataSetDefJsonMarshaller().toJsonString(def);
            projectStorageServices.saveDataSet(def.getUUID(), defJson);

            // CSV specific
            if (def instanceof CSVDataSetDef) {
                saveCSVFile((CSVDataSetDef) def);
            }
            super.registerDataSetDef(def,
                    subjectId,
                    message);
        } catch (Exception e) {
            throw exceptionManager.handleException(
                    new Exception("Can't register the data set definition\n" + def,
                            e));
        }
    }

    @Override
    public DataSetDef removeDataSetDef(String uuid,
                                       String subjectId,
                                       String message) {
        DataSetDef def = getDataSetDef(uuid);
        if (def == null) {
            return null;
        }
        return removeDataSetDef(def,
                subjectId,
                message);
    }

    public void removeDataSetDef(org.uberfire.backend.vfs.Path path,
                                 String subjectId,
                                 String comment) {
        DataSetDef def = loadDataSetDef(path);
        if (def != null) {
            removeDataSetDef(def,
                    subjectId,
                    comment);
        }
    }

    public DataSetDef removeDataSetDef(DataSetDef def,
                                       String subjectId,
                                       String message) {
        projectStorageServices.removeDataSet(def.getUUID());
        return super.removeDataSetDef(def.getUUID(),
                subjectId,
                message);
    }

    public Collection<DataSetDef> listDataSetDefs() {
        return projectStorageServices.listDataSets().values().stream().map(this::parseDataSet).collect(Collectors
                .toList());
    }

    public DataSetDef loadDataSetDef(org.uberfire.backend.vfs.Path path) {
        var content = projectStorageServices.getDataSet(path.getFileName());
        if (content.isPresent()) {
            try {
                return getDataSetDefJsonMarshaller().fromJson(content.get());
            } catch (Exception e) {
                String msg = "Error parsing data set JSON definition: " + path.getFileName();
                throw exceptionManager.handleException(new Exception(msg,
                        e));
            }
        }
        return null;
    }

    public DataSetDef copyDataSetDef(DataSetDef def,
                                     String newName,
                                     String subjectId,
                                     String message) {
        DataSetDef clone = def.clone();
        clone.setUUID(uuidGenerator.newUuid());
        clone.setName(newName);
        try {
            // CSV specific
            if (def instanceof CSVDataSetDef) {
                var csvDef = (CSVDataSetDef) def;
                var csvCloneDef = (CSVDataSetDef) clone;
                var csvName = resolveCsvName(csvDef);
                var cloneCsvName = resolveCsvName(csvCloneDef);
                var csvContent = projectStorageServices.getDataSetContent(csvName);
                if (csvContent.isPresent()) {
                    projectStorageServices.saveDataSetContent(cloneCsvName, csvContent.get());
                }
            }
            var defJson = getDataSetDefJsonMarshaller().toJsonString(clone);
            projectStorageServices.saveDataSet(clone.getUUID(), defJson);

            super.registerDataSetDef(clone,
                    subjectId,
                    message);
            return clone;
        } catch (Exception e) {
            throw exceptionManager.handleException(
                    new Exception("Can't register the data set definition\n" + def,
                            e));
        }
    }



    //
    // CSV files storage
    //

    @Override
    public String getCSVString(CSVDataSetDef def) {
        var csvName = resolveCsvName(def);
        var csvContent = projectStorageServices.getDataSetContent(csvName);
        // return null to keep compatibility
        return csvContent.orElse(null);
    }

    @Override
    public InputStream getCSVInputStream(CSVDataSetDef def) {
        var csvName = resolveCsvName(def);
        var csvContent = projectStorageServices.getDataSetContent(csvName);
        return csvContent.map(csv -> new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8))).orElse(null);
    }

    @Override
    public void deleteCSVFile(CSVDataSetDef def) {
        var csvName = resolveCsvName(def);
        projectStorageServices.removeDataSetContent(csvName);
    }

    @Override
    public void saveCSVFile(CSVDataSetDef def) {
        String path = def.getFilePath();
        if (StringUtils.isBlank(path)) {
            return;
        }

        // The CSV file was uploaded from UI to the temp directory => move the file to the definitions directory
        // TODO: Adjust upload tmp DIR  = grab the TMP and use the new storage
    }

    protected String resolveCsvName(CSVDataSetDef def) {
        return def.getUUID() + CSV_EXT;
    }

    void onDataSetDefRegisteredEvent(@Observes DataSetDefRegisteredEvent event) {
        DataSetDef def = event.getDataSetDef();
        dataSetDefMap.put(
                def.getUUID(),
                new DataSetDefEntry(def));
    }

    private DataSetDef parseDataSet(String content) {
        try {
            return getDataSetDefJsonMarshaller().fromJson(content);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing dataset content", e);
        }
    }

    public org.uberfire.backend.vfs.Path resolveVfsPath(DataSetDef dataSetDef) {
        return convertDefToPath(dataSetDef);
    }

    private org.uberfire.backend.vfs.Path convertDefToPath(DataSetDef def) {
        return PathFactory.newPath(def.getUUID(), def.getUUID());
    }
}
