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
import { DeployedModel } from "./DeployedModel";
import { DmnDevSandboxConnectionConfig } from "./DmnDevSandboxConnectionConfig";
import { DmnDevSandboxInstanceStatus } from "./DmnDevSandboxInstanceStatus";

export interface DmnDevSandboxContextType {
  deployments: DeployedModel[];
  currentConfig: DmnDevSandboxConnectionConfig;
  instanceStatus: DmnDevSandboxInstanceStatus;
  isDropdownOpen: boolean;
  isConfigModalOpen: boolean;
  isConfigWizardOpen: boolean;
  isConfirmDeployModalOpen: boolean;
  setDeployments: React.Dispatch<DeployedModel[]>;
  setInstanceStatus: React.Dispatch<DmnDevSandboxInstanceStatus>;
  setDropdownOpen: React.Dispatch<boolean>;
  setConfigModalOpen: React.Dispatch<boolean>;
  setConfigWizardOpen: React.Dispatch<boolean>;
  setConfirmDeployModalOpen: React.Dispatch<boolean>;
  onDeploy: (config: DmnDevSandboxConnectionConfig) => Promise<void>;
  onCheckConfig: (config: DmnDevSandboxConnectionConfig, persist: boolean) => Promise<boolean>;
  onResetConfig: () => void;
}

export const DmnDevSandboxContext = React.createContext<DmnDevSandboxContextType>({
  deployments: [],
  instanceStatus: DmnDevSandboxInstanceStatus.UNAVAILABLE,
  isDropdownOpen: false,
  isConfigModalOpen: false,
  isConfigWizardOpen: false,
  isConfirmDeployModalOpen: false,
} as any);

export function useDmnDevSandbox() {
  return useContext(DmnDevSandboxContext);
}
