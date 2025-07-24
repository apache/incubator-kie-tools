/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Namespaced, mergeMetas } from "@kie-tools/xml-parser-ts";

import { meta as bpmn20meta, ns as bpmn20ns } from "./schemas/bpmn-2_0/ts-gen/meta";
import "./schemas/bpmn-2_0/ts-gen/types";

import { meta as drools10meta, ns as drools10ns } from "./schemas/drools-1_0/ts-gen/meta";
import {
  drools__GLOBAL__global,
  drools__GLOBAL__import,
  drools__GLOBAL__metaData,
  drools__GLOBAL__onEntry_script,
  drools__GLOBAL__onExit_script,
} from "./schemas/drools-1_0/ts-gen/types";

export const DROOLS_NS__PRE_GWT_REMOVAL = "http://www.jboss.org/drools";
export const DROOLS_NS = "drools:";
export type DROOLS = "drools";

export { Namespaced };

export const BUSINESS_RULE_TASK_IMPLEMENTATIONS = {
  drools: "http://www.jboss.org/drools/rule",
  dmn: "http://www.jboss.org/drools/dmn",
};

export const BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING = {
  FILE_PATH: "fileName",
  NAMESPACE: "namespace",
  MODEL_NAME: "model",
};

export const USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING = {
  TASK_NAME: "TaskName",
  SKIPPABLE: "Skippable",
  GROUP_ID: "GroupId",
  COMMENT: "Comment",
  DESCRIPTION: "Description",
  PRIORITY: "Priority",
  CREATED_BY: "CreatedBy",
  CONTENT: "Content",
  NOT_STARTED_REASSIGN: "NotStartedReassign",
  NOT_COMPLETED_REASSIGN: "NotCompletedReassign",
  NOT_STARTED_NOTIFY: "NotStartedNotify",
  NOT_COMPLETELY_NOTIFY: "NotCompletedNotify",
  MULTI_INSTANCE_ITEM_TYPE: "multiInstanceItemType",
};

export const DATA_INPUT_RESERVED_NAMES = new Set([
  BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.FILE_PATH,
  BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NAMESPACE,
  BUSINESS_RULE_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MODEL_NAME,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.TASK_NAME,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.SKIPPABLE,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.GROUP_ID,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.COMMENT,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.DESCRIPTION,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.PRIORITY,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CREATED_BY,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.CONTENT,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_REASSIGN,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETED_REASSIGN,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_STARTED_NOTIFY,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.NOT_COMPLETELY_NOTIFY,
  USER_TASK_IO_SPECIFICATION_DATA_INPUTS_CONSTANTS_FOR_DMN_BINDING.MULTI_INSTANCE_ITEM_TYPE,
]);

export const SERVICE_TASK_IMPLEMENTATIONS = {
  java: "Java",
  webService: "WebService",
};

///////////////////////////
///       BPMN 2.0      ///
///////////////////////////

declare module "./schemas/bpmn-2_0/ts-gen/types" {
  type WithMetaData = {
    "drools:metaData"?: Namespaced<DROOLS, drools__GLOBAL__metaData>[];
  };

  export interface BPMN20__tProcess {
    "@_drools:packageName"?: Namespaced<DROOLS, string>;
    "@_drools:version"?: Namespaced<DROOLS, string>;
    "@_drools:adHoc"?: Namespaced<DROOLS, boolean>;
  }

  export interface BPMN20__tProcess__extensionElements extends WithMetaData, WithEntryAndExitScripts {
    "drools:import"?: Namespaced<DROOLS, drools__GLOBAL__import>[];
    "drools:global"?: Namespaced<DROOLS, drools__GLOBAL__global>[];
  }

  type WithEntryAndExitScripts = {
    "drools:onEntry-script"?: Namespaced<DROOLS, drools__GLOBAL__onEntry_script>;
    "drools:onExit-script"?: Namespaced<DROOLS, drools__GLOBAL__onExit_script>;
  };

  // *************************************************** NOTE ******************************************************
  //
  //  Some sequenceFlow elements are commented on purpose. They're here for completeness, but they're not currently
  //                   relevant by this BPMN marshaller, since none of those are executable.
  //
  // ***************************************************************************************************************

