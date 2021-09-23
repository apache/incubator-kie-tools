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

export function useAlert(alertsController: AlertsController | undefined, delegate: AlertDelegate) {
  //FIXME: tiago improve that.
  const key = useMemo(() => `${Math.random()}`, []);

  useEffect(() => {
    alertsController?.set(key, delegate);
  }, [alertsController, key, delegate]);

  return useMemo(
    () => ({
      close: () => alertsController?.close(key),
      show: () => alertsController?.show(key),
    }),
    [alertsController, key]
  );
}
