/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { createContext, useContext, useMemo } from "react";
import { useSyncedCompanionFs } from "../companionFs/CompanionFsHooks";
import { PreviewSvgsService } from "./PreviewSvgsService";

export interface PreviewSvgsContextType {
  previewSvgService: PreviewSvgsService;
}

export const PreviewSvgsContext = createContext<PreviewSvgsContextType>({} as any);

export function PreviewSvgsContextProvider(props: React.PropsWithChildren<{}>) {
  const previewSvgService = useMemo(() => new PreviewSvgsService(), []);

  useSyncedCompanionFs(previewSvgService.companionFsService);

  return <PreviewSvgsContext.Provider value={{ previewSvgService }}>{props.children}</PreviewSvgsContext.Provider>;
}

export function usePreviewSvgs() {
  return useContext(PreviewSvgsContext);
}
