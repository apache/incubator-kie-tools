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
import { useWorkspaces, WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ActiveWorkspace } from "@kie-tools-core/workspaces-git-fs/dist/model/ActiveWorkspace";
import { useState, useEffect, useMemo } from "react";
import { isServerlessWorkflow } from "../../extension";
import { getVirtualServiceRegistryDependencies } from "../models/VirtualServiceRegistryFunction";
import { useVirtualServiceRegistry } from "../VirtualServiceRegistryContext";

export type VirtualServiceRegistryDependency = ActiveWorkspace & { vsrFiles: WorkspaceFile[] };

export function useVirtualServiceRegistryDependencies(props: { workspace: ActiveWorkspace }) {
  const [virtualServiceRegistryDependencies, setVirtualServiceRegistryDependencies] = useState<Array<string>>([]);
  const workspaces = useWorkspaces();
  const virtualServiceRegistry = useVirtualServiceRegistry();

  useEffect(() => {
    const updateWorkspacesList = async () => {
      const dependencies: Array<string> = (
        await Promise.all(
          props.workspace.files
            .map((file) => isServerlessWorkflow(file.relativePath) && getVirtualServiceRegistryDependencies(file))
            .filter((value): value is Promise<Array<string>> => Boolean(value))
        )
      ).flat();

      setVirtualServiceRegistryDependencies(dependencies);
    };
    updateWorkspacesList();
  }, [props.workspace, virtualServiceRegistry, workspaces]);

  const needsDependencyDeployment = useMemo(
    () => virtualServiceRegistryDependencies.length > 0,
    [virtualServiceRegistryDependencies]
  );

  return { needsDependencyDeployment, virtualServiceRegistryDependencies };
}
