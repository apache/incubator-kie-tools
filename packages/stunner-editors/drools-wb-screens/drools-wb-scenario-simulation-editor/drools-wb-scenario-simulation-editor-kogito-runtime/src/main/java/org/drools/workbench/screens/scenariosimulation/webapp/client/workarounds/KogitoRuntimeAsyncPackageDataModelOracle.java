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
package org.drools.workbench.screens.scenariosimulation.webapp.client.workarounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.scenariosimulation.kogito.client.dmo.KogitoAsyncPackageDataModelOracle;
import org.kie.soup.project.datamodel.oracle.ModelField;

@ApplicationScoped
public class KogitoRuntimeAsyncPackageDataModelOracle extends KogitoAsyncPackageDataModelOracle {

    @Override
    protected Map<String, String> retrieveParametricFieldMap() {
        return new HashMap<>();
    }

    @Override
    protected List<String> retrievePackageNames() {
        return new ArrayList<>();
    }

    @Override
    protected String[] retrieveFactTypes() {
        return new String[0];
    }

    @Override
    protected String[] retrieveFqcnNames() {
        return fqcnNames;
    }

    @Override
    protected Map<String, String> retrieveFqcnNamesMap() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, Boolean> retrieveCollectionTypes() {
        return new HashMap<>();
    }

    @Override
    protected Map<String, ModelField[]> retrieveModelFieldsMap() {
        return new HashMap<>();
    }

}
