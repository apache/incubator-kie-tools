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

import { KafkaSettingsConfig } from "../settings/kafka/KafkaSettingsConfig";
import { isOpenShiftConfigValid, OpenShiftSettingsConfig } from "../settings/openshift/OpenShiftSettingsConfig";
import { ServiceAccountSettingsConfig } from "../settings/serviceAccount/ServiceAccountConfig";
import { ExtendedServicesConfig } from "../settings/SettingsContext";
import { OpenShiftDeployedModel, OpenShiftDeployedModelState } from "./OpenShiftDeployedModel";
import { Build, Builds, ListBuilds } from "./resources/Build";
import { CreateBuildConfig, DeleteBuildConfig, InstantiateBinary } from "./resources/BuildConfig";
import { Deployment, DeploymentCondition, Deployments, ListDeployments } from "./resources/Deployment";
import { CreateImageStream, DeleteImageStream } from "./resources/ImageStream";
import { CreateKafkaSource, DeleteKafkaSource } from "./resources/KafkaSource";
import {
  CreateKNativeService,
  DeleteKNativeService,
  GetKNativeService,
  KNativeService,
  KNativeServices,
  ListKNativeServices,
} from "./resources/KNativeService";
import { GetProject } from "./resources/Project";
import {
  APP_LABEL,
  KNATIVE_SERVING_SERVICE,
  RESOURCE_CREATED_BY,
  RESOURCE_URI,
  RESOURCE_WORKSPACE_NAME,
  Resource,
  ResourceArgs,
  ResourceFetch,
} from "./resources/Resource";
import { CreateSecret, DeleteSecret } from "./resources/Secret";

export const DEVELOPER_SANDBOX_URL = "https://developers.redhat.com/developer-sandbox";
export const DEVELOPER_SANDBOX_GET_STARTED_URL = "https://developers.redhat.com/developer-sandbox/get-started";
export const DEFAULT_CREATED_BY = "kie-tools-sandbox";

export class OpenShiftService {
  private readonly PROXY_ENDPOINT = `${this.configs.extendedServicesConfig.buildUrl()}/devsandbox`;

  constructor(
    private readonly configs: {
      openShift: OpenShiftSettingsConfig;
      kafka: KafkaSettingsConfig;
      serviceAccount: ServiceAccountSettingsConfig;
      extendedServicesConfig: ExtendedServicesConfig;
    }
  ) {}

  private prepareCommonResourceArgs(): ResourceArgs {
    return {
      host: this.configs.openShift.host,
      namespace: this.configs.openShift.namespace,
      token: this.configs.openShift.token,
      createdBy: DEFAULT_CREATED_BY,
    };
  }

  public async getDeploymentRoute(resourceName: string): Promise<string | undefined> {
    const resourceArgs = this.prepareCommonResourceArgs();

    try {
      const kNativeService = await this.fetchResource<KNativeService>(
        new GetKNativeService({ ...resourceArgs, resourceName: resourceName })
      );

      return kNativeService.status.url;
    } catch (e) {
      throw new Error(`Failed to get deployment route for resource ${resourceName}`);
    }
  }

  public async loadDeployments(): Promise<OpenShiftDeployedModel[]> {
    const resourceArgs = this.prepareCommonResourceArgs();

    try {
      const kNativeServices = await this.fetchResource<KNativeServices>(new ListKNativeServices(resourceArgs));

      if (kNativeServices.items.length === 0) {
        return [];
      }

      const [deployments, builds] = await Promise.all([
        this.fetchResource<Deployments>(new ListDeployments(resourceArgs)),
        this.fetchResource<Builds>(new ListBuilds(resourceArgs)),
      ]);

      return kNativeServices.items
        .filter(
          (kns: KNativeService) =>
            RESOURCE_CREATED_BY in kns.metadata.labels &&
            kns.metadata.labels[RESOURCE_CREATED_BY] === DEFAULT_CREATED_BY
        )
        .map((kns: KNativeService) => {
          const build = builds.items.find(
            (b: Build) =>
              APP_LABEL in b.metadata.labels &&
              APP_LABEL in kns.metadata.labels &&
              b.metadata.labels[APP_LABEL] === kns.metadata.labels[APP_LABEL]
          );
          const deployment = deployments.items.find(
            (d: Deployment) =>
              KNATIVE_SERVING_SERVICE in d.metadata.labels &&
              d.metadata.labels[KNATIVE_SERVING_SERVICE] === kns.metadata.name
          );
          return {
            resourceName: kns.metadata.name,
            uri: kns.metadata.annotations[RESOURCE_URI],
            baseUrl: kns.status.url,
            creationTimestamp: new Date(kns.metadata.creationTimestamp),
            state: this.extractDeploymentState(deployment, build),
            workspaceName: kns.metadata.annotations[RESOURCE_WORKSPACE_NAME],
          };
        });
    } catch (e) {
      throw new Error(`Failed to load deployments: ${e.message}`);
    }
  }

