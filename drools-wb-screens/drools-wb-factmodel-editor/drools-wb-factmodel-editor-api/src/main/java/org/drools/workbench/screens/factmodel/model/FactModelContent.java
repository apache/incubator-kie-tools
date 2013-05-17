/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.factmodel.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodel.oracle.PackageDataModelOracle;

import java.util.ArrayList;
import java.util.List;

@Portable
public class FactModelContent {

    private FactModels factModels;
    private List<FactMetaModel> superTypes = new ArrayList<FactMetaModel>();
    private PackageDataModelOracle oracle;

    public FactModelContent() {
    }

    public FactModelContent( final FactModels factModels,
                             final List<FactMetaModel> superTypes,
                             final PackageDataModelOracle oracle ) {
        this.factModels = factModels;
        this.superTypes.addAll( superTypes );
        this.oracle = oracle;
    }

    public FactModels getFactModels() {
        return this.factModels;
    }

    public List<FactMetaModel> getSuperTypes() {
        return this.superTypes;
    }

    public PackageDataModelOracle getDataModel() {
        return this.oracle;
    }

}
