/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import { useContext, createContext, PropsWithChildren, useMemo, useCallback } from "react";
import { useController } from "@kie-tools-core/react-hooks/dist/useController";
import { AlertAutoCloseArgs, AlertDelegate, Alerts, AlertsController } from "./Alerts";

export interface GlobalAlertsContextType {
  alerts: AlertsController | undefined;
  alertsRef: (controller: AlertsController) => void;
}

export interface GlobalAlertsDispatchContextType {
  closeAll(): void;
  close(key: string): void;
  show(key: string, staticArgs: unknown): void;
  set(
    key: string,
    alertDelegate: AlertDelegate<unknown>,
    autoCloseArgs?: AlertAutoCloseArgs
  ): {
    close: () => void | undefined;
    show: (staticArgs?: unknown) => void | undefined;
  };
}

export const GlobalAlertsContext = createContext<GlobalAlertsContextType>({} as any);
export const GlobalAlertsDispatchContext = createContext<GlobalAlertsDispatchContextType>({} as any);

export function GlobalAlertsContextProvider(props: PropsWithChildren<{}>) {
  const [alerts, alertsRef] = useController<AlertsController>();

  const value = useMemo(
    () => ({
      alerts,
      alertsRef,
    }),
    [alerts, alertsRef]
  );

  const closeAll = useCallback(() => {
    alerts?.closeAll();
  }, [alerts]);

  const close = useCallback(
    (key: string) => {
      alerts?.close(key);
    },
    [alerts]
  );

  const show = useCallback(
    (key: string, staticArgs: unknown) => {
      alerts?.show(key, staticArgs);
    },
    [alerts]
  );

  const set = useCallback(
    (key: string, alertDelegate: AlertDelegate<unknown>, autoCloseArgs?: AlertAutoCloseArgs) => {
      // This setTimeout is a hack to block a setState from hapenning while Alerts is still rendering
      setTimeout(() => {
        return alerts?.set(key, alertDelegate, autoCloseArgs);
      });

      return {
        close: () => alerts?.close(key),
        show: (staticArgs: unknown) => alerts?.show(key, staticArgs),
      };
    },
    [alerts]
  );

  const dispatch = useMemo(
    () => ({
      closeAll,
      close,
      show,
      set,
    }),
    [close, closeAll, set, show]
  );

  return (
    <GlobalAlertsContext.Provider value={value}>
      <GlobalAlertsDispatchContext.Provider value={dispatch}>
        <Alerts className="kogito--global-alert-container" ref={alertsRef} />
        {props.children}
      </GlobalAlertsDispatchContext.Provider>
    </GlobalAlertsContext.Provider>
  );
}

export function useGlobalAlertsContext() {
  return useContext(GlobalAlertsContext);
}

export function useGlobalAlertsDispatchContext() {
  return useContext(GlobalAlertsDispatchContext);
}

export function useGlobalAlert<T>(delegate: AlertDelegate<T>, autoCloseArgs?: AlertAutoCloseArgs) {
  const { set } = useGlobalAlertsDispatchContext();

  const key = useMemo(() => `${Math.random()}`, []);

  return set(key, delegate, autoCloseArgs);
}
