/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { ActionMap, Actions } from "./Actions";
import { HistoryAwareReducer, HistoryService } from "../history";
import { Model, Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { AllScorecardActions, ScorecardReducer } from "./ScorecardReducer";
import { DelegatingModelReducer } from "./DelegatingModelReducer";
import mergeReducers from "combine-reducer";
import { CharacteristicsReducer } from "./CharacteristicsReducer";
import { CharacteristicReducer } from "./CharacteristicReducer";

interface ModelPayload {
  [Actions.DeleteModel]: {
    readonly modelIndex: number;
  };
}

export type ModelActions = ActionMap<ModelPayload>[keyof ActionMap<ModelPayload>];

export const ModelReducer: HistoryAwareReducer<Model[], ModelActions | AllScorecardActions> = (
  service: HistoryService
): Reducer<Model[], ModelActions | AllScorecardActions> => {
  const scorecardReducer = mergeReducers(ScorecardReducer(service), {
    Characteristics: mergeReducers(CharacteristicsReducer(service), {
      Characteristic: CharacteristicReducer(service)
    })
  });

  const delegate = DelegatingModelReducer(
    service,
    new Map([
      [
        "Scorecard",
        {
          reducer: scorecardReducer,
          factory: (data: Scorecard) => {
            const model: Scorecard = new Scorecard(data);
            (model as any)._type = "Scorecard";
            return model;
          }
        }
      ]
    ])
  );

  return (state: Model[], action: ModelActions | AllScorecardActions) => {
    switch (action.type) {
      case Actions.DeleteModel:
        return service.mutate(state, "models", draft => {
          if (draft !== undefined) {
            const modelIndex: number = action.payload.modelIndex;
            if (modelIndex >= 0 && modelIndex < draft.length) {
              draft.splice(modelIndex, 1);
            }
          }
        });
    }

    return delegate(state, action);
  };
};
