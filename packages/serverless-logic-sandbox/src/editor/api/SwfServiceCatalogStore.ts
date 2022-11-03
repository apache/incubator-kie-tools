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
import { OpenAPIV3 } from "openapi-types";
import * as yaml from "yaml";
import { isSpec } from "../../extension";
import {
  isServiceAccountConfigValid,
  ServiceAccountSettingsConfig,
} from "../../settings/serviceAccount/ServiceAccountConfig";
import {
  isServiceRegistryConfigValid,
  ServiceRegistrySettingsConfig,
} from "../../settings/serviceRegistry/ServiceRegistryConfig";
import { ExtendedServicesConfig } from "../../settings/SettingsContext";
import {
  VIRTUAL_SERVICE_REGISTRY_NAME,
  VIRTUAL_SERVICE_REGISTRY_PATH_PREFIX,
} from "../../virtualServiceRegistry/VirtualServiceRegistryConstants";
import { VirtualServiceRegistryContextType } from "../../virtualServiceRegistry/VirtualServiceRegistryContext";
import { toVsrFunctionPathFromWorkspaceFilePath } from "../../virtualServiceRegistry/VirtualServiceRegistryPathConverter";
import { WorkspaceFile } from "@kie-tools-core/workspaces-git-fs/dist/context/WorkspacesContext";
import { ArtifactWithContent, RemoteArtifactCatalogApi, UploadArtifactArgs } from "./RemoteServiceRegistryCatalogApi";

export class SwfServiceCatalogStore {
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

  private remoteArtifactCatalogApi: RemoteArtifactCatalogApi;
  private storedServices: SwfServiceCatalogService[] = [];

  public virtualServiceRegistry?: VirtualServiceRegistryContextType;
  public currentFile?: WorkspaceFile;

  constructor(
    private readonly configs: {
      serviceAccount: ServiceAccountSettingsConfig;
      serviceRegistry: ServiceRegistrySettingsConfig;
      extendedServicesConfig: ExtendedServicesConfig;
    }
  ) {
    this.remoteArtifactCatalogApi = new RemoteArtifactCatalogApi({
      proxyEndpoint: `${configs.extendedServicesConfig.buildUrl()}/devsandbox`,
      baseUrl: configs.serviceRegistry.coreRegistryApi,
      auth: {
        clientId: configs.serviceAccount.clientId,
        clientSecret: configs.serviceAccount.clientSecret,
      },
    });
  }

  get services(): SwfServiceCatalogService[] {
    return this.storedServices;
  }

  public async setVirtualServiceRegistry(
    virtualServiceRegistry: VirtualServiceRegistryContextType,
    file?: WorkspaceFile
  ): Promise<void> {
    this.virtualServiceRegistry = virtualServiceRegistry;
    this.currentFile = file;
    await this.refresh();
  }

  public async uploadArtifact(args: UploadArtifactArgs): Promise<void> {
    if (!this.isConfigValid()) {
      return;
    }

    await this.remoteArtifactCatalogApi.uploadArtifact(args);
    await this.refresh();
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
      const artifactsSearchResult = await this.remoteArtifactCatalogApi.fetchArtifacts();
      return this.remoteArtifactCatalogApi.fetchArtifactsWithContent(artifactsSearchResult);
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
            } as SearchedArtifact,
            content: yaml.parse(await vsrFile.getFileContentsAsString()) as OpenAPIV3.Document,
          }))
        );
      })
    );

    const isFromCurrentFile = (metadata: SearchedArtifact) => {
      return (
        this.currentFile &&
        metadata.id ===
          toVsrFunctionPathFromWorkspaceFilePath({
            vsrWorkspaceId: this.currentFile?.workspaceId,
            relativePath: this.currentFile?.relativePath,
          })
      );
    };

    const isForeignArtifact = (metadata: SearchedArtifact) => {
      return !isSpec(metadata.id) && metadata.groupId !== this.currentFile?.workspaceId;
    };

    const isLocalSpec = (metadata: SearchedArtifact) => {
      return isSpec(metadata.id) && metadata.groupId === this.currentFile?.workspaceId;
    };

    // The list should:
    // - Show workflows from other workspaces
    // - Show specs from same workspace
    // - Hide current file workflow
    // - Hide specs from other workspaces
    // - Hide workflows from the same workspace since they need to be added as subFlowRefs
    return vsrWorkspacesWithEagerFiles.flat().filter((file) => {
      return (
        file.content &&
        !isFromCurrentFile(file.metadata) &&
        (isForeignArtifact(file.metadata) || isLocalSpec(file.metadata))
      );
    });
  }

  public async refresh() {
    const [remoteArtifacts, virtualArtifacts] = await Promise.all([
      this.buildRemoteArtifacts(),
      this.buildVirtualArtifacts(),
    ]);

    const remoteServices = remoteArtifacts
      .filter((artifact) => artifact.content.openapi)
      .map((artifact) => {
        const registry = this.configs.serviceRegistry.name;
        const serviceId = this.remoteArtifactCatalogApi.resolveServiceId(artifact);
        const url = this.remoteArtifactCatalogApi.resolveArtifactEndpoint(artifact);

        return this.buildSwfServiceCatalogService({
          artifact,
          registry,
          serviceId,
          url,
        });
      });

    const virtualServices = virtualArtifacts
      .filter((artifact) => artifact.content.openapi)
      .map((artifact) => {
        const isFromSameWorkspace = artifact.metadata.groupId === this.currentFile?.workspaceId;

        const serviceId = isFromSameWorkspace
          ? this.SAME_WORKSPACE_REGISTRY_API.getServiceId(artifact.metadata)
          : this.VIRTUAL_SERVICE_REGISTRY_API.getServiceId(artifact.metadata);

        const url = isFromSameWorkspace
          ? this.SAME_WORKSPACE_REGISTRY_API.getArtifactContentUrl(artifact.metadata)
          : this.VIRTUAL_SERVICE_REGISTRY_API.getArtifactContentUrl(artifact.metadata);

        return this.buildSwfServiceCatalogService({
          artifact,
          registry: VIRTUAL_SERVICE_REGISTRY_NAME,
          serviceId,
          url,
        });
      });

    this.storedServices = [...remoteServices, ...virtualServices];
  }

  private buildSwfServiceCatalogService(args: {
    artifact: ArtifactWithContent;
    registry: string;
    serviceId: string;
    url: string;
  }): SwfServiceCatalogService {
    const functions = extractFunctions(args.artifact.content, {
      type: SwfServiceCatalogFunctionSourceType.SERVICE_REGISTRY,
      registry: args.registry,
      serviceId: args.serviceId,
    });

    return {
      name: args.artifact.metadata.id,
      rawContent: yaml.stringify(args.artifact.content),
      type: SwfServiceCatalogServiceType.rest,
      functions: functions,
      source: {
        url: args.url,
        id: args.serviceId,
        registry: args.registry,
        type: SwfServiceCatalogServiceSourceType.SERVICE_REGISTRY,
      },
    };
  }
}
