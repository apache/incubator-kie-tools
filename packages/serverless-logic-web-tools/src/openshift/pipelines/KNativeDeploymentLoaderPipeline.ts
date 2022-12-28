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
import { ListKNativeServices } from "@kie-tools-core/openshift/dist/api/knative/KNativeService";
import { ListBuilds } from "@kie-tools-core/openshift/dist/api/kubernetes/Build";
import { ListDeployments } from "@kie-tools-core/openshift/dist/api/kubernetes/Deployment";
import {
  BuildDescriptor,
  BuildGroupDescriptor,
  DeploymentDescriptor,
  DeploymentGroupDescriptor,
  KNativeServiceDescriptor,
  KNativeServiceGroupDescriptor,
} from "@kie-tools-core/openshift/dist/api/types";
import { ResourceFetcher } from "@kie-tools-core/openshift/dist/fetch/ResourceFetcher";
import { ResourceLabelNames } from "@kie-tools-core/openshift/dist/template/TemplateConstants";
import { RESOURCE_OWNER } from "../OpenShiftConstants";
import { WebToolsOpenShiftDeployedModel } from "../deploy/types";
import { OpenShiftPipeline } from "../OpenShiftPipeline";

export class KNativeDeploymentLoaderPipeline extends OpenShiftPipeline<WebToolsOpenShiftDeployedModel[]> {
  public async execute(): Promise<WebToolsOpenShiftDeployedModel[]> {
    try {
      const knServices = await this.args.openShiftService.withFetch((fetcher: ResourceFetcher) =>
        fetcher.execute<KNativeServiceGroupDescriptor>({
          target: new ListKNativeServices({
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
          (kns: KNativeServiceDescriptor) =>
            kns.status &&
            kns.metadata.annotations &&
            kns.metadata.labels &&
            kns.metadata.labels[ResourceLabelNames.CREATED_BY] === RESOURCE_OWNER
        )
        .map((kns: KNativeServiceDescriptor) => {
          const build = allBuilds.items.find(
            (b: BuildDescriptor) =>
              b.metadata.labels &&
              kns.metadata.labels &&
              b.metadata.labels[KubernetesLabelNames.APP] === kns.metadata.labels[KubernetesLabelNames.APP]
          );
          const resourceDeployments = allDeployments.items
            .filter(
              (d: DeploymentDescriptor) =>
                d.metadata.labels && d.metadata.labels[KNativeLabelNames.SERVICE] === kns.metadata.name
            )
            .sort(sortDeploymentsByCreationTimeFn);
          const deployment = resourceDeployments.length > 0 ? resourceDeployments[0] : undefined;
          return {
            resourceName: kns.metadata.name,
            uri: kns.metadata.annotations![ResourceLabelNames.URI],
            routeUrl: kns.status!.url,
            creationTimestamp: new Date(kns.metadata.creationTimestamp!),
            state: this.args.openShiftService.kubernetes.extractDeploymentState({ deployment, build }),
            workspaceName: kns.metadata.annotations![ResourceLabelNames.WORKSPACE_NAME],
          };
        });
    } catch (e) {
      throw new Error(`Failed to load deployments: ${e.message}`);
    }
  }
}
