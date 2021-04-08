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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jsinterop.base.Js;
import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.BackgroundData;
import org.drools.scenariosimulation.api.model.ExpressionElement;
import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMapping;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.api.model.Scenario;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIBackgroundDataType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIBackgroundDatasType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIBackgroundType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIExpressionElementType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIExpressionElementsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIExpressionIdentifierType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactIdentifierType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactMappingType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactMappingValueType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactMappingValuesType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIFactMappingsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIGenericTypes;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIImportType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIImportsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIRawValueType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenarioSimulationModelType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenarioType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScenariosType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIScesimModelDescriptorType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSISettingsType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSISimulationType;
import org.drools.workbench.scenariosimulation.kogito.marshaller.js.model.JSIWrappedImportsType;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.soup.project.datamodel.imports.Imports;

/**
 * Class used to convert from <b>api</b> bean to <b>JSInterop</b> one
 */
public class ApiJSInteropConverter {

    private ApiJSInteropConverter() {
        // Not instantiable
    }

    public static JSIScenarioSimulationModelType getJSIScenarioSimulationModelType(ScenarioSimulationModel source) {
        JSIScenarioSimulationModelType toReturn = new JSIScenarioSimulationModelType();
        toReturn.setVersion(source.getVersion());
        toReturn.setSettings(getSettings(source.getSettings()));
        toReturn.setImports(getImports(source.getImports()));
        toReturn.setSimulation(getSimulation(source.getSimulation()));
        toReturn.setBackground(getBackground(source.getBackground()));
        return toReturn;
    }

    protected static JSISettingsType getSettings(Settings source) {
        JSISettingsType toReturn = new JSISettingsType();
        toReturn.setDmoSession(source.getDmoSession());
        toReturn.setType(source.getType().name());
        toReturn.setFileName(source.getFileName());
        toReturn.setKieSession(source.getKieSession());
        toReturn.setKieBase(source.getKieBase());
        toReturn.setRuleFlowGroup(source.getRuleFlowGroup());
        toReturn.setDmnFilePath(source.getDmnFilePath());
        toReturn.setDmnNamespace(source.getDmnNamespace());
        toReturn.setDmnName(source.getDmnName());
        toReturn.setSkipFromBuild(source.isSkipFromBuild());
        toReturn.setStateless(source.isStateless());
        return toReturn;
    }

    protected static JSIImportsType getImports(Imports source) {
        JSIImportsType toReturn = new JSIImportsType();
        final List<Import> imports = source.getImports();
        JSIWrappedImportsType jsiWrappedImportsType = new JSIWrappedImportsType();
        toReturn.setImports(jsiWrappedImportsType);
        if (imports != null) {
            List<JSIImportType> toSet = imports.stream().map(ApiJSInteropConverter::getImport).collect(Collectors.toList());
            jsiWrappedImportsType.setImport(toSet);
        }
        return toReturn;
    }

    protected static JSIImportType getImport(Import source) {
        JSIImportType toReturn = new JSIImportType();
        toReturn.setType(source.getType());
        return toReturn;
    }

    protected static JSISimulationType getSimulation(Simulation source) {
        JSISimulationType toReturn = new JSISimulationType();
        JSIScesimModelDescriptorType jsiScesimModelDescriptorType = getScesimModelDescriptor(source.getScesimModelDescriptor());
        toReturn.setScesimModelDescriptor(jsiScesimModelDescriptorType);
        final List<Scenario> unmodifiableScenarios = source.getUnmodifiableData();
        JSIScenariosType jsiScenariosType = new JSIScenariosType();
        toReturn.setScesimData(jsiScenariosType);
        List<JSIScenarioType> toSet = unmodifiableScenarios.stream()
                .map(ApiJSInteropConverter::getScenario).collect(Collectors.toList());
        jsiScenariosType.setScenario(toSet);
        return toReturn;
    }

    protected static JSIScenarioType getScenario(Scenario source) {
        JSIScenarioType toReturn = new JSIScenarioType();
        JSIFactMappingValuesType factMappingValuesType = new JSIFactMappingValuesType();
        toReturn.setFactMappingValues(factMappingValuesType);
        final List<FactMappingValue> unmodifiableFactMappingValues = source.getUnmodifiableFactMappingValues();
        List<JSIFactMappingValueType> toSet = populateJSIFactMappingValuesType(unmodifiableFactMappingValues);
        factMappingValuesType.setFactMappingValue(toSet);
        return toReturn;
    }

    protected static JSIBackgroundType getBackground(Background source) {
        JSIBackgroundType toReturn = new JSIBackgroundType();
        JSIScesimModelDescriptorType jsiScesimModelDescriptorType = getScesimModelDescriptor(source.getScesimModelDescriptor());
        toReturn.setScesimModelDescriptor(jsiScesimModelDescriptorType);
        final List<BackgroundData> unmodifiableBackgroundDatas = source.getUnmodifiableData();
        JSIBackgroundDatasType jsiBackgroundDatasType = new JSIBackgroundDatasType();
        toReturn.setScesimData(jsiBackgroundDatasType);
        List<JSIBackgroundDataType> toSet = unmodifiableBackgroundDatas.stream()
                .map(ApiJSInteropConverter::getBackgroundData).collect(Collectors.toList());
        jsiBackgroundDatasType.setBackgroundData(toSet);
        return toReturn;
    }

