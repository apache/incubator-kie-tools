/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodeller.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.kie.workbench.common.services.datamodeller.core.JavaInterface;
import org.kie.workbench.common.services.datamodeller.core.JavaType;
import org.kie.workbench.common.services.datamodeller.core.JavaTypeKind;
import org.kie.workbench.common.services.datamodeller.core.Visibility;

public class JavaInterfaceImpl extends AbstractJavaType implements JavaInterface {

    protected List<String> interfaces = new ArrayList<String>();

    public JavaInterfaceImpl() {
        //errai marshalling
    }

    public JavaInterfaceImpl(String packageName,
                             String name) {
        this(packageName,
             name,
             null);
    }

    public JavaInterfaceImpl(String packageName,
                             String name,
                             JavaType enclosingType) {
        super(packageName,
              name,
              JavaTypeKind.INTERFACE);
        this.enclosingType = enclosingType;
    }

    public JavaInterfaceImpl(String packageName,
                             String name,
                             JavaType enclosingType,
                             Visibility visibility) {
        super(packageName,
              name,
              JavaTypeKind.INTERFACE,
              visibility);
        this.enclosingType = enclosingType;
    }

    @Override
    public boolean isAbstract() {
        //TODO check how to implement this, I guess Roaster don't have this info.
        return false;
    }

    @Override
    public List<String> getInterfaces() {
        return interfaces;
    }

    @Override
    public void addInterface(String interfaceDefinition) {
        interfaces.add(interfaceDefinition);
    }

    @Override
    public String removeInterface(String interfaceDefinition) {
        return interfaces.remove(interfaceDefinition) ? interfaceDefinition : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        JavaInterfaceImpl that = (JavaInterfaceImpl) o;

        return !(interfaces != null ? !interfaces.equals(that.interfaces) : that.interfaces != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (interfaces != null ? interfaces.hashCode() : 0);
        result = ~~result;
        return result;
    }
}