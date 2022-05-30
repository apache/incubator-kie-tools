import {
  SwfServiceCatalogFunction,
  SwfServiceCatalogFunctionSourceType,
  SwfServiceCatalogService,
  SwfServiceCatalogServiceSourceType,
  SwfServiceCatalogServiceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { extractFunctions } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/openapi";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import * as yaml from "yaml";
import {
  ServiceRegistryArtifactSearchResponse,
  ServiceRegistryAuthInfo,
  ServiceRegistryInfo,
} from "./ServiceRegistryInfo";

export function getServiceFileNameFromSwfServiceCatalogServiceId(swfServiceCatalogServiceId: string) {
  return `${swfServiceCatalogServiceId}__latest.yaml`;
}

// TODO: does this class need to be static?
export class SwfServiceCatalogStore {
  public static storedServices: SwfServiceCatalogService[] = [];

  private static readonly buildProxiedDevSandboxUrl = (proxyUrl: string) => `${proxyUrl}/devsandbox`;
  private static readonly buildCommonHeaders = (args: { authInfo: ServiceRegistryAuthInfo }) => ({
    // We are facing a 401 Error when using oauth, let's use Basic auth for now.
    Authorization: "Basic " + btoa(`${args.authInfo.username}:${args.authInfo.token}`),
    "Content-Type": "application/json",
  });

  public static async refresh(args: { serviceRegistryInfo: ServiceRegistryInfo; proxyUrl: string }) {
    const serviceRegistryRestApi = {
      getArtifactContentUrl: (params: { groupId: string; id: string }) =>
        `${args.serviceRegistryInfo.url}/groups/${params.groupId}/artifacts/${params.id}`,
      getArtifactsUrl: () => `${args.serviceRegistryInfo.url}/search/artifacts`,
    };

    const artifactsMetadata: ServiceRegistryArtifactSearchResponse = (
      await axios.get(this.buildProxiedDevSandboxUrl(args.proxyUrl), {
        headers: {
          ...this.buildCommonHeaders({ authInfo: args.serviceRegistryInfo.authInfo }),
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
            await axios.get(this.buildProxiedDevSandboxUrl(args.proxyUrl), {
              headers: {
                ...this.buildCommonHeaders({ authInfo: args.serviceRegistryInfo.authInfo }),
                "Target-Url": serviceRegistryRestApi.getArtifactContentUrl(artifactMetadata),
              },
            })
          ).data as OpenAPIV3.Document,
        }))
    );

    SwfServiceCatalogStore.storedServices = artifactsWithContent.map((artifact) => {
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

    return SwfServiceCatalogStore.storedServices;
  }

  public static async uploadArtifact(args: {
    groupId: string;
    artifactId: string;
    content: string;
    proxyUrl: string;
    serviceRegistryInfo: ServiceRegistryInfo;
  }): Promise<void> {
    await axios.post(this.buildProxiedDevSandboxUrl(args.proxyUrl), args.content, {
      headers: {
        ...this.buildCommonHeaders({ authInfo: args.serviceRegistryInfo.authInfo }),
        "X-Registry-ArtifactId": args.artifactId.replace(/\s|\//g, "_"),
        "Target-Url": `${args.serviceRegistryInfo.url}/groups/${encodeURIComponent(args.groupId)}/artifacts`,
      },
    });
  }
}
