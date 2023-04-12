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

import { ResourceFetch, ResourceFetcher } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import {
  CreateBuildConfig,
  CreateImageStream,
  CreateKafkaSource,
  CreateKnativeService,
  CreateSecret,
  DeleteBuildConfig,
  DeleteImageStream,
  DeleteKafkaSource,
  DeleteKnativeService,
  DeleteSecret,
  InstantiateBinary,
  KAFKA_SOURCE_CLIENT_ID_KEY,
  KAFKA_SOURCE_CLIENT_MECHANISM_KEY,
  KAFKA_SOURCE_CLIENT_MECHANISM_PLAIN,
  KAFKA_SOURCE_CLIENT_SECRET_KEY,
  ResourceDataSource,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";
import { RESOURCE_OWNER } from "../OpenShiftConstants";
import { KafkaSourceArgs } from "../deploy/types";
import { OpenShiftPipeline, OpenShiftPipelineArgs } from "../OpenShiftPipeline";

interface KnativeBuilderPipelineArgs {
  resourceName: string;
  targetUri: string;
  workspaceName: string;
  workspaceZipBlob: Blob;
  kafkaSourceArgs?: KafkaSourceArgs;
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
      new DeleteKafkaSource(resourceArgs),
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

    if (this.args.kafkaSourceArgs) {
      const secretData = {
        [KAFKA_SOURCE_CLIENT_ID_KEY]: this.args.kafkaSourceArgs.serviceAccount.clientId,
        [KAFKA_SOURCE_CLIENT_SECRET_KEY]: this.args.kafkaSourceArgs.serviceAccount.clientSecret,
        [KAFKA_SOURCE_CLIENT_MECHANISM_KEY]: KAFKA_SOURCE_CLIENT_MECHANISM_PLAIN,
      };
      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateSecret({
            ...resourceArgs,
            data: secretData,
            resourceDataSource: ResourceDataSource.TEMPLATE,
          }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );

      const { bootstrapServers, topics } = this.args.kafkaSourceArgs;
      await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateKafkaSource({
            ...resourceArgs,
            sinkService: this.args.resourceName,
            bootstrapServers,
            topics,
            secret: {
              name: this.args.resourceName,
              keyId: KAFKA_SOURCE_CLIENT_ID_KEY,
              keySecret: KAFKA_SOURCE_CLIENT_SECRET_KEY,
              keyMechanism: KAFKA_SOURCE_CLIENT_MECHANISM_KEY,
            },
            resourceDataSource: ResourceDataSource.TEMPLATE,
          }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );
    }
  }
}
