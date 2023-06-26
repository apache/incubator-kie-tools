import { Meta } from "@kie-tools/xml-parser-ts";

export const root = {
  element: "SceSim.xsd__ScenarioSimulationModel",
  type: "SceSim__ScenarioSimulationModelType",
};

export const ns = new Map<string, string>([
  ["undefined", ""],
  ["", "undefined"],
]);

export const meta: Meta = {
  SceSim__ExpressionElementType: {
    step: { type: "string", isArray: false, isOptional: false },
  },
  SceSim__expressionElementsType: {
    ExpressionElement: { type: "SceSim__ExpressionElementType", isArray: true, isOptional: false },
  },
  SceSim__expressionIdentifierType: {
    name: { type: "string", isArray: false, isOptional: true },
    type: { type: "string", isArray: false, isOptional: true },
  },
  SceSim__factIdentifierType: {
    "@_importPrefix": { type: "string", isArray: false, isOptional: true },
    name: { type: "string", isArray: false, isOptional: true },
    className: { type: "string", isArray: false, isOptional: true },
  },
  SceSim__genericTypes: {
    string: { type: "string", isArray: true, isOptional: true },
  },
  SceSim__FactMappingType: {
    expressionElements: { type: "SceSim__expressionElementsType", isArray: false, isOptional: false },
    expressionIdentifier: { type: "SceSim__expressionIdentifierType", isArray: false, isOptional: false },
    factIdentifier: { type: "SceSim__factIdentifierType", isArray: false, isOptional: false },
    className: { type: "string", isArray: false, isOptional: false },
    factAlias: { type: "string", isArray: false, isOptional: false },
    expressionAlias: { type: "string", isArray: false, isOptional: true },
    genericTypes: { type: "SceSim__genericTypes", isArray: false, isOptional: false },
    columnWidth: { type: "float", isArray: false, isOptional: false },
    factMappingValueType: { type: "string", isArray: false, isOptional: true },
  },
  SceSim__factMappingsType: {
    FactMapping: { type: "SceSim__FactMappingType", isArray: true, isOptional: true },
  },
  SceSim__scesimModelDescriptorType: {
    factMappings: { type: "SceSim__factMappingsType", isArray: false, isOptional: true },
  },
  SceSim__settingsType: {
    dmoSession: { type: "string", isArray: false, isOptional: true },
    dmnFilePath: { type: "string", isArray: false, isOptional: true },
    type: { type: "string", isArray: false, isOptional: true },
    fileName: { type: "string", isArray: false, isOptional: true },
    kieSession: { type: "string", isArray: false, isOptional: true },
    kieBase: { type: "string", isArray: false, isOptional: true },
    ruleFlowGroup: { type: "string", isArray: false, isOptional: true },
    dmnNamespace: { type: "string", isArray: false, isOptional: true },
    dmnName: { type: "string", isArray: false, isOptional: true },
    skipFromBuild: { type: "boolean", isArray: false, isOptional: true },
    stateless: { type: "boolean", isArray: false, isOptional: true },
  },
  SceSim__rawValueType: {},
  SceSim__FactMappingValueType: {
    factIdentifier: { type: "SceSim__factIdentifierType", isArray: false, isOptional: false },
    expressionIdentifier: { type: "SceSim__expressionIdentifierType", isArray: false, isOptional: false },
    rawValue: { type: "SceSim__rawValueType", isArray: false, isOptional: true },
  },
  SceSim__factMappingValuesType: {
    FactMappingValue: { type: "SceSim__FactMappingValueType", isArray: true, isOptional: true },
  },
  SceSim__ScenarioType: {
    factMappingValues: { type: "SceSim__factMappingValuesType", isArray: false, isOptional: false },
  },
  SceSim__BackgroundDataType: {
    factMappingValues: { type: "SceSim__factMappingValuesType", isArray: false, isOptional: false },
  },
  SceSim__scenariosType: {
    Scenario: { type: "SceSim__ScenarioType", isArray: true, isOptional: true },
  },
  SceSim__backgroundDatasType: {
    BackgroundData: { type: "SceSim__BackgroundDataType", isArray: true, isOptional: true },
  },
  SceSim__simulationType: {
    scesimModelDescriptor: { type: "SceSim__scesimModelDescriptorType", isArray: false, isOptional: false },
    scesimData: { type: "SceSim__scenariosType", isArray: false, isOptional: false },
  },
  SceSim__backgroundType: {
    scesimModelDescriptor: { type: "SceSim__scesimModelDescriptorType", isArray: false, isOptional: false },
    scesimData: { type: "SceSim__backgroundDatasType", isArray: false, isOptional: false },
  },
  SceSim__ImportType: {
    type: { type: "string", isArray: false, isOptional: false },
  },
  SceSim__wrappedImportsType: {
    Import: { type: "SceSim__ImportType", isArray: true, isOptional: true },
  },
  SceSim__importsType: {
    imports: { type: "SceSim__wrappedImportsType", isArray: false, isOptional: true },
  },
  SceSim__ScenarioSimulationModelType: {
    "@_version": { type: "string", isArray: false, isOptional: true },
    simulation: { type: "SceSim__simulationType", isArray: false, isOptional: false },
    background: { type: "SceSim__backgroundType", isArray: false, isOptional: false },
    settings: { type: "SceSim__settingsType", isArray: false, isOptional: false },
    imports: { type: "SceSim__importsType", isArray: false, isOptional: false },
  },
};
