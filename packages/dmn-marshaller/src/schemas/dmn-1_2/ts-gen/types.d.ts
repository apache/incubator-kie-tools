// This file was automatically generated

import { DMN12__tDMNElementExtensionType } from "../ts-gen-extensions/DMN12__tDMNElementExtensionType";

export type DMN12__tHitPolicy = "UNIQUE" | "FIRST" | "PRIORITY" | "ANY" | "COLLECT" | "RULE ORDER" | "OUTPUT ORDER";

export type DMN12__tBuiltinAggregator = "SUM" | "COUNT" | "MIN" | "MAX";

export type DMN12__tDecisionTableOrientation = "Rule-as-Row" | "Rule-as-Column" | "CrossTable";

export type DMN12__tAssociationDirection = "None" | "One" | "Both";

export type DMN12__tFunctionKind = "FEEL" | "Java" | "PMML";

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

export type DMN12__tDMNElement = {
  "@_id"?: string; // from type DMN12__tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type DMN12__tDMNElement @ DMN12.xsd
  description?: string; // from type DMN12__tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type DMN12__tDMNElement @ DMN12.xsd
} & DMN12__tDMNElementExtensionType;

export type DMN12__tNamedElement = {
  "@_name": string; // from type DMN12__tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tDMNElementReference = {
  "@_href": string; // from type DMN12__tDMNElementReference @ DMN12.xsd
};

