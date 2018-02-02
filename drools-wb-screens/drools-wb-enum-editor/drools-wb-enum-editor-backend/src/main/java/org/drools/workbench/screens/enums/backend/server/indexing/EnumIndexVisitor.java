/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.enums.backend.server.indexing;

import java.util.Map;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.datamodel.backend.server.builder.util.DataEnumLoader;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;

/**
 * Visitor to extract index information from a DataEnumLoader
 */
public class EnumIndexVisitor extends ResourceReferenceCollector {

    private static final Logger logger = LoggerFactory.getLogger(EnumIndexVisitor.class);

    private final ModuleDataModelOracle dmo;
    private final Path resourcePath;
    private final DataEnumLoader enumLoader;

    public EnumIndexVisitor(final ModuleDataModelOracle dmo,
                            final Path resourcePath,
                            final DataEnumLoader enumLoader) {
        this.dmo = PortablePreconditions.checkNotNull("dmo",
                                                      dmo);
        this.resourcePath = PortablePreconditions.checkNotNull("resourcePath",
                                                               resourcePath);
        this.enumLoader = PortablePreconditions.checkNotNull("enumLoader",
                                                             enumLoader);
    }

    public void visit() {
        if (enumLoader.hasErrors()) {
            logger.error("Errors when indexing " + resourcePath.toAbsolutePath().toFile().getAbsolutePath());
            return;
        }
        for (Map.Entry<String, String[]> e : enumLoader.getData().entrySet()) {
            //Add type
            final String typeName = getTypeName(e.getKey());
            final String fullyQualifiedClassName = getFullyQualifiedClassName(typeName);

            //Add field
            final String fieldName = getFieldName(e.getKey());
            final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName(fullyQualifiedClassName,
                                                                                        fieldName);

            //If either type or field could not be resolved log a warning
            if (fullyQualifiedClassName == null) {
                logger.warn("Index entry will not be created for '" + e.getKey() + "'. Unable to determine FQCN for '" + typeName + "'. ");
            } else {
                ResourceReference resRef = addResourceReference(fullyQualifiedClassName,
                                                                ResourceType.JAVA);
                if (fieldFullyQualifiedClassName == null) {
                    logger.warn("Index entry will not be created for '" + e.getKey() + "'. Unable to determine FQCN for '" + typeName + "." + fieldName + "'. ");
                } else {
                    resRef.addPartReference(fieldName,
                                            PartType.FIELD);
                    addResourceReference(fieldFullyQualifiedClassName,
                                         ResourceType.JAVA);
                }
            }
        }
    }

    private String getTypeName(final String key) {
        final int hashIndex = key.indexOf("#");
        return key.substring(0,
                             hashIndex);
    }

    private String getFieldName(final String key) {
        final int hashIndex = key.indexOf("#");
        return key.substring(hashIndex + 1);
    }

    private String getFullyQualifiedClassName(final String typeName) {
        if (typeName.contains(".")) {
            return typeName;
        }
        //Look-up FQCN in DMO, if not found return null and log a warning
        for (Map.Entry<String, ModelField[]> e : dmo.getModuleModelFields().entrySet()) {
            String fqcn = e.getKey();
            if (e.getKey().contains(".")) {
                fqcn = fqcn.substring(fqcn.lastIndexOf(".") + 1);
            }
            if (fqcn.equals(typeName)) {
                return e.getKey();
            }
        }
        return null;
    }

    private String getFieldFullyQualifiedClassName(final String fullyQualifiedClassName,
                                                   final String fieldName) {
        //Look-up FQCN in DMO, if not found return null and log a warning
        final ModelField[] mfs = dmo.getModuleModelFields().get(fullyQualifiedClassName);
        if (mfs != null) {
            for (ModelField mf : mfs) {
                if (mf.getName().equals(fieldName)) {
                    return mf.getClassName();
                }
            }
        }
        return null;
    }
}
