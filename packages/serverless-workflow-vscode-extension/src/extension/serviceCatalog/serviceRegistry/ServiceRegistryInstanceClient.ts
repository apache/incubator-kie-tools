/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { ArtifactsApi, ArtifactType, Configuration, SearchedArtifact } from "@rhoas/registry-instance-sdk";
import { AuthProvider } from "./auth";
import {
  SwfServiceCatalogService,
  SwfCatalogSourceType,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import * as yaml from "yaml";
import { parseApiContent } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { supportArtifactTypes } from "@kie-tools/serverless-workflow-service-catalog/dist/channel/parsers/parseApiContent";

export class ServiceRegistryInstanceClient {
  constructor(
    private readonly args: {
      name: string;
      url: string;
      authProvider: AuthProvider;
    }
  ) {}

  public get name() {
    return this.args.name;
  }

  public get autProvider() {
    return this.args.authProvider;
  }

  public async getSwfServiceCatalogServices(): Promise<SwfServiceCatalogService[]> {
    const headers = await this.args.authProvider.getAuthHeader();

    const artifactsApi: ArtifactsApi = new ArtifactsApi(
      new Configuration({
        basePath: this.args.url,
        baseOptions: {
          headers,
        },
      })
    );

    const swfServices: SwfServiceCatalogService[] = [];

    try {
      const response = await artifactsApi.searchArtifacts();
      const artifacts: SearchedArtifact[] = response.data.artifacts ?? [];
      const specs: SearchedArtifact[] = artifacts.filter(
        (artifact: any) => supportArtifactTypes.includes(artifact.type) && artifact.groupId
      );
      for (const spec of specs) {
        const response = await artifactsApi.getLatestArtifact(spec.groupId ?? "", spec.id);
        try {
          const swfService: SwfServiceCatalogService = parseApiContent({
            source: {
              registry: this.name,
              url: `${this.args.url}/groups/${spec.groupId}/artifacts/${spec.id}`,
              type: SwfCatalogSourceType.SERVICE_REGISTRY,
              id: spec.id,
            },
            serviceFileName: `${this.name}.yaml`,
            serviceFileContent: yaml.stringify(response.data),
          });
          swfServices.push(swfService);
        } catch (e) {
          console.error(`Parser error: ${e}`);
        }
      }
    } catch (err) {
      console.error(`Cannot load services: ${err}`);
    }

    return Promise.resolve(swfServices);
  }
}
