import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";

export type AlertDelegate = (args: { close: () => void }) => React.ReactNode;

export interface AlertsController {
  closeAll(): void;
  show(key: string): void;
  close(key: string): void;
  set(key: string, alertDelegate: AlertDelegate): void;
}

export const Alerts = React.forwardRef<AlertsController>((props: {}, forwardedRef) => {
  const [alerts, setAlerts] = useState(new Map<string, { alertDelegate: AlertDelegate; isShowing: boolean }>());

  const changeIsShowingForKeys = useCallback((keys: string[], prev: typeof alerts, args: { isShowing: boolean }) => {
    const newAlerts = new Map(prev);
    for (const key of keys) {
      newAlerts.set(key, { ...newAlerts.get(key)!, isShowing: args.isShowing });
    }
    return newAlerts;
  }, []);

  const imperativeHandle = useMemo<AlertsController>(() => {
    return {
      closeAll: () => {
        setAlerts((prev) => changeIsShowingForKeys(Array.from(prev.keys()), prev, { isShowing: false }));
      },
      close: (key) => {
        setAlerts((prev) => (prev.has(key) ? changeIsShowingForKeys([key], prev, { isShowing: false }) : prev));
      },
      show: (key) => {
        setAlerts((prev) => (prev.has(key) ? changeIsShowingForKeys([key], prev, { isShowing: true }) : prev));
      },
      set: (key, alertDelegate) => {
        setAlerts((prev) => {
          const newAlerts = new Map(prev);
          newAlerts.set(key, { alertDelegate, isShowing: newAlerts.get(key)?.isShowing ?? false });
          return newAlerts;
        });
      },
    };
  }, [changeIsShowingForKeys]);

  useImperativeHandle(forwardedRef, () => imperativeHandle, [imperativeHandle]);

  return (
    <>
      {Array.from(alerts.entries())
        .filter(([_, v]) => v.isShowing)
        .map(([key, { alertDelegate }]) => (
          <React.Fragment key={key}>{alertDelegate({ close: () => imperativeHandle.close(key) })}</React.Fragment>
        ))}
    </>
  );
});

export function useAlertsRef() {
  const [controller, setController] = useState<AlertsController | undefined>(undefined);

  const alertsRef = useCallback((controller: AlertsController) => {
    setController(controller);
  }, []);

  return { alerts: controller, alertsRef: alertsRef };
}

export function useAlert(alertsRef: AlertsController | undefined, delegate: AlertDelegate, deps: React.DependencyList) {
  const key = useMemo(() => {
    return `${Math.random()}`; //FIXME: tiago improve that.
  }, []);

  useEffect(() => {
    console.info("setting alert " + key);
    alertsRef?.set(key, delegate);
  }, [alertsRef, deps, key /* `delegate` purposefully left out. See explanation below. */]);

  /** `delegate` will be different on every pass, as it's an arrow function.
   *  We only want to update the Alert when the `deps` array change, not everytime.
   *  In a way, this hook is a specialized version of a `useCallback`.
   */

  return useMemo(
    () => ({
      close: () => alertsRef?.close(key),
      show: () => alertsRef?.show(key),
    }),
    [alertsRef, key]
  );
}
