// This file was automatically generated

import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";

export type DMN14__tHitPolicy = "UNIQUE" | "FIRST" | "PRIORITY" | "ANY" | "COLLECT" | "RULE ORDER" | "OUTPUT ORDER";

export type DMN14__tBuiltinAggregator = "SUM" | "COUNT" | "MIN" | "MAX";

export type DMN14__tDecisionTableOrientation = "Rule-as-Row" | "Rule-as-Column" | "CrossTable";

export type DMN14__tAssociationDirection = "None" | "One" | "Both";

export type DMN14__tFunctionKind = "FEEL" | "Java" | "PMML";

export type DC__AlignmentKind = "start" | "end" | "center";

export type DC__KnownColor =
  | "maroon"
  | "red"
  | "orange"
  | "yellow"
  | "olive"
  | "purple"
  | "fuchsia"
  | "white"
  | "lime"
  | "green"
  | "navy"
  | "blue"
  | "aqua"
  | "teal"
  | "black"
  | "silver"
  | "gray";

export interface DMN14__tDMNElement {
  "@_id"?: string; // from type DMN14__tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type DMN14__tDMNElement @ DMN14.xsd
  description?: string; // from type DMN14__tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDMNElement__extensionElements; // from type DMN14__tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDMNElement__extensionElements {}

export interface DMN14__tNamedElement {
  "@_name": string; // from type DMN14__tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tNamedElement__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tNamedElement__extensionElements {}

export type DMN14__tDMNElementReference = {
  "@_href": string; // from type DMN14__tDMNElementReference @ DMN14.xsd
};

export interface DMN14__tDefinitions extends XmlParserTsRootElementBaseType {
  "@_expressionLanguage"?: string; // from type DMN14__tDefinitions @ DMN14.xsd
  "@_typeLanguage"?: string; // from type DMN14__tDefinitions @ DMN14.xsd
  "@_namespace": string; // from type DMN14__tDefinitions @ DMN14.xsd
  "@_exporter"?: string; // from type DMN14__tDefinitions @ DMN14.xsd
  "@_exporterVersion"?: string; // from type DMN14__tDefinitions @ DMN14.xsd
  import?: DMN14__tImport[]; // from type DMN14__tDefinitions @ DMN14.xsd
  itemDefinition?: DMN14__tItemDefinition[]; // from type DMN14__tDefinitions @ DMN14.xsd
  decision?: DMN14__tDecision[]; // from type DMN14__tDefinitions @ DMN14.xsd
  businessKnowledgeModel?: DMN14__tBusinessKnowledgeModel[]; // from type DMN14__tDefinitions @ DMN14.xsd
  decisionService?: DMN14__tDecisionService[]; // from type DMN14__tDefinitions @ DMN14.xsd
  inputData?: DMN14__tInputData[]; // from type DMN14__tDefinitions @ DMN14.xsd
  knowledgeSource?: DMN14__tKnowledgeSource[]; // from type DMN14__tDefinitions @ DMN14.xsd
  group?: DMN14__tGroup[]; // from type DMN14__tDefinitions @ DMN14.xsd
  textAnnotation?: DMN14__tTextAnnotation[]; // from type DMN14__tDefinitions @ DMN14.xsd
  association?: DMN14__tAssociation[]; // from type DMN14__tDefinitions @ DMN14.xsd
  elementCollection?: DMN14__tElementCollection[]; // from type DMN14__tDefinitions @ DMN14.xsd
  performanceIndicator?: DMN14__tPerformanceIndicator[]; // from type DMN14__tDefinitions @ DMN14.xsd
  organizationUnit?: DMN14__tOrganizationUnit[]; // from type DMN14__tDefinitions @ DMN14.xsd
  "dmndi:DMNDI"?: DMNDI13__DMNDI; // from type DMN14__tDefinitions @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDefinitions__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDefinitions__extensionElements {}

export interface DMN14__tImport {
  "@_namespace": string; // from type DMN14__tImport @ DMN14.xsd
  "@_locationURI"?: string; // from type DMN14__tImport @ DMN14.xsd
  "@_importType": string; // from type DMN14__tImport @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tImport__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tImport__extensionElements {}

export interface DMN14__tElementCollection {
  drgElement?: DMN14__tDMNElementReference[]; // from type DMN14__tElementCollection @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tElementCollection__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tElementCollection__extensionElements {}

export interface DMN14__tDRGElement {
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDRGElement__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDRGElement__extensionElements {}

export interface DMN14__tDecision {
  question?: string; // from type DMN14__tDecision @ DMN14.xsd
  allowedAnswers?: string; // from type DMN14__tDecision @ DMN14.xsd
  variable?: DMN14__tInformationItem; // from type DMN14__tDecision @ DMN14.xsd
  informationRequirement?: DMN14__tInformationRequirement[]; // from type DMN14__tDecision @ DMN14.xsd
  knowledgeRequirement?: DMN14__tKnowledgeRequirement[]; // from type DMN14__tDecision @ DMN14.xsd
  authorityRequirement?: DMN14__tAuthorityRequirement[]; // from type DMN14__tDecision @ DMN14.xsd
  supportedObjective?: DMN14__tDMNElementReference[]; // from type DMN14__tDecision @ DMN14.xsd
  impactedPerformanceIndicator?: DMN14__tDMNElementReference[]; // from type DMN14__tDecision @ DMN14.xsd
  decisionMaker?: DMN14__tDMNElementReference[]; // from type DMN14__tDecision @ DMN14.xsd
  decisionOwner?: DMN14__tDMNElementReference[]; // from type DMN14__tDecision @ DMN14.xsd
  usingProcess?: DMN14__tDMNElementReference[]; // from type DMN14__tDecision @ DMN14.xsd
  usingTask?: DMN14__tDMNElementReference[]; // from type DMN14__tDecision @ DMN14.xsd
  literalExpression?: DMN14__tLiteralExpression; // from type DMN14__tDecision @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type DMN14__tDecision @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type DMN14__tDecision @ DMN14.xsd
  context?: DMN14__tContext; // from type DMN14__tDecision @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type DMN14__tDecision @ DMN14.xsd
  relation?: DMN14__tRelation; // from type DMN14__tDecision @ DMN14.xsd
  list?: DMN14__tList; // from type DMN14__tDecision @ DMN14.xsd
  for?: DMN14__tFor; // from type DMN14__tDecision @ DMN14.xsd
  every?: DMN14__tQuantified; // from type DMN14__tDecision @ DMN14.xsd
  some?: DMN14__tQuantified; // from type DMN14__tDecision @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type DMN14__tDecision @ DMN14.xsd
  filter?: DMN14__tFilter; // from type DMN14__tDecision @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDecision__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDecision__extensionElements {}

export interface DMN14__tBusinessContextElement {
  "@_URI"?: string; // from type DMN14__tBusinessContextElement @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tBusinessContextElement__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tBusinessContextElement__extensionElements {}

export interface DMN14__tPerformanceIndicator {
  impactingDecision?: DMN14__tDMNElementReference[]; // from type DMN14__tPerformanceIndicator @ DMN14.xsd
  "@_URI"?: string; // from type tBusinessContextElement @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tPerformanceIndicator__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tPerformanceIndicator__extensionElements {}

export interface DMN14__tOrganizationUnit {
  decisionMade?: DMN14__tDMNElementReference[]; // from type DMN14__tOrganizationUnit @ DMN14.xsd
  decisionOwned?: DMN14__tDMNElementReference[]; // from type DMN14__tOrganizationUnit @ DMN14.xsd
  "@_URI"?: string; // from type tBusinessContextElement @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tOrganizationUnit__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tOrganizationUnit__extensionElements {}

export interface DMN14__tInvocable {
  variable?: DMN14__tInformationItem; // from type DMN14__tInvocable @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tInvocable__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tInvocable__extensionElements {}

export interface DMN14__tBusinessKnowledgeModel {
  encapsulatedLogic?: DMN14__tFunctionDefinition; // from type DMN14__tBusinessKnowledgeModel @ DMN14.xsd
  knowledgeRequirement?: DMN14__tKnowledgeRequirement[]; // from type DMN14__tBusinessKnowledgeModel @ DMN14.xsd
  authorityRequirement?: DMN14__tAuthorityRequirement[]; // from type DMN14__tBusinessKnowledgeModel @ DMN14.xsd
  variable?: DMN14__tInformationItem; // from type tInvocable @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tBusinessKnowledgeModel__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tBusinessKnowledgeModel__extensionElements {}

export interface DMN14__tInputData {
  variable?: DMN14__tInformationItem; // from type DMN14__tInputData @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tInputData__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tInputData__extensionElements {}

export interface DMN14__tKnowledgeSource {
  "@_locationURI"?: string; // from type DMN14__tKnowledgeSource @ DMN14.xsd
  authorityRequirement?: DMN14__tAuthorityRequirement[]; // from type DMN14__tKnowledgeSource @ DMN14.xsd
  type?: string; // from type DMN14__tKnowledgeSource @ DMN14.xsd
  owner?: DMN14__tDMNElementReference; // from type DMN14__tKnowledgeSource @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tKnowledgeSource__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tKnowledgeSource__extensionElements {}

export interface DMN14__tInformationRequirement {
  requiredDecision?: DMN14__tDMNElementReference; // from type DMN14__tInformationRequirement @ DMN14.xsd
  requiredInput?: DMN14__tDMNElementReference; // from type DMN14__tInformationRequirement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tInformationRequirement__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tInformationRequirement__extensionElements {}

export interface DMN14__tKnowledgeRequirement {
  requiredKnowledge: DMN14__tDMNElementReference; // from type DMN14__tKnowledgeRequirement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tKnowledgeRequirement__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tKnowledgeRequirement__extensionElements {}

export interface DMN14__tAuthorityRequirement {
  requiredDecision?: DMN14__tDMNElementReference; // from type DMN14__tAuthorityRequirement @ DMN14.xsd
  requiredInput?: DMN14__tDMNElementReference; // from type DMN14__tAuthorityRequirement @ DMN14.xsd
  requiredAuthority?: DMN14__tDMNElementReference; // from type DMN14__tAuthorityRequirement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tAuthorityRequirement__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tAuthorityRequirement__extensionElements {}

export interface DMN14__tExpression {
  "@_typeRef"?: string; // from type DMN14__tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tExpression__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tExpression__extensionElements {}

export interface DMN14__tItemDefinition {
  "@_typeLanguage"?: string; // from type DMN14__tItemDefinition @ DMN14.xsd
  "@_isCollection"?: boolean; // from type DMN14__tItemDefinition @ DMN14.xsd
  itemComponent?: DMN14__tItemDefinition[]; // from type DMN14__tItemDefinition @ DMN14.xsd
  functionItem?: DMN14__tFunctionItem; // from type DMN14__tItemDefinition @ DMN14.xsd
  typeRef?: string; // from type DMN14__tItemDefinition @ DMN14.xsd
  allowedValues?: DMN14__tUnaryTests; // from type DMN14__tItemDefinition @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tItemDefinition__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tItemDefinition__extensionElements {}

export interface DMN14__tFunctionItem {
  "@_outputTypeRef"?: string; // from type DMN14__tFunctionItem @ DMN14.xsd
  parameters?: DMN14__tInformationItem[]; // from type DMN14__tFunctionItem @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tFunctionItem__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tFunctionItem__extensionElements {}

export interface DMN14__tLiteralExpression {
  "@_expressionLanguage"?: string; // from type DMN14__tLiteralExpression @ DMN14.xsd
  text?: string; // from type DMN14__tLiteralExpression @ DMN14.xsd
  importedValues?: DMN14__tImportedValues; // from type DMN14__tLiteralExpression @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tLiteralExpression__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tLiteralExpression__extensionElements {}

export interface DMN14__tInvocation {
  literalExpression?: DMN14__tLiteralExpression; // from type DMN14__tInvocation @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type DMN14__tInvocation @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type DMN14__tInvocation @ DMN14.xsd
  context?: DMN14__tContext; // from type DMN14__tInvocation @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type DMN14__tInvocation @ DMN14.xsd
  relation?: DMN14__tRelation; // from type DMN14__tInvocation @ DMN14.xsd
  list?: DMN14__tList; // from type DMN14__tInvocation @ DMN14.xsd
  for?: DMN14__tFor; // from type DMN14__tInvocation @ DMN14.xsd
  every?: DMN14__tQuantified; // from type DMN14__tInvocation @ DMN14.xsd
  some?: DMN14__tQuantified; // from type DMN14__tInvocation @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type DMN14__tInvocation @ DMN14.xsd
  filter?: DMN14__tFilter; // from type DMN14__tInvocation @ DMN14.xsd
  binding?: DMN14__tBinding[]; // from type DMN14__tInvocation @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tInvocation__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tInvocation__extensionElements {}

export type DMN14__tBinding = {
  parameter: DMN14__tInformationItem; // from type DMN14__tBinding @ DMN14.xsd
  literalExpression?: DMN14__tLiteralExpression; // from type DMN14__tBinding @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type DMN14__tBinding @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type DMN14__tBinding @ DMN14.xsd
  context?: DMN14__tContext; // from type DMN14__tBinding @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type DMN14__tBinding @ DMN14.xsd
  relation?: DMN14__tRelation; // from type DMN14__tBinding @ DMN14.xsd
  list?: DMN14__tList; // from type DMN14__tBinding @ DMN14.xsd
  for?: DMN14__tFor; // from type DMN14__tBinding @ DMN14.xsd
  every?: DMN14__tQuantified; // from type DMN14__tBinding @ DMN14.xsd
  some?: DMN14__tQuantified; // from type DMN14__tBinding @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type DMN14__tBinding @ DMN14.xsd
  filter?: DMN14__tFilter; // from type DMN14__tBinding @ DMN14.xsd
};

export interface DMN14__tInformationItem {
  "@_typeRef"?: string; // from type DMN14__tInformationItem @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tInformationItem__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tInformationItem__extensionElements {}

export interface DMN14__tDecisionTable {
  "@_hitPolicy"?: DMN14__tHitPolicy; // from type DMN14__tDecisionTable @ DMN14.xsd
  "@_aggregation"?: DMN14__tBuiltinAggregator; // from type DMN14__tDecisionTable @ DMN14.xsd
  "@_preferredOrientation"?: DMN14__tDecisionTableOrientation; // from type DMN14__tDecisionTable @ DMN14.xsd
  "@_outputLabel"?: string; // from type DMN14__tDecisionTable @ DMN14.xsd
  input?: DMN14__tInputClause[]; // from type DMN14__tDecisionTable @ DMN14.xsd
  output: DMN14__tOutputClause[]; // from type DMN14__tDecisionTable @ DMN14.xsd
  annotation?: DMN14__tRuleAnnotationClause[]; // from type DMN14__tDecisionTable @ DMN14.xsd
  rule?: DMN14__tDecisionRule[]; // from type DMN14__tDecisionTable @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDecisionTable__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDecisionTable__extensionElements {}

export interface DMN14__tInputClause {
  inputExpression: DMN14__tLiteralExpression; // from type DMN14__tInputClause @ DMN14.xsd
  inputValues?: DMN14__tUnaryTests; // from type DMN14__tInputClause @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tInputClause__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tInputClause__extensionElements {}

export interface DMN14__tOutputClause {
  "@_name"?: string; // from type DMN14__tOutputClause @ DMN14.xsd
  "@_typeRef"?: string; // from type DMN14__tOutputClause @ DMN14.xsd
  outputValues?: DMN14__tUnaryTests; // from type DMN14__tOutputClause @ DMN14.xsd
  defaultOutputEntry?: DMN14__tLiteralExpression; // from type DMN14__tOutputClause @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tOutputClause__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tOutputClause__extensionElements {}

export type DMN14__tRuleAnnotationClause = {
  "@_name"?: string; // from type DMN14__tRuleAnnotationClause @ DMN14.xsd
};

export interface DMN14__tDecisionRule {
  inputEntry?: DMN14__tUnaryTests[]; // from type DMN14__tDecisionRule @ DMN14.xsd
  outputEntry: DMN14__tLiteralExpression[]; // from type DMN14__tDecisionRule @ DMN14.xsd
  annotationEntry?: DMN14__tRuleAnnotation[]; // from type DMN14__tDecisionRule @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDecisionRule__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDecisionRule__extensionElements {}

export type DMN14__tRuleAnnotation = {
  text?: string; // from type DMN14__tRuleAnnotation @ DMN14.xsd
};

export interface DMN14__tImportedValues {
  "@_expressionLanguage"?: string; // from type DMN14__tImportedValues @ DMN14.xsd
  importedElement: string; // from type DMN14__tImportedValues @ DMN14.xsd
  "@_namespace": string; // from type tImport @ DMN14.xsd
  "@_locationURI"?: string; // from type tImport @ DMN14.xsd
  "@_importType": string; // from type tImport @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tImportedValues__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tImportedValues__extensionElements {}

export interface DMN14__tArtifact {
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tArtifact__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tArtifact__extensionElements {}

export interface DMN14__tGroup {
  "@_name"?: string; // from type DMN14__tGroup @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tGroup__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tGroup__extensionElements {}

export interface DMN14__tTextAnnotation {
  "@_textFormat"?: string; // from type DMN14__tTextAnnotation @ DMN14.xsd
  text?: string; // from type DMN14__tTextAnnotation @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tTextAnnotation__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tTextAnnotation__extensionElements {}

export interface DMN14__tAssociation {
  "@_associationDirection"?: DMN14__tAssociationDirection; // from type DMN14__tAssociation @ DMN14.xsd
  sourceRef: DMN14__tDMNElementReference; // from type DMN14__tAssociation @ DMN14.xsd
  targetRef: DMN14__tDMNElementReference; // from type DMN14__tAssociation @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tAssociation__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tAssociation__extensionElements {}

export interface DMN14__tContext {
  contextEntry?: DMN14__tContextEntry[]; // from type DMN14__tContext @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tContext__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tContext__extensionElements {}

export interface DMN14__tContextEntry {
  variable?: DMN14__tInformationItem; // from type DMN14__tContextEntry @ DMN14.xsd
  literalExpression?: DMN14__tLiteralExpression; // from type DMN14__tContextEntry @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type DMN14__tContextEntry @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type DMN14__tContextEntry @ DMN14.xsd
  context?: DMN14__tContext; // from type DMN14__tContextEntry @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type DMN14__tContextEntry @ DMN14.xsd
  relation?: DMN14__tRelation; // from type DMN14__tContextEntry @ DMN14.xsd
  list?: DMN14__tList; // from type DMN14__tContextEntry @ DMN14.xsd
  for?: DMN14__tFor; // from type DMN14__tContextEntry @ DMN14.xsd
  every?: DMN14__tQuantified; // from type DMN14__tContextEntry @ DMN14.xsd
  some?: DMN14__tQuantified; // from type DMN14__tContextEntry @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type DMN14__tContextEntry @ DMN14.xsd
  filter?: DMN14__tFilter; // from type DMN14__tContextEntry @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tContextEntry__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tContextEntry__extensionElements {}

export interface DMN14__tFunctionDefinition {
  "@_kind"?: DMN14__tFunctionKind; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  formalParameter?: DMN14__tInformationItem[]; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  literalExpression?: DMN14__tLiteralExpression; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  context?: DMN14__tContext; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  relation?: DMN14__tRelation; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  list?: DMN14__tList; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  for?: DMN14__tFor; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  every?: DMN14__tQuantified; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  some?: DMN14__tQuantified; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  filter?: DMN14__tFilter; // from type DMN14__tFunctionDefinition @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tFunctionDefinition__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tFunctionDefinition__extensionElements {}

export interface DMN14__tRelation {
  column?: DMN14__tInformationItem[]; // from type DMN14__tRelation @ DMN14.xsd
  row?: DMN14__tList[]; // from type DMN14__tRelation @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tRelation__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tRelation__extensionElements {}

export interface DMN14__tList {
  literalExpression?: DMN14__tLiteralExpression[]; // from type DMN14__tList @ DMN14.xsd
  invocation?: DMN14__tInvocation[]; // from type DMN14__tList @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable[]; // from type DMN14__tList @ DMN14.xsd
  context?: DMN14__tContext[]; // from type DMN14__tList @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition[]; // from type DMN14__tList @ DMN14.xsd
  relation?: DMN14__tRelation[]; // from type DMN14__tList @ DMN14.xsd
  list?: DMN14__tList[]; // from type DMN14__tList @ DMN14.xsd
  for?: DMN14__tFor[]; // from type DMN14__tList @ DMN14.xsd
  every?: DMN14__tQuantified[]; // from type DMN14__tList @ DMN14.xsd
  some?: DMN14__tQuantified[]; // from type DMN14__tList @ DMN14.xsd
  conditional?: DMN14__tConditional[]; // from type DMN14__tList @ DMN14.xsd
  filter?: DMN14__tFilter[]; // from type DMN14__tList @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tList__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tList__extensionElements {}

export interface DMN14__tUnaryTests {
  "@_expressionLanguage"?: string; // from type DMN14__tUnaryTests @ DMN14.xsd
  text: string; // from type DMN14__tUnaryTests @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tUnaryTests__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tUnaryTests__extensionElements {}

export interface DMN14__tDecisionService {
  outputDecision?: DMN14__tDMNElementReference[]; // from type DMN14__tDecisionService @ DMN14.xsd
  encapsulatedDecision?: DMN14__tDMNElementReference[]; // from type DMN14__tDecisionService @ DMN14.xsd
  inputDecision?: DMN14__tDMNElementReference[]; // from type DMN14__tDecisionService @ DMN14.xsd
  inputData?: DMN14__tDMNElementReference[]; // from type DMN14__tDecisionService @ DMN14.xsd
  variable?: DMN14__tInformationItem; // from type tInvocable @ DMN14.xsd
  "@_name": string; // from type tNamedElement @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tDecisionService__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tDecisionService__extensionElements {}

export type DMN14__tChildExpression = {
  "@_id"?: string; // from type DMN14__tChildExpression @ DMN14.xsd
  literalExpression?: DMN14__tLiteralExpression; // from type DMN14__tChildExpression @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type DMN14__tChildExpression @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type DMN14__tChildExpression @ DMN14.xsd
  context?: DMN14__tContext; // from type DMN14__tChildExpression @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type DMN14__tChildExpression @ DMN14.xsd
  relation?: DMN14__tRelation; // from type DMN14__tChildExpression @ DMN14.xsd
  list?: DMN14__tList; // from type DMN14__tChildExpression @ DMN14.xsd
  for?: DMN14__tFor; // from type DMN14__tChildExpression @ DMN14.xsd
  every?: DMN14__tQuantified; // from type DMN14__tChildExpression @ DMN14.xsd
  some?: DMN14__tQuantified; // from type DMN14__tChildExpression @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type DMN14__tChildExpression @ DMN14.xsd
  filter?: DMN14__tFilter; // from type DMN14__tChildExpression @ DMN14.xsd
};

export type DMN14__tTypedChildExpression = {
  "@_typeRef"?: string; // from type DMN14__tTypedChildExpression @ DMN14.xsd
  "@_id"?: string; // from type tChildExpression @ DMN14.xsd
  literalExpression?: DMN14__tLiteralExpression; // from type tExpression @ DMN14.xsd
  invocation?: DMN14__tInvocation; // from type tExpression @ DMN14.xsd
  decisionTable?: DMN14__tDecisionTable; // from type tExpression @ DMN14.xsd
  context?: DMN14__tContext; // from type tExpression @ DMN14.xsd
  functionDefinition?: DMN14__tFunctionDefinition; // from type tExpression @ DMN14.xsd
  relation?: DMN14__tRelation; // from type tExpression @ DMN14.xsd
  list?: DMN14__tList; // from type tExpression @ DMN14.xsd
  for?: DMN14__tFor; // from type tExpression @ DMN14.xsd
  every?: DMN14__tQuantified; // from type tExpression @ DMN14.xsd
  some?: DMN14__tQuantified; // from type tExpression @ DMN14.xsd
  conditional?: DMN14__tConditional; // from type tExpression @ DMN14.xsd
  filter?: DMN14__tFilter; // from type tExpression @ DMN14.xsd
};

export interface DMN14__tIterator {
  "@_iteratorVariable"?: string; // from type DMN14__tIterator @ DMN14.xsd
  in: DMN14__tTypedChildExpression; // from type DMN14__tIterator @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tIterator__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tIterator__extensionElements {}

export interface DMN14__tFor {
  return: DMN14__tChildExpression; // from type DMN14__tFor @ DMN14.xsd
  "@_iteratorVariable"?: string; // from type tIterator @ DMN14.xsd
  in: DMN14__tTypedChildExpression; // from type tIterator @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tFor__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tFor__extensionElements {}

export interface DMN14__tQuantified {
  satisfies: DMN14__tChildExpression; // from type DMN14__tQuantified @ DMN14.xsd
  "@_iteratorVariable"?: string; // from type tIterator @ DMN14.xsd
  in: DMN14__tTypedChildExpression; // from type tIterator @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tQuantified__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tQuantified__extensionElements {}

export interface DMN14__tConditional {
  if: DMN14__tChildExpression; // from type DMN14__tConditional @ DMN14.xsd
  then: DMN14__tChildExpression; // from type DMN14__tConditional @ DMN14.xsd
  else: DMN14__tChildExpression; // from type DMN14__tConditional @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tConditional__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tConditional__extensionElements {}

export interface DMN14__tFilter {
  in: DMN14__tChildExpression; // from type DMN14__tFilter @ DMN14.xsd
  match: DMN14__tChildExpression; // from type DMN14__tFilter @ DMN14.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN14.xsd
  "@_id"?: string; // from type tDMNElement @ DMN14.xsd
  "@_label"?: string; // from type tDMNElement @ DMN14.xsd
  description?: string; // from type tDMNElement @ DMN14.xsd
  extensionElements?: DMN14__tFilter__extensionElements; // from type tDMNElement @ DMN14.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN14__tFilter__extensionElements {}

export type DMNDI13__DMNDI = {
  "dmndi:DMNDiagram"?: DMNDI13__DMNDiagram[]; // from type DMNDI13__DMNDI @ DMNDI13.xsd
  "dmndi:DMNStyle"?: DMNDI13__DMNStyle[]; // from type DMNDI13__DMNDI @ DMNDI13.xsd
};

export interface DMNDI13__DMNDiagram {
  "dmndi:Size"?: DC__Dimension; // from type DMNDI13__DMNDiagram @ DMNDI13.xsd
  "dmndi:DMNShape"?: DMNDI13__DMNShape[]; // from type DMNDI13__DMNDiagram @ DMNDI13.xsd
  "dmndi:DMNEdge"?: DMNDI13__DMNEdge[]; // from type DMNDI13__DMNDiagram @ DMNDI13.xsd
  "@_name"?: string; // from type Diagram @ DI.xsd
  "@_documentation"?: string; // from type Diagram @ DI.xsd
  "@_resolution"?: number; // from type Diagram @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: DMNDI13__DMNDiagram__extension; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI13__DMNStyle; // from type di:Style @ DI.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMNDI13__DMNDiagram__extension {}

export interface DMNDI13__DMNShape {
  "@_dmnElementRef": string; // from type DMNDI13__DMNShape @ DMNDI13.xsd
  "@_isListedInputData"?: boolean; // from type DMNDI13__DMNShape @ DMNDI13.xsd
  "@_isCollapsed"?: boolean; // from type DMNDI13__DMNShape @ DMNDI13.xsd
  "dmndi:DMNLabel"?: DMNDI13__DMNLabel; // from type DMNDI13__DMNShape @ DMNDI13.xsd
  "dmndi:DMNDecisionServiceDividerLine"?: DMNDI13__DMNDecisionServiceDividerLine; // from type DMNDI13__DMNShape @ DMNDI13.xsd
  "dc:Bounds"?: DC__Bounds; // from type dc:Bounds @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: DMNDI13__DMNShape__extension; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI13__DMNStyle; // from type di:Style @ DI.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMNDI13__DMNShape__extension {}

export interface DMNDI13__DMNDecisionServiceDividerLine {
  "di:waypoint"?: DC__Point[]; // from type Edge @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: DMNDI13__DMNDecisionServiceDividerLine__extension; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI13__DMNStyle; // from type di:Style @ DI.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMNDI13__DMNDecisionServiceDividerLine__extension {}

export interface DMNDI13__DMNEdge {
  "@_dmnElementRef": string; // from type DMNDI13__DMNEdge @ DMNDI13.xsd
  "@_sourceElement"?: string; // from type DMNDI13__DMNEdge @ DMNDI13.xsd
  "@_targetElement"?: string; // from type DMNDI13__DMNEdge @ DMNDI13.xsd
  "dmndi:DMNLabel"?: DMNDI13__DMNLabel; // from type DMNDI13__DMNEdge @ DMNDI13.xsd
  "di:waypoint"?: DC__Point[]; // from type Edge @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: DMNDI13__DMNEdge__extension; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI13__DMNStyle; // from type di:Style @ DI.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMNDI13__DMNEdge__extension {}

export interface DMNDI13__DMNLabel {
  "dmndi:Text"?: string; // from type DMNDI13__DMNLabel @ DMNDI13.xsd
  "dc:Bounds"?: DC__Bounds; // from type dc:Bounds @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: DMNDI13__DMNLabel__extension; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI13__DMNStyle; // from type di:Style @ DI.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMNDI13__DMNLabel__extension {}

export interface DMNDI13__DMNStyle {
  "@_fontFamily"?: string; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_fontSize"?: number; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_fontItalic"?: boolean; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_fontBold"?: boolean; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_fontUnderline"?: boolean; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_fontStrikeThrough"?: boolean; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_labelHorizontalAlignement"?: DC__AlignmentKind; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_labelVerticalAlignment"?: DC__AlignmentKind; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "dmndi:FillColor"?: DC__Color; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "dmndi:StrokeColor"?: DC__Color; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "dmndi:FontColor"?: DC__Color; // from type DMNDI13__DMNStyle @ DMNDI13.xsd
  "@_id"?: string; // from type Style @ DI.xsd
  "di:extension"?: DMNDI13__DMNStyle__extension; // from type Style @ DI.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMNDI13__DMNStyle__extension {}

export type DC__Color = {
  "@_red": number; // from type DC__Color @ DC.xsd
  "@_green": number; // from type DC__Color @ DC.xsd
  "@_blue": number; // from type DC__Color @ DC.xsd
};

export type DC__Point = {
  "@_x": number; // from type DC__Point @ DC.xsd
  "@_y": number; // from type DC__Point @ DC.xsd
};

export type DC__Dimension = {
  "@_width": number; // from type DC__Dimension @ DC.xsd
  "@_height": number; // from type DC__Dimension @ DC.xsd
};

export type DC__Bounds = {
  "@_x": number; // from type DC__Bounds @ DC.xsd
  "@_y": number; // from type DC__Bounds @ DC.xsd
  "@_width": number; // from type DC__Bounds @ DC.xsd
  "@_height": number; // from type DC__Bounds @ DC.xsd
};
