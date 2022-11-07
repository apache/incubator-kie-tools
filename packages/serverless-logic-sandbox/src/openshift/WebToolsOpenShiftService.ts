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

import { KNativeLabelNames, KubernetesLabelNames } from "@kie-tools-core/openshift/dist/api/ApiConstants";
import { CreateKafkaSource, DeleteKafkaSource } from "@kie-tools-core/openshift/dist/api/knative/KafkaSource";
import {
  CreateKNativeService,
  DeleteKNativeService,
  ListKNativeServices,
} from "@kie-tools-core/openshift/dist/api/knative/KNativeService";
import { ListBuilds } from "@kie-tools-core/openshift/dist/api/kubernetes/Build";
import {
  CreateBuildConfig,
  DeleteBuildConfig,
  InstantiateBinary,
} from "@kie-tools-core/openshift/dist/api/kubernetes/BuildConfig";
import { ListDeployments } from "@kie-tools-core/openshift/dist/api/kubernetes/Deployment";
import { CreateImageStream, DeleteImageStream } from "@kie-tools-core/openshift/dist/api/kubernetes/ImageStream";
import { CreateSecret, DeleteSecret } from "@kie-tools-core/openshift/dist/api/kubernetes/Secret";
import {
  BuildDescriptor,
  BuildGroupDescriptor,
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  KNativeServiceDescriptor,
  KNativeServiceGroupDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { ResourceFetch } from "@kie-tools-core/openshift/dist/fetch/ResourceFetch";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";
import { OpenShiftConnection } from "@kie-tools-core/openshift/dist/service/OpenShiftConnection";
import { OpenShiftService, OpenShiftServiceArgs } from "@kie-tools-core/openshift/dist/service/OpenShiftService";
import { OpenShiftDeployedModel } from "@kie-tools-core/openshift/dist/service/types";
import {
  KAFKA_SOURCE_CLIENT_ID_KEY,
  KAFKA_SOURCE_CLIENT_MECHANISM_KEY,
  KAFKA_SOURCE_CLIENT_MECHANISM_PLAIN,
  KAFKA_SOURCE_CLIENT_SECRET_KEY,
  ResourceLabelNames,
} from "@kie-tools-core/openshift/dist/template/TemplateConstants";

const RESOURCE_PREFIX = "webtools";
const RESOURCE_OWNER = "kie-tools__web-tools";

export type WebToolsOpenShiftDeployedModel = OpenShiftDeployedModel & {
  uri: string;
  workspaceName: string;
};

interface KafkaSourceArgs {
  serviceAccount: {
    clientId: string;
    clientSecret: string;
  };
  bootstrapServers: string[];
  topics: string[];
}

interface DeployArgs {
  resourceName: string;
  targetUri: string;
  workspaceName: string;
  workspaceZipBlob: Blob;
  kafkaSourceArgs?: KafkaSourceArgs;
}

export class WebToolsOpenShiftService {
  private readonly openShiftService: OpenShiftService;
  constructor(private readonly args: OpenShiftServiceArgs) {
    this.openShiftService = new OpenShiftService(args);
  }

  public async isConnectionEstablished(connection: OpenShiftConnection): Promise<boolean> {
    return this.openShiftService.isConnectionEstablished(connection);
  }

  public newResourceName(): string {
    return this.openShiftService.newResourceName(RESOURCE_PREFIX);
  }

  public async getKNativeDeploymentRoute(resourceName: string): Promise<string | undefined> {
    return this.openShiftService.knative.getDeploymentRoute(resourceName);
  }

  public async loadKNativeDeployments(): Promise<WebToolsOpenShiftDeployedModel[]> {
    try {
      const knServices = await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<KNativeServiceGroupDescriptor>({
          target: new ListKNativeServices({
            namespace: this.args.connection.namespace,
            labelSelector: ResourceLabelNames.CREATED_BY,
          }),
        })
      );

      if (knServices.items.length === 0) {
        return [];
      }

      const [deployments, builds] = await Promise.all([
        this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
          fetcher.execute<DeploymentGroupDescriptor>({
            target: new ListDeployments({
              namespace: this.args.connection.namespace,
            }),
          })
        ),
        this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
          fetcher.execute<BuildGroupDescriptor>({
            target: new ListBuilds({
              namespace: this.args.connection.namespace,
            }),
          })
        ),
      ]);

      return knServices.items
        .filter(
          (kns: KNativeServiceDescriptor) =>
            kns.status &&
            kns.metadata.annotations &&
            kns.metadata.labels &&
            kns.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
        )
        .map((kns: KNativeServiceDescriptor) => {
          const build = builds.items.find(
            (b: BuildDescriptor) =>
              b.metadata.labels &&
              kns.metadata.labels &&
              b.metadata.labels[KubernetesLabelNames.APP] === kns.metadata.labels[KubernetesLabelNames.APP]
          );
          const deployment = deployments.items.find(
            (d: DeploymentDescriptor) =>
              d.metadata.labels && d.metadata.labels[KNativeLabelNames.SERVICE] === kns.metadata.name
          );
          return {
            resourceName: kns.metadata.name,
            uri: kns.metadata.annotations![ResourceLabelNames.URI],
            routeUrl: kns.status!.url,
            creationTimestamp: new Date(kns.metadata.creationTimestamp ?? Date.now()),
            state: this.openShiftService.kubernetes.extractDeploymentState({ deployment, build }),
            workspaceName: kns.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
          };
        });
    } catch (e) {
      throw new Error(`Failed to load deployments: ${e.message}`);
    }
  }

  public async deployBuilderWithBinary(args: DeployArgs): Promise<void> {
    const resourceArgs = {
      namespace: this.args.connection.namespace,
      resourceName: args.resourceName,
      createdBy: RESOURCE_OWNER,
    };

    const rollbacks: ResourceFetch[] = [
      new DeleteKafkaSource(resourceArgs),
      new DeleteSecret(resourceArgs),
      new DeleteKNativeService(resourceArgs),
      new DeleteBuildConfig(resourceArgs),
      new DeleteImageStream(resourceArgs),
    ];

    let rollbacksCount = rollbacks.length;

    await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({ target: new CreateImageStream(resourceArgs) })
    );
    await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({ target: new CreateBuildConfig(resourceArgs), rollbacks: rollbacks.slice(--rollbacksCount) })
    );
    await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new InstantiateBinary({ ...resourceArgs, zipBlob: args.workspaceZipBlob }),
        rollbacks: rollbacks.slice(--rollbacksCount),
      })
    );
    await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
      fetcher.execute({
        target: new CreateKNativeService({ ...resourceArgs, uri: args.targetUri, workspaceName: args.workspaceName }),
        rollbacks: rollbacks.slice(rollbacksCount),
      })
    );

    if (args.kafkaSourceArgs) {
      const secretData = {
        [KAFKA_SOURCE_CLIENT_ID_KEY]: args.kafkaSourceArgs.serviceAccount.clientId,
        [KAFKA_SOURCE_CLIENT_SECRET_KEY]: args.kafkaSourceArgs.serviceAccount.clientSecret,
        [KAFKA_SOURCE_CLIENT_MECHANISM_KEY]: KAFKA_SOURCE_CLIENT_MECHANISM_PLAIN,
      };
      await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateSecret({ ...resourceArgs, data: secretData }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );

      const { bootstrapServers, topics } = args.kafkaSourceArgs;
      await this.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute({
          target: new CreateKafkaSource({
            ...resourceArgs,
            sinkService: args.resourceName,
            bootstrapServers,
            topics,
            secret: {
              name: args.resourceName,
              keyId: KAFKA_SOURCE_CLIENT_ID_KEY,
              keySecret: KAFKA_SOURCE_CLIENT_SECRET_KEY,
              keyMechanism: KAFKA_SOURCE_CLIENT_MECHANISM_KEY,
            },
          }),
          rollbacks: rollbacks.slice(--rollbacksCount),
        })
      );
    }
  }
}
