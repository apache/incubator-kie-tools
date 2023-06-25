// This file was automatically generated

export type SceSim__ExpressionElementType = {
  step: string; // from type SceSim__ExpressionElementType @ SceSim.xsd
};

export type SceSim__expressionElementsType = {
  ExpressionElement: SceSim__ExpressionElementType[]; // from type SceSim__expressionElementsType @ SceSim.xsd
};

export type SceSim__expressionIdentifierType = {
  name?: string; // from type SceSim__expressionIdentifierType @ SceSim.xsd
  type?: string; // from type SceSim__expressionIdentifierType @ SceSim.xsd
};

export type SceSim__factIdentifierType = {
  "@_importPrefix"?: string; // from type SceSim__factIdentifierType @ SceSim.xsd
  name?: string; // from type SceSim__factIdentifierType @ SceSim.xsd
  className?: string; // from type SceSim__factIdentifierType @ SceSim.xsd
};

export type SceSim__genericTypes = {
  string?: string[]; // from type SceSim__genericTypes @ SceSim.xsd
};

export type SceSim__FactMappingType = {
  expressionElements: SceSim__expressionElementsType; // from type SceSim__FactMappingType @ SceSim.xsd
  expressionIdentifier: SceSim__expressionIdentifierType; // from type SceSim__FactMappingType @ SceSim.xsd
  factIdentifier: SceSim__factIdentifierType; // from type SceSim__FactMappingType @ SceSim.xsd
  className: string; // from type SceSim__FactMappingType @ SceSim.xsd
  factAlias: string; // from type SceSim__FactMappingType @ SceSim.xsd
  expressionAlias?: string; // from type SceSim__FactMappingType @ SceSim.xsd
  genericTypes: SceSim__genericTypes; // from type SceSim__FactMappingType @ SceSim.xsd
  columnWidth: number; // from type SceSim__FactMappingType @ SceSim.xsd
  factMappingValueType?: string; // from type SceSim__FactMappingType @ SceSim.xsd
};

export type SceSim__factMappingsType = {
  FactMapping?: SceSim__FactMappingType[]; // from type SceSim__factMappingsType @ SceSim.xsd
};

export type SceSim__scesimModelDescriptorType = {
  factMappings?: SceSim__factMappingsType; // from type SceSim__scesimModelDescriptorType @ SceSim.xsd
};

export type SceSim__settingsType = {
  dmoSession?: string; // from type SceSim__settingsType @ SceSim.xsd
  dmnFilePath?: string; // from type SceSim__settingsType @ SceSim.xsd
  type?: string; // from type SceSim__settingsType @ SceSim.xsd
  fileName?: string; // from type SceSim__settingsType @ SceSim.xsd
  kieSession?: string; // from type SceSim__settingsType @ SceSim.xsd
  kieBase?: string; // from type SceSim__settingsType @ SceSim.xsd
  ruleFlowGroup?: string; // from type SceSim__settingsType @ SceSim.xsd
  dmnNamespace?: string; // from type SceSim__settingsType @ SceSim.xsd
  dmnName?: string; // from type SceSim__settingsType @ SceSim.xsd
  skipFromBuild?: boolean; // from type SceSim__settingsType @ SceSim.xsd
  stateless?: boolean; // from type SceSim__settingsType @ SceSim.xsd
};

export type SceSim__rawValueType = {};

export type SceSim__FactMappingValueType = {
  factIdentifier: SceSim__factIdentifierType; // from type SceSim__FactMappingValueType @ SceSim.xsd
  expressionIdentifier: SceSim__expressionIdentifierType; // from type SceSim__FactMappingValueType @ SceSim.xsd
  rawValue?: SceSim__rawValueType; // from type SceSim__FactMappingValueType @ SceSim.xsd
};

export type SceSim__factMappingValuesType = {
  FactMappingValue?: SceSim__FactMappingValueType[]; // from type SceSim__factMappingValuesType @ SceSim.xsd
};

export type SceSim__ScenarioType = {
  factMappingValues: SceSim__factMappingValuesType; // from type SceSim__ScenarioType @ SceSim.xsd
};

export type SceSim__BackgroundDataType = {
  factMappingValues: SceSim__factMappingValuesType; // from type SceSim__BackgroundDataType @ SceSim.xsd
};

export type SceSim__scenariosType = {
  Scenario?: SceSim__ScenarioType[]; // from type SceSim__scenariosType @ SceSim.xsd
};

export type SceSim__backgroundDatasType = {
  BackgroundData?: SceSim__BackgroundDataType[]; // from type SceSim__backgroundDatasType @ SceSim.xsd
};

export type SceSim__simulationType = {
  scesimModelDescriptor: SceSim__scesimModelDescriptorType; // from type SceSim__simulationType @ SceSim.xsd
  scesimData: SceSim__scenariosType; // from type SceSim__simulationType @ SceSim.xsd
};

export type SceSim__backgroundType = {
  scesimModelDescriptor: SceSim__scesimModelDescriptorType; // from type SceSim__backgroundType @ SceSim.xsd
  scesimData: SceSim__backgroundDatasType; // from type SceSim__backgroundType @ SceSim.xsd
};

export type SceSim__ImportType = {
  type: string; // from type SceSim__ImportType @ SceSim.xsd
};

export type SceSim__wrappedImportsType = {
  Import?: SceSim__ImportType[]; // from type SceSim__wrappedImportsType @ SceSim.xsd
};

export type SceSim__importsType = {
  imports?: SceSim__wrappedImportsType; // from type SceSim__importsType @ SceSim.xsd
};

export type SceSim__ScenarioSimulationModelType = Partial<{ [k: `@_xmlns:${string}`]: string }> & {
  "@_xmlns"?: string;
} & {
  "@_version"?: string; // from type SceSim__ScenarioSimulationModelType @ SceSim.xsd
  simulation: SceSim__simulationType; // from type SceSim__ScenarioSimulationModelType @ SceSim.xsd
  background: SceSim__backgroundType; // from type SceSim__ScenarioSimulationModelType @ SceSim.xsd
  settings: SceSim__settingsType; // from type SceSim__ScenarioSimulationModelType @ SceSim.xsd
  imports: SceSim__importsType; // from type SceSim__ScenarioSimulationModelType @ SceSim.xsd
};
