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
package org.drools.workbench.screens.guided.dtable.backend.server.conversion;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.util.ColumnUtilitiesBase;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;

public class XLSColumnUtilities
        extends ColumnUtilitiesBase {

    private final PackageDataModelOracle dmo;

    public XLSColumnUtilities(final GuidedDecisionTable52 model,
                              final PackageDataModelOracle dmo,
                              final boolean respectLists) {
        super(model,
              respectLists);
        this.dmo = PortablePreconditions.checkNotNull("dmo", dmo);
    }

    protected String getTypeFromDataOracle(final String factType,
                                           final String fieldName) {

        final String fqcn = getFQCN(factType);

        if (dmo.getModuleModelFields().keySet().contains(fqcn)) {
            for (final ModelField modelField : dmo.getModuleModelFields().get(fqcn)) {
                if (modelField.getName().equals(fieldName)) {
                    return modelField.getType();
                }
            }
        }

        return "";
    }

    private String getFQCN(final String factType) {
        if (dmo.getModuleModelFields().keySet().contains(factType)) {
            return factType;
        } else {
            return String.format("%s.%s", model.getPackageName(), factType);
        }
    }
}
