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
import { ExtendedServicesConfig } from "../settings/SettingsContext";
import { KieSandboxExtendedServicesStatus } from "./KieSandboxExtendedServicesStatus";

export enum DependentFeature {
  OPENSHIFT = "OPENSHIFT",
}

export interface KieSandboxExtendedServicesContextType {
  status: KieSandboxExtendedServicesStatus;
  setStatus: React.Dispatch<KieSandboxExtendedServicesStatus>;
  config: ExtendedServicesConfig;
  saveNewConfig: React.Dispatch<ExtendedServicesConfig>;
  version: string;
  outdated: boolean;
  isModalOpen: boolean;
  setModalOpen: React.Dispatch<boolean>;
  installTriggeredBy?: DependentFeature;
  setInstallTriggeredBy: React.Dispatch<React.SetStateAction<DependentFeature | undefined>>;
}

export const KieSandboxExtendedServicesContext = React.createContext<KieSandboxExtendedServicesContextType>({} as any);

export function useKieSandboxExtendedServices() {
  return useContext(KieSandboxExtendedServicesContext);
}
