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
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { KieSandboxOpenShiftDeployedModel } from "../openshift/KieSandboxOpenShiftService";

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
      resourceNames: string[];
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
  deploy: (workspaceFile: WorkspaceFile, connection: OpenShiftConnection) => Promise<boolean>;
  loadDeployments: (args: { connection: OpenShiftConnection }) => Promise<KieSandboxOpenShiftDeployedModel[]>;
  deleteDeployment: (args: { connection: OpenShiftConnection; resourceName: string }) => Promise<boolean>;
  deleteDeployments: (args: { connection: OpenShiftConnection; resourceNames: string[] }) => Promise<boolean>;
}

export const DevDeploymentsContext = React.createContext<DevDeploymentsContextType>({
  deployments: [],
  isDropdownOpen: false,
  isDeploymentsDropdownOpen: false,
  isConfirmDeployModalOpen: false,
} as any);

export function useDevDeployments() {
  return useContext(DevDeploymentsContext);
}
