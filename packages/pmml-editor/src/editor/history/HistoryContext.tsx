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
import * as React from "react";
import { PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { HistoryService } from "./HistoryProvider";

export interface History {
  service: HistoryService;
  getCurrentState: () => PMML | undefined;
}

export const HistoryContext = React.createContext<History>({
  service: new HistoryService(),
  getCurrentState: () => undefined
});

export function useHistoryService() {
  return React.useContext(HistoryContext);
}
