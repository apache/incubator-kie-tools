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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.json.DisplayerSettingsJSONMarshaller;
import org.dashbuilder.navigation.service.PerspectivePluginServices;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class ExportModelValidationServiceImpl implements ExportModelValidationService {

    PerspectivePluginServices perspectivePluginServices;

    DisplayerSettingsJSONMarshaller marshaller;

    public ExportModelValidationServiceImpl() {}

    @Inject
    public ExportModelValidationServiceImpl(PerspectivePluginServices perspectivePluginServices) {
        this(perspectivePluginServices, DisplayerSettingsJSONMarshaller.get());
    }

    ExportModelValidationServiceImpl(PerspectivePluginServices perspectivePluginServices,
                                     DisplayerSettingsJSONMarshaller marshaller) {
        this.perspectivePluginServices = perspectivePluginServices;
        this.marshaller = marshaller;
    }

    @Override
    public Map<String, List<String>> checkMissingDatasets(DataTransferExportModel exportModel) {
        if (exportModel == null || exportModel.getPages().isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> deps = exportModel.getPages()
                                                    .stream()
                                                    .collect(Collectors.toMap(p -> p,
                                                                              p -> datasetsUsedInPage(p, exportModel)));

        deps.entrySet().removeIf(e -> e.getValue().isEmpty());
        return deps;
    }

    private List<String> datasetsUsedInPage(String p, DataTransferExportModel exportModel) {
        List<DataSetDef> exportedDefs = exportModel.getDatasetDefinitions();
        return allDataSetsFromPage(p).filter(uuid -> isDataSetMissing(uuid, exportedDefs))
                                     .distinct()
                                     .collect(Collectors.toList());
    }

    private boolean isDataSetMissing(String uuid, List<DataSetDef> exportedDefs) {
        return exportedDefs.isEmpty() || exportedDefs.stream().noneMatch(ds -> ds.getUUID().equals(uuid));
    }

    private Stream<String> allDataSetsFromPage(String p) {
        return perspectivePluginServices.getLayoutTemplate(p)
                                        .getRows().stream()
                                        .flatMap(r -> r.getLayoutColumns().stream())
                                        .flatMap(cl -> cl.getLayoutComponents().stream())
                                        .map(lc -> lc.getProperties().get("json"))
                                        .filter(Objects::nonNull)
                                        .map(marshaller::fromJsonString)
                                        .map(DisplayerSettings::getDataSetLookup)
                                        .filter(Objects::nonNull)
                                        .map(DataSetLookup::getDataSetUUID)
                                        .filter(Objects::nonNull)
                                        ;
    }

}