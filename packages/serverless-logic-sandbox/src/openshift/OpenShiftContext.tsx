/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
import { WorkspaceFile } from "../workspace/WorkspacesContext";
import { OpenShiftDeployedModel } from "./OpenShiftDeployedModel";

export interface OpenShiftContextType {
  deployments: OpenShiftDeployedModel[];

  isDeployDropdownOpen: boolean;
  setDeployDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;

  isDeploymentsDropdownOpen: boolean;
  setDeploymentsDropdownOpen: React.Dispatch<React.SetStateAction<boolean>>;

  isConfirmDeployModalOpen: boolean;
  setConfirmDeployModalOpen: React.Dispatch<React.SetStateAction<boolean>>;

  deploy(args: {
    workspaceFile: WorkspaceFile;
    shouldAttachKafkaSource: boolean;
    shouldDeployAsProject: boolean;
  }): Promise<string | undefined>;
  uploadArtifactToServiceRegistry(artifactId: string, content: string): Promise<void>;
  fetchOpenApiFile(resourceName: string): Promise<string | undefined>;
}

export const OpenShiftContext = React.createContext<OpenShiftContextType>({} as any);

export function useOpenShift() {
  return useContext(OpenShiftContext);
}
