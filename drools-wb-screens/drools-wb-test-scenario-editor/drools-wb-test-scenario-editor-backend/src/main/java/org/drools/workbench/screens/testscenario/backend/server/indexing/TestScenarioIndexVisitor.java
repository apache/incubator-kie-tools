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
package org.drools.workbench.screens.testscenario.backend.server.indexing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;
import org.drools.workbench.models.testscenarios.shared.Fixture;
import org.drools.workbench.models.testscenarios.shared.FixtureList;
import org.drools.workbench.models.testscenarios.shared.FixturesMap;
import org.drools.workbench.models.testscenarios.shared.Scenario;
import org.drools.workbench.models.testscenarios.shared.VerifyFact;
import org.drools.workbench.models.testscenarios.shared.VerifyField;
import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModelField;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.model.KProperty;

/**
 * Visitor to extract index information from a Scenario
 */
public class TestScenarioIndexVisitor extends ResourceReferenceCollector {

    private final ModuleDataModelOracle dmo;
    private final DefaultIndexBuilder builder;
    private final Scenario model;
    private final Map<String, String> factDataToFullyQualifiedClassNameMap = new HashMap<String, String>();

    public TestScenarioIndexVisitor(final ModuleDataModelOracle dmo,
                                    final DefaultIndexBuilder builder,
                                    final Scenario model) {
        this.dmo = PortablePreconditions.checkNotNull("dmo",
                                                      dmo);
        this.builder = PortablePreconditions.checkNotNull("builder",
                                                          builder);
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
    }

    public Set<KProperty<?>> visit() {
        visit(model);
        return builder.build();
    }

    private void visit(final Scenario scenario) {
        for (FactData global : scenario.getGlobals()) {
            visit(global);
        }

        for (Fixture fixture : scenario.getFixtures()) {
            visit(fixture);
        }
    }

    private void visit(final Fixture fixture) {
        if (fixture instanceof FixtureList) {
            for (Fixture child : ((FixtureList) fixture)) {
                visit(child);
            }
        } else if (fixture instanceof FixturesMap) {
            for (Fixture child : ((FixturesMap) fixture).values()) {
                visit(child);
            }
        } else if (fixture instanceof FactData) {
            final FactData factData = (FactData) fixture;
            final String typeName = factData.getType();
            final String fullyQualifiedClassName = getFullyQualifiedClassName(typeName);
            ResourceReference resRef = addResourceReference(fullyQualifiedClassName,
                                                            ResourceType.JAVA);

            factDataToFullyQualifiedClassNameMap.put(factData.getName(),
                                                     fullyQualifiedClassName);

            for (Field field : factData.getFieldData()) {
                final String fieldName = field.getName();
                final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName(fullyQualifiedClassName,
                                                                                            fieldName);
                resRef.addPartReference(fieldName,
                                        PartType.FIELD);
                addResourceReference(fieldFullyQualifiedClassName,
                                     ResourceType.JAVA);
            }
        } else if (fixture instanceof VerifyFact) {
            final VerifyFact verifyFact = (VerifyFact) fixture;
            final String typeName = verifyFact.getName();

            //If VerifyFact is not anonymous lookup FQCN from previous FactData elements
            String fullyQualifiedClassName = null;
            if (!verifyFact.anonymous) {
                fullyQualifiedClassName = factDataToFullyQualifiedClassNameMap.get(verifyFact.getName());
            } else {
                fullyQualifiedClassName = getFullyQualifiedClassName(typeName);
            }
            ResourceReference resRef = null;
            if (fullyQualifiedClassName != null) {
                resRef = addResourceReference(fullyQualifiedClassName,
                                              ResourceType.JAVA);

                for (VerifyField field : verifyFact.getFieldValues()) {
                    final String fieldName = field.getFieldName();
                    final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName(fullyQualifiedClassName,
                                                                                                fieldName);
                    resRef.addPartReference(fieldName,
                                            PartType.FIELD);
                    addResourceReference(fieldFullyQualifiedClassName,
                                         ResourceType.JAVA);
                }
            }
        } else if (fixture instanceof VerifyRuleFired) {
            final VerifyRuleFired verifyRuleFired = (VerifyRuleFired) fixture;
            addResourceReference(verifyRuleFired.getRuleName(),
                                 ResourceType.RULE);
        }
    }

    private String getFullyQualifiedClassName(final String typeName) {
        if (typeName.contains(".")) {
            return typeName;
        }

        for (Import i : model.getImports().getImports()) {
            if (i.getType().endsWith(typeName)) {
                return i.getType();
            }
        }
        final String packageName = model.getPackageName();
        return (!(packageName == null || packageName.isEmpty()) ? packageName + "." + typeName : typeName);
    }

    private String getFieldFullyQualifiedClassName(final String fullyQualifiedClassName,
                                                   final String fieldName) {
        final ModelField[] mfs = dmo.getModuleModelFields().get(fullyQualifiedClassName);
        for (ModelField mf : mfs) {
            if (mf.getName().equals(fieldName)) {
                return mf.getClassName();
            }
        }
        return DataType.TYPE_OBJECT;
    }
}
