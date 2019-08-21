/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.scorecard.backend.server.indexing;

import org.drools.workbench.models.guided.scorecard.shared.Characteristic;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.soup.project.datamodel.commons.oracle.ModuleDataModelOracleUtils;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.soup.project.datamodel.oracle.ModuleDataModelOracle;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.backend.server.impact.ResourceReferenceCollector;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor to extract index information from a Guided Score Card Model
 */
public class GuidedScoreCardIndexVisitor extends ResourceReferenceCollector {

    private static final Logger logger = LoggerFactory.getLogger(GuidedScoreCardIndexVisitor.class);

    private final ModuleDataModelOracle dmo;
    private final ScoreCardModel model;

    public GuidedScoreCardIndexVisitor(final ModuleDataModelOracle dmo,
                                       final ScoreCardModel model) {
        this.dmo = PortablePreconditions.checkNotNull("dmo",
                                                      dmo);
        this.model = PortablePreconditions.checkNotNull("model",
                                                        model);
    }

    public void visit() {
        //Add type
        final String typeName = model.getFactName();
        if (typeName == null || typeName.isEmpty()) {
            return;
        }
        final String fullyQualifiedClassName = getFullyQualifiedClassName(typeName);
        ResourceReference resRef = addResourceReference(fullyQualifiedClassName,
                                                        ResourceType.JAVA);

        //Add field
        final String fieldName = model.getFieldName();
        if (fieldName == null || fieldName.isEmpty()) {
            return;
        }
        resRef.addPartReference(fieldName,
                                PartType.FIELD);
        final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName(fullyQualifiedClassName,
                                                                                    fieldName);
        addResourceReference(fieldFullyQualifiedClassName,
                             ResourceType.JAVA);

        //Add Characteristics
        for (Characteristic c : model.getCharacteristics()) {
            visit(c);
        }

        // agenda-group, ruleflow-group
        String agendaGroup = model.getAgendaGroup();
        if (agendaGroup != null && !agendaGroup.isEmpty()) {
            addSharedReference(agendaGroup,
                               PartType.AGENDA_GROUP);
        }
        String ruleFlowGroup = model.getRuleFlowGroup();
        if (ruleFlowGroup != null && !ruleFlowGroup.isEmpty()) {
            addSharedReference(ruleFlowGroup,
                               PartType.RULEFLOW_GROUP);
        }
        String modelName = model.getName();
        if (modelName != null && !modelName.isEmpty()) {
            addSharedReference(modelName,
                               PartType.SCORECARD_MODEL_NAME);
        }

        Imports imports = model.getImports();
        if (imports != null) {
            visit(imports);
        }
    }

    private void visit(final Imports imports) {
        for (Import imp : imports.getImports()) {
            String impStr = imp.getType();
            if (!impStr.endsWith("*")) {
                addResourceReference(impStr,
                                     ResourceType.JAVA);
            } else {
                logger.debug("Wildcard import encountered : '" + impStr + "'");
            }
        }
    }

    private void visit(final Characteristic c) {
        //Add type
        final String typeName = c.getFact();
        final String fullyQualifiedClassName = getFullyQualifiedClassName(typeName);
        ResourceReference resRef = addResourceReference(fullyQualifiedClassName,
                                                        ResourceType.JAVA);

        //Add field
        final String fieldName = c.getField();
        final String fieldFullyQualifiedClassName = getFieldFullyQualifiedClassName(fullyQualifiedClassName,
                                                                                    fieldName);
        resRef.addPartReference(fieldName,
                                PartType.FIELD);
        addResourceReference(fieldFullyQualifiedClassName,
                             ResourceType.JAVA);
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
        String className = ModuleDataModelOracleUtils.getFieldFullyQualifiedClassName(dmo, fullyQualifiedClassName, fieldName);
        if (className == null) {
            className = DataType.TYPE_OBJECT;
        }
        return className;
    }
}
