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
import { useCallback, useMemo, useState } from "react";
import { Holder } from "./Holder";
import { useCancelableEffect } from "./useCancelableEffect";

export type Pending<T> = { status: PromiseStateStatus.PENDING; data?: undefined; error?: undefined };
export type Resolved<T> = { status: PromiseStateStatus.RESOLVED; data: T; error?: undefined };
export type Rejected<T> = { status: PromiseStateStatus.REJECTED; data?: undefined; error: string[] };
export type PromiseState<T> = Resolved<T> | Pending<T> | Rejected<T>;
export type Unwrapped<T> = { [K in keyof T]: T[K] extends PromiseState<infer U> ? U : never };
export type NewStateArgs<T> =
  | { loading?: false; data: T; error?: undefined }
  | { loading?: false; data?: undefined; error: string }
  | { loading: true; data?: undefined; error?: undefined };

export enum PromiseStateStatus {
  PENDING,
  RESOLVED,
  REJECTED,
}

export function useDelay(ms: number) {
  const [resolved, setResolved] = usePromiseState<boolean>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        setTimeout(() => {
          if (canceled.get()) {
            return;
          }
          setResolved({ data: true });
        }, ms);
      },
      [setResolved, ms]
    )
  );

  return resolved;
}

export function useDelayedPromiseState<T>(ms: number): [PromiseState<T>, (newState: NewStateArgs<T>) => void] {
  const delay = useDelay(ms);
  const [state, setState] = usePromiseState<T>();

  const combined = useCombinedPromiseState({ state, delay });

  const ret: PromiseState<T> = useMemo(() => {
    switch (combined.status) {
      case PromiseStateStatus.PENDING:
        return { status: PromiseStateStatus.PENDING };
      case PromiseStateStatus.REJECTED:
        return { status: PromiseStateStatus.REJECTED, error: combined.error };
      case PromiseStateStatus.RESOLVED:
        return { status: PromiseStateStatus.RESOLVED, data: combined.data.state };
    }
  }, [combined]);

  return [ret, setState];
}

export function usePromiseState<T>(): [
  PromiseState<T>,
  (newState: NewStateArgs<T> | ((prevState: T | undefined) => NewStateArgs<T>)) => void
] {
  const [state, setState] = useState<PromiseState<T>>({ status: PromiseStateStatus.PENDING });

  const set = useCallback((newState) => {
    return setState((prev) => {
      const ns = typeof newState == "function" ? newState(prev.data) : newState;

      if (ns.error) {
        return { status: PromiseStateStatus.REJECTED, error: [ns.error] };
      } else if (ns.data !== undefined) {
        return { status: PromiseStateStatus.RESOLVED, data: ns.data };
      } else if (ns.loading) {
        return { status: PromiseStateStatus.PENDING };
      } else {
        throw new Error("Invalid promise state");
      }
    });
  }, []);

  return [state, set];
}

export function useCombinedPromiseState<T extends { [key: string]: PromiseState<any> }>(
  args: T
): PromiseState<Unwrapped<T>> {
  return useMemo(() => {
    const statuses = new Map<PromiseStateStatus, number>();
    const data: Unwrapped<T> = {} as any;
    let error: string[] = [];

    Object.entries(args).forEach(([key, state]) => {
      statuses.set(state.status, (statuses.get(state.status) ?? 0) + 1);
      data[key as keyof T] = state.data;
      error = [...error, ...(state.error ?? [])];
    });

    if (statuses.get(PromiseStateStatus.PENDING) ?? 0 > 0) {
      return { status: PromiseStateStatus.PENDING };
    }

    if (statuses.get(PromiseStateStatus.REJECTED) ?? 0 > 0) {
      return { status: PromiseStateStatus.REJECTED, error };
    }

    return { status: PromiseStateStatus.RESOLVED, data };
  }, [args]);
}

export function PromiseStateWrapper<T>(props: {
  promise: PromiseState<T>;
  pending?: React.ReactNode;
  resolved?: (data: Resolved<T>["data"]) => React.ReactNode;
  rejected?: (error: Rejected<T>["error"]) => React.ReactNode;
}) {
  const component = useMemo(() => {
    switch (props.promise.status) {
      case PromiseStateStatus.PENDING:
        return props.pending ?? <></>;
      case PromiseStateStatus.REJECTED:
        return props.rejected?.(props.promise.error) ?? <></>;
      case PromiseStateStatus.RESOLVED:
        return props.resolved?.(props.promise.data) ?? <></>;
    }
  }, [props]);

  return <>{component}</>;
}

export function useLivePromiseState<T>(
  promiseDelegate: (() => Promise<T>) | { error: string }
): [PromiseState<T>, (canceled: Holder<boolean>) => void] {
  const [state, setState] = usePromiseState<T>();

  const refresh = useCallback(
    (canceled: Holder<boolean>) => {
      if (typeof promiseDelegate !== "function") {
        setState({ error: promiseDelegate.error });
        return;
      }
      setState({ loading: true });
      promiseDelegate()
        .then((refs) => {
          if (canceled.get()) {
            return;
          }
          setState({ data: refs });
        })
        .catch((e) => {
          if (canceled.get()) {
            return;
          }
          console.log(e);
          setState({ error: e });
        });
    },
    [promiseDelegate, setState]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        refresh(canceled);
      },
      [refresh]
    )
  );
  return [state, refresh];
}