    protected static JSIBackgroundDataType getBackgroundData(BackgroundData source) {
        JSIBackgroundDataType toReturn = new JSIBackgroundDataType();
        JSIFactMappingValuesType factMappingValuesType = new JSIFactMappingValuesType();
        toReturn.setFactMappingValues(factMappingValuesType);
        final List<FactMappingValue> unmodifiableFactMappingValues = source.getUnmodifiableFactMappingValues();
        List<JSIFactMappingValueType> toSet = populateJSIFactMappingValuesType(unmodifiableFactMappingValues);
        factMappingValuesType.setFactMappingValue(toSet);
        return toReturn;
    }

    protected static List<JSIFactMappingValueType> populateJSIFactMappingValuesType(List<FactMappingValue> source) {
        List<JSIFactMappingValueType> toReturn = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            FactMappingValue factMappingValue = source.get(i);
            toReturn.add(Js.uncheckedCast(getFactMappingValue(factMappingValue)));
        }
        return toReturn;
    }

    protected static JSIScesimModelDescriptorType getScesimModelDescriptor(ScesimModelDescriptor source) {
        JSIScesimModelDescriptorType toReturn = new JSIScesimModelDescriptorType();
        final List<FactMapping> factMappings = source.getFactMappings();
        JSIFactMappingsType jsiFactMappingsType = new JSIFactMappingsType();
        toReturn.setFactMappings(jsiFactMappingsType);
        List<JSIFactMappingType> toSet = factMappings.stream()
                .map(ApiJSInteropConverter::getFactMapping).collect(Collectors.toList());
        jsiFactMappingsType.setFactMapping(toSet);
        return toReturn;
    }

    protected static JSIFactMappingValueType getFactMappingValue(FactMappingValue source) {
        JSIFactMappingValueType toReturn = new JSIFactMappingValueType();
        final JSIExpressionIdentifierType expressionIdentifierType = Js.uncheckedCast(getExpressionIdentifier(source.getExpressionIdentifier()));
        toReturn.setExpressionIdentifier(expressionIdentifierType);
        final JSIFactIdentifierType jsiFactIdentifierReferenceType = Js.uncheckedCast(getFactIdentifier(source.getFactIdentifier()));
        toReturn.setFactIdentifier(jsiFactIdentifierReferenceType);
        Object rawValue = source.getRawValue();
        if (rawValue != null) {
            JSIRawValueType jsiRawValueType = Js.uncheckedCast(getRawValueReference(rawValue));
            toReturn.setRawValue(jsiRawValueType);
        }
        return toReturn;
    }

    protected static JSIRawValueType getRawValueReference(Object rawValue) {
        JSIRawValueType toReturn = new JSIRawValueType();
        toReturn.setClazz("string");
        toReturn.setValue(rawValue.toString());
        return toReturn;
    }

    protected static JSIFactMappingType getFactMapping(FactMapping source) {
        JSIFactMappingType toReturn = new JSIFactMappingType();
        toReturn.setClassName(source.getClassName());
        toReturn.setExpressionAlias(source.getExpressionAlias());
        JSIExpressionElementsType jsiExpressionElementsType = Js.uncheckedCast(getExpressionElements(source.getExpressionElements()));
        toReturn.setExpressionElements(jsiExpressionElementsType);
        JSIExpressionIdentifierType jsiExpressionIdentifierType = Js.uncheckedCast(getExpressionIdentifier(source.getExpressionIdentifier()));
        toReturn.setExpressionIdentifier(jsiExpressionIdentifierType);
        toReturn.setFactAlias(source.getFactAlias());
        JSIFactIdentifierType jsiFactIdentifierType = Js.uncheckedCast(getFactIdentifier(source.getFactIdentifier()));
        toReturn.setFactIdentifier(jsiFactIdentifierType);
        toReturn.setFactMappingValueType(source.getFactMappingValueType().toString());
        toReturn.setColumnWidth(source.getColumnWidth());
        List<String> genericTypes = source.getGenericTypes();
        if (genericTypes != null) {
            JSIGenericTypes toSet = new JSIGenericTypes();
            toSet.setString(genericTypes);
            toReturn.setGenericTypes(toSet);
        }
        return toReturn;
    }

    protected static JSIExpressionElementsType getExpressionElements(List<ExpressionElement> expressionElements) {
        JSIExpressionElementsType toReturn = new JSIExpressionElementsType();
        List<JSIExpressionElementType> toSet = expressionElements.stream()
                .map(ApiJSInteropConverter::getExpressionElement).collect(Collectors.toList());
        toReturn.setExpressionElement(toSet);
        return toReturn;
    }

    protected static JSIExpressionElementType getExpressionElement(ExpressionElement expressionElement) {
        JSIExpressionElementType toReturn = new JSIExpressionElementType();
        toReturn.setStep(expressionElement.getStep());
        return toReturn;
    }

    protected static JSIExpressionIdentifierType getExpressionIdentifier(ExpressionIdentifier source) {
        JSIExpressionIdentifierType toReturn = new JSIExpressionIdentifierType();
        toReturn.setName(source.getName());
        toReturn.setType(source.getType().name());
        return toReturn;
    }

    protected static JSIFactIdentifierType getFactIdentifier(FactIdentifier factIdentifier) {
        JSIFactIdentifierType toReturn = new JSIFactIdentifierType();
        toReturn.setName(factIdentifier.getName());
        toReturn.setClassName(factIdentifier.getClassName());
        toReturn.setImportPrefix(factIdentifier.getImportPrefix());
        return toReturn;
    }
}
