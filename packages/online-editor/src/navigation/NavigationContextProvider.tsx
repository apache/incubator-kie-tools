/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useEffect, useMemo, useState } from "react";
import { useHistory } from "react-router";
import { Location } from "history";

export type BlockerDelegate = (args: { location: Location }) => boolean;

export interface NavigationBlockerContextType {
  addBlocker: (name: string, blocker: BlockerDelegate) => void;
  removeBlocker: (name: string) => void;
  block: (location: Location) => void;
  unblock: () => void;
  bypass: (callback: () => void) => void;
}

export interface NavigationStatusContextType {
  blockers: Map<string, BlockerDelegate>;
  lastBlockedLocation: Location | undefined;
  bypassBlockers: boolean;
}

export const NavigationBlockerContext = React.createContext<NavigationBlockerContextType>({} as any);
export const NavigationStatusContext = React.createContext<NavigationStatusContextType>({} as any);

export function NavigationContextProvider(props: { children: React.ReactNode }) {
  const history = useHistory();

  const [status, setStatus] = useState<NavigationStatusContextType>({
    blockers: new Map(),
    lastBlockedLocation: undefined,
    bypassBlockers: false,
  });

  const blockerCtx: NavigationBlockerContextType = useMemo(
    () => ({
      block: (location) => setStatus((prev) => ({ ...prev, lastBlockedLocation: location })),
      unblock: () => setStatus((prev) => ({ ...prev, lastBlockedLocation: undefined })),
      bypass: (callback: () => void) => {
        setStatus((prev) => ({ ...prev, bypassBlockers: true }));
        setTimeout(() => {
          callback();
          setStatus((prev) => ({ ...prev, bypassBlockers: false, lastBlockedLocation: undefined }));
        }, 0);
      },
      addBlocker: (name, blocker) =>
        setStatus((prev) => {
          if (prev.blockers.get(name) === blocker) {
            return prev;
          }
          const newBlockers = new Map(prev.blockers);
          newBlockers.set(name, blocker);
          return { ...prev, blockers: newBlockers };
        }),
      removeBlocker: (name) =>
        setStatus((prev) => {
          if (!prev.blockers.has(name)) {
            return prev;
          }

          const newBlockers = new Map(prev.blockers);
          newBlockers.delete(name);
          return { ...prev, blockers: newBlockers };
        }),
    }),
    []
  );

  useEffect(() => {
    const cleanup = history.block((location, action) => {
      // history.replace is usually necessary for plumbing, so no reason to block.
      if (action === "REPLACE") {
        return;
      }

      blockerCtx.unblock();

      if (status.bypassBlockers) {
        return;
      }

      const shouldBlock = [...status.blockers.values()].reduce((acc, blockerDelegate) => {
        return acc || blockerDelegate({ location });
      }, false);

      if (!shouldBlock) {
        return;
      }

      blockerCtx.block(location);
      return false;
    });

    return () => {
      cleanup();
    };
  }, [blockerCtx, history, status.blockers, status.bypassBlockers]);

  return (
    <NavigationBlockerContext.Provider value={blockerCtx}>
      <NavigationStatusContext.Provider value={status}>
        <>{props.children}</>
      </NavigationStatusContext.Provider>
    </NavigationBlockerContext.Provider>
  );
}
