/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ResourceFetch, ResourceFetcher } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import {
  CreateBuildConfig,
  CreateImageStream,
  CreateKnativeService,
  DeleteBuildConfig,
  DeleteImageStream,
  DeleteKnativeService,
  DeleteSecret,
  InstantiateBinary,
  ResourceDataSource,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { RESOURCE_OWNER } from "../OpenShiftConstants";
import { OpenShiftPipeline, OpenShiftPipelineArgs } from "../OpenShiftPipeline";

interface KnativeBuilderPipelineArgs {
  resourceName: string;
  targetUri: string;
  workspaceName: string;
  workspaceZipBlob: Blob;
}

export class KnativeBuilderPipeline extends OpenShiftPipeline {
  constructor(protected readonly args: OpenShiftPipelineArgs & KnativeBuilderPipelineArgs) {
    super(args);
  }

  public async execute(): Promise<void> {
    const resourceArgs = {
      namespace: this.args.namespace,
      resourceName: this.args.resourceName,
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks: ResourceFetch[] = [
      new DeleteSecret(resourceArgs),
      new DeleteKnativeService(resourceArgs),
      new DeleteBuildConfig(resourceArgs),
      new DeleteImageStream(resourceArgs),
    ];

    let rollbacksCount = rollbacks.length;

    await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new CreateImageStream({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
      })
    );
    await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new CreateBuildConfig({ ...resourceArgs, resourceDataSource: ResourceDataSource.TEMPLATE }),
        rollbacks: rollbacks.slice(--rollbacksCount),
      })
    );
    await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new InstantiateBinary({ ...resourceArgs, zipBlob: this.args.workspaceZipBlob }),
        rollbacks: rollbacks.slice(--rollbacksCount),
      })
    );
    await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new CreateKnativeService({
          ...resourceArgs,
          uri: this.args.targetUri,
          workspaceName: this.args.workspaceName,
          resourceDataSource: ResourceDataSource.TEMPLATE,
        }),
        rollbacks: rollbacks.slice(rollbacksCount),
      })
    );
  }
}
