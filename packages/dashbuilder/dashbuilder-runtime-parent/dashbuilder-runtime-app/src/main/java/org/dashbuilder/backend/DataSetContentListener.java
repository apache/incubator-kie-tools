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

package org.dashbuilder.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.backend.helper.PartitionHelper;
import org.dashbuilder.backend.services.dataset.RuntimeCSVFileStorage;
import org.dashbuilder.backend.services.dataset.provider.RuntimeDataSetProviderRegistry;
import org.dashbuilder.dataprovider.sql.SQLDataSourceLocator;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.kieserver.KieServerConnectionInfoProvider;
import org.dashbuilder.kieserver.RemoteDataSetDef;
import org.dashbuilder.shared.event.NewDataSetContentEvent;
import org.dashbuilder.shared.event.RemovedRuntimeModelEvent;
import org.dashbuilder.shared.model.DataSetContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listen for new dataset contents and register it accordingly.
 *
 */
@ApplicationScoped
public class DataSetContentListener {

    private final Logger logger = LoggerFactory.getLogger(DataSetContentListener.class);

    @Inject
    DataSetDefRegistry registry;

    @Inject
    RuntimeCSVFileStorage storage;

    @Inject
    RuntimeDataSetProviderRegistry runtimeDataSetProviderRegistry;

    @Inject
    SQLDataSourceLocator sqlDataSourceLocator;

    @Inject
    KieServerConnectionInfoProvider kieServerConnectionInfoProvider;

    DataSetDefJSONMarshaller defMarshaller;

    /**
     * Keep tracks of contents by runtime model to remove it later
     */
    Map<String, List<String>> runtimeModelDatasetContents;

    @PostConstruct
    public void init() {
        defMarshaller = runtimeDataSetProviderRegistry.getDataSetDefJSONMarshaller();
        runtimeModelDatasetContents = new HashMap<>();
    }

    public void register(@Observes NewDataSetContentEvent newDataSetContentEvent) {
        runtimeModelDatasetContents.put(newDataSetContentEvent.getRuntimeModelId(),
                newDataSetContentEvent.getContent()
                        .stream()
                        .map(DataSetContent::getId)
                        .collect(Collectors.toList()));
        newDataSetContentEvent.getContent()
                .forEach(this::registerDataSetContent);
    }

    public void unregister(@Observes RemovedRuntimeModelEvent removedRuntimeModelEvent) {
        var removedIds = runtimeModelDatasetContents.remove(removedRuntimeModelEvent.getRuntimeModelId());
        if (removedIds != null) {
            removedIds.forEach(id -> {
                storage.deleteCSVFile(id);
                registry.removeDataSetDef(id);
            });
        }
    }

    public void registerDataSetContent(final DataSetContent content) {
        try {
            var contentType = content.getContentType();
            switch (contentType) {
                case CSV:
                    storage.storeCSV(content.getId(), content.getContent());
                    break;
                case DEFINITION:
                    registerDataSetDefinition(content);
                    break;
                default:
                    logger.error("Unknown DataSet Content Type: {}", contentType.name(), null);
                    break;
            }
            logger.info("Sucessfully registered data set content \"{}\"", content.getId());
        } catch (Exception e) {
            logger.error("Error registering dataset", e);
            logger.debug("Error registering data set", e);
        }
    }

    private void registerDataSetDefinition(final DataSetContent content) throws Exception {
        DataSetDef dataSetDef;
        try {
            dataSetDef = defMarshaller.fromJson(content.getContent());
            dataSetDef.setUUID(content.getId());
        } catch (Exception e) {
            logger.warn("Ignoring Dataset {}: error parsing Json", content.getId());
            logger.debug("Error parsing dataset {}. Content: {}",
                    content.getId(),
                    content.getContent(),
                    e);
            return;
        }

        if (dataSetDef instanceof RemoteDataSetDef) {
            var remoteDef = ((RemoteDataSetDef) dataSetDef);
            var queryName = PartitionHelper.removePartition(dataSetDef.getUUID());
            checkRemoteDataSetDef(remoteDef);
            remoteDef.setQueryName(queryName);
        }

        else if (dataSetDef instanceof SQLDataSetDef) {
            checkSQLDataSet((SQLDataSetDef) dataSetDef);
        }

        registry.registerDataSetDef(dataSetDef);
    }

    private void checkRemoteDataSetDef(RemoteDataSetDef remoteDef) {
        kieServerConnectionInfoProvider.get(remoteDef.getUUID(), remoteDef.getServerTemplateId())
                .orElseThrow(() -> new RuntimeException("Configuration for remote dataset" + remoteDef.getName() +
                        " not found"));

    }

    private void checkSQLDataSet(SQLDataSetDef dataSetDef) throws Exception {
        try {
            sqlDataSourceLocator.lookup(dataSetDef);
        } catch (Exception e) {
            throw new RuntimeException("Datasource for data set " + dataSetDef.getName() + " not found", e);
        }
    }
}
