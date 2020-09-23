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
import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryService } from "../history/HistoryProvider";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface VersionPayload {
  [Actions.SetVersion]: {
    readonly service: HistoryService;
    readonly version: string;
  };
}

export type VersionActions = ActionMap<VersionPayload>[keyof ActionMap<VersionPayload>];

interface StateControlPayload {
  [Actions.Undo]: {
    readonly service: HistoryService;
  };
  [Actions.Redo]: {
    readonly service: HistoryService;
  };
}

export type StateControlActions = ActionMap<StateControlPayload>[keyof ActionMap<StateControlPayload>];

export const PMMLReducer: Reducer<PMML, AllActions> = (state: PMML, action: AllActions) => {
  switch (action.type) {
    case Actions.SetVersion:
      return action.payload.service.mutate(state, null, draft => {
        draft.version = action.payload.version;
      });

    case Actions.Undo:
      return action.payload.service.undo(state);

    case Actions.Redo:
      return action.payload.service.redo(state);
  }

  return state;
};
