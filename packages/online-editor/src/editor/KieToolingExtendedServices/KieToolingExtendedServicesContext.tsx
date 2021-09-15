/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useContext } from "react";
import { KieToolingExtendedServicesStatus } from "./KieToolingExtendedServicesStatus";

export enum DependentFeature {
  DMN_RUNNER = "DMN_RUNNER",
  DMN_DEV_SANDBOX = "DMN_DEV_SANDBOX",
}

export interface KieToolingExtendedServicesContextType {
  status: KieToolingExtendedServicesStatus;
  port: string;
  baseUrl: string;
  version: string;
  outdated: boolean;
  isModalOpen: boolean;
  installTriggeredBy: DependentFeature;
  setStatus: React.Dispatch<KieToolingExtendedServicesStatus>;
  setModalOpen: React.Dispatch<boolean>;
  setInstallTriggeredBy: React.Dispatch<DependentFeature>;
  saveNewPort: React.Dispatch<string>;
}

export const KieToolingExtendedServicesContext = React.createContext<KieToolingExtendedServicesContextType>({
  status: KieToolingExtendedServicesStatus.UNAVAILABLE,
  isModalOpen: false,
} as any);

export function useKieToolingExtendedServices() {
  return useContext(KieToolingExtendedServicesContext);
}
