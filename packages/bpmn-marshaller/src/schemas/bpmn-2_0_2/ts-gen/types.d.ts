// This file was automatically generated

import { BPMN20__tDefinitionsExtensionType } from "../ts-gen-extensions/BPMN20__tDefinitionsExtensionType";
import { BPMN20__tDocumentationExtensionType } from "../ts-gen-extensions/BPMN20__tDocumentationExtensionType";
import { BPMN20__tExtensionElementsExtensionType } from "../ts-gen-extensions/BPMN20__tExtensionElementsExtensionType";
import { BPMN20__tScriptExtensionType } from "../ts-gen-extensions/BPMN20__tScriptExtensionType";
import { BPMN20__tTextExtensionType } from "../ts-gen-extensions/BPMN20__tTextExtensionType";

export type BPMN20__tAdHocOrdering = "Parallel" | "Sequential";

export type BPMN20__tAssociationDirection = "None" | "One" | "Both";

export type BPMN20__tChoreographyLoopType = "None" | "Standard" | "MultiInstanceSequential" | "MultiInstanceParallel";

export type BPMN20__tEventBasedGatewayType = "Exclusive" | "Parallel";

export type BPMN20__tGatewayDirection = "Unspecified" | "Converging" | "Diverging" | "Mixed";

export type BPMN20__undefined = "##unspecified" | "##WebService";

export type BPMN20__tItemKind = "Information" | "Physical";

export type BPMN20__tMultiInstanceFlowCondition = "None" | "One" | "All" | "Complex";

export type BPMN20__tProcessType = "None" | "Public" | "Private";

export type BPMN20__tRelationshipDirection = "None" | "Forward" | "Backward" | "Both";

export type BPMN20__undefined = "##Compensate" | "##Image" | "##Store";

export type BPMNDI__ParticipantBandKind =
  | "top_initiating"
  | "middle_initiating"
  | "bottom_initiating"
  | "top_non_initiating"
  | "middle_non_initiating"
  | "bottom_non_initiating";

export type BPMNDI__MessageVisibleKind = "initiating" | "non_initiating";

