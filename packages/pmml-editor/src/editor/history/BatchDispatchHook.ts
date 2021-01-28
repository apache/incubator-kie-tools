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

import { useDispatch } from "react-redux";
import { HistoryService } from "./HistoryProvider";
import { Dispatch } from "redux";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions } from "../reducers";

export const useBatchDispatch = (service: HistoryService, getCurrentState: () => PMML | undefined): Dispatch<any> => {
  const dispatch = useDispatch();

  return (action: any): any => {
    const result = dispatch(action);

    dispatch({
      type: Actions.Refresh,
      payload: {
        pmml: service.commit(getCurrentState())
      }
    });

    return result;
  };
};
