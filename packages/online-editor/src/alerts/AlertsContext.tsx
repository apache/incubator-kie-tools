/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useContext } from "react";
import { AlertsController } from "./Alerts";

export interface DeploymentFile {
  path: string;
  getFileContents: () => Promise<string>;
}

export interface AlertsContextType {
  alerts: AlertsController | undefined;
  alertsRef: (controller: AlertsController) => void;
}

export const AlertsContext = React.createContext<AlertsContextType>({} as any);

export function useAlerts() {
  return useContext(AlertsContext);
}
