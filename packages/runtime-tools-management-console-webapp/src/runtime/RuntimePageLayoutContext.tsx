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

import React, { createContext, PropsWithChildren, useContext, useMemo, useState } from "react";
import { Location, LocationDescriptor } from "history";
import { ManagementConsolePageLayout } from "../managementConsole/ManagementConsolePageLayout";
import { RuntimeNav } from "./RuntimeNav";

export type BreadcrumbPathType = Array<Pick<Location, "pathname" | "state"> | LocationDescriptor>;

export type RuntimePageLayoutContextType = {
  currentPageTitle: string;
  breadcrumbText: string[];
  breadcrumbPath: BreadcrumbPathType;
};

export type RuntimePageLayoutDispatchContextType = {
  setCurrentPageTitle: React.Dispatch<React.SetStateAction<string>>;
  setBreadcrumbText: React.Dispatch<React.SetStateAction<string[]>>;
  setBreadcrumbPath: React.Dispatch<React.SetStateAction<BreadcrumbPathType | undefined>>;
};

const RuntimePageLayoutContext = createContext<RuntimePageLayoutContextType>({} as RuntimePageLayoutContextType);
const RuntimePageLayoutDispatchContext = createContext<RuntimePageLayoutDispatchContextType>(
  {} as RuntimePageLayoutDispatchContextType
);

export function useRuntimePageLayout() {
  return useContext(RuntimePageLayoutContext);
}

export function useRuntimePageLayoutDispatch() {
  return useContext(RuntimePageLayoutDispatchContext);
}

export function RuntimePageLayoutContextProvider(props: PropsWithChildren<{}>) {
  const [currentPageTitle, setCurrentPageTitle] = useState("");
  const [breadcrumbText, setBreadcrumbText] = useState<string[]>([]);
  const [breadcrumbPath, setBreadcrumbPath] = useState<BreadcrumbPathType>([]);

  const value = useMemo(
    () => ({ currentPageTitle, breadcrumbText, breadcrumbPath }),
    [breadcrumbPath, breadcrumbText, currentPageTitle]
  );
  const dispatch = useMemo(() => ({ setCurrentPageTitle, setBreadcrumbText, setBreadcrumbPath }), []);

  return (
    <RuntimePageLayoutContext.Provider value={value}>
      <RuntimePageLayoutDispatchContext.Provider value={dispatch}>
        <ManagementConsolePageLayout
          currentPageTile={currentPageTitle}
          breadcrumbText={breadcrumbText}
          breadcrumbPath={breadcrumbPath}
          disabledHeader={false}
          nav={<RuntimeNav />}
        >
          {props.children}
        </ManagementConsolePageLayout>
      </RuntimePageLayoutDispatchContext.Provider>
    </RuntimePageLayoutContext.Provider>
  );
}
