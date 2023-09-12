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

import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import { PMML } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { ValidationRegistry } from "../validation";
import { Builder } from "../paths";
import { validateMiningFieldsDataFieldReference } from "../validation/MiningSchema";
import { getMiningSchema } from "../PMMLModelHelper";

interface PMMLPayload {
  [Actions.Refresh]: {
    readonly pmml: PMML;
  };
  [Actions.Validate]: {
    readonly modelIndex?: number;
  };
  [Actions.SetVersion]: {
    readonly version: string;
  };
}

export type VersionActions = ActionMap<PMMLPayload>[keyof ActionMap<PMMLPayload>];

interface StateControlPayload {
  [Actions.Undo]: undefined;
  [Actions.Redo]: undefined;
}

export type StateControlActions = ActionMap<StateControlPayload>[keyof ActionMap<StateControlPayload>];

export const PMMLReducer: HistoryAwareValidatingReducer<PMML, AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<PMML, AllActions> => {
  return (state: PMML, action: AllActions) => {
    switch (action.type) {
      case Actions.Refresh:
        return action.payload.pmml;

      case Actions.SetVersion:
        historyService.batch(state, null, (draft) => {
          draft.version = action.payload.version;
        });
        break;

      case Actions.Undo:
        return historyService.undo(state);

      case Actions.Redo:
        return historyService.redo(state);

      case Actions.Validate:
        const dataFields = state.DataDictionary.DataField;
        const models = state.models ?? [];
        models.forEach((model, modelIndex) => {
          const miningSchema = getMiningSchema(state, modelIndex);
          if (miningSchema !== undefined) {
            validationRegistry.clear(Builder().forModel(modelIndex).forMiningSchema().build());
            validateMiningFieldsDataFieldReference(
              modelIndex,
              dataFields,
              miningSchema.MiningField,
              validationRegistry
            );
          }
        });
    }

    return state;
  };
};
