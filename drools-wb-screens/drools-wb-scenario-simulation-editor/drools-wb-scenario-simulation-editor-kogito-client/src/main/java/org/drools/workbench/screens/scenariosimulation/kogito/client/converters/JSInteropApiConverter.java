/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.converters;

import org.drools.scenariosimulation.api.model.AbstractScesimData;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingType;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIBackgroundDatasType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIBackgroundType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIExpressionElementsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIExpressionIdentifierType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactIdentifierType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactMappingValuesType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIGenericTypes;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIImportType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIImportsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenarioSimulationModelType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenariosType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScesimModelDescriptorType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSISettingsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSISimulationType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIWrappedImportsType;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;

/**
 * Class used to convert from <b>JSInterop</b> bean to <b>api</b> one
 */
public class JSInteropApiConverter {

    private JSInteropApiConverter() {
        // Not instantiable
    }

    public static ScenarioSimulationModel getScenarioSimulationModel(JSIScenarioSimulationModelType source) {
        ScenarioSimulationModel toReturn = new ScenarioSimulationModel();
        toReturn.setSettings(getSettings(source.getSettings()));
        toReturn.setImports(getImports(source.getImports()));
        toReturn.setSimulation(getSimulation(source.getSimulation()));
        toReturn.setBackground(getBackground(source.getBackground()));
        return toReturn;
    }

    protected static Settings getSettings(JSISettingsType source) {
        Settings toReturn = new Settings();
        if (source != null) {
            toReturn.setDmoSession(source.getDmoSession());
            toReturn.setType(ScenarioSimulationModel.Type.valueOf(source.getType()));
            toReturn.setFileName(source.getFileName());
            toReturn.setKieSession(source.getKieSession());
            toReturn.setKieBase(source.getKieBase());
            toReturn.setRuleFlowGroup(source.getRuleFlowGroup());
            toReturn.setDmnFilePath(source.getDmnFilePath());
            toReturn.setDmnNamespace(source.getDmnNamespace());
            toReturn.setDmnName(source.getDmnName());
            toReturn.setSkipFromBuild(source.getSkipFromBuild());
            toReturn.setStateless(source.getStateless());
        }
        return toReturn;
    }

    protected static Imports getImports(JSIImportsType source) {
        Imports toReturn = new Imports();
        if (source != null && source.getImports() != null) {
            final JSIWrappedImportsType imports = source.getImports();
            imports.getImport().forEach(importsType -> toReturn.addImport(getImport(importsType)));
        }
        return toReturn;
    }

    protected static Import getImport(JSIImportType source) {
        Import toReturn = new Import();
        toReturn.setType(source.getType());
        return toReturn;
    }

    protected static Simulation getSimulation(JSISimulationType source) {
        Simulation toReturn = new Simulation();
        populateScesimModelDescriptor(toReturn.getScesimModelDescriptor(), source.getScesimModelDescriptor());
        final JSIScenariosType jsiScenariosType = source.getScesimData();
        jsiScenariosType.getScenario().forEach(jsiScenarioType -> {
            Scenario added = toReturn.addData();
            populateAbstractScesimData(added, jsiScenarioType.getFactMappingValues());
        });
        return toReturn;
    }

    protected static Background getBackground(JSIBackgroundType source) {
        Background toReturn = new Background();
        populateScesimModelDescriptor(toReturn.getScesimModelDescriptor(), source.getScesimModelDescriptor());
        final JSIBackgroundDatasType jsiBackgroundDatasType = source.getScesimData();
        jsiBackgroundDatasType.getBackgroundData().forEach(jsiScenarioType -> {
            BackgroundData added = toReturn.addData();
            populateAbstractScesimData(added, jsiScenarioType.getFactMappingValues());
        });
        return toReturn;
    }

    protected static void populateAbstractScesimData(AbstractScesimData toPopulate, JSIFactMappingValuesType source) {
        source.getFactMappingValue().forEach(jsiFactMappingValueType -> {
            JSIFactIdentifierType factIdentifierType = jsiFactMappingValueType.getFactIdentifier();
            final JSIExpressionIdentifierType expressionIdentifierType = jsiFactMappingValueType.getExpressionIdentifier();
            if (factIdentifierType != null && expressionIdentifierType != null) {
                String value = jsiFactMappingValueType.getRawValue() != null ? jsiFactMappingValueType.getRawValue().getValue() : null;
                toPopulate.addMappingValue(getFactIdentifier(factIdentifierType), getExpressionIdentifier(expressionIdentifierType), value);
            }
        });
    }

    protected static void populateScesimModelDescriptor(ScesimModelDescriptor toPopulate, JSIScesimModelDescriptorType source) {
        source.getFactMappings().getFactMapping().forEach(jsiFactMappingType -> {
            final FactMapping added = toPopulate.addFactMapping(getFactIdentifier(jsiFactMappingType.getFactIdentifier()), getExpressionIdentifier(jsiFactMappingType.getExpressionIdentifier()));
            added.setFactAlias(jsiFactMappingType.getFactAlias());
            added.setExpressionAlias(jsiFactMappingType.getExpressionAlias());
            final JSIGenericTypes genericTypes = jsiFactMappingType.getGenericTypes();
            if (genericTypes != null && genericTypes.getString() != null) {
                added.setGenericTypes(genericTypes.getString());
            }
            final JSIExpressionElementsType expressionElements = jsiFactMappingType.getExpressionElements();
            if (expressionElements != null) {
                expressionElements.getExpressionElement().forEach(jsiExpressionElementType -> added.addExpressionElement(jsiExpressionElementType.getStep(), jsiFactMappingType.getClassName()));
            }
        });
    }

    protected static ExpressionIdentifier getExpressionIdentifier(JSIExpressionIdentifierType source) {
        return new ExpressionIdentifier(source.getName(), FactMappingType.valueOf(source.getType()));
    }

    protected static FactIdentifier getFactIdentifier(JSIFactIdentifierType source) {
        return new FactIdentifier(source.getName(), source.getClassName());
    }
}
