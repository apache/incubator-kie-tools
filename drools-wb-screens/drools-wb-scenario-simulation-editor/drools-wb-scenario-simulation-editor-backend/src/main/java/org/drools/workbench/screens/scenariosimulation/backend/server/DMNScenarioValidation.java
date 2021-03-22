/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.scenariosimulation.api.utils.ScenarioSimulationSharedUtils;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.drools.scenariosimulation.backend.util.DMNSimulationUtils.extractDMNModel;
import static org.drools.scenariosimulation.backend.util.DMNSimulationUtils.extractDMNRuntime;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.DMNUtils.getRootType;
import static org.drools.workbench.screens.scenariosimulation.backend.server.util.DMNUtils.navigateDMNType;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createFieldChangedError;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createGenericError;
import static org.drools.workbench.screens.scenariosimulation.model.FactMappingValidationError.createNodeChangedError;
import static org.kie.dmn.feel.lang.types.BuiltInType.UNKNOWN;

public class DMNScenarioValidation extends AbstractScenarioValidation {

    public static final DMNScenarioValidation INSTANCE = new DMNScenarioValidation();

    /**
     * Validate structure of a DMN test scenario.
     * Supported checks for each column:
     * - empty column skip
     * - DMN node removed
     * - simple type becomes complex type
     * - navigation of data type still valid
     * - field type changed
     * @param simulation
     * @param settings
     * @param kieContainer
     * @return
     */
    @Override
    public List<FactMappingValidationError> validate(Simulation simulation, Settings settings, KieContainer kieContainer) {
        List<FactMappingValidationError> errors = new ArrayList<>();
        String dmnFilePath = settings.getDmnFilePath();
        DMNModel dmnModel = getDMNModel(kieContainer, dmnFilePath);

        for (FactMapping factMapping : simulation.getScesimModelDescriptor().getFactMappings()) {
            if (isToSkip(factMapping)) {
                continue;
            }

            String nodeName = factMapping.getFactIdentifier().getName();

            DMNType factDMNType;
            try {
                factDMNType = dmnModel.getDecisionByName(nodeName) != null ?
                        dmnModel.getDecisionByName(nodeName).getResultType() :
                        dmnModel.getInputByName(nodeName).getType();
            } catch (NullPointerException e) {
                errors.add(createNodeChangedError(factMapping, "node not found"));
                continue;
            }

            List<String> steps = expressionElementToString(factMapping);

            try {
                DMNType fieldType = navigateDMNType(factDMNType, steps);
                if (!isDMNFactMappingValid(factMapping, fieldType)) {
                    errors.add(defineFieldChangedError(factMapping, factDMNType, fieldType));
                }
            } catch (IllegalStateException e) {
                errors.add(createGenericError(factMapping, e.getMessage()));
            }
        }
        return errors;
    }

    private FactMappingValidationError defineFieldChangedError(FactMapping factMapping, DMNType factType, DMNType fieldType) {
        String typeName = factMapping.getClassName();
        if (isConstraintAdded(typeName, fieldType)) {
            return FactMappingValidationError.createFieldAddedConstraintError(factMapping);
        }
        if (isConstraintRemoved(typeName, factType, fieldType)) {
            return FactMappingValidationError.createFieldRemovedConstraintError(factMapping);
        }
        return createFieldChangedError(factMapping, fieldType.getName());
    }

    private boolean isDMNFactMappingValid(FactMapping factMapping, DMNType dmnType) {
        // NOTE: Any/Undefined is a special case where collection is true
        Type rootType = getRootType((BaseDMNTypeImpl) dmnType);
        String typeName = factMapping.getClassName();
        boolean isCoherent = UNKNOWN.equals(rootType) || ScenarioSimulationSharedUtils.isList(typeName) ==
                dmnType.isCollection();
        if (!isCoherent) {
            return false;
        }
        String factMappingType = ScenarioSimulationSharedUtils.isList(typeName) ?
                factMapping.getGenericTypes().get(0) :
                typeName;

        return Objects.equals(factMappingType, dmnType.getName());
    }

    /**
     * To define if a constraint (allowed values) were added in a DMNType fieldType given a typeName, the following conditions
     * are requires:
     * - typeName MUST BE a BuiltInType (eg. STRING, NUMERIC ..)
     * - DMNType fieldType MUST have at least one defined Allowed Values
     * - DMNType fieldType MUST have a Base Type. It's name MUST be equals to given typeName.
     * @param typeName TypeName present in the scesim file
     * @param fieldType DMNType of field under analysis
     * @return
     */
    private boolean isConstraintAdded(String typeName, DMNType fieldType) {
        boolean isTypeNameBuiltInType = !Objects.equals(UNKNOWN, BuiltInType.determineTypeFromName(typeName));
        boolean hasFieldTypeAllowedValues = fieldType.getAllowedValues() != null && !fieldType.getAllowedValues().isEmpty();
        boolean hasFieldTypeBaseType = Objects.nonNull(fieldType.getBaseType());
        if (isTypeNameBuiltInType && hasFieldTypeBaseType && hasFieldTypeAllowedValues) {
            Type baseType = getRootType((BaseDMNTypeImpl) fieldType.getBaseType());
            return Objects.equals(typeName, baseType.getName());
        }
        return false;
    }

    /**
     * To define if a constraint (allowed values) were removed in a DMNType fieldType given a typeName, the following conditions
     * are requires:
     * - DMNType fieldType MUST DON'T have Allowed Values defined
     * - DMNType fieldType MUST DON'T have a Base Type.
     * - typeName MUST BE a DMNType factType's field. The field's DMNType MUST BE the same of given fieldType DMNType
     * @param typeName
     * @param factType Fact DMNType
     * @param fieldType Field DMNType
     * @return
     */
    private boolean isConstraintRemoved(String typeName, DMNType factType, DMNType fieldType) {
        boolean hasFieldTypeAllowedValues = fieldType.getAllowedValues() != null && !fieldType.getAllowedValues().isEmpty();
        boolean hasFieldTypeBaseType = Objects.nonNull(fieldType.getBaseType());
        boolean isTypeNameFactTypeField = factType.getFields().containsKey(typeName);
        if (!hasFieldTypeBaseType && !hasFieldTypeAllowedValues && isTypeNameFactTypeField) {
            DMNType typeNameDMNType = factType.getFields().get(typeName);
            return Objects.equals(fieldType.getNamespace(), typeNameDMNType.getNamespace());
        }
        return false;
    }

    protected DMNModel getDMNModel(KieContainer kieContainer, String dmnPath) {
        return extractDMNModel(extractDMNRuntime(kieContainer), dmnPath);
    }
}