  public async deploy(args: {
    targetFilePath: string;
    workspaceName: string;
    workspaceZipBlob: Blob;
    shouldAttachKafkaSource: boolean;
  }): Promise<string | undefined> {
    const resourceName = `sandbox-${this.generateRandomId()}`;

    const resourceArgs = {
      ...this.prepareCommonResourceArgs(),
      resourceName: resourceName,
    };

    const rollbacks = [
      new DeleteKafkaSource(resourceArgs),
      new DeleteSecret(resourceArgs),
      new DeleteKNativeService(resourceArgs),
      new DeleteBuildConfig(resourceArgs),
      new DeleteImageStream(resourceArgs),
    ];

    let rollbacksCount = rollbacks.length;

    try {
      await this.fetchResource(new CreateImageStream(resourceArgs));
      await this.fetchResource(new CreateBuildConfig(resourceArgs), rollbacks.slice(--rollbacksCount));
      await this.fetchResource(
        new InstantiateBinary({ ...resourceArgs, file: args.workspaceZipBlob }),
        rollbacks.slice(--rollbacksCount)
      );

      await this.fetchResource(
        new CreateKNativeService({
          ...resourceArgs,
          uri: args.targetFilePath,
          workspaceName: args.workspaceName,
        }),
        rollbacks.slice(rollbacksCount)
      );

      if (args.shouldAttachKafkaSource) {
        const kafkaClientIdKey = "kafka-client-id";
        const kafkaClientSecretKey = "kafka-client-secret";
        const kafkaClientMechanismKey = "kafka-client-mechanism";

        await this.fetchResource(
          new CreateSecret({
            ...resourceArgs,
            data: {
              [kafkaClientIdKey]: this.configs.serviceAccount.clientId,
              [kafkaClientSecretKey]: this.configs.serviceAccount.clientSecret,
              [kafkaClientMechanismKey]: "PLAIN",
            },
          }),
          rollbacks.slice(--rollbacksCount)
        );

        await this.fetchResource(
          new CreateKafkaSource({
            ...resourceArgs,
            sinkService: resourceName,
            bootstrapServer: this.configs.kafka.bootstrapServer,
            topic: this.configs.kafka.topic,
            secret: {
              name: resourceName,
              keyId: kafkaClientIdKey,
              keySecret: kafkaClientSecretKey,
              keyMechanism: kafkaClientMechanismKey,
            },
          }),
          rollbacks.slice(--rollbacksCount)
        );
      }

      return resourceName;
    } catch (e) {
      console.error(`Failed to deploy: ${e.message}`);
    }
  }

  private async fetchResource<T = Resource>(target: ResourceFetch, rollbacks?: ResourceFetch[]): Promise<Readonly<T>> {
    try {
      const response = await fetch(this.PROXY_ENDPOINT, await target.requestInit());
      if (response.ok) {
        return (await response.json()) as T;
      }
    } catch (e) {
      // No-op
    }
    if (rollbacks && rollbacks.length > 0) {
      for (const resource of rollbacks) {
        await this.fetchResource(resource);
      }
    }
    throw new Error(`Error fetching ${target.name()}`);
  }

  private generateRandomId(): string {
    const randomPart = Math.random().toString(36).substring(2, 9);
    const milliseconds = new Date().getMilliseconds();
    return `${randomPart}${milliseconds}`;
  }

  public async isConnectionEstablished(config: OpenShiftSettingsConfig): Promise<boolean> {
    try {
      await this.fetchResource(
        new GetProject({
          host: config.host,
          namespace: config.namespace,
          token: config.token,
        })
      );

      return true;
    } catch (error) {
      return false;
    }
  }

  public async onCheckConfig(config: OpenShiftSettingsConfig): Promise<boolean> {
    return isOpenShiftConfigValid(config) && this.isConnectionEstablished(config);
  }

  private extractDeploymentState(deployment?: Deployment, build?: Build): OpenShiftDeployedModelState {
    if (!build) {
      return OpenShiftDeployedModelState.DOWN;
    }

    if (["New", "Pending"].includes(build.status.phase)) {
      return OpenShiftDeployedModelState.PREPARING;
    }

    if (["Failed", "Error", "Cancelled"].includes(build.status.phase)) {
      return OpenShiftDeployedModelState.DOWN;
    }

    if (build.status.phase === "Running" || !deployment) {
      return OpenShiftDeployedModelState.IN_PROGRESS;
    }

    if (!deployment.status.replicas || +deployment.status.replicas === 0) {
      return OpenShiftDeployedModelState.DOWN;
    }

    const progressingCondition = deployment.status.conditions?.find(
      (condition: DeploymentCondition) => condition.type === "Progressing"
    );

    if (!progressingCondition || progressingCondition.status !== "True") {
      return OpenShiftDeployedModelState.DOWN;
    }

    if (!deployment.status.readyReplicas || +deployment.status.readyReplicas === 0) {
      return OpenShiftDeployedModelState.IN_PROGRESS;
    }

    return OpenShiftDeployedModelState.UP;
  }
}
