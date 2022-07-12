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
import { VirtualServiceRegistryGroup } from "../models/VirtualServiceRegistry";
import { ServiceRegistryFile } from "../models/ServiceRegistryFile";
import { WorkspaceDescriptor } from "../../../model/WorkspaceDescriptor";
import { BaseService, BaseServiceCreateProps, BaseServiceEvents } from "../../../commonServices/BaseService";

export const VIRTUAL_SERVICE_REGISTRY_BROADCAST_CHANNEL = "virtualServiceRegistry";
export const VIRTUAL_SERVICE_REGISTRY_GROUP_BROADCAST_CHANNEL = (groupId: string) =>
  `${VIRTUAL_SERVICE_REGISTRY_BROADCAST_CHANNEL}_${groupId}`;

export interface VirtualServiceRegistryCreateProps
  extends BaseServiceCreateProps<VirtualServiceRegistryGroup, ServiceRegistryFile> {
  workspaceDescriptor: WorkspaceDescriptor;
}

export class VirtualServiceRegistryService extends BaseService<
  VirtualServiceRegistryGroup,
  ServiceRegistryFile,
  VirtualServiceRegistryCreateProps
> {
  protected broadcastMessage(args: BaseServiceEvents): void {
    return;
  }

  protected newFile(id: string, relativePath: string, getFileContents: () => Promise<Uint8Array>): ServiceRegistryFile {
    return new ServiceRegistryFile({
      groupId: id,
      relativePath,
      getFileContents,
      needsWorkspaceDeploy: true,
    });
  }
}
