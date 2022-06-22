/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { extractFunctions } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/openapi";
import { SearchedArtifact } from "@rhoas/registry-instance-sdk";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import * as yaml from "yaml";
import { VirtualServiceRegistryContextType } from "../../workspace/services/virtualServiceRegistry/VirtualServiceRegistryContext";
import { ServiceAccountSettingsConfig } from "../../settings/serviceAccount/ServiceAccountConfig";
import { ServiceRegistrySettingsConfig } from "../../settings/serviceRegistry/ServiceRegistryConfig";
import { ExtendedServicesConfig } from "../../settings/SettingsContext";
import { VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX } from "../../workspace/services/virtualServiceRegistry/models/VirtualServiceRegistry";

export const VIRTUAL_SERVICE_REGISTRY_NAME = "Sandbox";

type ArtifactWithContent = {
  metadata: SearchedArtifact;
  content: OpenAPIV3.Document<{}>;
};

export class SwfServiceCatalogStore {
  private readonly PROXY_ENDPOINT = `${this.configs.extendedServicesConfig.buildUrl()}/devsandbox`;

  private readonly COMMON_HEADERS = {
    // We are facing a 401 Error when using oauth, let's use Basic auth for now.
    Authorization:
      "Basic " + btoa(`${this.configs.serviceAccount.clientId}:${this.configs.serviceAccount.clientSecret}`),
    "Content-Type": "application/json",
  };

  private readonly SERVICE_REGISTRY_API = {
    getArtifactContentUrl: (artifact: SearchedArtifact) =>
      `${this.configs.serviceRegistry.coreRegistryApi}/groups/${artifact.groupId}/artifacts/${artifact.id}`,
    getArtifactsUrl: () => `${this.configs.serviceRegistry.coreRegistryApi}/search/artifacts`,
  };

  private storedServices: SwfServiceCatalogService[] = [];

  public virtualServiceRegistry?: VirtualServiceRegistryContextType;
  public currentWorkspaceId?: string;

  constructor(
    private readonly configs: {
      serviceAccount: ServiceAccountSettingsConfig;
      serviceRegistry: ServiceRegistrySettingsConfig;
      extendedServicesConfig: ExtendedServicesConfig;
    }
  ) {}

  get services(): SwfServiceCatalogService[] {
    return this.storedServices;
  }

  public setVirtualServiceRegistry(virtualServiceRegistry: VirtualServiceRegistryContextType, workspaceId: string) {
    this.virtualServiceRegistry = virtualServiceRegistry;
    this.currentWorkspaceId = workspaceId;
  }

  public async refresh() {
    let artifacts: SearchedArtifact[] = [];
    let artifactsWithContent: ArtifactWithContent[] = [];

    try {
      artifacts = (
        await axios.get(this.PROXY_ENDPOINT, {
          headers: {
            ...this.COMMON_HEADERS,
            "Target-Url": this.SERVICE_REGISTRY_API.getArtifactsUrl(),
          },
        })
      ).data.artifacts;
    } catch (e) {
      console.error(e);
    }

    if (artifacts) {
      artifactsWithContent = await Promise.all(
        artifacts
          .filter((artifact) => artifact.type === "OPENAPI")
          .map(async (artifactMetadata) => ({
            metadata: artifactMetadata,
            content: (
              await axios.get(this.PROXY_ENDPOINT, {
                headers: {
                  ...this.COMMON_HEADERS,
                  "Target-Url": this.SERVICE_REGISTRY_API.getArtifactContentUrl(artifactMetadata),
                },
              })
            ).data as OpenAPIV3.Document,
          }))
      );
    }

    let virtualRegistry: ArtifactWithContent[] = [];

    if (this.virtualServiceRegistry && this.currentWorkspaceId) {
      const virtualServiceRegistryGroups = await this.virtualServiceRegistry.vsrGroupService.listAll();
      const virtualServiceRegistryGroupsFiles = await Promise.all(
        virtualServiceRegistryGroups.map(async (registryGroup) => {
          return {
            registryGroup,
            files: await this.virtualServiceRegistry!.getFiles({
              fs: await this.virtualServiceRegistry!.vsrFsService.getFs(registryGroup.groupId),
              groupId: registryGroup.groupId,
            }),
          };
        })
      );

      const virtualServiceRegistryGroupsFilesWithContent = await Promise.all(
        virtualServiceRegistryGroupsFiles.map(async (groupFiles) => {
          return await Promise.all(
            groupFiles.files.map(async (file) => ({
              metadata: {
                groupId: groupFiles.registryGroup.groupId,
                id: file.relativePath,
                type: "OPENAPI",
              } as SearchedArtifact,
              content: yaml.parse(await file.getFileContentsAsString()) as OpenAPIV3.Document,
            }))
          );
        })
      );

      virtualRegistry = virtualServiceRegistryGroupsFilesWithContent
        .flat()
        .filter((file) => file.content && file.metadata.groupId !== this.currentWorkspaceId);
    }

    this.storedServices = artifactsWithContent.concat(virtualRegistry).map((artifact) => {
      const serviceId = artifact.metadata.id;

      const isVirtualServiceRegistry = serviceId.startsWith(VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX);

      const swfFunctions = extractFunctions(
        artifact.content,
        isVirtualServiceRegistry
          ? {
              type: SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY,
              registry: VIRTUAL_SERVICE_REGISTRY_NAME,
              serviceId: serviceId.replace(VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX, ""),
            }
          : {
              type: SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY,
              registry: this.configs.serviceRegistry.name,
              serviceId,
            }
      );

      return {
        name: artifact.metadata.id,
        rawContent: yaml.stringify(artifact.content),
        type: SwfServiceCatalogServiceType.rest,
        functions: swfFunctions,
        source: isVirtualServiceRegistry
          ? {
              url: serviceId,
              id: serviceId,
              registry: VIRTUAL_SERVICE_REGISTRY_NAME,
              type: SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY,
            }
          : {
              url: this.SERVICE_REGISTRY_API.getArtifactContentUrl(artifact.metadata),
              id: serviceId,
              registry: this.configs.serviceRegistry.name,
              type: SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY,
            },
      };
    });
  }

  public async uploadArtifact(args: { groupId: string; artifactId: string; content: string }): Promise<void> {
    await axios.post(this.PROXY_ENDPOINT, args.content, {
      headers: {
        ...this.COMMON_HEADERS,
        "X-Registry-ArtifactId": args.artifactId.replace(/\s|\//g, "_"),
        "Target-Url": `${this.configs.serviceRegistry.coreRegistryApi}/groups/${encodeURIComponent(
          args.groupId
        )}/artifacts`,
      },
    });
    this.refresh();
  }
}
