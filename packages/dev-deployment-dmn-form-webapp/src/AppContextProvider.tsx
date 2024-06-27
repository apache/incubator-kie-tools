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

import React, { ReactNode, useCallback, useState, useMemo, useEffect } from "react";
import * as path from "path";
import { AppContext } from "./AppContext";
import { AppData, fetchAppData } from "./DmnDevDeploymentFormWebAppDataApi";
import { useCancelableEffect } from "@kie-tools-core/react-hooks/dist/useCancelableEffect";
import { useEnv } from "./env/hooks/EnvContext";

interface Props {
  children: ReactNode;
}

// The following regular expression matches everything between the first and last '/'.
const re = new RegExp("^([^/]*)/|/(?=[^/]*$)", "g");

export function AppContextProvider(props: Props) {
  const [fetchDone, setFetchDone] = useState(false);
  const [data, setData] = useState<AppData>();
  const { env } = useEnv();

  const quarkusAppOrigin = useMemo(
    () =>
      env.DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_ORIGIN?.length
        ? env.DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_ORIGIN
        : window.location.origin,
    [env.DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_ORIGIN]
  );
  const quarkusAppPath = useMemo(
    () =>
      env.DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_PATH?.length
        ? env.DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_PATH
        : path.join(window.location.pathname, window.location.pathname !== "/" ? ".." : "").replace(re, "$1"),
    [env.DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_PATH]
  );

  useCancelableEffect(
    useCallback(
      ({ canceled }) => {
        fetchAppData({ quarkusAppOrigin, quarkusAppPath })
          .then((data: AppData) => {
            if (canceled.get()) {
              return;
            }
            setData(data);
          })
          .catch((error: any) => console.error(error))
          .finally(() => setFetchDone(true));
      },
      [quarkusAppOrigin, quarkusAppPath]
    )
  );

  return (
    <AppContext.Provider value={{ fetchDone, data, quarkusAppOrigin, quarkusAppPath }}>
      {props.children}
    </AppContext.Provider>
  );
}
