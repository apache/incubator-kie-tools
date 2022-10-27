import * as React from "react";
import { useState, useEffect, useCallback, useMemo } from "react";
import { isServerlessWorkflow } from "../../extension";
import { ActiveWorkspace } from "../../workspace/model/ActiveWorkspace";
import { useWorkspaces, WorkspaceFile } from "../../workspace/WorkspacesContext";
import { getVirtualServiceRegistryDependencies } from "../models/VirtualServiceRegistryFunction";
import { useVirtualServiceRegistry } from "../VirtualServiceRegistryContext";

export type VirtualServiceRegistryDependency = ActiveWorkspace & { vsrFiles: WorkspaceFile[] };

export function useVirtualServiceRegistryDependencies(props: {
  workspace?: ActiveWorkspace;
  workspaceFile?: WorkspaceFile;
  deployAsProject?: boolean;
  canUploadOpenApi?: boolean;
}) {
  const [virtualServiceRegistryDependencies, setVirtualServiceRegistryDependencies] = useState<Array<string>>([]);
  const workspaces = useWorkspaces();
  const virtualServiceRegistry = useVirtualServiceRegistry();
  const [workspaceFiles, setWorkspaceFiles] = useState<WorkspaceFile[]>([]);

  useEffect(() => {
    if (props.workspace) {
      setWorkspaceFiles(props.workspace.files);
    } else if (props.workspaceFile) {
      workspaces.getFiles({ workspaceId: props.workspaceFile.workspaceId }).then(setWorkspaceFiles);
    }
  }, [workspaces, props.workspaceFile, setWorkspaceFiles, props.workspace]);

  useEffect(() => {
    const updateWorkspacesList = async () => {
      let dependencies: Array<string> = [];
      if (props.deployAsProject) {
        if (workspaceFiles) {
          dependencies = (
            await Promise.all(
              workspaceFiles
                .map((file) => isServerlessWorkflow(file.relativePath) && getVirtualServiceRegistryDependencies(file))
                .filter((value): value is Promise<Array<string>> => Boolean(value))
            )
          ).flat();
        }
      } else if (props.workspaceFile) {
        dependencies = await getVirtualServiceRegistryDependencies(props.workspaceFile);
      }

      setVirtualServiceRegistryDependencies(dependencies);
    };
    updateWorkspacesList();
  }, [props.deployAsProject, workspaceFiles, props.workspaceFile, virtualServiceRegistry, workspaces]);

  const needsDependencyDeployment = useMemo(
    () => virtualServiceRegistryDependencies.length > 0,
    [virtualServiceRegistryDependencies]
  );

  return { needsDependencyDeployment, virtualServiceRegistryDependencies };
}
