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

import React, { PropsWithChildren, useEffect, useMemo, useState } from "react";
import { DEFAULT_APPDATA_VALUES } from "../AppConstants";
import { AppData, verifyDataIndex } from "../data";
import { useAppDataPromise } from "../hooks/useAppDataPromise";
import { AppContext } from "./AppContext";

export function AppContextProvider(props: PropsWithChildren<{}>) {
  const appDataPromise = useAppDataPromise();
  const [data, setData] = useState<AppData>(DEFAULT_APPDATA_VALUES);
  const [dataIndexAvailable, setDataIndexAvailable] = useState<boolean>();

  useEffect(() => {
    if (!appDataPromise.data) {
      return;
    }

    const appData = {
      appName: appDataPromise.data.appName || DEFAULT_APPDATA_VALUES.appName,
      showDisclaimer: appDataPromise.data.showDisclaimer ?? DEFAULT_APPDATA_VALUES.showDisclaimer,
      dataIndexUrl: appDataPromise.data.dataIndexUrl || DEFAULT_APPDATA_VALUES.dataIndexUrl,
    };

    setData(appData);

    document.title = appDataPromise.data.appName;

    verifyDataIndex(appData.dataIndexUrl).then(setDataIndexAvailable);
  }, [appDataPromise.data]);

  const value = useMemo(() => {
    const isDataIndexUrlRelativePath = /^\/\w+/.test(data.dataIndexUrl);
    const isDataIndexEmbedded =
      data.dataIndexUrl === DEFAULT_APPDATA_VALUES.dataIndexUrl ||
      data.dataIndexUrl.startsWith(window.location.origin) ||
      isDataIndexUrlRelativePath;

    return {
      appDataPromise,
      data,
      dataIndexAvailable,
      isDataIndexEmbedded,
      fullDataIndexUrl: (isDataIndexEmbedded ? window.location.origin : "") + data.dataIndexUrl,
    };
  }, [data, appDataPromise, dataIndexAvailable]);

  return <AppContext.Provider value={value}>{props.children}</AppContext.Provider>;
}
