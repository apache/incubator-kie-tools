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

import { meta as dmn12meta, ns as dmn12ns } from "./schemas/dmn-1_2/ts-gen/meta";
import "./schemas/dmn-1_2/ts-gen/types";
import { meta as dmn13meta, ns as dmn13ns } from "./schemas/dmn-1_3/ts-gen/meta";
import "./schemas/dmn-1_3/ts-gen/types";
import { meta as dmn14meta, ns as dmn14ns } from "./schemas/dmn-1_4/ts-gen/meta";
import "./schemas/dmn-1_4/ts-gen/types";
import { meta as dmn15meta, ns as dmn15ns } from "./schemas/dmn-1_5/ts-gen/meta";
import "./schemas/dmn-1_5/ts-gen/types";
import { meta as kie10meta, ns as kie10ns } from "./schemas/kie-1_0/ts-gen/meta";
import {
  KIE__tAttachment,
  KIE__tComponentsWidthsExtension,
  KIE__tConstraintType,
} from "./schemas/kie-1_0/ts-gen/types";

export const LEGACY_KIE_NS__PRE_GWT_REMOVAL = "http://www.drools.org/kie/dmn/1.2";
export const KIE_NS = "kie:";
export type KIE = "kie";

export { Namespaced };

///////////////////////////
///       DMN 1.2       ///
///////////////////////////

declare module "./schemas/dmn-1_2/ts-gen/types" {
  export interface DMNDI12__DMNDiagram__extension {
    "kie:ComponentsWidthsExtension"?: Namespaced<KIE, KIE__tComponentsWidthsExtension>;
  }

  export interface DMN12__tBusinessKnowledgeModel__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN12__tDecision__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN12__tDecisionService__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN12__tInputData__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN12__tKnowledgeSource__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN12__tUnaryTests {
    "@_kie:constraintType"?: KIE__tConstraintType;
  }
}
dmn12ns.set(KIE_NS, kie10ns.get("")!);
dmn12ns.set(kie10ns.get("")!, KIE_NS);

mergeMetas(dmn12meta, [[KIE_NS, kie10meta]]);

(dmn12meta["DMNDI12__DMNDiagram__extension"] as any)["kie:ComponentsWidthsExtension"] = {
  type: "KIE__tComponentsWidthsExtension",
  isArray: false,
  xsdType: "// local type",
  fromType: "KIE__tComponentsWidthsExtension",
};

(dmn12meta["DMN12__tBusinessKnowledgeModel__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn12meta["DMN12__tDecision__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn12meta["DMN12__tDecisionService__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn12meta["DMN12__tInputData__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn12meta["DMN12__tKnowledgeSource__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

///////////////////////////
///       DMN 1.3       ///
///////////////////////////

declare module "./schemas/dmn-1_3/ts-gen/types" {
  export interface DMNDI13__DMNDiagram__extension {
    "kie:ComponentsWidthsExtension"?: Namespaced<KIE, KIE__tComponentsWidthsExtension>;
  }

  export interface DMN13__tBusinessKnowledgeModel__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN13__tDecision__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN13__tDecisionService__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN13__tInputData__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN13__tKnowledgeSource__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN13__tUnaryTests {
    "@_kie:constraintType"?: KIE__tConstraintType;
  }
}

dmn13ns.set(KIE_NS, kie10ns.get("")!);
dmn13ns.set(kie10ns.get("")!, KIE_NS);

mergeMetas(dmn13meta, [[KIE_NS, kie10meta]]);

(dmn13meta["DMNDI13__DMNDiagram__extension"] as any)["kie:ComponentsWidthsExtension"] = {
  type: "KIE__tComponentsWidthsExtension",
  isArray: false,
  xsdType: "// local type",
  fromType: "KIE__tComponentsWidthsExtension",
};

(dmn13meta["DMN13__tBusinessKnowledgeModel__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn13meta["DMN13__tDecision__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn13meta["DMN13__tDecisionService__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn13meta["DMN13__tInputData__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn13meta["DMN13__tKnowledgeSource__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

///////////////////////////
///       DMN 1.4       ///
///////////////////////////

declare module "./schemas/dmn-1_4/ts-gen/types" {
  export interface DMNDI13__DMNDiagram__extension {
    "kie:ComponentsWidthsExtension"?: Namespaced<KIE, KIE__tComponentsWidthsExtension>;
  }

  export interface DMN14__tBusinessKnowledgeModel__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN14__tDecision__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN14__tDecisionService__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN14__tInputData__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN14__tKnowledgeSource__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN14__tUnaryTests {
    "@_kie:constraintType"?: KIE__tConstraintType;
  }
}

dmn14ns.set(KIE_NS, kie10ns.get("")!);
dmn14ns.set(kie10ns.get("")!, KIE_NS);

mergeMetas(dmn14meta, [[KIE_NS, kie10meta]]);

(dmn14meta["DMNDI13__DMNDiagram__extension"] as any)["kie:ComponentsWidthsExtension"] = {
  type: "KIE__tComponentsWidthsExtension",
  isArray: false,
  xsdType: "// local type",
  fromType: "KIE__tComponentsWidthsExtension",
};

(dmn14meta["DMN14__tBusinessKnowledgeModel__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn14meta["DMN14__tDecision__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn14meta["DMN14__tDecisionService__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn14meta["DMN14__tInputData__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn14meta["DMN14__tKnowledgeSource__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

///////////////////////////
///       DMN 1.5       ///
///////////////////////////

declare module "./schemas/dmn-1_5/ts-gen/types" {
  export interface DMNDI15__DMNDiagram__extension {
    "kie:ComponentsWidthsExtension"?: Namespaced<KIE, KIE__tComponentsWidthsExtension>;
  }

  export interface DMN15__tBusinessKnowledgeModel__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN15__tDecision__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN15__tDecisionService__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN15__tInputData__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN15__tKnowledgeSource__extensionElements {
    "kie:attachment"?: Namespaced<KIE, KIE__tAttachment>[];
  }

  export interface DMN15__tUnaryTests {
    "@_kie:constraintType"?: KIE__tConstraintType;
  }
}

dmn15ns.set(KIE_NS, kie10ns.get("")!);
dmn15ns.set(kie10ns.get("")!, KIE_NS);

mergeMetas(dmn15meta, [[KIE_NS, kie10meta]]);

(dmn15meta["DMNDI15__DMNDiagram__extension"] as any)["kie:ComponentsWidthsExtension"] = {
  type: "KIE__tComponentsWidthsExtension",
  isArray: false,
  xsdType: "// local type",
  fromType: "KIE__tComponentsWidthsExtension",
};

(dmn15meta["DMN15__tBusinessKnowledgeModel__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn15meta["DMN15__tDecision__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn15meta["DMN15__tDecisionService__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn15meta["DMN15__tInputData__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};

(dmn15meta["DMN15__tKnowledgeSource__extensionElements"] as any)["kie:attachment"] = {
  type: "KIE__tAttachment",
  isArray: true,
  xsdType: "// local type",
  fromType: "KIE__tAttachment",
};
