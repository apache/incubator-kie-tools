/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { GraphQL } from "@kogito-apps/consoles-common/dist/graphql";
import { User } from "@kogito-apps/consoles-common/dist/environment/auth";
import { ProcessDefinition } from "@kie-tools/runtime-tools-enveloped-components/dist/workflowForm/api";

export const createProcessDefinitionList = (processDefinitionObjs, url: string): ProcessDefinition[] => {
  const processDefinitionList = [];
  processDefinitionObjs.forEach((processDefObj) => {
    const processName = Object.keys(processDefObj)[0].split("/")[1];
    const endpoint = `${url}/${processName}`;
    processDefinitionList.push({
      processName,
      endpoint,
    });
  });
  return processDefinitionList;
};
