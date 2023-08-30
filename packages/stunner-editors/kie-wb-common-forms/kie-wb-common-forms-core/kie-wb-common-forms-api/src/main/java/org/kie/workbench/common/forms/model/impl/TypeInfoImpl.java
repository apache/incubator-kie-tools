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
import org.kie.workbench.common.forms.model.TypeInfo;
import org.kie.workbench.common.forms.model.TypeKind;

@Portable
public class TypeInfoImpl implements TypeInfo {

    private TypeKind type = TypeKind.BASE;

    private String className;

    private boolean multiple = false;

    public TypeInfoImpl(String className) {
        this.className = className;
    }

    public TypeInfoImpl(String className,
                        boolean multiple) {
        this.className = className;
        this.multiple = multiple;
    }

    public TypeInfoImpl(@MapsTo("type") TypeKind type,
                        @MapsTo("className") String className,
                        @MapsTo("multiple") boolean multiple) {
        this.type = type;
        this.className = className;
        this.multiple = multiple;
    }

    public TypeKind getType() {
        return type;
    }

    public void setType(TypeKind type) {
        this.type = type;
    }

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TypeInfoImpl typeInfo = (TypeInfoImpl) o;

        if (multiple != typeInfo.multiple) {
            return false;
        }
        if (type != typeInfo.type) {
            return false;
        }
        return className.equals(typeInfo.className);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = ~~result;
        result = 31 * result + className.hashCode();
        result = ~~result;
        result = 31 * result + (multiple ? 1 : 0);
        result = ~~result;
        return result;
    }
}
