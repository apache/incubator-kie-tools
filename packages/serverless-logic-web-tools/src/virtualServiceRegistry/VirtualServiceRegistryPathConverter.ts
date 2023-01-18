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

import {
  VIRTUAL_SERVICE_REGISTRY_MOUNT_POINT,
  VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX,
} from "./VirtualServiceRegistryConstants";

export function toVsrMountPoint(id: string): string {
  return `${VIRTUAL_SERVICE_REGISTRY_MOUNT_POINT}${id}`;
}

export function toVsrFunctionPathFromWorkspaceFilePath(args: { vsrWorkspaceId: string; relativePath: string }) {
  return `${toVsrWorkspacePath(args.vsrWorkspaceId)}/${args.relativePath}`;
}

function toVsrWorkspacePath(vsrWorkspaceId: string): string {
  return `${VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX}${vsrWorkspaceId}`;
}

export function toWorkspaceIdFromVsrFunctionPath(functionPath: string) {
  const regex = new RegExp(`${VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX}(.*)/`, "g");
  const result = regex.exec(functionPath);
  return result?.[1];
}
