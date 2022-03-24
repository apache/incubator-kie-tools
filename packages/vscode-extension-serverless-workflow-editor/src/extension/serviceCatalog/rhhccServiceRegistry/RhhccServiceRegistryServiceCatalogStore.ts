import {
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { RhhccAuthenticationStore } from "../../rhhcc/RhhccAuthenticationStore";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import { extractFunctions } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/openapi";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "../../configuration";
import * as vscode from "vscode";
import { posix as posixPath } from "path";
import * as yaml from "yaml";
import { getServiceFileNameFromSwfServiceCatalogServiceId } from "./index";

export class RhhccServiceRegistryServiceCatalogStore {
  private onChangeCallback: undefined | ((services: SwfServiceCatalogService[]) => Promise<any>);
  private urlVsPathConfigurationChangeCallback: vscode.Disposable;
  private specsStoragePathConfigurationChangeCallback: vscode.Disposable;
  private serviceRegistryUrlConfigurationChangeCallback: vscode.Disposable;

  constructor(
    private readonly args: {
      baseFileAbsolutePosixPath: string;
      rhhccAuthenticationStore: RhhccAuthenticationStore;
      configuration: SwfVsCodeExtensionConfiguration;
    }
  ) {}

  public async init(args: { onNewServices: (swfServices: SwfServiceCatalogService[]) => Promise<any> }) {
    this.onChangeCallback = args.onNewServices;

    this.urlVsPathConfigurationChangeCallback = vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (e.affectsConfiguration(CONFIGURATION_SECTIONS.shouldReferenceServiceRegistryFunctionsWithUrls)) {
        return this.refresh();
      }
    });

    this.specsStoragePathConfigurationChangeCallback = vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (e.affectsConfiguration(CONFIGURATION_SECTIONS.specsStoragePath)) {
        return this.refresh();
      }
    });

    this.serviceRegistryUrlConfigurationChangeCallback = vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (e.affectsConfiguration(CONFIGURATION_SECTIONS.serviceRegistryUrl)) {
        return this.refresh();
      }
    });

    return this.refresh();
  }

  public async refresh() {
    const specsDirAbsolutePosixPath = this.args.configuration.getInterpolatedSpecsDirAbsolutePosixPath(this.args);
    const serviceRegistryUrl = this.args.configuration.getConfiguredServiceRegistryUrl();
    const shouldReferenceFunctionsWithUrls =
      this.args.configuration.getConfiguredFlagShouldReferenceServiceRegistryFunctionsWithUrls();

    if (!this.args.rhhccAuthenticationStore.session) {
      return this.onChangeCallback?.([]);
    }

    if (!serviceRegistryUrl) {
      return this.onChangeCallback?.([]);
    }

    const requestHeaders = {
      headers: { Authorization: "Bearer " + this.args.rhhccAuthenticationStore.session.accessToken },
    };

    const serviceRegistryRestApi = {
      getArtifactContentUrl: (params: { groupId: string; id: string }) => {
        return `${serviceRegistryUrl.toString()}/groups/${params.groupId}/artifacts/${params.id}`;
      },
      getArtifactsUrl: () => {
        return `${serviceRegistryUrl?.toString()}/search/artifacts`;
      },
    };

    const artifactsMetadata: ServiceRegistryArtifactSearchResponse = (
      await axios.get(serviceRegistryRestApi.getArtifactsUrl(), requestHeaders)
    ).data;

    const artifactsWithContent = await Promise.all(
      artifactsMetadata.artifacts
        .filter((artifactMetadata) => artifactMetadata.type === "OPENAPI")
        .map(async (artifactMetadata) => ({
          metadata: artifactMetadata,
          content: (
            await axios.get(serviceRegistryRestApi.getArtifactContentUrl(artifactMetadata), requestHeaders)
          ).data as OpenAPIV3.Document,
        }))
    );

    const services: SwfServiceCatalogService[] = artifactsWithContent.map((artifact) => {
      const serviceId = artifact.metadata.id;
      const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(serviceId);
      const specsDirRelativePosixPath = posixPath.relative(
        posixPath.dirname(this.args.baseFileAbsolutePosixPath),
        specsDirAbsolutePosixPath
      );
      const serviceFileRelativePosixPath = posixPath.join(specsDirRelativePosixPath, serviceFileName);

      let swfFunctions = extractFunctions(artifact.content, serviceFileRelativePosixPath, {
        type: SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY,
        serviceId: serviceId,
      });

      if (shouldReferenceFunctionsWithUrls) {
        // TODO tiago: I believe we should be doing that in a better way
        swfFunctions = swfFunctions.map((swfFunction) => ({
          ...swfFunction,
          operation: serviceRegistryRestApi.getArtifactContentUrl(artifact.metadata),
        }));
      }

      return {
        name: serviceFileName,
        rawContent: yaml.stringify(artifact.content),
        type: SwfServiceCatalogServiceType.rest,
        functions: swfFunctions,
        source: {
          type: SwfServiceCatalogServiceSourceType.RHHCC_SERVICE_REGISTRY,
          id: serviceId,
        },
      };
    });

    return this.onChangeCallback?.(services);
  }

  public dispose() {
    this.urlVsPathConfigurationChangeCallback?.dispose();
    this.specsStoragePathConfigurationChangeCallback?.dispose();
    this.serviceRegistryUrlConfigurationChangeCallback?.dispose();
  }
}

export interface ServiceRegistryArtifactSearchResponse {
  artifacts: ServiceRegistryArtifactMetadata[];
}

export interface ServiceRegistryArtifactMetadata {
  groupId: string;
  id: string;
  type: string;
}
