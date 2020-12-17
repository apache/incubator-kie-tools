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
import { Header } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { HistoryAwareReducer, HistoryService } from "../history";

interface HeaderPayload {
  [Actions.SetHeaderDescription]: {
    readonly description: string;
  };
}

export type HeaderActions = ActionMap<HeaderPayload>[keyof ActionMap<HeaderPayload>];

export const HeaderReducer: HistoryAwareReducer<Header, AllActions> = (
  service: HistoryService
): Reducer<Header, AllActions> => {
  return (state: Header, action: AllActions) => {
    switch (action.type) {
      case Actions.SetHeaderDescription:
        service.batch(state, "Header", draft => {
          draft.description = action.payload.description;
        });
    }

    return state;
  };
};
