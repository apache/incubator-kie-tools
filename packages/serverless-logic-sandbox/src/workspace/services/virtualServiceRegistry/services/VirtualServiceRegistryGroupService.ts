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

import KieSandboxFs from "@kie-tools/kie-sandbox-fs";
import { StorageService } from "../../../commonServices/StorageService";
import { groupPath, VirtualServiceRegistryGroup } from "../models/VirtualServiceRegistry";
import { WorkspaceDescriptor } from "../../../model/WorkspaceDescriptor";
import { DescriptorService } from "../../../commonServices/DescriptorService";

const VIRTUAL_SERVICE_REGISTRY_GROUP_FS_NAME = "registryGroup";

type CreateGroupDescriptorArgs = { workspaceDescriptor: WorkspaceDescriptor };

export class VirtualServiceRegistryGroupService extends DescriptorService<
  VirtualServiceRegistryGroup,
  CreateGroupDescriptorArgs
> {
  constructor(protected readonly storageService: StorageService, descriptorsFs?: KieSandboxFs) {
    super(
      storageService,
      {
        descriptorFsName: VIRTUAL_SERVICE_REGISTRY_GROUP_FS_NAME,
        idField: "groupId",
        nameField: "groupName",
      },
      descriptorsFs
    );
  }

  public getDescriptorPath(id: string): string {
    return `/${groupPath({ groupId: id })}`;
  }

  public createNewDescriptor(args: CreateGroupDescriptorArgs): VirtualServiceRegistryGroup {
    return {
      groupId: args.workspaceDescriptor.workspaceId,
      groupName: args.workspaceDescriptor.name,
      createdDateISO: new Date().toISOString(),
      lastUpdatedDateISO: new Date().toISOString(),
    };
  }
}
