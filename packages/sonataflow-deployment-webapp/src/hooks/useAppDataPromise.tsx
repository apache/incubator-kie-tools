/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { useCallback } from "react";
import { usePromiseState } from "@kie-tools-core/react-hooks/dist/PromiseState";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { AppData, fetchAppData } from "../data";
import { DEFAULT_APPDATA_VALUES } from "../AppConstants";

export function useAppDataPromise() {
  const [appDataPromise, setAppDataPromise] = usePromiseState<AppData>();

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        fetchAppData()
          .then((data) => {
            if (canceled.get()) {
              return;
            }

            if (!data) {
              setAppDataPromise({ error: "Cannot fetch data file" });
              return;
            }

            setAppDataPromise({ data: { ...DEFAULT_APPDATA_VALUES, ...data } });
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
