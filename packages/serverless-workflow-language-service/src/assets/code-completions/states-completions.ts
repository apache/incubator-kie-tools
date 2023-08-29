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

import { Specification } from "@severlessworkflow/sdk-typescript";
import { OmitRecursively } from "@kie-tools/json-yaml-language-service/dist/channel";

export const genericStateCompletion = {
  name: "${1:Unique State name}",
  transition: "${10:Next transition of the workflow}",
};

export const operationStateCompletion: OmitRecursively<Specification.Operationstate, "normalize"> = {
  name: genericStateCompletion.name,
  type: "operation",
  actions: [
    {
      name: "${5:Unique Action name}",
      functionRef: {},
    },
  ] as Specification.Action[],
  transition: genericStateCompletion.transition,
  end: false,
};

export const eventStateCompletion: OmitRecursively<Specification.Eventstate, "normalize"> = {
  name: genericStateCompletion.name,
  type: "event",
  onEvents: [
    {
      eventRefs: ["${5:Unique event names}"],
    },
  ],
  transition: genericStateCompletion.transition,
  end: false,
};

export const switchStateCompletion: OmitRecursively<Specification.Databasedswitchstate, "normalize"> = {
  name: genericStateCompletion.name,
  type: "switch",
  dataConditions: [
    {
      condition: "${5:Workflow expression evaluated against state data}",
      transition: "${6:Transition to another state if condition is true}",
    },
  ],
  defaultCondition: {
    transition: "${7:Default transition of the workflow}",
  },
};

export const injectStateCompletion: OmitRecursively<Specification.Injectstate, "normalize"> = {
  name: genericStateCompletion.name,
  type: "inject",
  data: {},
  transition: genericStateCompletion.transition,
  end: false,
};