  export interface BPMN20__tAdHocSubProcess__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tBoundaryEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tBusinessRuleTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tCallActivity__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tCallChoreography__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tChoreographyTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tComplexGateway__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tDataObject__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tDataObjectReference__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tDataStoreReference__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tEndEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tEventBasedGateway__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tExclusiveGateway__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tImplicitThrowEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tInclusiveGateway__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tIntermediateCatchEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tIntermediateThrowEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tManualTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tParallelGateway__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tReceiveTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tScriptTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tSendTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tSequenceFlow__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tServiceTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tStartEvent__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  // export interface BPMN20__tSubChoreography__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tSubProcess__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tTransaction__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tUserTask__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tAssociation__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tGroup__extensionElements extends WithEntryAndExitScripts, WithMetaData {}
  export interface BPMN20__tTextAnnotation__extensionElements extends WithEntryAndExitScripts, WithMetaData {}

  // Custom Tasks
  export interface BPMN20__tTask {
    "@_drools:taskName"?: Namespaced<DROOLS, string>;
  }

  // Other
  export interface BPMN20__tProperty__extensionElements extends WithMetaData {}
  export interface BPMN20__tLane__extensionElements extends WithMetaData {}

  // Call activity
  export interface BPMN20__tCallActivity {
    "@_drools:independent"?: Namespaced<DROOLS, boolean>;
    "@_drools:waitForCompletion"?: Namespaced<DROOLS, boolean>;
  }

  // Service task
  export interface BPMN20__tServiceTask {
    "@_drools:serviceimplementation"?: Namespaced<DROOLS, string>;
    "@_drools:serviceinterface"?: Namespaced<DROOLS, string>;
    "@_drools:serviceoperation"?: Namespaced<DROOLS, string>;
  }

  // Business Rule task
  export interface BPMN20__tBusinessRuleTask {
    "@_drools:ruleFlowGroup"?: Namespaced<DROOLS, string>;
  }

  // Data Input
  export interface BPMN20__tDataInput {
    "@_drools:dtype"?: Namespaced<DROOLS, string>;
  }

  // Data Output
  export interface BPMN20__tDataOutput {
    "@_drools:dtype"?: Namespaced<DROOLS, string>;
  }

  // Message Event Definition
  export interface BPMN20__tMessageEventDefinition {
    "@_drools:msgref"?: Namespaced<DROOLS, string>;
  }

  // Escalation Event Definition
  export interface BPMN20__tEscalationEventDefinition {
    "@_drools:esccode"?: Namespaced<DROOLS, string>;
  }

  // Error Event Definition
  export interface BPMN20__tErrorEventDefinition {
    "@_drools:erefname"?: Namespaced<DROOLS, string>;
  }

  // Sequence Flow Definition
  export interface BPMN20__tSequenceFlow {
    "@_drools:priority"?: Namespaced<DROOLS, string>;
  }
}

bpmn20ns.set(DROOLS_NS, drools10ns.get("")!);
bpmn20ns.set(drools10ns.get("")!, DROOLS_NS);

mergeMetas(bpmn20meta, [[DROOLS_NS, drools10meta]]);

// Process attrs
(bpmn20meta["BPMN20__tProcess"] as any)["@_drools:packageName"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tProcess",
};
(bpmn20meta["BPMN20__tProcess"] as any)["@_drools:version"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tProcess",
};
(bpmn20meta["BPMN20__tProcess"] as any)["@_drools:adHoc"] = {
  type: "boolean",
  isArray: false,
  xsdType: "xsd:boolean",
  fromType: "BPMN20__tProcess",
};

// Process elements
(bpmn20meta["BPMN20__tProcess__extensionElements"] as any)["drools:import"] = {
  type: "drools__GLOBAL__import",
  isArray: true,
  xsdType: "// local type",
  fromType: "BPMN20__tProcess__extensionElements",
};
(bpmn20meta["BPMN20__tProcess__extensionElements"] as any)["drools:global"] = {
  type: "drools__GLOBAL__global",
  isArray: true,
  xsdType: "// local type",
  fromType: "BPMN20__tProcess__extensionElements",
};

// Business Rule Task attrs
(bpmn20meta["BPMN20__tBusinessRuleTask"] as any)["@_drools:ruleFlowGroup"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tBusinessRuleTask",
};

