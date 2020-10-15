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
import { HistoryAwareModelReducer, HistoryService, ModelReducerBinding } from "../history";
import { Model } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { AllScorecardActions } from "./ScorecardReducer";
import { getModelType, ModelType } from "../utils";

export const DelegatingModelReducer: HistoryAwareModelReducer<AllScorecardActions> = (
  service: HistoryService,
  reducers: Map<ModelType, ModelReducerBinding<any, any>>
): Reducer<Model[], AllScorecardActions> => {
  return (state: Model[], action: AllScorecardActions) => {
    if (state === undefined || action === undefined || action.payload === undefined) {
      return state;
    }

    //Delegate actions to model specific reducers
    const modelIndex: number = action.payload.modelIndex;
    if (modelIndex >= 0 && modelIndex < state.length) {
      const model = state[modelIndex];
      const modelType = getModelType(model);
      const reducer = reducers.get(modelType);
      if (reducer) {
        const newState = reducer.reducer(model, action);
        if (newState !== model) {
          const newModels: Model[] = [];
          state.forEach(m => newModels.push(m));
          newModels[modelIndex] = reducer.factory(newState);
          return newModels;
        }
      }
    }
    return state;
  };
};
