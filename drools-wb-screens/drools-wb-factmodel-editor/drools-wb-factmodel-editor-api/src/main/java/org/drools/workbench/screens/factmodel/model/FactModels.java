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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;
import org.drools.workbench.models.datamodel.packages.HasPackageName;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Model for Declarative Fact Types
 */
@Portable
public class FactModels implements HasImports,
                                   HasPackageName {

    private String packageName;

    private Imports imports = new Imports();

    private List<FactMetaModel> models = new ArrayList<FactMetaModel>();

    public FactModels() {
    }

    public List<FactMetaModel> getModels() {
        return models;
    }

    @Override
    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports( Imports imports ) {
        this.imports = imports;
    }

    @Override
    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public void setPackageName( final String packageName ) {
        this.packageName = packageName;
    }

}
