/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { ServerlessWorkflowType } from "../../common/Editor";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service/dist/api";
import {
  SwfJsonLanguageService,
  SwfLanguageServiceArgs,
  SwfYamlLanguageService,
} from "@kie-tools/serverless-workflow-language-service/dist/channel";
import { StandaloneSwfLanguageServiceChannelApiImpl } from "./StandaloneSwfLanguageServiceChannelApiImpl";

const getDefaultLsArgs = (
  configOverrides: Partial<SwfLanguageServiceArgs["config"]>
): Omit<SwfLanguageServiceArgs, "lang"> => {
  return {
    fs: {},
    serviceCatalog: {
      global: {
        getServices: async () => [], //this.catalogStore.services,
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
      shouldConfigureServiceRegistries: () => false,
      shouldServiceRegistriesLogIn: () => false,
      canRefreshServices: () => true,
      ...configOverrides,
    },
  };
};

export const getLanguageServiceChannelApi = (args: {
  workflowType: ServerlessWorkflowType;
}): SwfLanguageServiceChannelApi => {
  const lsArgs = getDefaultLsArgs({});
  const ls = args.workflowType == "json" ? new SwfJsonLanguageService(lsArgs) : new SwfYamlLanguageService(lsArgs);

  return new StandaloneSwfLanguageServiceChannelApiImpl(ls);
};
