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
import { KieSandboxOpenShiftDeployedModel } from "../../openshift/KieSandboxOpenShiftService";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";

export interface DeploymentFile {
  path: string;
  getFileContents: () => Promise<string>;
}

export interface DmnDevSandboxContextType {
  deployments: KieSandboxOpenShiftDeployedModel[];
  isDropdownOpen: boolean;
  isDeploymentsDropdownOpen: boolean;
  isConfirmDeployModalOpen: boolean;
  setDeployments: React.Dispatch<React.SetStateAction<KieSandboxOpenShiftDeployedModel[]>>;
  setDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setDeploymentsDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setConfirmDeployModalOpen: React.Dispatch<React.SetStateAction<boolean>>;
  deploy: (workspaceFile: WorkspaceFile) => Promise<boolean>;
}

export const DmnDevSandboxContext = React.createContext<DmnDevSandboxContextType>({
  deployments: [],
  isDropdownOpen: false,
  isDeploymentsDropdownOpen: false,
  isConfirmDeployModalOpen: false,
} as any);

export function useDmnDevSandbox() {
  return useContext(DmnDevSandboxContext);
}
