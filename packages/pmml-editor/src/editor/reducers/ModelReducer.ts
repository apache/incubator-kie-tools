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
import { Model } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface ModelPayload {
  [Actions.DeleteModel]: {
    readonly model: Model;
  };
}

export type ModelActions = ActionMap<ModelPayload>[keyof ActionMap<ModelPayload>];

export const ModelReducer: HistoryAwareReducer<Model[], ModelActions> = (
  service: HistoryService
): Reducer<Model[], ModelActions> => {
  return (state: Model[], action: ModelActions) => {
    switch (action.type) {
      case Actions.DeleteModel:
        return service.mutate(state, "models", draft => {
          const model: Model = action.payload.model;
          if (draft !== undefined) {
            const index: number = draft.indexOf(model);
            if (index >= 0 && index < draft.length) {
              draft.splice(index, 1);
            }
          }
        });
    }

    return state;
  };
};
