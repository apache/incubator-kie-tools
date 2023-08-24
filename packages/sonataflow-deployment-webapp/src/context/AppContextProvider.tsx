/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, { PropsWithChildren, useEffect, useMemo, useState } from "react";
import { AppData } from "../data";
import { useAppDataPromise } from "../hooks/useAppDataPromise";
import { AppContext } from "./AppContext";

export function AppContextProvider(props: PropsWithChildren<{}>) {
  const appDataPromise = useAppDataPromise();
  const [data, setData] = useState<AppData>({ appName: "Deployment", openApiUrl: "" });

  useEffect(() => {
    if (!appDataPromise.data) {
      return;
    }

    setData(appDataPromise.data);

    document.title = appDataPromise.data.appName;
  }, [appDataPromise.data]);

  const value = useMemo(
    () => ({
      appDataPromise,
      data,
    }),
    [data, appDataPromise]
  );

  return <AppContext.Provider value={value}>{props.children}</AppContext.Provider>;
}
