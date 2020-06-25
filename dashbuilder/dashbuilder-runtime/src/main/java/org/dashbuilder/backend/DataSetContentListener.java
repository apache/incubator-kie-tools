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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.dashbuilder.backend.services.dataset.RuntimeCSVFileStorage;
import org.dashbuilder.backend.services.dataset.provider.RuntimeDataSetProviderRegistry;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefRegistry;
import org.dashbuilder.dataset.json.DataSetDefJSONMarshaller;
import org.dashbuilder.shared.event.NewDataSetContentEvent;
import org.dashbuilder.shared.model.DataSetContent;
import org.dashbuilder.shared.model.DataSetContentType;
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

    DataSetDefJSONMarshaller defMarshaller;

    @PostConstruct
    public void init() {
        defMarshaller = runtimeDataSetProviderRegistry.getDataSetDefJSONMarshaller();
    }

    public void register(@Observes NewDataSetContentEvent newDataSetContentEvent) {
        newDataSetContentEvent.getContent().forEach(this::registerDataSetContent);
    }

    public void registerDataSetContent(final DataSetContent content) {
        try {
            DataSetContentType contentType = content.getContentType();
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
        } catch (Exception e) {
            logger.error("Error registering dataset", e);
        }
    }

    private void registerDataSetDefinition(final DataSetContent content) throws Exception {
        try {
            DataSetDef dataSetDef = defMarshaller.fromJson(content.getContent());
            registry.registerDataSetDef(dataSetDef);
        } catch (Exception e) {
            logger.warn("Ignoring Dataset {}: error parsing Json", content.getId());
            logger.debug("Error parsing dataset {}. Content: {}",
                         content.getId(),
                         content.getContent(),
                         e);
        }
    }
}