export type BPMN20__tDefinitions = Partial<{ [k: `@_xmlns:${string}`]: string }> & { "@_xmlns"?: string } & {
  "@_id"?: string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "@_name"?: string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "@_targetNamespace": string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "@_expressionLanguage"?: string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "@_typeLanguage"?: string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "@_exporter"?: string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "@_exporterVersion"?: string; // from type BPMN20__tDefinitions @ BPMN20.xsd
  import?: BPMN20__tImport[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  extension?: BPMN20__tExtension[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  category?: BPMN20__tCategory[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalChoreographyTask?: BPMN20__tGlobalChoreographyTask[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalConversation?: BPMN20__tGlobalConversation[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  correlationProperty?: BPMN20__tCorrelationProperty[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  dataStore?: BPMN20__tDataStore[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  endPoint?: BPMN20__tEndPoint[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  error?: BPMN20__tError[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  escalation?: BPMN20__tEscalation[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalBusinessRuleTask?: BPMN20__tGlobalBusinessRuleTask[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalManualTask?: BPMN20__tGlobalManualTask[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalScriptTask?: BPMN20__tGlobalScriptTask[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalTask?: BPMN20__tGlobalTask[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  globalUserTask?: BPMN20__tGlobalUserTask[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  interface?: BPMN20__tInterface[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  itemDefinition?: BPMN20__tItemDefinition[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  message?: BPMN20__tMessage[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  partnerEntity?: BPMN20__tPartnerEntity[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  partnerRole?: BPMN20__tPartnerRole[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  process?: BPMN20__tProcess[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  resource?: BPMN20__tResource[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  signal?: BPMN20__tSignal[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  "bpmndi:BPMNDiagram"?: BPMNDI__BPMNDiagram[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
  relationship?: BPMN20__tRelationship[]; // from type BPMN20__tDefinitions @ BPMN20.xsd
} & BPMN20__tDefinitionsExtensionType;

export type BPMN20__tImport = {
  "@_namespace": string; // from type BPMN20__tImport @ BPMN20.xsd
  "@_location": string; // from type BPMN20__tImport @ BPMN20.xsd
  "@_importType": string; // from type BPMN20__tImport @ BPMN20.xsd
};

export type BPMN20__tAdHocSubProcess = {
  "@_cancelRemainingInstances"?: boolean; // from type BPMN20__tAdHocSubProcess @ BPMN20.xsd
  "@_ordering"?: BPMN20__tAdHocOrdering; // from type BPMN20__tAdHocSubProcess @ BPMN20.xsd
  completionCondition?: BPMN20__tExpression; // from type BPMN20__tAdHocSubProcess @ BPMN20.xsd
  "@_triggeredByEvent"?: boolean; // from type tSubProcess @ BPMN20.xsd
  laneSet?: BPMN20__tLaneSet[]; // from type tLaneSet @ BPMN20.xsd
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type tFlowElement @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type tFlowElement @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type tFlowElement @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type tFlowElement @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type tFlowElement @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type tFlowElement @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type tFlowElement @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type tFlowElement @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type tFlowElement @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type tFlowElement @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type tFlowElement @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type tFlowElement @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type tFlowElement @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type tFlowElement @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type tFlowElement @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type tFlowElement @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type tFlowElement @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type tFlowElement @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type tFlowElement @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type tFlowElement @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type tFlowElement @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type tFlowElement @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type tFlowElement @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type tFlowElement @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type tFlowElement @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type tFlowElement @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type tFlowElement @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type tFlowElement @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type tFlowElement @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type tFlowElement @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type tFlowElement @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type tArtifact @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type tArtifact @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type tArtifact @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tAssignment = {
  from: BPMN20__tExpression; // from type BPMN20__tAssignment @ BPMN20.xsd
  to: BPMN20__tExpression; // from type BPMN20__tAssignment @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tAssociation = {
  "@_sourceRef": string; // from type BPMN20__tAssociation @ BPMN20.xsd
  "@_targetRef": string; // from type BPMN20__tAssociation @ BPMN20.xsd
  "@_associationDirection"?: BPMN20__tAssociationDirection; // from type BPMN20__tAssociation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tAuditing = {
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tBoundaryEvent = {
  "@_cancelActivity"?: boolean; // from type BPMN20__tBoundaryEvent @ BPMN20.xsd
  "@_attachedToRef": string; // from type BPMN20__tBoundaryEvent @ BPMN20.xsd
  "@_parallelMultiple"?: boolean; // from type tCatchEvent @ BPMN20.xsd
  dataOutput?: BPMN20__tDataOutput[]; // from type tDataOutput @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  outputSet?: BPMN20__tOutputSet; // from type tOutputSet @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  eventDefinitionRef?: string[]; // from type tCatchEvent @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tBusinessRuleTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tBusinessRuleTask @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCallableElement = {
  "@_name"?: string; // from type BPMN20__tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type BPMN20__tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type BPMN20__tCallableElement @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type BPMN20__tCallableElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCallActivity = {
  "@_calledElement"?: string; // from type BPMN20__tCallActivity @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCallChoreography = {
  "@_calledChoreographyRef"?: string; // from type BPMN20__tCallChoreography @ BPMN20.xsd
  participantAssociation?: BPMN20__tParticipantAssociation[]; // from type BPMN20__tCallChoreography @ BPMN20.xsd
  "@_initiatingParticipantRef": string; // from type tChoreographyActivity @ BPMN20.xsd
  "@_loopType"?: BPMN20__tChoreographyLoopType; // from type tChoreographyActivity @ BPMN20.xsd
  participantRef: string[]; // from type tChoreographyActivity @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCallConversation = {
  "@_calledCollaborationRef"?: string; // from type BPMN20__tCallConversation @ BPMN20.xsd
  participantAssociation?: BPMN20__tParticipantAssociation[]; // from type BPMN20__tCallConversation @ BPMN20.xsd
  "@_name"?: string; // from type tConversationNode @ BPMN20.xsd
  participantRef?: string[]; // from type tConversationNode @ BPMN20.xsd
  messageFlowRef?: string[]; // from type tConversationNode @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCancelEventDefinition = {
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCategory = {
  "@_name"?: string; // from type BPMN20__tCategory @ BPMN20.xsd
  categoryValue?: BPMN20__tCategoryValue[]; // from type BPMN20__tCategory @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCategoryValue = {
  "@_value"?: string; // from type BPMN20__tCategoryValue @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tChoreography = {
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type BPMN20__tChoreography @ BPMN20.xsd
  "@_name"?: string; // from type tCollaboration @ BPMN20.xsd
  "@_isClosed"?: boolean; // from type tCollaboration @ BPMN20.xsd
  participant?: BPMN20__tParticipant[]; // from type tParticipant @ BPMN20.xsd
  messageFlow?: BPMN20__tMessageFlow[]; // from type tMessageFlow @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type tArtifact @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type tArtifact @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type tArtifact @ BPMN20.xsd
  callConversation?: BPMN20__tCallConversation[]; // from type tConversationNode @ BPMN20.xsd
  conversation?: BPMN20__tConversation[]; // from type tConversationNode @ BPMN20.xsd
  subConversation?: BPMN20__tSubConversation[]; // from type tConversationNode @ BPMN20.xsd
  conversationAssociation?: BPMN20__tConversationAssociation[]; // from type tConversationAssociation @ BPMN20.xsd
  participantAssociation?: BPMN20__tParticipantAssociation[]; // from type tParticipantAssociation @ BPMN20.xsd
  messageFlowAssociation?: BPMN20__tMessageFlowAssociation[]; // from type tMessageFlowAssociation @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  choreographyRef?: string[]; // from type tCollaboration @ BPMN20.xsd
  conversationLink?: BPMN20__tConversationLink[]; // from type tConversationLink @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tChoreographyTask = {
  messageFlowRef: string[]; // from type BPMN20__tChoreographyTask @ BPMN20.xsd
  "@_initiatingParticipantRef": string; // from type tChoreographyActivity @ BPMN20.xsd
  "@_loopType"?: BPMN20__tChoreographyLoopType; // from type tChoreographyActivity @ BPMN20.xsd
  participantRef: string[]; // from type tChoreographyActivity @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCollaboration = {
  "@_name"?: string; // from type BPMN20__tCollaboration @ BPMN20.xsd
  "@_isClosed"?: boolean; // from type BPMN20__tCollaboration @ BPMN20.xsd
  participant?: BPMN20__tParticipant[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  messageFlow?: BPMN20__tMessageFlow[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  callConversation?: BPMN20__tCallConversation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  conversation?: BPMN20__tConversation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  subConversation?: BPMN20__tSubConversation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  conversationAssociation?: BPMN20__tConversationAssociation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  participantAssociation?: BPMN20__tParticipantAssociation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  messageFlowAssociation?: BPMN20__tMessageFlowAssociation[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  choreographyRef?: string[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  conversationLink?: BPMN20__tConversationLink[]; // from type BPMN20__tCollaboration @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCompensateEventDefinition = {
  "@_waitForCompletion"?: boolean; // from type BPMN20__tCompensateEventDefinition @ BPMN20.xsd
  "@_activityRef"?: string; // from type BPMN20__tCompensateEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tComplexBehaviorDefinition = {
  condition: BPMN20__tFormalExpression; // from type BPMN20__tComplexBehaviorDefinition @ BPMN20.xsd
  event?: BPMN20__tImplicitThrowEvent; // from type BPMN20__tComplexBehaviorDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tComplexGateway = {
  "@_default"?: string; // from type BPMN20__tComplexGateway @ BPMN20.xsd
  activationCondition?: BPMN20__tExpression; // from type BPMN20__tComplexGateway @ BPMN20.xsd
  "@_gatewayDirection"?: BPMN20__tGatewayDirection; // from type tGateway @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tConditionalEventDefinition = {
  condition: BPMN20__tExpression; // from type BPMN20__tConditionalEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tConversation = {
  "@_name"?: string; // from type tConversationNode @ BPMN20.xsd
  participantRef?: string[]; // from type tConversationNode @ BPMN20.xsd
  messageFlowRef?: string[]; // from type tConversationNode @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tConversationAssociation = {
  "@_innerConversationNodeRef": string; // from type BPMN20__tConversationAssociation @ BPMN20.xsd
  "@_outerConversationNodeRef": string; // from type BPMN20__tConversationAssociation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tConversationLink = {
  "@_name"?: string; // from type BPMN20__tConversationLink @ BPMN20.xsd
  "@_sourceRef": string; // from type BPMN20__tConversationLink @ BPMN20.xsd
  "@_targetRef": string; // from type BPMN20__tConversationLink @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCorrelationKey = {
  "@_name"?: string; // from type BPMN20__tCorrelationKey @ BPMN20.xsd
  correlationPropertyRef?: string[]; // from type BPMN20__tCorrelationKey @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCorrelationProperty = {
  "@_name"?: string; // from type BPMN20__tCorrelationProperty @ BPMN20.xsd
  "@_type"?: string; // from type BPMN20__tCorrelationProperty @ BPMN20.xsd
  correlationPropertyRetrievalExpression: BPMN20__tCorrelationPropertyRetrievalExpression[]; // from type BPMN20__tCorrelationProperty @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCorrelationPropertyBinding = {
  "@_correlationPropertyRef": string; // from type BPMN20__tCorrelationPropertyBinding @ BPMN20.xsd
  dataPath: BPMN20__tFormalExpression; // from type BPMN20__tCorrelationPropertyBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCorrelationPropertyRetrievalExpression = {
  "@_messageRef": string; // from type BPMN20__tCorrelationPropertyRetrievalExpression @ BPMN20.xsd
  messagePath: BPMN20__tFormalExpression; // from type BPMN20__tCorrelationPropertyRetrievalExpression @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tCorrelationSubscription = {
  "@_correlationKeyRef": string; // from type BPMN20__tCorrelationSubscription @ BPMN20.xsd
  correlationPropertyBinding?: BPMN20__tCorrelationPropertyBinding[]; // from type BPMN20__tCorrelationSubscription @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataAssociation = {
  sourceRef?: string[]; // from type BPMN20__tDataAssociation @ BPMN20.xsd
  targetRef: string; // from type BPMN20__tDataAssociation @ BPMN20.xsd
  transformation?: BPMN20__tFormalExpression; // from type BPMN20__tDataAssociation @ BPMN20.xsd
  assignment?: BPMN20__tAssignment[]; // from type BPMN20__tDataAssociation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataInput = {
  "@_name"?: string; // from type BPMN20__tDataInput @ BPMN20.xsd
  "@_itemSubjectRef"?: string; // from type BPMN20__tDataInput @ BPMN20.xsd
  "@_isCollection"?: boolean; // from type BPMN20__tDataInput @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tDataInput @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataInputAssociation = {
  sourceRef?: string[]; // from type tDataAssociation @ BPMN20.xsd
  targetRef: string; // from type tDataAssociation @ BPMN20.xsd
  transformation?: BPMN20__tFormalExpression; // from type tDataAssociation @ BPMN20.xsd
  assignment?: BPMN20__tAssignment[]; // from type tAssignment @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataObject = {
  "@_itemSubjectRef"?: string; // from type BPMN20__tDataObject @ BPMN20.xsd
  "@_isCollection"?: boolean; // from type BPMN20__tDataObject @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tDataObject @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataObjectReference = {
  "@_itemSubjectRef"?: string; // from type BPMN20__tDataObjectReference @ BPMN20.xsd
  "@_dataObjectRef"?: string; // from type BPMN20__tDataObjectReference @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tDataObjectReference @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataOutput = {
  "@_name"?: string; // from type BPMN20__tDataOutput @ BPMN20.xsd
  "@_itemSubjectRef"?: string; // from type BPMN20__tDataOutput @ BPMN20.xsd
  "@_isCollection"?: boolean; // from type BPMN20__tDataOutput @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tDataOutput @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataOutputAssociation = {
  sourceRef?: string[]; // from type tDataAssociation @ BPMN20.xsd
  targetRef: string; // from type tDataAssociation @ BPMN20.xsd
  transformation?: BPMN20__tFormalExpression; // from type tDataAssociation @ BPMN20.xsd
  assignment?: BPMN20__tAssignment[]; // from type tAssignment @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataState = {
  "@_name"?: string; // from type BPMN20__tDataState @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataStore = {
  "@_name"?: string; // from type BPMN20__tDataStore @ BPMN20.xsd
  "@_capacity"?: number; // from type BPMN20__tDataStore @ BPMN20.xsd
  "@_isUnlimited"?: boolean; // from type BPMN20__tDataStore @ BPMN20.xsd
  "@_itemSubjectRef"?: string; // from type BPMN20__tDataStore @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tDataStore @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDataStoreReference = {
  "@_itemSubjectRef"?: string; // from type BPMN20__tDataStoreReference @ BPMN20.xsd
  "@_dataStoreRef"?: string; // from type BPMN20__tDataStoreReference @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tDataStoreReference @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tDocumentation = {
  "@_id"?: string; // from type BPMN20__tDocumentation @ BPMN20.xsd
  "@_textFormat"?: string; // from type BPMN20__tDocumentation @ BPMN20.xsd
} & BPMN20__tDocumentationExtensionType;

export type BPMN20__tEndEvent = {
  dataInput?: BPMN20__tDataInput[]; // from type tDataInput @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  inputSet?: BPMN20__tInputSet; // from type tInputSet @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  eventDefinitionRef?: string[]; // from type tThrowEvent @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tEndPoint = {
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tError = {
  "@_name"?: string; // from type BPMN20__tError @ BPMN20.xsd
  "@_errorCode"?: string; // from type BPMN20__tError @ BPMN20.xsd
  "@_structureRef"?: string; // from type BPMN20__tError @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tErrorEventDefinition = {
  "@_errorRef"?: string; // from type BPMN20__tErrorEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tEscalation = {
  "@_name"?: string; // from type BPMN20__tEscalation @ BPMN20.xsd
  "@_escalationCode"?: string; // from type BPMN20__tEscalation @ BPMN20.xsd
  "@_structureRef"?: string; // from type BPMN20__tEscalation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tEscalationEventDefinition = {
  "@_escalationRef"?: string; // from type BPMN20__tEscalationEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tEventBasedGateway = {
  "@_instantiate"?: boolean; // from type BPMN20__tEventBasedGateway @ BPMN20.xsd
  "@_eventGatewayType"?: BPMN20__tEventBasedGatewayType; // from type BPMN20__tEventBasedGateway @ BPMN20.xsd
  "@_gatewayDirection"?: BPMN20__tGatewayDirection; // from type tGateway @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tExclusiveGateway = {
  "@_default"?: string; // from type BPMN20__tExclusiveGateway @ BPMN20.xsd
  "@_gatewayDirection"?: BPMN20__tGatewayDirection; // from type tGateway @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tExpression = {
  "@_id"?: string; // from type tBaseElementWithMixedContent @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tExtension = {
  "@_definition"?: string; // from type BPMN20__tExtension @ BPMN20.xsd
  "@_mustUnderstand"?: boolean; // from type BPMN20__tExtension @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type BPMN20__tExtension @ BPMN20.xsd
};

export type BPMN20__tExtensionElements = {} & BPMN20__tExtensionElementsExtensionType;

export type BPMN20__tFormalExpression = {
  "@_language"?: string; // from type BPMN20__tFormalExpression @ BPMN20.xsd
  "@_evaluatesToTypeRef"?: string; // from type BPMN20__tFormalExpression @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElementWithMixedContent @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGateway = {
  "@_gatewayDirection"?: BPMN20__tGatewayDirection; // from type BPMN20__tGateway @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalBusinessRuleTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tGlobalBusinessRuleTask @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  "@_name"?: string; // from type tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalChoreographyTask = {
  "@_initiatingParticipantRef"?: string; // from type BPMN20__tGlobalChoreographyTask @ BPMN20.xsd
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type tFlowElement @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type tFlowElement @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type tFlowElement @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type tFlowElement @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type tFlowElement @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type tFlowElement @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type tFlowElement @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type tFlowElement @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type tFlowElement @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type tFlowElement @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type tFlowElement @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type tFlowElement @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type tFlowElement @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type tFlowElement @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type tFlowElement @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type tFlowElement @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type tFlowElement @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type tFlowElement @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type tFlowElement @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type tFlowElement @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type tFlowElement @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type tFlowElement @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type tFlowElement @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type tFlowElement @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type tFlowElement @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type tFlowElement @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type tFlowElement @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type tFlowElement @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type tFlowElement @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type tFlowElement @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type tFlowElement @ BPMN20.xsd
  "@_name"?: string; // from type tCollaboration @ BPMN20.xsd
  "@_isClosed"?: boolean; // from type tCollaboration @ BPMN20.xsd
  participant?: BPMN20__tParticipant[]; // from type tParticipant @ BPMN20.xsd
  messageFlow?: BPMN20__tMessageFlow[]; // from type tMessageFlow @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type tArtifact @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type tArtifact @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type tArtifact @ BPMN20.xsd
  callConversation?: BPMN20__tCallConversation[]; // from type tConversationNode @ BPMN20.xsd
  conversation?: BPMN20__tConversation[]; // from type tConversationNode @ BPMN20.xsd
  subConversation?: BPMN20__tSubConversation[]; // from type tConversationNode @ BPMN20.xsd
  conversationAssociation?: BPMN20__tConversationAssociation[]; // from type tConversationAssociation @ BPMN20.xsd
  participantAssociation?: BPMN20__tParticipantAssociation[]; // from type tParticipantAssociation @ BPMN20.xsd
  messageFlowAssociation?: BPMN20__tMessageFlowAssociation[]; // from type tMessageFlowAssociation @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  choreographyRef?: string[]; // from type tCollaboration @ BPMN20.xsd
  conversationLink?: BPMN20__tConversationLink[]; // from type tConversationLink @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalConversation = {
  "@_name"?: string; // from type tCollaboration @ BPMN20.xsd
  "@_isClosed"?: boolean; // from type tCollaboration @ BPMN20.xsd
  participant?: BPMN20__tParticipant[]; // from type tParticipant @ BPMN20.xsd
  messageFlow?: BPMN20__tMessageFlow[]; // from type tMessageFlow @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type tArtifact @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type tArtifact @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type tArtifact @ BPMN20.xsd
  callConversation?: BPMN20__tCallConversation[]; // from type tConversationNode @ BPMN20.xsd
  conversation?: BPMN20__tConversation[]; // from type tConversationNode @ BPMN20.xsd
  subConversation?: BPMN20__tSubConversation[]; // from type tConversationNode @ BPMN20.xsd
  conversationAssociation?: BPMN20__tConversationAssociation[]; // from type tConversationAssociation @ BPMN20.xsd
  participantAssociation?: BPMN20__tParticipantAssociation[]; // from type tParticipantAssociation @ BPMN20.xsd
  messageFlowAssociation?: BPMN20__tMessageFlowAssociation[]; // from type tMessageFlowAssociation @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  choreographyRef?: string[]; // from type tCollaboration @ BPMN20.xsd
  conversationLink?: BPMN20__tConversationLink[]; // from type tConversationLink @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalManualTask = {
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  "@_name"?: string; // from type tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalScriptTask = {
  "@_scriptLanguage"?: string; // from type BPMN20__tGlobalScriptTask @ BPMN20.xsd
  script?: BPMN20__tScript; // from type BPMN20__tGlobalScriptTask @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  "@_name"?: string; // from type tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalTask = {
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type BPMN20__tGlobalTask @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type BPMN20__tGlobalTask @ BPMN20.xsd
  "@_name"?: string; // from type tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGlobalUserTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tGlobalUserTask @ BPMN20.xsd
  rendering?: BPMN20__tRendering[]; // from type BPMN20__tGlobalUserTask @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  "@_name"?: string; // from type tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tGroup = {
  "@_categoryValueRef"?: string; // from type BPMN20__tGroup @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tHumanPerformer = {
  "@_name"?: string; // from type tResourceRole @ BPMN20.xsd
  resourceAssignmentExpression?: BPMN20__tResourceAssignmentExpression; // from type tResourceAssignmentExpression @ BPMN20.xsd
  resourceRef?: string; // from type tResourceRole @ BPMN20.xsd
  resourceParameterBinding?: BPMN20__tResourceParameterBinding[]; // from type tResourceParameterBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tImplicitThrowEvent = {
  dataInput?: BPMN20__tDataInput[]; // from type tDataInput @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  inputSet?: BPMN20__tInputSet; // from type tInputSet @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  eventDefinitionRef?: string[]; // from type tThrowEvent @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tInclusiveGateway = {
  "@_default"?: string; // from type BPMN20__tInclusiveGateway @ BPMN20.xsd
  "@_gatewayDirection"?: BPMN20__tGatewayDirection; // from type tGateway @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tInputSet = {
  "@_name"?: string; // from type BPMN20__tInputSet @ BPMN20.xsd
  dataInputRefs?: string[]; // from type BPMN20__tInputSet @ BPMN20.xsd
  optionalInputRefs?: string[]; // from type BPMN20__tInputSet @ BPMN20.xsd
  whileExecutingInputRefs?: string[]; // from type BPMN20__tInputSet @ BPMN20.xsd
  outputSetRefs?: string[]; // from type BPMN20__tInputSet @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tInterface = {
  "@_name": string; // from type BPMN20__tInterface @ BPMN20.xsd
  "@_implementationRef"?: string; // from type BPMN20__tInterface @ BPMN20.xsd
  operation: BPMN20__tOperation[]; // from type BPMN20__tInterface @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tIntermediateCatchEvent = {
  "@_parallelMultiple"?: boolean; // from type tCatchEvent @ BPMN20.xsd
  dataOutput?: BPMN20__tDataOutput[]; // from type tDataOutput @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  outputSet?: BPMN20__tOutputSet; // from type tOutputSet @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  eventDefinitionRef?: string[]; // from type tCatchEvent @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tIntermediateThrowEvent = {
  dataInput?: BPMN20__tDataInput[]; // from type tDataInput @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  inputSet?: BPMN20__tInputSet; // from type tInputSet @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  eventDefinitionRef?: string[]; // from type tThrowEvent @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tInputOutputBinding = {
  "@_operationRef": string; // from type BPMN20__tInputOutputBinding @ BPMN20.xsd
  "@_inputDataRef": string; // from type BPMN20__tInputOutputBinding @ BPMN20.xsd
  "@_outputDataRef": string; // from type BPMN20__tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tInputOutputSpecification = {
  dataInput?: BPMN20__tDataInput[]; // from type BPMN20__tInputOutputSpecification @ BPMN20.xsd
  dataOutput?: BPMN20__tDataOutput[]; // from type BPMN20__tInputOutputSpecification @ BPMN20.xsd
  inputSet: BPMN20__tInputSet[]; // from type BPMN20__tInputOutputSpecification @ BPMN20.xsd
  outputSet: BPMN20__tOutputSet[]; // from type BPMN20__tInputOutputSpecification @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tItemDefinition = {
  "@_structureRef"?: string; // from type BPMN20__tItemDefinition @ BPMN20.xsd
  "@_isCollection"?: boolean; // from type BPMN20__tItemDefinition @ BPMN20.xsd
  "@_itemKind"?: BPMN20__tItemKind; // from type BPMN20__tItemDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tLane = {
  "@_name"?: string; // from type BPMN20__tLane @ BPMN20.xsd
  "@_partitionElementRef"?: string; // from type BPMN20__tLane @ BPMN20.xsd
  partitionElement?: BPMN20__tBaseElement; // from type BPMN20__tLane @ BPMN20.xsd
  flowNodeRef?: string[]; // from type BPMN20__tLane @ BPMN20.xsd
  childLaneSet?: BPMN20__tLaneSet; // from type BPMN20__tLane @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tLaneSet = {
  "@_name"?: string; // from type BPMN20__tLaneSet @ BPMN20.xsd
  lane?: BPMN20__tLane[]; // from type BPMN20__tLaneSet @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tLinkEventDefinition = {
  "@_name": string; // from type BPMN20__tLinkEventDefinition @ BPMN20.xsd
  source?: string[]; // from type BPMN20__tLinkEventDefinition @ BPMN20.xsd
  target?: string; // from type BPMN20__tLinkEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tManualTask = {
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tMessage = {
  "@_name"?: string; // from type BPMN20__tMessage @ BPMN20.xsd
  "@_itemRef"?: string; // from type BPMN20__tMessage @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tMessageEventDefinition = {
  "@_messageRef"?: string; // from type BPMN20__tMessageEventDefinition @ BPMN20.xsd
  operationRef?: string; // from type BPMN20__tMessageEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tMessageFlow = {
  "@_name"?: string; // from type BPMN20__tMessageFlow @ BPMN20.xsd
  "@_sourceRef": string; // from type BPMN20__tMessageFlow @ BPMN20.xsd
  "@_targetRef": string; // from type BPMN20__tMessageFlow @ BPMN20.xsd
  "@_messageRef"?: string; // from type BPMN20__tMessageFlow @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tMessageFlowAssociation = {
  "@_innerMessageFlowRef": string; // from type BPMN20__tMessageFlowAssociation @ BPMN20.xsd
  "@_outerMessageFlowRef": string; // from type BPMN20__tMessageFlowAssociation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tMonitoring = {
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tMultiInstanceLoopCharacteristics = {
  "@_isSequential"?: boolean; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  "@_behavior"?: BPMN20__tMultiInstanceFlowCondition; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  "@_oneBehaviorEventRef"?: string; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  "@_noneBehaviorEventRef"?: string; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  loopCardinality?: BPMN20__tExpression; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  loopDataInputRef?: string; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  loopDataOutputRef?: string; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  inputDataItem?: BPMN20__tDataInput; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  outputDataItem?: BPMN20__tDataOutput; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  complexBehaviorDefinition?: BPMN20__tComplexBehaviorDefinition[]; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  completionCondition?: BPMN20__tExpression; // from type BPMN20__tMultiInstanceLoopCharacteristics @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tOperation = {
  "@_name": string; // from type BPMN20__tOperation @ BPMN20.xsd
  "@_implementationRef"?: string; // from type BPMN20__tOperation @ BPMN20.xsd
  inMessageRef: string; // from type BPMN20__tOperation @ BPMN20.xsd
  outMessageRef?: string; // from type BPMN20__tOperation @ BPMN20.xsd
  errorRef?: string[]; // from type BPMN20__tOperation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tOutputSet = {
  "@_name"?: string; // from type BPMN20__tOutputSet @ BPMN20.xsd
  dataOutputRefs?: string[]; // from type BPMN20__tOutputSet @ BPMN20.xsd
  optionalOutputRefs?: string[]; // from type BPMN20__tOutputSet @ BPMN20.xsd
  whileExecutingOutputRefs?: string[]; // from type BPMN20__tOutputSet @ BPMN20.xsd
  inputSetRefs?: string[]; // from type BPMN20__tOutputSet @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tParallelGateway = {
  "@_gatewayDirection"?: BPMN20__tGatewayDirection; // from type tGateway @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tParticipant = {
  "@_name"?: string; // from type BPMN20__tParticipant @ BPMN20.xsd
  "@_processRef"?: string; // from type BPMN20__tParticipant @ BPMN20.xsd
  interfaceRef?: string[]; // from type BPMN20__tParticipant @ BPMN20.xsd
  endPointRef?: string[]; // from type BPMN20__tParticipant @ BPMN20.xsd
  participantMultiplicity?: BPMN20__tParticipantMultiplicity; // from type BPMN20__tParticipant @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tParticipantAssociation = {
  innerParticipantRef: string; // from type BPMN20__tParticipantAssociation @ BPMN20.xsd
  outerParticipantRef: string; // from type BPMN20__tParticipantAssociation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tParticipantMultiplicity = {
  "@_minimum"?: number; // from type BPMN20__tParticipantMultiplicity @ BPMN20.xsd
  "@_maximum"?: number; // from type BPMN20__tParticipantMultiplicity @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tPartnerEntity = {
  "@_name"?: string; // from type BPMN20__tPartnerEntity @ BPMN20.xsd
  participantRef?: string[]; // from type BPMN20__tPartnerEntity @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tPartnerRole = {
  "@_name"?: string; // from type BPMN20__tPartnerRole @ BPMN20.xsd
  participantRef?: string[]; // from type BPMN20__tPartnerRole @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tPerformer = {
  "@_name"?: string; // from type tResourceRole @ BPMN20.xsd
  resourceAssignmentExpression?: BPMN20__tResourceAssignmentExpression; // from type tResourceAssignmentExpression @ BPMN20.xsd
  resourceRef?: string; // from type tResourceRole @ BPMN20.xsd
  resourceParameterBinding?: BPMN20__tResourceParameterBinding[]; // from type tResourceParameterBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tPotentialOwner = {
  "@_name"?: string; // from type tResourceRole @ BPMN20.xsd
  resourceAssignmentExpression?: BPMN20__tResourceAssignmentExpression; // from type tResourceAssignmentExpression @ BPMN20.xsd
  resourceRef?: string; // from type tResourceRole @ BPMN20.xsd
  resourceParameterBinding?: BPMN20__tResourceParameterBinding[]; // from type tResourceParameterBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tProcess = {
  "@_processType"?: BPMN20__tProcessType; // from type BPMN20__tProcess @ BPMN20.xsd
  "@_isClosed"?: boolean; // from type BPMN20__tProcess @ BPMN20.xsd
  "@_isExecutable"?: boolean; // from type BPMN20__tProcess @ BPMN20.xsd
  "@_definitionalCollaborationRef"?: string; // from type BPMN20__tProcess @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type BPMN20__tProcess @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type BPMN20__tProcess @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type BPMN20__tProcess @ BPMN20.xsd
  laneSet?: BPMN20__tLaneSet[]; // from type BPMN20__tProcess @ BPMN20.xsd
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type BPMN20__tProcess @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type BPMN20__tProcess @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type BPMN20__tProcess @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type BPMN20__tProcess @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type BPMN20__tProcess @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type BPMN20__tProcess @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type BPMN20__tProcess @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type BPMN20__tProcess @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type BPMN20__tProcess @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type BPMN20__tProcess @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type BPMN20__tProcess @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type BPMN20__tProcess @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type BPMN20__tProcess @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type BPMN20__tProcess @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type BPMN20__tProcess @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type BPMN20__tProcess @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type BPMN20__tProcess @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type BPMN20__tProcess @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type BPMN20__tProcess @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type BPMN20__tProcess @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type BPMN20__tProcess @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type BPMN20__tProcess @ BPMN20.xsd
  correlationSubscription?: BPMN20__tCorrelationSubscription[]; // from type BPMN20__tProcess @ BPMN20.xsd
  supports?: string[]; // from type BPMN20__tProcess @ BPMN20.xsd
  "@_name"?: string; // from type tCallableElement @ BPMN20.xsd
  supportedInterfaceRef?: string[]; // from type tCallableElement @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  ioBinding?: BPMN20__tInputOutputBinding[]; // from type tInputOutputBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tProperty = {
  "@_name"?: string; // from type BPMN20__tProperty @ BPMN20.xsd
  "@_itemSubjectRef"?: string; // from type BPMN20__tProperty @ BPMN20.xsd
  dataState?: BPMN20__tDataState; // from type BPMN20__tProperty @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tReceiveTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tReceiveTask @ BPMN20.xsd
  "@_instantiate"?: boolean; // from type BPMN20__tReceiveTask @ BPMN20.xsd
  "@_messageRef"?: string; // from type BPMN20__tReceiveTask @ BPMN20.xsd
  "@_operationRef"?: string; // from type BPMN20__tReceiveTask @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tRelationship = {
  "@_type": string; // from type BPMN20__tRelationship @ BPMN20.xsd
  "@_direction"?: BPMN20__tRelationshipDirection; // from type BPMN20__tRelationship @ BPMN20.xsd
  source: string[]; // from type BPMN20__tRelationship @ BPMN20.xsd
  target: string[]; // from type BPMN20__tRelationship @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tRendering = {
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tResource = {
  "@_name": string; // from type BPMN20__tResource @ BPMN20.xsd
  resourceParameter?: BPMN20__tResourceParameter[]; // from type BPMN20__tResource @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tResourceAssignmentExpression = {
  formalExpression: BPMN20__tFormalExpression; // from type BPMN20__tResourceAssignmentExpression @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tResourceParameter = {
  "@_name"?: string; // from type BPMN20__tResourceParameter @ BPMN20.xsd
  "@_type"?: string; // from type BPMN20__tResourceParameter @ BPMN20.xsd
  "@_isRequired"?: boolean; // from type BPMN20__tResourceParameter @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tResourceParameterBinding = {
  "@_parameterRef": string; // from type BPMN20__tResourceParameterBinding @ BPMN20.xsd
  formalExpression: BPMN20__tFormalExpression; // from type BPMN20__tResourceParameterBinding @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tResourceRole = {
  "@_name"?: string; // from type BPMN20__tResourceRole @ BPMN20.xsd
  resourceAssignmentExpression?: BPMN20__tResourceAssignmentExpression; // from type BPMN20__tResourceRole @ BPMN20.xsd
  resourceRef?: string; // from type BPMN20__tResourceRole @ BPMN20.xsd
  resourceParameterBinding?: BPMN20__tResourceParameterBinding[]; // from type BPMN20__tResourceRole @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tScriptTask = {
  "@_scriptFormat"?: string; // from type BPMN20__tScriptTask @ BPMN20.xsd
  script?: BPMN20__tScript; // from type BPMN20__tScriptTask @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tScript = {} & BPMN20__tScriptExtensionType;

export type BPMN20__tSendTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tSendTask @ BPMN20.xsd
  "@_messageRef"?: string; // from type BPMN20__tSendTask @ BPMN20.xsd
  "@_operationRef"?: string; // from type BPMN20__tSendTask @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tSequenceFlow = {
  "@_sourceRef": string; // from type BPMN20__tSequenceFlow @ BPMN20.xsd
  "@_targetRef": string; // from type BPMN20__tSequenceFlow @ BPMN20.xsd
  "@_isImmediate"?: boolean; // from type BPMN20__tSequenceFlow @ BPMN20.xsd
  conditionExpression?: BPMN20__tExpression; // from type BPMN20__tSequenceFlow @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tServiceTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tServiceTask @ BPMN20.xsd
  "@_operationRef"?: string; // from type BPMN20__tServiceTask @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tSignal = {
  "@_name"?: string; // from type BPMN20__tSignal @ BPMN20.xsd
  "@_structureRef"?: string; // from type BPMN20__tSignal @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tSignalEventDefinition = {
  "@_signalRef"?: string; // from type BPMN20__tSignalEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tStandardLoopCharacteristics = {
  "@_testBefore"?: boolean; // from type BPMN20__tStandardLoopCharacteristics @ BPMN20.xsd
  "@_loopMaximum"?: number; // from type BPMN20__tStandardLoopCharacteristics @ BPMN20.xsd
  loopCondition?: BPMN20__tExpression; // from type BPMN20__tStandardLoopCharacteristics @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tStartEvent = {
  "@_isInterrupting"?: boolean; // from type BPMN20__tStartEvent @ BPMN20.xsd
  "@_parallelMultiple"?: boolean; // from type tCatchEvent @ BPMN20.xsd
  dataOutput?: BPMN20__tDataOutput[]; // from type tDataOutput @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  outputSet?: BPMN20__tOutputSet; // from type tOutputSet @ BPMN20.xsd
  cancelEventDefinition?: BPMN20__tCancelEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  compensateEventDefinition?: BPMN20__tCompensateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  conditionalEventDefinition?: BPMN20__tConditionalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  errorEventDefinition?: BPMN20__tErrorEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  escalationEventDefinition?: BPMN20__tEscalationEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  linkEventDefinition?: BPMN20__tLinkEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  messageEventDefinition?: BPMN20__tMessageEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  signalEventDefinition?: BPMN20__tSignalEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  terminateEventDefinition?: BPMN20__tTerminateEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  timerEventDefinition?: BPMN20__tTimerEventDefinition[]; // from type tEventDefinition @ BPMN20.xsd
  eventDefinitionRef?: string[]; // from type tCatchEvent @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tSubChoreography = {
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type BPMN20__tSubChoreography @ BPMN20.xsd
  "@_initiatingParticipantRef": string; // from type tChoreographyActivity @ BPMN20.xsd
  "@_loopType"?: BPMN20__tChoreographyLoopType; // from type tChoreographyActivity @ BPMN20.xsd
  participantRef: string[]; // from type tChoreographyActivity @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tSubConversation = {
  callConversation?: BPMN20__tCallConversation[]; // from type BPMN20__tSubConversation @ BPMN20.xsd
  conversation?: BPMN20__tConversation[]; // from type BPMN20__tSubConversation @ BPMN20.xsd
  subConversation?: BPMN20__tSubConversation[]; // from type BPMN20__tSubConversation @ BPMN20.xsd
  "@_name"?: string; // from type tConversationNode @ BPMN20.xsd
  participantRef?: string[]; // from type tConversationNode @ BPMN20.xsd
  messageFlowRef?: string[]; // from type tConversationNode @ BPMN20.xsd
  correlationKey?: BPMN20__tCorrelationKey[]; // from type tCorrelationKey @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tSubProcess = {
  "@_triggeredByEvent"?: boolean; // from type BPMN20__tSubProcess @ BPMN20.xsd
  laneSet?: BPMN20__tLaneSet[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type BPMN20__tSubProcess @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tTask = {
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tTerminateEventDefinition = {
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tTextAnnotation = {
  "@_textFormat"?: string; // from type BPMN20__tTextAnnotation @ BPMN20.xsd
  text?: BPMN20__tText; // from type BPMN20__tTextAnnotation @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tText = {} & BPMN20__tTextExtensionType;

export type BPMN20__tTimerEventDefinition = {
  timeDate?: BPMN20__tExpression; // from type BPMN20__tTimerEventDefinition @ BPMN20.xsd
  timeDuration?: BPMN20__tExpression; // from type BPMN20__tTimerEventDefinition @ BPMN20.xsd
  timeCycle?: BPMN20__tExpression; // from type BPMN20__tTimerEventDefinition @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tTransaction = {
  "@_method"?: BPMN20__tTransactionMethod; // from type BPMN20__tTransaction @ BPMN20.xsd
  "@_triggeredByEvent"?: boolean; // from type tSubProcess @ BPMN20.xsd
  laneSet?: BPMN20__tLaneSet[]; // from type tLaneSet @ BPMN20.xsd
  adHocSubProcess?: BPMN20__tAdHocSubProcess[]; // from type tFlowElement @ BPMN20.xsd
  boundaryEvent?: BPMN20__tBoundaryEvent[]; // from type tFlowElement @ BPMN20.xsd
  businessRuleTask?: BPMN20__tBusinessRuleTask[]; // from type tFlowElement @ BPMN20.xsd
  callActivity?: BPMN20__tCallActivity[]; // from type tFlowElement @ BPMN20.xsd
  callChoreography?: BPMN20__tCallChoreography[]; // from type tFlowElement @ BPMN20.xsd
  choreographyTask?: BPMN20__tChoreographyTask[]; // from type tFlowElement @ BPMN20.xsd
  complexGateway?: BPMN20__tComplexGateway[]; // from type tFlowElement @ BPMN20.xsd
  dataObject?: BPMN20__tDataObject[]; // from type tFlowElement @ BPMN20.xsd
  dataObjectReference?: BPMN20__tDataObjectReference[]; // from type tFlowElement @ BPMN20.xsd
  dataStoreReference?: BPMN20__tDataStoreReference[]; // from type tFlowElement @ BPMN20.xsd
  endEvent?: BPMN20__tEndEvent[]; // from type tFlowElement @ BPMN20.xsd
  event?: BPMN20__tEvent[]; // from type tFlowElement @ BPMN20.xsd
  eventBasedGateway?: BPMN20__tEventBasedGateway[]; // from type tFlowElement @ BPMN20.xsd
  exclusiveGateway?: BPMN20__tExclusiveGateway[]; // from type tFlowElement @ BPMN20.xsd
  implicitThrowEvent?: BPMN20__tImplicitThrowEvent[]; // from type tFlowElement @ BPMN20.xsd
  inclusiveGateway?: BPMN20__tInclusiveGateway[]; // from type tFlowElement @ BPMN20.xsd
  intermediateCatchEvent?: BPMN20__tIntermediateCatchEvent[]; // from type tFlowElement @ BPMN20.xsd
  intermediateThrowEvent?: BPMN20__tIntermediateThrowEvent[]; // from type tFlowElement @ BPMN20.xsd
  manualTask?: BPMN20__tManualTask[]; // from type tFlowElement @ BPMN20.xsd
  parallelGateway?: BPMN20__tParallelGateway[]; // from type tFlowElement @ BPMN20.xsd
  receiveTask?: BPMN20__tReceiveTask[]; // from type tFlowElement @ BPMN20.xsd
  scriptTask?: BPMN20__tScriptTask[]; // from type tFlowElement @ BPMN20.xsd
  sendTask?: BPMN20__tSendTask[]; // from type tFlowElement @ BPMN20.xsd
  sequenceFlow?: BPMN20__tSequenceFlow[]; // from type tFlowElement @ BPMN20.xsd
  serviceTask?: BPMN20__tServiceTask[]; // from type tFlowElement @ BPMN20.xsd
  startEvent?: BPMN20__tStartEvent[]; // from type tFlowElement @ BPMN20.xsd
  subChoreography?: BPMN20__tSubChoreography[]; // from type tFlowElement @ BPMN20.xsd
  subProcess?: BPMN20__tSubProcess[]; // from type tFlowElement @ BPMN20.xsd
  task?: BPMN20__tTask[]; // from type tFlowElement @ BPMN20.xsd
  transaction?: BPMN20__tTransaction[]; // from type tFlowElement @ BPMN20.xsd
  userTask?: BPMN20__tUserTask[]; // from type tFlowElement @ BPMN20.xsd
  association?: BPMN20__tAssociation[]; // from type tArtifact @ BPMN20.xsd
  group?: BPMN20__tGroup[]; // from type tArtifact @ BPMN20.xsd
  textAnnotation?: BPMN20__tTextAnnotation[]; // from type tArtifact @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMN20__tUserTask = {
  "@_implementation"?: BPMN20__tImplementation; // from type BPMN20__tUserTask @ BPMN20.xsd
  rendering?: BPMN20__tRendering[]; // from type BPMN20__tUserTask @ BPMN20.xsd
  "@_isForCompensation"?: boolean; // from type tActivity @ BPMN20.xsd
  "@_startQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_completionQuantity"?: number; // from type tActivity @ BPMN20.xsd
  "@_default"?: string; // from type tActivity @ BPMN20.xsd
  ioSpecification?: BPMN20__tInputOutputSpecification; // from type tInputOutputSpecification @ BPMN20.xsd
  property?: BPMN20__tProperty[]; // from type tProperty @ BPMN20.xsd
  dataInputAssociation?: BPMN20__tDataInputAssociation[]; // from type tDataInputAssociation @ BPMN20.xsd
  dataOutputAssociation?: BPMN20__tDataOutputAssociation[]; // from type tDataOutputAssociation @ BPMN20.xsd
  humanPerformer?: BPMN20__tHumanPerformer[]; // from type tResourceRole @ BPMN20.xsd
  potentialOwner?: BPMN20__tPotentialOwner[]; // from type tResourceRole @ BPMN20.xsd
  multiInstanceLoopCharacteristics?: BPMN20__tMultiInstanceLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  standardLoopCharacteristics?: BPMN20__tStandardLoopCharacteristics; // from type tLoopCharacteristics @ BPMN20.xsd
  incoming?: string[]; // from type tFlowNode @ BPMN20.xsd
  outgoing?: string[]; // from type tFlowNode @ BPMN20.xsd
  "@_name"?: string; // from type tFlowElement @ BPMN20.xsd
  auditing?: BPMN20__tAuditing; // from type tAuditing @ BPMN20.xsd
  monitoring?: BPMN20__tMonitoring; // from type tMonitoring @ BPMN20.xsd
  categoryValueRef?: string[]; // from type tFlowElement @ BPMN20.xsd
  "@_id"?: string; // from type tBaseElement @ BPMN20.xsd
  documentation?: BPMN20__tDocumentation[]; // from type tDocumentation @ BPMN20.xsd
  extensionElements?: BPMN20__tExtensionElements; // from type tExtensionElements @ BPMN20.xsd
};

export type BPMNDI__BPMNDiagram = {
  "bpmndi:BPMNPlane": BPMNDI__BPMNPlane; // from type BPMNDI__BPMNDiagram @ BPMNDI.xsd
  "bpmndi:BPMNLabelStyle"?: BPMNDI__BPMNLabelStyle[]; // from type BPMNDI__BPMNDiagram @ BPMNDI.xsd
  "@_name"?: string; // from type Diagram @ DI.xsd
  "@_documentation"?: string; // from type Diagram @ DI.xsd
  "@_resolution"?: number; // from type Diagram @ DI.xsd
  "@_id"?: string; // from type Diagram @ DI.xsd
};

export type BPMNDI__BPMNPlane = {
  "@_bpmnElement"?: string; // from type BPMNDI__BPMNPlane @ BPMNDI.xsd
  "bpmndi:BPMNEdge"?: BPMNDI__BPMNEdge[]; // from type di:DiagramElement @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
};

export type BPMNDI__BPMNEdge = {
  "@_bpmnElement"?: string; // from type BPMNDI__BPMNEdge @ BPMNDI.xsd
  "@_sourceElement"?: string; // from type BPMNDI__BPMNEdge @ BPMNDI.xsd
  "@_targetElement"?: string; // from type BPMNDI__BPMNEdge @ BPMNDI.xsd
  "@_messageVisibleKind"?: BPMNDI__MessageVisibleKind; // from type BPMNDI__BPMNEdge @ BPMNDI.xsd
  "bpmndi:BPMNLabel"?: BPMNDI__BPMNLabel; // from type BPMNDI__BPMNEdge @ BPMNDI.xsd
  "di:waypoint": DC__Point[]; // from type Edge @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
};

export type BPMNDI__BPMNShape = {
  "@_bpmnElement"?: string; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "@_isHorizontal"?: boolean; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "@_isExpanded"?: boolean; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "@_isMarkerVisible"?: boolean; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "@_isMessageVisible"?: boolean; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "@_participantBandKind"?: BPMNDI__ParticipantBandKind; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "@_choreographyActivityShape"?: string; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "bpmndi:BPMNLabel"?: BPMNDI__BPMNLabel; // from type BPMNDI__BPMNShape @ BPMNDI.xsd
  "dc:Bounds": DC__Bounds; // from type dc:Bounds @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
};

export type BPMNDI__BPMNLabel = {
  "@_labelStyle"?: string; // from type BPMNDI__BPMNLabel @ BPMNDI.xsd
  "dc:Bounds"?: DC__Bounds; // from type dc:Bounds @ DI.xsd
  "@_id"?: string; // from type DiagramElement @ DI.xsd
  "di:extension"?: any; // from type DiagramElement @ DI.xsd
};

export type BPMNDI__BPMNLabelStyle = {
  "dc:Font": DC__Font; // from type BPMNDI__BPMNLabelStyle @ BPMNDI.xsd
  "@_id"?: string; // from type Style @ DI.xsd
};

export type DC__Font = {
  "@_name"?: string; // from type DC__Font @ DC.xsd
  "@_size"?: number; // from type DC__Font @ DC.xsd
  "@_isBold"?: boolean; // from type DC__Font @ DC.xsd
  "@_isItalic"?: boolean; // from type DC__Font @ DC.xsd
  "@_isUnderline"?: boolean; // from type DC__Font @ DC.xsd
  "@_isStrikeThrough"?: boolean; // from type DC__Font @ DC.xsd
};

export type DC__Point = {
  "@_x": number; // from type DC__Point @ DC.xsd
  "@_y": number; // from type DC__Point @ DC.xsd
};

export type DC__Bounds = {
  "@_x": number; // from type DC__Bounds @ DC.xsd
  "@_y": number; // from type DC__Bounds @ DC.xsd
  "@_width": number; // from type DC__Bounds @ DC.xsd
  "@_height": number; // from type DC__Bounds @ DC.xsd
};
