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
import { ConnectionConfig } from "./ConnectionConfig";
import { DeployedModel } from "./DeployedModel";
import { DeployInstanceStatus } from "./DeployInstanceStatus";
import { DeveloperSandboxService } from "./devsandbox/DeveloperSandboxService";

export interface DeployContextType {
  deployments: DeployedModel[];
  service: DeveloperSandboxService;
  currentConfig: ConnectionConfig;
  instanceStatus: DeployInstanceStatus;
  isConfigModalOpen: boolean;
  isConfigWizardOpen: boolean;
  isDeployDropdownOpen: boolean;
  isConfirmDeployModalOpen: boolean;
  isDeployIntroductionModalOpen: boolean;
  setDeployments: React.Dispatch<DeployedModel[]>;
  setInstanceStatus: React.Dispatch<DeployInstanceStatus>;
  setConfigModalOpen: React.Dispatch<boolean>;
  setConfigWizardOpen: React.Dispatch<boolean>;
  setDeployDropdownOpen: React.Dispatch<boolean>;
  setConfirmDeployModalOpen: React.Dispatch<boolean>;
  setDeployIntroductionModalOpen: React.Dispatch<boolean>;
  onDeploy: (config: ConnectionConfig) => Promise<void>;
  onCheckConfig: (config: ConnectionConfig, persist: boolean) => Promise<boolean>;
  onResetConfig: () => void;
}

export const DeployContext = React.createContext<DeployContextType>({
  deployments: [],
  instanceStatus: DeployInstanceStatus.UNAVAILABLE,
  isConfigModalOpen: false,
  isConfigWizardOpen: false,
  isDeployDropdownOpen: false,
  isConfirmDeployModalOpen: false,
  isDeployIntroductionModalOpen: false,
} as any);

export function useDeploy() {
  return useContext(DeployContext);
}
