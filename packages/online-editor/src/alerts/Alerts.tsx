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
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { AlertGroup } from "@patternfly/react-core/dist/js/components/AlertGroup";

type MapValueType<A> = A extends Map<any, infer V> ? V : never;

export type AlertDelegate<T> = (
  args: { close: () => void; secondsUntilAutoClose?: number } & Partial<AlertAutoCloseArgs>,
  staticArgs: T
) => React.ReactNode;

export interface AlertAutoCloseArgs {
  durationInSeconds: number;
}

export interface AlertsController {
  closeAll(): void;
  show(key: string, staticArgs: unknown): void;
  close(key: string): void;
  set(key: string, alertDelegate: AlertDelegate<unknown>, autoCloseArgs?: AlertAutoCloseArgs): void;
}

const AUTO_CLOSE_ALERTS_REFRESH_RATE_IN_MS = 1000;

type AlertControl = {
  alertDelegate: AlertDelegate<unknown>;
  autoCloseArgs?: AlertAutoCloseArgs;
  isShowing: boolean;
  lastShowedAt?: Date;
  staticArgs: unknown;
};

type AlertsProps = {
  className?: string;
};

export const Alerts = React.forwardRef<AlertsController, AlertsProps>((props, forwardedRef) => {
  const [alerts, setAlerts] = useState(new Map<string, AlertControl>());
  const [autoCloseAlertsControl, setAutoCloseAlertsControl] = useState(
    new Map<string, { secondsLeft: number; interval: ReturnType<typeof setInterval> }>()
  );

  const changeValueForKeys = useCallback(
    (prev: typeof alerts, keys: string[], args: Partial<MapValueType<typeof alerts>>) => {
      const newAlerts = new Map(prev);
      for (const key of keys) {
        newAlerts.set(key, { ...newAlerts.get(key)!, ...args });
      }
      return newAlerts;
    },
    []
  );

  const imperativeHandle = useMemo<AlertsController>(() => {
    return {
      closeAll: () => {
        setAlerts((prev) => changeValueForKeys(prev, [...prev.keys()], { isShowing: false }));
      },
      close: (key) => {
        setAlerts((prev) => (prev.has(key) ? changeValueForKeys(prev, [key], { isShowing: false }) : prev));
      },
      show: (key, staticArgs) => {
        setAlerts((prev) =>
          prev.has(key)
            ? changeValueForKeys(prev, [key], { isShowing: true, lastShowedAt: new Date(), staticArgs })
            : prev
        );
      },
      set: (key, alertDelegate, autoCloseArgs) => {
        setAlerts((prev) => {
          const next = new Map(prev);
          next.set(key, {
            alertDelegate,
            isShowing: prev.get(key)?.isShowing ?? false,
            lastShowedAt: prev.get(key)?.lastShowedAt,
            staticArgs: prev.get(key)?.staticArgs,
            autoCloseArgs,
          });
          return next;
        });
      },
    };
  }, [changeValueForKeys]);

  useImperativeHandle(forwardedRef, () => imperativeHandle, [imperativeHandle]);

  const startRefreshingAlertWithKey = useCallback(
    (k: string) => {
      return setInterval(() => {
        setAutoCloseAlertsControl((prev) => {
          const next = new Map(prev);
          const prevAlert = prev.get(k)!;

          if (prevAlert.secondsLeft > 1) {
            next.set(k, { ...prevAlert, secondsLeft: prevAlert.secondsLeft - 1 });
          } else {
            imperativeHandle.close(k);
          }

          return next;
        });
      }, AUTO_CLOSE_ALERTS_REFRESH_RATE_IN_MS);
    },
    [imperativeHandle]
  );

  useEffect(() => {
    setAutoCloseAlertsControl((prev) => {
      const next = new Map(prev);
      [...alerts.entries()].forEach(([k, v]) => {
        if (v.isShowing && prev.has(k) && v.autoCloseArgs) {
          next.set(k, { secondsLeft: v.autoCloseArgs.durationInSeconds, interval: prev.get(k)!.interval });
        }
        //
        else if (v.isShowing && !prev.has(k) && v.autoCloseArgs) {
          const interval = startRefreshingAlertWithKey(k);
          next.set(k, { secondsLeft: v.autoCloseArgs.durationInSeconds, interval });
        }
        //
        else if (!v.isShowing) {
          const saved = prev.get(k);
          if (saved) {
            clearInterval(saved.interval);
            next.delete(k);
          }
        }
      });
      return next;
    });
  }, [alerts, startRefreshingAlertWithKey]);

  return (
    <AlertGroup className={`kogito--alert-container ${props.className ?? ""}`}>
      <div className={"kogito--alert-list"}>
        {[...alerts.entries()]
          .filter(([_, { isShowing }]) => isShowing)
          .sort(([_, a], [__, b]) => a.lastShowedAt!.getTime() - b.lastShowedAt!.getTime()) // show newest at the bottom
          .map(([key, { alertDelegate, autoCloseArgs, staticArgs }]) => (
            <React.Fragment key={key}>
              {alertDelegate(
                {
                  close: () => imperativeHandle.close(key),
                  secondsUntilAutoClose:
                    autoCloseAlertsControl.get(key)?.secondsLeft ?? autoCloseArgs?.durationInSeconds,
                  durationInSeconds: autoCloseArgs?.durationInSeconds,
                },
                staticArgs
              )}
              <br />
            </React.Fragment>
          ))}
      </div>
    </AlertGroup>
  );
});

export function useAlert<T = void>(
  alertsController: AlertsController | undefined,
  delegate: AlertDelegate<T>,
  autoCloseArgs?: AlertAutoCloseArgs
) {
  const key = useMemo(() => `${Math.random()}`, []);

  useEffect(() => {
    alertsController?.set(key, delegate, autoCloseArgs);
  }, [alertsController, key, delegate, autoCloseArgs]);

  return useMemo(
    () => ({
      close: () => alertsController?.close(key),
      show: (staticArgs: T) => alertsController?.show(key, staticArgs),
    }),
    [alertsController, key]
  );
}
