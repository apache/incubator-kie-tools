/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { DecisionResult } from "@kie-tools/form-dmn";
import { routes } from "./Routes";

export interface FetchDmnResultArgs {
  modelName: string;
  inputs: any;
}

export async function fetchDmnResult(args: FetchDmnResultArgs): Promise<DecisionResult[]> {
  const response = await fetch(routes.dmnResult.path({ modelName: args.modelName }), {
    method: "POST",
    headers: {
      Accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(args.inputs),
  });

  return (await response.json()).decisionResults as DecisionResult[];
}
