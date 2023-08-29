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

export const workflowCompletion: OmitRecursively<Specification.Workflow, "normalize"> = {
  id: "${1:workflow_unique_identifier}",
  version: "${2:0.1}",
  specVersion: "${3:0.8}",
  name: "${4:Workflow name}",
  description: "${5:Workflow description}",
  start: "${13}",
  functions: [
    {
      name: "${7:uniqueFunctionName}",
      operation: "${8:localhost#operation}",
      // @ts-expect-error not using the original type to use CodeCompletions placeholder
      type: "${9:rest}",
    },
  ],
  events: [
    {
      name: "${10:Unique event name}",
      source: "${11:CloudEvent source}",
      type: "${12:CloudEvent type}",
    },
  ],
  states: [
    {
      name: "${13:StartState}",
      // @ts-expect-error not using the original type to use CodeCompletions placeholder
      type: "${14:operation}",
      actions: [
        {
          name: "${15:uniqueActionName}",
          functionRef: {
            refName: "${7}",
            arguments: {
              firstArgument: "",
              secondArgument: "",
            },
          },
        },
      ],
      end: true,
    },
  ],
};
