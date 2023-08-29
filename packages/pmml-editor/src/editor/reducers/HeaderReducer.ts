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
import { Header } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { HistoryAwareReducer, HistoryService } from "../history";
import { Builder } from "../paths";

interface HeaderPayload {
  [Actions.SetHeaderDescription]: {
    readonly description: string;
  };
}

export type HeaderActions = ActionMap<HeaderPayload>[keyof ActionMap<HeaderPayload>];

export const HeaderReducer: HistoryAwareReducer<Header, AllActions> = (
  historyService: HistoryService
): Reducer<Header, AllActions> => {
  return (state: Header, action: AllActions) => {
    switch (action.type) {
      case Actions.SetHeaderDescription:
        historyService.batch(state, Builder().forHeader().build(), (draft) => {
          draft.description = action.payload.description;
        });
    }

    return state;
  };
};
