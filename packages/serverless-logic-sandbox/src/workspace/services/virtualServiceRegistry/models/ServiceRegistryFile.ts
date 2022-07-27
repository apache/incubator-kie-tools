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

import { BaseFile, BaseFileProps } from "../../../commonServices/BaseFile";

export interface ServiceRegistryFileProps extends BaseFileProps {
  groupId: string;
  needsWorkspaceDeploy: boolean;
}

export class ServiceRegistryFile extends BaseFile {
  constructor(protected readonly args: ServiceRegistryFileProps) {
    super(args);
  }

  get groupId() {
    return this.args.groupId;
  }

  get relatedWorkspaceId() {
    return this.args.groupId;
  }

  get needsWorkspaceDeploy() {
    return this.args.needsWorkspaceDeploy;
  }

  get parentId() {
    return this.groupId;
  }
}
