import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import { extractFunctions } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/openapi";
import * as yaml from "yaml";
import { ServiceRegistrySettingsConfig } from "../settings/serviceRegistry/ServiceRegistryConfig";
import { ServiceAccountSettingsConfig } from "../settings/serviceAccount/ServiceAccountConfig";

export function getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId: string) {
  return `${swfServiceCatalogServiceId}__latest.yaml`;
}

// TODO: refactor and remove duplicated code
export class SwfServiceCatalogStore {
  public static services: SwfServiceCatalogService[];
  public static async refresh(
    proxyUrl: string,
    serviceRegistryConfig: ServiceRegistrySettingsConfig,
    serviceAccountConfig: ServiceAccountSettingsConfig
  ) {
    const serviceRegistryUrl = serviceRegistryConfig.coreRegistryApi;

    const serviceRegistryRestApi = {
      getArtifactContentUrl: (params: { groupId: string; id: string }) => {
        return `${serviceRegistryUrl.toString()}/groups/${params.groupId}/artifacts/${params.id}`;
      },
      getArtifactsUrl: () => {
        return `${serviceRegistryUrl?.toString()}/search/artifacts`;
      },
    };

    const artifactsMetadata: ServiceRegistryArtifactSearchResponse = (
      await axios.get(proxyUrl + "/devsandbox", {
        headers: {
          // We are facing a 401 Error when using oauth, let's use Basic auth for now.
          Authorization: "Basic " + btoa(`${serviceAccountConfig.clientId}:${serviceAccountConfig.clientSecret}`),
          "Content-Type": "application/json",
          "Target-Url": serviceRegistryRestApi.getArtifactsUrl(),
        },
      })
    ).data;

    const artifactsWithContent = await Promise.all(
      artifactsMetadata.artifacts
        .filter((artifactMetadata) => artifactMetadata.type === "OPENAPI")
        .map(async (artifactMetadata) => ({
          metadata: artifactMetadata,
          content: (
            await axios.get(proxyUrl + "/devsandbox", {
              headers: {
                // We are facing a 401 Error when using oauth, let's use Basic auth for now.
                Authorization: "Basic " + btoa(`${serviceAccountConfig.clientId}:${serviceAccountConfig.clientSecret}`),
                "Content-Type": "application/json",
                "Target-Url": serviceRegistryRestApi.getArtifactContentUrl(artifactMetadata),
              },
            })
          ).data as OpenAPIV3.Document,
        }))
    );

    SwfServiceCatalogStore.services = artifactsWithContent.map((artifact) => {
      const serviceId = artifact.metadata.id;
      const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(serviceId);

      const swfFunctions: SwfServiceCatalogFunction[] = extractFunctions(artifact.content, {
        type: SwfServiceCatalogFunctionSourceType.RHHCC_SERVICE_REGISTRY,
        serviceId: serviceId,
      });

      return {
        name: serviceFileName,
        rawContent: yaml.stringify(artifact.content),
        type: SwfServiceCatalogServiceType.rest,
        functions: swfFunctions,
        source: {
          url: serviceRegistryRestApi.getArtifactContentUrl(artifact.metadata),
          type: SwfServiceCatalogServiceSourceType.RHHCC_SERVICE_REGISTRY,
          id: serviceId,
        },
      };
    });

    return SwfServiceCatalogStore.services;
  }

  public static async uploadArtifact(args: {
    groupId: string;
    artifactId: string;
    content: string;
    proxyUrl: string;
    serviceRegistryConfig: ServiceRegistrySettingsConfig;
    serviceAccountConfig: ServiceAccountSettingsConfig;
  }): Promise<void> {
    await axios.post(args.proxyUrl + "/devsandbox", args.content, {
      headers: {
        // We are facing a 401 Error when using oauth, let's use Basic auth for now.
        Authorization:
          "Basic " + btoa(`${args.serviceAccountConfig.clientId}:${args.serviceAccountConfig.clientSecret}`),
        "Content-Type": "application/json",
        "X-Registry-ArtifactId": args.artifactId.replace(/\s|\//g, "_"),
        "Target-Url": `${args.serviceRegistryConfig.coreRegistryApi}/groups/${encodeURIComponent(
          args.groupId
        )}/artifacts`,
      },
    });
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
