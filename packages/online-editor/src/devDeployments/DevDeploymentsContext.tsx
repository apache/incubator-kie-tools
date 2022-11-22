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
import { KieSandboxOpenShiftDeployedModel } from "../openshift/KieSandboxOpenShiftService";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";

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

export interface DevDeploymentsContextType {
  isDropdownOpen: boolean;
  setDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;

  confirmDeployModalState: ConfirmDeployModalState;
  setConfirmDeployModalState: React.Dispatch<React.SetStateAction<ConfirmDeployModalState>>;
  isConfirmDeleteModalOpen: boolean;
  setConfirmDeleteModalOpen: React.Dispatch<React.SetStateAction<boolean>>;
  isDeploymentsDropdownOpen: boolean;
  setDeploymentsDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;

  deployments: KieSandboxOpenShiftDeployedModel[];
  setDeployments: React.Dispatch<React.SetStateAction<KieSandboxOpenShiftDeployedModel[]>>;
  deploymentsToBeDeleted: string[];
  setDeploymentsToBeDeleted: React.Dispatch<React.SetStateAction<string[]>>;

  deleteDeployment: (resourceName: string) => Promise<boolean>;
  deleteDeployments: () => Promise<boolean>;
  loadDeployments: (errCallback?: () => void) => Promise<void>;

  deploy: (workspaceFile: WorkspaceFile, connection: OpenShiftConnection) => Promise<boolean>;
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
