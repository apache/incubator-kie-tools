/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.stunner.bpmn.project.backend.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.DataModelOracleUtilities;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.kie.workbench.common.stunner.bpmn.project.service.DataTypesService;
import org.uberfire.backend.vfs.Path;

@Service
public class BPMNFindDataTypesProjectService implements DataTypesService {

    private DataModelService dataModelService;

    //CDI proxy
    protected BPMNFindDataTypesProjectService() {
        this(null);
    }

    @Inject
    public BPMNFindDataTypesProjectService(final DataModelService dataModelService) {
        this.dataModelService = dataModelService;
    }

    public List<String> getDataTypeNames(final Path path) {
        if (null == path) {
            return Collections.emptyList();
        }
        final List<String> dataTypeNames = new ArrayList<>();

        try {
            final PackageDataModelOracle oracle = dataModelService.getDataModel(path);
            final String[] fullyQualifiedClassNames = DataModelOracleUtilities.getFactTypes(oracle);

            dataTypeNames.addAll(Arrays.asList(fullyQualifiedClassNames));
            Collections.sort(dataTypeNames);
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }

        return dataTypeNames;
    }
}
