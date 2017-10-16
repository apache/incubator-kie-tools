/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.builder.core;

import java.util.Set;

import org.kie.scanner.KieModuleMetaData;
import org.kie.soup.project.datamodel.oracle.TypeSource;

public class TypeSourceResolver {

    private final Set<String> javaResources;
    private final KieModuleMetaData kieModuleMetaData;

    public TypeSourceResolver(final KieModuleMetaData kieModuleMetaData,
                              final Set<String> javaResources) {
        this.kieModuleMetaData = kieModuleMetaData;
        this.javaResources = javaResources;
    }

    public TypeSource getTypeSource(final Class<?> clazz) {
        if (isDeclaredInDRL(clazz)) {
            return TypeSource.DECLARED;
        } else if (isDefinedInProjectOrWithinDependency(clazz)) {
            return TypeSource.JAVA_PROJECT;
        } else {
            return TypeSource.JAVA_DEPENDENCY;
        }
    }

    private boolean isDefinedInProjectOrWithinDependency(final Class<?> clazz) {
        return javaResources.contains(toFQCN(clazz));
    }

    private boolean isDeclaredInDRL(final Class<?> clazz) {
        return kieModuleMetaData.getTypeMetaInfo(clazz).isDeclaredType();
    }

    private String toFQCN(final Class<?> clazz) {
        String fullyQualifiedClassName = clazz.getName();
        int innerClassIdentifierIndex = fullyQualifiedClassName.indexOf("$");
        if (innerClassIdentifierIndex > 0) {
            return fullyQualifiedClassName.substring(0,
                                                     innerClassIdentifierIndex);
        } else {
            return fullyQualifiedClassName;
        }
    }
}
