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

export function useBlockingHistoryWhen(shouldBlock: boolean) {
  const history = useHistory();
  const [navigation, setNavigation] = useState({ isBlocked: false });

  const blockingHistory = useMemo(() => {
    return new Proxy<typeof history>(history, {
      get: (target: any, name) => {
        if (typeof target[name] !== "function") {
          return target[name];
        }

        if (!shouldBlock) {
          return target[name];
        }

        return () => {
          setNavigation({ isBlocked: true });
        };
      },
    });
  }, [history, shouldBlock]);

  useEffect(() => {
    if (!shouldBlock) {
      setNavigation({ isBlocked: true });
    }
  }, [shouldBlock]);

  return { blockingHistory, navigation };
}
