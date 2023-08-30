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

import { FileLanguage, getFileLanguageOrThrow } from "@kie-tools/serverless-workflow-language-service/dist/api";
import {
  SwfJsonLanguageService,
  SwfLanguageServiceArgs,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";

export class DevWebAppSwfLanguageService {
  public getLs(relativePath: string): SwfJsonLanguageService | SwfYamlLanguageService {
    const swfLanguageLsArgs = this.getDefaultLsArgs({});

    const fileLanguage = getFileLanguageOrThrow(relativePath);
    if (fileLanguage === FileLanguage.YAML) {
      return new SwfYamlLanguageService(swfLanguageLsArgs);
    } else if (fileLanguage === FileLanguage.JSON) {
      return new SwfJsonLanguageService(swfLanguageLsArgs);
    } else {
      throw new Error(`Could not determine LS for ${relativePath}`);
    }
  }

  private getDefaultLsArgs(
    configOverrides: Partial<SwfLanguageServiceArgs["config"]>
  ): Omit<SwfLanguageServiceArgs, "lang"> {
    return {
      fs: {},
      serviceCatalog: {
        global: {
          getServices: async () => [],
        },
        relative: {
          getServices: async (_textDocument) => [],
        },
        getServiceFileNameFromSwfServiceCatalogServiceId: async (
          registryName: string,
          swfServiceCatalogServiceId: string
        ) => `${registryName}__${swfServiceCatalogServiceId}__latest.yaml`,
      },
      config: {
        shouldDisplayServiceRegistriesIntegration: async () => false,
        shouldIncludeJsonSchemaDiagnostics: async () => true,
        shouldReferenceServiceRegistryFunctionsWithUrls: async () => true,
        getSpecsDirPosixPaths: async (_textDocument) => ({
          specsDirRelativePosixPath: "",
          specsDirAbsolutePosixPath: "",
        }),
        getRoutesDirPosixPaths: async (_textDocument) => ({
          routesDirRelativePosixPath: "",
          routesDirAbsolutePosixPath: "",
        }),
        shouldConfigureServiceRegistries: () => false,
        shouldServiceRegistriesLogIn: () => false,
        canRefreshServices: () => false,
        ...configOverrides,
      },
      jqCompletions: {
        remote: {
          getJqAutocompleteProperties: (_args: any) => Promise.resolve([]),
        },
        relative: {
          getJqAutocompleteProperties: (_args: any) => Promise.resolve([]),
        },
      },
    };
  }
}