export type DMN12__tDefinitions = Partial<{ [k: `@_xmlns:${string}`]: string }> & { "@_xmlns"?: string } & {
  "@_expressionLanguage"?: string; // from type DMN12__tDefinitions @ DMN12.xsd
  "@_typeLanguage"?: string; // from type DMN12__tDefinitions @ DMN12.xsd
  "@_namespace": string; // from type DMN12__tDefinitions @ DMN12.xsd
  "@_exporter"?: string; // from type DMN12__tDefinitions @ DMN12.xsd
  "@_exporterVersion"?: string; // from type DMN12__tDefinitions @ DMN12.xsd
  import?: DMN12__tImport[]; // from type DMN12__tDefinitions @ DMN12.xsd
  itemDefinition?: DMN12__tItemDefinition[]; // from type DMN12__tDefinitions @ DMN12.xsd
  decision?: DMN12__tDecision[]; // from type DMN12__tDefinitions @ DMN12.xsd
  businessKnowledgeModel?: DMN12__tBusinessKnowledgeModel[]; // from type DMN12__tDefinitions @ DMN12.xsd
  decisionService?: DMN12__tDecisionService[]; // from type DMN12__tDefinitions @ DMN12.xsd
  inputData?: DMN12__tInputData[]; // from type DMN12__tDefinitions @ DMN12.xsd
  knowledgeSource?: DMN12__tKnowledgeSource[]; // from type DMN12__tDefinitions @ DMN12.xsd
  textAnnotation?: DMN12__tTextAnnotation[]; // from type DMN12__tDefinitions @ DMN12.xsd
  association?: DMN12__tAssociation[]; // from type DMN12__tDefinitions @ DMN12.xsd
  elementCollection?: DMN12__tElementCollection[]; // from type DMN12__tDefinitions @ DMN12.xsd
  performanceIndicator?: DMN12__tPerformanceIndicator[]; // from type DMN12__tDefinitions @ DMN12.xsd
  organizationUnit?: DMN12__tOrganizationUnit[]; // from type DMN12__tDefinitions @ DMN12.xsd
  "dmndi:DMNDI"?: DMNDI12__DMNDI; // from type DMN12__tDefinitions @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tImport = {
  "@_namespace": string; // from type DMN12__tImport @ DMN12.xsd
  "@_locationURI"?: string; // from type DMN12__tImport @ DMN12.xsd
  "@_importType": string; // from type DMN12__tImport @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tElementCollection = {
  drgElement?: DMN12__tDMNElementReference[]; // from type DMN12__tElementCollection @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tDRGElement = {
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tDecision = {
  question?: string; // from type DMN12__tDecision @ DMN12.xsd
  allowedAnswers?: string; // from type DMN12__tDecision @ DMN12.xsd
  variable?: DMN12__tInformationItem; // from type DMN12__tDecision @ DMN12.xsd
  informationRequirement?: DMN12__tInformationRequirement[]; // from type DMN12__tDecision @ DMN12.xsd
  knowledgeRequirement?: DMN12__tKnowledgeRequirement[]; // from type DMN12__tDecision @ DMN12.xsd
  authorityRequirement?: DMN12__tAuthorityRequirement[]; // from type DMN12__tDecision @ DMN12.xsd
  supportedObjective?: DMN12__tDMNElementReference[]; // from type DMN12__tDecision @ DMN12.xsd
  impactedPerformanceIndicator?: DMN12__tDMNElementReference[]; // from type DMN12__tDecision @ DMN12.xsd
  decisionMaker?: DMN12__tDMNElementReference[]; // from type DMN12__tDecision @ DMN12.xsd
  decisionOwner?: DMN12__tDMNElementReference[]; // from type DMN12__tDecision @ DMN12.xsd
  usingProcess?: DMN12__tDMNElementReference[]; // from type DMN12__tDecision @ DMN12.xsd
  usingTask?: DMN12__tDMNElementReference[]; // from type DMN12__tDecision @ DMN12.xsd
  literalExpression?: DMN12__tLiteralExpression; // from type DMN12__tDecision @ DMN12.xsd
  invocation?: DMN12__tInvocation; // from type DMN12__tDecision @ DMN12.xsd
  decisionTable?: DMN12__tDecisionTable; // from type DMN12__tDecision @ DMN12.xsd
  context?: DMN12__tContext; // from type DMN12__tDecision @ DMN12.xsd
  functionDefinition?: DMN12__tFunctionDefinition; // from type DMN12__tDecision @ DMN12.xsd
  relation?: DMN12__tRelation; // from type DMN12__tDecision @ DMN12.xsd
  list?: DMN12__tList; // from type DMN12__tDecision @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tBusinessContextElement = {
  "@_URI"?: string; // from type DMN12__tBusinessContextElement @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tPerformanceIndicator = {
  impactingDecision?: DMN12__tDMNElementReference[]; // from type DMN12__tPerformanceIndicator @ DMN12.xsd
  "@_URI"?: string; // from type tBusinessContextElement @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tOrganizationUnit = {
  decisionMade?: DMN12__tDMNElementReference[]; // from type DMN12__tOrganizationUnit @ DMN12.xsd
  decisionOwned?: DMN12__tDMNElementReference[]; // from type DMN12__tOrganizationUnit @ DMN12.xsd
  "@_URI"?: string; // from type tBusinessContextElement @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tInvocable = {
  variable?: DMN12__tInformationItem; // from type DMN12__tInvocable @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tBusinessKnowledgeModel = {
  encapsulatedLogic?: DMN12__tFunctionDefinition; // from type DMN12__tBusinessKnowledgeModel @ DMN12.xsd
  knowledgeRequirement?: DMN12__tKnowledgeRequirement[]; // from type DMN12__tBusinessKnowledgeModel @ DMN12.xsd
  authorityRequirement?: DMN12__tAuthorityRequirement[]; // from type DMN12__tBusinessKnowledgeModel @ DMN12.xsd
  variable?: DMN12__tInformationItem; // from type tInvocable @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tInputData = {
  variable?: DMN12__tInformationItem; // from type DMN12__tInputData @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tKnowledgeSource = {
  "@_locationURI"?: string; // from type DMN12__tKnowledgeSource @ DMN12.xsd
  authorityRequirement?: DMN12__tAuthorityRequirement[]; // from type DMN12__tKnowledgeSource @ DMN12.xsd
  type?: string; // from type DMN12__tKnowledgeSource @ DMN12.xsd
  owner?: DMN12__tDMNElementReference; // from type DMN12__tKnowledgeSource @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tInformationRequirement = {
  requiredDecision?: DMN12__tDMNElementReference; // from type DMN12__tInformationRequirement @ DMN12.xsd
  requiredInput?: DMN12__tDMNElementReference; // from type DMN12__tInformationRequirement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tKnowledgeRequirement = {
  requiredKnowledge: DMN12__tDMNElementReference; // from type DMN12__tKnowledgeRequirement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tAuthorityRequirement = {
  requiredDecision?: DMN12__tDMNElementReference; // from type DMN12__tAuthorityRequirement @ DMN12.xsd
  requiredInput?: DMN12__tDMNElementReference; // from type DMN12__tAuthorityRequirement @ DMN12.xsd
  requiredAuthority?: DMN12__tDMNElementReference; // from type DMN12__tAuthorityRequirement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tExpression = {
  "@_typeRef"?: string; // from type DMN12__tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tItemDefinition = {
  "@_typeLanguage"?: string; // from type DMN12__tItemDefinition @ DMN12.xsd
  "@_isCollection"?: boolean; // from type DMN12__tItemDefinition @ DMN12.xsd
  itemComponent?: DMN12__tItemDefinition[]; // from type DMN12__tItemDefinition @ DMN12.xsd
  typeRef?: string; // from type DMN12__tItemDefinition @ DMN12.xsd
  allowedValues?: DMN12__tUnaryTests; // from type DMN12__tItemDefinition @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tLiteralExpression = {
  "@_expressionLanguage"?: string; // from type DMN12__tLiteralExpression @ DMN12.xsd
  text?: string; // from type DMN12__tLiteralExpression @ DMN12.xsd
  importedValues?: DMN12__tImportedValues; // from type DMN12__tLiteralExpression @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tInvocation = {
  literalExpression?: DMN12__tLiteralExpression; // from type DMN12__tInvocation @ DMN12.xsd
  invocation?: DMN12__tInvocation; // from type DMN12__tInvocation @ DMN12.xsd
  decisionTable?: DMN12__tDecisionTable; // from type DMN12__tInvocation @ DMN12.xsd
  context?: DMN12__tContext; // from type DMN12__tInvocation @ DMN12.xsd
  functionDefinition?: DMN12__tFunctionDefinition; // from type DMN12__tInvocation @ DMN12.xsd
  relation?: DMN12__tRelation; // from type DMN12__tInvocation @ DMN12.xsd
  list?: DMN12__tList; // from type DMN12__tInvocation @ DMN12.xsd
  binding?: DMN12__tBinding[]; // from type DMN12__tInvocation @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tBinding = {
  parameter: DMN12__tInformationItem; // from type DMN12__tBinding @ DMN12.xsd
  literalExpression?: DMN12__tLiteralExpression; // from type DMN12__tBinding @ DMN12.xsd
  invocation?: DMN12__tInvocation; // from type DMN12__tBinding @ DMN12.xsd
  decisionTable?: DMN12__tDecisionTable; // from type DMN12__tBinding @ DMN12.xsd
  context?: DMN12__tContext; // from type DMN12__tBinding @ DMN12.xsd
  functionDefinition?: DMN12__tFunctionDefinition; // from type DMN12__tBinding @ DMN12.xsd
  relation?: DMN12__tRelation; // from type DMN12__tBinding @ DMN12.xsd
  list?: DMN12__tList; // from type DMN12__tBinding @ DMN12.xsd
};

export type DMN12__tInformationItem = {
  "@_typeRef"?: string; // from type DMN12__tInformationItem @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tDecisionTable = {
  "@_hitPolicy"?: DMN12__tHitPolicy; // from type DMN12__tDecisionTable @ DMN12.xsd
  "@_aggregation"?: DMN12__tBuiltinAggregator; // from type DMN12__tDecisionTable @ DMN12.xsd
  "@_preferredOrientation"?: DMN12__tDecisionTableOrientation; // from type DMN12__tDecisionTable @ DMN12.xsd
  "@_outputLabel"?: string; // from type DMN12__tDecisionTable @ DMN12.xsd
  input?: DMN12__tInputClause[]; // from type DMN12__tDecisionTable @ DMN12.xsd
  output: DMN12__tOutputClause[]; // from type DMN12__tDecisionTable @ DMN12.xsd
  annotation?: DMN12__tRuleAnnotationClause[]; // from type DMN12__tDecisionTable @ DMN12.xsd
  rule?: DMN12__tDecisionRule[]; // from type DMN12__tDecisionTable @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tInputClause = {
  inputExpression: DMN12__tLiteralExpression; // from type DMN12__tInputClause @ DMN12.xsd
  inputValues?: DMN12__tUnaryTests; // from type DMN12__tInputClause @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tOutputClause = {
  "@_name"?: string; // from type DMN12__tOutputClause @ DMN12.xsd
  "@_typeRef"?: string; // from type DMN12__tOutputClause @ DMN12.xsd
  outputValues?: DMN12__tUnaryTests; // from type DMN12__tOutputClause @ DMN12.xsd
  defaultOutputEntry?: DMN12__tLiteralExpression; // from type DMN12__tOutputClause @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tRuleAnnotationClause = {
  "@_name"?: string; // from type DMN12__tRuleAnnotationClause @ DMN12.xsd
};

export type DMN12__tDecisionRule = {
  inputEntry?: DMN12__tUnaryTests[]; // from type DMN12__tDecisionRule @ DMN12.xsd
  outputEntry: DMN12__tLiteralExpression[]; // from type DMN12__tDecisionRule @ DMN12.xsd
  annotationEntry?: DMN12__tRuleAnnotation[]; // from type DMN12__tDecisionRule @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tRuleAnnotation = {
  text?: string; // from type DMN12__tRuleAnnotation @ DMN12.xsd
};

export type DMN12__tImportedValues = {
  "@_expressionLanguage"?: string; // from type DMN12__tImportedValues @ DMN12.xsd
  importedElement: string; // from type DMN12__tImportedValues @ DMN12.xsd
  "@_namespace": string; // from type tImport @ DMN12.xsd
  "@_locationURI"?: string; // from type tImport @ DMN12.xsd
  "@_importType": string; // from type tImport @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tArtifact = {
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tTextAnnotation = {
  "@_textFormat"?: string; // from type DMN12__tTextAnnotation @ DMN12.xsd
  text?: string; // from type DMN12__tTextAnnotation @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tAssociation = {
  "@_associationDirection"?: DMN12__tAssociationDirection; // from type DMN12__tAssociation @ DMN12.xsd
  sourceRef: DMN12__tDMNElementReference; // from type DMN12__tAssociation @ DMN12.xsd
  targetRef: DMN12__tDMNElementReference; // from type DMN12__tAssociation @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tContext = {
  contextEntry?: DMN12__tContextEntry[]; // from type DMN12__tContext @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tContextEntry = {
  variable?: DMN12__tInformationItem; // from type DMN12__tContextEntry @ DMN12.xsd
  literalExpression?: DMN12__tLiteralExpression; // from type DMN12__tContextEntry @ DMN12.xsd
  invocation?: DMN12__tInvocation; // from type DMN12__tContextEntry @ DMN12.xsd
  decisionTable?: DMN12__tDecisionTable; // from type DMN12__tContextEntry @ DMN12.xsd
  context?: DMN12__tContext; // from type DMN12__tContextEntry @ DMN12.xsd
  functionDefinition?: DMN12__tFunctionDefinition; // from type DMN12__tContextEntry @ DMN12.xsd
  relation?: DMN12__tRelation; // from type DMN12__tContextEntry @ DMN12.xsd
  list?: DMN12__tList; // from type DMN12__tContextEntry @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tFunctionDefinition = {
  "@_kind"?: DMN12__tFunctionKind; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  formalParameter?: DMN12__tInformationItem[]; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  literalExpression?: DMN12__tLiteralExpression; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  invocation?: DMN12__tInvocation; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  decisionTable?: DMN12__tDecisionTable; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  context?: DMN12__tContext; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  functionDefinition?: DMN12__tFunctionDefinition; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  relation?: DMN12__tRelation; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  list?: DMN12__tList; // from type DMN12__tFunctionDefinition @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tRelation = {
  column?: DMN12__tInformationItem[]; // from type DMN12__tRelation @ DMN12.xsd
  row?: DMN12__tList[]; // from type DMN12__tRelation @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tList = {
  literalExpression?: DMN12__tLiteralExpression[]; // from type DMN12__tList @ DMN12.xsd
  invocation?: DMN12__tInvocation[]; // from type DMN12__tList @ DMN12.xsd
  decisionTable?: DMN12__tDecisionTable[]; // from type DMN12__tList @ DMN12.xsd
  context?: DMN12__tContext[]; // from type DMN12__tList @ DMN12.xsd
  functionDefinition?: DMN12__tFunctionDefinition[]; // from type DMN12__tList @ DMN12.xsd
  relation?: DMN12__tRelation[]; // from type DMN12__tList @ DMN12.xsd
  list?: DMN12__tList[]; // from type DMN12__tList @ DMN12.xsd
  "@_typeRef"?: string; // from type tExpression @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tUnaryTests = {
  "@_expressionLanguage"?: string; // from type DMN12__tUnaryTests @ DMN12.xsd
  text: string; // from type DMN12__tUnaryTests @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMN12__tDecisionService = {
  outputDecision?: DMN12__tDMNElementReference[]; // from type DMN12__tDecisionService @ DMN12.xsd
  encapsulatedDecision?: DMN12__tDMNElementReference[]; // from type DMN12__tDecisionService @ DMN12.xsd
  inputDecision?: DMN12__tDMNElementReference[]; // from type DMN12__tDecisionService @ DMN12.xsd
  inputData?: DMN12__tDMNElementReference[]; // from type DMN12__tDecisionService @ DMN12.xsd
  variable?: DMN12__tInformationItem; // from type tInvocable @ DMN12.xsd
  "@_name": string; // from type tNamedElement @ DMN12.xsd
  "@_id"?: string; // from type tDMNElement @ DMN12.xsd
  "@_label"?: string; // from type tDMNElement @ DMN12.xsd
  description?: string; // from type tDMNElement @ DMN12.xsd
  extensionElements?: any; // from type tDMNElement @ DMN12.xsd
};

export type DMNDI12__DMNDI = {
  "dmndi:DMNDiagram"?: DMNDI12__DMNDiagram[]; // from type DMNDI12__DMNDI @ DMNDI12.xsd
  "dmndi:DMNStyle"?: DMNDI12__DMNStyle[]; // from type DMNDI12__DMNDI @ DMNDI12.xsd
};

export type DMNDI12__DMNDiagram = {
  "dmndi:Size"?: DC__Dimension; // from type DMNDI12__DMNDiagram @ DMNDI12.xsd
  "dmndi:DMNShape"?: DMNDI12__DMNShape[]; // from type DMNDI12__DMNDiagram @ DMNDI12.xsd
  "dmndi:DMNEdge"?: DMNDI12__DMNEdge[]; // from type DMNDI12__DMNDiagram @ DMNDI12.xsd
  "@_name"?: string; // from type Diagram @ DI.xsd
  "@_documentation"?: string; // from type Diagram @ DI.xsd
  "@_resolution"?: number; // from type Diagram @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI12__DMNStyle; // from type di:Style @ DI.xsd
};

export type DMNDI12__DMNShape = {
  "@_dmnElementRef": string; // from type DMNDI12__DMNShape @ DMNDI12.xsd
  "@_isListedInputData"?: boolean; // from type DMNDI12__DMNShape @ DMNDI12.xsd
  "@_isCollapsed"?: boolean; // from type DMNDI12__DMNShape @ DMNDI12.xsd
  "dmndi:DMNLabel"?: DMNDI12__DMNLabel; // from type DMNDI12__DMNShape @ DMNDI12.xsd
  "dmndi:DMNDecisionServiceDividerLine"?: DMNDI12__DMNDecisionServiceDividerLine; // from type DMNDI12__DMNShape @ DMNDI12.xsd
  "dc:Bounds"?: DC__Bounds; // from type dc:Bounds @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI12__DMNStyle; // from type di:Style @ DI.xsd
};

export type DMNDI12__DMNDecisionServiceDividerLine = {
  "di:waypoint"?: DC__Point[]; // from type Edge @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI12__DMNStyle; // from type di:Style @ DI.xsd
};

export type DMNDI12__DMNEdge = {
  "@_dmnElementRef": string; // from type DMNDI12__DMNEdge @ DMNDI12.xsd
  "dmndi:DMNLabel"?: DMNDI12__DMNLabel; // from type DMNDI12__DMNEdge @ DMNDI12.xsd
  "di:waypoint"?: DC__Point[]; // from type Edge @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI12__DMNStyle; // from type di:Style @ DI.xsd
};

export type DMNDI12__DMNLabel = {
  "dmndi:Text"?: string; // from type DMNDI12__DMNLabel @ DMNDI12.xsd
  "dc:Bounds"?: DC__Bounds; // from type dc:Bounds @ DI.xsd
  "@_sharedStyle"?: string; // from type DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
  "dmndi:DMNStyle"?: DMNDI12__DMNStyle; // from type di:Style @ DI.xsd
};

export type DMNDI12__DMNStyle = {
  "@_fontFamily"?: string; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_fontSize"?: number; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_fontItalic"?: boolean; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_fontBold"?: boolean; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_fontUnderline"?: boolean; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_fontStrikeThrough"?: boolean; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_labelHorizontalAlignement"?: DC__AlignmentKind; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_labelVerticalAlignment"?: DC__AlignmentKind; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "dmndi:FillColor"?: DC__Color; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "dmndi:StrokeColor"?: DC__Color; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "dmndi:FontColor"?: DC__Color; // from type DMNDI12__DMNStyle @ DMNDI12.xsd
  "@_id"?: string; // from type Style @ DI.xsd
  "di:extension"?: any; // from type Style @ DI.xsd
};

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
