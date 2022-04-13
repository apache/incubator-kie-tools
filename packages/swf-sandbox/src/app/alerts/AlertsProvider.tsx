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
import { useContext, useMemo, Ref } from "react";
import { useController } from "../reactExt/Hooks";
import { AlertsController } from "./Alerts";

interface Props {
  children: React.ReactNode;
}

export interface AlertsContextType {
  alerts?: AlertsController;
  alertsRef: Ref<AlertsController>;
}

export const AlertsContext = React.createContext<AlertsContextType>({} as any);

export function AlertsProvider(props: Props) {
  const [alerts, alertsRef] = useController<AlertsController>();

  const value = useMemo(
    () => ({
      alerts,
      alertsRef,
    }),
    [alerts, alertsRef]
  );

  return <AlertsContext.Provider value={value}>{props.children}</AlertsContext.Provider>;
}

export function useAlertsController(): [AlertsController | undefined, Ref<AlertsController>] {
  const { alerts, alertsRef } = useContext(AlertsContext);

  return [alerts, alertsRef];
}
