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


package org.kie.workbench.common.forms.model.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.model.ModelMetaData;
import org.kie.workbench.common.forms.model.ModelProperty;
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.impl.meta.ModelMetaDataImpl;

@Portable
public class ModelPropertyImpl implements ModelProperty {

    private String name;

    private TypeInfo typeInfo;

    private ModelMetaData metaData;

    public ModelPropertyImpl(@MapsTo("name") String name,
                             @MapsTo("typeInfo") TypeInfo typeInfo) {
        this.name = name;
        this.typeInfo = typeInfo;
        this.metaData = new ModelMetaDataImpl();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public TypeInfo getTypeInfo() {
        return typeInfo;
    }

    @Override
    public ModelMetaData getMetaData() {
        return metaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ModelPropertyImpl that = (ModelPropertyImpl) o;

        if (!name.equals(that.name)) {
            return false;
        }
        return typeInfo.equals(that.typeInfo);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = ~~result;
        result = 31 * result + typeInfo.hashCode();
        result = ~~result;
        result = 31 * result + metaData.hashCode();
        result = ~~result;
        return result;
    }
}
