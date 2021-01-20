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
import { getModelType, ModelType } from "../PMMLModelHelper";
import { AllActions } from "./Actions";
import get = Reflect.get;

const reduce = (model: Model, action: AllActions, reducers: Map<ModelType, ModelReducerBinding<any, any>>) => {
  const modelType = getModelType(model);
  const reducer = reducers.get(modelType);
  return reducer?.factory(reducer.reducer(model, action)) ?? model;
};

export const DelegatingModelReducer: HistoryAwareModelReducer<AllActions> = (
  service: HistoryService,
  reducers: Map<ModelType, ModelReducerBinding<any, any>>
): Reducer<Model[], AllActions> => {
  return (state: Model[], action: AllActions) => {
    //Redux calls all reducers when the Store is created to allow initialisation.
    if (state === undefined || action === undefined || action.payload === undefined) {
      return state;
    }

    //The sub-reducers may have created new instances of model components however
    //this reducer needs to return a *new* Model[] instance in order for a state change
    //to be correctly detected.
    let changed = false;
    const newState: Model[] = [];
    state.forEach(m => newState.push(m));
    const modelIndex: number = get(action.payload, "modelIndex");

    //Delegate Model agnostic actions to all Model reducers
    if (modelIndex === undefined) {
      state.forEach((model, index) => {
        const modelAction = Object.assign({}, action, { payload: { ...action.payload, modelIndex: index } });
        const newModel = reduce(model, modelAction, reducers);
        if (model !== newModel) {
          changed = true;
          newState[index] = newModel;
        }
      });
    }

    //Delegate Model specific actions to Model specific reducers
    if (modelIndex >= 0 && modelIndex < state.length) {
      const model = state[modelIndex];
      const newModel = reduce(model, action, reducers);
      if (model !== newModel) {
        changed = true;
        newState[modelIndex] = newModel;
      }
    }

    if (changed) {
      return newState;
    }

    return state;
  };
};
