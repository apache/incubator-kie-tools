import {
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { RhhccAuthenticationStore } from "../../rhhcc/RhhccAuthenticationStore";
import axios from "axios";
import type * as openapiTypes from "openapi-types";
import { extractFunctions } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/openapi";
import { SwfServiceCatalogFunctionSourceType } from "@kie-tools/serverless-workflow-service-catalog/src/api";
import { SwfVsCodeExtensionSettings } from "../../settings";

export class RhhccServiceRegistryServiceCatalogStore {
  private onChangeCallback: undefined | ((services: SwfServiceCatalogService[]) => Promise<any>);
  private _serviceRegistryUrl: URL | undefined;
  private subscriptions = new Set<(serviceRegistryUrl: URL | undefined) => void>();

  constructor(
    private readonly rhhccAuthenticationStore: RhhccAuthenticationStore,
    private readonly settings: SwfVsCodeExtensionSettings
  ) {}

  public async init(args: { onNewServices: (swfServices: SwfServiceCatalogService[]) => Promise<any> }) {
    this.onChangeCallback = args.onNewServices;
    return this.refresh();
  }

  public get serviceRegistryUrl() {
    return this._serviceRegistryUrl;
  }

  public setServiceRegistryUrl(serviceRegistryUrl: URL | undefined) {
    this._serviceRegistryUrl = serviceRegistryUrl;
    this.subscriptions.forEach((subscription) => subscription(serviceRegistryUrl));
  }

  public async refresh() {
    if (!this.rhhccAuthenticationStore.session) {
      return this.onChangeCallback?.([]);
    }

    if (!this._serviceRegistryUrl) {
      return this.onChangeCallback?.([]);
    }

    const requestHeaders = {
      headers: { Authorization: "Bearer " + this.rhhccAuthenticationStore.session.accessToken },
    };

    const serviceRegistryRestApi = {
      getArtifactContentUrl: (args: { groupId: string; id: string }) => {
        return `${this._serviceRegistryUrl?.toString()}/groups/${args.groupId}/artifacts/${args.id}`;
      },
      getArtifactsUrl: () => {
        return `${this._serviceRegistryUrl?.toString()}/search/artifacts`;
      },
    };

    const artifactsMetadata: ServiceRegistryArtifactSearchResponse = (
      await axios.get(serviceRegistryRestApi.getArtifactsUrl(), requestHeaders)
    ).data;

    const artifactsWithContent = await Promise.all(
      artifactsMetadata.artifacts
        .filter((metadata) => metadata.type === "OPENAPI")
        .map(async (metadata) => ({
          metadata,
          content: (
            await axios.get(serviceRegistryRestApi.getArtifactContentUrl(metadata), requestHeaders)
          ).data as openapiTypes.OpenAPIV3.Document,
        }))
    );

    const swfServiceCatalogServices: SwfServiceCatalogService[] = artifactsWithContent.map((artifact) => {
      const serviceId = `${artifact.metadata.groupId}__${artifact.metadata.id}`;

      //TODO tiago: fix empty string here
      let functions = extractFunctions(artifact.content, "", {
        type: SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY,
        serviceId: artifact.metadata.id,
      });

      // TODO tiago: I believe we should be doing that in a better way
      if (this.settings.shouldReferenceServiceRegistryFunctionsWithUrls()) {
        functions = functions.map((swfFunction) => ({
          ...swfFunction,
          operation: serviceRegistryRestApi.getArtifactContentUrl(artifact.metadata),
        }));
      }

      return {
        name: serviceId,
        rawContent: JSON.stringify(artifact.content, undefined, 2),
        type: SwfServiceCatalogServiceType.rest,
        functions,
        source: {
          type: SwfServiceCatalogServiceSourceType.RHHCC_SERVICE_REGISTRY,
          id: serviceId,
        },
      };
    });

    return this.onChangeCallback?.(swfServiceCatalogServices);
  }

  subscribeToServiceRegistryUrlChange(callback: (serviceRegistryUrl: URL | undefined) => any) {
    this.subscriptions.add(callback);
    return callback;
  }

  unsubscribeToServiceRegistryUrlChange(callback: (serviceRegistryUrl: URL | undefined) => any) {
    this.subscriptions.delete(callback);
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
