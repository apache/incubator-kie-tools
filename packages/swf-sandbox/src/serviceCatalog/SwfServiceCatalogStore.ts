import {
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import { extractFunctions } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/openapi";
import { posix as posixPath } from "path";
import * as yaml from "yaml";
import { ServiceRegistrySettingsConfig } from "../settings/serviceRegistry/ServiceRegistryConfig";
import { ServiceAccountSettingsConfig } from "../settings/serviceAccount/ServiceAccountConfig";

export function getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId: string) {
  return `${swfServiceCatalogServiceId}__latest.yaml`;
}

export class SwfServiceCatalogStore {
  public static services: SwfServiceCatalogService[];
  public static async refresh(
    serviceRegistryConfig: ServiceRegistrySettingsConfig,
    serviceAccountConfig: ServiceAccountSettingsConfig
  ) {
    const serviceRegistryUrl = serviceRegistryConfig.coreRegistryApi;
    const shouldReferenceFunctionsWithUrls = true;

    const requestHeaders = {
      headers: {
        // We are facing a 401 Error when using oauth, let's use Basic auth for now.
        Authorization: "Basic " + btoa(`${serviceAccountConfig.clientId}:${serviceAccountConfig.clientSecret}`),
        "Content-Type": "application/json",
      },
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

    SwfServiceCatalogStore.services = artifactsWithContent.map((artifact) => {
      const serviceId = artifact.metadata.id;
      const serviceFileName = getServiceFileNameFromSwfServiceCatalogServiceId(serviceId);
      const specsDirRelativePosixPath = "./";
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

    return SwfServiceCatalogStore.services;
  }

  public dispose() {}
}

export interface ServiceRegistryArtifactSearchResponse {
  artifacts: ServiceRegistryArtifactMetadata[];
}

export interface ServiceRegistryArtifactMetadata {
  groupId: string;
  id: string;
  type: string;
}
