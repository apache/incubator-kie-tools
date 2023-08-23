/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.definition.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

/**
 * Specialisation of {@link Import} that has a model count property.
 * It also synchronises {@see name} and {@see namespace} values.
 */
@Portable
public class ImportPMML extends Import {

    private Name wrapped;

    private int modelCount;

    public ImportPMML() {
        super();
        this.wrapped = nameHolder.getValue();
        this.setName(nameHolder.getValue());
    }

    public ImportPMML(final String namespace,
                      final LocationURI locationURI,
                      final String importType) {
        super(namespace,
              locationURI,
              importType);
        this.wrapped = nameHolder.getValue();
        this.getName().setValue(namespace);
        this.setName(nameHolder.getValue());
    }

    public int getModelCount() {
        return modelCount;
    }

    public void setModelCount(final int modelCount) {
        this.modelCount = modelCount;
    }

    @Override
    public String getNamespace() {
        return nameHolder.getValue().getValue();
    }

    @Override
    public void setNamespace(final String namespace) {
        super.setNamespace(namespace);
        nameHolder.getValue().setValue(namespace);
    }

    @Override
    public Name getName() {
        return wrapped;
    }

    @Override
    public void setName(final Name name) {
        super.setName(wrap(name));
        this.namespace = name.getValue();
    }

    private Name wrap(final Name name) {
        this.wrapped = name;

        return new Name(name.getValue()) {
            @Override
            public String getValue() {
                return ImportPMML.this.wrapped.getValue();
            }

            @Override
            public void setValue(final String value) {
                ImportPMML.this.wrapped.setValue(value);
                ImportPMML.this.namespace = value;
            }
        };
    }
}
