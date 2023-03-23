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
import { UnitablesInputsConfigs, UnitablesCellConfigs } from "@kie-tools/unitables";
import { CompanionFsService } from "../companionFs/CompanionFsService";
import { v4 as uuid } from "uuid";
import cloneDeep from "lodash/cloneDeep";

export const generateUuid = () => {
  return `_${uuid()}`.toLocaleUpperCase();
};

const DMN_RUNNER_PERSISTENCE_JSON_VERSION = "v1";

// TODO: use it!
export enum DmnRunnerMode {
  FORM = "form",
  TABLE = "table",
}

interface DmnRunnerPersistenceJsonConfigs {
  version: string;
  mode: DmnRunnerMode;
  inputs: UnitablesInputsConfigs;
}

export interface DmnRunnerPersistenceJson {
  configs: DmnRunnerPersistenceJsonConfigs;
  inputs: Array<InputRow>;
}

// DEFAULT VALUES
// TODO: defualt width?
export const DEFAULT_DMN_RUNNER_CONFIG_INPUT_WIDTH = 150;

export const DEFAULT_DMN_RUNNER_CONFIG_INPUT: UnitablesCellConfigs = {
  width: DEFAULT_DMN_RUNNER_CONFIG_INPUT_WIDTH,
};

export function getNewDefaultDmnRunnerPersistenceJson(): DmnRunnerPersistenceJson {
  return {
    configs: {
      version: DMN_RUNNER_PERSISTENCE_JSON_VERSION,
      mode: DmnRunnerMode.FORM,
      inputs: {},
    },
    inputs: [{ id: generateUuid() }],
  };
}

export class DmnRunnerPersistenceService {
  public readonly companionFsService = new CompanionFsService({
    storeNameSuffix: "dmn_runner_persistence",
    emptyFileContent: JSON.stringify(getNewDefaultDmnRunnerPersistenceJson()),
  });

  public parseDmnRunnerPersistenceJson(persistence: string): DmnRunnerPersistenceJson {
    const parsedDmnRunnerPersistenceJson = JSON.parse(persistence) as DmnRunnerPersistenceJson;

    // v0 to v1;
    if (Array.isArray(parsedDmnRunnerPersistenceJson)) {
      // backwards compatibility
      return { ...getNewDefaultDmnRunnerPersistenceJson(), inputs: parsedDmnRunnerPersistenceJson };
    }

    if (Object.prototype.toString.call(parsedDmnRunnerPersistenceJson) === "[object Object]") {
      if (
        !Object.prototype.hasOwnProperty.call(parsedDmnRunnerPersistenceJson, "inputs") ||
        !Object.prototype.hasOwnProperty.call(parsedDmnRunnerPersistenceJson, "configs")
      ) {
        return cloneDeep(getNewDefaultDmnRunnerPersistenceJson());
      }
      return parsedDmnRunnerPersistenceJson;
    }
    return getNewDefaultDmnRunnerPersistenceJson();
  }
}
