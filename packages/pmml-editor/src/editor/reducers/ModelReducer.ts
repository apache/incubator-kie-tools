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
import { Model, Scorecard } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { ScorecardReducer } from "./ScorecardReducer";
import { DelegatingModelReducer } from "./DelegatingModelReducer";
import mergeReducers from "combine-reducer";
import { CharacteristicsReducer } from "./CharacteristicsReducer";
import { CharacteristicReducer } from "./CharacteristicReducer";
import { OutputFieldReducer } from "./OutputFieldReducer";
import { OutputReducer } from "./OutputReducer";
import { MiningSchemaReducer } from "./MiningSchemaReducer";
import { MiningSchemaFieldReducer } from "./MiningSchemaFieldReducer";
import { ValidationRegistry } from "../validation";
import { Builder } from "../paths";

interface ModelPayload {
  [Actions.DeleteModel]: {
    readonly modelIndex: number;
  };
}

export type ModelActions = ActionMap<ModelPayload>[keyof ActionMap<ModelPayload>];

export const ModelReducer: HistoryAwareValidatingReducer<Model[], AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<Model[], AllActions> => {
  const scorecardReducer = mergeReducers(ScorecardReducer(historyService, validationRegistry), {
    MiningSchema: mergeReducers(MiningSchemaReducer(historyService, validationRegistry), {
      MiningField: MiningSchemaFieldReducer(historyService, validationRegistry),
    }),
    Output: mergeReducers(OutputReducer(historyService, validationRegistry), {
      OutputField: OutputFieldReducer(historyService, validationRegistry),
    }),
    Characteristics: mergeReducers(CharacteristicsReducer(historyService), {
      Characteristic: CharacteristicReducer(historyService),
    }),
  });

  const delegate = DelegatingModelReducer(
    historyService,
    new Map([
      [
        "Scorecard",
        {
          reducer: scorecardReducer,
          factory: (data: Scorecard) => {
            const model: Scorecard = new Scorecard(data);
            //TODO {manstis} This is vitally important to ensure marshalling to XML works OK!
            (model as any)._type = "Scorecard";
            return model;
          },
        },
      ],
    ])
  );

  return (state: Model[], action: AllActions) => {
    switch (action.type) {
      case Actions.DeleteModel:
        historyService.batch(state, Builder().forModel().build(), (draft) => {
          if (draft !== undefined) {
            const modelIndex: number = action.payload.modelIndex;
            if (modelIndex >= 0 && modelIndex < draft.length) {
              draft.splice(modelIndex, 1);
            }
          }
        });
        return state;
    }

    return delegate(state, action);
  };
};
