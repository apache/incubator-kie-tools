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

import { ArtifactSearchResults, SearchedArtifact } from "@rhoas/registry-instance-sdk";
import axios from "axios";
import { OpenAPIV3 } from "openapi-types";
import { AsyncAPIDocument } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { supportArtifactTypes } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/parseApiContent";

export interface UploadArtifactArgs {
  groupId: string;
  artifactId: string;
  content: string;
}

export interface ArtifactWithContent {
  metadata: SearchedArtifact;
  content: OpenAPIV3.Document | AsyncAPIDocument;
}

const ARTIFACT_ENDPOINTS = {
  allArtifacts: (baseUrl: string) => `${baseUrl}/search/artifacts`,
  artifactById: (args: { baseUrl: string; artifact: SearchedArtifact }) =>
    `${args.baseUrl}/groups/${args.artifact.groupId}/artifacts/${args.artifact.id}`,
  artifactsByGroupId: (args: { baseUrl: string; groupId: string }) =>
    `${args.baseUrl}/groups/${encodeURIComponent(args.groupId)}/artifacts`,
};

export class RemoteArtifactCatalogApi {
  private readonly commonHeaders;

  constructor(
    private args: {
      proxyEndpoint: string;
      baseUrl: string;
      auth: {
        clientId: string;
        clientSecret: string;
      };
    }
  ) {
    this.commonHeaders = {
      // We are facing a 401 Error when using OAuth, let's use Basic Auth for now.
      Authorization: "Basic " + window.btoa(`${args.auth.clientId}:${args.auth.clientSecret}`),
      "Content-Type": "application/json",
    };
  }

  public async fetchArtifacts(): Promise<ArtifactSearchResults> {
    return (
      await axios.get(this.args.proxyEndpoint, {
        headers: {
          ...this.commonHeaders,
          "Target-Url": ARTIFACT_ENDPOINTS.allArtifacts(this.args.baseUrl),
        },
      })
    ).data as ArtifactSearchResults;
  }

  public async fetchArtifactsWithContent(artifactSearchResult: ArtifactSearchResults): Promise<ArtifactWithContent[]> {
    if (artifactSearchResult.count === 0) {
      return [];
    }
    return Promise.all(
      artifactSearchResult.artifacts
        .filter((artifact: any) => supportArtifactTypes.includes(artifact.type))
        .map(async (artifact) => ({
          metadata: artifact,
          content: (
            await axios.get(this.args.proxyEndpoint, {
              headers: {
                ...this.commonHeaders,
                "Target-Url": ARTIFACT_ENDPOINTS.artifactById({ baseUrl: this.args.baseUrl, artifact }),
              },
            })
          ).data,
        }))
    );
  }

  public async uploadArtifact(args: UploadArtifactArgs): Promise<void> {
    await axios.post(this.args.proxyEndpoint, args.content, {
      headers: {
        ...this.commonHeaders,
        "X-Registry-ArtifactId": args.artifactId.replace(/\s|\//g, "_"),
        "Target-Url": ARTIFACT_ENDPOINTS.artifactsByGroupId({ baseUrl: this.args.baseUrl, groupId: args.groupId }),
      },
    });
  }

  public resolveServiceId(artifact: ArtifactWithContent): string {
    return artifact.metadata.id;
  }

  public resolveArtifactEndpoint(artifact: ArtifactWithContent): string {
    return ARTIFACT_ENDPOINTS.artifactById({ baseUrl: this.args.baseUrl, artifact: artifact.metadata });
  }
}
