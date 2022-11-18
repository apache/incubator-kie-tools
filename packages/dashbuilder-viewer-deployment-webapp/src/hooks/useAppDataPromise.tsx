/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { useCallback } from "react";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { AppData, fetchAppData } from "../data";

export function useAppDataPromise() {
  const [appDataPromise, setAppDataPromise] = usePromiseState<{ appData: AppData }>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        fetchAppData()
          .then((appData) => {
            if (canceled.get()) {
              return;
            }

            if (!appData) {
              setAppDataPromise({ error: "Cannot fetch data file" });
              return;
            }

            if (!appData.primary) {
              setAppDataPromise({ error: "Missing primary dashboard" });
              return;
            }

            setAppDataPromise({ data: { appData } });
          })
          .catch((e) => {
            setAppDataPromise({ error: e });
          });
      },
      [setAppDataPromise]
    )
  );

  return appDataPromise;
}
