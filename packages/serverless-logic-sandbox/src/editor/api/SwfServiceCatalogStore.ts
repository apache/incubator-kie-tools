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
import { ArtifactType, SearchedArtifact } from "@rhoas/registry-instance-sdk";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import * as yaml from "yaml";
import { VirtualServiceRegistryContextType } from "../../virtualServiceRegistry/VirtualServiceRegistryContext";
import {
  isServiceAccountConfigValid,
  ServiceAccountSettingsConfig,
} from "../../settings/serviceAccount/ServiceAccountConfig";
import {
  isServiceRegistryConfigValid,
  ServiceRegistrySettingsConfig,
} from "../../settings/serviceRegistry/ServiceRegistryConfig";
import { ExtendedServicesConfig } from "../../settings/SettingsContext";
import { WorkspaceFile } from "../../workspace/WorkspacesContext";
import { VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX } from "../../virtualServiceRegistry/VirtualServiceRegistryContextProvider";

export const VIRTUAL_SERVICE_REGISTRY_NAME = "Sandbox";
export const ARTIFACT_TAGS = {
  IS_VIRTUAL_SERVICE_REGISTRY: "isVirtualServiceRegistry",
};

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
    getServiceId: (artifact: SearchedArtifact) => artifact.id,
  };

  private readonly VIRTUAL_SERVICE_REGISTRY_API = {
    getArtifactContentUrl: (artifact: SearchedArtifact) => artifact.id,
    getServiceId: (artifact: SearchedArtifact) => artifact.id.replace(VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX, ""),
  };

  private readonly SAME_WORKSPACE_REGISTRY_API = {
    getArtifactContentUrl: (artifact: SearchedArtifact) =>
      artifact.id.replace(`${VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX}${artifact.groupId}`, ""),
    getServiceId: (artifact: SearchedArtifact) =>
      artifact.id.replace(`${VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX}${artifact.groupId}`, ""),
  };

  private storedServices: SwfServiceCatalogService[] = [];

  public virtualServiceRegistry?: VirtualServiceRegistryContextType;
  public currentFile?: WorkspaceFile;

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

  public setVirtualServiceRegistry(virtualServiceRegistry: VirtualServiceRegistryContextType, file?: WorkspaceFile) {
    this.virtualServiceRegistry = virtualServiceRegistry;
    this.currentFile = file;
    this.refresh();
  }

  private isConfigValid(): boolean {
    return (
      isServiceAccountConfigValid(this.configs.serviceAccount) &&
      isServiceRegistryConfigValid(this.configs.serviceRegistry)
    );
  }

  private async buildRemoteArtifacts(): Promise<ArtifactWithContent[]> {
    if (!this.isConfigValid()) {
      return [];
    }

    try {
      const artifacts = (
        await axios.get(this.PROXY_ENDPOINT, {
          headers: {
            ...this.COMMON_HEADERS,
            "Target-Url": this.SERVICE_REGISTRY_API.getArtifactsUrl(),
          },
        })
      ).data.artifacts;

      if (artifacts.length > 0) {
        return Promise.all(
          artifacts
            .filter((artifact: SearchedArtifact) => artifact.type === ArtifactType.Openapi)
            .map(async (artifact: SearchedArtifact) => ({
              metadata: artifact,
              content: (
                await axios.get(this.PROXY_ENDPOINT, {
                  headers: {
                    ...this.COMMON_HEADERS,
                    "Target-Url": this.SERVICE_REGISTRY_API.getArtifactContentUrl(artifact),
                  },
                })
              ).data as OpenAPIV3.Document,
            }))
        );
      }
    } catch (e) {
      console.debug(e);
    }

    return [];
  }

  private async buildVirtualArtifacts(): Promise<ArtifactWithContent[]> {
    if (!this.virtualServiceRegistry || !this.currentFile) {
      return [];
    }

    const vsrWorkspaces = await this.virtualServiceRegistry.listVsrWorkspaces();
    const vsrWorkspacesWithFiles = await Promise.all(
      vsrWorkspaces.map(async (vsrWorkspace) => ({
        vsrWorkspace,
        files: await this.virtualServiceRegistry!.getVsrFiles({
          vsrWorkspaceId: vsrWorkspace.workspaceId,
        }),
      }))
    );

    const vsrWorkspacesWithEagerFiles = await Promise.all(
      vsrWorkspacesWithFiles.map(async (vsrWorkspaceWithFiles) => {
        return Promise.all(
          vsrWorkspaceWithFiles.files.map(async (vsrFile) => ({
            metadata: {
              groupId: vsrWorkspaceWithFiles.vsrWorkspace.workspaceId,
              id: vsrFile.relativePath,
              type: ArtifactType.Openapi,
              labels: [ARTIFACT_TAGS.IS_VIRTUAL_SERVICE_REGISTRY],
            } as SearchedArtifact,
            content: yaml.parse(await vsrFile.getFileContentsAsString()) as OpenAPIV3.Document,
          }))
        );
      })
    );

    return vsrWorkspacesWithEagerFiles.flat().filter((file) => {
      return (
        file.content &&
        `${file.metadata.id}` !==
          `${VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX}${this.currentFile?.workspaceId}/${this.currentFile?.relativePath}`
      );
    });
  }

  public async refresh() {
    const [remoteArtifacts, virtualArtifacts] = await Promise.all([
      this.buildRemoteArtifacts(),
      this.buildVirtualArtifacts(),
    ]);

    this.storedServices = [...remoteArtifacts, ...virtualArtifacts]
      .filter((artifact) => artifact.content.openapi)
      .map((artifact) => {
        const isVirtualServiceRegistry = artifact.metadata.labels?.includes(ARTIFACT_TAGS.IS_VIRTUAL_SERVICE_REGISTRY);
        const isFromSameWorkspace =
          isVirtualServiceRegistry && artifact.metadata.groupId === this.currentFile?.workspaceId;

        const registry = isVirtualServiceRegistry ? VIRTUAL_SERVICE_REGISTRY_NAME : this.configs.serviceRegistry.name;
        const serviceId = isVirtualServiceRegistry
          ? isFromSameWorkspace
            ? this.SAME_WORKSPACE_REGISTRY_API.getServiceId(artifact.metadata)
            : this.VIRTUAL_SERVICE_REGISTRY_API.getServiceId(artifact.metadata)
          : this.SERVICE_REGISTRY_API.getServiceId(artifact.metadata);

        const url = isVirtualServiceRegistry
          ? isFromSameWorkspace
            ? this.SAME_WORKSPACE_REGISTRY_API.getArtifactContentUrl(artifact.metadata)
            : this.VIRTUAL_SERVICE_REGISTRY_API.getArtifactContentUrl(artifact.metadata)
          : this.SERVICE_REGISTRY_API.getArtifactContentUrl(artifact.metadata);

        const swfFunctions = extractFunctions(artifact.content, {
          type: SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY,
          registry,
          serviceId,
        });

        return {
          name: artifact.metadata.id,
          rawContent: yaml.stringify(artifact.content),
          type: SwfServiceCatalogServiceType.rest,
          functions: swfFunctions,
          source: {
            url,
            id: serviceId,
            registry,
            type: SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY,
          },
        };
      });
  }

  public async uploadArtifact(args: { groupId: string; artifactId: string; content: string }): Promise<void> {
    if (!this.isConfigValid()) {
      return;
    }

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