// Call Activity attrs
(bpmn20meta["BPMN20__tCallActivity"] as any)["@_drools:independent"] = {
  type: "boolean",
  isArray: false,
  xsdType: "xsd:boolean",
  fromType: "BPMN20__tCallActivity",
};
(bpmn20meta["BPMN20__tCallActivity"] as any)["@_drools:waitForCompletion"] = {
  type: "boolean",
  isArray: false,
  xsdType: "xsd:boolean",
  fromType: "BPMN20__tCallActivity",
};

// Service Task attrs
(bpmn20meta["BPMN20__tServiceTask"] as any)["@_drools:serviceimplementation"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tServiceTask",
};
(bpmn20meta["BPMN20__tServiceTask"] as any)["@_drools:serviceinterface"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tServiceTask",
};
(bpmn20meta["BPMN20__tServiceTask"] as any)["@_drools:serviceoperation"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tServiceTask",
};

// Data Input
(bpmn20meta["BPMN20__tDataInput"] as any)["@_drools:dtype"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tDataInput",
};
// Data Output
(bpmn20meta["BPMN20__tDataOutput"] as any)["@_drools:dtype"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tDataOutput",
};

// Message Event Definition
(bpmn20meta["BPMN20__tMessageEventDefinition"] as any)["@_drools:msgref"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tMessageEventDefinition",
};

// Escalation Event Definition
(bpmn20meta["BPMN20__tEscalationEventDefinition"] as any)["@_drools:esccode"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tEscalationEventDefinition",
};

// Error Event Definition
(bpmn20meta["BPMN20__tErrorEventDefinition"] as any)["@_drools:erefname"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tErrorEventDefinition",
};

// Error Event Definition
(bpmn20meta["BPMN20__tSequenceFlow"] as any)["@_drools:priority"] = {
  type: "string",
  isArray: false,
  xsdType: "xsd:string",
  fromType: "BPMN20__tSequenceFlow",
};

class MetaType {
  public static of(typeName: keyof typeof bpmn20meta) {
    return new MetaType(typeName);
  }

  private constructor(private readonly typeName: keyof typeof bpmn20meta) {}

  public hasMetadata() {
    (bpmn20meta[this.typeName] as any)["drools:metaData"] = {
      type: "drools__GLOBAL__metaData",
      isArray: true,
      xsdType: "// local type",
      fromType: this.typeName,
    };
    return this;
  }

  public hasEntryAndExitScripts() {
    (bpmn20meta[this.typeName] as any)["drools:onEntry-script"] = {
      type: "drools__GLOBAL__onEntry_script",
      isArray: false,
      xsdType: "// local type",
      fromType: this.typeName,
    };
    (bpmn20meta[this.typeName] as any)["drools:onExit-script"] = {
      type: "drools__GLOBAL__onExit_script",
      isArray: false,
      xsdType: "// local type",
      fromType: this.typeName,
    };
    return this;
  }
}

//
// See some of those are commented above too.
//
MetaType.of("BPMN20__tAdHocSubProcess__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tBoundaryEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tBusinessRuleTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tCallActivity__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tCallChoreography__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tChoreographyTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tComplexGateway__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tDataObject__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tDataObjectReference__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tDataStoreReference__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tEndEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tEventBasedGateway__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tExclusiveGateway__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tImplicitThrowEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tInclusiveGateway__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tIntermediateCatchEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tIntermediateThrowEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tManualTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tParallelGateway__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tReceiveTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tScriptTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tSendTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tSequenceFlow__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tServiceTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tStartEvent__extensionElements").hasEntryAndExitScripts().hasMetadata();
// MetaType.of("BPMN20__tSubChoreography__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tSubProcess__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tTransaction__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tUserTask__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tAssociation__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tGroup__extensionElements").hasEntryAndExitScripts().hasMetadata();
MetaType.of("BPMN20__tTextAnnotation__extensionElements").hasEntryAndExitScripts().hasMetadata();

// Process
MetaType.of("BPMN20__tProcess__extensionElements").hasMetadata();

// Property
MetaType.of("BPMN20__tProperty__extensionElements").hasMetadata();

// Lane
MetaType.of("BPMN20__tLane__extensionElements").hasMetadata();
