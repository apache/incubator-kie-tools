/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { InputRow } from "@kie-tools/form-dmn";
import { CompanionFsService } from "../companionFs/CompanionFsService";

export const EMPTY_DMN_RUNNER_INPUTS = [{}];

export class DmnRunnerInputsService {
  public readonly companionFsService = new CompanionFsService({
    storeNameSuffix: "dmn_runner_inputs",
    emptyFileContent: JSON.stringify(EMPTY_DMN_RUNNER_INPUTS),
  });

  public stringifyDmnRunnerInputs(
    inputs: Array<InputRow> | ((previous: Array<InputRow>) => Array<InputRow>),
    previous?: Array<InputRow>
  ) {
    if (typeof inputs === "function") {
      return JSON.stringify(inputs(previous ?? EMPTY_DMN_RUNNER_INPUTS));
    }
    return JSON.stringify(inputs);
  }

  public parseDmnRunnerInputs(inputs: string): Array<InputRow> {
    return JSON.parse(inputs) as Array<InputRow>;
  }
}
