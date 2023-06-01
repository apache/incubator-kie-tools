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

import { RESOURCE_OWNER } from "../OpenShiftConstants";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { OpenShiftPipeline } from "../OpenShiftPipeline";
import { ResourceFetcher } from "@kie-tools-core/kubernetes-bridge/dist/fetch";
import {
  BuildDescriptor,
  BuildGroupDescriptor,
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  KnativeLabelNames,
  KnativeServiceDescriptor,
  KnativeServiceGroupDescriptor,
  KubernetesLabelNames,
  ListBuilds,
  ListDeployments,
  ListKnativeServices,
  ResourceLabelNames,
} from "@kie-tools-core/kubernetes-bridge/dist/resources";

export class KnativeDeploymentLoaderPipeline extends OpenShiftPipeline<WebToolsOpenShiftDeployedModel[]> {
  public async execute(): Promise<WebToolsOpenShiftDeployedModel[]> {
    try {
      const knServices = await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<KnativeServiceGroupDescriptor>({
          target: new ListKnativeServices({
            namespace: this.args.namespace,
            labelSelector: ResourceLabelNames.CREATED_BY,
          }),
        })
      );

      if (knServices.items.length === 0) {
        return [];
      }

      const [allDeployments, allBuilds] = await Promise.all([
        this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
          fetcher.execute<DeploymentGroupDescriptor>({
            target: new ListDeployments({
              namespace: this.args.namespace,
            }),
          })
        ),
        this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
          fetcher.execute<BuildGroupDescriptor>({
            target: new ListBuilds({
              namespace: this.args.namespace,
            }),
          })
        ),
      ]);

      const sortDeploymentsByCreationTimeFn = (a: DeploymentDescriptor, b: DeploymentDescriptor) =>
        new Date(a.metadata.creationTimestamp!).getTime() - new Date(b.metadata.creationTimestamp!).getTime();

      return knServices.items
        .filter(
          (kns: KnativeServiceDescriptor) =>
            kns.status &&
            kns.metadata.annotations &&
            kns.metadata.labels &&
            kns.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
        )
        .map((kns: KnativeServiceDescriptor) => {
          const build = allBuilds.items.find(
            (b: BuildDescriptor) =>
              b.metadata.labels &&
              kns.metadata.labels &&
              b.metadata.labels[KubernetesLabelNames.APP] === kns.metadata.labels[KubernetesLabelNames.APP]
          );
          const resourceDeployments = allDeployments.items
            .filter(
              (d: DeploymentDescriptor) =>
                d.metadata.labels && d.metadata.labels[KnativeLabelNames.SERVICE] === kns.metadata.name
            )
            .sort(sortDeploymentsByCreationTimeFn);
          const deployment = resourceDeployments.length > 0 ? resourceDeployments[0] : undefined;
          return {
            resourceName: kns.metadata.name!,
            uri: kns.metadata.annotations![ResourceLabelNames.URI],
            routeUrl: kns.status!.url!,
            creationTimestamp: new Date(kns.metadata.creationTimestamp!),
            state: this.args.openShiftService.extractDeploymentState({ deployment, build }),
            workspaceName: kns.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
            devMode: false,
          };
        });
    } catch (e) {
      throw new Error(`Failed to load deployments: ${e.message}`);
    }
  }
}
