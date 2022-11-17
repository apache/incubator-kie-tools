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

import { isJson, isSpec } from "../../extension";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { generateOpenApiSpec } from "./BaseOpenApiSpec";
import * as yaml from "yaml";
import { toWorkspaceIdFromVsrFunctionPath } from "../VirtualServiceRegistryPathConverter";
import { VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX } from "../VirtualServiceRegistryConstants";

export class VirtualServiceRegistryFunction {
  constructor(private readonly file: WorkspaceFile) {}

  get relativePath() {
    return this.file.relativePath;
  }

  public async getOpenApiSpec(): Promise<string | undefined> {
    // Don't generate spec for files that depend on other workflows
    if (await hasVirtualServiceRegistryDependency(this.file)) {
      return;
    }

    const content = await this.file.getFileContentsAsString();
    if (!content) {
      return;
    }

    if (isSpec(this.relativePath)) {
      return content;
    }

    try {
      const parsedContent = isJson(this.file.relativePath) ? JSON.parse(content) : yaml.parse(content);
      if (parsedContent.id) {
        return generateOpenApiSpec(parsedContent.id);
      } else {
        console.debug("No workflow ID!");
      }
    } catch (e) {
      console.debug(e);
    }
  }
}

export async function getVirtualServiceRegistryDependencies(file: WorkspaceFile) {
  const content = await file.getFileContentsAsString();
  if (!content) {
    return [];
  }

  let parsedContent: Record<string, unknown>;
  try {
    parsedContent = isJson(file.relativePath) ? JSON.parse(content) : yaml.parse(content);
  } catch (e) {
    // Invalid file.
    return [];
  }
  const workflowFunctions = parsedContent["functions"] as Array<{ operation?: string }> | undefined;

  return (
    workflowFunctions
      ?.filter((workflowFunction) => workflowFunction.operation?.includes(VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX))
      .map((workflowFunction) => toWorkspaceIdFromVsrFunctionPath(workflowFunction.operation!))
      .filter((workspaceId): workspaceId is string => !!workspaceId) || []
  );
}

export async function hasVirtualServiceRegistryDependency(file: WorkspaceFile) {
  return (await getVirtualServiceRegistryDependencies(file)).length > 0;
}
