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

import * as React from "react";
import { useContext } from "react";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { CloudAuthSession } from "../authSessions/AuthSessionApi";
import { KieSandboxDevDeploymentsService } from "./services/KieSandboxDevDeploymentsService";
import { KieSandboxDeployment } from "./services/types";
import { K8sResourceYaml } from "@kie-tools-core/k8s-yaml-to-apiserver-requests/dist";
import { DeploymentOption } from "./services/deploymentOptions/types";

export interface DeploymentFile {
  path: string;
  getFileContents: () => Promise<string>;
}

export type ConfirmDeployModalState =
  | {
      isOpen: true;
      cloudAuthSessionId: string;
    }
  | {
      isOpen: false;
    };

export type DeleteDeployModalState =
  | {
      isOpen: true;
      cloudAuthSessionId: string;
      resources: K8sResourceYaml[];
    }
  | {
      isOpen: false;
    };

export interface DevDeploymentsContextType {
  // Dropdowns
  isDeployDropdownOpen: boolean;
  setDeployDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;
  isDeploymentsDropdownOpen: boolean;
  setDeploymentsDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;

  // Modals
  confirmDeployModalState: ConfirmDeployModalState;
  setConfirmDeployModalState: React.Dispatch<React.SetStateAction<ConfirmDeployModalState>>;
  confirmDeleteModalState: DeleteDeployModalState;
  setConfirmDeleteModalState: React.Dispatch<React.SetStateAction<DeleteDeployModalState>>;

  // Actions
  deploy: (
    workspaceFile: WorkspaceFile,
    authSession: CloudAuthSession,
    deploymentOption: DeploymentOption,
    deploymentParameters: Record<string, string | number | boolean>
  ) => Promise<boolean>;
  loadDevDeployments: (args: { authSession: CloudAuthSession }) => Promise<KieSandboxDeployment[]>;
  deleteDeployments: (args: { authSession: CloudAuthSession; resources: K8sResourceYaml[] }) => Promise<boolean>;

  // Services
  devDeploymentsServices: Map<string, KieSandboxDevDeploymentsService>;
}

export const DevDeploymentsContext = React.createContext<DevDeploymentsContextType>({
  isDropdownOpen: false,
  isDeploymentsDropdownOpen: false,
  isConfirmDeployModalOpen: false,
  devDeploymentsServices: new Map<string, KieSandboxDevDeploymentsService>(),
} as any);

export function useDevDeployments() {
  return useContext(DevDeploymentsContext);
}
