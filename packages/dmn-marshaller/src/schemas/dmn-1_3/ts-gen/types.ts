// This file was automatically generated

import { XmlParserTsRootElementBaseType } from "@kie-tools/xml-parser-ts";

export type DMN13__tHitPolicy = "UNIQUE" | "FIRST" | "PRIORITY" | "ANY" | "COLLECT" | "RULE ORDER" | "OUTPUT ORDER";

export type DMN13__tBuiltinAggregator = "SUM" | "COUNT" | "MIN" | "MAX";

export type DMN13__tDecisionTableOrientation = "Rule-as-Row" | "Rule-as-Column" | "CrossTable";

export type DMN13__tAssociationDirection = "None" | "One" | "Both";

export type DMN13__tFunctionKind = "FEEL" | "Java" | "PMML";

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

export interface DMN13__tDMNElement {
  "@_id"?: string; // from type DMN13__tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type DMN13__tDMNElement @ DMN13.xsd
  description?: string; // from type DMN13__tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDMNElement__extensionElements; // from type DMN13__tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDMNElement__extensionElements {}

export interface DMN13__tNamedElement {
  "@_name": string; // from type DMN13__tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tNamedElement__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tNamedElement__extensionElements {}

export type DMN13__tDMNElementReference = {
  "@_href": string; // from type DMN13__tDMNElementReference @ DMN13.xsd
};

export interface DMN13__tDefinitions extends XmlParserTsRootElementBaseType {
  "@_expressionLanguage"?: string; // from type DMN13__tDefinitions @ DMN13.xsd
  "@_typeLanguage"?: string; // from type DMN13__tDefinitions @ DMN13.xsd
  "@_namespace": string; // from type DMN13__tDefinitions @ DMN13.xsd
  "@_exporter"?: string; // from type DMN13__tDefinitions @ DMN13.xsd
  "@_exporterVersion"?: string; // from type DMN13__tDefinitions @ DMN13.xsd
  import?: DMN13__tImport[]; // from type DMN13__tDefinitions @ DMN13.xsd
  itemDefinition?: DMN13__tItemDefinition[]; // from type DMN13__tDefinitions @ DMN13.xsd
  decision?: DMN13__tDecision[]; // from type DMN13__tDefinitions @ DMN13.xsd
  businessKnowledgeModel?: DMN13__tBusinessKnowledgeModel[]; // from type DMN13__tDefinitions @ DMN13.xsd
  decisionService?: DMN13__tDecisionService[]; // from type DMN13__tDefinitions @ DMN13.xsd
  inputData?: DMN13__tInputData[]; // from type DMN13__tDefinitions @ DMN13.xsd
  knowledgeSource?: DMN13__tKnowledgeSource[]; // from type DMN13__tDefinitions @ DMN13.xsd
  group?: DMN13__tGroup[]; // from type DMN13__tDefinitions @ DMN13.xsd
  textAnnotation?: DMN13__tTextAnnotation[]; // from type DMN13__tDefinitions @ DMN13.xsd
  association?: DMN13__tAssociation[]; // from type DMN13__tDefinitions @ DMN13.xsd
  elementCollection?: DMN13__tElementCollection[]; // from type DMN13__tDefinitions @ DMN13.xsd
  performanceIndicator?: DMN13__tPerformanceIndicator[]; // from type DMN13__tDefinitions @ DMN13.xsd
  organizationUnit?: DMN13__tOrganizationUnit[]; // from type DMN13__tDefinitions @ DMN13.xsd
  "dmndi:DMNDI"?: DMNDI13__DMNDI; // from type DMN13__tDefinitions @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDefinitions__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDefinitions__extensionElements {}

export interface DMN13__tImport {
  "@_namespace": string; // from type DMN13__tImport @ DMN13.xsd
  "@_locationURI"?: string; // from type DMN13__tImport @ DMN13.xsd
  "@_importType": string; // from type DMN13__tImport @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tImport__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tImport__extensionElements {}

export interface DMN13__tElementCollection {
  drgElement?: DMN13__tDMNElementReference[]; // from type DMN13__tElementCollection @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tElementCollection__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tElementCollection__extensionElements {}

export interface DMN13__tDRGElement {
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDRGElement__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDRGElement__extensionElements {}

export interface DMN13__tDecision {
  question?: string; // from type DMN13__tDecision @ DMN13.xsd
  allowedAnswers?: string; // from type DMN13__tDecision @ DMN13.xsd
  variable?: DMN13__tInformationItem; // from type DMN13__tDecision @ DMN13.xsd
  informationRequirement?: DMN13__tInformationRequirement[]; // from type DMN13__tDecision @ DMN13.xsd
  knowledgeRequirement?: DMN13__tKnowledgeRequirement[]; // from type DMN13__tDecision @ DMN13.xsd
  authorityRequirement?: DMN13__tAuthorityRequirement[]; // from type DMN13__tDecision @ DMN13.xsd
  supportedObjective?: DMN13__tDMNElementReference[]; // from type DMN13__tDecision @ DMN13.xsd
  impactedPerformanceIndicator?: DMN13__tDMNElementReference[]; // from type DMN13__tDecision @ DMN13.xsd
  decisionMaker?: DMN13__tDMNElementReference[]; // from type DMN13__tDecision @ DMN13.xsd
  decisionOwner?: DMN13__tDMNElementReference[]; // from type DMN13__tDecision @ DMN13.xsd
  usingProcess?: DMN13__tDMNElementReference[]; // from type DMN13__tDecision @ DMN13.xsd
  usingTask?: DMN13__tDMNElementReference[]; // from type DMN13__tDecision @ DMN13.xsd
  literalExpression?: DMN13__tLiteralExpression; // from type DMN13__tDecision @ DMN13.xsd
  invocation?: DMN13__tInvocation; // from type DMN13__tDecision @ DMN13.xsd
  decisionTable?: DMN13__tDecisionTable; // from type DMN13__tDecision @ DMN13.xsd
  context?: DMN13__tContext; // from type DMN13__tDecision @ DMN13.xsd
  functionDefinition?: DMN13__tFunctionDefinition; // from type DMN13__tDecision @ DMN13.xsd
  relation?: DMN13__tRelation; // from type DMN13__tDecision @ DMN13.xsd
  list?: DMN13__tList; // from type DMN13__tDecision @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDecision__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDecision__extensionElements {}

export interface DMN13__tBusinessContextElement {
  "@_URI"?: string; // from type DMN13__tBusinessContextElement @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tBusinessContextElement__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tBusinessContextElement__extensionElements {}

export interface DMN13__tPerformanceIndicator {
  impactingDecision?: DMN13__tDMNElementReference[]; // from type DMN13__tPerformanceIndicator @ DMN13.xsd
  "@_URI"?: string; // from type tBusinessContextElement @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tPerformanceIndicator__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tPerformanceIndicator__extensionElements {}

export interface DMN13__tOrganizationUnit {
  decisionMade?: DMN13__tDMNElementReference[]; // from type DMN13__tOrganizationUnit @ DMN13.xsd
  decisionOwned?: DMN13__tDMNElementReference[]; // from type DMN13__tOrganizationUnit @ DMN13.xsd
  "@_URI"?: string; // from type tBusinessContextElement @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tOrganizationUnit__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tOrganizationUnit__extensionElements {}

export interface DMN13__tInvocable {
  variable?: DMN13__tInformationItem; // from type DMN13__tInvocable @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tInvocable__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tInvocable__extensionElements {}

export interface DMN13__tBusinessKnowledgeModel {
  encapsulatedLogic?: DMN13__tFunctionDefinition; // from type DMN13__tBusinessKnowledgeModel @ DMN13.xsd
  knowledgeRequirement?: DMN13__tKnowledgeRequirement[]; // from type DMN13__tBusinessKnowledgeModel @ DMN13.xsd
  authorityRequirement?: DMN13__tAuthorityRequirement[]; // from type DMN13__tBusinessKnowledgeModel @ DMN13.xsd
  variable?: DMN13__tInformationItem; // from type tInvocable @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tBusinessKnowledgeModel__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tBusinessKnowledgeModel__extensionElements {}

export interface DMN13__tInputData {
  variable?: DMN13__tInformationItem; // from type DMN13__tInputData @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tInputData__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tInputData__extensionElements {}

export interface DMN13__tKnowledgeSource {
  "@_locationURI"?: string; // from type DMN13__tKnowledgeSource @ DMN13.xsd
  authorityRequirement?: DMN13__tAuthorityRequirement[]; // from type DMN13__tKnowledgeSource @ DMN13.xsd
  type?: string; // from type DMN13__tKnowledgeSource @ DMN13.xsd
  owner?: DMN13__tDMNElementReference; // from type DMN13__tKnowledgeSource @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tKnowledgeSource__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tKnowledgeSource__extensionElements {}

export interface DMN13__tInformationRequirement {
  requiredDecision?: DMN13__tDMNElementReference; // from type DMN13__tInformationRequirement @ DMN13.xsd
  requiredInput?: DMN13__tDMNElementReference; // from type DMN13__tInformationRequirement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tInformationRequirement__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tInformationRequirement__extensionElements {}

export interface DMN13__tKnowledgeRequirement {
  requiredKnowledge: DMN13__tDMNElementReference; // from type DMN13__tKnowledgeRequirement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tKnowledgeRequirement__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tKnowledgeRequirement__extensionElements {}

export interface DMN13__tAuthorityRequirement {
  requiredDecision?: DMN13__tDMNElementReference; // from type DMN13__tAuthorityRequirement @ DMN13.xsd
  requiredInput?: DMN13__tDMNElementReference; // from type DMN13__tAuthorityRequirement @ DMN13.xsd
  requiredAuthority?: DMN13__tDMNElementReference; // from type DMN13__tAuthorityRequirement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tAuthorityRequirement__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tAuthorityRequirement__extensionElements {}

export interface DMN13__tExpression {
  "@_typeRef"?: string; // from type DMN13__tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tExpression__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tExpression__extensionElements {}

export interface DMN13__tItemDefinition {
  "@_typeLanguage"?: string; // from type DMN13__tItemDefinition @ DMN13.xsd
  "@_isCollection"?: boolean; // from type DMN13__tItemDefinition @ DMN13.xsd
  itemComponent?: DMN13__tItemDefinition[]; // from type DMN13__tItemDefinition @ DMN13.xsd
  functionItem?: DMN13__tFunctionItem; // from type DMN13__tItemDefinition @ DMN13.xsd
  typeRef?: string; // from type DMN13__tItemDefinition @ DMN13.xsd
  allowedValues?: DMN13__tUnaryTests; // from type DMN13__tItemDefinition @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tItemDefinition__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tItemDefinition__extensionElements {}

export interface DMN13__tFunctionItem {
  "@_outputTypeRef"?: string; // from type DMN13__tFunctionItem @ DMN13.xsd
  parameters?: DMN13__tInformationItem[]; // from type DMN13__tFunctionItem @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tFunctionItem__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tFunctionItem__extensionElements {}

export interface DMN13__tLiteralExpression {
  "@_expressionLanguage"?: string; // from type DMN13__tLiteralExpression @ DMN13.xsd
  text?: string; // from type DMN13__tLiteralExpression @ DMN13.xsd
  importedValues?: DMN13__tImportedValues; // from type DMN13__tLiteralExpression @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tLiteralExpression__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tLiteralExpression__extensionElements {}

export interface DMN13__tInvocation {
  literalExpression?: DMN13__tLiteralExpression; // from type DMN13__tInvocation @ DMN13.xsd
  invocation?: DMN13__tInvocation; // from type DMN13__tInvocation @ DMN13.xsd
  decisionTable?: DMN13__tDecisionTable; // from type DMN13__tInvocation @ DMN13.xsd
  context?: DMN13__tContext; // from type DMN13__tInvocation @ DMN13.xsd
  functionDefinition?: DMN13__tFunctionDefinition; // from type DMN13__tInvocation @ DMN13.xsd
  relation?: DMN13__tRelation; // from type DMN13__tInvocation @ DMN13.xsd
  list?: DMN13__tList; // from type DMN13__tInvocation @ DMN13.xsd
  binding?: DMN13__tBinding[]; // from type DMN13__tInvocation @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tInvocation__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tInvocation__extensionElements {}

export type DMN13__tBinding = {
  parameter: DMN13__tInformationItem; // from type DMN13__tBinding @ DMN13.xsd
  literalExpression?: DMN13__tLiteralExpression; // from type DMN13__tBinding @ DMN13.xsd
  invocation?: DMN13__tInvocation; // from type DMN13__tBinding @ DMN13.xsd
  decisionTable?: DMN13__tDecisionTable; // from type DMN13__tBinding @ DMN13.xsd
  context?: DMN13__tContext; // from type DMN13__tBinding @ DMN13.xsd
  functionDefinition?: DMN13__tFunctionDefinition; // from type DMN13__tBinding @ DMN13.xsd
  relation?: DMN13__tRelation; // from type DMN13__tBinding @ DMN13.xsd
  list?: DMN13__tList; // from type DMN13__tBinding @ DMN13.xsd
};

export interface DMN13__tInformationItem {
  "@_typeRef"?: string; // from type DMN13__tInformationItem @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tInformationItem__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tInformationItem__extensionElements {}

export interface DMN13__tDecisionTable {
  "@_hitPolicy"?: DMN13__tHitPolicy; // from type DMN13__tDecisionTable @ DMN13.xsd
  "@_aggregation"?: DMN13__tBuiltinAggregator; // from type DMN13__tDecisionTable @ DMN13.xsd
  "@_preferredOrientation"?: DMN13__tDecisionTableOrientation; // from type DMN13__tDecisionTable @ DMN13.xsd
  "@_outputLabel"?: string; // from type DMN13__tDecisionTable @ DMN13.xsd
  input?: DMN13__tInputClause[]; // from type DMN13__tDecisionTable @ DMN13.xsd
  output: DMN13__tOutputClause[]; // from type DMN13__tDecisionTable @ DMN13.xsd
  annotation?: DMN13__tRuleAnnotationClause[]; // from type DMN13__tDecisionTable @ DMN13.xsd
  rule?: DMN13__tDecisionRule[]; // from type DMN13__tDecisionTable @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDecisionTable__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDecisionTable__extensionElements {}

export interface DMN13__tInputClause {
  inputExpression: DMN13__tLiteralExpression; // from type DMN13__tInputClause @ DMN13.xsd
  inputValues?: DMN13__tUnaryTests; // from type DMN13__tInputClause @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tInputClause__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tInputClause__extensionElements {}

export interface DMN13__tOutputClause {
  "@_name"?: string; // from type DMN13__tOutputClause @ DMN13.xsd
  "@_typeRef"?: string; // from type DMN13__tOutputClause @ DMN13.xsd
  outputValues?: DMN13__tUnaryTests; // from type DMN13__tOutputClause @ DMN13.xsd
  defaultOutputEntry?: DMN13__tLiteralExpression; // from type DMN13__tOutputClause @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tOutputClause__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tOutputClause__extensionElements {}

export type DMN13__tRuleAnnotationClause = {
  "@_name"?: string; // from type DMN13__tRuleAnnotationClause @ DMN13.xsd
};

export interface DMN13__tDecisionRule {
  inputEntry?: DMN13__tUnaryTests[]; // from type DMN13__tDecisionRule @ DMN13.xsd
  outputEntry: DMN13__tLiteralExpression[]; // from type DMN13__tDecisionRule @ DMN13.xsd
  annotationEntry?: DMN13__tRuleAnnotation[]; // from type DMN13__tDecisionRule @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDecisionRule__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDecisionRule__extensionElements {}

export type DMN13__tRuleAnnotation = {
  text?: string; // from type DMN13__tRuleAnnotation @ DMN13.xsd
};

export interface DMN13__tImportedValues {
  "@_expressionLanguage"?: string; // from type DMN13__tImportedValues @ DMN13.xsd
  importedElement: string; // from type DMN13__tImportedValues @ DMN13.xsd
  "@_namespace": string; // from type tImport @ DMN13.xsd
  "@_locationURI"?: string; // from type tImport @ DMN13.xsd
  "@_importType": string; // from type tImport @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tImportedValues__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tImportedValues__extensionElements {}

export interface DMN13__tArtifact {
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tArtifact__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tArtifact__extensionElements {}

export interface DMN13__tGroup {
  "@_name"?: string; // from type DMN13__tGroup @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tGroup__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tGroup__extensionElements {}

export interface DMN13__tTextAnnotation {
  "@_textFormat"?: string; // from type DMN13__tTextAnnotation @ DMN13.xsd
  text?: string; // from type DMN13__tTextAnnotation @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tTextAnnotation__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tTextAnnotation__extensionElements {}

export interface DMN13__tAssociation {
  "@_associationDirection"?: DMN13__tAssociationDirection; // from type DMN13__tAssociation @ DMN13.xsd
  sourceRef: DMN13__tDMNElementReference; // from type DMN13__tAssociation @ DMN13.xsd
  targetRef: DMN13__tDMNElementReference; // from type DMN13__tAssociation @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tAssociation__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tAssociation__extensionElements {}

export interface DMN13__tContext {
  contextEntry?: DMN13__tContextEntry[]; // from type DMN13__tContext @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tContext__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tContext__extensionElements {}

export interface DMN13__tContextEntry {
  variable?: DMN13__tInformationItem; // from type DMN13__tContextEntry @ DMN13.xsd
  literalExpression?: DMN13__tLiteralExpression; // from type DMN13__tContextEntry @ DMN13.xsd
  invocation?: DMN13__tInvocation; // from type DMN13__tContextEntry @ DMN13.xsd
  decisionTable?: DMN13__tDecisionTable; // from type DMN13__tContextEntry @ DMN13.xsd
  context?: DMN13__tContext; // from type DMN13__tContextEntry @ DMN13.xsd
  functionDefinition?: DMN13__tFunctionDefinition; // from type DMN13__tContextEntry @ DMN13.xsd
  relation?: DMN13__tRelation; // from type DMN13__tContextEntry @ DMN13.xsd
  list?: DMN13__tList; // from type DMN13__tContextEntry @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tContextEntry__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tContextEntry__extensionElements {}

export interface DMN13__tFunctionDefinition {
  "@_kind"?: DMN13__tFunctionKind; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  formalParameter?: DMN13__tInformationItem[]; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  literalExpression?: DMN13__tLiteralExpression; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  invocation?: DMN13__tInvocation; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  decisionTable?: DMN13__tDecisionTable; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  context?: DMN13__tContext; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  functionDefinition?: DMN13__tFunctionDefinition; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  relation?: DMN13__tRelation; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  list?: DMN13__tList; // from type DMN13__tFunctionDefinition @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tFunctionDefinition__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tFunctionDefinition__extensionElements {}

export interface DMN13__tRelation {
  column?: DMN13__tInformationItem[]; // from type DMN13__tRelation @ DMN13.xsd
  row?: DMN13__tList[]; // from type DMN13__tRelation @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tRelation__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tRelation__extensionElements {}

export interface DMN13__tList {
  literalExpression?: DMN13__tLiteralExpression[]; // from type DMN13__tList @ DMN13.xsd
  invocation?: DMN13__tInvocation[]; // from type DMN13__tList @ DMN13.xsd
  decisionTable?: DMN13__tDecisionTable[]; // from type DMN13__tList @ DMN13.xsd
  context?: DMN13__tContext[]; // from type DMN13__tList @ DMN13.xsd
  functionDefinition?: DMN13__tFunctionDefinition[]; // from type DMN13__tList @ DMN13.xsd
  relation?: DMN13__tRelation[]; // from type DMN13__tList @ DMN13.xsd
  list?: DMN13__tList[]; // from type DMN13__tList @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tList__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tList__extensionElements {}

export interface DMN13__tUnaryTests {
  "@_expressionLanguage"?: string; // from type DMN13__tUnaryTests @ DMN13.xsd
  text: string; // from type DMN13__tUnaryTests @ DMN13.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tUnaryTests__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tUnaryTests__extensionElements {}

export interface DMN13__tDecisionService {
  outputDecision?: DMN13__tDMNElementReference[]; // from type DMN13__tDecisionService @ DMN13.xsd
  encapsulatedDecision?: DMN13__tDMNElementReference[]; // from type DMN13__tDecisionService @ DMN13.xsd
  inputDecision?: DMN13__tDMNElementReference[]; // from type DMN13__tDecisionService @ DMN13.xsd
  inputData?: DMN13__tDMNElementReference[]; // from type DMN13__tDecisionService @ DMN13.xsd
  variable?: DMN13__tInformationItem; // from type tInvocable @ DMN13.xsd
  "@_name": string; // from type tNamedElement @ DMN13.xsd
  "@_id"?: string; // from type tDMNElement @ DMN13.xsd
  "@_label"?: string; // from type tDMNElement @ DMN13.xsd
  description?: string; // from type tDMNElement @ DMN13.xsd
  extensionElements?: DMN13__tDecisionService__extensionElements; // from type tDMNElement @ DMN13.xsd
}

// eslint-disable-next-line @typescript-eslint/no-empty-interface
export interface DMN13__tDecisionService__extensionElements {}

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
